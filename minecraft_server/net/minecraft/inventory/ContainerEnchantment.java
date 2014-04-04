package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container
{
    /** SlotEnchantmentTable object with ItemStack to be enchanted */
    public IInventory tableInventory = new InventoryBasic("Enchant", true, 1)
    {
        private static final String __OBFID = "CL_00001746";
        public int getInventoryStackLimit()
        {
            return 1;
        }
        public void onInventoryChanged()
        {
            super.onInventoryChanged();
            ContainerEnchantment.this.onCraftMatrixChanged(this);
        }
    };

    /** current world (for bookshelf counting) */
    private World worldPointer;
    private int posX;
    private int posY;
    private int posZ;
    private Random rand = new Random();

    /** used as seed for EnchantmentNameParts (see GuiEnchantment) */
    public long nameSeed;

    /** 3-member array storing the enchantment levels of each slot */
    public int[] enchantLevels = new int[3];
    private static final String __OBFID = "CL_00001745";

    public ContainerEnchantment(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
    {
        this.worldPointer = par2World;
        this.posX = par3;
        this.posY = par4;
        this.posZ = par5;
        this.addSlotToContainer(new Slot(this.tableInventory, 0, 25, 47)
        {
            private static final String __OBFID = "CL_00001747";
            public boolean isItemValid(ItemStack par1ItemStack)
            {
                return true;
            }
        });
        int var6;

        for (var6 = 0; var6 < 3; ++var6)
        {
            for (int var7 = 0; var7 < 9; ++var7)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
            }
        }

        for (var6 = 0; var6 < 9; ++var6)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 142));
        }
    }

    public void onCraftGuiOpened(ICrafting par1ICrafting)
    {
        super.onCraftGuiOpened(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
        par1ICrafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1)
        {
            ICrafting var2 = (ICrafting)this.crafters.get(var1);
            var2.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
            var2.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
            var2.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        if (par1IInventory == this.tableInventory)
        {
            ItemStack var2 = par1IInventory.getStackInSlot(0);
            int var3;

            if (var2 != null && var2.isItemEnchantable())
            {
                this.nameSeed = this.rand.nextLong();

                if (!this.worldPointer.isClient)
                {
                    var3 = 0;
                    int var4;

                    for (var4 = -1; var4 <= 1; ++var4)
                    {
                        for (int var5 = -1; var5 <= 1; ++var5)
                        {
                            if ((var4 != 0 || var5 != 0) && this.worldPointer.isAirBlock(this.posX + var5, this.posY, this.posZ + var4) && this.worldPointer.isAirBlock(this.posX + var5, this.posY + 1, this.posZ + var4))
                            {
                                if (this.worldPointer.getBlock(this.posX + var5 * 2, this.posY, this.posZ + var4 * 2) == Blocks.bookshelf)
                                {
                                    ++var3;
                                }

                                if (this.worldPointer.getBlock(this.posX + var5 * 2, this.posY + 1, this.posZ + var4 * 2) == Blocks.bookshelf)
                                {
                                    ++var3;
                                }

                                if (var5 != 0 && var4 != 0)
                                {
                                    if (this.worldPointer.getBlock(this.posX + var5 * 2, this.posY, this.posZ + var4) == Blocks.bookshelf)
                                    {
                                        ++var3;
                                    }

                                    if (this.worldPointer.getBlock(this.posX + var5 * 2, this.posY + 1, this.posZ + var4) == Blocks.bookshelf)
                                    {
                                        ++var3;
                                    }

                                    if (this.worldPointer.getBlock(this.posX + var5, this.posY, this.posZ + var4 * 2) == Blocks.bookshelf)
                                    {
                                        ++var3;
                                    }

                                    if (this.worldPointer.getBlock(this.posX + var5, this.posY + 1, this.posZ + var4 * 2) == Blocks.bookshelf)
                                    {
                                        ++var3;
                                    }
                                }
                            }
                        }
                    }

                    for (var4 = 0; var4 < 3; ++var4)
                    {
                        this.enchantLevels[var4] = EnchantmentHelper.calcItemStackEnchantability(this.rand, var4, var3, var2);
                    }

                    this.detectAndSendChanges();
                }
            }
            else
            {
                for (var3 = 0; var3 < 3; ++var3)
                {
                    this.enchantLevels[var3] = 0;
                }
            }
        }
    }

    /**
     * enchants the item on the table using the specified slot; also deducts XP from player
     */
    public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = this.tableInventory.getStackInSlot(0);

        if (this.enchantLevels[par2] > 0 && var3 != null && (par1EntityPlayer.experienceLevel >= this.enchantLevels[par2] || par1EntityPlayer.capabilities.isCreativeMode))
        {
            if (!this.worldPointer.isClient)
            {
                List var4 = EnchantmentHelper.buildEnchantmentList(this.rand, var3, this.enchantLevels[par2]);
                boolean var5 = var3.getItem() == Items.book;

                if (var4 != null)
                {
                    par1EntityPlayer.addExperienceLevel(-this.enchantLevels[par2]);

                    if (var5)
                    {
                        var3.func_150996_a(Items.enchanted_book);
                    }

                    int var6 = var5 && var4.size() > 1 ? this.rand.nextInt(var4.size()) : -1;

                    for (int var7 = 0; var7 < var4.size(); ++var7)
                    {
                        EnchantmentData var8 = (EnchantmentData)var4.get(var7);

                        if (!var5 || var7 != var6)
                        {
                            if (var5)
                            {
                                Items.enchanted_book.addEnchantment(var3, var8);
                            }
                            else
                            {
                                var3.addEnchantment(var8.enchantmentobj, var8.enchantmentLevel);
                            }
                        }
                    }

                    this.onCraftMatrixChanged(this.tableInventory);
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);

        if (!this.worldPointer.isClient)
        {
            ItemStack var2 = this.tableInventory.getStackInSlotOnClosing(0);

            if (var2 != null)
            {
                par1EntityPlayer.dropPlayerItemWithRandomChoice(var2, false);
            }
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.worldPointer.getBlock(this.posX, this.posY, this.posZ) != Blocks.enchanting_table ? false : par1EntityPlayer.getDistanceSq((double)this.posX + 0.5D, (double)this.posY + 0.5D, (double)this.posZ + 0.5D) <= 64.0D;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(var5, 1, 37, true))
                {
                    return null;
                }
            }
            else
            {
                if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(var5))
                {
                    return null;
                }

                if (var5.hasTagCompound() && var5.stackSize == 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(var5.copy());
                    var5.stackSize = 0;
                }
                else if (var5.stackSize >= 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(var5.getItem(), 1, var5.getItemDamage()));
                    --var5.stackSize;
                }
            }

            if (var5.stackSize == 0)
            {
                var4.putStack((ItemStack)null);
            }
            else
            {
                var4.onSlotChanged();
            }

            if (var5.stackSize == var3.stackSize)
            {
                return null;
            }

            var4.onPickupFromSlot(par1EntityPlayer, var5);
        }

        return var3;
    }
}
