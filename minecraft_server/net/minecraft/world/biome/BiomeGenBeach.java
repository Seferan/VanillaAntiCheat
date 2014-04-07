package net.minecraft.world.biome;

import net.minecraft.init.Blocks;

public class BiomeGenBeach extends BiomeGenBase
{
    private static final String __OBFID = "CL_00000157";

    public BiomeGenBeach(int par1)
    {
        super(par1);
        spawnableCreatureList.clear();
        topBlock = Blocks.sand;
        fillerBlock = Blocks.sand;
        theBiomeDecorator.treesPerChunk = -999;
        theBiomeDecorator.deadBushPerChunk = 0;
        theBiomeDecorator.reedsPerChunk = 0;
        theBiomeDecorator.cactiPerChunk = 0;
    }
}
