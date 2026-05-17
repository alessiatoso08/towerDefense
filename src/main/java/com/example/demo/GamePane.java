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

    private void onMouseClicked(MouseEvent e){
        if (gameState != GameState.PLAYING){
            return;
        }
        double mx = e.getX(), my = e.getY();
        boolean onTower = mx >= TOWER_X1 && mx <= TOWER_X2 && my >= TOWER_Y1 && my <= TOWER_Y2;
        if (onTower){
            if(monete >= COSTO_ARCIERE){
                monete -= COSTO_ARCIERE;
                double ax = mx - Archer.W/2;
                double ay = my - Archer.H/2;
                archers.addx(new Archer(ax, ay, lucciolaShootImg));
                princess.triggerCast();
                popups.add(new MoneyPopup("-" + COSTO_ARCIERE, mx, my-20, Color.ORANGERED));
                feedback = "✨ Lucciola evocata!  " + monete;
            }else{
                feedback = "Servono " + COSTO_ARCIERE " (hai " + monete + ")";
            }
        }else{
            feedback = "Clicca sulla TORRE (costo: " + COSTO_ARCIERE + ")";
        }
        feedbackTime = Ststem.currentTimeMillis();
    }

    private void update(){
        if(gameState == GameState.NEXT_LEVEL){
            if (Ststem.currentTimeMillis() - stateTime > 2500){
                monete += BONUS_LIVELLO;
                initLevel(level + 1);
            }
            return;
        }
        if(gameState != GameState.PLAYING){
            return;
        }
        princess.update();
        spawnEnemies();

        List<Bullet> allBullets = new ArrayList<>();
        archers.forEach(a -> allBullets.addall(a.bullets));

        archers.forEach(a -> a.update(enemies));
        archers.removeIf(a -> a.dead);

        enemies.forEach(Enemy::update);

        for(Bullet b : allBullets){
            if (b.dead){
                continue;
            }
            for (Enemy en : enemies){
                if (!en.isDead() && !en.isAttacking() && b.getBounds().inteersects(en.getBounds())){
                    b.dead = true;
                    en.hit();
                    break;
                }
            }
        }
        for (Enemy en : enemies){
            if (en.isJustKilled()){
                monete += RICOMPENSA_NEMICO;
                popups.add(new MoneyPopup("+" + RICOMPENSA_NEMICO, en.getCenterX() - 20, en.y - 10, Color.GOLD));
            }
        } //riprova
        enemies.removeIf(en -> en.isDead());
        popups.forEach(MoneyPopup::update);
        popups.removeIf(p -> !p.alive());
        if(enemiesSpawned >= totalEnemies && enemies.isEmpty() && !waitingLevelUp){
            if (level < 4){
                gameState = GameState.NEXT_LEVEL;
                stateTime = System.currentTimeMillis();
            }else{
                gameState = GameState.WIN;
                stateTime = System.currentTimeMillis();
            }
        }
    }

    private void spawnEnemies(){
        if(enemiesSpawned >= totalEnemies){
            return;
        }
        long now = System.currentTimeMillis();
        if(now - lastSpawnMs < spawnIntervalMs){
            return;
        }
        lastSpawnMs = now;
        double y = GROUND_Y + rng.nextInt(10) - 5;
        Enemy e = (rng.nextBoolean()) ? new Enemy(W + 10, y, "golem", golemWalkImg, 9, golemAttackImg, 6, 1.0, ATTACK_TRIGGER_X) : new Enemy(W + 10, y - 4, "spirito", spiritoWalkImg, 9, spiritoAttackImg, 6, 1.4, ATTACK_TRIGGER_X);
        enemies.add(e);
        enemiesSpawned++;
    }

    private void render(){
        gc.drawImage(bgImg, 0, 0, W, H);

        princess.draw(gc);
        archers.forEach(a -> a.draw(gc));
        enemies.forEach(e -> e.draw(gc));
        popups.forEach(p -> p.draw(gc));

        drawHUD();

        if (!feedback.isEmpty()){
            long el = System.currentTimeMillis() - feedbackTime;
            if (el < 2500){
                double alpha = Math.max (0, 1.0 - el / 2500.0);
                gc.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
                gc.setFill(Color.color(0, 0, 0, alpha));
                gc.fillText(feedback, W / 2.0 - feedback.length() * 7.5 + 2, H / 2.0 - 48);
                gc.setFill(Color.color(1, 1, 1, alpha));
                gc.fillText(feedback, W / 2.0 - feedback.length() * 7.5, H / 2.0 - 50);
            }else {
                feedback = "";
            }
        }
        switch (gameState){
            case WIN -> dawOverlay("✨ Vittoria!", Color.GOLD);
            case LOSE -> dawOverlay("💔 Game Over!", Color.INDIANRED);
            case NEXT_LEVEL -> dawOverlay("Livello " + level + " superato!", Color.LIGHTGREEN);
        }
    }

    private void drawHUD(){
        gc.setFill(Color.web("#000", 0.60));
        gc.fillRoundRect(8, 8, 218, 88, 10, 10);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.WHITE);
        gc.fillText("Livello: " + level + " / 4", 18, 27);
        gc.fillText("Nemici: " + enemies.size(), 18, 46);
        gc.fillText("Lucciole: " + archers.size(), 18, 65);

        gc.setFill(monete >= COSTO_ARCIERE ? Color.GOLD : Color.TOMATO);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("🪙 " + monete, 18, 85);
    }

    private void drawOverlay(String text, Color color){
        gc.setFill(Color.web("#000", 0.62));
        gc.fillRect(0, 0, W, H);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gc.setFill(color);

        double y = H / 2.0;
        gc.fillText(text, W / 2.0 - text.length() * 8.5, y);
    }
}