package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase
{
    private EntityCreature theEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private double movementSpeed;
    private static final String __OBFID = "CL_00001598";

    public EntityAIMoveTowardsRestriction(EntityCreature par1EntityCreature, double par2)
    {
        theEntity = par1EntityCreature;
        movementSpeed = par2;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (theEntity.isWithinHomeDistanceCurrentPosition())
        {
            return false;
        }
        else
        {
            ChunkCoordinates var1 = theEntity.getHomePosition();
            Vec3 var2 = RandomPositionGenerator.findRandomTargetBlockTowards(theEntity, 16, 7, theEntity.worldObj.getWorldVec3Pool().getVecFromPool(var1.posX, var1.posY, var1.posZ));

            if (var2 == null)
            {
                return false;
            }
            else
            {
                movePosX = var2.xCoord;
                movePosY = var2.yCoord;
                movePosZ = var2.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !theEntity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        theEntity.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, movementSpeed);
    }
}
