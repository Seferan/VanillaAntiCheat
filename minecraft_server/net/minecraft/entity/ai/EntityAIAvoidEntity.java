package net.minecraft.entity.ai;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

public class EntityAIAvoidEntity extends EntityAIBase
{
    public final IEntitySelector field_98218_a = new IEntitySelector()
    {
        private static final String __OBFID = "CL_00001575";

        public boolean isEntityApplicable(Entity par1Entity)
        {
            return par1Entity.isEntityAlive() && theEntity.getEntitySenses().canSee(par1Entity);
        }
    };

    /** The entity we are attached to */
    private EntityCreature theEntity;
    private double farSpeed;
    private double nearSpeed;
    private Entity closestLivingEntity;
    private float distanceFromEntity;

    /** The PathEntity of our entity */
    private PathEntity entityPathEntity;

    /** The PathNavigate of our entity */
    private PathNavigate entityPathNavigate;

    /** The class of the entity we should avoid */
    private Class targetEntityClass;
    private static final String __OBFID = "CL_00001574";

    public EntityAIAvoidEntity(EntityCreature par1EntityCreature, Class par2Class, float par3, double par4, double par6)
    {
        theEntity = par1EntityCreature;
        targetEntityClass = par2Class;
        distanceFromEntity = par3;
        farSpeed = par4;
        nearSpeed = par6;
        entityPathNavigate = par1EntityCreature.getNavigator();
        setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (targetEntityClass == EntityPlayer.class)
        {
            if (theEntity instanceof EntityTameable && ((EntityTameable)theEntity).isTamed()) { return false; }

            closestLivingEntity = theEntity.worldObj.getClosestPlayerToEntity(theEntity, distanceFromEntity);

            if (closestLivingEntity == null) { return false; }
        }
        else
        {
            List var1 = theEntity.worldObj.selectEntitiesWithinAABB(targetEntityClass, theEntity.boundingBox.expand(distanceFromEntity, 3.0D, distanceFromEntity), field_98218_a);

            if (var1.isEmpty()) { return false; }

            closestLivingEntity = (Entity)var1.get(0);
        }

        Vec3 var2 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(theEntity, 16, 7, theEntity.worldObj.getWorldVec3Pool().getVecFromPool(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));

        if (var2 == null)
        {
            return false;
        }
        else if (closestLivingEntity.getDistanceSq(var2.xCoord, var2.yCoord, var2.zCoord) < closestLivingEntity.getDistanceSqToEntity(theEntity))
        {
            return false;
        }
        else
        {
            entityPathEntity = entityPathNavigate.getPathToXYZ(var2.xCoord, var2.yCoord, var2.zCoord);
            return entityPathEntity == null ? false : entityPathEntity.isDestinationSame(var2);
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !entityPathNavigate.noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        entityPathNavigate.setPath(entityPathEntity, farSpeed);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        closestLivingEntity = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (theEntity.getDistanceSqToEntity(closestLivingEntity) < 49.0D)
        {
            theEntity.getNavigator().setSpeed(nearSpeed);
        }
        else
        {
            theEntity.getNavigator().setSpeed(farSpeed);
        }
    }
}
