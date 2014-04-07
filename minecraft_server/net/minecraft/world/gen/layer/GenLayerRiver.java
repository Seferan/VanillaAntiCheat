package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiver extends GenLayer
{
    private static final String __OBFID = "CL_00000566";

    public GenLayerRiver(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        super.parent = par3GenLayer;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be
     * interpreted as temperatures, rainfall amounts, or biomeList[] indices
     * based on the particular GenLayer subclass.
     */
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int var5 = par1 - 1;
        int var6 = par2 - 1;
        int var7 = par3 + 2;
        int var8 = par4 + 2;
        int[] var9 = parent.getInts(var5, var6, var7, var8);
        int[] var10 = IntCache.getIntCache(par3 * par4);

        for (int var11 = 0; var11 < par4; ++var11)
        {
            for (int var12 = 0; var12 < par3; ++var12)
            {
                int var13 = func_151630_c(var9[var12 + 0 + (var11 + 1) * var7]);
                int var14 = func_151630_c(var9[var12 + 2 + (var11 + 1) * var7]);
                int var15 = func_151630_c(var9[var12 + 1 + (var11 + 0) * var7]);
                int var16 = func_151630_c(var9[var12 + 1 + (var11 + 2) * var7]);
                int var17 = func_151630_c(var9[var12 + 1 + (var11 + 1) * var7]);

                if (var17 == var13 && var17 == var15 && var17 == var14 && var17 == var16)
                {
                    var10[var12 + var11 * par3] = -1;
                }
                else
                {
                    var10[var12 + var11 * par3] = BiomeGenBase.river.biomeID;
                }
            }
        }

        return var10;
    }

    private int func_151630_c(int p_151630_1_)
    {
        return p_151630_1_ >= 2 ? 2 + (p_151630_1_ & 1) : p_151630_1_;
    }
}
