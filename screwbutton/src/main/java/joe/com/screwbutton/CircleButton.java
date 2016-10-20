package joe.com.screwbutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Description
 * Created by chenqiao on 2016/10/19.
 */
public class CircleButton extends ImageView {
    private int mWidth;
    private int mRadius;
    private Matrix mMatrix;
    private Paint mPaint;

    public CircleButton(Context context) {
        this(context, null);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMatrix = new Matrix();
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mRadius = mWidth / 2;
        int mHeight = mWidth;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#FF4081"));
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        mPaint.reset();
        setShader();
        canvas.drawCircle(mRadius, mRadius, (float) (mWidth * 0.9), mPaint);
    }

    private void setShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        mPaint.setAntiAlias(true);//去锯齿
        Bitmap bm = drawableToBitmap(drawable);
        BitmapShader mBitmapShader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //计算图片与View的大小关系和缩放比例
        int bmSize = Math.min(bm.getWidth(), bm.getHeight()); //取宽高最小值
        if (bmSize > mWidth * 0.9) {
            float scale = mWidth * 0.9f / bmSize; //将最小的缩放到View的大小（填充）
            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(mRadius - mWidth * 0.9f / 2f, mRadius - mWidth * 0.9f / 2f);
        } else {
            mMatrix.setTranslate(mRadius - bm.getWidth() / 2f, mRadius - bm.getHeight() / 2f);
        }
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mBitmapShader);
    }

    /**
     * Drawable转Bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bm;
    }
}