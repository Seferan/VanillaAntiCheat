package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderEye extends Entity
{
    /** 'x' location the eye should float towards. */
    private double targetX;

    /** 'y' location the eye should float towards. */
    private double targetY;

    /** 'z' location the eye should float towards. */
    private double targetZ;
    private int despawnTimer;
    private boolean shatterOrDrop;
    private static final String __OBFID = "CL_00001716";

    public EntityEnderEye(World par1World)
    {
        super(par1World);
        setSize(0.25F, 0.25F);
    }

    protected void entityInit()
    {
    }

    public EntityEnderEye(World par1World, double par2, double par4, double par6)
    {
        super(par1World);
        despawnTimer = 0;
        setSize(0.25F, 0.25F);
        setPosition(par2, par4, par6);
        yOffset = 0.0F;
    }

    /**
     * The location the eye should float/move towards. Currently used for moving
     * towards the nearest stronghold. Args: strongholdX, strongholdY,
     * strongholdZ
     */
    public void moveTowards(double par1, int par3, double par4)
    {
        double var6 = par1 - posX;
        double var8 = par4 - posZ;
        float var10 = MathHelper.sqrt_double(var6 * var6 + var8 * var8);

        if (var10 > 12.0F)
        {
            targetX = posX + var6 / var10 * 12.0D;
            targetZ = posZ + var8 / var10 * 12.0D;
            targetY = posY + 8.0D;
        }
        else
        {
            targetX = par1;
            targetY = par3;
            targetZ = par4;
        }

        despawnTimer = 0;
        shatterOrDrop = rand.nextInt(5) > 0;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;
        super.onUpdate();
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float var1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

        for (rotationPitch = (float)(Math.atan2(motionY, var1) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (rotationPitch - prevRotationPitch >= 180.0F)
        {
            prevRotationPitch += 360.0F;
        }

        while (rotationYaw - prevRotationYaw < -180.0F)
        {
            prevRotationYaw -= 360.0F;
        }

        while (rotationYaw - prevRotationYaw >= 180.0F)
        {
            prevRotationYaw += 360.0F;
        }

        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;

        if (!worldObj.isClient)
        {
            double var2 = targetX - posX;
            double var4 = targetZ - posZ;
            float var6 = (float)Math.sqrt(var2 * var2 + var4 * var4);
            float var7 = (float)Math.atan2(var4, var2);
            double var8 = var1 + (var6 - var1) * 0.0025D;

            if (var6 < 1.0F)
            {
                var8 *= 0.8D;
                motionY *= 0.8D;
            }

            motionX = Math.cos(var7) * var8;
            motionZ = Math.sin(var7) * var8;

            if (posY < targetY)
            {
                motionY += (1.0D - motionY) * 0.014999999664723873D;
            }
            else
            {
                motionY += (-1.0D - motionY) * 0.014999999664723873D;
            }
        }

        float var10 = 0.25F;

        if (isInWater())
        {
            for (int var3 = 0; var3 < 4; ++var3)
            {
                worldObj.spawnParticle("bubble", posX - motionX * var10, posY - motionY * var10, posZ - motionZ * var10, motionX, motionY, motionZ);
            }
        }
        else
        {
            worldObj.spawnParticle("portal", posX - motionX * var10 + rand.nextDouble() * 0.6D - 0.3D, posY - motionY * var10 - 0.5D, posZ - motionZ * var10 + rand.nextDouble() * 0.6D - 0.3D, motionX, motionY, motionZ);
        }

        if (!worldObj.isClient)
        {
            setPosition(posX, posY, posZ);
            ++despawnTimer;

            if (despawnTimer > 80 && !worldObj.isClient)
            {
                setDead();

                if (shatterOrDrop)
                {
                    worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.ender_eye)));
                }
                else
                {
                    worldObj.playAuxSFX(2003, (int)Math.round(posX), (int)Math.round(posY), (int)Math.round(posZ), 0);
                }
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1)
    {
        return 1.0F;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }
}
