package com.kenm.busfinder;

import android.app.Notification;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;



public class MainActivity extends AppCompatActivity{

    final String BLOG_URL = "http://www.dublinbus.ie/RTPI/Sources-of-Real-Time-Information/?searchtype=view&searchquery=3024";
    private String result;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Date nextBusTime = new Date();
    private Handler handler = new Handler();

    final int soundResId = R.raw.ding;

    Ringtone notification1;
    private boolean seven_minuteSent = false;
    private boolean fifteen_minuteSent = false;
    private Date lastUpdateTime = new Date(0);



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // set layout view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        final String packageName = getApplicationContext().getPackageName();
        notification1 = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://" + packageName + "/" + soundResId));

        //getBlogStats();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastUpdateTime = new Date(0);
                reloadInfo();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        Button btn1 = (Button) findViewById(R.id.btnexit);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
                System.exit(0);
            }
        });

        reloadInfo();
        handler.postDelayed(beeper, 1000);

    }




    public void reloadInfo(){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentDateandTime = sdf.format(now);
        long diff = now.getTime() - lastUpdateTime.getTime();
        if ( diff >=60000) {
            ((TextView) findViewById(R.id.TV1)).setText("updating");
            getBlogStats();
            ((TextView) findViewById(R.id.Updated)).setText("Last Updated at :" + currentDateandTime);
            lastUpdateTime = now;
        }
        ((TextView) findViewById(R.id.Time)).setText(currentDateandTime);
    }





    protected void getBlogStats() {
        ((TextView) findViewById(R.id.TV1)).setText("Please Wait!");
        new Thread() {
            public void run() {

                Document document;
                try {
                    Connection con = Jsoup.connect(BLOG_URL);
                    con.timeout(10000);
                    document = con.get();
                    result = "";
                    System.out.println("searching for table");
                    for (Element table : document.select("#rtpi-results")) {
                        for (Element row : table.select("tr")) {
                            Elements tds = row.select("td");
                            if (tds.size() > 1) {
                                System.out.println() ;


                                long diffMinutes = 0;
                                // this is the first one, lets see how many minutes to go
                                String time = tds.get(2).text();
                                Calendar c = Calendar.getInstance();
                                Date rightNow = c.getTime();

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                                SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd_HH:mm");
                                String formattedDate = df.format(c.getTime());
                                String formattedDateNow = dt.format(c.getTime());

                                String firstBusTime = formattedDate+"_"+time;

                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = dateFormat.parse(time);
                                    Date busDate = dt.parse(firstBusTime);
                                    Date nowDate = dt.parse(formattedDateNow);

                                    String out = dateFormat2.format(date);
                                    long diff = busDate.getTime() - nowDate.getTime();
                                    diffMinutes = diff / (60 * 1000);
                                    if (result == "")
                                    {
                                        if (!nextBusTime.equals(busDate)) /* we've a new next bus time . */ {
                                            seven_minuteSent = false;
                                            fifteen_minuteSent = false;

                                            result += "* ";
                                            System.out.println("NEW NEXT BUS TIME, was " + nextBusTime + " now " + busDate);
                                        }

                                        if (diffMinutes <= 7 )
                                        {
                                            if (!seven_minuteSent)
                                            {
                                                notification1.play();
                                                seven_minuteSent = true;
                                                System.out.println("7 minute notification sent");
                                            }
                                        }
                                        else if (diffMinutes <= 15)
                                        {
                                            if(!fifteen_minuteSent)
                                            {
                                                notification1.play();
                                                fifteen_minuteSent = true;
                                                System.out.println("15 minute notification sent");
                                            }
                                        }
                                        nextBusTime = busDate;
                                    }



                                } catch (ParseException e) {
                                    Log.e("error", "rror");
                                }

                                result += tds.get(2).text() +" ("+diffMinutes +")\n";
                            }
                        }
                    }
                } catch (IOException e) {
                    result = "Error";
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {

                    public void run() {
                        ((TextView) findViewById(R.id.TV1)).setText(result);
                        try {
  //                          final String packageName = getApplicationContext().getPackageName();
   //                         Ringtone notification1 = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://" + packageName + "/" + soundResId));


//                            notification1.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }.start();

    }
    final Runnable beeper = new Runnable() {
        public void run() {
            try {
                reloadInfo();
                handler.postDelayed(this, 1000);
            }catch (Exception e) {
                Log.e("TAG","error in executing: It will no longer be run!: "+e.getMessage());
                e.printStackTrace();
            }
        }
    };
}
