package ai.tomorrow.handlerexample;

import android.os.Looper;
import android.os.Process;
import android.util.Log;

/**
 * An easy version of HandlerThread
 */
public class MyHandlerThread extends Thread {
    private static final String TAG = "MyHandlerThread";

    int mPriority;
    Looper mLooper;

    public MyHandlerThread(String name) {
        super(name);
        mPriority = Process.THREAD_PRIORITY_DEFAULT;
    }

    @Override
    public void run() {
        Log.d(TAG, "run...");
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        Looper.loop();
    }

    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }

        // If the thread has been started, wait until the looper has been created.
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.d(TAG, "getLooper: e = " + e.getMessage());
                }
            }
        }
        return mLooper;
    }
}