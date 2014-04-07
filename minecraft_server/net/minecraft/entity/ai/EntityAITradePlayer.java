package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EntityAITradePlayer extends EntityAIBase
{
    private EntityVillager villager;
    private static final String __OBFID = "CL_00001617";

    public EntityAITradePlayer(EntityVillager par1EntityVillager)
    {
        villager = par1EntityVillager;
        setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!villager.isEntityAlive())
        {
            return false;
        }
        else if (villager.isInWater())
        {
            return false;
        }
        else if (!villager.onGround)
        {
            return false;
        }
        else if (villager.velocityChanged)
        {
            return false;
        }
        else
        {
            EntityPlayer var1 = villager.getCustomer();
            return var1 == null ? false : (villager.getDistanceSqToEntity(var1) > 16.0D ? false : var1.openContainer instanceof Container);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        villager.getNavigator().clearPathEntity();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        villager.setCustomer((EntityPlayer)null);
    }
}
