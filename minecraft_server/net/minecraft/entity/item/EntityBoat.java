package net.minecraft.entity.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity
{
    /** true if no player in boat */
    private boolean isBoatEmpty;
    private double speedMultiplier;
    private int boatPosRotationIncrements;
    private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    private static final String __OBFID = "CL_00001667";

    public EntityBoat(World par1World)
    {
        super(par1World);
        isBoatEmpty = true;
        speedMultiplier = 0.07D;
        preventEntitySpawning = true;
        setSize(1.5F, 0.6F);
        yOffset = height / 2.0F;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        dataWatcher.addObject(17, new Integer(0));
        dataWatcher.addObject(18, new Integer(1));
        dataWatcher.addObject(19, new Float(0.0F));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and
     * blocks. This enables the entity to be pushable on contact, like boats or
     * minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return par1Entity.boundingBox;
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities
     * when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    public EntityBoat(World par1World, double par2, double par4, double par6)
    {
        this(par1World);
        setPosition(par2, par4 + yOffset, par6);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = par2;
        prevPosY = par4;
        prevPosZ = par6;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding
     * this one.
     */
    public double getMountedYOffset()
    {
        return height * 0.0D - 0.30000001192092896D;
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
        else if (!worldObj.isClient && !isDead)
        {
            setForwardDirection(-getForwardDirection());
            setTimeSinceHit(10);
            setDamageTaken(getDamageTaken() + par2 * 10.0F);
            setBeenAttacked();
            boolean var3 = par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode;

            if (var3 || getDamageTaken() > 40.0F)
            {
                if (riddenByEntity != null)
                {
                    riddenByEntity.mountEntity(this);
                }

                if (!var3)
                {
                    func_145778_a(Items.boat, 1, 0.0F);
                }

                setDead();
            }

            return true;
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through
     * this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (getTimeSinceHit() > 0)
        {
            setTimeSinceHit(getTimeSinceHit() - 1);
        }

        if (getDamageTaken() > 0.0F)
        {
            setDamageTaken(getDamageTaken() - 1.0F);
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        byte var1 = 5;
        double var2 = 0.0D;

        for (int var4 = 0; var4 < var1; ++var4)
        {
            double var5 = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * (var4 + 0) / var1 - 0.125D;
            double var7 = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * (var4 + 1) / var1 - 0.125D;
            AxisAlignedBB var9 = AxisAlignedBB.getAABBPool().getAABB(boundingBox.minX, var5, boundingBox.minZ, boundingBox.maxX, var7, boundingBox.maxZ);

            if (worldObj.isAABBInMaterial(var9, Material.field_151586_h))
            {
                var2 += 1.0D / var1;
            }
        }

        double var19 = Math.sqrt(motionX * motionX + motionZ * motionZ);
        double var6;
        double var8;
        int var10;

        if (var19 > 0.26249999999999996D)
        {
            var6 = Math.cos(rotationYaw * Math.PI / 180.0D);
            var8 = Math.sin(rotationYaw * Math.PI / 180.0D);

            for (var10 = 0; var10 < 1.0D + var19 * 60.0D; ++var10)
            {
                double var11 = rand.nextFloat() * 2.0F - 1.0F;
                double var13 = (rand.nextInt(2) * 2 - 1) * 0.7D;
                double var15;
                double var17;

                if (rand.nextBoolean())
                {
                    var15 = posX - var6 * var11 * 0.8D + var8 * var13;
                    var17 = posZ - var8 * var11 * 0.8D - var6 * var13;
                    worldObj.spawnParticle("splash", var15, posY - 0.125D, var17, motionX, motionY, motionZ);
                }
                else
                {
                    var15 = posX + var6 + var8 * var11 * 0.7D;
                    var17 = posZ + var8 - var6 * var11 * 0.7D;
                    worldObj.spawnParticle("splash", var15, posY - 0.125D, var17, motionX, motionY, motionZ);
                }
            }
        }

        double var24;
        double var26;

        if (worldObj.isClient && isBoatEmpty)
        {
            if (boatPosRotationIncrements > 0)
            {
                var6 = posX + (boatX - posX) / boatPosRotationIncrements;
                var8 = posY + (boatY - posY) / boatPosRotationIncrements;
                var24 = posZ + (boatZ - posZ) / boatPosRotationIncrements;
                var26 = MathHelper.wrapAngleTo180_double(boatYaw - rotationYaw);
                rotationYaw = (float)(rotationYaw + var26 / boatPosRotationIncrements);
                rotationPitch = (float)(rotationPitch + (boatPitch - rotationPitch) / boatPosRotationIncrements);
                --boatPosRotationIncrements;
                setPosition(var6, var8, var24);
                setRotation(rotationYaw, rotationPitch);
            }
            else
            {
                var6 = posX + motionX;
                var8 = posY + motionY;
                var24 = posZ + motionZ;
                setPosition(var6, var8, var24);

                if (onGround)
                {
                    motionX *= 0.5D;
                    motionY *= 0.5D;
                    motionZ *= 0.5D;
                }

                motionX *= 0.9900000095367432D;
                motionY *= 0.949999988079071D;
                motionZ *= 0.9900000095367432D;
            }
        }
        else
        {
            if (var2 < 1.0D)
            {
                var6 = var2 * 2.0D - 1.0D;
                motionY += 0.03999999910593033D * var6;
            }
            else
            {
                if (motionY < 0.0D)
                {
                    motionY /= 2.0D;
                }

                motionY += 0.007000000216066837D;
            }

            if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase)
            {
                EntityLivingBase var20 = (EntityLivingBase)riddenByEntity;
                float var21 = riddenByEntity.rotationYaw + -var20.moveStrafing * 90.0F;
                motionX += -Math.sin(var21 * (float)Math.PI / 180.0F) * speedMultiplier * var20.moveForward * 0.05000000074505806D;
                motionZ += Math.cos(var21 * (float)Math.PI / 180.0F) * speedMultiplier * var20.moveForward * 0.05000000074505806D;
            }

            var6 = Math.sqrt(motionX * motionX + motionZ * motionZ);

            if (var6 > 0.35D)
            {
                var8 = 0.35D / var6;
                motionX *= var8;
                motionZ *= var8;
                var6 = 0.35D;
            }

            if (var6 > var19 && speedMultiplier < 0.35D)
            {
                speedMultiplier += (0.35D - speedMultiplier) / 35.0D;

                if (speedMultiplier > 0.35D)
                {
                    speedMultiplier = 0.35D;
                }
            }
            else
            {
                speedMultiplier -= (speedMultiplier - 0.07D) / 35.0D;

                if (speedMultiplier < 0.07D)
                {
                    speedMultiplier = 0.07D;
                }
            }

            int var22;

            for (var22 = 0; var22 < 4; ++var22)
            {
                int var23 = MathHelper.floor_double(posX + (var22 % 2 - 0.5D) * 0.8D);
                var10 = MathHelper.floor_double(posZ + (var22 / 2 - 0.5D) * 0.8D);

                for (int var25 = 0; var25 < 2; ++var25)
                {
                    int var12 = MathHelper.floor_double(posY) + var25;
                    Block var27 = worldObj.getBlock(var23, var12, var10);

                    if (var27 == Blocks.snow_layer)
                    {
                        worldObj.setBlockToAir(var23, var12, var10);
                        isCollidedHorizontally = false;
                    }
                    else if (var27 == Blocks.waterlily)
                    {
                        worldObj.func_147480_a(var23, var12, var10, true);
                        isCollidedHorizontally = false;
                    }
                }
            }

            if (onGround)
            {
                motionX *= 0.5D;
                motionY *= 0.5D;
                motionZ *= 0.5D;
            }

            moveEntity(motionX, motionY, motionZ);

            if (isCollidedHorizontally && var19 > 0.2D)
            {
                if (!worldObj.isClient && !isDead)
                {
                    setDead();

                    for (var22 = 0; var22 < 3; ++var22)
                    {
                        func_145778_a(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                    }

                    for (var22 = 0; var22 < 2; ++var22)
                    {
                        func_145778_a(Items.stick, 1, 0.0F);
                    }
                }
            }
            else
            {
                motionX *= 0.9900000095367432D;
                motionY *= 0.949999988079071D;
                motionZ *= 0.9900000095367432D;
            }

            rotationPitch = 0.0F;
            var8 = rotationYaw;
            var24 = prevPosX - posX;
            var26 = prevPosZ - posZ;

            if (var24 * var24 + var26 * var26 > 0.001D)
            {
                var8 = ((float)(Math.atan2(var26, var24) * 180.0D / Math.PI));
            }

            double var14 = MathHelper.wrapAngleTo180_double(var8 - rotationYaw);

            if (var14 > 20.0D)
            {
                var14 = 20.0D;
            }

            if (var14 < -20.0D)
            {
                var14 = -20.0D;
            }

            rotationYaw = (float)(rotationYaw + var14);
            setRotation(rotationYaw, rotationPitch);

            if (!worldObj.isClient)
            {
                List var16 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

                if (var16 != null && !var16.isEmpty())
                {
                    for (int var28 = 0; var28 < var16.size(); ++var28)
                    {
                        Entity var18 = (Entity)var16.get(var28);

                        if (var18 != riddenByEntity && var18.canBePushed() && var18 instanceof EntityBoat)
                        {
                            var18.applyEntityCollision(this);
                        }
                    }
                }

                if (riddenByEntity != null && riddenByEntity.isDead)
                {
                    riddenByEntity = null;
                }
            }
        }
    }

    public void updateRiderPosition()
    {
        if (riddenByEntity != null)
        {
            double var1 = Math.cos(rotationYaw * Math.PI / 180.0D) * 0.4D;
            double var3 = Math.sin(rotationYaw * Math.PI / 180.0D) * 0.4D;
            riddenByEntity.setPosition(posX + var1, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ + var3);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && riddenByEntity != par1EntityPlayer)
        {
            return true;
        }
        else
        {
            if (!worldObj.isClient)
            {
                par1EntityPlayer.mountEntity(this);
            }

            return true;
        }
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on
     * the ground to update the fall distance and deal fall damage if landing on
     * the ground. Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
        int var4 = MathHelper.floor_double(posX);
        int var5 = MathHelper.floor_double(posY);
        int var6 = MathHelper.floor_double(posZ);

        if (par3)
        {
            if (fallDistance > 3.0F)
            {
                fall(fallDistance);

                if (!worldObj.isClient && !isDead)
                {
                    setDead();
                    int var7;

                    for (var7 = 0; var7 < 3; ++var7)
                    {
                        func_145778_a(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                    }

                    for (var7 = 0; var7 < 2; ++var7)
                    {
                        func_145778_a(Items.stick, 1, 0.0F);
                    }
                }

                fallDistance = 0.0F;
            }
        }
        else if (worldObj.getBlock(var4, var5 - 1, var6).getMaterial() != Material.field_151586_h && par1 < 0.0D)
        {
            fallDistance = (float)(fallDistance - par1);
        }
    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setDamageTaken(float par1)
    {
        dataWatcher.updateObject(19, Float.valueOf(par1));
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken()
    {
        return dataWatcher.getWatchableObjectFloat(19);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int par1)
    {
        dataWatcher.updateObject(17, Integer.valueOf(par1));
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return dataWatcher.getWatchableObjectInt(17);
    }

    /**
     * Sets the forward direction of the entity.
     */
    public void setForwardDirection(int par1)
    {
        dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    /**
     * Gets the forward direction of the entity.
     */
    public int getForwardDirection()
    {
        return dataWatcher.getWatchableObjectInt(18);
    }
}
