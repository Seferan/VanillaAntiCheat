package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class EntityAIRunAroundLikeCrazy extends EntityAIBase
{
    private EntityHorse horseHost;
    private double field_111178_b;
    private double field_111179_c;
    private double field_111176_d;
    private double field_111177_e;
    private static final String __OBFID = "CL_00001612";

    public EntityAIRunAroundLikeCrazy(EntityHorse par1EntityHorse, double par2)
    {
        horseHost = par1EntityHorse;
        field_111178_b = par2;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!horseHost.isTame() && horseHost.riddenByEntity != null)
        {
            Vec3 var1 = RandomPositionGenerator.findRandomTarget(horseHost, 5, 4);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                field_111179_c = var1.xCoord;
                field_111176_d = var1.yCoord;
                field_111177_e = var1.zCoord;
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        horseHost.getNavigator().tryMoveToXYZ(field_111179_c, field_111176_d, field_111177_e, field_111178_b);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !horseHost.getNavigator().noPath() && horseHost.riddenByEntity != null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (horseHost.getRNG().nextInt(50) == 0)
        {
            if (horseHost.riddenByEntity instanceof EntityPlayer)
            {
                int var1 = horseHost.getTemper();
                int var2 = horseHost.getMaxTemper();

                if (var2 > 0 && horseHost.getRNG().nextInt(var2) < var1)
                {
                    horseHost.setTamedBy((EntityPlayer)horseHost.riddenByEntity);
                    horseHost.worldObj.setEntityState(horseHost, (byte)7);
                    return;
                }

                horseHost.increaseTemper(5);
            }

            horseHost.riddenByEntity.mountEntity((Entity)null);
            horseHost.riddenByEntity = null;
            horseHost.makeHorseRearWithSound();
            horseHost.worldObj.setEntityState(horseHost, (byte)6);
        }
    }
}
