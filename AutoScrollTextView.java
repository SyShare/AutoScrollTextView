package la.shanggou.live.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import la.shanggou.live.R;
/**
 * Created by Administrator on 2017/7/1.
 */

public class AutoScrollTextView extends TextView {
    private Paint paint = null;//绘图样式

    private float moveSpeed = 5f;//移动的速度,速度设置：1-10
    private float textLength = 0f;//文本长度
    private int contentLength;//字符长度
    private float viewWidth = 0f;//MyText
    private float step = 0f, y = 0f;//文字的横坐标;文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//TextView的长度+TextView文本的长度
    private float temp_view_plus_two_text_length = 0.0f;//TextView的长度+TextView文本的长度*2

    private boolean isStarting = false;//是否开始滚动
    private boolean isRunning = false;//是否正在滚动

    private String text = "";//文本内容
    private String changeText = "";//切换后的文本内容

    private int repeatCount = 0;
    private int MarqueeRepeatLimit;

    public AutoScrollTextView(Context context) {
        this(context, null);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, la.shanggou.live.R.styleable.MarqueeView, defStyleAttr, 0);
        moveSpeed = a.getFloat(la.shanggou.live.R.styleable.MarqueeView_moveSpeed, 3f);
        a.recycle();

        paint = getPaint();
        paint.setColor(this.getCurrentTextColor());//文本颜色

        text = getText().toString();
        setTextValue();
        y = getTextSize() + getPaddingTop();
        MarqueeRepeatLimit = getMarqueeRepeatLimit();
        changeText = TextUtils.isEmpty(changeText) ? changeTextStyle(text) : changeText;
    }

    public void setText(String text) {
        if (text != null && !text.equals(this.text)) {
            this.text = text;
            setTextValue();
        }
    }

    private void setTextValue() {
        textLength = paint.measureText(text);
        step = viewWidth + textLength;
        temp_view_plus_text_length = viewWidth + textLength;
        temp_view_plus_two_text_length = viewWidth + textLength * 2;
    }

    /**
     * 覆写TextView的onDraw()方法，实现文本滚动显示的效果
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        contentLength = text.length();
        viewWidth = getWidth();
        canvas.drawText(isStarting ? text : changeText, temp_view_plus_text_length - step, y, paint);
        if (!isStarting) {
            return;
        }
        if (isOverLength()) {
            step += moveSpeed;//速度设置：1-10
            isRunning = true;
            if (step > temp_view_plus_two_text_length + moveSpeed) {
                step = viewWidth/2;
                repeatCount++;
                if (repeatCount == MarqueeRepeatLimit) {
                    stopScroll();
                }
            }
            postInvalidate();
        }
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        isStarting = true;
        if(isStarting && !isRunning)
            postInvalidate();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        invalidate();
        reset();
    }

    private void reset(){
        isRunning = false;
        isStarting = false;
        step = temp_view_plus_text_length;
        changeText = changeTextStyle(text);
        repeatCount = 0;
    }

    private String changeTextStyle(String text) {
        if (isOverLength() || contentLength > 4) {
            text = text.substring(0, 3) + "...";
        }
        return text;
    }

    private boolean isOverLength() {
        return textLength > viewWidth;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(hasWindowFocus)
            super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if(focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

}
