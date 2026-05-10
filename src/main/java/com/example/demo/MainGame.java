package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.*;

public class MainGame extends Application {

    List<Creatura> creature = new ArrayList<>();
    List<Torre> torri = new ArrayList<>();

    PrincipessaElara elara;
    Luogo luogo = new Luogo();

    Image imgSpirito = new Image("file:spirito.png");
    Image imgLucciola = new Image("file:lucciola.png");
    Image imgGolem = new Image("file:golem.png");
    Image imgElara = new Image("file:elara.png");

    int energia = 100;
    int polvere = 200;

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        elara = new PrincipessaElara(imgElara);

        canvas.setOnMouseClicked(e -> {
            if (polvere >= 50) {
                torri.add(new Torre(e.getX(), e.getY()));
                polvere -= 50;
            }
        });

        new AnimationTimer() {
            public void handle(long now) {
                update();
                render(gc);
            }
        }.start();

        // spawn
        new Thread(() -> {
            Random r = new Random();
            while (true) {
                try { Thread.sleep(3000); } catch (Exception e) {}

                int tipo = r.nextInt(3);

                if (tipo == 0)
                    creature.add(new SpiritoNebbia(0, 280, imgSpirito));
                else if (tipo == 1)
                    creature.add(new Lucciola(0, 200, imgLucciola));
                else
                    creature.add(new GolemMuschio(0, 300, imgGolem));
            }
        }).start();

        stage.setScene(new Scene(new StackPane(canvas)));
        stage.setTitle("Luminia");
        stage.show();
    }

    private void update() {

        for (int i = 0; i < creature.size(); i++) {
            Creatura c = creature.get(i);
            c.update();

            for (Torre t : torri) {
                if (Math.hypot(c.x - t.x, c.y - t.y) < t.range) {
                    c.purificazione += 0.3;
                }
            }

            if (c.purificazione >= 100) {
                creature.remove(i);
                polvere += 15;
                i--;
            } else if (c.x > 780) {
                creature.remove(i);
                energia -= 10;
                i--;
            }
        }
    }

    private void render(GraphicsContext gc) {
        luogo.draw(gc);

        torri.forEach(t -> t.draw(gc));
        creature.forEach(c -> c.draw(gc));
        elara.draw(gc);

        gc.fillText("Energia: " + energia, 20, 20);
        gc.fillText("Polvere: " + polvere, 20, 40);
    }

    public static void main(String[] args) {
        launch();
    }
}