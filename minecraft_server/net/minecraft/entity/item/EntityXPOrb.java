package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity
{
    /**
     * A constantly increasing value that RenderXPOrb uses to control the colour
     * shifting (Green / yellow)
     */
    public int xpColor;

    /** The age of the XP orb in ticks. */
    public int xpOrbAge;
    public int field_70532_c;

    /** The health of this XP orb. */
    private int xpOrbHealth = 5;

    /** This is how much XP this orb has. */
    private int xpValue;

    /** The closest EntityPlayer to this orb. */
    private EntityPlayer closestPlayer;

    /** Threshold color for tracking players */
    private int xpTargetColor;
    private static final String __OBFID = "CL_00001544";

    public EntityXPOrb(World par1World, double par2, double par4, double par6, int par8)
    {
        super(par1World);
        setSize(0.5F, 0.5F);
        yOffset = height / 2.0F;
        setPosition(par2, par4, par6);
        rotationYaw = (float)(Math.random() * 360.0D);
        motionX = (float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F;
        motionY = (float)(Math.random() * 0.2D) * 2.0F;
        motionZ = (float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F;
        xpValue = par8;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityXPOrb(World par1World)
    {
        super(par1World);
        setSize(0.25F, 0.25F);
        yOffset = height / 2.0F;
    }

    protected void entityInit()
    {
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (field_70532_c > 0)
        {
            --field_70532_c;
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        motionY -= 0.029999999329447746D;

        if (worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)).getMaterial() == Material.field_151587_i)
        {
            motionY = 0.20000000298023224D;
            motionX = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            motionZ = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            playSound("random.fizz", 0.4F, 2.0F + rand.nextFloat() * 0.4F);
        }

        func_145771_j(posX, (boundingBox.minY + boundingBox.maxY) / 2.0D, posZ);
        double var1 = 8.0D;

        if (xpTargetColor < xpColor - 20 + getEntityId() % 100)
        {
            if (closestPlayer == null || closestPlayer.getDistanceSqToEntity(this) > var1 * var1)
            {
                closestPlayer = worldObj.getClosestPlayerToEntity(this, var1);
            }

            xpTargetColor = xpColor;
        }

        if (closestPlayer != null)
        {
            double var3 = (closestPlayer.posX - posX) / var1;
            double var5 = (closestPlayer.posY + closestPlayer.getEyeHeight() - posY) / var1;
            double var7 = (closestPlayer.posZ - posZ) / var1;
            double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
            double var11 = 1.0D - var9;

            if (var11 > 0.0D)
            {
                var11 *= var11;
                motionX += var3 / var9 * var11 * 0.1D;
                motionY += var5 / var9 * var11 * 0.1D;
                motionZ += var7 / var9 * var11 * 0.1D;
            }
        }

        moveEntity(motionX, motionY, motionZ);
        float var13 = 0.98F;

        if (onGround)
        {
            var13 = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)).slipperiness * 0.98F;
        }

        motionX *= var13;
        motionY *= 0.9800000190734863D;
        motionZ *= var13;

        if (onGround)
        {
            motionY *= -0.8999999761581421D;
        }

        ++xpColor;
        ++xpOrbAge;

        if (xpOrbAge >= 6000)
        {
            setDead();
        }
    }

    /**
     * Returns if this entity is in water and will end up adding the waters
     * velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.field_151586_h, this);
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity
     * isn't immune to fire damage. Args: amountDamage
     */
    protected void dealFireDamage(int par1)
    {
        attackEntityFrom(DamageSource.inFire, par1);
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
            xpOrbHealth = (int)(xpOrbHealth - par2);

            if (xpOrbHealth <= 0)
            {
                setDead();
            }

            return false;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("Health", ((byte)xpOrbHealth));
        par1NBTTagCompound.setShort("Age", (short)xpOrbAge);
        par1NBTTagCompound.setShort("Value", (short)xpValue);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        xpOrbHealth = par1NBTTagCompound.getShort("Health") & 255;
        xpOrbAge = par1NBTTagCompound.getShort("Age");
        xpValue = par1NBTTagCompound.getShort("Value");
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer)
    {
        if (!worldObj.isClient)
        {
            if (field_70532_c == 0 && par1EntityPlayer.xpCooldown == 0)
            {
                par1EntityPlayer.xpCooldown = 2;
                worldObj.playSoundAtEntity(par1EntityPlayer, "random.orb", 0.1F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
                par1EntityPlayer.onItemPickup(this, 1);
                par1EntityPlayer.addExperience(xpValue);
                setDead();
            }
        }
    }

    /**
     * Returns the XP value of this XP orb.
     */
    public int getXpValue()
    {
        return xpValue;
    }

    /**
     * Get a fragment of the maximum experience points value for the supplied
     * value of experience points value.
     */
    public static int getXPSplit(int par0)
    {
        return par0 >= 2477 ? 2477 : (par0 >= 1237 ? 1237 : (par0 >= 617 ? 617 : (par0 >= 307 ? 307 : (par0 >= 149 ? 149 : (par0 >= 73 ? 73 : (par0 >= 37 ? 37 : (par0 >= 17 ? 17 : (par0 >= 7 ? 7 : (par0 >= 3 ? 3 : 1)))))))));
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }
}
