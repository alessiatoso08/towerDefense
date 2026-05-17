package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


public class Archer {
    public static final double W = 72;
    public static final double H = 72;

    public final double x, y;
    private static final int  MAX_ARROWS     = 10;
    private static final long SHOOT_INTERVAL = 900; // ms

    private int     arrowsLeft = MAX_ARROWS;
    private long    lastShot   = 0;
    public  boolean dead       = false;

    private final SpriteAnimation shootAnim; // 13 frame loop
    public  final List<Bullet>    bullets    = new ArrayList<>();

    public Archer(double x, double y, Image shootSheet) {
        this.x = x; this.y = y;
        shootAnim = new SpriteAnimation(shootSheet, 13, 90);
        shootAnim.setLoop(true);
    }

    public void update(List<Enemy> enemies) {
        if (dead) return;
        shootAnim.update();

        if (arrowsLeft <= 0) { dead = true; return; }

        long now = System.currentTimeMillis();
        if (now - lastShot > SHOOT_INTERVAL) {
            // Nemico più vicino alla torre (x minima)
            Enemy target = enemies.stream()
                    .filter(e -> !e.isDead())
                    .min((a, b) -> Double.compare(a.x, b.x))
                    .orElse(null);
            if (target != null) {
                bullets.add(new Bullet(x + W/2, y + H/2,
                        target.getCenterX(), target.getCenterY()));
                arrowsLeft--;
                lastShot = now;
            }
        }

        bullets.removeIf(b -> b.dead);
        bullets.forEach(Bullet::update);
    }

    public void draw(GraphicsContext gc) {
        if (dead) return;
        shootAnim.draw(gc, x, y, W, H);
        // Barra frecce
        gc.setFill(Color.web("#1a1a1a", 0.7));
        gc.fillRect(x, y - 10, W, 6);
        gc.setFill(Color.GOLD);
        gc.fillRect(x, y - 10, W * arrowsLeft / MAX_ARROWS, 6);
        bullets.forEach(b -> b.draw(gc));
    }

}

