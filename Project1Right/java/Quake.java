import processing.core.PImage;
import java.util.List;
import java.util.Optional;


public class Quake extends Moving {
    private int actionPeriod;


    public Quake(Point position, List<PImage> images, int animationPeriod, int actionPeriod){
        super("",position, images, animationPeriod);
        this.actionPeriod =actionPeriod;
    }


    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                new Animation(this, world, imageStore, Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }

    public void executeQuakeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents( this);
        world.removeEntity(this);
    }

}
