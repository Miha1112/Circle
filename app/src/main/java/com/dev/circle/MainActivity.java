package com.dev.circle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawTZ(this));
    }
    public class DrawTZ extends  View{
        private float radius = 70;
        private float x = 0;
        private  float y = 0;
        private float prev_x = 0;
        private float prev_y = 0;
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
            maxWidth = display.getWidth();
            maxHeight = display.getHeight();
//            System.out.println("max width and max height:  " + maxWidth + " " + maxHeight);
            angle = Math.random() * 360;
            //get random start positions;
            float[] start_pos = getSpawn();
//            float[] start_pos = {(float) (maxWidth/2), (float) (maxHeight/2)};
            x = start_pos[0];
            y = start_pos[1];
            Timer timer = new Timer();
            int time = 120;

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    updatePosition();
//                    System.out.println("x and y for drawing: " + x + " " + y + " angle: " + angle);
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
            if (distance <=70){
                return true;
            }else return  false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float mouse_x = event.getX();
            float mouse_y = event.getY();
            PointF mouse = new PointF(mouse_x,mouse_y);
            PointF circle = new PointF(x,y);
            if (checkDistance(mouse,circle)){
                total_score++;
                respawnCircle();
            }
            postInvalidate();
            return super.onTouchEvent(event);
        }

        private void updatePosition(){
            boolean resetCoord = false;
            double[] newPos = setMoveVector(x,y,angle);
            prev_x = x;
            prev_y = y;
            x = (float) newPos[0];
            y = (float) newPos[1];
            if (x+radius >= maxWidth - 40 || x - radius <= 40){
                angle = 180 - angle;
                angle = angle % 360;
//                System.out.println("first if distance ");
                resetCoord = true;
                newPos = setMoveVector(x,y,angle);
            }
            if (y+radius >= maxHeight - 40 || y-radius <= 40){
                angle = 360 - angle;
                angle = angle % 360;
//                System.out.println("stuknulo ob verh ili niz");
                resetCoord = true;
                newPos = setMoveVector(x,y,angle);
            }
            if (resetCoord){
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
//                canvas.drawCircle(x, y, 10, paint2);
            }
            canvas.drawText(Integer.toString(total_score),50,50,paint2);
        }

        private double[] setMoveVector(double x, double y, double angle){
            int speed = 50;
            int time = 1;
            double new_x =Math.round(x + speed * Math.cos((angle * 3.14)/180));
            double new_y =Math.round(y + speed * Math.sin((angle*3.14)/180));
//            System.out.println("return x: " + new_x + " and y: "  + new_y + " this coords set for angle: "+ angle);
//            System.out.println("cos for angle: " + Math.cos((angle * 3.14)/180) + " sin: " + Math.sin((angle*3.14)/180));
            return new double[]{new_x,new_y};
        }

    }
}