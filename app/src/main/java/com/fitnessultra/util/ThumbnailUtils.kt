package com.fitnessultra.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.location.Location

object ThumbnailUtils {

    private const val SIZE = 128
    private const val PADDING = 12f
    private const val STROKE = 3f
    private const val DOT_RADIUS = 5f

    fun render(locations: List<Location>): Bitmap? {
        if (locations.size < 2) return null

        val lats = locations.map { it.latitude }
        val lons = locations.map { it.longitude }
        val minLat = lats.min()
        val maxLat = lats.max()
        val minLon = lons.min()
        val maxLon = lons.max()

        val latRange = maxLat - minLat
        val lonRange = maxLon - minLon
        if (latRange == 0.0 && lonRange == 0.0) return null

        val drawArea = SIZE - 2 * PADDING

        // Keep aspect ratio
        val scale = if (latRange == 0.0) {
            drawArea / lonRange.toFloat()
        } else if (lonRange == 0.0) {
            drawArea / latRange.toFloat()
        } else {
            minOf(drawArea / latRange.toFloat(), drawArea / lonRange.toFloat())
        }

        val routeW = lonRange.toFloat() * scale
        val routeH = latRange.toFloat() * scale
        val offsetX = PADDING + (drawArea - routeW) / 2f
        val offsetY = PADDING + (drawArea - routeH) / 2f

        fun toX(lon: Double) = offsetX + ((lon - minLon) * scale).toFloat()
        fun toY(lat: Double) = offsetY + ((maxLat - lat) * scale).toFloat()  // flip Y axis

        val bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#1A1A2E"))

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4FC3F7")
            strokeWidth = STROKE
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        val path = Path()
        path.moveTo(toX(locations[0].longitude), toY(locations[0].latitude))
        for (i in 1 until locations.size) {
            path.lineTo(toX(locations[i].longitude), toY(locations[i].latitude))
        }
        canvas.drawPath(path, linePaint)

        // Start dot (green)
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        dotPaint.color = Color.parseColor("#66BB6A")
        canvas.drawCircle(toX(locations.first().longitude), toY(locations.first().latitude), DOT_RADIUS, dotPaint)

        // End dot (red)
        dotPaint.color = Color.parseColor("#EF5350")
        canvas.drawCircle(toX(locations.last().longitude), toY(locations.last().latitude), DOT_RADIUS, dotPaint)

        return bitmap
    }
}
