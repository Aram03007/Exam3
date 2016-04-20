package com.example.narek.exam3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Narek on 4/20/16.
 */
public class AnimationView extends View {

    private BitmapPointer bitmapPointer;
    private int alfa;
    private float scaleFactor;
    ViewListener viewListener;


    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmapPointer == null) return;
        Rect rect = new Rect();
        scaleFactor = bitmapPointer.getBitmap().getWidth() / (bitmapPointer.getBitmap().getHeight() * 1f);
        rect.set((int) bitmapPointer.getPoint().x - alfa, (int) (bitmapPointer.getPoint().y - alfa * scaleFactor),
                (int) bitmapPointer.getPoint().x + 50 + alfa, (int) (bitmapPointer.getPoint().y + 50 + alfa * scaleFactor));

        canvas.drawBitmap(bitmapPointer.bitmap, null, rect, null);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void startAnimation(Bitmap bitmap, float x, float y) {
        this.bitmapPointer = new BitmapPointer(bitmap, x, y);

        ValueAnimator animatorBitmap = ValueAnimator.ofInt(0, 100);
        animatorBitmap.setDuration(700);
        animatorBitmap.setInterpolator(new DecelerateInterpolator());
        animatorBitmap.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alfa = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        animatorBitmap.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(bitmapPointer == null) return;

                viewListener.onDrawEnd(bitmapPointer.point.x - alfa, bitmapPointer.point.y - alfa * scaleFactor,
                        bitmapPointer.point.x + 200 + alfa, bitmapPointer.point.y + 200 + alfa * scaleFactor);
            }
        });

        animatorBitmap.start();

        bitmapPointer.bitmap = DrawView.createRoundedBitmap(bitmapPointer.bitmap);
    }


    public void init(ViewListener viewListener) {
        this.viewListener = viewListener;
    }



    public void clear() {
        bitmapPointer = null;
        invalidate();
    }

    public class BitmapPointer {
        private Bitmap bitmap;
        private PointF point;

        public PointF getPoint() {
            return point;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public BitmapPointer(Bitmap bitmap, float x, float y) {
            point = new PointF(x, y);
            this.bitmap = bitmap;
        }
    }
}