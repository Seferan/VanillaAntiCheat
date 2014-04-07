package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase
{
    private EntityCreature entityObj;
    private VillageDoorInfo frontDoor;
    private static final String __OBFID = "CL_00001610";

    public EntityAIRestrictOpenDoor(EntityCreature par1EntityCreature)
    {
        entityObj = par1EntityCreature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (entityObj.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            Village var1 = entityObj.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(entityObj.posX), MathHelper.floor_double(entityObj.posY), MathHelper.floor_double(entityObj.posZ), 16);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                frontDoor = var1.findNearestDoor(MathHelper.floor_double(entityObj.posX), MathHelper.floor_double(entityObj.posY), MathHelper.floor_double(entityObj.posZ));
                return frontDoor == null ? false : frontDoor.getInsideDistanceSquare(MathHelper.floor_double(entityObj.posX), MathHelper.floor_double(entityObj.posY), MathHelper.floor_double(entityObj.posZ)) < 2.25D;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return entityObj.worldObj.isDaytime() ? false : !frontDoor.isDetachedFromVillageFlag && frontDoor.isInside(MathHelper.floor_double(entityObj.posX), MathHelper.floor_double(entityObj.posZ));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        entityObj.getNavigator().setBreakDoors(false);
        entityObj.getNavigator().setEnterDoors(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        entityObj.getNavigator().setBreakDoors(true);
        entityObj.getNavigator().setEnterDoors(true);
        frontDoor = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        frontDoor.incrementDoorOpeningRestrictionCounter();
    }
}
