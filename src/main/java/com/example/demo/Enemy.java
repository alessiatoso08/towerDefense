package com.example.demo;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Enemy {
    public double x, y;
    public static final double RENDER_W = 72;
    public static final double RENDER_H = 72;

    private static final int MAX_HP = 20;
    private int hp = MAX_HP;

    public enum State{
        WALKING,
        ATTACKING,
        DONE;
    }
    private State state = State.WALKING;

    private boolean killCounted = false;
    public boolean isDead = false;

    public final String type;
    private final double speed;
    private final double attackTriggerX;

    private final SpriteAnimation walkAnim;
    private final SpriteAnimation attackAnim;
    private SpriteAnimation current;

    private long attackStartMs = 0;
    private static final long ATTACK_DURATION_MS = 600;

    public Enemy(double x, double y, String type, Image walkSheet, int walkFrames, Image attackSheet, int attackFrames, double speed, double attackTriggerX) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.speed = speed;
        this.attackTriggerX = attackTriggerX;
        walkAnim = new SpriteAnimation(walkSheet, walkFrames, 140);
        attackAnim = new SpriteAnimation(attackSheet, attackFrames, 100);
        attackAnim.setLoop(true);
        current = walkAnim;
    }

    public void update(){
        if (isDead){
            return;
        }
        switch (state){
            case WALKING -> {
                x -= speed;
                current.update();
                if(x <= attackTriggerX){
                    state = State.ATTACKING;
                    current = attackAnim;
                    attackAnim.reset();
                    attackStartMs = System.currentTimeMillis();
                }
            }
            case ATTACKING -> {
                current.update();
                if(System.currentTimeMillis() - attackStartMs > ATTACK_DURATION_MS){
                    state = State.DONE;
                }
            }
            case DONE -> { /* GamePane lo rivela e avanza livello */}
        }
    }

    public void hit(){
        if(isDead || state == State.ATTACKING || state == State.DONE){
            return;
        }
        hp--;
        if(hp <= 0){
            isDead = true;
        }
    }

    public boolean isJustKilled(){
        if (isDead && !killCounted){
            killCounted = true;
            return true;
        }
        return false;
    }

    public void draw(GraphicsContext gc){
        if (isDead){
            return;
        }
        current.draw(gc, x, y, RENDER_W, RENDER_H);
        if (state == State.WALKING){
            gc.setFill(Color.web("#500"));
            gc.fillRect(x, y-10, RENDER_W, 5);
            gc.setFill(Color.LIMEGREEN);
            gc.fillRect(x, y-10, RENDER_W*hp/MAX_HP, 5);
        }
    }

    public double getCenterX(){
        return x + RENDER_W/2;
    }
    public double getCenterY(){
        return y + RENDER_H/2;
    }
    public Rectangle2D getBounds(){
        return new Rectangle2D(x+6, y+6, RENDER_W-12, RENDER_H-12);
    }

    public boolean isDead(){
        return isDead;
    }
    public boolean isAttacking(){
        return state == State.ATTACKING;
    }
    public boolean isDone(){
        return state == State.DONE;
    }
}