package pers.loren.appupdate.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import pers.loren.appupdate.R;

/**
 * Copyright © 2018/12/28 by loren
 */
public class DownloadProcessView extends View {

    private Paint paint = new Paint(); // 绘制背景灰色线条画笔
    private Paint paintText = new Paint(); // 绘制下载进度画笔
    private float offset = 0; // 偏移量
    private float maxvalue = 100;
    private float currentProcess = 0f;
    private Rect mBound = new Rect(); // 获取百分比数字的长宽
    private String percentValue = "0%"; // 要显示的现在百分比
    private float offsetRight = 0f; // 灰色线条距离右边的距离
    private int textSize = 30;
    private float offsetTop = 18f; // 距离顶部的偏移量

    public DownloadProcessView(Context context) {
        super(context, null);
    }

    public DownloadProcessView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        getTextWidth();
    }

    public DownloadProcessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制底色
        paint.setColor(getResources().getColor(R.color.line));
        paint.setStrokeWidth(2);
        canvas.drawLine(0, offsetTop, getWidth(), offsetTop, paint);
        // 绘制进度条颜色
        paint.setColor(getResources().getColor(R.color.download_indicator_color));
        paint.setStrokeWidth(3);
        canvas.drawLine(0, offsetTop, offset, offsetTop, paint);
        // 绘制白色区域及百分比
        paint.setColor(getResources().getColor(R.color.text_white));
        paint.setStrokeWidth(2);
        paintText.setColor(getResources().getColor(R.color.download_indicator_color));
        paintText.setTextSize(textSize);
        paintText.setAntiAlias(true);
        paintText.getTextBounds(percentValue, 0, percentValue.length(), mBound);
        canvas.drawLine(offset, offsetTop, offset + mBound.width() + 4, offsetTop, paint);
        canvas.drawText(percentValue, offset, offsetTop + mBound.height() / 2 - 2, paintText);
    }

    /**
     * 设置当前进度值
     */
    public void setCurrentProcess(int process) {
        currentProcess = process;
        if (currentProcess < 100) {
            percentValue = currentProcess + "%";
        } else {
            percentValue = "100%";
        }
        initCurrentProgressBar();
        invalidate();
    }

    /**
     * 获取当前进度条长度
     */
    public void initCurrentProgressBar() {
        if (currentProcess < maxvalue) {
            offset = ((getWidth() - offsetRight) * currentProcess / maxvalue);
        } else {
            offset = (getWidth() - offsetRight);
        }
    }

    /**
     * 获取“100%”的宽度
     */
    public void getTextWidth() {
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.getTextBounds("100%", 0, "100%".length(), rect);
        offsetRight = rect.width() + 5;
    }
}
