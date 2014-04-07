package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.Vec3;

public class EntityAIPlay extends EntityAIBase
{
    private EntityVillager villagerObj;
    private EntityLivingBase targetVillager;
    private double field_75261_c;
    private int playTime;
    private static final String __OBFID = "CL_00001605";

    public EntityAIPlay(EntityVillager par1EntityVillager, double par2)
    {
        villagerObj = par1EntityVillager;
        field_75261_c = par2;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (villagerObj.getGrowingAge() >= 0)
        {
            return false;
        }
        else if (villagerObj.getRNG().nextInt(400) != 0)
        {
            return false;
        }
        else
        {
            List var1 = villagerObj.worldObj.getEntitiesWithinAABB(EntityVillager.class, villagerObj.boundingBox.expand(6.0D, 3.0D, 6.0D));
            double var2 = Double.MAX_VALUE;
            Iterator var4 = var1.iterator();

            while (var4.hasNext())
            {
                EntityVillager var5 = (EntityVillager)var4.next();

                if (var5 != villagerObj && !var5.isPlaying() && var5.getGrowingAge() < 0)
                {
                    double var6 = var5.getDistanceSqToEntity(villagerObj);

                    if (var6 <= var2)
                    {
                        var2 = var6;
                        targetVillager = var5;
                    }
                }
            }

            if (targetVillager == null)
            {
                Vec3 var8 = RandomPositionGenerator.findRandomTarget(villagerObj, 16, 3);

                if (var8 == null) { return false; }
            }

            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return playTime > 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        if (targetVillager != null)
        {
            villagerObj.setPlaying(true);
        }

        playTime = 1000;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        villagerObj.setPlaying(false);
        targetVillager = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        --playTime;

        if (targetVillager != null)
        {
            if (villagerObj.getDistanceSqToEntity(targetVillager) > 4.0D)
            {
                villagerObj.getNavigator().tryMoveToEntityLiving(targetVillager, field_75261_c);
            }
        }
        else if (villagerObj.getNavigator().noPath())
        {
            Vec3 var1 = RandomPositionGenerator.findRandomTarget(villagerObj, 16, 3);

            if (var1 == null) { return; }

            villagerObj.getNavigator().tryMoveToXYZ(var1.xCoord, var1.yCoord, var1.zCoord, field_75261_c);
        }
    }
}
