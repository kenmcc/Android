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
        String broker = i.getStringExtra("broker");
        String message = i.getStringExtra("message");
        String topic = i.getStringExtra("topic");
        Log.d("Starting MQTT","MQTT");
        startMqtt(broker, topic,message);
        Log.d("Started", "MQTT");
        finish();
    }

    private void startMqtt(String inbroker, String intopic, String inmessage){
        final String topic = intopic;
        final String message = inmessage;
        final String broker = inbroker;
        final Activity act = this;

        mqttHelper = new MqttHelper(getApplicationContext(), broker);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                mqttHelper.postToTopic(topic, message);
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


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d("Delivery", "complete");

                act.finishAffinity();

            }
        });
    }


}
