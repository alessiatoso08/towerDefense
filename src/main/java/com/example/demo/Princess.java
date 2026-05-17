package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class Princess {

    public static final double X    = 48;
    public static final double Y    = 330;
    public static final double W    = 72;
    public static final double H    = 72;

    private enum State { IDLE, CASTING }
    private State state = State.IDLE;

    private final SpriteAnimation idleAnim;
    private final SpriteAnimation castAnim;

    public Princess(Image idleSheet, Image castSheet) {
        idleAnim = new SpriteAnimation(idleSheet, 2, 500);
        castAnim = new SpriteAnimation(castSheet, 9, 90);
        castAnim.setLoop(false);
    }


    public void triggerCast() {
        if (state == State.CASTING) return;
        state = State.CASTING;
        castAnim.reset();
    }

    public void update() {
        if (state == State.IDLE) {
            idleAnim.update();
        } else {
            castAnim.update();
            if (castAnim.isFinished()) {
                state = State.IDLE;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        if (state == State.IDLE) {
            idleAnim.draw(gc, X, Y, W, H);
        } else {
            castAnim.draw(gc, X, Y, W, H);
        }
    }
}
