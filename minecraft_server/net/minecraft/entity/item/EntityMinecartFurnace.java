package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart
{
    private int fuel;
    public double pushX;
    public double pushZ;
    private static final String __OBFID = "CL_00001675";

    public EntityMinecartFurnace(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartFurnace(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public int getMinecartType()
    {
        return 2;
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (fuel > 0)
        {
            --fuel;
        }

        if (fuel <= 0)
        {
            pushX = pushZ = 0.0D;
        }

        setMinecartPowered(fuel > 0);

        if (isMinecartPowered() && rand.nextInt(4) == 0)
        {
            worldObj.spawnParticle("largesmoke", posX, posY + 0.8D, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    public void killMinecart(DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);

        if (!par1DamageSource.isExplosion())
        {
            entityDropItem(new ItemStack(Blocks.furnace, 1), 0.0F);
        }
    }

    protected void func_145821_a(int p_145821_1_, int p_145821_2_, int p_145821_3_, double p_145821_4_, double p_145821_6_, Block p_145821_8_, int p_145821_9_)
    {
        super.func_145821_a(p_145821_1_, p_145821_2_, p_145821_3_, p_145821_4_, p_145821_6_, p_145821_8_, p_145821_9_);
        double var10 = pushX * pushX + pushZ * pushZ;

        if (var10 > 1.0E-4D && motionX * motionX + motionZ * motionZ > 0.001D)
        {
            var10 = MathHelper.sqrt_double(var10);
            pushX /= var10;
            pushZ /= var10;

            if (pushX * motionX + pushZ * motionZ < 0.0D)
            {
                pushX = 0.0D;
                pushZ = 0.0D;
            }
            else
            {
                pushX = motionX;
                pushZ = motionZ;
            }
        }
    }

    protected void applyDrag()
    {
        double var1 = pushX * pushX + pushZ * pushZ;

        if (var1 > 1.0E-4D)
        {
            var1 = MathHelper.sqrt_double(var1);
            pushX /= var1;
            pushZ /= var1;
            double var3 = 0.05D;
            motionX *= 0.800000011920929D;
            motionY *= 0.0D;
            motionZ *= 0.800000011920929D;
            motionX += pushX * var3;
            motionZ += pushZ * var3;
        }
        else
        {
            motionX *= 0.9800000190734863D;
            motionY *= 0.0D;
            motionZ *= 0.9800000190734863D;
        }

        super.applyDrag();
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.getItem() == Items.coal)
        {
            if (!par1EntityPlayer.capabilities.isCreativeMode && --var2.stackSize == 0)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
            }

            fuel += 3600;
        }

        pushX = posX - par1EntityPlayer.posX;
        pushZ = posZ - par1EntityPlayer.posZ;
        return true;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setDouble("PushX", pushX);
        par1NBTTagCompound.setDouble("PushZ", pushZ);
        par1NBTTagCompound.setShort("Fuel", (short)fuel);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        pushX = par1NBTTagCompound.getDouble("PushX");
        pushZ = par1NBTTagCompound.getDouble("PushZ");
        fuel = par1NBTTagCompound.getShort("Fuel");
    }

    protected boolean isMinecartPowered()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    protected void setMinecartPowered(boolean par1)
    {
        if (par1)
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(dataWatcher.getWatchableObjectByte(16) | 1)));
        }
        else
        {
            dataWatcher.updateObject(16, Byte.valueOf((byte)(dataWatcher.getWatchableObjectByte(16) & -2)));
        }
    }

    public Block func_145817_o()
    {
        return Blocks.lit_furnace;
    }

    public int getDefaultDisplayTileData()
    {
        return 2;
    }
}
