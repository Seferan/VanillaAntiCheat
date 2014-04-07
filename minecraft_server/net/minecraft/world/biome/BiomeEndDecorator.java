package net.minecraft.world.biome;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeEndDecorator extends BiomeDecorator
{
    protected WorldGenerator spikeGen;
    private static final String __OBFID = "CL_00000188";

    public BiomeEndDecorator()
    {
        spikeGen = new WorldGenSpikes(Blocks.end_stone);
    }

    protected void func_150513_a(BiomeGenBase p_150513_1_)
    {
        generateOres();

        if (randomGenerator.nextInt(5) == 0)
        {
            int var2 = chunk_X + randomGenerator.nextInt(16) + 8;
            int var3 = chunk_Z + randomGenerator.nextInt(16) + 8;
            int var4 = currentWorld.getTopSolidOrLiquidBlock(var2, var3);
            spikeGen.generate(currentWorld, randomGenerator, var2, var4, var3);
        }

        if (chunk_X == 0 && chunk_Z == 0)
        {
            EntityDragon var5 = new EntityDragon(currentWorld);
            var5.setLocationAndAngles(0.0D, 128.0D, 0.0D, randomGenerator.nextFloat() * 360.0F, 0.0F);
            currentWorld.spawnEntityInWorld(var5);
        }
    }
}
