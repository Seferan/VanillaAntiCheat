package net.minecraft.item;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockJukebox;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemRecord extends Item
{
    private static final Map field_150928_b = new HashMap();
    public final String field_150929_a;
    private static final String __OBFID = "CL_00000057";

    protected ItemRecord(String p_i45350_1_)
    {
        this.field_150929_a = p_i45350_1_;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabMisc);
        field_150928_b.put(p_i45350_1_, this);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par3World.getBlock(par4, par5, par6) == Blocks.jukebox && par3World.getBlockMetadata(par4, par5, par6) == 0)
        {
            if (par3World.isClient)
            {
                return true;
            }
            else
            {
                ((BlockJukebox)Blocks.jukebox).func_149926_b(par3World, par4, par5, par6, par1ItemStack);
                par3World.playAuxSFXAtEntity((EntityPlayer)null, 1005, par4, par5, par6, Item.getIdFromItem(this));
                --par1ItemStack.stackSize;
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public EnumRarity func_77613_e(ItemStack p_77613_1_)
    {
        return EnumRarity.rare;
    }
}
