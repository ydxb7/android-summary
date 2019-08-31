package ai.tomorrow.handlerexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView mUiNumber;
    private TextView mUiCount;
    private TextView mBackgroundNumber;
    private TextView mBackgroundCount;

    private int uiCount = 0;
    private int backgroundCount = 0;

    // TODO example 1: ui thread -> add 1/3sec
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

    // TODO example 2: background thread -> add 1/4sec
    // background thread
    private Handler backgroundHandler;
    private HandlerThread handlerThread = new HandlerThread("background_thread_xx");

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUiNumber = findViewById(R.id.tv_ui_number);
        mUiCount = findViewById(R.id.tv_ui_count);
        mBackgroundNumber = findViewById(R.id.tv_background_number);
        mBackgroundCount = findViewById(R.id.tv_background_count);

        // Main/ui thread no need to start
        uiHandler = new Handler();

        // start a background thread.
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());

        mUiCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUiCount.getText().equals("start UI thread +1/s")) {
                    Log.d(TAG, "onClick: ui thread start to count.");
                    mUiCount.setText("stop UI thread +1/s");

                    // start to count every second
                    uiHandler.postDelayed(mainThreadRunner, 1000);
                } else {
                    Log.d(TAG, "onClick: stop ui thread");
                    mUiCount.setText("start UI thread +1/s");

                    uiHandler.removeCallbacks(mainThreadRunner);
                    // remove all runnable posted on this handler.
                    uiHandler.removeCallbacksAndMessages(null);
                }
            }
        });

        mBackgroundCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBackgroundCount.getText().equals("start Background thread +1/2s")) {
                    mBackgroundCount.setText("stop Background thread +1/2s");

                    Log.d(TAG, "onClick: background thread start to count.");
                    backgroundHandler.postDelayed(backgroundThreadRunner, 2000);
                } else {
                    mBackgroundCount.setText("start Background thread +1/2s");
                    backgroundHandler.removeCallbacks(backgroundThreadRunner);
                }

            }
        });


    }

    @Override
    public void onPause() {
        uiHandler.removeCallbacks(mainThreadRunner);
        // remove all runnable posted on this handler.
        uiHandler.removeCallbacksAndMessages(null);

        backgroundHandler.removeCallbacks(backgroundThreadRunner);

        super.onPause();
    }

}
