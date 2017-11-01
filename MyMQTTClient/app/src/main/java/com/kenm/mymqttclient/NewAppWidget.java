package com.kenm.mymqttclient;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
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
    static String brokerString= "127.0.0.1";
    static String brokerPort = "1883";
    static String topicString = "";
    static String ACTION_RELOAD="RELOAD_WIDGET";
    static boolean connected = false;
    public void setMessage(String st)
    {
        appString = st;
    }
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("CREATING", "config activity " + appWidgetId);
        CharSequence widgetText = NewAppWidgetConfigureActivity.loadTitlePref(context, R.id.brokerAddress);
        CharSequence topicText = NewAppWidgetConfigureActivity.loadTitlePref(context, R.id.mqttTopic);
        brokerString = String.valueOf(widgetText);
        topicString = String.valueOf(topicText);

        Log.d("brokerString = ", brokerString);
        Log.d("topicString", topicString);



        Log.d("Updated", "updatedaaa aa a a a "+ brokerString);
    }
    static int x = 1;
    static boolean firstTime = true;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        String theMessage = "";
        Log.d("state", "onUpdate count = " + count);
        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            Log.d("state", String.valueOf(widgetId));
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.new_app_widget);
            //if (x%2 == 0) {
                Log.d("Setting Home", "begin");
                remoteViews.setImageViewResource(R.id.imageButton, R.drawable.home);
                theMessage = "Home";
                Log.d("Setting Home", "end");

//            }
  /*          else
            {
                Log.d("Setting Away", "begin");

                remoteViews.setImageViewResource(R.id.imageButton, R.drawable.nothome);
                theMessage = "Away";
                Log.d("Setting Away", "end");

            }*/
            Intent intent = new Intent(context, NewAppWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            Log.d("loop", "done");


        }
        Log.d("abba", "1");
        if (!firstTime) {
            Log.d("Starting service", "");
            String theBroker = "tcp://"+brokerString + ":" + brokerPort;
            String theTopic = "HoneyImHome/"+topicString;
            MyMqttPublishService.startActionFoo(context, theBroker,theTopic, theMessage);
        }
        Log.d("abba", "2");
        firstTime = false;
        x += 1;
        Log.d("abba", "3");
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

    @Override
    public void onReceive(Context context, Intent intent) {
//
        final String theAction = intent.getAction();
        Log.d("ACTION = ", theAction);
        // do your stuff here on ACTION_RELOAD
        if (theAction == ACTION_RELOAD) {
            firstTime = true;
            brokerString = intent.getStringExtra("broker");
            topicString = intent.getStringExtra("topic");
            brokerPort = intent.getStringExtra("port");
            Log.d("Setting brokerAddress to ", brokerString);
        }
        else if (theAction.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            // save the connected state to get in onUpdate
            this.connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);

            if (!this.connected)
            {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.new_app_widget);
                remoteViews.setImageViewResource(R.id.imageButton, R.drawable.nothome);
            }
            // update all widgets
            //int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
            //onUpdate(context, appWidgetManager, appWidgetIds);

        }


        super.onReceive(context, intent);
    }
}

