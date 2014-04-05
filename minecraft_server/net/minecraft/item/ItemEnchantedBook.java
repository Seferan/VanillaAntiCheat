package net.minecraft.item;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item
{
    private static final String __OBFID = "CL_00000025";

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isItemTool(ItemStack par1ItemStack)
    {
        return false;
    }

    public EnumRarity func_77613_e(ItemStack p_77613_1_)
    {
        return this.func_92110_g(p_77613_1_).tagCount() > 0 ? EnumRarity.uncommon : super.func_77613_e(p_77613_1_);
    }

    public NBTTagList func_92110_g(ItemStack par1ItemStack)
    {
        return par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.func_150297_b("StoredEnchantments", 9) ? (NBTTagList)par1ItemStack.stackTagCompound.getTag("StoredEnchantments") : new NBTTagList();
    }

    /**
     * Adds an stored enchantment to an enchanted book ItemStack
     */
    public void addEnchantment(ItemStack par1ItemStack, EnchantmentData par2EnchantmentData)
    {
        NBTTagList var3 = this.func_92110_g(par1ItemStack);
        boolean var4 = true;

        for (int var5 = 0; var5 < var3.tagCount(); ++var5)
        {
            NBTTagCompound var6 = var3.getCompoundTagAt(var5);

            if (var6.getShort("id") == par2EnchantmentData.enchantmentobj.effectId)
            {
                if (var6.getShort("lvl") < par2EnchantmentData.enchantmentLevel)
                {
                    var6.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
                }

                var4 = false;
                break;
            }
        }

        if (var4)
        {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setShort("id", (short)par2EnchantmentData.enchantmentobj.effectId);
            var7.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
            var3.appendTag(var7);
        }

        if (!par1ItemStack.hasTagCompound())
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.getTagCompound().setTag("StoredEnchantments", var3);
    }

    /**
     * Returns the ItemStack of an enchanted version of this item.
     */
    public ItemStack getEnchantedItemStack(EnchantmentData par1EnchantmentData)
    {
        ItemStack var2 = new ItemStack(this);
        this.addEnchantment(var2, par1EnchantmentData);
        return var2;
    }

    public WeightedRandomChestContent func_92114_b(Random par1Random)
    {
        return this.func_92112_a(par1Random, 1, 1, 1);
    }

    public WeightedRandomChestContent func_92112_a(Random par1Random, int par2, int par3, int par4)
    {
        ItemStack var5 = new ItemStack(Items.book, 1, 0);
        EnchantmentHelper.addRandomEnchantment(par1Random, var5, 30);
        return new WeightedRandomChestContent(var5, par2, par3, par4);
    }
}
