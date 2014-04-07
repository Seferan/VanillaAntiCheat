package net.minecraft.entity.ai;

import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;

public class EntityAILookAtVillager extends EntityAIBase
{
    private EntityIronGolem theGolem;
    private EntityVillager theVillager;
    private int lookTime;
    private static final String __OBFID = "CL_00001602";

    public EntityAILookAtVillager(EntityIronGolem par1EntityIronGolem)
    {
        theGolem = par1EntityIronGolem;
        setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!theGolem.worldObj.isDaytime())
        {
            return false;
        }
        else if (theGolem.getRNG().nextInt(8000) != 0)
        {
            return false;
        }
        else
        {
            theVillager = (EntityVillager)theGolem.worldObj.findNearestEntityWithinAABB(EntityVillager.class, theGolem.boundingBox.expand(6.0D, 2.0D, 6.0D), theGolem);
            return theVillager != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return lookTime > 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        lookTime = 400;
        theGolem.setHoldingRose(true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        theGolem.setHoldingRose(false);
        theVillager = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        theGolem.getLookHelper().setLookPositionWithEntity(theVillager, 30.0F, 30.0F);
        --lookTime;
    }
}
