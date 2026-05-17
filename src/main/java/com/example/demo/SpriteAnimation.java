package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteAnimation {
    private final Image  sheet;
    private final int    frameCount;
    private final int    frameW;
    private final int    frameH;
    private long         frameDurationMs;
    private int          currentFrame  = 0;
    private long         lastFrameTime = 0;
    private boolean      loop     = true;
    private boolean      finished = false;

    public SpriteAnimation(Image sheet, int frameCount, long frameDurationMs) {
        this.sheet           = sheet;
        this.frameCount      = frameCount;
        this.frameDurationMs = frameDurationMs;
        this.frameW = (int)(sheet.getWidth()  / frameCount);
        this.frameH = (int) sheet.getHeight();
    }

    public void update() {
        if (finished) return;
        long now = System.currentTimeMillis();
        if (now - lastFrameTime > frameDurationMs) {
            currentFrame++;
            lastFrameTime = now;
            if (currentFrame >= frameCount) {
                if (loop) currentFrame = 0;
                else { currentFrame = frameCount - 1; finished = true; }
            }
        }
    }

    public void draw(GraphicsContext gc, double x, double y, double w, double h) {
        draw(gc, x, y, w, h, false);
    }

    public void draw(GraphicsContext gc, double x, double y, double w, double h, boolean flipX) {
        if (flipX) {
            gc.save();
            gc.translate(x + w, y);
            gc.scale(-1, 1);
            gc.drawImage(sheet, (double)currentFrame * frameW, 0, frameW, frameH, 0, 0, w, h);
            gc.restore();
        } else {
            gc.drawImage(sheet, (double)currentFrame * frameW, 0, frameW, frameH, x, y, w, h);
        }
    }

    public void reset()            { currentFrame = 0; finished = false; lastFrameTime = 0; }
    public void setLoop(boolean v) { loop = v; }
    public void setSpeed(long ms)  { frameDurationMs = ms; }
    public boolean isFinished()    { return finished; }
    public int getFrameCount()     { return frameCount; }
}
