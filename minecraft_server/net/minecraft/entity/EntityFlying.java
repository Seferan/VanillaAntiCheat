package net.minecraft.entity;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving
{
    private static final String __OBFID = "CL_00001545";

    public EntityFlying(World par1World)
    {
        super(par1World);
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on
     * the ground to update the fall distance and deal fall damage if landing on
     * the ground. Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
    }

    /**
     * Moves the entity based on the specified heading. Args: strafe, forward
     */
    public void moveEntityWithHeading(float par1, float par2)
    {
        if (isInWater())
        {
            moveFlying(par1, par2, 0.02F);
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.800000011920929D;
            motionY *= 0.800000011920929D;
            motionZ *= 0.800000011920929D;
        }
        else if (handleLavaMovement())
        {
            moveFlying(par1, par2, 0.02F);
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
        }
        else
        {
            float var3 = 0.91F;

            if (onGround)
            {
                var3 = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)).slipperiness * 0.91F;
            }

            float var4 = 0.16277136F / (var3 * var3 * var3);
            moveFlying(par1, par2, onGround ? 0.1F * var4 : 0.02F);
            var3 = 0.91F;

            if (onGround)
            {
                var3 = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)).slipperiness * 0.91F;
            }

            moveEntity(motionX, motionY, motionZ);
            motionX *= var3;
            motionY *= var3;
            motionZ *= var3;
        }

        prevLimbSwingAmount = limbSwingAmount;
        double var8 = posX - prevPosX;
        double var5 = posZ - prevPosZ;
        float var7 = MathHelper.sqrt_double(var8 * var8 + var5 * var5) * 4.0F;

        if (var7 > 1.0F)
        {
            var7 = 1.0F;
        }

        limbSwingAmount += (var7 - limbSwingAmount) * 0.4F;
        limbSwing += limbSwingAmount;
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return false;
    }
}
