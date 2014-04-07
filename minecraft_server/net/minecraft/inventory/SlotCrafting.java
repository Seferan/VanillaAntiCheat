package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;

public class SlotCrafting extends Slot
{
    /** The craft matrix inventory linked to this result slot. */
    private final IInventory craftMatrix;

    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer thePlayer;

    /**
     * The number of items that have been crafted so far. Gets passed to
     * ItemStack.onCrafting before being reset.
     */
    private int amountCrafted;
    private static final String __OBFID = "CL_00001761";

    public SlotCrafting(EntityPlayer par1EntityPlayer, IInventory par2IInventory, IInventory par3IInventory, int par4, int par5, int par6)
    {
        super(par3IInventory, par4, par5, par6);
        thePlayer = par1EntityPlayer;
        craftMatrix = par2IInventory;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for
     * the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of
     * the second int arg. Returns the new stack.
     */
    public ItemStack decrStackSize(int par1)
    {
        if (getHasStack())
        {
            amountCrafted += Math.min(par1, getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
     * not ore and wood. Typically increases an internal count then calls
     * onCrafting(item).
     */
    protected void onCrafting(ItemStack par1ItemStack, int par2)
    {
        amountCrafted += par2;
        this.onCrafting(par1ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
     * not ore and wood.
     */
    protected void onCrafting(ItemStack par1ItemStack)
    {
        par1ItemStack.onCrafting(thePlayer.worldObj, thePlayer, amountCrafted);
        amountCrafted = 0;

        if (par1ItemStack.getItem() == Item.getItemFromBlock(Blocks.crafting_table))
        {
            thePlayer.addStat(AchievementList.buildWorkBench, 1);
        }

        if (par1ItemStack.getItem() instanceof ItemPickaxe)
        {
            thePlayer.addStat(AchievementList.buildPickaxe, 1);
        }

        if (par1ItemStack.getItem() == Item.getItemFromBlock(Blocks.furnace))
        {
            thePlayer.addStat(AchievementList.buildFurnace, 1);
        }

        if (par1ItemStack.getItem() instanceof ItemHoe)
        {
            thePlayer.addStat(AchievementList.buildHoe, 1);
        }

        if (par1ItemStack.getItem() == Items.bread)
        {
            thePlayer.addStat(AchievementList.makeBread, 1);
        }

        if (par1ItemStack.getItem() == Items.cake)
        {
            thePlayer.addStat(AchievementList.bakeCake, 1);
        }

        if (par1ItemStack.getItem() instanceof ItemPickaxe && ((ItemPickaxe)par1ItemStack.getItem()).func_150913_i() != Item.ToolMaterial.WOOD)
        {
            thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
        }

        if (par1ItemStack.getItem() instanceof ItemSword)
        {
            thePlayer.addStat(AchievementList.buildSword, 1);
        }

        if (par1ItemStack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table))
        {
            thePlayer.addStat(AchievementList.enchantments, 1);
        }

        if (par1ItemStack.getItem() == Item.getItemFromBlock(Blocks.bookshelf))
        {
            thePlayer.addStat(AchievementList.bookcase, 1);
        }
    }

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.onCrafting(par2ItemStack);

        for (int var3 = 0; var3 < craftMatrix.getSizeInventory(); ++var3)
        {
            ItemStack var4 = craftMatrix.getStackInSlot(var3);

            if (var4 != null)
            {
                craftMatrix.decrStackSize(var3, 1);

                if (var4.getItem().hasContainerItem())
                {
                    ItemStack var5 = new ItemStack(var4.getItem().getContainerItem());

                    if (!var4.getItem().doesContainerItemLeaveCraftingGrid(var4) || !thePlayer.inventory.addItemStackToInventory(var5))
                    {
                        if (craftMatrix.getStackInSlot(var3) == null)
                        {
                            craftMatrix.setInventorySlotContents(var3, var5);
                        }
                        else
                        {
                            thePlayer.dropPlayerItemWithRandomChoice(var5, false);
                        }
                    }
                }
            }
        }
    }
}
