import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Principessa extends Creatura{
   private double x = 700;
   private double y = 800;
   private Image sprite;

    public Principessa(int x, int y, Image img, Image sprite) {
        super(x, y, img);
        this.sprite = sprite;
    }

    public void draw(GraphicsContext gc){
        gc.drawImage(sprite,x,y,64,64);
    }
}
