package ai.tomorrow.networkrequest;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy.");
    }
}
