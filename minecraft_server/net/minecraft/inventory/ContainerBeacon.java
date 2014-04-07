package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBeacon;

public class ContainerBeacon extends Container
{
    private TileEntityBeacon theBeacon;

    /**
     * This beacon's slot where you put in Emerald, Diamond, Gold or Iron Ingot.
     */
    private final ContainerBeacon.BeaconSlot beaconSlot;
    private int field_82865_g;
    private int field_82867_h;
    private int field_82868_i;
    private static final String __OBFID = "CL_00001735";

    public ContainerBeacon(InventoryPlayer par1InventoryPlayer, TileEntityBeacon par2TileEntityBeacon)
    {
        theBeacon = par2TileEntityBeacon;
        addSlotToContainer(beaconSlot = new ContainerBeacon.BeaconSlot(par2TileEntityBeacon, 0, 136, 110));
        byte var3 = 36;
        short var4 = 137;
        int var5;

        for (var5 = 0; var5 < 3; ++var5)
        {
            for (int var6 = 0; var6 < 9; ++var6)
            {
                addSlotToContainer(new Slot(par1InventoryPlayer, var6 + var5 * 9 + 9, var3 + var6 * 18, var4 + var5 * 18));
            }
        }

        for (var5 = 0; var5 < 9; ++var5)
        {
            addSlotToContainer(new Slot(par1InventoryPlayer, var5, var3 + var5 * 18, 58 + var4));
        }

        field_82865_g = par2TileEntityBeacon.func_145998_l();
        field_82867_h = par2TileEntityBeacon.func_146007_j();
        field_82868_i = par2TileEntityBeacon.func_146006_k();
    }

    public void onCraftGuiOpened(ICrafting par1ICrafting)
    {
        super.onCraftGuiOpened(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, field_82865_g);
        par1ICrafting.sendProgressBarUpdate(this, 1, field_82867_h);
        par1ICrafting.sendProgressBarUpdate(this, 2, field_82868_i);
    }

    public TileEntityBeacon func_148327_e()
    {
        return theBeacon;
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return theBeacon.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 == 0)
            {
                if (!mergeItemStack(var5, 1, 37, true)) { return null; }

                var4.onSlotChange(var5, var3);
            }
            else if (!beaconSlot.getHasStack() && beaconSlot.isItemValid(var5) && var5.stackSize == 1)
            {
                if (!mergeItemStack(var5, 0, 1, false)) { return null; }
            }
            else if (par2 >= 1 && par2 < 28)
            {
                if (!mergeItemStack(var5, 28, 37, false)) { return null; }
            }
            else if (par2 >= 28 && par2 < 37)
            {
                if (!mergeItemStack(var5, 1, 28, false)) { return null; }
            }
            else if (!mergeItemStack(var5, 1, 37, false)) { return null; }

            if (var5.stackSize == 0)
            {
                var4.putStack((ItemStack)null);
            }
            else
            {
                var4.onSlotChanged();
            }

            if (var5.stackSize == var3.stackSize) { return null; }

            var4.onPickupFromSlot(par1EntityPlayer, var5);
        }

        return var3;
    }

    class BeaconSlot extends Slot
    {
        private static final String __OBFID = "CL_00001736";

        public BeaconSlot(IInventory par2IInventory, int par3, int par4, int par5)
        {
            super(par2IInventory, par3, par4, par5);
        }

        public boolean isItemValid(ItemStack par1ItemStack)
        {
            return par1ItemStack == null ? false : par1ItemStack.getItem() == Items.emerald || par1ItemStack.getItem() == Items.diamond || par1ItemStack.getItem() == Items.gold_ingot || par1ItemStack.getItem() == Items.iron_ingot;
        }

        public int getSlotStackLimit()
        {
            return 1;
        }
    }
}
