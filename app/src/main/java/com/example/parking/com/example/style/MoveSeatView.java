package com.example.parking.com.example.style;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.example.parking.R;

import java.util.ArrayList;

import static org.xutils.common.util.DensityUtil.dip2px;

public class MoveSeatView  extends View {

    private final boolean DBG = false;
    Paint paint = new Paint();
    Matrix matrix = new Matrix();

    Matrix tempMatrix = new Matrix();

    //水平间距
    int spacing;

    //座位垂直间距
    int verSpacing;

    //行号宽度
    int numberWidth;

    //行数
    int row;

    //列数
    int column;

    //可选车位的图片
    Bitmap seatBitmap;

    //选中时车位的图片
    Bitmap checkedSeatBitmap;
    int lastX;
    int lastY;

    //整个车位图的宽度
    int seatBitmapWidth;
    int seatBitmapHeight;
    float screenHeight;

    //屏幕的最小宽度
    int defaultScreenWidth;

    //标识是否正在缩放
    boolean isScaling;
    float scaleX, scaleY;

    //是否是第一次缩放
    boolean firstScale = true;
    boolean isOnClick;
    private int downX, downY;
    private boolean pointer;

    //用于存储已经选在好的车位
    public ArrayList<Point> list;

    /**
     * 默认的车位图片的宽度，如果使用的自己的座位的图片比这个尺寸大或者小，会缩放到这个大小
     */
    private float defaultImgW = 40;
    private float defaultImgH = 34;

    /**
     * 车位图片的宽度
     */
    private int seatWidth = 40;

    /**
     * 车位图片的高度
     */

    private int seatHeight = 34;
    private float zoom;
    float xScalel = 1;
    float yScalel = 1;


    public MoveSeatView(Context context) {
        this(context, null);
    }

    public MoveSeatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MoveSeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void init() {
        spacing = (int) dip2px(5);
        verSpacing = (int) dip2px(10);
        defaultScreenWidth = (int) dip2px(80);
        seatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seat_default);
        xScalel = defaultImgW / seatBitmap.getWidth();
        yScalel = defaultImgH / seatBitmap.getHeight();
        checkedSeatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seat_green);
        seatBitmapWidth = (int) (column * seatBitmap.getWidth() * xScalel + (column - 1) * spacing);
        seatBitmapHeight = (int) (row * seatBitmap.getHeight() * yScalel + (row - 1) * verSpacing);
        paint.setColor(Color.RED);
        numberWidth = (int) dip2px(20);
        screenHeight = dip2px(20);
        list = new ArrayList<>();
        matrix.postTranslate(numberWidth + spacing, screenHeight + 1 + verSpacing);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (row <= 0 || column <= 0) {
            return;
        }
        drawSeat(canvas);
        super.onDraw(canvas);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        scaleGuestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            //多手指操作
            pointer = true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointer = false;
                downX = x;
                downY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                autoScale();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && !isOnClick) {
                    int downDX = Math.abs(x - downX);
                    int downDY = Math.abs(y - downY);
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        int dx = x - lastX;
                        int dy = y - lastY;
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }
                }
                lastX = x;
                lastY = y;
                isOnClick = false;
                break;
        }

        return true;
    }

    private void autoScale() {
        if (getMatrixScaleX() > 2.2) {
            zoomAnimate(getMatrixScaleX(), 2.0f);
        } else if (getMatrixScaleX() < 0.98) {
            zoomAnimate(getMatrixScaleX(), 1.0f);
        }
    }

    private void drawSeat(Canvas canvas) {
        zoom = getMatrixScaleX();
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        float scaleX = zoom;
        float scaleY = zoom;
        for (int i = 0; i < row; i++) {
            float top = i * seatBitmap.getHeight() * yScalel * scaleY + i * verSpacing * scaleY + translateY;
            float bottom = top + seatBitmap.getHeight() * yScalel * scaleY;

            for (int j = 0; j < column; j++) {
                float left = j * seatBitmap.getWidth() * xScalel * scaleX + j * spacing * xScalel * scaleX + translateX;
                float right = left + seatBitmap.getWidth() * xScalel * scaleX;
                tempMatrix.setTranslate(left, top);
                tempMatrix.postScale(xScalel, yScalel, left, top);
                tempMatrix.postScale(scaleX, scaleY, left, top);
                if (isHave(i, j)) {
                    canvas.drawBitmap(checkedSeatBitmap, tempMatrix, paint);
                    drawText(canvas, i, j, top, left);
                } else {
                    canvas.drawBitmap(seatBitmap, tempMatrix, paint);
                }
            }
        }
    }


    float[] m = new float[9];
    private float getMatrixScaleX() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }

    private float getTranslateX() {
        matrix.getValues(m);
        return m[2];
    }

    private float getTranslateY() {
        matrix.getValues(m);
        return m[5];
    }

    private float getMatrixScaleY() {
        matrix.getValues(m);
        return m[4];
    }


    /**
     * 是否被选上
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isHave(int x, int y) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        for (Point point : list) {
            if (point.x == x && point.y == y) {
                return true;
            }
        }
        return false;
    }


    private void drawText(Canvas canvas, int row, int column, float top, float left) {
        String txt = (row + 1) + "排";
        String txt1 = (column + 1) + "座";
        TextPaint txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float seatHeight = this.seatHeight * getMatrixScaleX();
        float seatWidth = this.seatWidth * getMatrixScaleX();
        txtPaint.setTextSize(seatHeight / 3);
        //获取中间线
        float center = seatHeight / 2;
        float txtWidth = txtPaint.measureText(txt);
        float startX = left + seatWidth / 2 - txtWidth / 2;
        //只绘制一行文字
        if (txt1 == null) {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + seatHeight), txtPaint);

        } else {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + center), txtPaint);
            canvas.drawText(txt1, startX, getBaseLine(txtPaint, top + center, top + center + seatHeight / 2), txtPaint);

        }
        if (DBG) {
            Log.d("drawTest", "top" + top);

        }
    }

    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        int baseLine = (int) ((bottom + top - fontMetrics.bottom - fontMetrics.top) / 2);
        return baseLine;
    }

    private void zoomAnimate(float cur, float tar) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cur, tar);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
//        ZoomAnimation zoomAnim = new ZoomAnimation();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                zoom = (Float) animation.getAnimatedValue();
                zoom(zoom);
                if (DBG) {
                    Log.d("zoomTest", "zoom" + zoom);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    private void zoom(float zoom) {
        float z = zoom / getMatrixScaleX();
        matrix.postScale(z, z, scaleX, scaleY);
        invalidate();
    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animation.AnimationListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    ScaleGestureDetector scaleGuestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isScaling = true;
            float scaleFactor = detector.getScaleFactor();
            if (getMatrixScaleY() * scaleFactor > 3) {
                scaleFactor = 3 / getMatrixScaleY();
            }
            if (firstScale) {
                scaleX = detector.getCurrentSpanX();
                scaleY = detector.getCurrentSpanY();
                firstScale = false;
            }
            if (getMatrixScaleY() * scaleFactor < 0.5) {
                scaleFactor = 0.5f * getMatrixScaleY();
            }
            matrix.postScale(scaleFactor, scaleFactor, scaleX, scaleY);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            isScaling = false;
            firstScale = true;
        }
    });

    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    int tempX = (int) ((j * seatWidth + j * spacing) * getMatrixScaleX() + getTranslateX());
                    int maxTempX = (int) (tempX + seatWidth * getMatrixScaleX());
                    int tempY = (int) ((seatHeight * i + i * verSpacing) * getMatrixScaleY() + getTranslateY());
                    int maxTempY = (int) (tempY + seatHeight * getMatrixScaleY());
                    if (x >= tempX && x <= maxTempX && y >= tempY && y <= maxTempY) {
                        if (isHave(i, j)) {
                            remove(i, j);
                        } else {
                            list.add(new Point(i, j));
                        }
                    }
                }
            }
            float currentScaleY = getMatrixScaleY();
            if (currentScaleY < 1.7) {
                scaleX = x;
                scaleY = y;
                zoomAnimate(currentScaleY, 1.9f);
            }
            invalidate();
            return true;
        }

        private void remove(int x, int y) {
            for (Point point : list) {
                if (point.x == x && point.y == y) {
                    list.remove(point);
                    return;
                }
            }
        }
    });

    public void setData(int row, int column) {
        this.row = row;
        this.column = column;
        init();
        invalidate();
    }


    private float dip2px(float value) {
        return getResources().getDisplayMetrics().density * value;
    }
}
