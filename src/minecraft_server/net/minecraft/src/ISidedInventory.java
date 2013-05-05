package net.minecraft.src;

public interface ISidedInventory extends IInventory
{
    /**
     * param side
     */
    int[] getSlotsForFace(int var1);

    boolean func_102007_a(int var1, ItemStack var2, int var3);

    boolean func_102008_b(int var1, ItemStack var2, int var3);
}
