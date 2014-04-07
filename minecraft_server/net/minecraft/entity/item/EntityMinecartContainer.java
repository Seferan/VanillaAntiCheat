package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements IInventory
{
    private ItemStack[] minecartContainerItems = new ItemStack[36];

    /**
     * When set to true, the minecart will drop all items when setDead() is
     * called. When false (such as when travelling dimensions) it preserves its
     * contents.
     */
    private boolean dropContentsWhenDead = true;
    private static final String __OBFID = "CL_00001674";

    public EntityMinecartContainer(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartContainer(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public void killMinecart(DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);

        for (int var2 = 0; var2 < getSizeInventory(); ++var2)
        {
            ItemStack var3 = getStackInSlot(var2);

            if (var3 != null)
            {
                float var4 = rand.nextFloat() * 0.8F + 0.1F;
                float var5 = rand.nextFloat() * 0.8F + 0.1F;
                float var6 = rand.nextFloat() * 0.8F + 0.1F;

                while (var3.stackSize > 0)
                {
                    int var7 = rand.nextInt(21) + 10;

                    if (var7 > var3.stackSize)
                    {
                        var7 = var3.stackSize;
                    }

                    var3.stackSize -= var7;
                    EntityItem var8 = new EntityItem(worldObj, posX + var4, posY + var5, posZ + var6, new ItemStack(var3.getItem(), var7, var3.getItemDamage()));
                    float var9 = 0.05F;
                    var8.motionX = (float)rand.nextGaussian() * var9;
                    var8.motionY = (float)rand.nextGaussian() * var9 + 0.2F;
                    var8.motionZ = (float)rand.nextGaussian() * var9;
                    worldObj.spawnEntityInWorld(var8);
                }
            }
        }
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return minecartContainerItems[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (minecartContainerItems[par1] != null)
        {
            ItemStack var3;

            if (minecartContainerItems[par1].stackSize <= par2)
            {
                var3 = minecartContainerItems[par1];
                minecartContainerItems[par1] = null;
                return var3;
            }
            else
            {
                var3 = minecartContainerItems[par1].splitStack(par2);

                if (minecartContainerItems[par1].stackSize == 0)
                {
                    minecartContainerItems[par1] = null;
                }

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
        if (minecartContainerItems[par1] != null)
        {
            ItemStack var2 = minecartContainerItems[par1];
            minecartContainerItems[par1] = null;
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
        minecartContainerItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit())
        {
            par2ItemStack.stackSize = getInventoryStackLimit();
        }
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this) <= 64.0D;
    }

    public void openChest()
    {
    }

    public void closeChest()
    {
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
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return isInventoryNameLocalized() ? func_95999_t() : "container.minecart";
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
     * Teleports the entity to another dimension. Params: Dimension number to
     * teleport to
     */
    public void travelToDimension(int par1)
    {
        dropContentsWhenDead = false;
        super.travelToDimension(par1);
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        if (dropContentsWhenDead)
        {
            for (int var1 = 0; var1 < getSizeInventory(); ++var1)
            {
                ItemStack var2 = getStackInSlot(var1);

                if (var2 != null)
                {
                    float var3 = rand.nextFloat() * 0.8F + 0.1F;
                    float var4 = rand.nextFloat() * 0.8F + 0.1F;
                    float var5 = rand.nextFloat() * 0.8F + 0.1F;

                    while (var2.stackSize > 0)
                    {
                        int var6 = rand.nextInt(21) + 10;

                        if (var6 > var2.stackSize)
                        {
                            var6 = var2.stackSize;
                        }

                        var2.stackSize -= var6;
                        EntityItem var7 = new EntityItem(worldObj, posX + var3, posY + var4, posZ + var5, new ItemStack(var2.getItem(), var6, var2.getItemDamage()));

                        if (var2.hasTagCompound())
                        {
                            var7.getEntityItem().setTagCompound((NBTTagCompound)var2.getTagCompound().copy());
                        }

                        float var8 = 0.05F;
                        var7.motionX = (float)rand.nextGaussian() * var8;
                        var7.motionY = (float)rand.nextGaussian() * var8 + 0.2F;
                        var7.motionZ = (float)rand.nextGaussian() * var8;
                        worldObj.spawnEntityInWorld(var7);
                    }
                }
            }
        }

        super.setDead();
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < minecartContainerItems.length; ++var3)
        {
            if (minecartContainerItems[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                minecartContainerItems[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
        minecartContainerItems = new ItemStack[getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < minecartContainerItems.length)
            {
                minecartContainerItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if (!worldObj.isClient)
        {
            par1EntityPlayer.displayGUIChest(this);
        }

        return true;
    }

    protected void applyDrag()
    {
        int var1 = 15 - Container.calcRedstoneFromInventory(this);
        float var2 = 0.98F + var1 * 0.001F;
        motionX *= var2;
        motionY *= 0.0D;
        motionZ *= var2;
    }
}
