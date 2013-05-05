package net.minecraft.src;

import tk.afffsdd.vanillaanticheat.VanillaAntiCheatUtils;

public abstract class BlockDisabled extends BlockSpecial {
	
	protected BlockDisabled(int par1, Material par2Material, String name) {
		super(par1, par2Material, name);
	}
	
	protected void checkCanPlace(World par1World, int par2, int par3, int par4)
	{
        if (placer != null && !VanillaAntiCheatUtils.isOp(placer))
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
            VanillaAntiCheatUtils.notifyAndLog(placer.username + " tried to place " + name);
        }
	}
	
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack)
    {
        if(par5EntityLiving instanceof EntityPlayerMP)
        {
        	placer = (EntityPlayerMP) par5EntityLiving;
        	checkCanPlace(par1World, par2, par3, par4);
        }
    }
}
