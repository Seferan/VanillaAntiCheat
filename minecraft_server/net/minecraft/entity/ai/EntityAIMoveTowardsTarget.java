package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsTarget extends EntityAIBase
{
    private EntityCreature theEntity;
    private EntityLivingBase targetEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private double speed;

    /**
     * If the distance to the target entity is further than this, this AI task
     * will not run.
     */
    private float maxTargetDistance;
    private static final String __OBFID = "CL_00001599";

    public EntityAIMoveTowardsTarget(EntityCreature par1EntityCreature, double par2, float par4)
    {
        theEntity = par1EntityCreature;
        speed = par2;
        maxTargetDistance = par4;
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        targetEntity = theEntity.getAttackTarget();

        if (targetEntity == null)
        {
            return false;
        }
        else if (targetEntity.getDistanceSqToEntity(theEntity) > maxTargetDistance * maxTargetDistance)
        {
            return false;
        }
        else
        {
            Vec3 var1 = RandomPositionGenerator.findRandomTargetBlockTowards(theEntity, 16, 7, theEntity.worldObj.getWorldVec3Pool().getVecFromPool(targetEntity.posX, targetEntity.posY, targetEntity.posZ));

            if (var1 == null)
            {
                return false;
            }
            else
            {
                movePosX = var1.xCoord;
                movePosY = var1.yCoord;
                movePosZ = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !theEntity.getNavigator().noPath() && targetEntity.isEntityAlive() && targetEntity.getDistanceSqToEntity(theEntity) < maxTargetDistance * maxTargetDistance;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        targetEntity = null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        theEntity.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, speed);
    }
}
