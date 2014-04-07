package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase
{
    private EntityCreature entityObj;
    private VillageDoorInfo doorInfo;
    private int insidePosX = -1;
    private int insidePosZ = -1;
    private static final String __OBFID = "CL_00001596";

    public EntityAIMoveIndoors(EntityCreature par1EntityCreature)
    {
        entityObj = par1EntityCreature;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        int var1 = MathHelper.floor_double(entityObj.posX);
        int var2 = MathHelper.floor_double(entityObj.posY);
        int var3 = MathHelper.floor_double(entityObj.posZ);

        if ((!entityObj.worldObj.isDaytime() || entityObj.worldObj.isRaining() || !entityObj.worldObj.getBiomeGenForCoords(var1, var3).canSpawnLightningBolt()) && !entityObj.worldObj.provider.hasNoSky)
        {
            if (entityObj.getRNG().nextInt(50) != 0)
            {
                return false;
            }
            else if (insidePosX != -1 && entityObj.getDistanceSq(insidePosX, entityObj.posY, insidePosZ) < 4.0D)
            {
                return false;
            }
            else
            {
                Village var4 = entityObj.worldObj.villageCollectionObj.findNearestVillage(var1, var2, var3, 14);

                if (var4 == null)
                {
                    return false;
                }
                else
                {
                    doorInfo = var4.findNearestDoorUnrestricted(var1, var2, var3);
                    return doorInfo != null;
                }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !entityObj.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        insidePosX = -1;

        if (entityObj.getDistanceSq(doorInfo.getInsidePosX(), doorInfo.posY, doorInfo.getInsidePosZ()) > 256.0D)
        {
            Vec3 var1 = RandomPositionGenerator.findRandomTargetBlockTowards(entityObj, 14, 3, entityObj.worldObj.getWorldVec3Pool().getVecFromPool(doorInfo.getInsidePosX() + 0.5D, doorInfo.getInsidePosY(), doorInfo.getInsidePosZ() + 0.5D));

            if (var1 != null)
            {
                entityObj.getNavigator().tryMoveToXYZ(var1.xCoord, var1.yCoord, var1.zCoord, 1.0D);
            }
        }
        else
        {
            entityObj.getNavigator().tryMoveToXYZ(doorInfo.getInsidePosX() + 0.5D, doorInfo.getInsidePosY(), doorInfo.getInsidePosZ() + 0.5D, 1.0D);
        }
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        insidePosX = doorInfo.getInsidePosX();
        insidePosZ = doorInfo.getInsidePosZ();
        doorInfo = null;
    }
}
