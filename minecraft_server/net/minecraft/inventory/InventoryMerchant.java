package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory
{
    private final IMerchant theMerchant;
    private ItemStack[] theInventory = new ItemStack[3];
    private final EntityPlayer thePlayer;
    private MerchantRecipe currentRecipe;
    private int currentRecipeIndex;
    private static final String __OBFID = "CL_00001756";

    public InventoryMerchant(EntityPlayer par1EntityPlayer, IMerchant par2IMerchant)
    {
        thePlayer = par1EntityPlayer;
        theMerchant = par2IMerchant;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return theInventory.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return theInventory[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (theInventory[par1] != null)
        {
            ItemStack var3;

            if (par1 == 2)
            {
                var3 = theInventory[par1];
                theInventory[par1] = null;
                return var3;
            }
            else if (theInventory[par1].stackSize <= par2)
            {
                var3 = theInventory[par1];
                theInventory[par1] = null;

                if (inventoryResetNeededOnSlotChange(par1))
                {
                    resetRecipeAndSlots();
                }

                return var3;
            }
            else
            {
                var3 = theInventory[par1].splitStack(par2);

                if (theInventory[par1].stackSize == 0)
                {
                    theInventory[par1] = null;
                }

                if (inventoryResetNeededOnSlotChange(par1))
                {
                    resetRecipeAndSlots();
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
     * if par1 slot has changed, does resetRecipeAndSlots need to be called?
     */
    private boolean inventoryResetNeededOnSlotChange(int par1)
    {
        return par1 == 0 || par1 == 1;
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (theInventory[par1] != null)
        {
            ItemStack var2 = theInventory[par1];
            theInventory[par1] = null;
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
        theInventory[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit())
        {
            par2ItemStack.stackSize = getInventoryStackLimit();
        }

        if (inventoryResetNeededOnSlotChange(par1))
        {
            resetRecipeAndSlots();
        }
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return "mob.villager";
    }

    /**
     * Returns if the inventory name is localized
     */
    public boolean isInventoryNameLocalized()
    {
        return false;
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
        return theMerchant.getCustomer() == par1EntityPlayer;
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
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        resetRecipeAndSlots();
    }

    public void resetRecipeAndSlots()
    {
        currentRecipe = null;
        ItemStack var1 = theInventory[0];
        ItemStack var2 = theInventory[1];

        if (var1 == null)
        {
            var1 = var2;
            var2 = null;
        }

        if (var1 == null)
        {
            setInventorySlotContents(2, (ItemStack)null);
        }
        else
        {
            MerchantRecipeList var3 = theMerchant.getRecipes(thePlayer);

            if (var3 != null)
            {
                MerchantRecipe var4 = var3.canRecipeBeUsed(var1, var2, currentRecipeIndex);

                if (var4 != null && !var4.func_82784_g())
                {
                    currentRecipe = var4;
                    setInventorySlotContents(2, var4.getItemToSell().copy());
                }
                else if (var2 != null)
                {
                    var4 = var3.canRecipeBeUsed(var2, var1, currentRecipeIndex);

                    if (var4 != null && !var4.func_82784_g())
                    {
                        currentRecipe = var4;
                        setInventorySlotContents(2, var4.getItemToSell().copy());
                    }
                    else
                    {
                        setInventorySlotContents(2, (ItemStack)null);
                    }
                }
                else
                {
                    setInventorySlotContents(2, (ItemStack)null);
                }
            }
        }

        theMerchant.func_110297_a_(getStackInSlot(2));
    }

    public MerchantRecipe getCurrentRecipe()
    {
        return currentRecipe;
    }

    public void setCurrentRecipeIndex(int par1)
    {
        currentRecipeIndex = par1;
        resetRecipeAndSlots();
    }
}
