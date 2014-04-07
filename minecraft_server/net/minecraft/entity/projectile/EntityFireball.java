package net.minecraft.entity.projectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityFireball extends Entity
{
    private int field_145795_e = -1;
    private int field_145793_f = -1;
    private int field_145794_g = -1;
    private Block field_145796_h;
    private boolean inGround;
    public EntityLivingBase shootingEntity;
    private int ticksAlive;
    private int ticksInAir;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    private static final String __OBFID = "CL_00001717";

    public EntityFireball(World par1World)
    {
        super(par1World);
        setSize(1.0F, 1.0F);
    }

    protected void entityInit()
    {
    }

    public EntityFireball(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World);
        setSize(1.0F, 1.0F);
        setLocationAndAngles(par2, par4, par6, rotationYaw, rotationPitch);
        setPosition(par2, par4, par6);
        double var14 = MathHelper.sqrt_double(par8 * par8 + par10 * par10 + par12 * par12);
        accelerationX = par8 / var14 * 0.1D;
        accelerationY = par10 / var14 * 0.1D;
        accelerationZ = par12 / var14 * 0.1D;
    }

    public EntityFireball(World par1World, EntityLivingBase par2EntityLivingBase, double par3, double par5, double par7)
    {
        super(par1World);
        shootingEntity = par2EntityLivingBase;
        setSize(1.0F, 1.0F);
        setLocationAndAngles(par2EntityLivingBase.posX, par2EntityLivingBase.posY, par2EntityLivingBase.posZ, par2EntityLivingBase.rotationYaw, par2EntityLivingBase.rotationPitch);
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        motionX = motionY = motionZ = 0.0D;
        par3 += rand.nextGaussian() * 0.4D;
        par5 += rand.nextGaussian() * 0.4D;
        par7 += rand.nextGaussian() * 0.4D;
        double var9 = MathHelper.sqrt_double(par3 * par3 + par5 * par5 + par7 * par7);
        accelerationX = par3 / var9 * 0.1D;
        accelerationY = par5 / var9 * 0.1D;
        accelerationZ = par7 / var9 * 0.1D;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (!worldObj.isClient && (shootingEntity != null && shootingEntity.isDead || !worldObj.blockExists((int)posX, (int)posY, (int)posZ)))
        {
            setDead();
        }
        else
        {
            super.onUpdate();
            setFire(1);

            if (inGround)
            {
                if (worldObj.getBlock(field_145795_e, field_145793_f, field_145794_g) == field_145796_h)
                {
                    ++ticksAlive;

                    if (ticksAlive == 600)
                    {
                        setDead();
                    }

                    return;
                }

                inGround = false;
                motionX *= rand.nextFloat() * 0.2F;
                motionY *= rand.nextFloat() * 0.2F;
                motionZ *= rand.nextFloat() * 0.2F;
                ticksAlive = 0;
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

            Entity var4 = null;
            List var5 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            double var6 = 0.0D;

            for (int var8 = 0; var8 < var5.size(); ++var8)
            {
                Entity var9 = (Entity)var5.get(var8);

                if (var9.canBeCollidedWith() && (!var9.isEntityEqual(shootingEntity) || ticksInAir >= 25))
                {
                    float var10 = 0.3F;
                    AxisAlignedBB var11 = var9.boundingBox.expand(var10, var10, var10);
                    MovingObjectPosition var12 = var11.calculateIntercept(var1, var2);

                    if (var12 != null)
                    {
                        double var13 = var1.distanceTo(var12.hitVec);

                        if (var13 < var6 || var6 == 0.0D)
                        {
                            var4 = var9;
                            var6 = var13;
                        }
                    }
                }
            }

            if (var4 != null)
            {
                var3 = new MovingObjectPosition(var4);
            }

            if (var3 != null)
            {
                onImpact(var3);
            }

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float var15 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float)(Math.atan2(motionZ, motionX) * 180.0D / Math.PI) + 90.0F;

            for (rotationPitch = (float)(Math.atan2(var15, motionY) * 180.0D / Math.PI) - 90.0F; rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
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
            float var16 = getMotionFactor();

            if (isInWater())
            {
                for (int var18 = 0; var18 < 4; ++var18)
                {
                    float var17 = 0.25F;
                    worldObj.spawnParticle("bubble", posX - motionX * var17, posY - motionY * var17, posZ - motionZ * var17, motionX, motionY, motionZ);
                }

                var16 = 0.8F;
            }

            motionX += accelerationX;
            motionY += accelerationY;
            motionZ += accelerationZ;
            motionX *= var16;
            motionY *= var16;
            motionZ *= var16;
            worldObj.spawnParticle("smoke", posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
            setPosition(posX, posY, posZ);
        }
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by
     * the original motion.
     */
    protected float getMotionFactor()
    {
        return 0.95F;
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected abstract void onImpact(MovingObjectPosition var1);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)field_145795_e);
        par1NBTTagCompound.setShort("yTile", (short)field_145793_f);
        par1NBTTagCompound.setShort("zTile", (short)field_145794_g);
        par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(field_145796_h));
        par1NBTTagCompound.setByte("inGround", (byte)(inGround ? 1 : 0));
        par1NBTTagCompound.setTag("direction", newDoubleNBTList(new double[] {motionX, motionY, motionZ}));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        field_145795_e = par1NBTTagCompound.getShort("xTile");
        field_145793_f = par1NBTTagCompound.getShort("yTile");
        field_145794_g = par1NBTTagCompound.getShort("zTile");
        field_145796_h = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
        inGround = par1NBTTagCompound.getByte("inGround") == 1;

        if (par1NBTTagCompound.func_150297_b("direction", 9))
        {
            NBTTagList var2 = par1NBTTagCompound.getTagList("direction", 6);
            motionX = var2.func_150309_d(0);
            motionY = var2.func_150309_d(1);
            motionZ = var2.func_150309_d(2);
        }
        else
        {
            setDead();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through
     * this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    public float getCollisionBorderSize()
    {
        return 1.0F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            setBeenAttacked();

            if (par1DamageSource.getEntity() != null)
            {
                Vec3 var3 = par1DamageSource.getEntity().getLookVec();

                if (var3 != null)
                {
                    motionX = var3.xCoord;
                    motionY = var3.yCoord;
                    motionZ = var3.zCoord;
                    accelerationX = motionX * 0.1D;
                    accelerationY = motionY * 0.1D;
                    accelerationZ = motionZ * 0.1D;
                }

                if (par1DamageSource.getEntity() instanceof EntityLivingBase)
                {
                    shootingEntity = (EntityLivingBase)par1DamageSource.getEntity();
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1)
    {
        return 1.0F;
    }
}
