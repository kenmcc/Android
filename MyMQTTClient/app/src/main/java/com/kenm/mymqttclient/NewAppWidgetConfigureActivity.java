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
    public static int mBrokerAddressId = R.id.brokerAddress;
    public static int mMqttTopicId = R.id.mqttTopic;
    public static int mBrokerPortId = R.id.brokerPort;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    EditText mBrokerAddress;
    EditText mMqttTopic;
    EditText mBrokerPort;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = NewAppWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String brokerAddressText = mBrokerAddress.getText().toString();
            String mqttTopicText = mMqttTopic.getText().toString();
            String mqttBrokerPortText = mBrokerPort.getText().toString();

            saveTitlePref(context, mBrokerAddressId, brokerAddressText);
            saveTitlePref(context, mMqttTopicId, mqttTopicText);
            saveTitlePref(context, mBrokerPortId, mqttBrokerPortText);


            //  Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent(getApplicationContext(), NewAppWidget.class);
            resultValue.setAction(NewAppWidget.ACTION_RELOAD);
            resultValue.putExtra("broker", brokerAddressText);
            resultValue.putExtra("topic", mqttTopicText);
            resultValue.putExtra("port", mqttBrokerPortText);
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            sendBroadcast(resultValue);
            finish();
        }
    };

    public NewAppWidgetConfigureActivity() {
        super();
    }


    static void saveTitlePref(Context context, int widgetId, String value)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + widgetId, value);
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
        setResult(RESULT_OK);

        setContentView(R.layout.new_app_widget_configure);
        mBrokerAddress = (EditText) findViewById(R.id.brokerAddress);
        mMqttTopic = (EditText) findViewById(R.id.mqttTopic);
        mBrokerPort = (EditText) findViewById(R.id.brokerPort);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mBrokerAddressId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        Log.d("Setting the fields", "begin");
        mBrokerAddress.setText(loadTitlePref(NewAppWidgetConfigureActivity.this, mBrokerAddressId));
        mMqttTopic.setText(loadTitlePref(NewAppWidgetConfigureActivity.this, mMqttTopicId));
        mBrokerPort.setText(loadTitlePref(NewAppWidgetConfigureActivity.this, mBrokerPortId));
        Log.d("Done setting", "done");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d("BACK", "pressed");
    }
}

