package com.serviceexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import services.DemoService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView txtDataFetched;
    private DemoService demoService;

    int count = 0;
    private boolean mServiceBound;
    private ServiceConnection mServiceConnection;


    private DemoService myService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;

    /*Handler handler;*/


    private Intent serviceIntent;

    private boolean mStopLoop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnBind = findViewById(R.id.btnBind);
        Button btnUnbind = findViewById(R.id.btnUnbind);
        Button btnFetch = findViewById(R.id.btnFetch);

        txtDataFetched = (TextView) findViewById(R.id.txtDataFetched);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnBind.setOnClickListener(this);
        btnUnbind.setOnClickListener(this);
        btnFetch.setOnClickListener(this);
        serviceIntent = new Intent(getApplicationContext(), DemoService.class);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnStart:
                mStopLoop = true;
                startService(serviceIntent);
                break;
            case R.id.btnStop:
                stopService(serviceIntent);
                break;
            case R.id.btnBind:
                bindTheService();
                break;
            case R.id.btnUnbind:
                unbindTheService();
                break;
            case R.id.btnFetch:
                setRandomNumber();
                break;

            default:
                break;
        }
    }


    private void bindTheService() {
        if (mServiceConnection == null) {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    DemoService.MyServiceBinder myServiceBinder = (DemoService.MyServiceBinder) iBinder;
                    myService = myServiceBinder.getService();
                    isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    isServiceBound = false;
                }
            };
        }

        /**
         * Client means the Android Component which is trying to connect with the Service.
         * */

        /**
         * A client binds to a service by calling bindService().
         * When it does, it must provide an implementation of ServiceConnection,
         * which monitors the connection with the service.
         * The return value of bindService() indicates whether the requested service exists
         * and whether the client is permitted access to it.
         * When the Android system creates the connection between the client and service,
         * it calls onServiceConnected() on the ServiceConnection.
         * */
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private void unbindTheService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    private void setRandomNumber() {
        if (isServiceBound) {
            txtDataFetched.setText("Random number: " + myService.getRandomNumber());
        } else {
            txtDataFetched.setText("Service not bound");
        }
    }

}
