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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ProgressBar progressBar;

    private ImageView imageView;

    private TextView textView;

    private Button button;

    private final String url = "https://static.photocdn.pt/images/articles/2018/03/09/articles/2017_8/landscape_photography.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.demo_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        imageView = findViewById(R.id.demo_image);
        textView = findViewById(R.id.demo_showProgress);
        button = findViewById(R.id.demo_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(null);
                new MyTask().execute(url);
            }
        });
    }

    private class MyTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("start AsyncTask");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            publishProgress("downloading image...");
            return getBitmapFromUrl(strings[0]);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "onProgressUpdate: values[0] = " + values[0]);
            textView.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
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
}