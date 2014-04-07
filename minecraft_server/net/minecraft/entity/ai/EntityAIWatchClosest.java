package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase
{
    private EntityLiving theWatcher;

    /** The closest entity which is being watched by this one. */
    protected Entity closestEntity;

    /** This is the Maximum distance that the AI will look for the Entity */
    private float maxDistanceForPlayer;
    private int lookTime;
    private float field_75331_e;
    private Class watchedClass;
    private static final String __OBFID = "CL_00001592";

    public EntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3)
    {
        theWatcher = par1EntityLiving;
        watchedClass = par2Class;
        maxDistanceForPlayer = par3;
        field_75331_e = 0.02F;
        setMutexBits(2);
    }

    public EntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3, float par4)
    {
        theWatcher = par1EntityLiving;
        watchedClass = par2Class;
        maxDistanceForPlayer = par3;
        field_75331_e = par4;
        setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (theWatcher.getRNG().nextFloat() >= field_75331_e)
        {
            return false;
        }
        else
        {
            if (theWatcher.getAttackTarget() != null)
            {
                closestEntity = theWatcher.getAttackTarget();
            }

            if (watchedClass == EntityPlayer.class)
            {
                closestEntity = theWatcher.worldObj.getClosestPlayerToEntity(theWatcher, maxDistanceForPlayer);
            }
            else
            {
                closestEntity = theWatcher.worldObj.findNearestEntityWithinAABB(watchedClass, theWatcher.boundingBox.expand(maxDistanceForPlayer, 3.0D, maxDistanceForPlayer), theWatcher);
            }

            return closestEntity != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !closestEntity.isEntityAlive() ? false : (theWatcher.getDistanceSqToEntity(closestEntity) > maxDistanceForPlayer * maxDistanceForPlayer ? false : lookTime > 0);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        lookTime = 40 + theWatcher.getRNG().nextInt(40);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        closestEntity = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        theWatcher.getLookHelper().setLookPosition(closestEntity.posX, closestEntity.posY + closestEntity.getEyeHeight(), closestEntity.posZ, 10.0F, theWatcher.getVerticalFaceSpeed());
        --lookTime;
    }
}
