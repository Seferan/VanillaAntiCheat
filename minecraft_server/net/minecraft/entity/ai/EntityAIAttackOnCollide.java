package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase
{
    World worldObj;
    EntityCreature attacker;

    /**
     * An amount of decrementing ticks that allows the entity to attack once the
     * tick reaches 0.
     */
    int attackTick;

    /** The speed with which the mob will approach the target */
    double speedTowardsTarget;

    /**
     * When true, the mob will continue chasing its target, even if it can't
     * find a path to them right now.
     */
    boolean longMemory;

    /** The PathEntity of our entity. */
    PathEntity entityPathEntity;
    Class classTarget;
    private int field_75445_i;
    private double field_151497_i;
    private double field_151495_j;
    private double field_151496_k;
    private static final String __OBFID = "CL_00001595";

    public EntityAIAttackOnCollide(EntityCreature par1EntityCreature, Class par2Class, double par3, boolean par5)
    {
        this(par1EntityCreature, par3, par5);
        classTarget = par2Class;
    }

    public EntityAIAttackOnCollide(EntityCreature par1EntityCreature, double par2, boolean par4)
    {
        attacker = par1EntityCreature;
        worldObj = par1EntityCreature.worldObj;
        speedTowardsTarget = par2;
        longMemory = par4;
        setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase var1 = attacker.getAttackTarget();

        if (var1 == null)
        {
            return false;
        }
        else if (!var1.isEntityAlive())
        {
            return false;
        }
        else if (classTarget != null && !classTarget.isAssignableFrom(var1.getClass()))
        {
            return false;
        }
        else
        {
            entityPathEntity = attacker.getNavigator().getPathToEntityLiving(var1);
            return entityPathEntity != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLivingBase var1 = attacker.getAttackTarget();
        return var1 == null ? false : (!var1.isEntityAlive() ? false : (!longMemory ? !attacker.getNavigator().noPath() : attacker.isWithinHomeDistance(MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ))));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        attacker.getNavigator().setPath(entityPathEntity, speedTowardsTarget);
        field_75445_i = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        attacker.getNavigator().clearPathEntity();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        EntityLivingBase var1 = attacker.getAttackTarget();
        attacker.getLookHelper().setLookPositionWithEntity(var1, 30.0F, 30.0F);
        double var2 = attacker.getDistanceSq(var1.posX, var1.boundingBox.minY, var1.posZ);
        double var4 = attacker.width * 2.0F * attacker.width * 2.0F + var1.width;
        --field_75445_i;

        if ((longMemory || attacker.getEntitySenses().canSee(var1)) && field_75445_i <= 0 && (field_151497_i == 0.0D && field_151495_j == 0.0D && field_151496_k == 0.0D || var1.getDistanceSq(field_151497_i, field_151495_j, field_151496_k) >= 1.0D || attacker.getRNG().nextFloat() < 0.05F))
        {
            field_151497_i = var1.posX;
            field_151495_j = var1.boundingBox.minY;
            field_151496_k = var1.posZ;
            field_75445_i = 4 + attacker.getRNG().nextInt(7);

            if (var2 > 1024.0D)
            {
                field_75445_i += 10;
            }
            else if (var2 > 256.0D)
            {
                field_75445_i += 5;
            }

            if (!attacker.getNavigator().tryMoveToEntityLiving(var1, speedTowardsTarget))
            {
                field_75445_i += 15;
            }
        }

        attackTick = Math.max(attackTick - 1, 0);

        if (var2 <= var4 && attackTick <= 20)
        {
            attackTick = 20;

            if (attacker.getHeldItem() != null)
            {
                attacker.swingItem();
            }

            attacker.attackEntityAsMob(var1);
        }
    }
}
