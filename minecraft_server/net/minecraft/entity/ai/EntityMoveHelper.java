package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper
{
    /** The EntityLiving that is being moved */
    private EntityLiving entity;
    private double posX;
    private double posY;
    private double posZ;

    /** The speed at which the entity should move */
    private double speed;
    private boolean update;
    private static final String __OBFID = "CL_00001573";

    public EntityMoveHelper(EntityLiving par1EntityLiving)
    {
        entity = par1EntityLiving;
        posX = par1EntityLiving.posX;
        posY = par1EntityLiving.posY;
        posZ = par1EntityLiving.posZ;
    }

    public boolean isUpdating()
    {
        return update;
    }

    public double getSpeed()
    {
        return speed;
    }

    /**
     * Sets the speed and location to move to
     */
    public void setMoveTo(double par1, double par3, double par5, double par7)
    {
        posX = par1;
        posY = par3;
        posZ = par5;
        speed = par7;
        update = true;
    }

    public void onUpdateMoveHelper()
    {
        entity.setMoveForward(0.0F);

        if (update)
        {
            update = false;
            int var1 = MathHelper.floor_double(entity.boundingBox.minY + 0.5D);
            double var2 = posX - entity.posX;
            double var4 = posZ - entity.posZ;
            double var6 = posY - var1;
            double var8 = var2 * var2 + var6 * var6 + var4 * var4;

            if (var8 >= 2.500000277905201E-7D)
            {
                float var10 = (float)(Math.atan2(var4, var2) * 180.0D / Math.PI) - 90.0F;
                entity.rotationYaw = limitAngle(entity.rotationYaw, var10, 30.0F);
                entity.setAIMoveSpeed((float)(speed * entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));

                if (var6 > 0.0D && var2 * var2 + var4 * var4 < 1.0D)
                {
                    entity.getJumpHelper().setJumping();
                }
            }
        }
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */
    private float limitAngle(float par1, float par2, float par3)
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
