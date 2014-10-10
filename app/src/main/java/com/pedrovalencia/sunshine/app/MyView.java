package com.pedrovalencia.sunshine.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by pedrovalencia on 09/10/2014.
 */
public class MyView extends View {

    private int weatherIcon;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int myHeight = hSpecSize;
        if(hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        } else if(hSpecMode == MeasureSpec.AT_MOST){
            //wrap content
        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int myWidth = wSpecSize;
        if(wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        } else if(wSpecMode == MeasureSpec.AT_MOST){
            //wrap content
        }

        setMeasuredDimension(myWidth, myHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap smiley;
        if(weatherIcon == R.drawable.art_clear) {
            //happy smiley
            smiley = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_action_emo_basic);
        } else {
            //sad smiley
            smiley = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_action_emo_shame);
        }

        canvas.drawBitmap(smiley, 0.0f, 0.0f, null);
    }

    public void setWeatherIcon(int weatherIconIn) {
        this.weatherIcon = weatherIconIn;
    }
}
