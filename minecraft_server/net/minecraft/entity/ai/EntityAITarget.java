package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase
{
    /** The entity that this task belongs to */
    protected EntityCreature taskOwner;

    /**
     * If true, EntityAI targets must be able to be seen (cannot be blocked by
     * walls) to be suitable targets.
     */
    protected boolean shouldCheckSight;

    /**
     * When true, only entities that can be reached with minimal effort will be
     * targetted.
     */
    private boolean nearbyOnly;

    /**
     * When nearbyOnly is true: 0 -> No target, but OK to search; 1 -> Nearby
     * target found; 2 -> Target too far.
     */
    private int targetSearchStatus;

    /**
     * When nearbyOnly is true, this throttles target searching to avoid
     * excessive pathfinding.
     */
    private int targetSearchDelay;
    private int field_75298_g;
    private static final String __OBFID = "CL_00001626";

    public EntityAITarget(EntityCreature par1EntityCreature, boolean par2)
    {
        this(par1EntityCreature, par2, false);
    }

    public EntityAITarget(EntityCreature par1EntityCreature, boolean par2, boolean par3)
    {
        taskOwner = par1EntityCreature;
        shouldCheckSight = par2;
        nearbyOnly = par3;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLivingBase var1 = taskOwner.getAttackTarget();

        if (var1 == null)
        {
            return false;
        }
        else if (!var1.isEntityAlive())
        {
            return false;
        }
        else
        {
            double var2 = getTargetDistance();

            if (taskOwner.getDistanceSqToEntity(var1) > var2 * var2)
            {
                return false;
            }
            else
            {
                if (shouldCheckSight)
                {
                    if (taskOwner.getEntitySenses().canSee(var1))
                    {
                        field_75298_g = 0;
                    }
                    else if (++field_75298_g > 60) { return false; }
                }

                return !(var1 instanceof EntityPlayerMP) || !((EntityPlayerMP)var1).theItemInWorldManager.isCreative();
            }
        }
    }

    protected double getTargetDistance()
    {
        IAttributeInstance var1 = taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
        return var1 == null ? 16.0D : var1.getAttributeValue();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        targetSearchStatus = 0;
        targetSearchDelay = 0;
        field_75298_g = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        taskOwner.setAttackTarget((EntityLivingBase)null);
    }

    /**
     * A method used to see if an entity is a suitable target through a number
     * of checks.
     */
    protected boolean isSuitableTarget(EntityLivingBase par1EntityLivingBase, boolean par2)
    {
        if (par1EntityLivingBase == null)
        {
            return false;
        }
        else if (par1EntityLivingBase == taskOwner)
        {
            return false;
        }
        else if (!par1EntityLivingBase.isEntityAlive())
        {
            return false;
        }
        else if (!taskOwner.canAttackClass(par1EntityLivingBase.getClass()))
        {
            return false;
        }
        else
        {
            if (taskOwner instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)taskOwner).getOwnerName()))
            {
                if (par1EntityLivingBase instanceof IEntityOwnable && ((IEntityOwnable)taskOwner).getOwnerName().equals(((IEntityOwnable)par1EntityLivingBase).getOwnerName())) { return false; }

                if (par1EntityLivingBase == ((IEntityOwnable)taskOwner).getOwner()) { return false; }
            }
            else if (par1EntityLivingBase instanceof EntityPlayer && !par2 && ((EntityPlayer)par1EntityLivingBase).capabilities.disableDamage) { return false; }

            if (!taskOwner.isWithinHomeDistance(MathHelper.floor_double(par1EntityLivingBase.posX), MathHelper.floor_double(par1EntityLivingBase.posY), MathHelper.floor_double(par1EntityLivingBase.posZ)))
            {
                return false;
            }
            else if (shouldCheckSight && !taskOwner.getEntitySenses().canSee(par1EntityLivingBase))
            {
                return false;
            }
            else
            {
                if (nearbyOnly)
                {
                    if (--targetSearchDelay <= 0)
                    {
                        targetSearchStatus = 0;
                    }

                    if (targetSearchStatus == 0)
                    {
                        targetSearchStatus = canEasilyReach(par1EntityLivingBase) ? 1 : 2;
                    }

                    if (targetSearchStatus == 2) { return false; }
                }

                return true;
            }
        }
    }

    /**
     * Checks to see if this entity can find a short path to the given target.
     */
    private boolean canEasilyReach(EntityLivingBase par1EntityLivingBase)
    {
        targetSearchDelay = 10 + taskOwner.getRNG().nextInt(5);
        PathEntity var2 = taskOwner.getNavigator().getPathToEntityLiving(par1EntityLivingBase);

        if (var2 == null)
        {
            return false;
        }
        else
        {
            PathPoint var3 = var2.getFinalPathPoint();

            if (var3 == null)
            {
                return false;
            }
            else
            {
                int var4 = var3.xCoord - MathHelper.floor_double(par1EntityLivingBase.posX);
                int var5 = var3.zCoord - MathHelper.floor_double(par1EntityLivingBase.posZ);
                return var4 * var4 + var5 * var5 <= 2.25D;
            }
        }
    }
}
