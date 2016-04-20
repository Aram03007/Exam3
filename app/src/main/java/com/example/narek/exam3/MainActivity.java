package com.example.narek.exam3;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity  extends AppCompatActivity implements ViewListener {


    DrawView drawView;
    private boolean isLayoutVisible = false;
    private FrameLayout showHideLayout;
    AnimationView animationView;
    List<String> imagePaths = new ArrayList<>();
    int canvasHeight;
    int canvasWidth;
    SubActionButton photo;
    SubActionButton circle;
    FloatingActionMenu fam;
    FloatingActionButton fab;

    GridLayoutManager layoutManager;
    Bitmap bitmap = null;
    boolean bitmapIsReady = false;
    boolean isAnimationEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        CustomAdapter adapter = new CustomAdapter(imagePaths, this);
        layoutManager = new GridLayoutManager(this, 3, LinearLayout.VERTICAL, false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        assert recyclerView != null;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        showHideLayout = (FrameLayout) findViewById(R.id.show_hide_layout);

        drawView = (DrawView) findViewById(R.id.draw_view);
        assert drawView != null;
        drawView.initListeners(this);

        animationView = (AnimationView) findViewById(R.id.bubble_animationView);
        assert animationView != null;
        animationView.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView icon1 = new ImageView(this);
        icon1.setImageResource(R.drawable.images);
        ImageView icon2 = new ImageView(this);
        icon2.setImageResource(R.drawable.images);
        ImageView icon3 = new ImageView(this);
        icon3.setImageResource(R.drawable.images);

        fab = new FloatingActionButton.Builder(this)
                .setContentView(icon1)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        photo = itemBuilder.setContentView(icon2).build();
        circle = itemBuilder.setContentView(icon3).build();
        circle.setTag("addCircle");
        photo.setTag("photo");

        fam = new FloatingActionMenu.Builder(this)
                .addSubActionView(photo)
                .addSubActionView(circle)
                .attachTo(fab)
                .build();


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveLayout();
            }
        });
    }


    private void init() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;

        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);

        int count = cursor != null ? cursor.getCount() : 0;

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imagePaths.add(cursor.getString(dataColumnIndex));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.about:
                Intent intent = new Intent(this, About.class);
                startActivity(intent);


                return true;
            case R.id.export:
                drawView.setLines(false);

                Bitmap bitmap = drawView.asBitmap();


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null));
                final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");

                startActivity(Intent.createChooser(shareIntent, "Select application to share"));
                Share share = new Share();
                share.setmNotifyManager((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                share.setmBuilder(new android.support.v7.app.NotificationCompat.Builder(MainActivity.this));
                share.getmBuilder().setContentTitle("Sharing")
                        .setContentText("Sharing in progress")
                        .setSmallIcon(R.drawable.icon);


                share.execute();


                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void hideLayout() {
        if (bitmapIsReady) {
            animationView.startAnimation(bitmap, canvasWidth / 2 - 100,
                    canvasHeight / 2 - 100);

            bitmapIsReady = false;
        }
    }

    private void moveLayout() {
        int currentSize = canvasHeight / 2;
        AnimatorSet animatorSet = new AnimatorSet();

        if (isLayoutVisible) {
            animatorSet.playTogether(
                    ObjectAnimator.ofPropertyValuesHolder(drawView, PropertyValuesHolder.
                            ofFloat("translationY", -currentSize, 0)).setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("translationY", -currentSize, 0))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(photo,
                            PropertyValuesHolder.ofFloat("translationY", -currentSize, 0))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(circle,
                            PropertyValuesHolder.ofFloat("translationY", -currentSize, 0))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(fam,
                            PropertyValuesHolder.ofFloat("translationY", -currentSize, 0))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(fab,
                            PropertyValuesHolder.ofFloat("translationY", -currentSize, 0))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(showHideLayout,
                            PropertyValuesHolder.ofFloat("translationY", 0, currentSize))
                            .setDuration(500)
            );
        } else {
            animatorSet.playTogether(ObjectAnimator.ofPropertyValuesHolder(
                    drawView,
                    PropertyValuesHolder.ofFloat("translationY", 0, -currentSize))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("translationY", 0, -currentSize))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(
                            photo,
                            PropertyValuesHolder.ofFloat("translationY", 0, -currentSize))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(
                            fam,
                            PropertyValuesHolder.ofFloat("translationY", 0, -currentSize))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(
                            fab,
                            PropertyValuesHolder.ofFloat("translationY", 0, -currentSize))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(
                            circle,
                            PropertyValuesHolder.ofFloat("translationY", 0, -currentSize))
                            .setDuration(500),
                    ObjectAnimator.ofPropertyValuesHolder(
                            showHideLayout,
                            PropertyValuesHolder.ofFloat("translationY", currentSize, 0))
                            .setDuration(500)
            );
        }

        animatorSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                photo.setEnabled(false);
                fab.setEnabled(false);

                isAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                photo.setEnabled(true);
                fab.setEnabled(true);
                isAnimationEnd = true;
                if (!isLayoutVisible) hideLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animatorSet.start();
        isLayoutVisible = !isLayoutVisible;
    }

    @Override
    public void onDrawEnd(float left, float top, float right, float bottom) {
        drawView.addImage(bitmap, left, top, right, bottom);
        animationView.clear();
        animationView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        if (!isLayoutVisible) return;

        moveLayout();
        animationView.setVisibility(View.VISIBLE);

        AsyncTask<Integer, String, Bitmap> asyncTask = new AsyncTask<Integer, String, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Integer... params) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePaths.get(params[0]), options);
                options.inSampleSize = DrawView.calculateInSampleSize(options, canvasWidth, canvasHeight);
                options.inJustDecodeBounds = false;

                return BitmapFactory.decodeFile(imagePaths.get(params[0]), options);
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                bitmap = bmp;
                bitmapIsReady = true;

                if (isAnimationEnd) hideLayout();
            }
        };

        asyncTask.execute(position);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        layoutManager.setSpanCount(canvasWidth / 150);
        ViewGroup.LayoutParams params = showHideLayout.getLayoutParams();
        params.height = canvasHeight / 2;
        showHideLayout.setLayoutParams(params);
    }

}
