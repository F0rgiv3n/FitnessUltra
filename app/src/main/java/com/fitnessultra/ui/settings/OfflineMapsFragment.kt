package com.fitnessultra.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentOfflineMapsBinding
import com.fitnessultra.service.TrackingService
import com.fitnessultra.util.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

class OfflineMapsFragment : Fragment() {

    private var _binding: FragmentOfflineMapsBinding? = null
    private val binding get() = _binding!!

    private var downloadJob: Job? = null
    private var isDownloading = false
    private var suppressSpinnerUpdate = false

    private val detailMaxZooms = intArrayOf(14, 16, 17)
    private val minZoom = 10
    private val maxTilesLimit = 5_000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfflineMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.apply {
            setTileSource(SettingsManager.tileSource(requireContext()))
            setMultiTouchControls(true)
        }

        val lastPoint = TrackingService.pathPoints.value?.lastOrNull()
        if (lastPoint != null) {
            binding.mapView.controller.setZoom(13.0)
            binding.mapView.controller.setCenter(lastPoint)
        } else {
            binding.mapView.controller.setZoom(6.0)
            binding.mapView.controller.setCenter(GeoPoint(48.0, 13.0))
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.offline_maps_detail_entries)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDetail.adapter = adapter
        binding.spinnerDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                if (!suppressSpinnerUpdate) updateEstimate()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Auto-update detail level and estimate when user pans/zooms
        binding.mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                updateEstimate(); return false
            }
            override fun onZoom(event: ZoomEvent): Boolean {
                autoSelectDetail(); return false
            }
        })

        updateEstimate()

        binding.btnDownload.setOnClickListener {
            if (isDownloading) cancelDownload() else startDownload()
        }
    }

    /** Auto-selects the spinner option that best matches the current map zoom. */
    private fun autoSelectDetail() {
        val zoom = binding.mapView.zoomLevelDouble.toInt()
        val newPos = when {
            zoom >= 17 -> 2  // HD
            zoom >= 15 -> 1  // Detailed
            else       -> 0  // Normal
        }
        if (binding.spinnerDetail.selectedItemPosition != newPos) {
            suppressSpinnerUpdate = true
            binding.spinnerDetail.setSelection(newPos)
            suppressSpinnerUpdate = false
        }
        updateEstimate()
    }

    private fun countTiles(maxZoom: Int): Int {
        val box = binding.mapView.boundingBox ?: return 0
        if (box.latNorth.isNaN()) return 0
        var total = 0
        for (zoom in minZoom..maxZoom) {
            val x1 = lonToTileX(box.lonWest, zoom)
            val x2 = lonToTileX(box.lonEast, zoom)
            val y1 = latToTileY(box.latNorth, zoom)
            val y2 = latToTileY(box.latSouth, zoom)
            total += (x2 - x1 + 1) * (y2 - y1 + 1)
        }
        return total
    }

    private fun updateEstimate() {
        val maxZoom = detailMaxZooms[binding.spinnerDetail.selectedItemPosition]
        val tiles = countTiles(maxZoom)
        val sizeMb = (tiles.toLong() * 20L) / 1024L   // ~20 KB per tile average
        binding.tvEstimate.text = getString(R.string.offline_maps_estimate, tiles, sizeMb)
    }

    private fun startDownload() {
        val box = binding.mapView.boundingBox ?: return
        if (box.latNorth.isNaN() || box.latSouth.isNaN()) return
        val maxZoom = detailMaxZooms[binding.spinnerDetail.selectedItemPosition]
        val total = countTiles(maxZoom)
        val sizeMb = (total.toLong() * 20L) / 1024L

        if (total > maxTilesLimit) {
            binding.tvProgress.visibility = View.VISIBLE
            binding.tvProgress.text = getString(R.string.offline_maps_too_large, total, maxTilesLimit)
            return
        }

        val tileSource = binding.mapView.tileProvider.tileSource as? OnlineTileSourceBase ?: return
        // Reuse the MapView's own tile writer — avoids SQLite locking conflicts
        val tileCache = binding.mapView.tileProvider.tileWriter

        isDownloading = true
        binding.btnDownload.text = getString(R.string.offline_maps_btn_cancel)
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.progress = 0
        binding.tvProgress.visibility = View.VISIBLE
        binding.tvProgress.text = getString(R.string.offline_maps_downloading, 0, sizeMb)

        downloadJob = viewLifecycleOwner.lifecycleScope.launch {
            var done = 0
            var errors = 0
            var processed = 0

            withContext(Dispatchers.IO) {
                outer@ for (zoom in minZoom..maxZoom) {
                    val x1 = lonToTileX(box.lonWest, zoom)
                    val x2 = lonToTileX(box.lonEast, zoom)
                    val y1 = latToTileY(box.latNorth, zoom)
                    val y2 = latToTileY(box.latSouth, zoom)
                    for (x in x1..x2) {
                        for (y in y1..y2) {
                            if (!isActive) break@outer
                            val tileIndex = (zoom.toLong() shl 40) or (x.toLong() shl 20) or y.toLong()
                            try {
                                val urlString = tileSource.getTileURLString(tileIndex)
                                if (urlString != null) {
                                    val conn = URL(urlString).openConnection() as HttpURLConnection
                                    conn.setRequestProperty("User-Agent", Configuration.getInstance().userAgentValue)
                                    conn.instanceFollowRedirects = true
                                    conn.connectTimeout = 15000
                                    conn.readTimeout = 15000
                                    val code = conn.responseCode
                                    if (code == 200) {
                                        val bytes = conn.inputStream.use { it.readBytes() }
                                        conn.disconnect()
                                        done++  // count downloaded regardless of cache write result
                                        try {
                                            val expiry = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
                                            tileCache.saveFile(tileSource, tileIndex, bytes.inputStream(), expiry)
                                        } catch (_: Exception) { /* cache write failure, tile will load live */ }
                                    } else {
                                        conn.disconnect()
                                        // non-200 = tile doesn't exist or server issue, skip silently
                                    }
                                }
                            } catch (_: Exception) { errors++ }
                            processed++
                            val progress = if (total > 0) processed * 100 / total else 100
                            withContext(Dispatchers.Main) {
                                if (_binding != null) {
                                    binding.progressBar.progress = progress
                                    binding.tvProgress.text =
                                        getString(R.string.offline_maps_downloading, progress, sizeMb)
                                }
                            }
                            delay(300L)  // respect OSM fair-use policy (≤2 req/sec)
                        }
                    }
                }
            }

            if (_binding == null) return@launch
            isDownloading = false
            binding.btnDownload.text = getString(R.string.offline_maps_btn_download)
            binding.progressBar.visibility = View.GONE
            @SuppressLint("StringFormatMatches")
            val resultMsg = when {
                errors == 0 -> getString(R.string.offline_maps_complete)
                done > 0    -> getString(R.string.offline_maps_partial, done, errors)
                else        -> getString(R.string.offline_maps_failed, errors)
            }
            binding.tvProgress.text = resultMsg
        }
    }

    private fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        isDownloading = false
        binding.btnDownload.text = getString(R.string.offline_maps_btn_download)
        binding.progressBar.visibility = View.GONE
        binding.tvProgress.visibility = View.GONE
    }

    private fun lonToTileX(lon: Double, zoom: Int): Int =
        floor((lon + 180.0) / 360.0 * (1 shl zoom)).toInt().coerceIn(0, (1 shl zoom) - 1)

    private fun latToTileY(lat: Double, zoom: Int): Int {
        val latRad = Math.toRadians(lat.coerceIn(-85.0511, 85.0511))
        return floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / Math.PI) / 2.0 * (1 shl zoom))
            .toInt().coerceIn(0, (1 shl zoom) - 1)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        updateEstimate()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        downloadJob?.cancel()
        _binding = null
        super.onDestroyView()
    }
}
