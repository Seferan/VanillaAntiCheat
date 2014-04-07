package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget
{
    boolean entityCallsForHelp;
    private int field_142052_b;
    private static final String __OBFID = "CL_00001619";

    public EntityAIHurtByTarget(EntityCreature par1EntityCreature, boolean par2)
    {
        super(par1EntityCreature, false);
        entityCallsForHelp = par2;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        int var1 = taskOwner.func_142015_aE();
        return var1 != field_142052_b && isSuitableTarget(taskOwner.getAITarget(), false);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        taskOwner.setAttackTarget(taskOwner.getAITarget());
        field_142052_b = taskOwner.func_142015_aE();

        if (entityCallsForHelp)
        {
            double var1 = getTargetDistance();
            List var3 = taskOwner.worldObj.getEntitiesWithinAABB(taskOwner.getClass(), AxisAlignedBB.getAABBPool().getAABB(taskOwner.posX, taskOwner.posY, taskOwner.posZ, taskOwner.posX + 1.0D, taskOwner.posY + 1.0D, taskOwner.posZ + 1.0D).expand(var1, 10.0D, var1));
            Iterator var4 = var3.iterator();

            while (var4.hasNext())
            {
                EntityCreature var5 = (EntityCreature)var4.next();

                if (taskOwner != var5 && var5.getAttackTarget() == null && !var5.isOnSameTeam(taskOwner.getAITarget()))
                {
                    var5.setAttackTarget(taskOwner.getAITarget());
                }
            }
        }

        super.startExecuting();
    }
}
