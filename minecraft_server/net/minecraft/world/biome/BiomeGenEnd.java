package net.minecraft.world.biome;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;

public class BiomeGenEnd extends BiomeGenBase
{
    private static final String __OBFID = "CL_00000187";

    public BiomeGenEnd(int par1)
    {
        super(par1);
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 10, 4, 4));
        topBlock = Blocks.dirt;
        fillerBlock = Blocks.dirt;
        theBiomeDecorator = new BiomeEndDecorator();
    }
}
