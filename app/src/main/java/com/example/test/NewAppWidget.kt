package com.example.test

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.widget.RemoteViewsCompat

/*
* @author chunyu
* */
class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            updateWithIntent(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateWithIntent(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)
    val intent = Intent(context, RemoteDataService::class.java)
    views.setRemoteAdapter(R.id.appwidget_list, intent)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    val array = arrayListOf<String>()
    for (i in 0 until 10) {
        array.add("index - $i")
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val builder = RemoteViews.RemoteCollectionItems.Builder()
        array.forEachIndexed { index, s ->
            val view = RemoteViews(context.packageName, R.layout.appwidget_list_item)
            view.setTextViewText(R.id.item_text, array[index])
            builder.addItem(index.toLong(), view)
        }
        views.setRemoteAdapter(R.id.appwidget_list, builder.build())
    } else {
        val builder = RemoteViewsCompat.RemoteCollectionItems.Builder()
        array.forEachIndexed { index, s ->
            val view = RemoteViews(context.packageName, R.layout.appwidget_list_item)
            view.setTextViewText(R.id.item_text, array[index])
            builder.addItem(index.toLong(), view)
        }
        RemoteViewsCompat.setRemoteAdapter(context, views, appWidgetId, R.id.appwidget_list, builder.build())
    }
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}