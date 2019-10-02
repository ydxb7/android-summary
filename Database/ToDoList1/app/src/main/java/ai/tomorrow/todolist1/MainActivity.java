package ai.tomorrow.todolist1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyAdapter.onDeleteClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Button mAddButton;
    private EditText mAddEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        mAddButton = findViewById(R.id.add_btn);
        mAddEditText = findViewById(R.id.editText);

        TodoDbHelper dbHelper = new TodoDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
//        insertFakeData();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        Cursor cursor = getAllList();


        mAdapter = new MyAdapter(cursor, this);
        recyclerView.setAdapter(mAdapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add a new message");
                if(mAddEditText.getText().equals("")){
                    Toast.makeText(MainActivity.this, "you can't add an empty message", Toast.LENGTH_SHORT).show();
                } else {
                    insertNewMessage();

                    Cursor cursor = getAllList();
                    mAdapter.swapCursor(cursor);
                    mAddEditText.setText("");
                }
            }
        });



    }

    private Cursor getAllList() {
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ToDoListContract.ToDoListEntry.COLUMN_TIMESTAMP;

        return mDb.query(
                ToDoListContract.ToDoListEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
    }

    private long insertNewMessage() {
        Log.d(TAG, "insertNewMessage: ");
        ContentValues cv = new ContentValues();
        cv.put(ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE, mAddEditText.getText().toString());
        return mDb.insert(ToDoListContract.ToDoListEntry.TABLE_NAME, null, cv);
    }

    public void hideSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    private void insertFakeData() {
        // Create a new map of values, where column names are the keys
        List<ContentValues> list = new ArrayList<>();

        ContentValues cv = new ContentValues();
        cv.put(ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE, "fake data 1");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE, "fake data 2");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE, "fake data 3");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE, "fake data 4");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE, "fake data 5");
        list.add(cv);

        try {
            mDb.beginTransaction();
            mDb.delete(ToDoListContract.ToDoListEntry.TABLE_NAME, null, null);

            for (ContentValues c : list) {
                mDb.insert(ToDoListContract.ToDoListEntry.TABLE_NAME, null, c);
            }

            mDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    public void onDeleteClickListener(long id) {
        Log.d(TAG, "onDeleteClickListener: ");
        // Define 'where' part of query.
        String selection = ToDoListContract.ToDoListEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };
        // Issue SQL statement.
        int deletedRows = mDb.delete(ToDoListContract.ToDoListEntry.TABLE_NAME, selection, selectionArgs);
        Cursor cursor = getAllList();
        mAdapter.swapCursor(cursor);
    }
}
