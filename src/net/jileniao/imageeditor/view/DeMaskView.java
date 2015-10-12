package net.jileniao.imageeditor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class DeMaskView extends BaseMaskView {

    public DeMaskView(Context context) {
        super(context);
    }

    public DeMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DeMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mMatrix.reset();
        // 下面两行的作用是放大图片，并绘制出来
        mMatrix.postScale((float) mWidth / mBitmapGround.getWidth(),
                (float) mHeight / mBitmapGround.getHeight());
        canvas.drawBitmap(mBitmapGround, mMatrix, null);
        // 画一个蒙板
        mCanvasBm.drawRect(0, 0, mWidth, mHeight, mPaintCircle);

        // 将path画出，因为mPaintRect的模式为PorterDuff.Mode.XOR，所以画图时会让交叠部分都不显示，从而显示出底部图片来
        mCanvasBm.drawPath(mPath, mPaintRect);

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }
}
