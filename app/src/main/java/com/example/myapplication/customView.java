package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.content.Context.MODE_PRIVATE;

public class customView extends View
{
    public Integer flagSliderMove=0, flagBall=0, flagSlider=0, flagBallFirst=0, flagHitSlider=0;
    public Integer flagHitWall=0, flagHitTop=0, flagB=-1, mode, level, flagFirstTopHit=0;
    public Canvas mcanvas;
    public float xSlider=0, ySlider=0, xcSlider=0, ycSlider=0;
    public Integer canvasSide = 1, yDelta=15, xDelta=15, score=0, cScore=0, gameEnd=0;
    public float xRandomStart, yRandom;
    public Float xCanvas, yCanvas;
    private slider sliderObj;
    private cSlider cSliderObj;
    private ball ballObj;
    public Float xRunStart, yRunStart, ballSlope;
    public Boolean updateView=true;
    private SoundPool soundPool;
    private RectF endRect;

    private int gameOver, sliderHit, wallHit;
    public Integer interval;

    private class UpdateViewRunnable implements Runnable {
        public void run()
        {
            flagBall=1;
            movePong();
            if(updateView) {
                postDelayed(this, interval);
            }
        }
    }

    private UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
    public customView(Context context)
    {
        super(context);
        soundEnable(context);
        init(null);
    }

    public customView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public customView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public customView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateView = true;
        postDelayed(updateViewRunnable, interval);
    }

    @Override
    protected void onDetachedFromWindow() {
        updateView = false;
        super.onDetachedFromWindow();
    }

    private void init(@Nullable AttributeSet set)
    {
        getProperty();
    }


    //Enable sound when hit on wall or slider or when game is over
    public void soundEnable(Context context){
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes
                    audioAttributes
                    = new AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool= new SoundPool
                    .Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            soundPool= new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        }



        //beep sound for hit and game over
        gameOver= soundPool.load(context, R.raw.beep2,1);
        wallHit= soundPool.load(context, R.raw.beep4,1);
        sliderHit= soundPool.load(context, R.raw.beep4,1);


    }

    //play different sound based on the action
    public void playSound(int i)
    {
        switch (i) {

            case 1:
                soundPool.play(gameOver, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundPool.play(wallHit, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundPool.play(sliderHit, 1, 1, 0, 0, 1);
                break;
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        mcanvas = canvas;
        mcanvas.drawColor(getResources().getColor(R.color.backgnd));
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.foregnd));


            if (sliderObj == null) {
                setDefault();
                sliderObj = new slider((float) mcanvas.getHeight());
                if (mode == 2)
                    cSliderObj = new cSlider((float) mcanvas.getHeight());
                ballObj = new ball();
            }

            mcanvas.drawCircle(xRunStart, yRunStart, ballObj.radius, paint);
            //Log.d("debug", String.valueOf(xRunStart) + "," +String.valueOf(yRunStart) + " canvas " +String.valueOf(canvasSide) );
            // Draw the mScore
            paint.setColor(getResources().getColor(R.color.txtClr));
            paint.setTextSize(40); //Refresh score

            if (mode == 2) {
                canvas.save();
                canvas.rotate(90f, 50, yCanvas / 6);
                mcanvas.drawText("AI - " + String.valueOf(cScore) + " ,    player - " + String.valueOf(score), 50, yCanvas / 6, paint);
                // mcanvas.drawText( String.valueOf(cScore), 50, 50, paint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.rotate(90f, 50, yCanvas / 2 - 300);
                mcanvas.drawText("Score   " + String.valueOf(score), 50, yCanvas / 2 - 300, paint);
                //mcanvas.drawText(  50, yCanvas/2+50, paint);
                canvas.restore();
            }

            paint.setColor(getResources().getColor(R.color.sliderclr));
            mcanvas.drawRect(sliderObj.slider, paint);

            if (mode == 2)
                mcanvas.drawRect(cSliderObj.cSlider, paint);

            paint.setColor(getResources().getColor(R.color.watermark));
            mcanvas.drawRect(0, (yCanvas / 2) - 2, xCanvas, (yCanvas / 2) + 2, paint);
            mcanvas.drawCircle(xCanvas / 2, yCanvas / 2, (float) (ballObj.radius * 1.3), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(20f);

            mcanvas.drawCircle(xCanvas / 2, yCanvas / 2, 150, paint);
            if (mode == 2)
                cSliderObj.setcSliderPos();
            flagBall = 0;

            debugCanvas();



    }

    //stores the highest score in shared preferences to show in main screen
    //This function is called when the balls misses
    private void storeScore() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore", "0"));

        if (score > highScore) {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("highScore", String.valueOf(score));
            myEdit.commit();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        flagBall = 1;
        xSlider= event.getX();
        ySlider = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (sliderObj.slider.contains(xSlider, ySlider)) {
                    flagSliderMove = 1;
                    //Log.d("test","down");
                    sliderObj.setSliderPos(xSlider);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (flagSliderMove == 1) {
                    sliderObj.setSliderPos(xSlider);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                flagSliderMove = 0;
               // Log.d("test","up");
                break;
            }
        }
        if(flagSliderMove==1)
        {
            postInvalidate();
        }
        return true;
    }

    private class ball
    {
        Float x;
        Float y;
        Integer radius;

        public ball()
        {
            this.radius = 20;
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            mcanvas.drawCircle(xCanvas/2, yCanvas/2, this.radius, paint);
        }

        public void moveBallStart()
        {
            if(((yRunStart+ballObj.radius)>=sliderObj.height)&&(flagBallFirst==0))
            {
                yRunStart=yRunStart-yDelta;
                xRunStart = xRunStart+(canvasSide*ballSlope*xDelta);
            }
            else {
                if(flagBallFirst==2)
                {
                    flagFirstTopHit=1;
                }
                xRunStart = xRunStart+(canvasSide*ballSlope*yDelta);
                yRunStart = yRunStart+xDelta;
                flagBallFirst = 2;

                if (mode==2)
                    if(flagFirstTopHit==0)
                        if (cSliderObj.cSlider.contains(xRunStart + ballObj.radius, yRunStart - ballObj.radius))
                        {
                            cScore+=2;
                        }
                        else
                           score+=2;
            }
        }
        public void ballHit() {
            xRunStart = xRunStart + xDelta * ballSlope;
            yRunStart = yRunStart + yDelta;
            //Log.d("test","inside hit");
        }
    }

    private class cSlider
    {
        Integer width;
        Integer height;
        RectF cSlider;

        public cSlider(Float canvasHeight)
        {
            this.width = 200;
            this.height = 50;

            cSlider = new RectF();
            cSlider.top = 0;
            cSlider.left = xCanvas/2-width/2;
            cSlider.bottom = height;
            cSlider.right = cSlider.left+width;

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            mcanvas.drawRect(cSlider, paint);
        }

        public void setcSliderPos()
        {
            if((flagFirstTopHit==0)&&(mode==1))
            {
                xcSlider= (float) (xDelta*0.2)*canvasSide;
            }
            else
            {    xcSlider = (float) (xDelta);  }

            if((cSlider.right>=xCanvas)&&(xcSlider>0.0)) {
                Log.d("Right", String.valueOf(cSlider.right) + "," +String.valueOf(xDelta));
            }
            else if((cSlider.left<=0.0)&&(xcSlider<0.0)){
                Log.d("Left", String.valueOf(cSlider.left) + "," +String.valueOf(xDelta));
            }


            else {
                if((mode==2)&&(level==3))
                {
                    cSlider.left = xRunStart-(cSliderObj.width/2);
                    cSlider.right = xRunStart+(cSliderObj.width/2);
                }
                else {
                    Log.d("else", String.valueOf(cSlider.left) + "," + String.valueOf(xcSlider) + "," + String.valueOf((cSlider.left <= 0.0) && (xDelta < 0.0)));
                    cSlider.left += xcSlider;
                    cSlider.right += xcSlider;
                }
            }
        }

    }

    private class slider
    {
        RectF slider;
        Integer width;
        Integer height;
        public slider(Float canvasHeight)
        {
            this.width = 200;
            this.height = 50;
            slider = new RectF();
            slider.top = canvasHeight-height;
            slider.left = 0;
            slider.bottom = canvasHeight;
            slider.right = width;

            Paint paint = new Paint();
            paint.setColor(Color.MAGENTA);
            mcanvas.drawRect(slider, paint);
        }

        public void setSliderPos(Float xSlider)
        {
            if((xSlider+(width/2))>xCanvas)
            {
                slider.left = xCanvas-width;
                slider.right = xCanvas;
            }
            else if((xSlider-(width/2))<0)
            {
                slider.left = 0;
                slider.right = width;
            }
            else {
                slider.left = xSlider - (width/2);
                slider.right = xSlider + (width/2);
            }
        }
    }

    public void movePong() {
       // Log.d("first", String.valueOf(yRunStart) + "," +String.valueOf(flagBallFirst));
        if (flagBall == 1) {
            if ((yRunStart + ballObj.radius >= (yCanvas - sliderObj.height)) && (flagHitSlider == 0)) {
                if ((sliderObj.slider.contains(xRunStart+ballObj.radius, yRunStart+ ballObj.radius))||(sliderObj.slider.contains(xRunStart-ballObj.radius, yRunStart+ ballObj.radius))) {
                    yDelta=(-1)*yDelta;
                    playSound(3);
                    ballHitRandom();
                    flagBallFirst=1;
                    flagHitWall = 0;
                    flagHitSlider = 1;
                    score+=2;
                    if((level==3)&&(score%4==0)) {
                        if((interval-1)>=5)
                        {interval -= 1;}
                    }
                   // Log.d("debug","slider");
                    //Log.d("debug", String.valueOf(xRunStart) + "," +String.valueOf(yRunStart) + " delta" +String.valueOf(xDelta) + "," + String.valueOf(yDelta)  );

                } else {

                    playSound(3);
                    gameEnd=1;

                    storeScore();

                    updateView = false;
                    Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
                    intentCanva.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intentCanva);
                }
            }

            if ((((xRunStart - ballObj.radius) <= 0) || ((xRunStart + ballObj.radius) >= xCanvas))&& (flagHitWall == 0)) {
                flagHitWall = 1;
                xDelta = (-1)*xDelta;
               // Log.d("debug","side wall");
              //  Log.d("debug", String.valueOf(xRunStart) + "," +String.valueOf(yRunStart) + " delta" +String.valueOf(xDelta) + "," + String.valueOf(yDelta)  );

                flagBallFirst=1;
                flagHitTop = 0;
                flagHitSlider = 0;
                playSound(2);
            }

            Log.d("mode", String.valueOf(mode));
            if (mode==2) {
                if (((yRunStart - (ballObj.radius)) <= cSliderObj.height) && (flagBallFirst == 1)) {
                    flagHitTop = 1;
                    if (cSliderObj.cSlider.contains(xRunStart + ballObj.radius, yRunStart - ballObj.radius)) {
                        cScore += 2;
                    } else {
                        score += 5;
                        playSound(3);
                    }
                    yDelta = (-1) * yDelta;
                    flagHitWall = 0;
                    flagHitSlider = 0;
                    playSound(2);
                    //Log.d("debug","Top wall");
                    //Log.d("debug", String.valueOf(xRunStart) + "," +String.valueOf(yRunStart) + " delta" +String.valueOf(xDelta) + "," + String.valueOf(yDelta)  );
                }
            }
            else
             {
                     Log.d("debug", "inside 1");

                     if ((yRunStart - ballObj.radius) <= 0) {
                         Log.d("debug", "inside 2");

                         if (flagBallFirst == 1) {
                             Log.d("debug", "inside 3");
                             flagHitTop = 1;
                             yDelta = (-1) * yDelta;
                             flagHitWall = 0;
                             flagHitSlider = 0;
                             playSound(2);
                         }
                     }
                 }


            if(flagBallFirst==0) {
                ballObj.moveBallStart();
            }
            else {
                ballObj.ballHit();
            }
        }
        flagHitWall=0;
        invalidate();
    }


    public void ballHitRandom()
    {
        Random random = new Random();
        yRandom = random.nextInt((int) ((yCanvas*0.8) + 1)-20);
        ballSlope = (xCanvas-xRunStart)/(yCanvas-yRandom);
        //ballSlope =
    }

    public class scaleFactor
    {
        public Float x;
        public Float y;

        public scaleFactor(Float x, Float y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public void setDefault()
    {
        xCanvas = Float.valueOf(mcanvas.getWidth());
        yCanvas = Float.valueOf(mcanvas.getHeight());
        mcanvas.drawColor(getResources().getColor(R.color.backgnd));

        xRunStart = xCanvas/2;
        yRunStart = yCanvas/2;
        Random random = new Random();
        xRandomStart = random.nextInt((int) (xCanvas*0.9+1)-10);
        if(xRandomStart<xRunStart)
        {
            xRandomStart = xRunStart-xRandomStart;
            canvasSide=-1;
        }
        else
        {
            xRandomStart = xRandomStart-xRunStart;
            canvasSide=1;
        }
        ballSlope = (xRandomStart)/yRunStart;
    }

    public void debugCanvas()
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        //mcanvas.drawText(String.valueOf(yRandom), 200, 200, paint);
        //mcanvas.drawText(String.valueOf(yCanvas), 100, 300, paint);
        //Log.d("debug", String.valueOf(xRunStart) + "," +String.valueOf(yRunStart) + " delta" +String.valueOf(xDelta) + "," + String.valueOf(yDelta)  );

        //mcanvas.drawText(String.valueOf(xRunStart), 100, 100, paint);
        //mcanvas.drawText(String.valueOf(yRunStart), 200, 100, paint);
        //mcanvas.drawText(String.valueOf(ballSlope), 200, 200, paint);
        //mcanvas.drawText(String.valueOf(xDelta), 100, 400, paint);
        //mcanvas.drawText(String.valueOf(yDelta), 200, 400, paint);
    }

    public void getProperty()
    {
        mode = ((Activity)getContext()).getIntent().getIntExtra("mode", 1);
        level = ((Activity)getContext()).getIntent().getIntExtra("level", 1);

        if(level==1)
            interval = 25;
        else
            interval = 20;
    }
}



