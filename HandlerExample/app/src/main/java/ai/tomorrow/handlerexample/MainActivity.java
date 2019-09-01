package ai.tomorrow.handlerexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView mUiNumber;
    private TextView mBackgroundNumber;
    private TextView mMyHandlerThreadNumber;

    private int uiCount = 0;
    private int backgroundCount = 0;
    private int myHandlerThreadCount = 0;

    // TODO example 1: ui thread -> add 1/sec
    // ui thread
    private Handler uiHandler;

    private Runnable mainThreadRunner = new Runnable() {
        @Override
        public void run() {
            Log.d("TAG", "mainThreadRunner, thread name: " + Thread.currentThread().getName());

            uiHandler.postDelayed(mainThreadRunner, 1000);
            uiCount++;
            mUiNumber.setText(String.valueOf(uiCount));
        }
    };

    // TODO example 2: background thread -> add 1/2sec
    // background thread
    private Handler backgroundHandler;
    private HandlerThread handlerThread;

    private Runnable backgroundThreadRunner = new Runnable() {
        @Override
        public void run() {
            Log.d("TAG", "backgroundThreadRunner, thread name: " + Thread.currentThread().getName());

            backgroundHandler.postDelayed(backgroundThreadRunner, 2000);

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    backgroundCount++;
                    mBackgroundNumber.setText(String.valueOf(backgroundCount));
                }
            });

            Toast.makeText(MainActivity.this, "background " + backgroundCount, Toast.LENGTH_SHORT).show();
        }
    };

    // TODO example 3: MyHandlerThread -> add 1/3sec
    private MyHandlerThread myHandlerThread;
    private Handler myHandler;
    private Runnable myHandlerThreadRunner = new Runnable() {
        @Override
        public void run() {
            Log.d("TAG", "myHandlerThreadRunner, thread name: " + Thread.currentThread().getName());

            myHandler.postDelayed(myHandlerThreadRunner, 3000);

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    myHandlerThreadCount++;
                    mMyHandlerThreadNumber.setText(String.valueOf(myHandlerThreadCount));
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUiNumber = findViewById(R.id.tv_ui_number);
        mBackgroundNumber = findViewById(R.id.tv_background_number);
        mMyHandlerThreadNumber = findViewById(R.id.tv_MyHandlerThread_number);

        // TODO example 1: ui
        // Main/ui thread no need to start
        uiHandler = new Handler();
        uiHandler.postDelayed(mainThreadRunner, 1000);

        // TODO example 2: background HandlerThread
        // start a background thread.
        handlerThread = new HandlerThread("background_thread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
        backgroundHandler.postDelayed(backgroundThreadRunner, 2000);

        // TODO example 3: background MyHandlerThread
        myHandlerThread = new MyHandlerThread("MyHandlerThread");
        myHandlerThread.start();
        myHandler = new Handler(myHandlerThread.getLooper());
        Log.d(TAG, "onCreate: myHandlerThread.getLooper() = " + myHandlerThread.getLooper());
        myHandler.postDelayed(myHandlerThreadRunner, 3000);
    }

    @Override
    public void onPause() {

        uiHandler.removeCallbacks(mainThreadRunner);
        uiHandler.removeCallbacksAndMessages(null);

        backgroundHandler.removeCallbacks(backgroundThreadRunner);

        myHandler.removeCallbacks(myHandlerThreadRunner);

        super.onPause();
    }

}
