package ai.tomorrow.networkrequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ai.tomorrow.base.NetManager;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private Button mButton;
    private TextView mTextView;

    // thread
    private Handler mHandler;
    private HandlerThread mThread = new HandlerThread("background_thread");
    private Handler uiHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // widgets
        mButton = findViewById(R.id.btn_connect);
        mTextView = findViewById(R.id.textView);

        // handler
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        // net
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: start to connect the network.");

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: on background_thread.");

                        getNetManager().run("https://www.google.com", new NetManager.Callback() {
                            @Override
                            public void onResponse(final String s) {
                                Log.d(TAG, "onResponse: network response.");

                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "run: post result.");
                                        mTextView.setText(s);
                                    }
                                });
                            }

                            @Override
                            public void onError(final String e) {
                                Log.d(TAG, "onError: network error.");

                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "run: post error.");
                                        mTextView.setText(e);
                                    }
                                });
                            }
                        });

                    }
                });

            }
        });


    }
}
