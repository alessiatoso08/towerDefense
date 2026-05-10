package com.example.demo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PrincipessaElara {
    private double x = 700, y = 280;
    private Image sprite;

    public PrincipessaElara(Image img) {
        this.sprite = img;
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(sprite, x, y, 64, 64);
    }
}