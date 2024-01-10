package com.example.test

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class RemoteDataService : RemoteViewsService() {

    private var array = ArrayList<String>()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        for ( i in 0 until 10) {
            array.add("index - $i")
        }
        return RemoteFactory(this, array)
    }
}

class RemoteFactory(private val context: Context, private val array: ArrayList<String>): RemoteViewsService.RemoteViewsFactory {
    override fun onCreate() {
        Log.d("chunyu", "RemoteFactory onCreate")
    }

    override fun onDataSetChanged() {

    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return array.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val view = RemoteViews(context.packageName, R.layout.appwidget_list_item)
        view.setTextViewText(R.id.item_text, array[position])
        return view
    }

    override fun getLoadingView(): RemoteViews {
        val view = RemoteViews(context.packageName, R.layout.appwidget_list_item)
        view.setTextViewText(R.id.item_text, "loading...")
        return view
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
