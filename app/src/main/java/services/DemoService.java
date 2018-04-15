package services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.serviceexample.R;

import java.util.Random;

public class DemoService extends Service {
    private int mRandomNumber;
    private boolean mIsRandomGeneratorOn;

    public static final int GET_COUNT = 0;

    @SuppressLint("HandlerLeak")
    private class RandomNumberRequestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e(getString(R.string.service_demo_tag), "Message intercepted");
            switch (msg.what) {
                case GET_COUNT:
                    Message messageSendRandomNumber = Message.obtain(null, GET_COUNT);
                    messageSendRandomNumber.arg1 = getRandomNumber();
                    try {
                        msg.replyTo.send(messageSendRandomNumber);
                    } catch (RemoteException e) {
                        Log.e(getString(R.string.service_demo_tag), "" + e.getMessage());
                    }
            }
            super.handleMessage(msg);
        }
    }

    public class MyServiceBinder extends Binder {
        /**
         * overide the method getService(),
         * this method will return the service instance.
         *
         * @return
         */
        public DemoService getService() {
            return DemoService.this;
        }
    }

    private IBinder mBinder = new MyServiceBinder();

    /**
     * Return the mBinder in onBind() method
     */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(getString(R.string.service_demo_tag), "In OnReBind");
    }

    private Messenger randomNumberMessenger = new Messenger(new RandomNumberRequestHandler());

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(getString(R.string.service_demo_tag), "Service Started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRandomNumberGenerator();
        Log.e(getString(R.string.service_demo_tag), "Service Destroyed");
    }

    /**
     * onStartCommand() this method will be executed first when you will start the service.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(getString(R.string.service_demo_tag), "In onStartCommend, thread id: " + Thread.currentThread().getId());
        mIsRandomGeneratorOn = true;
        /**
         * startRandomNumberGenerator() is getting called on a new Thread, as it is advised to
         * always execute your task in a separate Thread.
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRandomNumberGenerator();
            }
        }).start();
        return START_STICKY;
    }

    private void startRandomNumberGenerator() {
        while (mIsRandomGeneratorOn) {
            try {
                Thread.sleep(1000); // After every 1 sec, a new Random Number will be generated.
                if (mIsRandomGeneratorOn) {
                    int MIN = 0;
                    int MAX = 100;
                    mRandomNumber = new Random().nextInt(MAX) + MIN;
                    Log.e(getString(R.string.service_demo_tag), "Thread id: " + Thread.currentThread().getId() + ", Random Number: " + mRandomNumber);
                }
            } catch (InterruptedException e) {
                Log.e(getString(R.string.service_demo_tag), "Thread Interrupted");
            }

        }
    }

    private void stopRandomNumberGenerator() {
        mIsRandomGeneratorOn = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(getString(R.string.service_demo_tag), "In onUnbind");
        return super.onUnbind(intent);
    }

    public int getRandomNumber() {
        return mRandomNumber;
    }
}
