package com.fitnessultra.ui.history

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
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
import java.util.concurrent.Executors

class RunAdapter(
    private val onItemClick: (RunEntity) -> Unit
) : ListAdapter<RunEntity, RunAdapter.RunViewHolder>(DiffCallback()) {

    var prRunIds: Set<Long> = emptySet()
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    // Decode thumbnails off the main thread to keep scrolling smooth.
    private val thumbnailCache = LruCache<Long, Bitmap>(64)
    private val decodeExecutor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class RunViewHolder(private val binding: ItemRunBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(run: RunEntity) {
            binding.tvDate.text = dateFormat.format(Date(run.dateTimestamp))
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

            bindThumbnail(run.id)

            binding.root.setOnClickListener { onItemClick(run) }
        }

        private fun bindThumbnail(runId: Long) {
            binding.ivThumbnail.tag = runId
            thumbnailCache.get(runId)?.let {
                binding.ivThumbnail.setImageBitmap(it)
                binding.ivThumbnail.visibility = View.VISIBLE
                return
            }
            binding.ivThumbnail.visibility = View.GONE
            val file = File(itemView.context.filesDir, "thumbnails/$runId.png")
            if (!file.exists()) return
            decodeExecutor.execute {
                val bmp = BitmapFactory.decodeFile(file.absolutePath) ?: return@execute
                thumbnailCache.put(runId, bmp)
                mainHandler.post {
                    // Only apply if the holder hasn't been recycled to another run.
                    if (binding.ivThumbnail.tag == runId) {
                        binding.ivThumbnail.setImageBitmap(bmp)
                        binding.ivThumbnail.visibility = View.VISIBLE
                    }
                }
            }
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
