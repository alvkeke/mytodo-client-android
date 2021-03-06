package com.alvkeke.tools.todo.Compoents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

public class ColorSelector extends AppCompatImageView {

    int radius;
    int centerX;
    int centerY;

    float touchCircleY;
    float touchCircleX;

    Paint colorWheelPaint;
    Paint selectPain;

    int[] colors;

    int mColor;

    //三个构造方法必须都有，否则会出现闪退的情况
    public ColorSelector(Context context) {
        super(context);
        init();
    }

    public ColorSelector(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public ColorSelector(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init();
    }

    void init(){
        setBackgroundColor(Color.TRANSPARENT);
        colorWheelPaint = new Paint();
        selectPain = new Paint();


        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        colors = new int[colorCount];
        float[] hsv = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
    }

    public int getColor(){
        return mColor;
    }

    private int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }
    private int interpColor(int[] colors, float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float cx = x - centerX;
                float cy = y - centerY;
                double d = Math.sqrt(cx * cx + cy * cy);

                if (d <= radius) {
                    touchCircleY = y;
                    touchCircleX = x;
                }else {
                    double angle = Math.atan2(cy, cx);
                    touchCircleY = (float) ((radius*Math.sin(angle)) + centerY);
                    touchCircleX = (float) ((radius*Math.cos(angle)) + centerX);
                }

                float angle = (float)Math.atan2(touchCircleY - centerY, touchCircleX - centerX);
                float unit = angle/(2*3.141592653f);
                if(unit <0){
                    unit+=1;
                }

                mColor = interpColor(colors, unit);

                postInvalidate();


                break;
            case MotionEvent.ACTION_UP:
                performClick();
                break;
        }
        return true;


    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    void createColorWheel(){

        SweepGradient sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        //RadialGradient radialGradient = new RadialGradient(centerX, centerY,
        //        radius, 0xFFFFFFFF, 0x00000000, Shader.TileMode.CLAMP);
        //Shader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        colorWheelPaint.setShader(sweepGradient);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(centerX, centerY) - 16;
        //生成色轮
        createColorWheel();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, radius, colorWheelPaint);
        if(touchCircleX != 0 && touchCircleY != 0) {
            //canvas.drawCircle(touchCircleX, touchCircleY, 10, selectPain);
            for(int i = 0; i<360; i+=5){
                canvas.drawArc(touchCircleX-15, touchCircleY-15, touchCircleX+15,
                        touchCircleY+15, i, 80, false, selectPain);
            }
        }
    }

}
