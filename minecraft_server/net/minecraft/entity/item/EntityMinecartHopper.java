package net.minecraft.entity.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper
{
    /** Whether this hopper minecart is being blocked by an activator rail. */
    private boolean isBlocked = true;
    private int transferTicker = -1;
    private static final String __OBFID = "CL_00001676";

    public EntityMinecartHopper(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartHopper(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public int getMinecartType()
    {
        return 5;
    }

    public Block func_145817_o()
    {
        return Blocks.hopper;
    }

    public int getDefaultDisplayTileOffset()
    {
        return 1;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 5;
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if (!worldObj.isClient)
        {
            par1EntityPlayer.displayGUIHopperMinecart(this);
        }

        return true;
    }

    /**
     * Called every tick the minecart is on an activator rail. Args: x, y, z, is
     * the rail receiving power
     */
    public void onActivatorRailPass(int par1, int par2, int par3, boolean par4)
    {
        boolean var5 = !par4;

        if (var5 != getBlocked())
        {
            setBlocked(var5);
        }
    }

    /**
     * Get whether this hopper minecart is being blocked by an activator rail.
     */
    public boolean getBlocked()
    {
        return isBlocked;
    }

    /**
     * Set whether this hopper minecart is being blocked by an activator rail.
     */
    public void setBlocked(boolean par1)
    {
        isBlocked = par1;
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorldObj()
    {
        return worldObj;
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    public double getXPos()
    {
        return posX;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos()
    {
        return posY;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos()
    {
        return posZ;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!worldObj.isClient && isEntityAlive() && getBlocked())
        {
            --transferTicker;

            if (!canTransfer())
            {
                setTransferTicker(0);

                if (func_96112_aD())
                {
                    setTransferTicker(4);
                    onInventoryChanged();
                }
            }
        }
    }

    public boolean func_96112_aD()
    {
        if (TileEntityHopper.func_145891_a(this))
        {
            return true;
        }
        else
        {
            List var1 = worldObj.selectEntitiesWithinAABB(EntityItem.class, boundingBox.expand(0.25D, 0.0D, 0.25D), IEntitySelector.selectAnything);

            if (var1.size() > 0)
            {
                TileEntityHopper.func_145898_a(this, (EntityItem)var1.get(0));
            }

            return false;
        }
    }

    public void killMinecart(DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);
        func_145778_a(Item.getItemFromBlock(Blocks.hopper), 1, 0.0F);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("TransferCooldown", transferTicker);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        transferTicker = par1NBTTagCompound.getInteger("TransferCooldown");
    }

    /**
     * Sets the transfer ticker, used to determine the delay between transfers.
     */
    public void setTransferTicker(int par1)
    {
        transferTicker = par1;
    }

    /**
     * Returns whether the hopper cart can currently transfer an item.
     */
    public boolean canTransfer()
    {
        return transferTicker > 0;
    }
}
