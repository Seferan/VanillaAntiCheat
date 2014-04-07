package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class EntityEnderCrystal extends Entity
{
    /** Used to create the rotation animation when rendering the crystal. */
    public int innerRotation;
    public int health;
    private static final String __OBFID = "CL_00001658";

    public EntityEnderCrystal(World par1World)
    {
        super(par1World);
        preventEntitySpawning = true;
        setSize(2.0F, 2.0F);
        yOffset = height / 2.0F;
        health = 5;
        innerRotation = rand.nextInt(100000);
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
        dataWatcher.addObject(8, Integer.valueOf(health));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        ++innerRotation;
        dataWatcher.updateObject(8, Integer.valueOf(health));
        int var1 = MathHelper.floor_double(posX);
        int var2 = MathHelper.floor_double(posY);
        int var3 = MathHelper.floor_double(posZ);

        if (worldObj.provider instanceof WorldProviderEnd && worldObj.getBlock(var1, var2, var3) != Blocks.fire)
        {
            worldObj.setBlock(var1, var2, var3, Blocks.fire);
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
     * Returns true if other Entities should be prevented from moving through
     * this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
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
            if (!isDead && !worldObj.isClient)
            {
                health = 0;

                if (health <= 0)
                {
                    setDead();

                    if (!worldObj.isClient)
                    {
                        worldObj.createExplosion((Entity)null, posX, posY, posZ, 6.0F, true);
                    }
                }
            }

            return true;
        }
    }
}
