package ai.tomorrow.networkrequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private Button mButton;
    private TextView mTextView;

    // thread



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.btn_connect);
        mTextView = findViewById(R.id.textView);

    }
}
