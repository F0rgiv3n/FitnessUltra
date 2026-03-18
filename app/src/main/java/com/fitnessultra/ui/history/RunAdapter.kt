package com.fitnessultra.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.databinding.ItemRunBinding
import com.fitnessultra.util.TrackingUtils
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter(
    private val onItemClick: (RunEntity) -> Unit
) : ListAdapter<RunEntity, RunAdapter.RunViewHolder>(DiffCallback()) {

    class RunViewHolder(private val binding: ItemRunBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(run: RunEntity, onItemClick: (RunEntity) -> Unit) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.tvDate.text = sdf.format(Date(run.dateTimestamp))
            binding.tvDistance.text = TrackingUtils.formatDistance(run.distanceMeters)
            binding.tvDuration.text = TrackingUtils.formatTime(run.durationMillis)
            binding.tvAvgSpeed.text = TrackingUtils.formatSpeedKmh(run.avgSpeedKmh)
            binding.tvCalories.text = itemView.context.getString(com.fitnessultra.R.string.calories_format, run.caloriesBurned)
            binding.tvSteps.text = if (run.stepCount > 0) "${run.stepCount} steps" else ""
            binding.root.setOnClickListener { onItemClick(run) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding = ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<RunEntity>() {
        override fun areItemsTheSame(oldItem: RunEntity, newItem: RunEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RunEntity, newItem: RunEntity) = oldItem == newItem
    }
}
