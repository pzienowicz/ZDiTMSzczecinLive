package pl.pzienowicz.zditmszczecinlive.widget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListProvider(this.applicationContext, intent)
    }
}