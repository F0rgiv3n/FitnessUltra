package com.fitnessultra.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.fitnessultra.R
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import java.text.SimpleDateFormat
import java.util.*

object ShareImageGenerator {

    private const val W = 1080
    private const val H = 1080

    // Layout zones
    private const val HEADER_H = 110f
    private const val MAP_TOP = 118f
    private const val MAP_BOT = 710f
    private const val STATS_TOP = 730f
    private const val STATS_ROW2 = 880f
    private const val FOOTER_TOP = 1030f

    // Colors
    private val BG_TOP    = Color.parseColor("#0D1117")
    private val BG_BOT    = Color.parseColor("#1A2744")
    private val ACCENT    = Color.parseColor("#4FC3F7")
    private val ROUTE_CLR = Color.parseColor("#4FC3F7")
    private val START_DOT = Color.parseColor("#66BB6A")
    private val END_DOT   = Color.parseColor("#EF5350")
    private val MAP_BG    = Color.parseColor("#111827")
    private val WHITE     = Color.WHITE
    private val GRAY      = Color.parseColor("#90A4AE")
    private val DIVIDER   = Color.parseColor("#1E3A5F")

    fun generate(
        locations: List<LocationPoint>,
        run: RunEntity,
        useMiles: Boolean,
        context: Context
    ): Bitmap {
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        drawBackground(canvas)
        drawHeader(canvas)
        drawMap(canvas, locations)
        drawDivider(canvas)
        drawStats(canvas, run, useMiles, context)
        drawFooter(canvas, run)

        return bmp
    }

    // ── Background ──────────────────────────────────────────────────────────────

    private fun drawBackground(canvas: Canvas) {
        val paint = Paint()
        paint.shader = LinearGradient(
            0f, 0f, 0f, H.toFloat(),
            BG_TOP, BG_BOT, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, W.toFloat(), H.toFloat(), paint)
    }

    // ── Header ───────────────────────────────────────────────────────────────────

    private fun drawHeader(canvas: Canvas) {
        // App name
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ACCENT
            textSize = 68f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.1f
        }
        canvas.drawText("FitnessUltra", W / 2f, 78f, titlePaint)

        // Thin accent underline
        val linePaint = Paint().apply {
            color = ACCENT
            strokeWidth = 2f
            alpha = 120
        }
        canvas.drawLine(W / 2f - 160f, 92f, W / 2f + 160f, 92f, linePaint)
    }

    // ── Map ──────────────────────────────────────────────────────────────────────

    private fun drawMap(canvas: Canvas, locations: List<LocationPoint>) {
        val mapRect = RectF(40f, MAP_TOP, W - 40f, MAP_BOT)

        // Map background
        val mapBgPaint = Paint().apply { color = MAP_BG }
        val cornerRadius = 18f
        canvas.drawRoundRect(mapRect, cornerRadius, cornerRadius, mapBgPaint)

        if (locations.size < 2) {
            // No route — just show placeholder text
            val ph = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = GRAY; textSize = 36f; textAlign = Paint.Align.CENTER
            }
            canvas.drawText("No route data", W / 2f, (MAP_TOP + MAP_BOT) / 2f + 12f, ph)
            return
        }

        // Clip to map rect
        canvas.save()
        val clipPath = Path().apply { addRoundRect(mapRect, cornerRadius, cornerRadius, Path.Direction.CW) }
        canvas.clipPath(clipPath)

        // Coordinate mapping
        val lats = locations.map { it.latitude }
        val lons = locations.map { it.longitude }
        val minLat = lats.min(); val maxLat = lats.max()
        val minLon = lons.min(); val maxLon = lons.max()
        val latRange = maxLat - minLat
        val lonRange = maxLon - minLon

        val drawW = mapRect.width() - 80f
        val drawH = mapRect.height() - 80f
        val scale = if (latRange == 0.0 && lonRange == 0.0) 1f
            else minOf(
                if (lonRange > 0) drawW / lonRange.toFloat() else Float.MAX_VALUE,
                if (latRange > 0) drawH / latRange.toFloat() else Float.MAX_VALUE
            )

        val routeW = lonRange.toFloat() * scale
        val routeH = latRange.toFloat() * scale
        val offX = mapRect.left + 40f + (drawW - routeW) / 2f
        val offY = mapRect.top  + 40f + (drawH - routeH) / 2f

        fun toX(lon: Double) = offX + ((lon - minLon) * scale).toFloat()
        fun toY(lat: Double) = offY + ((maxLat - lat) * scale).toFloat()

        // Route shadow
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#80000000")
            strokeWidth = 10f; style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND; strokeCap = Paint.Cap.ROUND
            maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
        }
        val path = buildPath(locations, ::toX, ::toY)
        canvas.drawPath(path, shadowPaint)

        // Route line
        val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ROUTE_CLR; strokeWidth = 7f; style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND; strokeCap = Paint.Cap.ROUND
        }
        canvas.drawPath(path, routePaint)

        // Start dot
        drawDot(canvas, toX(locations.first().longitude), toY(locations.first().latitude), START_DOT, 14f)
        // End dot
        drawDot(canvas, toX(locations.last().longitude), toY(locations.last().latitude), END_DOT, 14f)

        canvas.restore()

        // Map border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = DIVIDER; style = Paint.Style.STROKE; strokeWidth = 2f
        }
        canvas.drawRoundRect(mapRect, cornerRadius, cornerRadius, borderPaint)
    }

    private fun buildPath(
        locations: List<LocationPoint>,
        toX: (Double) -> Float,
        toY: (Double) -> Float
    ): Path {
        val path = Path()
        path.moveTo(toX(locations[0].longitude), toY(locations[0].latitude))
        for (i in 1 until locations.size) {
            path.lineTo(toX(locations[i].longitude), toY(locations[i].latitude))
        }
        return path
    }

    private fun drawDot(canvas: Canvas, x: Float, y: Float, color: Int, r: Float) {
        val outer = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
        val inner = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = Color.WHITE }
        canvas.drawCircle(x, y, r, outer)
        canvas.drawCircle(x, y, r * 0.45f, inner)
    }

    // ── Divider ──────────────────────────────────────────────────────────────────

    private fun drawDivider(canvas: Canvas) {
        val paint = Paint().apply { color = DIVIDER; strokeWidth = 1.5f }
        canvas.drawLine(40f, STATS_TOP - 10f, W - 40f, STATS_TOP - 10f, paint)
    }

    // ── Stats ────────────────────────────────────────────────────────────────────

    private fun drawStats(canvas: Canvas, run: RunEntity, useMiles: Boolean, context: Context) {
        val distance = TrackingUtils.formatDistance(run.distanceMeters, useMiles, context)
        val duration = TrackingUtils.formatTime(run.durationMillis)
        val pace     = TrackingUtils.calculatePace(run.distanceMeters, run.durationMillis, useMiles, context)
        val calories = context.getString(R.string.calories_format, run.caloriesBurned)

        // Row 1: Distance | Duration
        drawStatCell(canvas, distance, context.getString(R.string.label_distance),
            W / 4f, STATS_TOP + 75f)
        drawStatCell(canvas, duration, context.getString(R.string.label_duration),
            W * 3 / 4f, STATS_TOP + 75f)

        // Vertical divider between row 1 cols
        drawVertDiv(canvas, W / 2f, STATS_TOP + 5f, STATS_ROW2 - 5f)

        // Row 2: Pace | Calories
        drawStatCell(canvas, pace, context.getString(R.string.label_pace),
            W / 4f, STATS_ROW2 + 75f)
        drawStatCell(canvas, calories, context.getString(R.string.label_calories),
            W * 3 / 4f, STATS_ROW2 + 75f)

        drawVertDiv(canvas, W / 2f, STATS_ROW2 + 5f, FOOTER_TOP - 5f)

        // Horizontal divider between rows
        val hDivPaint = Paint().apply { color = DIVIDER; strokeWidth = 1f }
        canvas.drawLine(40f, STATS_ROW2, W - 40f, STATS_ROW2, hDivPaint)
    }

    private fun drawStatCell(canvas: Canvas, value: String, label: String, cx: Float, baseY: Float) {
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = WHITE; textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = GRAY; textSize = 32f
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.08f
        }
        // Scale down value text if too wide
        val maxWidth = W / 2f - 40f
        if (valuePaint.measureText(value) > maxWidth) {
            valuePaint.textSize = 72f * maxWidth / valuePaint.measureText(value)
        }
        canvas.drawText(value, cx, baseY, valuePaint)
        canvas.drawText(label.uppercase(), cx, baseY + 42f, labelPaint)
    }

    private fun drawVertDiv(canvas: Canvas, x: Float, y1: Float, y2: Float) {
        val paint = Paint().apply { color = DIVIDER; strokeWidth = 1f }
        canvas.drawLine(x, y1, x, y2, paint)
    }

    // ── Footer ───────────────────────────────────────────────────────────────────

    private fun drawFooter(canvas: Canvas, run: RunEntity) {
        val sdf = SimpleDateFormat("dd MMM yyyy  ·  HH:mm", Locale.getDefault())
        val dateStr = sdf.format(Date(run.dateTimestamp))

        val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = GRAY; textSize = 30f; textAlign = Paint.Align.CENTER
        }
        canvas.drawText(dateStr, W / 2f, FOOTER_TOP + 36f, datePaint)
    }
}
