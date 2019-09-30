package ai.tomorrow.recyclerviewexercise2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private EditText editText;
    private Adapter mAdapter;
    private List<String> myData = new ArrayList<>();
    private List<String> mySearchResult = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);

        setUpWidgets();

    }

    private void setUpWidgets() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        for (int i = 0; i < 1000; i++) {
            myData.add("item" + i);
        }
        Log.d(TAG, "onCreate: myData.size = " + myData.size());

        mAdapter = new Adapter(myData);
        recyclerView.setAdapter(mAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);

                mySearchResult.clear();
                String search = s.toString();
                for (int i = 0; i < myData.size(); i++) {
                    if (myData.get(i).contains(search)) {
                        mySearchResult.add(myData.get(i));
                    }
                }
                Log.d(TAG, "onTextChanged: setData " + mySearchResult.size());
                mAdapter.setData(mySearchResult);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s);

            }
        });
    }
}
