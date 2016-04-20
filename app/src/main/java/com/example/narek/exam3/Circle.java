package com.example.narek.exam3;

/**
 * Created by Narek on 4/20/16.
 */
public class Circle {

    private float centerX;
    private float centerY;
    private int color;
    private float radius;
    private int id;

    public int getId() {
        return id;
    }


    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Circle(int id, float centerX, float centerY, float radius, int color) {
        this.id = id;
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
        this.color = color;
    }

    public int getColor() {

        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }


    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public boolean containsPoint(float x, float y) {
        float deltaX = centerX - x;
        float deltaY = centerY - y;
        return radius >= Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }


}

