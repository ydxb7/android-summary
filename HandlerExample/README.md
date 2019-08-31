# *Handler, Thread, HandlerThread*

## Thread
### Create Thread
There are two ways to create a new thread of execution. One is to
declare a class to be a subclass of <code>Thread</code>. This
subclass should override the <code>run</code> method of class
<code>Thread</code>. An instance of the subclass can then be
allocated and started. For example, a thread that computes primes
larger than a stated value could be written as follows:

<blockquote><pre>
    class PrimeThread extends Thread {
        long minPrime;
        PrimeThread(long minPrime) {
            this.minPrime = minPrime;
        }
        public void run() {
            // compute primes larger than minPrime
            &nbsp;.&nbsp;.&nbsp;.
        }
    }
</pre></blockquote>
<p>
The following code would then create a thread and start it running:
<blockquote><pre>
    PrimeThread p = new PrimeThread(143);
    p.start();
</pre></blockquote>
<p><hr>
The other way to create a thread is to declare a class that
implements the <code>Runnable</code> interface. That class then
implements the <code>run</code> method. An instance of the class can
then be allocated, passed as an argument when creating
<code>Thread</code>, and started. The same example in this other
style looks like the following:
<blockquote><pre>
    class PrimeRun implements Runnable {
        long minPrime;
        PrimeRun(long minPrime) {
            this.minPrime = minPrime;
        }
        public void run() {
            // compute primes larger than minPrime
            &nbsp;.&nbsp;.&nbsp;.
        }
    }
</pre></blockquote>
<p>
The following code would then create a thread and start it running:
<blockquote><pre>
    PrimeRun p = new PrimeRun(143);
    new Thread(p).start();
</pre></blockquote>

### Lifecycle
![lifecycle](lifecycle.png)

### wait()、notify()、notifyAll()
wait、notify、notifyAll都必须在synchronized中执行，否则会抛出异常。android 官方不推荐使用。
wait: 进入阻塞状态，释放锁（其它线程可以进入synchronized方法体或方法块，释放锁不需要try/catch）. Causes the current thread to wait until another thread invokes the notify() method or the notifyAll() method for this object. 

Java多线程面试题：子线程循环10次，接着主线程循环15次，接着又回到子线程循环10次，接着再回到主线程又循环15次，如此循环50次

```
private static Object lock = new Object();

public static void main(String[] args) {
    //子线程
    new Thread() {
        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                synchronized (lock){
                    for (int j = 0; j < 10; j++) {
                        System.out.println("i = " + i + "子循环循环第" + (j + 1) + "次");
                    }
                    //唤醒【等待唤醒队列】的第一个线程
                    lock.notify();
                    //等待
                    try {
                        // 代表子线程等待，让出使用权让主线程执行，
                        // 这个时候子线程等待这一事件会被加进到【等待唤醒队列】中。
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }.start();

    //主线程
    for (int i = 0; i < 50; i++) {
        synchronized (lock){
            try {
                // 代表主线程等待，让出使用权让子线程执行，
                // 这个时候主线程等待这一事件会被加进到【等待唤醒队列】中。
                lock.wait(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < 15; j++) {
                System.out.println("i = " + i + "主循环循环第" + (j + 1) + "次");
            }
            //唤醒【等待唤醒队列】的第一个线程
            lock.notify();
        }
    }
}
```
### sleep()、join()、yield()
#### 1. sleep()
线程休眠，进入阻塞状态，sleep方法不会释放锁（其它线程不会进入synchronized方法体或方法块，不释放锁需要try/catch）

```
try {
    Thread.sleep(2000);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

#### 2. join()
线程插队，join()作用是让指定的线程先执行完再执行其他线程，而且会阻塞主线程，它的使用也很简单

```
public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //启动线程一
        try {
            MyThread myThread1 = new MyThread("线程一");
            myThread1.start();
            myThread1.join(); // 会block主线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("主线程需要等待");

        //启动线程二
        try {
            MyThread myThread2 = new MyThread("线程二");
            myThread2.start();
            myThread2.join(); // 会block主线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("主线程继续执行");
    }

    class MyThread extends Thread {

        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(getName() + "在运行");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

#### 3. yield()
yield()的作用是指定线程先让别的线程的先执行，就好比公交车只有一个座位，谁礼让了谁就坐上去。特别注意的是：yield()会礼让给相同优先级的或者是优先级更高的线程执行，不过yield()这个方法只是把线程的执行状态打回准备就绪状态，所以执行了该方法后，有可能马上又开始运行，有可能等待很长时间。
线程交出CPU，但是不会阻塞而是重置为就绪状态，**不会释放锁**

```
public static void main(String[] args) {
    MyThread myThread1 = new MyThread("线程一");
    MyThread myThread2 = new MyThread("线程二");

    myThread1.start();
    myThread2.start();
}

static class MyThread extends Thread {

    public MyThread(String name) {
        super(name);
    }

    @Override
    public synchronized void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(getName() + "在运行，i的值为：" + i + " 优先级为：" + getPriority());
            if (i == 2) {
                System.out.println(getName() + "yield");
                Thread.yield();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
