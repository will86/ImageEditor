package net.jileniao.imageeditor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.jileniao.imageeditor.R;

public class BaseMaskView extends View {

    protected int mWidth;
    protected int mHeight;
    private float x;
    private float y;
    private float old_x;
    private float old_y;

    protected Paint mPaintCircle;
    protected Paint mPaintRect;

    protected Bitmap mBitmap;
    protected Bitmap mBitmapGround;

    protected Matrix mMatrix;
    protected Path mPath;
    protected Canvas mCanvasBm;

    public BaseMaskView(Context context) {
        super(context);
    }

    public BaseMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.editor);

        BitmapDrawable dra = (BitmapDrawable) a
                .getDrawable(R.styleable.editor_background);
        if (dra == null) {
            mBitmapGround = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
        } else {
            mBitmapGround = dra.getBitmap();
        }

        mPaintRect = new Paint();
        float paintWidth = a.getDimension(R.styleable.editor_paintWidth, 30);
        mPaintRect.setStrokeWidth(paintWidth);

        mPaintCircle = new Paint();
        int paintColor = a
                .getColor(R.styleable.editor_paintColor, Color.YELLOW);
        mPaintCircle.setColor(paintColor);

        // XOR：交叠和被交叠部分均不显示;DST_OVER：自身交叠部分不显示;SRC_OVER交叠部分只显示自己
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
        mPaintRect.setXfermode(mode);
        // 设置联接部分样式
        mPaintRect.setStrokeJoin(Paint.Join.ROUND);
        // 设置中间部分样式：圆形
        mPaintRect.setStrokeCap(Paint.Cap.ROUND);
        mPaintRect.setStyle(Paint.Style.FILL_AND_STROKE);

        mPath = new Path();
        mMatrix = new Matrix();
    }

    public BaseMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvasBm = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

        case MotionEvent.ACTION_DOWN:
            x = event.getX();
            y = event.getY();
            // CW是Path移动方向：顺时针
            // mPath.addCircle(x,y,50,Path.Direction.CW);
            mPath.moveTo(x, y);

            // 重新绘制，就是重新调用onDraw()方法
            invalidate();
            // 更新old_x、old_y的值
            old_x = x;
            old_y = y;
            return true;

        case MotionEvent.ACTION_MOVE:
            y = event.getY();
            x = event.getX();
            // 将mPath移动到上一个位置
            mPath.moveTo(old_x, old_y);
            // 绘制贝塞尔曲线，（(x + old_x) / 2, (y + old_y) / 2）作为控制点，（x, y）作为结束点
            mPath.quadTo((x + old_x) / 2, (y + old_y) / 2, x, y);
            invalidate();
            // 更新old_x、old_y的值
            old_x = x;
            old_y = y;
            return true;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }
}
