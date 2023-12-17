package com.dev.circle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(new DrawTZ(this));
    }
    public class DrawTZ extends  View{
        private float radius = 70;
        private float x = 0;
        private  float y = 0;
        private double maxWidth;
        private double maxHeight;
        private double angle;
        private boolean isAlive = true;
        private int total_score = 0;

        Paint paint = new Paint();
        Paint paint2 = new Paint();

        public DrawTZ(Context context) {
            super(context);
            paint.setColor(Color.GREEN);
            paint2.setColor(Color.RED);
            paint2.setTextSize(35.0f);
            init();
        }
        private void init(){
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            maxWidth = size.x;
            maxHeight = size.y;
            angle = Math.random() * 360;
            //get random start positions;
            float[] start_pos = getSpawn();
            x = start_pos[0];
            y = start_pos[1];
            Timer timer = new Timer();
            int time = 1;

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    updatePosition();
                    postInvalidate();
                }
            };
            timer.scheduleAtFixedRate(timerTask,0,time);
        }

        private void respawnCircle(){
            isAlive = false;
            CountDownTimer timerRespawn = new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    float[] start_pos = getSpawn();
                    x = start_pos[0];
                    y = start_pos[1];
                    angle = Math.random() * 180;
                    postInvalidate();
                    isAlive=true;

                }
            };
            timerRespawn.start();
        }
        private boolean checkDistance(PointF a,PointF b){
            float dx = a.x - b.x;
            float dy = a.y - b.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < radius){
                return true;
            }else return  false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float mouse_x = event.getX();
            float mouse_y = event.getY();
            PointF mouse = new PointF(mouse_x,mouse_y);
            PointF circle = new PointF(x,y);
            if (checkDistance(mouse,circle) && isAlive){
                total_score++;
                respawnCircle();
            }
            postInvalidate();
            return super.onTouchEvent(event);
        }

        private void updatePosition() {
            double[] newPos = setMoveVector(x, y, angle);
            x = (float) newPos[0];
            y = (float) newPos[1];

            // Замінено умови зіткнення з межами екрана
            if (x < radius - 15  || x > maxWidth - radius + 15) {
                angle = 180 - angle;
                angle %= 360;
                newPos = setMoveVector(x, y, angle);
                x = (float) newPos[0];
                y = (float) newPos[1];
            }

            if (y < radius - 15 || y > maxHeight - radius + 40) {
                angle = 360 - angle;
                angle %= 360;
                newPos = setMoveVector(x, y, angle);
                x = (float) newPos[0];
                y = (float) newPos[1];
            }
        }
        private float[] getSpawn(){
            float x_ = (float) (Math.random() * (maxWidth - 550)) + 200;
            float y_ = (float) (Math.random() * (maxHeight - 550)) + 200;
            return new float[]{x_,y_};
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);
            if (isAlive) {
                canvas.drawCircle(x, y, radius, paint);
            }
            canvas.drawText(Integer.toString(total_score),50,50,paint2);
        }

        private double[] setMoveVector(double x, double y, double angle) {
            int speed = 1;
            double new_x = x + speed * Math.cos((angle * Math.PI) / 180);
            double new_y = y + speed * Math.sin((angle * Math.PI) / 180);
            return new double[]{new_x, new_y};
        }

    }
}