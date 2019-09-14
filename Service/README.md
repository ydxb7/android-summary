# Service

**如果要加载用于界面的内容，用Loader。如果处理的内容与界面无关就用service**

特殊 Service: **Foreground Service**. 例如音乐播放器，谷歌地图，即使 navigate away app 界面也能一直播放. 与 background service 不同的地方在于，用户会一直知道有一个 Foreground Service 正在进行，因为安卓要求 Foreground Service **持续发出一个不可忽略的持续的通知 non-dismissible ongoing notification**。经常用于告知用户一个 real-time progress of a long-running operation. 优先级高，在系统 memory constrained 的情况下也不会关闭。

Learn how to use Service and send notifications and schedule the job work in background.

3 ways to start a service:

* **manually start a service**: 通常处理service后**不会** communicate back to 开启他的组件。例如用service发送邮件，发完就不通知你了。
* **schedule a service**: **JobService** 会在满足一定条件后自动开启（充电情况，时间...）。 
* **bind to the service**: 使用`bindService()`, **会** communicate back to 开启他的组件. 例如用 service 播放音乐，希望界面根据音乐播放的情况显示相关内容（已经播放的时间），页希望通过按钮控制service。

android 所有的 component（Services, Activity, Content Provider, Broadcast Receiver） 开启的时候都是在主线程上的，所以在 service 里要自己在background thread上完成。

Service 也有自己的 lifecycle:

1. `onCreate()`
2. `onStartCommand()` -> 开始你希望service处理的工作
3. Service Running -> start the AsyncTask here
4. service 结束后，我们自己调用 `stopSelf()`, 会触发 `onDestroy()`
5. `onDestroy()`

## [IntentService](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder/T10.01-Exercise-IntentServices)

IntentService 是一种单独在 background thread 上运行的 service。所有 intent service requests 都是在同一个background thread中处理的，并且是按顺序发出的, 适用于一些应该按顺序处理的任务。

```
public class MyIntentService extends IntentService {

    //  Create a default constructor that calls super with the name of this class
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	  // do whatever you want in the background
    	  ...
    }
}
```
```
Intent myIntentService = new Intent(this, MyIntentService);
startService(myIntentService);
```

[Example](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder) - 点击图片后，使用service增加水计数器，同时增加preference的水计数器。有简单的 preference 和 OnSharedPreferenceChangeListener 的操作。
这个例子用 IntentService 的原因：如果这个app上的数据会传到云上的话同步就很费时间，需要在background 操作。

点击图片水量+1，这个例子里有简单的 preference 和 OnSharedPreferenceChangeListener 的操作

![](show_intentService.gif)

## PendingIntents
让别的app像你的app一样，使用用你的app（经过permission的）中的操作例如: 使用 services, private activities, broadcast protected intents.

使用下面的static method，创建 PendingIntent instance:
`getActivity(Context, int, Intent, int)`, `getActivities(Context, int, Intent[], int)`, `getBroadcast(Context, int, Intent, int)`, and `getService(Context, int, Intent, int)`.

```
private static PendingIntent contentIntent(Context context) {
    // Create an intent that opens up the MainActivity
    Intent startActivityIntent = new Intent(context, MainActivity.class);
    return PendingIntent.getActivity(
    		context, 
    		PENDING_INTENT_ID, 
			startActivityIntent, 
			PendingIntent.FLAG_CANCEL_CURRENT);
    }
```

## Notification

[例子: Exercises_Java: Lesson10-Hydration-Reminder](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder)：

[**Notification 的简单实现**](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder/T10.02-Exercise-CreateNotification) - **按下按钮，在顶部弹出通知**，PendingIntent，NotificationManager，NotificationCompat.Builder-CreateNotification

[**Add Notification Actions**](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder/T10.03-Exercise-NotificationActions) - **在弹出的通知上增加2个action按钮**：1个是已经喝过水了（水量+1），1个是忽略所有通知。 NotificationCompat.Action， PendingIntent， notificationManager.cancelAll();-NotificationActions。
**这2个action的本质就是，每个一个 `Action` 就是用 `PendingIntent` 开始一个 `IntentService` (就是这 background thread 上做一些操作)**

## [JobScheduler 安排作业](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder/T10.04-Exercise-PeriodicSyncWithJobDispatcher)
* **JobScheduler** schedule the job(每隔15分钟，在wifi，充电条件下开始某个job)
* **FirebaseJobDispatcher** - 一个比 JobScheduler 更兼容（compatible）的版本
[视频讲解 简单使用](https://classroom.udacity.com/courses/ud851/lessons/f5ef4e52-c485-4c85-a26a-3231c17d6154/concepts/bd589300-dbc0-45aa-810d-c8e80f650175)

首先创建job:

```
public class WaterReminderFirebaseJobService extends JobService {
    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        // By default, jobs are executed on the main thread, so make an anonymous class extending
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                // do something in background thread
                ...
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                // call jobFinished. This will inform the JobManager that your job is done 
                // and that you do not want to reschedule the job.
                jobFinished(jobParameters, false); // false: wether or not your job will be reschedules.
            }
        };

        mBackgroundTask.execute();

        return true; // means our job is still doing some work.
    }

    // 当你的工作条件不满足时系统自动call。例如要求在wifi条件下下载一个大文件，在下载途中wifi停止了
    @Override
    public boolean onStopJob(JobParameters params) {
        if(mBackgroundTask != null) mBackgroundTask.cancel(true);

        // 当条件重新满足时，要不要重新开始job
        return true;
    }
}

```
```
implementation 'com.firebase:firebase-jobdispatcher:0.8.5'
```
```
<service android:name=".sync.WaterReminderFirebaseJobService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.firebase.jobdispatcher.ACTION_EXECUTE"/>
    </intent-filter>
</service>
```

然后 schedule job:

```
public class ReminderUtilities {

    private static final int REMINDER_INTERVAL_MINUTES = 15; // 间隔时间15 minutes
    private static final int REMINDER_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES); // 转换成秒
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS; // execution window, .setTrigger(Trigger.executionWindow(60, 120)) the job service would be execute somewhere between 60 seconds and 120 seconds after it's scheduled

    private static final String REMINDER_JOB_TAG = "hydration_reminder_tag"; // job unique tag
    private static boolean sInitialized;

    // Create a **synchronized**, public static method called scheduleChargingReminder that takes
    // in a context. This method will use FirebaseJobDispatcher to schedule a job that repeats roughly
    // every REMINDER_INTERVAL_SECONDS when the phone is charging. It will trigger WaterReminderFirebaseJobService
    // Checkout https://github.com/firebase/firebase-jobdispatcher-android for an example
    synchronized public static void scheduleChargingReminder(final Context context) {
        // If the job has already been initialized, return
        if(sInitialized) return;

        // Create a new GooglePlayDriver
        Driver driver = new GooglePlayDriver(context);

        // Create a new FirebaseJobDispatcher with the driver
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Use FirebaseJobDispatcher's newJobBuilder method to build a job which:
        // Finally, you should build the job.
        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(WaterReminderFirebaseJobService.class) // - has WaterReminderFirebaseJobService as it's service
                .setTag(REMINDER_JOB_TAG) // - has the tag REMINDER_JOB_TAG 
                .setConstraints(Constraint.DEVICE_CHARGING) // - only triggers if the device is charging
                .setLifetime(Lifetime.FOREVER) // - has the lifetime of the job as forever, even if the device restarts.
                .setRecurring(true) // - has the job recur
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS)) // - occurs every 15 minutes with a window of 15 minutes. You can do this using a setTrigger, passing in a Trigger.executionWindow. 等待至少15分钟，最多30分钟
                .setReplaceCurrent(true) // - replaces the current job if it's already running
                .build();

        // Use dispatcher's schedule method to schedule the job
        dispatcher.schedule(constraintReminderJob);

        // Set sInitialized to true to mark that we're done setting up the job
        sInitialized = true;
    }
}
```
MainActivity 的 `onCreate()` 中调用

```
ReminderUtilities.scheduleChargingReminder(this);
```

## Broadcast Receiver
Broadcast Receiver 示例: 

- android.intent.action.SCREEN_ON
- android.intent.action.MEDIA_MOUNTED
- android.intent.action.HEADSET_PLUG
- android.intent.action.BATTERY_LOW
- android.intent.action.DOWNLOAD_COMPLETE
- android.intent.action.AUDIO_BECOMING_NOISY

如果你的app需要知道手机的这些状态，就需要使用 Broadcast Receiver，Broadcast Receiver 是安卓的核心组件，使app可以接收**系统或别的app**发出的 broadcast。即使app没有运行可以出发 Broadcast Receiver。例如你的app要根据设备是否在线来更新UI，当状态变动时，Broadcast Receiver 就会被触发。使用 `IntentFilter` 来指定你需要的request。`IntentFilter` 是用来指定什么 intent 会触发你的 component，通常在`AndroidManifest.xml`。

```
<activity
    android:name=".MainActivity"
    android:launchMode="singleTop"
    android:screenOrientation="portrait">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>
</activity>
```
上面的 Intent Filter 使用了 action.Main 和 category.LAUNCHER, 说明这是你的app的 main entry. 并且当 launcher 开始一个 intent 来launch这个app时，这个 activity 会被触发。

create Broadcast Receiver 的2中方式

- Static: 每当有 broadcast 的时候，Static Broadcast Receiver 都会被触发，**即使 app 没有在使用**。
- Dynamic: 与 app 的 lifecycle 相关联，app 不使用的时候就不开启，尽量使用 Dynamic Broadcast Receiver + Job Scheduling

#### Static Broadcast Receiver 的创建方式（强烈不建议使用）
```
<reveiver android:name=".NewPictureBroadcastReceiver">
    <intent-filter>
        <action android:name="com.action.camera.NEW_PICTURE"/>
        <action android:name="android.hardware.action.NEW_PICTURE"/>
        <data android:mimeType="image/*" />
    </intent-filter>
</reveiver>
```
```
public class NewPictureBroadcastReceiver extends BroadcastReceiver{
	@override
	public void onReceive(Context context, Intent intent){
		// do something
	}
}
```

试想如果你有10个app都有上面这个 Static Broadcast Receiver，每当你拍个照片，这10个app都会被触发！手机会卡死吧。。。

#### [Dynamic Broadcast Receiver](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder/T10.05-Exercise-ChargingBroadcastReceiver)
```
public class ChargingBroadcastReceiver extends BroadcastReceiver {
    // Override onReceive to get the action from the intent and see if it matches the
    // Intent.ACTION_POWER_CONNECTED. If it matches, it's charging. If it doesn't match, it's not charging.
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean isCharging = action.equals(Intent.ACTION_POWER_CONNECTED);
        
        // Update the UI using the showCharging method you wrote
        ...
    }
}
```

MainActivity.java 中

```
ChargingBroadcastReceiver mChargingReceiver;
IntentFilter mChargingIntentFilter;

@Override
protected void onCreate(Bundle savedInstanceState) {
    ...

    // Create and instantiate a new instance variable for your ChargingBroadcastReceiver
    // and an IntentFilter
    mChargingIntentFilter = new IntentFilter();
    mChargingReceiver = new ChargingBroadcastReceiver();

    // Call the addAction method on your intent filter and add Intent.ACTION_POWER_CONNECTED
    // and Intent.ACTION_POWER_DISCONNECTED. This sets up an intent filter which will trigger
    // when the charging state changes.
    mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
    mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
}

// etup your broadcast receiver
@Override
protected void onResume() {
    super.onResume();
    registerReceiver(mChargingReceiver, mChargingIntentFilter);
}

// unregister your receiver 
@Override
protected void onPause() {
    super.onPause();
    unregisterReceiver(mChargingReceiver);
}
```
[**Charging Broadcast Receiver**](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder/T10.05-Exercise-ChargingBroadcastReceiver) - Dynamic Broadcast receiver: 设备在充电是插头显示红色，否则就是灰色. **这个代码有bug：我们的应用分别在 onResume 和 onPause 中添加及删除动态广播接收器。当应用不可见时，插头的图标将不更新。这样的话，有时候当应用启动时，插头的图标可能不对。**



[**Sticky Broadcast For Charging**](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson10-Hydration-Reminder/T10.06-Exercise-StickyBroadcastForCharging) - 修复之前代码的 bug。就是在`onResume`中查看现在是否在充电，然后更新UI。[udacity 图文教程](https://classroom.udacity.com/courses/ud851/lessons/f5ef4e52-c485-4c85-a26a-3231c17d6154/concepts/8c074bea-0ad1-46aa-ae51-b41b8e1943d3)







