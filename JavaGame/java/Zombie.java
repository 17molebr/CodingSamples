import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Zombie extends Moving {
    private int actionPeriod;
    public Zombie(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod){
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;

    }
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this, world, imageStore, 0 ),
                this.getAnimationPeriod());
    }

    public void executeZombieActivity(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Optional<Entity> zombieTarget = world.findNearest(this.getPosition(),
                MinerNotFull.class);
        if (zombieTarget.isPresent() && this.moveToMinerNotFull(world, zombieTarget.get(), scheduler)){
                this.spawnZombie(world, scheduler, imageStore, zombieTarget.get());
        }

            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.actionPeriod);
        }





    public boolean moveToMinerNotFull(WorldModel world,
                                 Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        } else {
            AstarPathing pathing2 = new AstarPathing();
            //SingleStepPathingStrategy pathing2 = new SingleStepPathingStrategy();
            Predicate<Point> canPassThrough = p -> {
                Optional<Entity> occupant = world.getOccupant(p);
                if (occupant.isPresent()) {
                    return false;
                }
                return true;
            };
            BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);
            List<Point> nextPosts = pathing2.computePath(this.position, target.getPosition(), canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
            if(nextPosts == null){
                return false;
            }
            if (nextPosts.size() == 0) {
                return false;
            } else{
                world.moveEntity(this,nextPosts.get(1));
            }

        }
        return false;
    }

    public void spawnZombie(WorldModel world, EventScheduler scheduler, ImageStore imageStore, Entity target){

            world.entities.remove(target);
            Zombie z = new Zombie("zombie new",new Point(this.getPosition().x-1,this.getPosition().y),imageStore.getImageList("zombie"),10, 6);
            world.addEntity(z);
            z.scheduleActions(scheduler, world, imageStore);




    }
}
