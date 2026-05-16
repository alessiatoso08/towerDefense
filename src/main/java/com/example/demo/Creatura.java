package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Creatura { //prova 2
    protected double x, y, velocita;
    protected double purificazione = 0;
    protected int frameAttuale = 0;
    protected Image spriteSheet;

    public Creatura(double x, double y, Image img) {
        this.x = x;
        this.y = y;
        this.spriteSheet = img;
    }

    public void update() {
        x += velocita;

        if (System.currentTimeMillis() % 120 == 0) {
            frameAttuale = (frameAttuale + 1) % 4;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(spriteSheet,
                frameAttuale * SpriteConfig.TILE_SIZE, 0,
                SpriteConfig.TILE_SIZE, SpriteConfig.TILE_SIZE,
                x, y,
                SpriteConfig.TILE_SIZE, SpriteConfig.TILE_SIZE);

        // barra purificazione
        gc.fillRect(x, y - 10, (purificazione / 100.0) * 64, 5);
    }
}
