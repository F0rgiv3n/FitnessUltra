package com.fitnessultra.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentDownloadedMapPreviewBinding
import com.fitnessultra.util.SettingsManager
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polygon

class DownloadedMapPreviewFragment : Fragment() {

    private var _binding: FragmentDownloadedMapPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadedMapPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val areaId = arguments?.getString("areaId") ?: return
        val area = DownloadedMapsManager.getAll(requireContext()).find { it.id == areaId } ?: return

        binding.mapView.apply {
            setTileSource(SettingsManager.tileSource(requireContext()))
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)
        }

        // Draw the downloaded area as a filled rectangle
        val rect = Polygon().apply {
            points = listOf(
                GeoPoint(area.latNorth, area.lonWest),
                GeoPoint(area.latNorth, area.lonEast),
                GeoPoint(area.latSouth, area.lonEast),
                GeoPoint(area.latSouth, area.lonWest),
                GeoPoint(area.latNorth, area.lonWest)
            )
            fillColor = Color.argb(40, 14, 165, 233)
            strokeColor = Color.argb(255, 14, 165, 233)
            strokeWidth = 3f
        }
        binding.mapView.overlays.add(rect)

        // Zoom to fit the bounding box once the view is laid out
        val box = BoundingBox(area.latNorth, area.lonEast, area.latSouth, area.lonWest)
        binding.mapView.post {
            if (_binding != null) binding.mapView.zoomToBoundingBox(box, true, 64)
        }

        binding.etName.setText(area.description)

        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text?.toString()?.trim() ?: ""
            if (newName.isNotEmpty()) {
                DownloadedMapsManager.update(requireContext(), area.copy(description = newName))
                Toast.makeText(requireContext(), R.string.msg_name_saved, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        binding.mapView.onDetach()
        _binding = null
        super.onDestroyView()
    }
}
