package com.example.narek.exam3;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Narek on 4/20/16.
 */
public class DrawView extends View {

    private  int radius = 20;

    List<Circle> circles = new ArrayList<>();
    private Paint mPaint;
    ViewListener viewListener;
    List<Image> images;
    Rect rect;
    private boolean lines = true;
    Paint linePaint;
    Circle curCircle;
    boolean active = false;
    private int index = -1;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    public boolean isLines() {
        return lines;
    }

    public void setLines(boolean lines) {
        this.lines = lines;
    }

    private void initView() {
        images = new ArrayList<>();
        linePaint = new Paint();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rect = new Rect();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("log", "list size " + circles.size());
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < circles.size(); i++) {
                    if (circles.get(i).containsPoint(event.getX(pointerIndex), event.getY(pointerIndex))) {
                        curCircle = circles.get(i);
                        index = circles.get(i).getId();
                        active = true;
                        invalidate();
                        return true;
                    }
                }
                Random random = new Random();
                int tmp = random.nextInt(70);
                int randomColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

                radius = tmp + 20;
                if (!active) {
                    final Circle circle = new Circle( pointerId,event.getX(), event.getY(), radius, randomColor);
                    circles.add(pointerId, circle);
                    curCircle = circle;
                    index = circle.getId();
                    final int curRadius = (int) circle.getRadius();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    ValueAnimator animator = ValueAnimator.ofFloat(0, curRadius);
                    animator.setDuration(300);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            circle.setRadius((Float) animation.getAnimatedValue());
                            invalidate();
                        }
                    });
                    animator.start();
                }

            case MotionEvent.ACTION_POINTER_DOWN: {


                break;
            }
            case MotionEvent.ACTION_MOVE: {
                curCircle.setCenterX(event.getX());
                curCircle.setCenterY(event.getY());
                break;
            }
            case MotionEvent.ACTION_UP:
                if (event.getEventTime() - event.getDownTime() >= 1000 && index !=-1) {
                    circles.remove(index);
                }
                if (event.getEventTime() - event.getDownTime() >= 1000 && index !=-1) {
                    for (int i = 0; i < images.size(); i++) {
                        if (images.get(i).containsPoint(event.getX(pointerIndex), event.getY(pointerIndex))) {
                            images.remove(i);
                            return false;
                        }
                    }
                }


                active = false;
                curCircle = null;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
        }

        invalidate();


        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewListener.onSizeChanged(w, h);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    || (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap createRoundedBitmap(Bitmap source) {
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
        RectF rectF = new RectF(rect);
        float roundDp = 50;

        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundDp, roundDp, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, null, rect, paint);
        return result;
    }


    public Bitmap asBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap drawingCache = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);
        return drawingCache;
    }


    public void addImage(Bitmap bitmap, float left, float top, float right, float bottom) {
        Image image = new Image(createRoundedBitmap(bitmap), left, top, (int) right, (int) bottom);
        images.add(image);
        invalidate();
    }

    public void initListeners(ViewListener viewListener) {
        this.viewListener = viewListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (int size = circles.size(), i = 0; i < size; i++) {
            Circle circle = circles.get(i);
            if (circle != null) {
                mPaint.setColor(circle.getColor());
                canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), mPaint);
            }
        }

        if (isLines()) {

            for (int i = 0; i < getWidth(); i += 10) {
                canvas.drawLine(i, 0, i, getHeight(), linePaint);
            }

            for (int i = 0; i < getHeight(); i += 10) {
                canvas.drawLine(0, i, getWidth(), i, linePaint);
            }
        }

        for (int i = 0; i < images.size(); i++) {
            rect.set((int) images.get(i).point.x, (int) images.get(i).point.y, images.get(i).right, images.get(i).bottom);
            if (!images.get(i).remove) {
                float starty = (images.get(i).bottom - images.get(i).point.y- images.get(i).bitmap.getHeight())/2;
                rect.set(rect.left, rect.top + (int) starty, rect.right, (int) (rect.bottom - starty));

                canvas.drawBitmap(images.get(i).bitmap, null, rect, null);

            }
        }

    }

    private class Image  {
        private Bitmap bitmap;
        private PointF point;
        private int right;
        private int bottom;
        private boolean remove = false;

        public PointF getPoint() {
            return point;
        }
        public void setPoint(PointF point) {
            this.point = point;
        }
        public Bitmap getBitmap() {
            return bitmap;
        }
        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
        public Image(Bitmap bitmap, float x, float y, int right, int bottom) {
            point = new PointF(x, y);
            this.bitmap = bitmap;
            this.right = right;
            this.bottom = bottom;
        }

        private boolean containsPoint(float x, float y) {
            return x <= right && x >= point.x && y <= bottom && y >= point.y;
        }


    }

}