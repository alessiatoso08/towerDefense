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


}
