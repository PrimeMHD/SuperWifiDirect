package hiveconnect.com.superwifidirect.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import hiveconnect.com.superwifidirect.R;



public class ButtonView extends MediaView {

    String textStr = "Button";
    int textColor;
    int textSize;
    @Override
    public void drawInside(Canvas canvas) {


        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(3);

        mPaint.setTextSize(textSize);

        Typeface typeface = Typeface.create("sans-serif-smallcaps", Typeface.ITALIC);
        mPaint.setTypeface(typeface);

        mPaint.setColor(textColor);
        mPaint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        mPaint.getTextBounds(textStr, 0, textStr.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(textStr,getMeasuredWidth() / 2 - bounds.width() / 2, baseline, mPaint);
    }







    public ButtonView(Context context) {
        super(context);

    }



    public ButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ButtonView, defStyleAttr, 0);



        textColor = array.getColor(R.styleable.ButtonView_textColor, Color.GRAY);
        textStr=array.getString(R.styleable.ButtonView_text);
        textSize=array.getDimensionPixelSize(R.styleable.ButtonView_textSize,90);
        array.recycle();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        int ac = event.getActionMasked();
        int ex = (int) event.getX();
        int ey = (int) event.getY();
        if (ex < 0 || ey < 0 || ex > getWidth() || ey > getHeight()) {
            release();
            return true;
        }

        switch (ac) {
            case MotionEvent.ACTION_DOWN:
                press();
                //调用 View 的事件监听以使用 View 的 click 和 longClick 监听
                super.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
                release();
                //调用 View 的事件监听以使用 View 的 click 和 longClick 监听
                super.onTouchEvent(event);
                break;
        }

        return false;
    }

}




