package ai.tomorrow.drawclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Paint paint;
    private double second = 0;
    private double minus = 0;
    private double hour = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomView customView = new CustomView(this);
        setContentView(customView);
        //获取系统时间
        minus = Integer.parseInt(DateFormat.format("mm", getDate().getTime()).toString());
        hour = Integer.parseInt(DateFormat.format("hh", getDate().getTime()).toString()) * 5;
        second = Integer.parseInt(DateFormat.format("ss", getDate().getTime()).toString()) - 12;
    }

    private Date getDate() {
        return new Date(System.currentTimeMillis());
    }

    class CustomView extends View {
        public CustomView(Context context) {
            super(context);
            //new 一个画笔
            paint = new Paint();
            //设置画笔颜色
            paint.setColor(Color.YELLOW);
            //设置结合处的样子,Miter:结合处为锐角, Round:结合处为圆弧:BEVEL:结合处为直线。
            paint.setStrokeJoin(Paint.Join.ROUND);
            //设置画笔笔刷类型 如影响画笔但始末端
            paint.setStrokeCap(Paint.Cap.ROUND);
            //设置画笔宽度
            paint.setStrokeWidth(3);

        }

        @Override
        public void draw(Canvas canvas) {
            double startTime = System.currentTimeMillis();   //获取开始时间
            super.draw(canvas);
            //设置屏幕颜色，也可以利用来清屏。
            canvas.drawColor(Color.rgb(122, 65, 255));
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2); //将画布移动到屏幕中心
            canvas.drawCircle(0, 0, 100, paint); //画圆圈
            Paint tmpPaint = new Paint(paint); //小刻度画笔对象
            tmpPaint.setStrokeWidth(1);//设置画笔笔尖的粗细
            float y = 100;   //向Y方向移动画笔的位置
            int count = 60; //总刻度数
            //时针把360度分为12份
            //分针把360度分为12*5份
            //秒针把360度分为12*5份
            //把360分为60份，每6度一个刻度，始终它们都是一个一个刻度走的
            canvas.rotate(180 + 360 / 12, 0f, 0f); //旋转画纸，使1到12的刻度按钟表习惯写在上面。
            for (int i = 0; i < count; i++) {
                if (i % 5 == 0) {
                    //60份里面5的倍数就是1-12，所以比其他刻度画得长一点，y加减控制刻度长度
                    canvas.drawLine(0f, y, 0, y + 12f, paint);
                    //把1-12数字写在钟表相应位置上
                    canvas.drawText(String.valueOf(i / 5 + 1), -4f, y + 25f, tmpPaint);
                } else {
                    canvas.drawLine(0f, y, 0f, y + 5f, tmpPaint);
                }
                //每一个循环就旋转一个刻度，可以想象一下就是笔不动，下面的纸旋转，那么下一次画的位置就发生改变了
                canvas.rotate(360 / count, 0f, 0f); //旋转画纸
            }
            canvas.save();//保存之前的状态，是下次第二个canvas.restore()返回点
            canvas.save();//各个状态最初，是下次第一个canvas.restore()返回点
            //绘制钟表的中心点
            tmpPaint.setColor(Color.GRAY);
            //设置画笔宽度
            tmpPaint.setStrokeWidth(4);
            canvas.drawCircle(0, 0, 7, tmpPaint);
            tmpPaint.setStyle(Paint.Style.FILL);
            tmpPaint.setColor(Color.YELLOW);
            canvas.drawCircle(0, 0, 5, tmpPaint);

            tmpPaint.setColor(Color.RED);
            //设置画笔宽度
            tmpPaint.setStrokeWidth(6);
            canvas.rotate(-30, 0f, 0f); //调整时针
            //绘制时针
            canvas.rotate((float) ((360 / 12 / 5 * (hour + minus / 12.0)) % 360), 0f, 0f);
            canvas.drawLine(0, -10, 0, 45, tmpPaint);

            canvas.restore();//第一个canvas.restore()回到初始状态,使得分针不受时针影响
            canvas.rotate(25 * 360 / 12 / 5, 0f, 0f); //调整分针
            tmpPaint.setColor(Color.GREEN);
            //设置画笔宽度
            tmpPaint.setStrokeWidth(3);
            //绘制分针
            canvas.rotate((float) ((360 / 12 / 5 * minus) % 360), 0f, 0f);
            canvas.drawLine(0, 10, 0, -65, tmpPaint);

            canvas.restore();//第二个canvas.restore()回到初始状态,使得秒针不受分针，时针影响
            canvas.rotate(-30, 0f, 0f); //调整秒针
            //绘制秒针
            tmpPaint.setColor(Color.BLUE);
            //设置画笔宽度
            tmpPaint.setStrokeWidth(1);
            canvas.rotate((float) ((360 / 12 / 5 * second) % 360), 0f, 0f); //旋转画纸,没秒旋转360/12/5度
            canvas.drawLine(0, -10, 0, 85, tmpPaint);
            canvas.rotate(360 / 12 / 5);
            //秒针转60次，那么分针转1次，当分针转了60次，那么时针转1次
            if (second == 60) {
                second = second % 60;
                minus++;
                if (minus == 60) {
                    minus = minus % 60;
                    hour += 5;
                    if (hour == 60)
                        hour = hour % 60;
                }
            }
            second++;
            //设置这个是为了避免上面一系列的计算的时间影响
            double endTime = System.currentTimeMillis(); //获取结束时间
            //每隔1秒钟刷新页面
            postInvalidateDelayed((long) (1000 - (endTime - startTime)));
        }
    }
}
