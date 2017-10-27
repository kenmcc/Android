package com.kenm.mymqttclient;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * The configuration screen for the {@link NewAppWidget NewAppWidget} AppWidget.
 */
public class NewAppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.kenm.mymqttclient.NewAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    public int mBrokerAddressId = R.id.brokerAddress;
    public int mMqttTopicId = R.id.mqttTopic;

    EditText mBrokerAddress;
    EditText mMqttTopic;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = NewAppWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String brokerAddressText = mBrokerAddress.getText().toString();
            String mqttTopicText = mMqttTopic.getText().toString();

            Log.d("mBrokerAddressId", String.valueOf(mBrokerAddressId));
            Log.d("mMqttTopicId", String.valueOf(mMqttTopicId));

            saveTitlePref(context, mBrokerAddressId, brokerAddressText);
            saveTitlePref(context, mMqttTopicId, mqttTopicText);



            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            NewAppWidget.updateAppWidget(context, appWidgetManager, mBrokerAddressId);
            //NewAppWidget.updateAppWidget(context, appWidgetManager, mMqttTopicId);


            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mBrokerAddressId);
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mMqttTopicId);

            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public NewAppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.new_app_widget_configure);
        mBrokerAddress = (EditText) findViewById(R.id.brokerAddress);
        mMqttTopic = (EditText) findViewById(R.id.mqttTopic);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mBrokerAddressId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            mMqttTopicId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mBrokerAddressId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mBrokerAddress.setText(loadTitlePref(NewAppWidgetConfigureActivity.this, mBrokerAddressId));
    }
}

