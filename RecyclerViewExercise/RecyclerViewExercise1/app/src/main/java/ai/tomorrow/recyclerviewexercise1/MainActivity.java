package ai.tomorrow.recyclerviewexercise1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> fakeData = new ArrayList<>();
        fakeData.add("fake data 1");
        fakeData.add("fake data 2");
        fakeData.add("fake data 3");
        fakeData.add("fake data 4");
        fakeData.add("fake data 5");
        fakeData.add("fake data 6");
        fakeData.add("fake data 7");

        adapter = new MyAdapter(fakeData);
        recyclerView.setAdapter(adapter);
    }
}
