package com.lexica.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StreakWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id -> updateWidget(context, manager, id) }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        manager: AppWidgetManager,
        widgetId: Int,
        newOptions: android.os.Bundle
    ) {
        updateWidget(context, manager, widgetId)
    }

    companion object {
        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, StreakWidgetProvider::class.java))
            ids.forEach { updateWidget(context, manager, it) }
        }

        private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val prefs = context.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE)
            val rawCount = prefs.getInt(MainActivity.KEY_STREAK, 0)
            val lastDay = prefs.getString(MainActivity.KEY_LAST_DAY, "") ?: ""
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val today = formatter.format(Date())
            val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val yesterday = formatter.format(yesterdayCalendar.time)
            val count = if (lastDay == today || lastDay == yesterday) rawCount else 0

            val views = RemoteViews(context.packageName, R.layout.widget_streak)
            views.setTextViewText(R.id.widget_streak_count, count.toString())
            views.setTextViewText(R.id.widget_streak_label, streakLabel(context, count))
            val intent = Intent(context, MainActivity::class.java)
            val pending = PendingIntent.getActivity(
                context,
                widgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pending)
            manager.updateAppWidget(widgetId, views)
        }

        private fun streakLabel(context: Context, count: Int): String {
            val isRussian = context.resources.configuration.locales[0].language.startsWith("ru")
            if (!isRussian) return context.getString(R.string.widget_streak_english)
            val n10 = count % 10
            val n100 = count % 100
            return when {
                n10 == 1 && n100 != 11 -> context.getString(R.string.widget_day_one)
                n10 in 2..4 && n100 !in 12..14 -> context.getString(R.string.widget_day_few)
                else -> context.getString(R.string.widget_day_many)
            }
        }
    }
}
