public class Animation implements Action {
    public Moving moving;
    public WorldModel world;
    public ImageStore imageStore;
    public int repeatCount;

    public Animation(Moving moving, WorldModel world, ImageStore imageStore, int repeatCount){
        this.moving = moving;
        this.world= world;
        this.imageStore =imageStore;
        this.repeatCount =repeatCount;
    }

    public void executeAction(EventScheduler Scheduler)
    {
        this.moving.nextImage();
        if (this.repeatCount != 1)

            Scheduler.scheduleEvent(this.moving,
                    new Animation(this.moving, this.world, this.imageStore,
                            Math.max(this.repeatCount - 1, 0)),
                    this.moving.getAnimationPeriod());
    }

}

