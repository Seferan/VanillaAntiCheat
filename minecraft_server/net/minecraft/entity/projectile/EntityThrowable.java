package net.minecraft.entity.projectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityThrowable extends Entity implements IProjectile
{
    private int field_145788_c = -1;
    private int field_145786_d = -1;
    private int field_145787_e = -1;
    private Block field_145785_f;
    protected boolean inGround;
    public int throwableShake;

    /** The entity that threw this throwable item. */
    private EntityLivingBase thrower;
    private String throwerName;
    private int ticksInGround;
    private int ticksInAir;
    private static final String __OBFID = "CL_00001723";

    public EntityThrowable(World par1World)
    {
        super(par1World);
        setSize(0.25F, 0.25F);
    }

    protected void entityInit()
    {
    }

    public EntityThrowable(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World);
        thrower = par2EntityLivingBase;
        setSize(0.25F, 0.25F);
        setLocationAndAngles(par2EntityLivingBase.posX, par2EntityLivingBase.posY + par2EntityLivingBase.getEyeHeight(), par2EntityLivingBase.posZ, par2EntityLivingBase.rotationYaw, par2EntityLivingBase.rotationPitch);
        posX -= MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        posY -= 0.10000000149011612D;
        posZ -= MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        float var3 = 0.4F;
        motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * var3;
        motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * var3;
        motionY = -MathHelper.sin((rotationPitch + func_70183_g()) / 180.0F * (float)Math.PI) * var3;
        setThrowableHeading(motionX, motionY, motionZ, func_70182_d(), 1.0F);
    }

    public EntityThrowable(World par1World, double par2, double par4, double par6)
    {
        super(par1World);
        ticksInGround = 0;
        setSize(0.25F, 0.25F);
        setPosition(par2, par4, par6);
        yOffset = 0.0F;
    }

    protected float func_70182_d()
    {
        return 1.5F;
    }

    protected float func_70183_g()
    {
        return 0.0F;
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z
     * direction.
     */
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8)
    {
        float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= var9;
        par3 /= var9;
        par5 /= var9;
        par1 += rand.nextGaussian() * 0.007499999832361937D * par8;
        par3 += rand.nextGaussian() * 0.007499999832361937D * par8;
        par5 += rand.nextGaussian() * 0.007499999832361937D * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        motionX = par1;
        motionY = par3;
        motionZ = par5;
        float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        prevRotationYaw = rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
        prevRotationPitch = rotationPitch = (float)(Math.atan2(par3, var10) * 180.0D / Math.PI);
        ticksInGround = 0;
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

        if (throwableShake > 0)
        {
            --throwableShake;
        }

        if (inGround)
        {
            if (worldObj.getBlock(field_145788_c, field_145786_d, field_145787_e) == field_145785_f)
            {
                ++ticksInGround;

                if (ticksInGround == 1200)
                {
                    setDead();
                }

                return;
            }

            inGround = false;
            motionX *= rand.nextFloat() * 0.2F;
            motionY *= rand.nextFloat() * 0.2F;
            motionZ *= rand.nextFloat() * 0.2F;
            ticksInGround = 0;
            ticksInAir = 0;
        }
        else
        {
            ++ticksInAir;
        }

        Vec3 var1 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
        Vec3 var2 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);
        MovingObjectPosition var3 = worldObj.rayTraceBlocks(var1, var2);
        var1 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
        var2 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);

        if (var3 != null)
        {
            var2 = worldObj.getWorldVec3Pool().getVecFromPool(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
        }

        if (!worldObj.isClient)
        {
            Entity var4 = null;
            List var5 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            double var6 = 0.0D;
            EntityLivingBase var8 = getThrower();

            for (int var9 = 0; var9 < var5.size(); ++var9)
            {
                Entity var10 = (Entity)var5.get(var9);

                if (var10.canBeCollidedWith() && (var10 != var8 || ticksInAir >= 5))
                {
                    float var11 = 0.3F;
                    AxisAlignedBB var12 = var10.boundingBox.expand(var11, var11, var11);
                    MovingObjectPosition var13 = var12.calculateIntercept(var1, var2);

                    if (var13 != null)
                    {
                        double var14 = var1.distanceTo(var13.hitVec);

                        if (var14 < var6 || var6 == 0.0D)
                        {
                            var4 = var10;
                            var6 = var14;
                        }
                    }
                }
            }

            if (var4 != null)
            {
                var3 = new MovingObjectPosition(var4);
            }
        }

        if (var3 != null)
        {
            if (var3.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && worldObj.getBlock(var3.blockX, var3.blockY, var3.blockZ) == Blocks.portal)
            {
                setInPortal();
            }
            else
            {
                onImpact(var3);
            }
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float var16 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

        for (rotationPitch = (float)(Math.atan2(motionY, var16) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
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
        float var17 = 0.99F;
        float var18 = getGravityVelocity();

        if (isInWater())
        {
            for (int var7 = 0; var7 < 4; ++var7)
            {
                float var19 = 0.25F;
                worldObj.spawnParticle("bubble", posX - motionX * var19, posY - motionY * var19, posZ - motionZ * var19, motionX, motionY, motionZ);
            }

            var17 = 0.8F;
        }

        motionX *= var17;
        motionY *= var17;
        motionZ *= var17;
        motionY -= var18;
        setPosition(posX, posY, posZ);
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.03F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected abstract void onImpact(MovingObjectPosition var1);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)field_145788_c);
        par1NBTTagCompound.setShort("yTile", (short)field_145786_d);
        par1NBTTagCompound.setShort("zTile", (short)field_145787_e);
        par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(field_145785_f));
        par1NBTTagCompound.setByte("shake", (byte)throwableShake);
        par1NBTTagCompound.setByte("inGround", (byte)(inGround ? 1 : 0));

        if ((throwerName == null || throwerName.length() == 0) && thrower != null && thrower instanceof EntityPlayer)
        {
            throwerName = thrower.getUsername();
        }

        par1NBTTagCompound.setString("ownerName", throwerName == null ? "" : throwerName);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        field_145788_c = par1NBTTagCompound.getShort("xTile");
        field_145786_d = par1NBTTagCompound.getShort("yTile");
        field_145787_e = par1NBTTagCompound.getShort("zTile");
        field_145785_f = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
        throwableShake = par1NBTTagCompound.getByte("shake") & 255;
        inGround = par1NBTTagCompound.getByte("inGround") == 1;
        throwerName = par1NBTTagCompound.getString("ownerName");

        if (throwerName != null && throwerName.length() == 0)
        {
            throwerName = null;
        }
    }

    public EntityLivingBase getThrower()
    {
        if (thrower == null && throwerName != null && throwerName.length() > 0)
        {
            thrower = worldObj.getPlayerEntityByName(throwerName);
        }

        return thrower;
    }
}
