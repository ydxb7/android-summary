## synchronized

#### 1. 对象锁
```
public class SynchronizedDemo {
    //同步方法，对象锁
    public synchronized void syncMethod() {
        
    }

    //同步块，对象锁
    public void syncThis() {
        synchronized (this) {
           
        }
    }
}
```
#### 2. 类锁
```
public class SynchronizedDemo {
    //同步class对象，类锁
    public void syncClassMethod() {
        synchronized (SynchronizedDemo.class) {
            
        }
    }

    //同步静态方法，类锁
    public static synchronized void syncStaticMethod(){

    }
}
```
## ReentrantLock
使用ReentrantLock很好理解，就好比我们现实的锁头是一样道理的。使用ReentrantLock的一般组合是lock与unlock成对出现的，需要注意的是，**千万不要忘记调用unlock来释放锁**，否则可能会引发死锁等问题。如果忘记了在finally块中释放锁，可能会在程序中留下一个定时炸弹，随时都会炸了，而是用synchronized，JVM将确保锁会获得自动释放，这也是为什么Lock没有完全替代掉synchronized的原因.

```
public class RenntrantLockActivity extends AppCompatActivity {

    Lock lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renntrant_lock);

        lock = new ReentrantLock();
        doSth();
    }

    public void doSth() {
        lock.lock();
        try {
            //这里执行线程同步操作

        } finally {
            lock.unlock();
        }
    }
}
```







