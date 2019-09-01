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
    private TextView mUiMessageNumber;
    private TextView mHandlerThreadNumber;

    private int uiCount = 0;
    private int uiCount2 = 0;
    private int handlerThreadCount1 = 0;
    private int handlerThreadCount2 = 0;
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
                    handlerThreadCount1++;
                    mBackgroundNumber.setText(String.valueOf(handlerThreadCount1));
                }
            });

            Toast.makeText(MainActivity.this, "background " + handlerThreadCount1, Toast.LENGTH_SHORT).show();
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

    // TODO example 4: ui, sendMessage
    private Handler uiHandler2;

    // TODO example 5: HandlerThread, sendMessage
    private Handler backgroundHandler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUiNumber = findViewById(R.id.tv_ui_number);
        mBackgroundNumber = findViewById(R.id.tv_background_number);
        mMyHandlerThreadNumber = findViewById(R.id.tv_MyHandlerThread_number);
        mUiMessageNumber = findViewById(R.id.tv_ui_message_number);
        mHandlerThreadNumber = findViewById(R.id.tv_HandlerThread_number);

        // TODO example 1: ui
        // Main/ui thread no need to start
        uiHandler = new Handler();
//        uiHandler.postDelayed(mainThreadRunner, 1000);

        // TODO example 2: background HandlerThread
        // start a background thread.
        handlerThread = new HandlerThread("HandlerThread1");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
//        backgroundHandler.postDelayed(backgroundThreadRunner, 2000);

        // TODO example 3: background MyHandlerThread
        myHandlerThread = new MyHandlerThread("MyHandlerThread");
        myHandlerThread.start();
        myHandler = new Handler(myHandlerThread.getLooper());
        Log.d(TAG, "onCreate: myHandlerThread.getLooper() = " + myHandlerThread.getLooper());
//        myHandler.postDelayed(myHandlerThreadRunner, 3000);

        // TODO example 4: ui sendMessageDelayed
        uiHandler2 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        Log.d(TAG, "handleMessage: thread id " + Thread.currentThread().getId());
                        uiCount2++;
                        mUiMessageNumber.setText(String.valueOf(uiCount2));
                        uiHandler2.sendEmptyMessageDelayed(1, 1000);
                        break;
                }
            }
        };
//        uiHandler2.sendEmptyMessageDelayed(1, 1000);
        Message msg = new Message();
        msg.what = 1;
//        uiHandler2.sendMessageDelayed(msg, 1000);

        // TODO example 5: MyHandlerThread sendMessageDelayed
        backgroundHandler2 = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage: msg.");
                switch (msg.what){
                    case 1:
                        Log.d(TAG, "handleMessage: thread id " + Thread.currentThread().getId());
                        handlerThreadCount2++;
                        uiHandler2.post(new Runnable() {
                            @Override
                            public void run() {
                                mHandlerThreadNumber.setText(String.valueOf(handlerThreadCount2));
                            }
                        });
                        backgroundHandler2.sendEmptyMessageDelayed(1, 2000);
                        break;
                }
            }
        };
//        backgroundHandler2.sendEmptyMessageDelayed(1, 2000);

    }

    @Override
    public void onPause() {

        uiHandler.removeCallbacks(mainThreadRunner);
        uiHandler.removeCallbacksAndMessages(null);

        backgroundHandler.removeCallbacks(backgroundThreadRunner);

        myHandler.removeCallbacks(myHandlerThreadRunner);

        uiHandler2.removeCallbacksAndMessages(null);

        backgroundHandler2.removeCallbacksAndMessages(null);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHandler.postDelayed(mainThreadRunner, 1000);
        backgroundHandler.postDelayed(backgroundThreadRunner, 2000);
        myHandler.postDelayed(myHandlerThreadRunner, 3000);
        uiHandler2.sendEmptyMessageDelayed(1, 1000);
        backgroundHandler2.sendEmptyMessageDelayed(1, 2000);
    }
}
