package net.minecraft.src;

public abstract class BlockSpecial extends Block {

	/** The player that placed this block */
	protected EntityPlayerMP placer;
	/** The player that broke this block */
	protected EntityPlayerMP breaker;
	/** The name of this block */
	public String name;
	
	protected BlockSpecial(int par1, Material par2Material, String name) {
		super(par1, par2Material);
		this.name = name;
	}
	
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack)
    {
        if(par5EntityLiving instanceof EntityPlayerMP)
        {
        	placer = (EntityPlayerMP) par5EntityLiving;
        }
    }
}
