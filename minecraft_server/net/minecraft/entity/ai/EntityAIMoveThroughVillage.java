package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase
{
    private EntityCreature theEntity;
    private double movementSpeed;

    /** The PathNavigate of our entity. */
    private PathEntity entityPathNavigate;
    private VillageDoorInfo doorInfo;
    private boolean isNocturnal;
    private List doorList = new ArrayList();
    private static final String __OBFID = "CL_00001597";

    public EntityAIMoveThroughVillage(EntityCreature par1EntityCreature, double par2, boolean par4)
    {
        theEntity = par1EntityCreature;
        movementSpeed = par2;
        isNocturnal = par4;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        func_75414_f();

        if (isNocturnal && theEntity.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            Village var1 = theEntity.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(theEntity.posX), MathHelper.floor_double(theEntity.posY), MathHelper.floor_double(theEntity.posZ), 0);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                doorInfo = func_75412_a(var1);

                if (doorInfo == null)
                {
                    return false;
                }
                else
                {
                    boolean var2 = theEntity.getNavigator().getCanBreakDoors();
                    theEntity.getNavigator().setBreakDoors(false);
                    entityPathNavigate = theEntity.getNavigator().getPathToXYZ(doorInfo.posX, doorInfo.posY, doorInfo.posZ);
                    theEntity.getNavigator().setBreakDoors(var2);

                    if (entityPathNavigate != null)
                    {
                        return true;
                    }
                    else
                    {
                        Vec3 var3 = RandomPositionGenerator.findRandomTargetBlockTowards(theEntity, 10, 7, theEntity.worldObj.getWorldVec3Pool().getVecFromPool(doorInfo.posX, doorInfo.posY, doorInfo.posZ));

                        if (var3 == null)
                        {
                            return false;
                        }
                        else
                        {
                            theEntity.getNavigator().setBreakDoors(false);
                            entityPathNavigate = theEntity.getNavigator().getPathToXYZ(var3.xCoord, var3.yCoord, var3.zCoord);
                            theEntity.getNavigator().setBreakDoors(var2);
                            return entityPathNavigate != null;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (theEntity.getNavigator().noPath())
        {
            return false;
        }
        else
        {
            float var1 = theEntity.width + 4.0F;
            return theEntity.getDistanceSq(doorInfo.posX, doorInfo.posY, doorInfo.posZ) > var1 * var1;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        theEntity.getNavigator().setPath(entityPathNavigate, movementSpeed);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        if (theEntity.getNavigator().noPath() || theEntity.getDistanceSq(doorInfo.posX, doorInfo.posY, doorInfo.posZ) < 16.0D)
        {
            doorList.add(doorInfo);
        }
    }

    private VillageDoorInfo func_75412_a(Village par1Village)
    {
        VillageDoorInfo var2 = null;
        int var3 = Integer.MAX_VALUE;
        List var4 = par1Village.getVillageDoorInfoList();
        Iterator var5 = var4.iterator();

        while (var5.hasNext())
        {
            VillageDoorInfo var6 = (VillageDoorInfo)var5.next();
            int var7 = var6.getDistanceSquared(MathHelper.floor_double(theEntity.posX), MathHelper.floor_double(theEntity.posY), MathHelper.floor_double(theEntity.posZ));

            if (var7 < var3 && !func_75413_a(var6))
            {
                var2 = var6;
                var3 = var7;
            }
        }

        return var2;
    }

    private boolean func_75413_a(VillageDoorInfo par1VillageDoorInfo)
    {
        Iterator var2 = doorList.iterator();
        VillageDoorInfo var3;

        do
        {
            if (!var2.hasNext()) { return false; }

            var3 = (VillageDoorInfo)var2.next();
        } while (par1VillageDoorInfo.posX != var3.posX || par1VillageDoorInfo.posY != var3.posY || par1VillageDoorInfo.posZ != var3.posZ);

        return true;
    }

    private void func_75414_f()
    {
        if (doorList.size() > 15)
        {
            doorList.remove(0);
        }
    }
}
