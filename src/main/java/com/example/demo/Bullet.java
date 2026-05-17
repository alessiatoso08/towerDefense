package com.example.demo;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Freccia sparata dalla lucciola verso il nemico più vicino. */
public class Bullet {
    public double x, y;
    public final double vx, vy;
    private final double angle;
    public boolean dead = false;
    private static final double SPEED = 7.0;

    public Bullet(double sx, double sy, double tx, double ty) {
        this.x = sx; this.y = sy;
        double dx = tx -sx, dy = ty - sy;
        double d = Math.hypot(dx,dy);
        vx = dx / d * SPEED;
        vy = dy / d * SPEED;
        angle = Math.atan2(dy,dx);
    }

    public void update(){
        x += vx; y += vy;
        if (x < -20 || x > 820 || y < -20 || y > 490){
            dead = true;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(Math.toDegrees(angle));
        gc.setFill(Color.BURLYWOOD);
        gc.fillRect(-12, -1.5, 16, 3);
        gc.setFill(Color.DARKGOLDENROD);
        gc.fillPolygon(new double[]{4,12,4}, new double[]{-4,0,4}, 3);
        gc.setFill(Color.web("#fff8",0.6));
        gc.fillPolygon(new double[]{-12,-6,-12}, new double[]{-3,0,3}, 3);
        gc.restore();
    }

    public Rectangle2D getBounds(){
        return new Rectangle2D(x-12, y-4, 24, 8);
    }
}