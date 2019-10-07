import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class OreBlob extends Moving{
    public int actionPeriod;

    public OreBlob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod){
        super(id, position, images, animationPeriod);
        this.actionPeriod =actionPeriod;
    }


    public void executeOreBlobActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {

        Optional<Entity> blobTarget = world.findNearest(
                this.position, Vein.class);
        long nextPeriod = this.actionPeriod;

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().getPosition();

            if (this.moveToOreBlob(world, blobTarget.get(), scheduler, imageStore).equals("donePathing"))
            {
                Entity quake = new Quake(tgtPos,
                        imageStore.getImageList(Functions.QUAKE_KEY),0,0);

                world.addEntity( quake);
                nextPeriod += this.actionPeriod;
                quake.scheduleActions( scheduler, world, imageStore);
            }
            if(this.moveToOreBlob(world, blobTarget.get(), scheduler, imageStore).equals("doneChanged")){
                Optional<Entity> blobTarget1 = world.findNearest(
                        this.position, Zombie.class);
                if(this.moveToZombie(world, blobTarget1.get(), scheduler, imageStore).equals("donePathing")){
                    world.removeEntity(this);


                }
            }
        }

        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                nextPeriod);


    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                new Animation( this, world, imageStore,0), this.getAnimationPeriod());
    }

    public String moveToOreBlob( WorldModel world,
                                   Entity target, EventScheduler scheduler,ImageStore imageStore)
    {
        if (this.position.adjacent(target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents( target);
            return "donePathing";
        }
        else {
            //SingleStepPathingStrategy pathing2 = new SingleStepPathingStrategy();
            AstarPathing pathing2 = new AstarPathing();
            Predicate<Point> canPassThrough = p -> !(world.isOccupied(p));
            BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);
            List<Point> nextPosts = pathing2.computePath(this.position, target.getPosition(), canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
            if(nextPosts == null){
                return "notDone";
            }
            if (nextPosts.size() == 0) {
                return "notDone";
            }else{
                Point nextPos = nextPosts.get(1);
                if(world.getBackgroundImage(nextPos).isPresent()&&world.getBackgroundImage(nextPos).get().equals(imageStore.getImageList("bloodygrass").get(0))){
                    world.removeEntity(this);
                    OreBlob b = new OreBlob("bloodblob", nextPos, imageStore.getImageList("bloodyblob"), this.actionPeriod / Functions.BLOB_PERIOD_SCALE,
                            Functions.BLOB_ANIMATION_MIN +
                                    Functions.rand.nextInt(Functions.BLOB_ANIMATION_MAX - Functions.BLOB_ANIMATION_MIN));
                    world.addEntity(b);
                    b.scheduleActions(scheduler, world, imageStore);
                    return "doneChanged";
                }
                world.moveEntity(this, nextPos);

            }
        }


                return "notDone";
            }



    public String moveToZombie( WorldModel world,
                                 Entity target, EventScheduler scheduler,ImageStore imageStore)
    {
        if (this.position.adjacent(target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents( target);
            return "donePathing";
        }
        else {
            //SingleStepPathingStrategy pathing2 = new SingleStepPathingStrategy();
            AstarPathing pathing2 = new AstarPathing();
            Predicate<Point> canPassThrough = p -> !(world.isOccupied(p));
            BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);
            List<Point> nextPosts = pathing2.computePath(this.position, target.getPosition(), canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
            if(nextPosts == null){
                return "notDone";
            }
            if (nextPosts.size() == 0) {
                return "notDone";
            }else{
                Point nextPos = nextPosts.get(1);
                world.moveEntity(this, nextPos);

            }
        }


        return "notDone";
    }



}
