package ru.myshows.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 17.08.2011
 * Time: 15:06:31
 * To change this template use File | Settings | File Templates.
 */
public class TextProgressBar extends ProgressBar {

    private String text;
    private Paint textPaint;

    public TextProgressBar(Context context) {
        super(context);
        text = "";
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        text = "";
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        text = "";
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {  
        // First draw the regular progress bar, then custom draw our text
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 ;
        canvas.drawText(text, x, y, textPaint);
    }

    public synchronized void setText(String text) {
        this.text = text;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }

}
