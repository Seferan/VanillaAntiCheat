package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MerchantRecipe
{
    /** Item the Villager buys. */
    private ItemStack itemToBuy;

    /** Second Item the Villager buys. */
    private ItemStack secondItemToBuy;

    /** Item the Villager sells. */
    private ItemStack itemToSell;

    /**
     * Saves how much has been tool used when put into to slot to be enchanted.
     */
    private int toolUses;

    /** Maximum times this trade can be used. */
    private int maxTradeUses;
    private static final String __OBFID = "CL_00000126";

    public MerchantRecipe(NBTTagCompound par1NBTTagCompound)
    {
        readFromTags(par1NBTTagCompound);
    }

    public MerchantRecipe(ItemStack par1ItemStack, ItemStack par2ItemStack, ItemStack par3ItemStack)
    {
        itemToBuy = par1ItemStack;
        secondItemToBuy = par2ItemStack;
        itemToSell = par3ItemStack;
        maxTradeUses = 7;
    }

    public MerchantRecipe(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        this(par1ItemStack, (ItemStack)null, par2ItemStack);
    }

    public MerchantRecipe(ItemStack par1ItemStack, Item par2Item)
    {
        this(par1ItemStack, new ItemStack(par2Item));
    }

    /**
     * Gets the itemToBuy.
     */
    public ItemStack getItemToBuy()
    {
        return itemToBuy;
    }

    /**
     * Gets secondItemToBuy.
     */
    public ItemStack getSecondItemToBuy()
    {
        return secondItemToBuy;
    }

    /**
     * Gets if Villager has secondItemToBuy.
     */
    public boolean hasSecondItemToBuy()
    {
        return secondItemToBuy != null;
    }

    /**
     * Gets itemToSell.
     */
    public ItemStack getItemToSell()
    {
        return itemToSell;
    }

    /**
     * checks if both the first and second ItemToBuy IDs are the same
     */
    public boolean hasSameIDsAs(MerchantRecipe par1MerchantRecipe)
    {
        return itemToBuy.getItem() == par1MerchantRecipe.itemToBuy.getItem() && itemToSell.getItem() == par1MerchantRecipe.itemToSell.getItem() ? secondItemToBuy == null && par1MerchantRecipe.secondItemToBuy == null || secondItemToBuy != null && par1MerchantRecipe.secondItemToBuy != null && secondItemToBuy.getItem() == par1MerchantRecipe.secondItemToBuy.getItem() : false;
    }

    /**
     * checks first and second ItemToBuy ID's and count. Calls hasSameIDs
     */
    public boolean hasSameItemsAs(MerchantRecipe par1MerchantRecipe)
    {
        return hasSameIDsAs(par1MerchantRecipe) && (itemToBuy.stackSize < par1MerchantRecipe.itemToBuy.stackSize || secondItemToBuy != null && secondItemToBuy.stackSize < par1MerchantRecipe.secondItemToBuy.stackSize);
    }

    public void incrementToolUses()
    {
        ++toolUses;
    }

    public void func_82783_a(int par1)
    {
        maxTradeUses += par1;
    }

    public boolean func_82784_g()
    {
        return toolUses >= maxTradeUses;
    }

    public void readFromTags(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("buy");
        itemToBuy = ItemStack.loadItemStackFromNBT(var2);
        NBTTagCompound var3 = par1NBTTagCompound.getCompoundTag("sell");
        itemToSell = ItemStack.loadItemStackFromNBT(var3);

        if (par1NBTTagCompound.func_150297_b("buyB", 10))
        {
            secondItemToBuy = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("buyB"));
        }

        if (par1NBTTagCompound.func_150297_b("uses", 99))
        {
            toolUses = par1NBTTagCompound.getInteger("uses");
        }

        if (par1NBTTagCompound.func_150297_b("maxUses", 99))
        {
            maxTradeUses = par1NBTTagCompound.getInteger("maxUses");
        }
        else
        {
            maxTradeUses = 7;
        }
    }

    public NBTTagCompound writeToTags()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        var1.setTag("buy", itemToBuy.writeToNBT(new NBTTagCompound()));
        var1.setTag("sell", itemToSell.writeToNBT(new NBTTagCompound()));

        if (secondItemToBuy != null)
        {
            var1.setTag("buyB", secondItemToBuy.writeToNBT(new NBTTagCompound()));
        }

        var1.setInteger("uses", toolUses);
        var1.setInteger("maxUses", maxTradeUses);
        return var1;
    }
}
