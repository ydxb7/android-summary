# AsyncTask and AsyncTaskLoader
## AsyncTask

![image](show_asyncTask.gif)

AsyncTask is designed to be a helper class around `Thread` and `Handler` and does not constitute a generic threading framework. AsyncTasks should ideally be used for **short operations** (a few seconds at the most.) If you need to keep threads running for long periods of time, it is highly recommended you use the various APIs provided by the java.util.concurrent package such as `Executor`, `ThreadPoolExecutor` and `FutureTask`.

An asynchronous task is defined by a computation that runs on a background thread and whose result is published on the UI thread. An asynchronous task is defined by 3 generic types, called `Params`, `Progress` and `Result`, and 4 steps, called `onPreExecute`, `doInBackground`, `onProgressUpdate` and `onPostExecute`.

Example of subclassing:

```
 private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
     protected Long doInBackground(URL... urls) {
         int count = urls.length;
         long totalSize = 0;
         for (int i = 0; i < count; i++) {
             totalSize += Downloader.downloadFile(urls[i]);
             publishProgress((int) ((i / (float) count) * 100));
             // Escape early if cancel() is called
             if (isCancelled()) break;
         }
         return totalSize;
     }

     protected void onProgressUpdate(Integer... progress) {
         setProgressPercent(progress[0]);
     }

     protected void onPostExecute(Long result) {
         showDialog("Downloaded " + result + " bytes");
     }
 }
```

Once created, a task is executed very simply:

```
 new DownloadFilesTask().execute(url1, url2, url3);
```

**`onProgressUpdate(Progress...)`**, invoked on the UI thread after a call to `publishProgress(Progress...)`. The timing of the execution is undefined. This method is used to display any form of progress in the user interface while the background computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text field.

### Cancelling a task
A task can be cancelled at any time by invoking `cancel(boolean)`. Invoking this method will cause subsequent calls to `isCancelled()` to return true. After invoking this method, `onCancelled(java.lang.Object)`, instead of `onPostExecute(java.lang.Object)` will be invoked after `doInBackground(java.lang.Object[])` returns. **To ensure that a task is cancelled as quickly as possible, you should always check the return value of `isCancelled()` periodically from `doInBackground(java.lang.Object[])`, if possible (inside a loop for instance.)**

## AsyncTaskLoader

**AsyncTask 缺点**: 正在下载的时候旋转屏幕，得到的result会传递给一个已经不存在的activity。使用component LoaderManager的Loader，他们的生命超过activity的生命周期来避免重复加载。如果我们需要在background thread load data，使用AsyncTaskLoader and prevent the reload after the user back into the App.</br>

[Example](https://github.com/ydxb7/Exercises_Java/tree/master/Lesson05b-Smarter-GitHub-Repo-Search)

1. **SaveResults** - 旋转设备后保存搜索结果
2. **AsyncTaskLoader 的使用** - 把AsyncTask改成`AsyncTaskLoader`，只处理旋转的加载问题，但是如果用户离开再回来就会重新加载，用3来处理这个问题。使用 `AsyncTaskLoader` 后就不需要用 onSaveInstanceState 记录的结果
3. **PolishAsyncTask** - 缓存加载器结果，防止客户离开应用后出现查询