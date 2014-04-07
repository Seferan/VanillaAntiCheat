package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.MathHelper;

public class EntityAIArrowAttack extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    private final EntityLiving entityHost;

    /**
     * The entity (as a RangedAttackMob) the AI instance has been applied to.
     */
    private final IRangedAttackMob rangedAttackEntityHost;
    private EntityLivingBase attackTarget;

    /**
     * A decrementing tick that spawns a ranged attack once this value reaches
     * 0. It is then set back to the maxRangedAttackTime.
     */
    private int rangedAttackTime;
    private double entityMoveSpeed;
    private int field_75318_f;
    private int field_96561_g;

    /**
     * The maximum time the AI has to wait before peforming another ranged
     * attack.
     */
    private int maxRangedAttackTime;
    private float field_96562_i;
    private float field_82642_h;
    private static final String __OBFID = "CL_00001609";

    public EntityAIArrowAttack(IRangedAttackMob par1IRangedAttackMob, double par2, int par4, float par5)
    {
        this(par1IRangedAttackMob, par2, par4, par4, par5);
    }

    public EntityAIArrowAttack(IRangedAttackMob par1IRangedAttackMob, double par2, int par4, int par5, float par6)
    {
        rangedAttackTime = -1;

        if (!(par1IRangedAttackMob instanceof EntityLivingBase))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        else
        {
            rangedAttackEntityHost = par1IRangedAttackMob;
            entityHost = (EntityLiving)par1IRangedAttackMob;
            entityMoveSpeed = par2;
            field_96561_g = par4;
            maxRangedAttackTime = par5;
            field_96562_i = par6;
            field_82642_h = par6 * par6;
            setMutexBits(3);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase var1 = entityHost.getAttackTarget();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            attackTarget = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return shouldExecute() || !entityHost.getNavigator().noPath();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        attackTarget = null;
        field_75318_f = 0;
        rangedAttackTime = -1;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        double var1 = entityHost.getDistanceSq(attackTarget.posX, attackTarget.boundingBox.minY, attackTarget.posZ);
        boolean var3 = entityHost.getEntitySenses().canSee(attackTarget);

        if (var3)
        {
            ++field_75318_f;
        }
        else
        {
            field_75318_f = 0;
        }

        if (var1 <= field_82642_h && field_75318_f >= 20)
        {
            entityHost.getNavigator().clearPathEntity();
        }
        else
        {
            entityHost.getNavigator().tryMoveToEntityLiving(attackTarget, entityMoveSpeed);
        }

        entityHost.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
        float var4;

        if (--rangedAttackTime == 0)
        {
            if (var1 > field_82642_h || !var3) { return; }

            var4 = MathHelper.sqrt_double(var1) / field_96562_i;
            float var5 = var4;

            if (var4 < 0.1F)
            {
                var5 = 0.1F;
            }

            if (var5 > 1.0F)
            {
                var5 = 1.0F;
            }

            rangedAttackEntityHost.attackEntityWithRangedAttack(attackTarget, var5);
            rangedAttackTime = MathHelper.floor_float(var4 * (maxRangedAttackTime - field_96561_g) + field_96561_g);
        }
        else if (rangedAttackTime < 0)
        {
            var4 = MathHelper.sqrt_double(var1) / field_96562_i;
            rangedAttackTime = MathHelper.floor_float(var4 * (maxRangedAttackTime - field_96561_g) + field_96561_g);
        }
    }
}
