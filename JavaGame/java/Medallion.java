import processing.core.PImage;

import java.util.List;

public class Medallion extends Entity{
    private int actionPeriod;
    public Medallion(String id, Point position, List<PImage> images, int actionPeriod ){
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.actionPeriod);
    }

    public void executeMedallionAction(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        world.setBackground(new Point(this.getPosition().x-1,this.getPosition().y), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        world.setBackground(new Point(this.getPosition().x+1,this.getPosition().y), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        world.setBackground(new Point(this.getPosition().x,this.getPosition().y+1), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        world.setBackground(new Point(this.getPosition().x,this.getPosition().y-1), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        world.setBackground(new Point(this.getPosition().x+1,this.getPosition().y-1), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        world.setBackground(new Point(this.getPosition().x-1,this.getPosition().y+1), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        world.setBackground(new Point(this.getPosition().x-1,this.getPosition().y-1), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        world.setBackground(new Point(this.getPosition().x+1,this.getPosition().y+1), new Background("bloodybackgroud", imageStore.getImageList("bloodygrass")));
        Zombie z = new Zombie("zombie",new Point(this.getPosition().x+1,this.getPosition().y),imageStore.getImageList("zombie"),10, 6);
        world.addEntity(z);
        z.scheduleActions(scheduler, world, imageStore);


    }


}
