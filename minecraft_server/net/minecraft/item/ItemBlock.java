package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemBlock extends Item
{
    protected final Block field_150939_a;
    private static final String __OBFID = "CL_00001772";

    public ItemBlock(Block p_i45328_1_)
    {
        this.field_150939_a = p_i45328_1_;
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    public ItemBlock setUnlocalizedName(String p_150937_1_)
    {
        super.setUnlocalizedName(p_150937_1_);
        return this;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block var11 = par3World.getBlock(par4, par5, par6);

        if (var11 == Blocks.snow_layer && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        }
        else if (var11 != Blocks.vine && var11 != Blocks.tallgrass && var11 != Blocks.deadbush)
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }

        if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else if (par5 == 255 && this.field_150939_a.getMaterial().isSolid())
        {
            return false;
        }
        else if (par3World.func_147472_a(this.field_150939_a, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack))
        {
            int var12 = this.getMetadata(par1ItemStack.getItemDamage());
            int var13 = this.field_150939_a.onBlockPlaced(par3World, par4, par5, par6, par7, par8, par9, par10, var12);

            if (par3World.setBlock(par4, par5, par6, this.field_150939_a, var13, 3))
            {
                if (par3World.getBlock(par4, par5, par6) == this.field_150939_a)
                {
                    this.field_150939_a.onBlockPlacedBy(par3World, par4, par5, par6, par2EntityPlayer, par1ItemStack);
                    this.field_150939_a.onPostBlockPlaced(par3World, par4, par5, par6, var13);
                }

                par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a.stepSound.getFrequency() * 0.8F);
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return this.field_150939_a.getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return this.field_150939_a.getUnlocalizedName();
    }
}
