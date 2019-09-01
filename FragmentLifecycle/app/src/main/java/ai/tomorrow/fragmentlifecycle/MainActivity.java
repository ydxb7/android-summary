package ai.tomorrow.fragmentlifecycle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FrameLayout frameLayout;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        frameLayout = findViewById(R.id.container);
//        btn = findViewById(R.id.btn1);
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick.");
//                Fragment fragment = new MyFragment();
//                FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.container, fragment);
//                transaction.addToBackStack("Fragment");
//                transaction.commit();
//            }
//        });
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

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow.");
    }
}
