package com.example.narek.exam3;

/**
 * Created by Narek on 4/20/16.
 */
public interface ViewListener {
    void onDrawEnd(float left, float top, float right, float bottom);
    void onItemClick(int position);
    void onSizeChanged(int width, int height);
}
