package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe
{
    /** How many horizontal slots this recipe is wide. */
    private int recipeWidth;

    /** How many vertical slots this recipe uses. */
    private int recipeHeight;

    /** Is a array of ItemStack that composes the recipe. */
    private ItemStack[] recipeItems;

    /** Is the ItemStack that you get when craft the recipe. */
    private ItemStack recipeOutput;
    private boolean field_92101_f;
    private static final String __OBFID = "CL_00000093";

    public ShapedRecipes(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack)
    {
        recipeWidth = par1;
        recipeHeight = par2;
        recipeItems = par3ArrayOfItemStack;
        recipeOutput = par4ItemStack;
    }

    public ItemStack getRecipeOutput()
    {
        return recipeOutput;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        for (int var3 = 0; var3 <= 3 - recipeWidth; ++var3)
        {
            for (int var4 = 0; var4 <= 3 - recipeHeight; ++var4)
            {
                if (checkMatch(par1InventoryCrafting, var3, var4, true)) { return true; }

                if (checkMatch(par1InventoryCrafting, var3, var4, false)) { return true; }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(InventoryCrafting par1InventoryCrafting, int par2, int par3, boolean par4)
    {
        for (int var5 = 0; var5 < 3; ++var5)
        {
            for (int var6 = 0; var6 < 3; ++var6)
            {
                int var7 = var5 - par2;
                int var8 = var6 - par3;
                ItemStack var9 = null;

                if (var7 >= 0 && var8 >= 0 && var7 < recipeWidth && var8 < recipeHeight)
                {
                    if (par4)
                    {
                        var9 = recipeItems[recipeWidth - var7 - 1 + var8 * recipeWidth];
                    }
                    else
                    {
                        var9 = recipeItems[var7 + var8 * recipeWidth];
                    }
                }

                ItemStack var10 = par1InventoryCrafting.getStackInRowAndColumn(var5, var6);

                if (var10 != null || var9 != null)
                {
                    if (var10 == null && var9 != null || var10 != null && var9 == null) { return false; }

                    if (var9.getItem() != var10.getItem()) { return false; }

                    if (var9.getItemDamage() != 32767 && var9.getItemDamage() != var10.getItemDamage()) { return false; }
                }
            }
        }

        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting)
    {
        ItemStack var2 = getRecipeOutput().copy();

        if (field_92101_f)
        {
            for (int var3 = 0; var3 < par1InventoryCrafting.getSizeInventory(); ++var3)
            {
                ItemStack var4 = par1InventoryCrafting.getStackInSlot(var3);

                if (var4 != null && var4.hasTagCompound())
                {
                    var2.setTagCompound((NBTTagCompound)var4.stackTagCompound.copy());
                }
            }
        }

        return var2;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return recipeWidth * recipeHeight;
    }

    public ShapedRecipes func_92100_c()
    {
        field_92101_f = true;
        return this;
    }
}
