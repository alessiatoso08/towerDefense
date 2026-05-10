package com.example.demo;
import javafx.scene.image.Image;

public class GolemMuschio extends Creatura {

    public GolemMuschio(double x, double y, Image img) {
        super(x, y, img);
        this.velocita = 0.5;
    }

    @Override
    public void update() {
        super.update();
        purificazione -= 0.1; // più resistente
    }
}
