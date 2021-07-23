package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

public class customView<xRunStart> extends View
{
    public Integer flagSliderMove=0, flagBall=0, flagSlider=0, flagBallFirst=0, flagHitSlider=0;
    public Integer flagHitWall=0;
    public Canvas mcanvas;
    public static float xCoord=0, yCoord=0;
    public static Integer canvasSide = 1;
    public static float xRandomStart, yRandom;
    public static Float xCanvas, yCanvas;
    private slider sliderObj;
    private ball ballObj;
    public Float xRunStart, yRunStart, ballSlope;
    public Boolean updateView=true;
    public Integer interval=10;

    private class UpdateViewRunnable implements Runnable {
        public void run()
        {
            invalidate();
            flagBall=1;
            if(updateView) {
                postDelayed(this, interval);
            }
        }
    }

    private UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
    public customView(Context context)
    {
        super(context);
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

    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        mcanvas = canvas;
        mcanvas.drawColor(Color.GRAY);
        if(sliderObj==null)
        {
            setDefault();
            sliderObj = new slider((float) mcanvas.getHeight());
            ballObj = new ball();
        }

        debugCanvas();
        if(flagBall==1) {
            if ((yRunStart-20>=(yCanvas-50))&&(flagHitSlider==0)) {
                if (sliderObj.slider.contains(xRunStart, yRunStart - 20)) {
                    ballHit();
                    flagHitSlider = 1;
                } else {
                    updateView = false;
                    Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
                    getContext().startActivity(intentCanva);
                }
            }


            if((((xRunStart-20)<=0)||((xRunStart+20)>=xCanvas))&&(flagHitSlider==0)) {
                flagHitWall = 1;
                if ((xRunStart-20) <= 0) {
                    canvasSide = -1; }
                else {
                    canvasSide = 1; }
            }

            if(flagHitWall==1)
            {
                ballObj.ballHitWall();
            }
            else if(flagHitSlider==1)
            {
                ballObj.ballHitSlider();
            }
            else {
                ballObj.moveBallStart();
            }
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            mcanvas.drawCircle(xRunStart, yRunStart, 20, paint);
        }

        Paint paint = new Paint();
        paint.setColor(Color.MAGENTA);
        mcanvas.drawRect(sliderObj.slider, paint);
        flagBall=0;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        flagBall = 1;
        xCoord = event.getX();
        yCoord = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (sliderObj.slider.contains(xCoord, yCoord)) {
                    flagSliderMove = 1;
                    Log.d("test","down");
                    sliderObj.setSliderPos(xCoord);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (flagSliderMove == 1) {
                    sliderObj.setSliderPos(xCoord);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                flagSliderMove = 0;
                Log.d("test","up");
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
        Float radius;

        public ball()
        {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            mcanvas.drawCircle(xCanvas/2, yCanvas/2, 20, paint);
        }

        public void moveBallStart()
        {
            if(((yRunStart+10)>=0)&&(flagBallFirst==0))
            {
                yRunStart=yRunStart-10;
                xRunStart = xRunStart+(canvasSide*ballSlope*10);
            }//
            else {
                xRunStart = xRunStart+(canvasSide*ballSlope*10);
                yRunStart = yRunStart+10;
                flagBallFirst = 1;
            }
        }

        public void ballHitSlider()
        {
            if((((xRunStart+20)<=xCanvas)&&(canvasSide==1))||((xRunStart-20>=0)&&(canvasSide==-1))) {
                xRunStart = xRunStart + (canvasSide * ballSlope*10);
                yRunStart = yRunStart - 10;
            }

            else{
                if(flagHitSlider==1) {
                    ballSlope=1/ballSlope;
                    if (xRunStart < (xCanvas / 2)) {
                        canvasSide = -1;
                    } else {
                        canvasSide = 1;
                    }
                }
                flagHitSlider=0;
                xRunStart = xRunStart - 10*canvasSide;
                yRunStart = yRunStart - (canvasSide * ballSlope*10);
            }
        }

        public void ballHitWall()
        {
            yRunStart = yRunStart + 10;
            xRunStart = xRunStart - (canvasSide * ballSlope*10);
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

        public void setSliderPos(Float xCoord)
        {
            if((xCoord+(width/2))>xCanvas)
            {
                slider.left = xCanvas-width;
                slider.right = xCanvas;
            }
            else if((xCoord-(width/2))<0)
            {
                slider.left = 0;
                slider.right = width;
            }
            else {
                slider.left = xCoord - (width/2);
                slider.right = xCoord + (width/2);
            }
        }
    }


    public void ballHit()
    {
        Random random = new Random();
        yRandom = random.nextInt((int) (yCanvas + 1));
        Float ballSlope = (xCanvas-xRunStart)/(yCanvas-yRandom);
        //ballShift = ballSlope*10;
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
        mcanvas.drawColor(Color.GRAY);

        xRunStart = xCanvas/2;
        yRunStart = yCanvas/2;
        Random random = new Random();
        xRandomStart = random.nextInt((int) (xCanvas+1));
        if(xRandomStart<xRunStart)
        {
            xRandomStart = xRunStart-xRandomStart;
            canvasSide=-1;
        }
        else
        {
            xRandomStart = xRandomStart-xRunStart;
        }

        ballSlope = (xRandomStart)/yRunStart;
        //ballShift = ballSlope*10;
    }

    public void debugCanvas()
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        mcanvas.drawText(String.valueOf(xRunStart), 100, 100, paint);
        mcanvas.drawText(String.valueOf(yRunStart), 200, 200, paint);
        mcanvas.drawText(String.valueOf(flagHitWall), 100, 300, paint);
        mcanvas.drawText(String.valueOf(flagHitSlider), 100, 400, paint);
    }

}

/*
            paint.setTextSize(50);
            mcanvas.drawText(String.valueOf(xRunStart), 100, 100, paint);
            mcanvas.drawText(String.valueOf(yRunStart), 200, 100, paint);

        //Paint paint = new Paint();*/

