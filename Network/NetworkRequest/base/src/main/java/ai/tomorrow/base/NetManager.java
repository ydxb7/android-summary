package ai.tomorrow.base;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetManager {
    private static final String TAG = "NetManager";

    private OkHttpClient mClient;
    private Context mContext;


    public interface Callback {
        void onResponse(String s);

        void onError(String e);
    }

    public NetManager(Context applicationContext) {
        this.mContext = applicationContext;
    }

    public OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            mClient = new OkHttpClient();
        }

        return mClient;
    }

    public void run(String url, Callback callback) {
        Log.d(TAG, "run: start to get network request.");
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        Response response = null;
        try {

            response = getOkHttpClient().newCall(request).execute();

            Log.d(TAG, "inx run message:: " + response.message());
            String res = response.body().string();
            Log.d(TAG, "inx run body:: " + res);
            callback.onResponse(res);

        } catch (Exception e) {
            Log.d(TAG, "run: e: " + e.getMessage());
            callback.onError(e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
