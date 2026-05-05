import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Creatura {
    protected double x, y, velocita;
    protected double purificazione = 0;
    protected int frameAttuale;
    protected Image spriteSheet;

    public Creatura(int x, int y, Image img){
        this.x = x;
        this.y = y;
        this.spriteSheet = img;
    }

    public void update(){
        x += velocita;
        if(System.currentTimeMillis() % 120 == 0){ //ogni 120 millisecondi fai qualcosa
            frameAttuale = (frameAttuale + 1) % 4;
        }
    }

    public void draw(GraphicsContext gc){
        gc.drawImage(spriteSheet, frameAttuale * SpriteConfig);
    }

}
