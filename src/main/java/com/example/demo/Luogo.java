package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Luogo {

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.DARKSEAGREEN);
        gc.fillRect(0, 0, 800, 600);
    }
}