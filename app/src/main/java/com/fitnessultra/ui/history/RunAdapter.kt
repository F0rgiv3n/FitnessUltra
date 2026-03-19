package com.fitnessultra.ui.history

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.databinding.ItemRunBinding
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter(
    private val onItemClick: (RunEntity) -> Unit
) : ListAdapter<RunEntity, RunAdapter.RunViewHolder>(DiffCallback()) {

    var prRunIds: Set<Long> = emptySet()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class RunViewHolder(private val binding: ItemRunBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(run: RunEntity) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.tvDate.text = sdf.format(Date(run.dateTimestamp))
            val useMiles = SettingsManager.useMiles(itemView.context)
            binding.tvDistance.text = TrackingUtils.formatDistance(run.distanceMeters, useMiles, itemView.context)
            binding.tvDuration.text = TrackingUtils.formatTime(run.durationMillis)
            binding.tvAvgSpeed.text = TrackingUtils.formatSpeedKmh(run.avgSpeedKmh, useMiles, itemView.context)
            binding.tvCalories.text = itemView.context.getString(com.fitnessultra.R.string.calories_format, run.caloriesBurned)
            binding.tvSteps.text = if (run.stepCount > 0)
                itemView.context.getString(com.fitnessultra.R.string.steps_format, run.stepCount)
            else ""

            // PR badge
            binding.tvPrBadge.visibility = if (run.id in prRunIds) View.VISIBLE else View.GONE

            // Route thumbnail
            val thumbFile = File(itemView.context.filesDir, "thumbnails/${run.id}.png")
            if (thumbFile.exists()) {
                val bmp = BitmapFactory.decodeFile(thumbFile.absolutePath)
                if (bmp != null) {
                    binding.ivThumbnail.setImageBitmap(bmp)
                    binding.ivThumbnail.visibility = View.VISIBLE
                } else {
                    binding.ivThumbnail.visibility = View.GONE
                }
            } else {
                binding.ivThumbnail.visibility = View.GONE
            }

            binding.root.setOnClickListener { onItemClick(run) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding = ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<RunEntity>() {
        override fun areItemsTheSame(oldItem: RunEntity, newItem: RunEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RunEntity, newItem: RunEntity) = oldItem == newItem
    }
}
