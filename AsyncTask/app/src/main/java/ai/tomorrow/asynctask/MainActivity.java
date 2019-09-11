package ai.tomorrow.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ProgressBar progressBar;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;

    private TextView textView;

    private Button download_button;
    private Button cancel_button;

    private final String url1 = "https://static.photocdn.pt/images/articles/2018/03/09/articles/2017_8/landscape_photography.jpg";
    private final String url2 = "https://static.photocdn.pt/images/articles/2018/03/09/articles/2017_8/landscape_photography.jpg";
    private final String url3 = "https://static.photocdn.pt/images/articles/2018/03/09/articles/2017_8/landscape_photography.jpg";
    private final String url4 = "https://static.photocdn.pt/images/articles/2018/03/09/articles/2017_8/landscape_photography.jpg";
    private MyTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.demo_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        imageView1 = findViewById(R.id.demo_image1);
        imageView2 = findViewById(R.id.demo_image2);
        imageView3 = findViewById(R.id.demo_image3);
        imageView4 = findViewById(R.id.demo_image4);
        textView = findViewById(R.id.demo_showProgress);
        download_button = findViewById(R.id.demo_button);
        cancel_button = findViewById(R.id.cancel_button);

        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: start to download.");
                imageView1.setImageBitmap(null);
                imageView2.setImageBitmap(null);
                imageView3.setImageBitmap(null);
                imageView4.setImageBitmap(null);
                myTask = new MyTask();
                myTask.execute(url1, url2, url3, url4);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cancel download.");
                if(myTask != null){
                    myTask.cancel(true);
                    textView.setText("Task canceled.");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private class MyTask extends AsyncTask<String, String, List<Bitmap>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("start AsyncTask");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Bitmap> doInBackground(String... strings) {

            List<Bitmap> bitmaps = new ArrayList<>();

            for (int i = 0; i < strings.length; i++){
                Log.d(TAG, "doInBackground: downloading image: " + i);
                publishProgress("downloading image: " + (i + 1));
                if(isCancelled()){
                    Log.d(TAG, "doInBackground: cancel task.");
                    break;
                }

                bitmaps.add(getBitmapFromUrl(strings[i]));
            }

            return bitmaps;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "onProgressUpdate: " + values);
            textView.setText(values[0]);
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);

            imageView1.setImageBitmap(bitmaps.get(0));
            imageView2.setImageBitmap(bitmaps.get(1));
            imageView3.setImageBitmap(bitmaps.get(2));
            imageView4.setImageBitmap(bitmaps.get(3));

            textView.setText("Done");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public Bitmap getBitmapFromUrl(String urlString) {
        //获取okHttp对象get请求
        try {
            OkHttpClient client = new OkHttpClient();
            //获取请求对象
            Request request = new Request.Builder().url(urlString).build();
            //获取响应体
            ResponseBody body = client.newCall(request).execute().body();
            //获取流
            InputStream in = body.byteStream();
            //转化为bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            Log.d(TAG, "getBitmapFromUrl: get bitmap.");
            return bitmap;
        } catch (IOException e) {
            Log.d(TAG, "getBitmapFromUrl: e " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: cancel the task.");

        if(myTask != null){
            myTask.cancel(true);
        }

    }
}