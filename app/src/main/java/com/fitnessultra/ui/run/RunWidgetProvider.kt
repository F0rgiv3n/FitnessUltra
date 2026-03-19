package com.fitnessultra.ui.run

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.fitnessultra.MainActivity
import com.fitnessultra.R

class RunWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            manager.updateAppWidget(id, buildIdleViews(context))
        }
    }

    companion object {
        fun buildIdleViews(context: Context): RemoteViews =
            RemoteViews(context.packageName, R.layout.widget_run).apply {
                setTextViewText(R.id.widgetStatus, "FitnessUltra")
                setTextViewText(R.id.widgetTimer, "--:--:--")
                setTextViewText(R.id.widgetStats, context.getString(R.string.widget_no_run))
                val intent = PendingIntent.getActivity(
                    context, 0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widgetRoot, intent)
            }
    }
}
