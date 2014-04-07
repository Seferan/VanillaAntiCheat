package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity
{
    /** How long the fuse is */
    public int fuse;
    private EntityLivingBase tntPlacedBy;
    private static final String __OBFID = "CL_00001681";

    public EntityTNTPrimed(World par1World)
    {
        super(par1World);
        preventEntitySpawning = true;
        setSize(0.98F, 0.98F);
        yOffset = height / 2.0F;
    }

    public EntityTNTPrimed(World par1World, double par2, double par4, double par6, EntityLivingBase par8EntityLivingBase)
    {
        this(par1World);
        setPosition(par2, par4, par6);
        float var9 = (float)(Math.random() * Math.PI * 2.0D);
        motionX = -((float)Math.sin(var9)) * 0.02F;
        motionY = 0.20000000298023224D;
        motionZ = -((float)Math.cos(var9)) * 0.02F;
        fuse = 80;
        prevPosX = par2;
        prevPosY = par4;
        prevPosZ = par6;
        tntPlacedBy = par8EntityLivingBase;
    }

    protected void entityInit()
    {
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
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
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        motionY -= 0.03999999910593033D;
        moveEntity(motionX, motionY, motionZ);
        motionX *= 0.9800000190734863D;
        motionY *= 0.9800000190734863D;
        motionZ *= 0.9800000190734863D;

        if (onGround)
        {
            motionX *= 0.699999988079071D;
            motionZ *= 0.699999988079071D;
            motionY *= -0.5D;
        }

        if (fuse-- <= 0)
        {
            setDead();

            if (!worldObj.isClient)
            {
                explode();
            }
        }
        else
        {
            worldObj.spawnParticle("smoke", posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode()
    {
        float strength = 4.0F;
        if (worldObj.getGameRules().getGameRuleBooleanValue("doTNTExplosion"))
        {
            worldObj.createExplosion(this, posX, posY, posZ, strength, true);
        }
        else
        {
            if (getTntPlacedBy() instanceof EntityPlayerMP)
            {
                if (MinecraftServer.isPlayerOpped((EntityPlayerMP)getTntPlacedBy()))
                {
                    worldObj.createExplosion(this, posX, posY, posZ, strength, true);
                }
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Fuse", (byte)fuse);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        fuse = par1NBTTagCompound.getByte("Fuse");
    }

    /**
     * returns null or the entityliving it was placed or ignited by
     */
    public EntityLivingBase getTntPlacedBy()
    {
        return tntPlacedBy;
    }
}
