public class Activity implements Action {
    public Entity entity;
    public WorldModel world;
    public ImageStore imageStore;


    public Activity(Entity entity, WorldModel world, ImageStore imageStore){
        this.entity =entity;
        this.world= world;
        this.imageStore =imageStore;

    }

    public void executeAction(EventScheduler scheduler)
    {


        if(this.entity.getClass() == MinerFull.class) {
            MinerFull m = (MinerFull)this.entity;
            m.executeMinerFullActivity(this.world,
                    this.imageStore, scheduler);
        }

        if(this.entity.getClass() == MinerNotFull.class) {
            MinerNotFull m = (MinerNotFull)this.entity;
            m.executeMinerNotFullActivity(this.world, this.imageStore, scheduler);
        }
        if(this.entity.getClass() == Ore.class) {
            Ore o = (Ore)this.entity;
            o.executeOreActivity(this.world, this.imageStore,
                    scheduler);
        }

        if(this.entity.getClass() == OreBlob.class) {
            OreBlob o = (OreBlob)this.entity;
            o.executeOreBlobActivity(this.world,
                    this.imageStore, scheduler);
        }

        if(this.entity.getClass()== Quake.class) {
            Quake q = (Quake)this.entity;
            q.executeQuakeActivity(this.world, this.imageStore,
                    scheduler);
        }

        if(this.entity.getClass()==Vein.class) {
            Vein v = (Vein)this.entity;
            v.executeVeinActivity(this.world, this.imageStore,
                    scheduler);
        }
        if(this.entity.getClass() == Medallion.class){
            Medallion m = (Medallion)this.entity;
            m.executeMedallionAction(this.world, scheduler, this.imageStore);
        }
        if(this.entity.getClass() == Zombie.class){
            Zombie g = (Zombie)this.entity;
            g.executeZombieActivity(this.world, scheduler, this.imageStore);
        }
    }

}
