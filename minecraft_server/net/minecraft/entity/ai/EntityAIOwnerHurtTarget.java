package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget
{
    EntityTameable theEntityTameable;
    EntityLivingBase theTarget;
    private int field_142050_e;
    private static final String __OBFID = "CL_00001625";

    public EntityAIOwnerHurtTarget(EntityTameable par1EntityTameable)
    {
        super(par1EntityTameable, false);
        theEntityTameable = par1EntityTameable;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!theEntityTameable.isTamed())
        {
            return false;
        }
        else
        {
            EntityLivingBase var1 = theEntityTameable.getOwner();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                theTarget = var1.getLastAttacker();
                int var2 = var1.getLastAttackerTime();
                return var2 != field_142050_e && isSuitableTarget(theTarget, false) && theEntityTameable.func_142018_a(theTarget, var1);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        taskOwner.setAttackTarget(theTarget);
        EntityLivingBase var1 = theEntityTameable.getOwner();

        if (var1 != null)
        {
            field_142050_e = var1.getLastAttackerTime();
        }

        super.startExecuting();
    }
}
