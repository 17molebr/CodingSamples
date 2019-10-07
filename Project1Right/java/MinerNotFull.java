import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MinerNotFull extends Moving {
    private int resourceCount;
    private int actionPeriod;
    private int resourceLimit;

    public MinerNotFull(String id, Point position, List<PImage> images, int resourceCount, int actionPeriod, int animationPeriod, int resourceLimit) {
        super(id, position, images, animationPeriod);
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.resourceLimit = resourceLimit;
    }


    public void executeMinerNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        Optional<Entity> notFullTarget = world.findNearest(this.getPosition(),
                Ore.class);
        if (!notFullTarget.isPresent() ||
                !this.moveToNotFull(world, notFullTarget.get(), scheduler) ||
                !this.transformNotFull(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.actionPeriod);
        }

    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                new Animation(this, world, imageStore, 0), this.getAnimationPeriod());
    }

    public boolean transformNotFull(WorldModel world,
                                    EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            MinerFull miner = new MinerFull(this.id, this.position, this.images, this.resourceCount,
                    this.actionPeriod, this.animationPeriod, this.resourceLimit);
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean moveToNotFull(WorldModel world,
                                 Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            this.resourceCount += 1;
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





    public Point nextPositionMiner(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz,
                this.position.y);

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x,
                    this.position.y + vert);

            if (vert == 0 || world.isOccupied( newPos))
            {
                newPos = this.position;
            }
        }

        return newPos;
    }




}
