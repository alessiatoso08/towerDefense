package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePane extends Pane {

    static final int W = 800, H = 450;

    //  Torre
    private static final double TOWER_X1 = 270, TOWER_X2 = 530;
    private static final double TOWER_Y1 = 55, TOWER_Y2 = 185;

    private static final double ATTACK_TRIGGER_X = 285;
    private static final double GROUND_Y = 345;

    //  Monete
    private static final int MONETE_INIZIO = 180;
    private static final int COSTO_ARCIERE = 30;
    private static final int RICOMPENSA_NEMICO = 90;
    private static final int BONUS_LIVELLO = 150;
    private int monete = MONETE_INIZIO;

    private final Canvas canvas = new Canvas(W, H);
    private final GraphicsContext gc;

    //  Assets
    private Image bgImg;
    private Image princessIdleImg, princessCastImg;
    private Image lucciolaShootImg;
    private Image golemWalkImg, golemAttackImg;
    private Image spiritoWalkImg, spiritoAttackImg;

    // Entità
    private Princess princess;
    private final List<Archer> archers = new ArrayList<>();
    private final List<Enemy> enemies = new CopyOnWriteArrayList<>();

    // Stato gioco
    private int level = 1;
    private int totalEnemies, enemiesSpawned;
    private long lastSpawnMs, spawnIntervalMs;
    private final Random rng = new Random();

    private boolean waitingLevelUp = false;
    private long levelUpDelayMs = 0;
    private static final long LEVEL_UP_PAUSE = 800;

    private enum GameState { PLAYING, WIN, LOSE, NEXT_LEVEL }
    private GameState gameState = GameState.PLAYING;
    private long stateTime = 0;

    private String feedback = "";
    private long feedbackTime = 0;

    private final List<MoneyPopup> popups = new ArrayList<>();

    private static class MoneyPopup {
        String text; double x, y; long born; Color color;

        MoneyPopup(String t, double x, double y, Color c) {
            text = t;
            this.x = x;
            this.y = y;
            born = System.currentTimeMillis();
            color = c;
        }

        boolean alive() {
            return System.currentTimeMillis() - born < 1300;
        }

        void update() {
            y -= 1.0;
        }

        void draw(GraphicsContext gc) {
            double a = Math.max(0, 1.0 - (System.currentTimeMillis() - born) / 1300.0);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            gc.setFill(color.deriveColor(0, 1, 1, a));
            gc.fillText(text, x, y);
        }
    }

    public GamePane() {
        getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();
    }

    public void startGame() {
        loadAssets();
        initLevel(1);
        canvas.setOnMouseClicked(this::onMouseClicked);

        new AnimationTimer() {
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    private void loadAssets() {
        bgImg = res("sfondo.png");
        princessIdleImg = res("princess_idle.png");
        princessCastImg = res("princess_cast.png");
        lucciolaShootImg = res("lucciola-tiraFreccia.png");
        golemWalkImg = res("golemMuschio-walk.png");
        golemAttackImg = res("golemMuschio-attack.png");
        spiritoWalkImg = res("spiritoDiNebbia-cammina.png");
        spiritoAttackImg = res("spiritoDiNebbia-attacca.png");

        princess = new Princess(princessIdleImg, princessCastImg);
    }

    private Image res(String n) {
        return new Image(getClass().getResourceAsStream("/assets/" + n));
    }

    private void initLevel(int lvl) {
        level = lvl;
        int[] counts = {4, 8, 16, 30};
        totalEnemies = counts[Math.min(lvl - 1, 3)];
        enemiesSpawned = 0;
        spawnIntervalMs = Math.max(900, 3200 - (lvl - 1) * 550L);
        lastSpawnMs = System.currentTimeMillis();

        archers.clear();
        enemies.clear();
        popups.clear();

        waitingLevelUp = false;
        gameState = GameState.PLAYING;

        feedback = "Livello " + lvl + "  —   " + monete;
        feedbackTime = System.currentTimeMillis();
    }
}