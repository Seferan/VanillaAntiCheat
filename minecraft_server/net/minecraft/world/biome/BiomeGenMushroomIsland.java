package net.minecraft.world.biome;

import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;

public class BiomeGenMushroomIsland extends BiomeGenBase
{
    private static final String __OBFID = "CL_00000177";

    public BiomeGenMushroomIsland(int par1)
    {
        super(par1);
        theBiomeDecorator.treesPerChunk = -100;
        theBiomeDecorator.flowersPerChunk = -100;
        theBiomeDecorator.grassPerChunk = -100;
        theBiomeDecorator.mushroomsPerChunk = 1;
        theBiomeDecorator.bigMushroomsPerChunk = 1;
        topBlock = Blocks.mycelium;
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityMooshroom.class, 8, 4, 8));
    }
}
