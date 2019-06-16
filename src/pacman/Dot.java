package pacman;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Pozywienie dla PacMana, rozszerza Circle
 */
public class Dot extends Circle{
    /**
     * 4 - normalna, radius 2, 10pkt
     * 8 - magiczna, radius 5, miganie, 100pkt
     */
    private int type;
    private final int bigRadius=5;
    private final int smallRadius=2;
    private AnimationTimer timer;
    /**
     * zmiana promienia Magicznej Dot
     */
    private int delta = -1;
    /**
     * punkty za zjedzenie Dot
     */
    private int points;
    private SimpleBooleanProperty paused;

    public Dot(double centerX, double centerY, double radius, Paint fill) {
        super(centerX, centerY, radius, fill);

        paused=new SimpleBooleanProperty(false);
        if(radius==2) {
            type=4;
            points=10;
        }
        else{
            type=8;
            createTimer();
            timer.start();
            points=100;
        }
    }

    /**
     * Utworzenie timer
     */
    private void createTimer(){
        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            /**
             * co 100ms doOneTick
             */
            @Override
            public void handle(long l) {
                if(l-lastUpdate>100000000){
                    doOneTick();
                    lastUpdate = l;
                }
            }
        };
    }

    /**
     * jesli nie jest spauzowany zmień promień o delta
     */
    private void doOneTick() {
        if(!paused.get()) {

            if (this.getRadius() == smallRadius)
                delta = 1;
            else if (this.getRadius() == bigRadius) {
                delta = -1;
            }
            this.setRadius(this.getRadius() + delta);
        }
    }

    public int getType() {
        return type;
    }


    /**
     * Ukryj instancję
     */
    public void hide(){
        this.setVisible(false);
    }

    public SimpleBooleanProperty pausedProperty() {
        return paused;
    }


    public int getPoints() {
        return points;
    }
}
