import processing.core.PImage;

import java.util.List;

public abstract class Entity{
    protected String id;
    protected Point position;
    protected List<PImage> images;
    protected int imageIndex =0;

    public Entity(String id, Point position, List<PImage> images){
        this.id = id;
        this.position = position;
        this.images =images;
    }


    public Point getPosition() {
        return this.position;
    }
    public void setPosition(Point p){ this.position = p; }
    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex);
    }
    void nextImage()
    {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    };
    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
