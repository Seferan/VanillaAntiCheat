package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityLookHelper
{
    private EntityLiving entity;

    /**
     * The amount of change that is made each update for an entity facing a
     * direction.
     */
    private float deltaLookYaw;

    /**
     * The amount of change that is made each update for an entity facing a
     * direction.
     */
    private float deltaLookPitch;

    /** Whether or not the entity is trying to look at something. */
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;
    private static final String __OBFID = "CL_00001572";

    public EntityLookHelper(EntityLiving par1EntityLiving)
    {
        entity = par1EntityLiving;
    }

    /**
     * Sets position to look at using entity
     */
    public void setLookPositionWithEntity(Entity par1Entity, float par2, float par3)
    {
        posX = par1Entity.posX;

        if (par1Entity instanceof EntityLivingBase)
        {
            posY = par1Entity.posY + par1Entity.getEyeHeight();
        }
        else
        {
            posY = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D;
        }

        posZ = par1Entity.posZ;
        deltaLookYaw = par2;
        deltaLookPitch = par3;
        isLooking = true;
    }

    /**
     * Sets position to look at
     */
    public void setLookPosition(double par1, double par3, double par5, float par7, float par8)
    {
        posX = par1;
        posY = par3;
        posZ = par5;
        deltaLookYaw = par7;
        deltaLookPitch = par8;
        isLooking = true;
    }

    /**
     * Updates look
     */
    public void onUpdateLook()
    {
        entity.rotationPitch = 0.0F;

        if (isLooking)
        {
            isLooking = false;
            double var1 = posX - entity.posX;
            double var3 = posY - (entity.posY + entity.getEyeHeight());
            double var5 = posZ - entity.posZ;
            double var7 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
            float var9 = (float)(Math.atan2(var5, var1) * 180.0D / Math.PI) - 90.0F;
            float var10 = (float)(-(Math.atan2(var3, var7) * 180.0D / Math.PI));
            entity.rotationPitch = updateRotation(entity.rotationPitch, var10, deltaLookPitch);
            entity.rotationYawHead = updateRotation(entity.rotationYawHead, var9, deltaLookYaw);
        }
        else
        {
            entity.rotationYawHead = updateRotation(entity.rotationYawHead, entity.renderYawOffset, 10.0F);
        }

        float var11 = MathHelper.wrapAngleTo180_float(entity.rotationYawHead - entity.renderYawOffset);

        if (!entity.getNavigator().noPath())
        {
            if (var11 < -75.0F)
            {
                entity.rotationYawHead = entity.renderYawOffset - 75.0F;
            }

            if (var11 > 75.0F)
            {
                entity.rotationYawHead = entity.renderYawOffset + 75.0F;
            }
        }
    }

    private float updateRotation(float par1, float par2, float par3)
    {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (var4 > par3)
        {
            var4 = par3;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        return par1 + var4;
    }
}
