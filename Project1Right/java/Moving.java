import processing.core.PImage;

import java.util.List;

public abstract class Moving extends Entity {
    protected int animationPeriod;

    public Moving(String id, Point position, List<PImage> images, int animationPeriod){
        super(id, position, images);
        this.animationPeriod =animationPeriod;
    }


    public int getAnimationPeriod(){ return animationPeriod;}
}



