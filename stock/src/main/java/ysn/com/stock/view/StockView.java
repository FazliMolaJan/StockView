package ysn.com.stock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;

import ysn.com.stock.R;

/**
 * @Author yangsanning
 * @ClassName BaseView
 * @Description 一句话概括作用
 * @Date 2019/5/4
 * @History 2019/5/4 author: description:
 */
public class StockView extends View {

    /**
     * 默认虚线效果
     */
    private static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(new float[]{2, 2, 2, 2}, 1);

    protected Context context;

    /**
     * columnCount: 列数+1
     * topRowCount: 上表横数+1
     * bottomRowCount: 下表横数+1
     */
    private int columnCount = 4;
    private int topRowCount = 4;
    private int bottomRowCount = 2;

    /**
     * 两边边距
     */
    protected float tableMargin = 1;

    protected int viewWidth, viewHeight;

    protected float timeTableHeight;
    protected float topTableWidth, topTableHeight;

    protected Paint xYTextPaint;
    protected Rect textRect = new Rect();
    protected DecimalFormat decimalFormat;

    protected Paint linePaint, dottedLinePaint;
    protected Path linePath;

    /**
     * 虚线效果
     */
    protected PathEffect mDashEffect = DEFAULT_DASH_EFFECT;

    public StockView(Context context) {
        this(context, null);
    }

    public StockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        linePaint = new Paint();
        linePaint.setColor(getColor(R.color.stock_line));
        linePaint.setStrokeWidth(1f);
        linePaint.setStyle(Paint.Style.STROKE);

        dottedLinePaint = new Paint();
        dottedLinePaint.setStyle(Paint.Style.STROKE);
        dottedLinePaint.setPathEffect(mDashEffect);
        dottedLinePaint.setStrokeWidth(1f);

        linePath = new Path();

        xYTextPaint = new Paint();
        xYTextPaint.setAntiAlias(true);
        xYTextPaint.setStyle(Paint.Style.STROKE);
        xYTextPaint.setTextAlign(Paint.Align.LEFT);

        decimalFormat = new DecimalFormat("0.00");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;

        timeTableHeight = viewHeight * 0.055f;
        topTableHeight = viewHeight - timeTableHeight;

        xYTextPaint.setTextSize(timeTableHeight * 0.8f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(0, topTableHeight);

        // 绘制边框
        drawBorders(canvas);
        // 绘制竖线
        drawColumnLine(canvas);
        // 绘制横线
        drawRowLine(canvas);
        // 绘制时间坐标
        drawTimeText(canvas);

        //开放给子类自由绘制
        childDraw(canvas);

        canvas.restore();
    }

    /**
     * 绘制边框
     */
    protected void drawBorders(Canvas canvas) {
        // 上表边框
        linePath.moveTo(tableMargin, getTopTableMinY());
        linePath.lineTo(tableMargin, getTopTableMaxY());
        linePath.lineTo((viewWidth - tableMargin), getTopTableMaxY());
        linePath.lineTo((viewWidth - tableMargin), getTopTableMinY());
        linePath.close();
        canvas.drawPath(linePath, linePaint);
        linePath.reset();
    }

    protected float getTopTableMaxY() {
        return (tableMargin - topTableHeight);
    }

    protected float getTopTableMinY() {
        return -tableMargin;
    }

    /**
     * 绘制竖线
     */
    protected void drawColumnLine(Canvas canvas) {
        // 绘制上表竖线
        dottedLinePaint.setColor(getColor(R.color.stock_dotted_column_line));
        float xSpace = (viewWidth - 2 * tableMargin) / columnCount;
        for (int i = 1; i < columnCount; i++) {
            linePath.reset();
            float x = getColumnX(xSpace, i);
            linePath.moveTo(x, getTopTableMinY());
            linePath.lineTo(x, getTopTableMaxY());
            canvas.drawPath(linePath, dottedLinePaint);
        }
    }

    /**
     * 获取竖线x轴坐标
     *
     * @param xSpace   竖线x轴间隙
     * @param position 当前position
     * @return 竖线x轴坐标
     */
    protected float getColumnX(float xSpace, int position) {
        return tableMargin + xSpace * position;
    }

    /**
     * 绘制横线
     */
    protected void drawRowLine(Canvas canvas) {
        // 绘制上表横线
        float rowSpacing = topTableHeight / topRowCount;
        for (int i = 1; i < topRowCount; i++) {
            linePath.reset();
            float y = getRowY(rowSpacing, i);
            linePath.moveTo(tableMargin, y);
            linePath.lineTo((viewWidth - tableMargin), y);
            dottedLinePaint.setColor(getColor(i != topRowCount / 2 ?
                    R.color.stock_dotted_column_line : R.color.stock_dotted_row_line));
            canvas.drawPath(linePath, dottedLinePaint);
        }
    }

    /**
     * 获取横线y轴坐标
     *
     * @param ySpace   横线y轴间隙
     * @param position 当前position
     * @return 横线y轴坐标
     */
    protected float getRowY(float ySpace, int position) {
        return getTopTableMaxY() + ySpace * position;
    }

    /**
     * 绘制时间坐标
     */
    protected void drawTimeText(Canvas canvas) {
    }

    /**
     * 注意: 要先进行测量文本 getTextBounds
     *
     * @return 时间文字的Y坐标
     */
    protected float getTimeTextY() {
        return getXYTextMargin() + textRect.height();
    }

    /**
     * 注意: 要先进行测量文本 getTextBounds
     *
     * @return 文本距离XY轴的Margin
     */
    protected float getXYTextMargin() {
        return (timeTableHeight - textRect.height()) / 2f;
    }

    /**
     * 开放给子类自由绘制
     */
    protected void childDraw(Canvas canvas) {
    }

    protected int getColor(@ColorRes int colorRes) {
        return context.getResources().getColor(colorRes);
    }
}
