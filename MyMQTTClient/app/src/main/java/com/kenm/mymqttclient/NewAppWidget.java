package com.kenm.mymqttclient;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewAppWidgetConfigureActivity NewAppWidgetConfigureActivity}
 */
public class NewAppWidget extends AppWidgetProvider {

    static String appString = "";
    public void setMessage(String st)
    {
        appString = st;
    }
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("CREATING", "config activity " + appWidgetId);
        CharSequence widgetText = NewAppWidgetConfigureActivity.loadTitlePref(context, R.id.brokerAddress);
        CharSequence topicText = NewAppWidgetConfigureActivity.loadTitlePref(context, R.id.mqttTopic);
        Log.d("BrokerAddress", String.valueOf(widgetText));
        Log.d("TopicText", String.valueOf(topicText));

        // Construct the RemoteViews object
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, views);

        Log.d("Updated", "updatedaaa aa a a a ");
    }
    static int x = 1;
    static boolean firstTime = true;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        Log.d("state", "onUpdate");
        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            Log.d("state", String.valueOf(widgetId));
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.new_app_widget);
            if (x%2 == 0) {
                remoteViews.setImageViewResource(R.id.imageButton, R.drawable.home);
            }
            else
            {
                remoteViews.setImageViewResource(R.id.imageButton, R.drawable.nothome);
            }
            Intent intent = new Intent(context, NewAppWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);


        }
        if (!firstTime) {
            Log.d("Starting service", "");
            MyMqttPublishService.startActionFoo(context, "honey", "I'm home");
        }
        firstTime = false;
        x += 1;

        Log.d("Service started","");



    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            NewAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
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

