package ai.tomorrow.networkrequest;

import android.app.Application;

import ai.tomorrow.base.NetManager;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private NetManager mNetManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetManager = new NetManager(this);
    }

    public NetManager getNetManager() {
        return mNetManager;
    }
}
