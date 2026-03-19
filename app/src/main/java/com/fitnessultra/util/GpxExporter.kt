package com.fitnessultra.util

import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object GpxExporter {

    private val iso8601: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun generate(run: RunEntity, points: List<LocationPoint>): String {
        val runDate = iso8601.format(Date(run.dateTimestamp))
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine(
            """<gpx version="1.1" creator="FitnessUltra" """ +
            """xmlns="http://www.topografix.com/GPX/1/1" """ +
            """xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" """ +
            """xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd">"""
        )
        sb.appendLine("  <metadata>")
        sb.appendLine("    <name>FitnessUltra Run $runDate</name>")
        sb.appendLine("    <time>$runDate</time>")
        sb.appendLine("  </metadata>")
        sb.appendLine("  <trk>")
        sb.appendLine("    <name>Run $runDate</name>")
        sb.appendLine("    <trkseg>")
        for (pt in points) {
            sb.appendLine("""      <trkpt lat="${pt.latitude}" lon="${pt.longitude}">""")
            if (pt.altitude != 0.0) {
                sb.appendLine("        <ele>${String.format(Locale.US, "%.1f", pt.altitude)}</ele>")
            }
            if (pt.timestamp > 0L) {
                sb.appendLine("        <time>${iso8601.format(Date(pt.timestamp))}</time>")
            }
            if (pt.speedMs > 0f) {
                sb.appendLine("        <extensions><speed>${String.format(Locale.US, "%.2f", pt.speedMs)}</speed></extensions>")
            }
            sb.appendLine("      </trkpt>")
        }
        sb.appendLine("    </trkseg>")
        sb.appendLine("  </trk>")
        sb.append("</gpx>")
        return sb.toString()
    }
}
