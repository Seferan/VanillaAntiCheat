package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase
{
    /** The entity that is leaping. */
    EntityLiving leaper;

    /** The entity that the leaper is leaping towards. */
    EntityLivingBase leapTarget;

    /** The entity's motionY after leaping. */
    float leapMotionY;
    private static final String __OBFID = "CL_00001591";

    public EntityAILeapAtTarget(EntityLiving par1EntityLiving, float par2)
    {
        leaper = par1EntityLiving;
        leapMotionY = par2;
        setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        leapTarget = leaper.getAttackTarget();

        if (leapTarget == null)
        {
            return false;
        }
        else
        {
            double var1 = leaper.getDistanceSqToEntity(leapTarget);
            return var1 >= 4.0D && var1 <= 16.0D ? (!leaper.onGround ? false : leaper.getRNG().nextInt(5) == 0) : false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !leaper.onGround;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        double var1 = leapTarget.posX - leaper.posX;
        double var3 = leapTarget.posZ - leaper.posZ;
        float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3);
        leaper.motionX += var1 / var5 * 0.5D * 0.800000011920929D + leaper.motionX * 0.20000000298023224D;
        leaper.motionZ += var3 / var5 * 0.5D * 0.800000011920929D + leaper.motionZ * 0.20000000298023224D;
        leaper.motionY = leapMotionY;
    }
}
