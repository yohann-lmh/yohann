package com.example.game.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.game.R;
import com.example.game.RankingListActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RankListWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rank_list_widget);
        views.setTextViewText(R.id.appwidget_btn, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for(int x = 0; x < appWidgetIds.length;x++) {

            Intent intent = new Intent(context, RankingListActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,

                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews remote = new RemoteViews(context.getPackageName(),

                    R.layout.rank_list_widget);

            remote.setOnClickPendingIntent(R.id.appwidget_btn, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[x], remote);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

