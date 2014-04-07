package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EntityAITempt extends EntityAIBase
{
    /** The entity using this AI that is tempted by the player. */
    private EntityCreature temptedEntity;
    private double field_75282_b;

    /** X position of player tempting this mob */
    private double targetX;

    /** Y position of player tempting this mob */
    private double targetY;

    /** Z position of player tempting this mob */
    private double targetZ;
    private double field_75278_f;
    private double field_75279_g;

    /** The player that is tempting the entity that is using this AI. */
    private EntityPlayer temptingPlayer;

    /**
     * A counter that is decremented each time the shouldExecute method is
     * called. The shouldExecute method will always return false if
     * delayTemptCounter is greater than 0.
     */
    private int delayTemptCounter;

    /** True if this EntityAITempt task is running */
    private boolean isRunning;
    private Item field_151484_k;

    /**
     * Whether the entity using this AI will be scared by the tempter's sudden
     * movement.
     */
    private boolean scaredByPlayerMovement;
    private boolean field_75286_m;
    private static final String __OBFID = "CL_00001616";

    public EntityAITempt(EntityCreature p_i45316_1_, double p_i45316_2_, Item p_i45316_4_, boolean p_i45316_5_)
    {
        temptedEntity = p_i45316_1_;
        field_75282_b = p_i45316_2_;
        field_151484_k = p_i45316_4_;
        scaredByPlayerMovement = p_i45316_5_;
        setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (delayTemptCounter > 0)
        {
            --delayTemptCounter;
            return false;
        }
        else
        {
            temptingPlayer = temptedEntity.worldObj.getClosestPlayerToEntity(temptedEntity, 10.0D);

            if (temptingPlayer == null)
            {
                return false;
            }
            else
            {
                ItemStack var1 = temptingPlayer.getCurrentEquippedItem();
                return var1 == null ? false : var1.getItem() == field_151484_k;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (scaredByPlayerMovement)
        {
            if (temptedEntity.getDistanceSqToEntity(temptingPlayer) < 36.0D)
            {
                if (temptingPlayer.getDistanceSq(targetX, targetY, targetZ) > 0.010000000000000002D) { return false; }

                if (Math.abs(temptingPlayer.rotationPitch - field_75278_f) > 5.0D || Math.abs(temptingPlayer.rotationYaw - field_75279_g) > 5.0D) { return false; }
            }
            else
            {
                targetX = temptingPlayer.posX;
                targetY = temptingPlayer.posY;
                targetZ = temptingPlayer.posZ;
            }

            field_75278_f = temptingPlayer.rotationPitch;
            field_75279_g = temptingPlayer.rotationYaw;
        }

        return shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        targetX = temptingPlayer.posX;
        targetY = temptingPlayer.posY;
        targetZ = temptingPlayer.posZ;
        isRunning = true;
        field_75286_m = temptedEntity.getNavigator().getAvoidsWater();
        temptedEntity.getNavigator().setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        temptingPlayer = null;
        temptedEntity.getNavigator().clearPathEntity();
        delayTemptCounter = 100;
        isRunning = false;
        temptedEntity.getNavigator().setAvoidsWater(field_75286_m);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        temptedEntity.getLookHelper().setLookPositionWithEntity(temptingPlayer, 30.0F, temptedEntity.getVerticalFaceSpeed());

        if (temptedEntity.getDistanceSqToEntity(temptingPlayer) < 6.25D)
        {
            temptedEntity.getNavigator().clearPathEntity();
        }
        else
        {
            temptedEntity.getNavigator().tryMoveToEntityLiving(temptingPlayer, field_75282_b);
        }
    }

    /**
     * @see #isRunning
     */
    public boolean isRunning()
    {
        return isRunning;
    }
}
