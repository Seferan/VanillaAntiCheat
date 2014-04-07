package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISit extends EntityAIBase
{
    private EntityTameable theEntity;

    /** If the EntityTameable is sitting. */
    private boolean isSitting;
    private static final String __OBFID = "CL_00001613";

    public EntityAISit(EntityTameable par1EntityTameable)
    {
        theEntity = par1EntityTameable;
        setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!theEntity.isTamed())
        {
            return false;
        }
        else if (theEntity.isInWater())
        {
            return false;
        }
        else if (!theEntity.onGround)
        {
            return false;
        }
        else
        {
            EntityLivingBase var1 = theEntity.getOwner();
            return var1 == null ? true : (theEntity.getDistanceSqToEntity(var1) < 144.0D && var1.getAITarget() != null ? false : isSitting);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        theEntity.getNavigator().clearPathEntity();
        theEntity.setSitting(true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        theEntity.setSitting(false);
    }

    /**
     * Sets the sitting flag.
     */
    public void setSitting(boolean par1)
    {
        isSitting = par1;
    }
}
