package com.fitnessultra.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentDownloadedMapsBinding
import com.fitnessultra.databinding.ItemDownloadedMapBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DownloadedMapsFragment : Fragment() {

    private var _binding: FragmentDownloadedMapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadedMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadList()
    }

    private fun loadList() {
        val areas = DownloadedMapsManager.getAll(requireContext()).toMutableList()
        binding.tvEmpty.visibility = if (areas.isEmpty()) View.VISIBLE else View.GONE
        binding.rvMaps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMaps.adapter = MapsAdapter(areas)
    }

    inner class MapsAdapter(private val items: MutableList<DownloadedMapArea>) :
        RecyclerView.Adapter<MapsAdapter.VH>() {

        inner class VH(val b: ItemDownloadedMapBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            ItemDownloadedMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val area = items[position]
            val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            holder.b.tvName.text = area.description
            holder.b.tvInfo.text = getString(
                R.string.downloaded_map_info,
                area.tileCount,
                df.format(Date(area.downloadedAt))
            )
            holder.b.btnView.setOnClickListener {
                val bundle = Bundle().apply { putString("areaId", area.id) }
                findNavController().navigate(
                    R.id.action_downloadedMapsFragment_to_downloadedMapPreviewFragment, bundle
                )
            }
            holder.b.btnDelete.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.downloaded_map_delete_title)
                    .setMessage(getString(R.string.downloaded_map_delete_message, area.description))
                    .setPositiveButton(R.string.btn_delete) { _, _ ->
                        val pos = holder.bindingAdapterPosition
                        if (pos != RecyclerView.NO_ID.toInt()) {
                            DownloadedMapsManager.delete(requireContext(), area.id)
                            items.removeAt(pos)
                            notifyItemRemoved(pos)
                            if (items.isEmpty()) binding.tvEmpty.visibility = View.VISIBLE
                        }
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
