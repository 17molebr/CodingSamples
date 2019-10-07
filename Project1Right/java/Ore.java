import processing.core.PImage;

import java.util.List;

public class Ore extends Entity {
    private int actionPeriod;


    public Ore(String id, Point position, List<PImage> images, int actionPeriod){
        super(id, position, images);
        this.actionPeriod =actionPeriod;
    }


    public void executeOreActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos = super.getPosition();  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents( this);

        Entity blob = new OreBlob(this.id + Functions.BLOB_ID_SUFFIX,
                pos,imageStore.getImageList( Functions.BLOB_KEY), this.actionPeriod / Functions.BLOB_PERIOD_SCALE,
                Functions.BLOB_ANIMATION_MIN +
                        Functions.rand.nextInt(Functions.BLOB_ANIMATION_MAX - Functions.BLOB_ANIMATION_MIN))
                ;

        world.addEntity( blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
    }


}
