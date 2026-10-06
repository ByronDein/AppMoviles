package com.example.conectasenas

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

// Widget de la pantalla de inicio: un botón que abre la aplicación.
class WidgetConectaSenas : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (idWidget in appWidgetIds) {
            val vista = RemoteViews(context.packageName, R.layout.widget_conectasenas)

            val intentAbrir = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intentAbrir,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            vista.setOnClickPendingIntent(R.id.boton_abrir_app, pendingIntent)
            appWidgetManager.updateAppWidget(idWidget, vista)
        }
    }
}
