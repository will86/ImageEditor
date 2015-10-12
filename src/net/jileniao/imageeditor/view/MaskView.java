package net.jileniao.imageeditor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class MaskView extends BaseMaskView {

    public MaskView(Context context) {
        super(context);
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画一个蒙板
        canvas.drawRect(0, 0, mWidth, mHeight, mPaintCircle);
        // 将path画出，因为mPaintRect的模式为PorterDuff.Mode.XOR，所以画图时会让交叠部分都不显示，从而显示出底部图片来
        mMatrix.reset();
        // 下面两行的作用是放大图片，并绘制出来
        mMatrix.postScale((float) mWidth / mBitmapGround.getWidth(),
                (float) mHeight / mBitmapGround.getHeight());
        mCanvasBm.drawBitmap(mBitmapGround, mMatrix, null);

        mCanvasBm.drawPath(mPath, mPaintRect);
        // 这一步的意义是：将mCanvasBm在mBitmap上绘制的内容画出
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }
}
