package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget
{
    EntityTameable theDefendingTameable;
    EntityLivingBase theOwnerAttacker;
    private int field_142051_e;
    private static final String __OBFID = "CL_00001624";

    public EntityAIOwnerHurtByTarget(EntityTameable par1EntityTameable)
    {
        super(par1EntityTameable, false);
        theDefendingTameable = par1EntityTameable;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!theDefendingTameable.isTamed())
        {
            return false;
        }
        else
        {
            EntityLivingBase var1 = theDefendingTameable.getOwner();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                theOwnerAttacker = var1.getAITarget();
                int var2 = var1.func_142015_aE();
                return var2 != field_142051_e && isSuitableTarget(theOwnerAttacker, false) && theDefendingTameable.func_142018_a(theOwnerAttacker, var1);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        taskOwner.setAttackTarget(theOwnerAttacker);
        EntityLivingBase var1 = theDefendingTameable.getOwner();

        if (var1 != null)
        {
            field_142051_e = var1.func_142015_aE();
        }

        super.startExecuting();
    }
}
