package ai.tomorrow.fragmentlifecycle;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyFragment extends Fragment {
    private static final String TAG = "MyFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView.");

        View view = inflater.inflate(R.layout.fragment_my, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy.");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach.");
    }
}