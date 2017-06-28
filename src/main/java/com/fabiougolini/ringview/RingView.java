package com.fabiougolini.ringview;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class RingView extends View {

    private OnValueChange onValueChange;

    private Paint paint = new Paint();
    private Paint backRing = new Paint();
    private Paint textPaint = new Paint();

    private int thickness = 25;
    private int startValue = 0;
    private int endValue = 100;
    private float textSize = 20;
    private String text;

    boolean isValueChanging = false;

    public void setThickness(int thickness) {
        this.thickness = thickness;
        invalidate();
    }

    public RingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        applyStyle(context, attrs);
    }

    public RingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyStyle(context, attrs);
    }

    public void applyStyle(Context context, AttributeSet attributeSet){
        TypedArray attributeArray = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.RingView
        );

        paint.setColor(attributeArray.getColor(R.styleable.RingView_ringColor, Color.GREEN));

        thickness = attributeArray.getInt(R.styleable.RingView_ringThickness, 25);

        startValue = attributeArray.getInt(R.styleable.RingView_startValue, 0);
        endValue = attributeArray.getInt(R.styleable.RingView_endValue, 0);

        startValue = map(startValue, 0, 100, 0, 360);
        endValue = map(endValue, 0, 100, 0, 360);

        backRing.setColor(attributeArray.getColor(R.styleable.RingView_ringColor, Color.GREEN));
        backRing.setStyle(Paint.Style.STROKE);
        backRing.setAlpha(60);

        text = attributeArray.getString(R.styleable.RingView_text);

        AssetManager assetManager = context.getAssets();
        Typeface plain = Typeface.createFromAsset(assetManager, "fonts/neuropol.ttf");

        textSize = attributeArray.getDimension(R.styleable.RingView_textSize, 30);
        textPaint.setTypeface(plain);

        attributeArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(thickness);

        backRing.setStrokeWidth(thickness);

        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        canvas.drawCircle(
                getWidth() / 2,
                getHeight() / 2,
                getWidth() / 2 - backRing.getStrokeWidth() / 2, backRing);

        canvas.drawArc(
                0 + paint.getStrokeWidth() / 2,
                0 + paint.getStrokeWidth() / 2,
                getWidth() - paint.getStrokeWidth() / 2,
                getHeight() - paint.getStrokeWidth() / 2,
                startValue - 90,
                endValue,
                false,
                paint
        );

        if(text != null && text.length() > 0){
            Rect r = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), r);
            int yPos = (Math.abs(r.height()))/2;

            canvas.drawText(text, getWidth() / 2, (getHeight() / 2) + yPos, textPaint);
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public void setText(String text){
        this.text = text;
        invalidate();
    }

    // setValue 0-100
    public void setValue(int value){
        this.endValue = map(value, 0, 100, 0, 360);

        invalidate();
    }

    public void setAngle(float x, float y){

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int angle = (int) (Math.toDegrees(Math.atan2(getHeight() / 2 - y, getWidth() / 2 - x)));

        if(angle >= 0 && angle <= 90) {
            angle = angle + 270;
        } else if(angle > 90 && angle <= 180){
            angle = angle -90;
        } else if(angle < 0){
            angle = (180 - (angle * -1)) + 90;
        }

        int pct = map(angle, 0, 360, 0, 100);

        onValueChange.onChange(pct);

        endValue = angle;

        invalidate();
    }

/*    public double setAngle(float x, float y){
        double tx = x - getWidth() / 2;
        double ty = y - getHeight() / 2;
        double t_length = Math.sqrt(tx * tx + ty * ty);
        double a = Math.acos(ty / t_length);

        Log.d("RingView", "Angle: " + a);

        endValue = (int)a;
        invalidate();

        return a;
    }*/

    private int map(int x, int in_min, int in_max, int out_min, int out_max){
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public void setOnValueChangeListener(OnValueChange onValueChange){
        this.onValueChange = onValueChange;
    }

    public interface OnValueChange {
        public void onChangeStart(int percent);
        public void onChange(int percent);
        public void onChangeStop(int percent);
    }
}
