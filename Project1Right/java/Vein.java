import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Vein extends Entity {
    private int actionPeriod;


    public Vein(Point position, List<PImage> images, String id, int actionPeriod){
        super(id, position, images);
        this.actionPeriod= actionPeriod;
    }




    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
    }

    public void executeVeinActivity(WorldModel world,
                                    ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround( this.position);

        if (openPt.isPresent())
        {
           Entity ore = new Ore(Functions.ORE_ID_PREFIX + this.id,
                    openPt.get(),
                    imageStore.getImageList( Functions.ORE_KEY),Functions.ORE_CORRUPT_MIN +
                            Functions.rand.nextInt(Functions.ORE_CORRUPT_MAX - Functions.ORE_CORRUPT_MIN));
            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent( this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
    }





}
