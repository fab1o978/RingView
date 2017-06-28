package com.fabiougolini.ringview;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RingViewControl extends RelativeLayout {

    private RingView ringView;
    private OnValueChangeListener onValueChangeListener;

    public RingViewControl(Context context) {
        super(context);
        init();
    }

    public RingViewControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RingViewControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.ringviewcontrol, this);

        final TextView tvPct = (TextView)findViewById(R.id.pct);
        AssetManager am = getContext().getAssets();
        Typeface tf = Typeface.createFromAsset(am, "fonts/neuropol.ttf");
        tvPct.setTypeface(tf);

        ringView = (RingView) findViewById(R.id.ring);

        ringView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    tvPct.animate().scaleXBy(.4f).scaleYBy(.4f).setDuration(150).start();
                } else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                    //Log.d("RingViewControl", motionEvent.getX() + ", " + motionEvent.getY());
                    ((RingView)view).setAngle(motionEvent.getX(), motionEvent.getY());
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    tvPct.animate().scaleXBy(-.4f).scaleYBy(-.4f).setDuration(250).start();
                }
                return true;
            }
        });
        ringView.setThickness(60);
        //ringView.setText("Ready");

        ringView.setOnValueChangeListener(new RingView.OnValueChange() {
            @Override
            public void onChangeStart(int percent) {
                Log.d("RingViewControl", "Touch started at position: " + percent);
            }

            @Override
            public void onChange(int percent) {
                tvPct.setText(percent + "%");
                onValueChangeListener.onValueChanged(percent);
            }

            @Override
            public void onChangeStop(int percent) {
                Log.d("RingViewControl", "Touch finished at position: " + percent);
            }
        });
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener){
        this.onValueChangeListener = onValueChangeListener;
    }

    public interface OnValueChangeListener {
        public void onValueChanged(int percent);
    }
}
