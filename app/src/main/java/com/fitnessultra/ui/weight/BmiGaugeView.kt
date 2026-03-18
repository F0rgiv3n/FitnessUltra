package com.fitnessultra.ui.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class BmiGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var bmi: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val bmiMin = 10f
    private val bmiMax = 40f

    private val segments = listOf(
        Triple(18.5f, "#1565C0".toColorInt(), "Under"),
        Triple(25f,   "#388E3C".toColorInt(), "Normal"),
        Triple(30f,   "#F57F17".toColorInt(), "Over"),
        Triple(40f,   "#D32F2F".toColorInt(), "Obese")
    )

    private val arcPaint    = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.BUTT }
    private val trackPaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.BUTT; color = "#E0E0E0".toColorInt() }
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL_AND_STROKE; strokeCap = Paint.Cap.ROUND }
    private val textPaint   = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; textAlign = Paint.Align.CENTER }
    private val dotPaint    = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val tickPaint   = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; color = "#FFFFFF".toColorInt(); strokeCap = Paint.Cap.ROUND }

    private val oval = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val radius = w * 0.252f          // 0.28 × 0.9 = 10% smaller
        val strokeW = radius * 0.24f
        val outerLabelR = radius + strokeW * 0.9f
        val labelTextH = radius * 0.16f
        // cy must clear the outer labels above it: cy >= outerLabelR + labelTextH + small pad
        val cy = outerLabelR + labelTextH + radius * 0.08f
        // bottom: labels at 0°/180° sit at cy, text baseline cy + labelTextH*0.35
        val h = (cy + labelTextH * 0.5f + radius * 0.08f).toInt()
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val radius = w * 0.252f
        val strokeW = radius * 0.24f
        val outerLabelR = radius + strokeW * 0.9f
        val labelTextH = radius * 0.16f
        val cy = outerLabelR + labelTextH + radius * 0.08f

        oval.set(cx - radius, cy - radius, cx + radius, cy + radius)

        // Background track
        trackPaint.strokeWidth = strokeW
        canvas.drawArc(oval, 180f, 180f, false, trackPaint)

        // Colored segments
        arcPaint.strokeWidth = strokeW
        var prevBmi = bmiMin
        for ((bmiEnd, color, _) in segments) {
            val startAngle = 180f + (prevBmi - bmiMin) / (bmiMax - bmiMin) * 180f
            val sweep = (bmiEnd - prevBmi) / (bmiMax - bmiMin) * 180f
            arcPaint.color = color
            canvas.drawArc(oval, startAngle, sweep, false, arcPaint)
            prevBmi = bmiEnd
        }

        // Tick marks at boundaries
        tickPaint.strokeWidth = strokeW * 0.12f
        val innerR = radius - strokeW * 0.5f
        val outerR = radius + strokeW * 0.5f
        for (b in listOf(bmiMin, 18.5f, 25f, 30f, bmiMax)) {
            val angleRad = bmiToRad(b)
            canvas.drawLine(
                cx + (innerR * cos(angleRad)).toFloat(), cy + (innerR * sin(angleRad)).toFloat(),
                cx + (outerR * cos(angleRad)).toFloat(), cy + (outerR * sin(angleRad)).toFloat(),
                tickPaint
            )
        }

        // Boundary labels outside arc
        textPaint.textSize = radius * 0.16f
        textPaint.color = "#444444".toColorInt()
        val labelR = radius + strokeW * 0.9f
        val labelBmis  = listOf(bmiMin, 18.5f, 25f, 30f, bmiMax)
        val labelTexts = listOf("10", "18.5", "25", "30", "40")
        for (i in labelBmis.indices) {
            val ar = bmiToRad(labelBmis[i])
            val lx = cx + (labelR * cos(ar)).toFloat()
            val ly = cy + (labelR * sin(ar)).toFloat()
            canvas.drawText(labelTexts[i], lx, ly + textPaint.textSize * 0.35f, textPaint)
        }

        // Category labels inside arc
        textPaint.textSize = radius * 0.13f
        textPaint.color = "#FFFFFF".toColorInt()
        val catMids = listOf((bmiMin + 18.5f) / 2f, (18.5f + 25f) / 2f, (25f + 30f) / 2f, (30f + bmiMax) / 2f)
        val catR = radius - strokeW * 1.15f
        for (i in segments.indices) {
            val ar = bmiToRad(catMids[i])
            val lx = cx + (catR * cos(ar)).toFloat()
            val ly = cy + (catR * sin(ar)).toFloat()
            canvas.drawText(segments[i].third, lx, ly + textPaint.textSize * 0.35f, textPaint)
        }

        // Needle
        if (bmi > 0f) {
            val clamped = bmi.coerceIn(bmiMin, bmiMax)
            val ar = bmiToRad(clamped)
            val needleLen = radius * 0.80f
            val tailLen   = radius * 0.18f

            needlePaint.color = "#212121".toColorInt()
            needlePaint.strokeWidth = strokeW * 0.18f
            canvas.drawLine(
                cx - (tailLen * cos(ar)).toFloat(), cy - (tailLen * sin(ar)).toFloat(),
                cx + (needleLen * cos(ar)).toFloat(), cy + (needleLen * sin(ar)).toFloat(),
                needlePaint
            )

            dotPaint.color = "#424242".toColorInt()
            canvas.drawCircle(cx, cy, radius * 0.09f, dotPaint)
            dotPaint.color = "#FFFFFF".toColorInt()
            canvas.drawCircle(cx, cy, radius * 0.045f, dotPaint)
        }
    }

    private fun bmiToRad(bmi: Float): Double =
        Math.toRadians((180.0 + (bmi - bmiMin) / (bmiMax - bmiMin) * 180.0))
}
