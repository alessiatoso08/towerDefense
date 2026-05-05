import javafx.scene.image.Image;

public class GolemDiMuschio extends Creatura{
    public GolemDiMuschio(int x, int y, Image img) {
        super(x, y, img);
        this.velocita = 0.5;
    }

    @Override
    public void update() {
        super.update();
        purificazione -= 0.1;
    }
}
