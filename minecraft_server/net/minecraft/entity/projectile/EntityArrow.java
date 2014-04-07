package net.minecraft.entity.projectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrow extends Entity implements IProjectile
{
    private int field_145791_d = -1;
    private int field_145792_e = -1;
    private int field_145789_f = -1;
    private Block field_145790_g;
    private int inData;
    private boolean inGround;

    /** 1 if the player can pick up the arrow */
    public int canBePickedUp;

    /** Seems to be some sort of timer for animating an arrow. */
    public int arrowShake;

    /** The owner of this arrow. */
    public Entity shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private double damage = 2.0D;

    /** The amount of knockback an arrow applies when it hits a mob. */
    private int knockbackStrength;
    private static final String __OBFID = "CL_00001715";

    public EntityArrow(World par1World)
    {
        super(par1World);
        renderDistanceWeight = 10.0D;
        setSize(0.5F, 0.5F);
    }

    public EntityArrow(World par1World, double par2, double par4, double par6)
    {
        super(par1World);
        renderDistanceWeight = 10.0D;
        setSize(0.5F, 0.5F);
        setPosition(par2, par4, par6);
        yOffset = 0.0F;
    }

    public EntityArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5)
    {
        super(par1World);
        renderDistanceWeight = 10.0D;
        shootingEntity = par2EntityLivingBase;

        if (par2EntityLivingBase instanceof EntityPlayer)
        {
            canBePickedUp = 1;
        }

        posY = par2EntityLivingBase.posY + par2EntityLivingBase.getEyeHeight() - 0.10000000149011612D;
        double var6 = par3EntityLivingBase.posX - par2EntityLivingBase.posX;
        double var8 = par3EntityLivingBase.boundingBox.minY + par3EntityLivingBase.height / 3.0F - posY;
        double var10 = par3EntityLivingBase.posZ - par2EntityLivingBase.posZ;
        double var12 = MathHelper.sqrt_double(var6 * var6 + var10 * var10);

        if (var12 >= 1.0E-7D)
        {
            float var14 = (float)(Math.atan2(var10, var6) * 180.0D / Math.PI) - 90.0F;
            float var15 = (float)(-(Math.atan2(var8, var12) * 180.0D / Math.PI));
            double var16 = var6 / var12;
            double var18 = var10 / var12;
            setLocationAndAngles(par2EntityLivingBase.posX + var16, posY, par2EntityLivingBase.posZ + var18, var14, var15);
            yOffset = 0.0F;
            float var20 = (float)var12 * 0.2F;
            setThrowableHeading(var6, var8 + var20, var10, par4, par5);
        }
    }

    public EntityArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3)
    {
        super(par1World);
        renderDistanceWeight = 10.0D;
        shootingEntity = par2EntityLivingBase;

        if (par2EntityLivingBase instanceof EntityPlayer)
        {
            canBePickedUp = 1;
        }

        setSize(0.5F, 0.5F);
        setLocationAndAngles(par2EntityLivingBase.posX, par2EntityLivingBase.posY + par2EntityLivingBase.getEyeHeight(), par2EntityLivingBase.posZ, par2EntityLivingBase.rotationYaw, par2EntityLivingBase.rotationPitch);
        posX -= MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        posY -= 0.10000000149011612D;
        posZ -= MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
        motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
        motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI));
        setThrowableHeading(motionX, motionY, motionZ, par3 * 1.5F, 1.0F);
    }

    protected void entityInit()
    {
        dataWatcher.addObject(16, Byte.valueOf((byte)0));
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
        par1 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * par8;
        par3 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * par8;
        par5 += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * par8;
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
        super.onUpdate();

        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
        {
            float var1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float)(Math.atan2(motionY, var1) * 180.0D / Math.PI);
        }

        Block var16 = worldObj.getBlock(field_145791_d, field_145792_e, field_145789_f);

        if (var16.getMaterial() != Material.air)
        {
            var16.setBlockBoundsBasedOnState(worldObj, field_145791_d, field_145792_e, field_145789_f);
            AxisAlignedBB var2 = var16.getCollisionBoundingBoxFromPool(worldObj, field_145791_d, field_145792_e, field_145789_f);

            if (var2 != null && var2.isVecInside(worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ)))
            {
                inGround = true;
            }
        }

        if (arrowShake > 0)
        {
            --arrowShake;
        }

        if (inGround)
        {
            int var18 = worldObj.getBlockMetadata(field_145791_d, field_145792_e, field_145789_f);

            if (var16 == field_145790_g && var18 == inData)
            {
                ++ticksInGround;

                if (ticksInGround == 1200)
                {
                    setDead();
                }
            }
            else
            {
                inGround = false;
                motionX *= rand.nextFloat() * 0.2F;
                motionY *= rand.nextFloat() * 0.2F;
                motionZ *= rand.nextFloat() * 0.2F;
                ticksInGround = 0;
                ticksInAir = 0;
            }
        }
        else
        {
            ++ticksInAir;
            Vec3 var17 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
            Vec3 var3 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);
            MovingObjectPosition var4 = worldObj.func_147447_a(var17, var3, false, true, false);
            var17 = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
            var3 = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);

            if (var4 != null)
            {
                var3 = worldObj.getWorldVec3Pool().getVecFromPool(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);
            }

            Entity var5 = null;
            List var6 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            double var7 = 0.0D;
            int var9;
            float var11;

            for (var9 = 0; var9 < var6.size(); ++var9)
            {
                Entity var10 = (Entity)var6.get(var9);

                if (var10.canBeCollidedWith() && (var10 != shootingEntity || ticksInAir >= 5))
                {
                    var11 = 0.3F;
                    AxisAlignedBB var12 = var10.boundingBox.expand(var11, var11, var11);
                    MovingObjectPosition var13 = var12.calculateIntercept(var17, var3);

                    if (var13 != null)
                    {
                        double var14 = var17.distanceTo(var13.hitVec);

                        if (var14 < var7 || var7 == 0.0D)
                        {
                            var5 = var10;
                            var7 = var14;
                        }
                    }
                }
            }

            if (var5 != null)
            {
                var4 = new MovingObjectPosition(var5);
            }

            if (var4 != null && var4.entityHit != null && var4.entityHit instanceof EntityPlayer)
            {
                EntityPlayer var20 = (EntityPlayer)var4.entityHit;

                if (var20.capabilities.disableDamage || shootingEntity instanceof EntityPlayer && !((EntityPlayer)shootingEntity).canAttackPlayer(var20))
                {
                    var4 = null;
                }
            }

            float var19;
            float var26;

            if (var4 != null)
            {
                if (var4.entityHit != null)
                {
                    var19 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
                    int var23 = MathHelper.ceiling_double_int(var19 * damage);

                    if (getIsCritical())
                    {
                        var23 += rand.nextInt(var23 / 2 + 2);
                    }

                    DamageSource var21 = null;

                    if (shootingEntity == null)
                    {
                        var21 = DamageSource.causeArrowDamage(this, this);
                    }
                    else
                    {
                        var21 = DamageSource.causeArrowDamage(this, shootingEntity);
                    }

                    if (isBurning() && !(var4.entityHit instanceof EntityEnderman))
                    {
                        var4.entityHit.setFire(5);
                    }

                    if (var4.entityHit.attackEntityFrom(var21, var23))
                    {
                        if (var4.entityHit instanceof EntityLivingBase)
                        {
                            EntityLivingBase var24 = (EntityLivingBase)var4.entityHit;

                            if (!worldObj.isClient)
                            {
                                var24.setArrowCountInEntity(var24.getArrowCountInEntity() + 1);
                            }

                            if (knockbackStrength > 0)
                            {
                                var26 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

                                if (var26 > 0.0F)
                                {
                                    var4.entityHit.addVelocity(motionX * knockbackStrength * 0.6000000238418579D / var26, 0.1D, motionZ * knockbackStrength * 0.6000000238418579D / var26);
                                }
                            }

                            if (shootingEntity != null && shootingEntity instanceof EntityLivingBase)
                            {
                                EnchantmentHelper.func_151384_a(var24, shootingEntity);
                                EnchantmentHelper.func_151385_b((EntityLivingBase)shootingEntity, var24);
                            }

                            if (shootingEntity != null && var4.entityHit != shootingEntity && var4.entityHit instanceof EntityPlayer && shootingEntity instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP)shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                            }
                        }

                        playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

                        if (!(var4.entityHit instanceof EntityEnderman))
                        {
                            setDead();
                        }
                    }
                    else
                    {
                        motionX *= -0.10000000149011612D;
                        motionY *= -0.10000000149011612D;
                        motionZ *= -0.10000000149011612D;
                        rotationYaw += 180.0F;
                        prevRotationYaw += 180.0F;
                        ticksInAir = 0;
                    }
                }
                else
                {
                    field_145791_d = var4.blockX;
                    field_145792_e = var4.blockY;
                    field_145789_f = var4.blockZ;
                    field_145790_g = var16;
                    inData = worldObj.getBlockMetadata(field_145791_d, field_145792_e, field_145789_f);
                    motionX = ((float)(var4.hitVec.xCoord - posX));
                    motionY = ((float)(var4.hitVec.yCoord - posY));
                    motionZ = ((float)(var4.hitVec.zCoord - posZ));
                    var19 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
                    posX -= motionX / var19 * 0.05000000074505806D;
                    posY -= motionY / var19 * 0.05000000074505806D;
                    posZ -= motionZ / var19 * 0.05000000074505806D;
                    playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
                    inGround = true;
                    arrowShake = 7;
                    setIsCritical(false);

                    if (field_145790_g.getMaterial() != Material.air)
                    {
                        field_145790_g.onEntityCollidedWithBlock(worldObj, field_145791_d, field_145792_e, field_145789_f, this);
                    }
                }
            }

            if (getIsCritical())
            {
                for (var9 = 0; var9 < 4; ++var9)
                {
                    worldObj.spawnParticle("crit", posX + motionX * var9 / 4.0D, posY + motionY * var9 / 4.0D, posZ + motionZ * var9 / 4.0D, -motionX, -motionY + 0.2D, -motionZ);
                }
            }

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            var19 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

            for (rotationPitch = (float)(Math.atan2(motionY, var19) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
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
            float var22 = 0.99F;
            var11 = 0.05F;

            if (isInWater())
            {
                for (int var25 = 0; var25 < 4; ++var25)
                {
                    var26 = 0.25F;
                    worldObj.spawnParticle("bubble", posX - motionX * var26, posY - motionY * var26, posZ - motionZ * var26, motionX, motionY, motionZ);
                }

                var22 = 0.8F;
            }

            if (isWet())
            {
                extinguish();
            }

            motionX *= var22;
            motionY *= var22;
            motionZ *= var22;
            motionY -= var11;
            setPosition(posX, posY, posZ);
            func_145775_I();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)field_145791_d);
        par1NBTTagCompound.setShort("yTile", (short)field_145792_e);
        par1NBTTagCompound.setShort("zTile", (short)field_145789_f);
        par1NBTTagCompound.setShort("life", (short)ticksInGround);
        par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(field_145790_g));
        par1NBTTagCompound.setByte("inData", (byte)inData);
        par1NBTTagCompound.setByte("shake", (byte)arrowShake);
        par1NBTTagCompound.setByte("inGround", (byte)(inGround ? 1 : 0));
        par1NBTTagCompound.setByte("pickup", (byte)canBePickedUp);
        par1NBTTagCompound.setDouble("damage", damage);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        field_145791_d = par1NBTTagCompound.getShort("xTile");
        field_145792_e = par1NBTTagCompound.getShort("yTile");
        field_145789_f = par1NBTTagCompound.getShort("zTile");
        ticksInGround = par1NBTTagCompound.getShort("life");
        field_145790_g = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
        inData = par1NBTTagCompound.getByte("inData") & 255;
        arrowShake = par1NBTTagCompound.getByte("shake") & 255;
        inGround = par1NBTTagCompound.getByte("inGround") == 1;

        if (par1NBTTagCompound.func_150297_b("damage", 99))
        {
            damage = par1NBTTagCompound.getDouble("damage");
        }

        if (par1NBTTagCompound.func_150297_b("pickup", 99))
        {
            canBePickedUp = par1NBTTagCompound.getByte("pickup");
        }
        else if (par1NBTTagCompound.func_150297_b("player", 99))
        {
            canBePickedUp = par1NBTTagCompound.getBoolean("player") ? 1 : 0;
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer)
    {
        if (!worldObj.isClient && inGround && arrowShake <= 0)
        {
            boolean var2 = canBePickedUp == 1 || canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode;

            if (canBePickedUp == 1 && !par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1)))
            {
                var2 = false;
            }

            if (var2)
            {
                playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, 1);
                setDead();
            }
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public void setDamage(double par1)
    {
        damage = par1;
    }

    public double getDamage()
    {
        return damage;
    }

    /**
     * Sets the amount of knockback the arrow applies when it hits a mob.
     */
    public void setKnockbackStrength(int par1)
    {
        knockbackStrength = par1;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind
     * it.
     */
    public void setIsCritical(boolean par1)
    {
        byte var2 = dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 1)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -2)));
        }
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind
     * it.
     */
    public boolean getIsCritical()
    {
        byte var1 = dataWatcher.getWatchableObjectByte(16);
        return (var1 & 1) != 0;
    }
}
