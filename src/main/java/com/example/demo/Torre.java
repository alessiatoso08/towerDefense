package com.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Torre {
    double x, y;
    double range = 120;

    public Torre(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.GOLD);
        gc.fillOval(x - 20, y - 20, 40, 40);

        gc.setStroke(Color.web("white", 0.2));
        gc.strokeOval(x - range, y - range, range * 2, range * 2);
    }
}
