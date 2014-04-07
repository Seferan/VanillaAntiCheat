package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMutated extends BiomeGenBase
{
    protected BiomeGenBase field_150611_aD;
    private static final String __OBFID = "CL_00000178";

    public BiomeGenMutated(int p_i45381_1_, BiomeGenBase p_i45381_2_)
    {
        super(p_i45381_1_);
        field_150611_aD = p_i45381_2_;
        func_150557_a(p_i45381_2_.color, true);
        biomeName = p_i45381_2_.biomeName + " M";
        topBlock = p_i45381_2_.topBlock;
        fillerBlock = p_i45381_2_.fillerBlock;
        field_76754_C = p_i45381_2_.field_76754_C;
        minHeight = p_i45381_2_.minHeight;
        maxHeight = p_i45381_2_.maxHeight;
        temperature = p_i45381_2_.temperature;
        rainfall = p_i45381_2_.rainfall;
        waterColorMultiplier = p_i45381_2_.waterColorMultiplier;
        enableSnow = p_i45381_2_.enableSnow;
        enableRain = p_i45381_2_.enableRain;
        spawnableCreatureList = new ArrayList(p_i45381_2_.spawnableCreatureList);
        spawnableMonsterList = new ArrayList(p_i45381_2_.spawnableMonsterList);
        spawnableCaveCreatureList = new ArrayList(p_i45381_2_.spawnableCaveCreatureList);
        spawnableWaterCreatureList = new ArrayList(p_i45381_2_.spawnableWaterCreatureList);
        temperature = p_i45381_2_.temperature;
        rainfall = p_i45381_2_.rainfall;
        minHeight = p_i45381_2_.minHeight + 0.1F;
        maxHeight = p_i45381_2_.maxHeight + 0.2F;
    }

    public void decorate(World par1World, Random par2Random, int par3, int par4)
    {
        field_150611_aD.theBiomeDecorator.func_150512_a(par1World, par2Random, this, par3, par4);
    }

    public void func_150573_a(World p_150573_1_, Random p_150573_2_, Block[] p_150573_3_, byte[] p_150573_4_, int p_150573_5_, int p_150573_6_, double p_150573_7_)
    {
        field_150611_aD.func_150573_a(p_150573_1_, p_150573_2_, p_150573_3_, p_150573_4_, p_150573_5_, p_150573_6_, p_150573_7_);
    }

    /**
     * returns the chance a creature has to spawn.
     */
    public float getSpawningChance()
    {
        return field_150611_aD.getSpawningChance();
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return field_150611_aD.func_150567_a(p_150567_1_);
    }

    public Class func_150562_l()
    {
        return field_150611_aD.func_150562_l();
    }

    public boolean func_150569_a(BiomeGenBase p_150569_1_)
    {
        return field_150611_aD.func_150569_a(p_150569_1_);
    }

    public BiomeGenBase.TempCategory func_150561_m()
    {
        return field_150611_aD.func_150561_m();
    }
}
