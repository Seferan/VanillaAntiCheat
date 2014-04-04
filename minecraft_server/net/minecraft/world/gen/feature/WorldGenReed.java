package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator
{
    private static final String __OBFID = "CL_00000429";

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        for (int var6 = 0; var6 < 20; ++var6)
        {
            int var7 = par3 + par2Random.nextInt(4) - par2Random.nextInt(4);
            int var8 = par4;
            int var9 = par5 + par2Random.nextInt(4) - par2Random.nextInt(4);

            if (par1World.isAirBlock(var7, par4, var9) && (par1World.getBlock(var7 - 1, par4 - 1, var9).getMaterial() == Material.field_151586_h || par1World.getBlock(var7 + 1, par4 - 1, var9).getMaterial() == Material.field_151586_h || par1World.getBlock(var7, par4 - 1, var9 - 1).getMaterial() == Material.field_151586_h || par1World.getBlock(var7, par4 - 1, var9 + 1).getMaterial() == Material.field_151586_h))
            {
                int var10 = 2 + par2Random.nextInt(par2Random.nextInt(3) + 1);

                for (int var11 = 0; var11 < var10; ++var11)
                {
                    if (Blocks.reeds.canBlockStay(par1World, var7, var8 + var11, var9))
                    {
                        par1World.setBlock(var7, var8 + var11, var9, Blocks.reeds, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}
