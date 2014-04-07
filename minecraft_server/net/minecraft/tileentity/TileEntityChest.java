package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityChest extends TileEntity implements IInventory
{
    private ItemStack[] field_145985_p = new ItemStack[36];
    public boolean field_145984_a;
    public TileEntityChest field_145992_i;
    public TileEntityChest field_145990_j;
    public TileEntityChest field_145991_k;
    public TileEntityChest field_145988_l;
    public float field_145989_m;
    public float field_145986_n;
    public int field_145987_o;
    private int field_145983_q;
    private int field_145982_r = -1;
    private String field_145981_s;
    private static final String __OBFID = "CL_00000346";

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 27;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return field_145985_p[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (field_145985_p[par1] != null)
        {
            ItemStack var3;

            if (field_145985_p[par1].stackSize <= par2)
            {
                var3 = field_145985_p[par1];
                field_145985_p[par1] = null;
                onInventoryChanged();
                return var3;
            }
            else
            {
                var3 = field_145985_p[par1].splitStack(par2);

                if (field_145985_p[par1].stackSize == 0)
                {
                    field_145985_p[par1] = null;
                }

                onInventoryChanged();
                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (field_145985_p[par1] != null)
        {
            ItemStack var2 = field_145985_p[par1];
            field_145985_p[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        field_145985_p[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit())
        {
            par2ItemStack.stackSize = getInventoryStackLimit();
        }

        onInventoryChanged();
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return isInventoryNameLocalized() ? field_145981_s : "container.chest";
    }

    /**
     * Returns if the inventory name is localized
     */
    public boolean isInventoryNameLocalized()
    {
        return field_145981_s != null && field_145981_s.length() > 0;
    }

    public void func_145976_a(String p_145976_1_)
    {
        field_145981_s = p_145976_1_;
    }

    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        super.readFromNBT(p_145839_1_);
        NBTTagList var2 = p_145839_1_.getTagList("Items", 10);
        field_145985_p = new ItemStack[getSizeInventory()];

        if (p_145839_1_.func_150297_b("CustomName", 8))
        {
            field_145981_s = p_145839_1_.getString("CustomName");
        }

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < field_145985_p.length)
            {
                field_145985_p[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }

    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        super.writeToNBT(p_145841_1_);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < field_145985_p.length; ++var3)
        {
            if (field_145985_p[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                field_145985_p[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        p_145841_1_.setTag("Items", var2);

        if (isInventoryNameLocalized())
        {
            p_145841_1_.setString("CustomName", field_145981_s);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : par1EntityPlayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    public void updateContainingBlockInfo()
    {
        super.updateContainingBlockInfo();
        field_145984_a = false;
    }

    private void func_145978_a(TileEntityChest p_145978_1_, int p_145978_2_)
    {
        if (p_145978_1_.isInvalid())
        {
            field_145984_a = false;
        }
        else if (field_145984_a)
        {
            switch (p_145978_2_)
            {
            case 0:
                if (field_145988_l != p_145978_1_)
                {
                    field_145984_a = false;
                }

                break;

            case 1:
                if (field_145991_k != p_145978_1_)
                {
                    field_145984_a = false;
                }

                break;

            case 2:
                if (field_145992_i != p_145978_1_)
                {
                    field_145984_a = false;
                }

                break;

            case 3:
                if (field_145990_j != p_145978_1_)
                {
                    field_145984_a = false;
                }
            }
        }
    }

    public void func_145979_i()
    {
        if (!field_145984_a)
        {
            field_145984_a = true;
            field_145992_i = null;
            field_145990_j = null;
            field_145991_k = null;
            field_145988_l = null;

            if (func_145977_a(xCoord - 1, yCoord, zCoord))
            {
                field_145991_k = (TileEntityChest)worldObj.getTileEntity(xCoord - 1, yCoord, zCoord);
            }

            if (func_145977_a(xCoord + 1, yCoord, zCoord))
            {
                field_145990_j = (TileEntityChest)worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
            }

            if (func_145977_a(xCoord, yCoord, zCoord - 1))
            {
                field_145992_i = (TileEntityChest)worldObj.getTileEntity(xCoord, yCoord, zCoord - 1);
            }

            if (func_145977_a(xCoord, yCoord, zCoord + 1))
            {
                field_145988_l = (TileEntityChest)worldObj.getTileEntity(xCoord, yCoord, zCoord + 1);
            }

            if (field_145992_i != null)
            {
                field_145992_i.func_145978_a(this, 0);
            }

            if (field_145988_l != null)
            {
                field_145988_l.func_145978_a(this, 2);
            }

            if (field_145990_j != null)
            {
                field_145990_j.func_145978_a(this, 1);
            }

            if (field_145991_k != null)
            {
                field_145991_k.func_145978_a(this, 3);
            }
        }
    }

    private boolean func_145977_a(int p_145977_1_, int p_145977_2_, int p_145977_3_)
    {
        Block var4 = worldObj.getBlock(p_145977_1_, p_145977_2_, p_145977_3_);
        return var4 instanceof BlockChest && ((BlockChest)var4).field_149956_a == func_145980_j();
    }

    public void updateEntity()
    {
        super.updateEntity();
        func_145979_i();
        ++field_145983_q;
        float var1;

        if (!worldObj.isClient && field_145987_o != 0 && (field_145983_q + xCoord + yCoord + zCoord) % 200 == 0)
        {
            field_145987_o = 0;
            var1 = 5.0F;
            List var2 = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord - var1, yCoord - var1, zCoord - var1, xCoord + 1 + var1, yCoord + 1 + var1, zCoord + 1 + var1));
            Iterator var3 = var2.iterator();

            while (var3.hasNext())
            {
                EntityPlayer var4 = (EntityPlayer)var3.next();

                if (var4.openContainer instanceof ContainerChest)
                {
                    IInventory var5 = ((ContainerChest)var4.openContainer).getLowerChestInventory();

                    if (var5 == this || var5 instanceof InventoryLargeChest && ((InventoryLargeChest)var5).isPartOfLargeChest(this))
                    {
                        ++field_145987_o;
                    }
                }
            }
        }

        field_145986_n = field_145989_m;
        var1 = 0.1F;
        double var11;

        if (field_145987_o > 0 && field_145989_m == 0.0F && field_145992_i == null && field_145991_k == null)
        {
            double var8 = xCoord + 0.5D;
            var11 = zCoord + 0.5D;

            if (field_145988_l != null)
            {
                var11 += 0.5D;
            }

            if (field_145990_j != null)
            {
                var8 += 0.5D;
            }

            worldObj.playSoundEffect(var8, yCoord + 0.5D, var11, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (field_145987_o == 0 && field_145989_m > 0.0F || field_145987_o > 0 && field_145989_m < 1.0F)
        {
            float var9 = field_145989_m;

            if (field_145987_o > 0)
            {
                field_145989_m += var1;
            }
            else
            {
                field_145989_m -= var1;
            }

            if (field_145989_m > 1.0F)
            {
                field_145989_m = 1.0F;
            }

            float var10 = 0.5F;

            if (field_145989_m < var10 && var9 >= var10 && field_145992_i == null && field_145991_k == null)
            {
                var11 = xCoord + 0.5D;
                double var6 = zCoord + 0.5D;

                if (field_145988_l != null)
                {
                    var6 += 0.5D;
                }

                if (field_145990_j != null)
                {
                    var11 += 0.5D;
                }

                worldObj.playSoundEffect(var11, yCoord + 0.5D, var6, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (field_145989_m < 0.0F)
            {
                field_145989_m = 0.0F;
            }
        }
    }

    public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
    {
        if (p_145842_1_ == 1)
        {
            field_145987_o = p_145842_2_;
            return true;
        }
        else
        {
            return super.receiveClientEvent(p_145842_1_, p_145842_2_);
        }
    }

    public void openChest()
    {
        if (field_145987_o < 0)
        {
            field_145987_o = 0;
        }

        ++field_145987_o;
        worldObj.func_147452_c(xCoord, yCoord, zCoord, getBlockType(), 1, field_145987_o);
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType());
    }

    public void closeChest()
    {
        if (getBlockType() instanceof BlockChest)
        {
            --field_145987_o;
            worldObj.func_147452_c(xCoord, yCoord, zCoord, getBlockType(), 1, field_145987_o);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType());
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring
     * stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        super.invalidate();
        updateContainingBlockInfo();
        func_145979_i();
    }

    public int func_145980_j()
    {
        if (field_145982_r == -1)
        {
            if (worldObj == null || !(getBlockType() instanceof BlockChest)) { return 0; }

            field_145982_r = ((BlockChest)getBlockType()).field_149956_a;
        }

        return field_145982_r;
    }
}
