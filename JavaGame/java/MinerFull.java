import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MinerFull extends Moving{
    private int resourceCount;
    private int actionPeriod;
    private int resourceLimit;

    public MinerFull(String id, Point position, List<PImage> images, int resourceCount, int actionPeriod, int animationPeriod, int resourceLimit){
        super(id, position, images, animationPeriod);
        this.resourceCount = resourceCount;
        this.actionPeriod =actionPeriod;
        this.resourceLimit =resourceLimit;
    }


    public void executeMinerFullActivity( WorldModel world,
                                          ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> fullTarget = world.findNearest( this.position,
                Blacksmith.class);

        if (fullTarget.isPresent() &&
                this.moveToFull(world, fullTarget.get(), scheduler))
        {
            this.transformFull( world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    new Activity(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this, world, imageStore, 0 ),
                this.getAnimationPeriod());
    }

    public void transformFull( WorldModel world,
                               EventScheduler scheduler, ImageStore imageStore)
    {
        MinerNotFull miner = new MinerNotFull(this.id,this.position, this.images, 0,
                this.actionPeriod, this.animationPeriod, this.resourceLimit);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions( scheduler, world, imageStore);
    }

    public boolean moveToFull( WorldModel world,
                               Entity target, EventScheduler scheduler)
    {
        AstarPathing Pathing1 = new AstarPathing();
        Predicate<Point> canPassThrough = p -> !(world.isOccupied(p));
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);



        if (this.position.adjacent(target.getPosition()))
        {
            return true;
        }
        else {
            List<Point> nextPosts = Pathing1.computePath(this.position, target.getPosition(), canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
            if(nextPosts == null){
                return false;
            }
            if(nextPosts.size()==0){
                return false;
            }else{
                Point nextPos = nextPosts.get(1);
                //Point nextPos = this.nextPositionMiner(world, target.getPosition());
                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
    /*
    public Point nextPositionMiner(WorldModel world, Point nestPos)
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
    */

}
