package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;

public class EntityAICreeperSwell extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntityCreeper swellingCreeper;

    /**
     * The creeper's attack target. This is used for the changing of the
     * creeper's state.
     */
    EntityLivingBase creeperAttackTarget;
    private static final String __OBFID = "CL_00001614";

    public EntityAICreeperSwell(EntityCreeper par1EntityCreeper)
    {
        swellingCreeper = par1EntityCreeper;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase var1 = swellingCreeper.getAttackTarget();
        return swellingCreeper.getCreeperState() > 0 || var1 != null && swellingCreeper.getDistanceSqToEntity(var1) < 9.0D;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        swellingCreeper.getNavigator().clearPathEntity();
        creeperAttackTarget = swellingCreeper.getAttackTarget();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        creeperAttackTarget = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (creeperAttackTarget == null)
        {
            swellingCreeper.setCreeperState(-1);
        }
        else if (swellingCreeper.getDistanceSqToEntity(creeperAttackTarget) > 49.0D)
        {
            swellingCreeper.setCreeperState(-1);
        }
        else if (!swellingCreeper.getEntitySenses().canSee(creeperAttackTarget))
        {
            swellingCreeper.setCreeperState(-1);
        }
        else
        {
            swellingCreeper.setCreeperState(1);
        }
    }
}
