package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnace extends Slot
{
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer thePlayer;
    private int field_75228_b;
    private static final String __OBFID = "CL_00001749";

    public SlotFurnace(EntityPlayer par1EntityPlayer, IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
        thePlayer = par1EntityPlayer;
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
            field_75228_b += Math.min(par1, getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.onCrafting(par2ItemStack);
        super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
     * not ore and wood. Typically increases an internal count then calls
     * onCrafting(item).
     */
    protected void onCrafting(ItemStack par1ItemStack, int par2)
    {
        field_75228_b += par2;
        this.onCrafting(par1ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
     * not ore and wood.
     */
    protected void onCrafting(ItemStack par1ItemStack)
    {
        par1ItemStack.onCrafting(thePlayer.worldObj, thePlayer, field_75228_b);

        if (!thePlayer.worldObj.isClient)
        {
            int var2 = field_75228_b;
            float var3 = FurnaceRecipes.smelting().func_151398_b(par1ItemStack);
            int var4;

            if (var3 == 0.0F)
            {
                var2 = 0;
            }
            else if (var3 < 1.0F)
            {
                var4 = MathHelper.floor_float(var2 * var3);

                if (var4 < MathHelper.ceiling_float_int(var2 * var3) && (float)Math.random() < var2 * var3 - var4)
                {
                    ++var4;
                }

                var2 = var4;
            }

            while (var2 > 0)
            {
                var4 = EntityXPOrb.getXPSplit(var2);
                var2 -= var4;
                thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(thePlayer.worldObj, thePlayer.posX, thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, var4));
            }
        }

        field_75228_b = 0;

        if (par1ItemStack.getItem() == Items.iron_ingot)
        {
            thePlayer.addStat(AchievementList.acquireIron, 1);
        }

        if (par1ItemStack.getItem() == Items.cooked_fished)
        {
            thePlayer.addStat(AchievementList.cookFish, 1);
        }
    }
}
