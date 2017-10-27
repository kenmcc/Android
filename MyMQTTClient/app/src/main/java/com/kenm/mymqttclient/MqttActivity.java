package com.kenm.mymqttclient;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import helpers.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MqttActivity extends AppCompatActivity {

    MqttHelper mqttHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String message = i.getStringExtra("message");
        String topic = i.getStringExtra("topic");
        Log.d("Starting MQTT","MQTT");
        startMqtt(topic,message);
        Log.d("Started", "MQTT");
        //finish();
    }

    private void startMqtt(String topic, String message){
        final String t = topic;
        final String m = message;
        final Activity act = this;

        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                mqttHelper.postToTopic(t, m);
                mqttHelper.disconnect();
                Log.d("posted", "topic");
                finish();
            }

            @Override
            public void connectionLost(Throwable throwable) {
                act.finishAffinity();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                // dataReceived.setText(mqttMessage.toString());
                //mqttHelper.postToTopic();

                Intent intent = new Intent(getApplicationContext(), NewAppWidget.class);
                intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), NewAppWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                intent.putExtra("title", mqttMessage.toString());
                sendBroadcast(intent);



            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d("delivery", "complete");

                act.finishAffinity();

            }
        });
    }


}
