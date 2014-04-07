package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenScatteredFeature extends MapGenStructure
{
    private static List biomelist = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland});

    /** contains possible spawns for scattered features */
    private List scatteredFeatureSpawnList;

    /** the maximum distance between scattered features */
    private int maxDistanceBetweenScatteredFeatures;

    /** the minimum distance between scattered features */
    private int minDistanceBetweenScatteredFeatures;
    private static final String __OBFID = "CL_00000471";

    public MapGenScatteredFeature()
    {
        scatteredFeatureSpawnList = new ArrayList();
        maxDistanceBetweenScatteredFeatures = 32;
        minDistanceBetweenScatteredFeatures = 8;
        scatteredFeatureSpawnList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenScatteredFeature(Map par1Map)
    {
        this();
        Iterator var2 = par1Map.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();

            if (((String)var3.getKey()).equals("distance"))
            {
                maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), maxDistanceBetweenScatteredFeatures, minDistanceBetweenScatteredFeatures + 1);
            }
        }
    }

    public String func_143025_a()
    {
        return "Temple";
    }

    protected boolean canSpawnStructureAtCoords(int par1, int par2)
    {
        int var3 = par1;
        int var4 = par2;

        if (par1 < 0)
        {
            par1 -= maxDistanceBetweenScatteredFeatures - 1;
        }

        if (par2 < 0)
        {
            par2 -= maxDistanceBetweenScatteredFeatures - 1;
        }

        int var5 = par1 / maxDistanceBetweenScatteredFeatures;
        int var6 = par2 / maxDistanceBetweenScatteredFeatures;
        Random var7 = worldObj.setRandomSeed(var5, var6, 14357617);
        var5 *= maxDistanceBetweenScatteredFeatures;
        var6 *= maxDistanceBetweenScatteredFeatures;
        var5 += var7.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
        var6 += var7.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);

        if (var3 == var5 && var4 == var6)
        {
            BiomeGenBase var8 = worldObj.getWorldChunkManager().getBiomeGenAt(var3 * 16 + 8, var4 * 16 + 8);
            Iterator var9 = biomelist.iterator();

            while (var9.hasNext())
            {
                BiomeGenBase var10 = (BiomeGenBase)var9.next();

                if (var8 == var10) { return true; }
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int par1, int par2)
    {
        return new MapGenScatteredFeature.Start(worldObj, rand, par1, par2);
    }

    public boolean func_143030_a(int par1, int par2, int par3)
    {
        StructureStart var4 = func_143028_c(par1, par2, par3);

        if (var4 != null && var4 instanceof MapGenScatteredFeature.Start && !var4.components.isEmpty())
        {
            StructureComponent var5 = (StructureComponent)var4.components.getFirst();
            return var5 instanceof ComponentScatteredFeaturePieces.SwampHut;
        }
        else
        {
            return false;
        }
    }

    /**
     * returns possible spawns for scattered features
     */
    public List getScatteredFeatureSpawnList()
    {
        return scatteredFeatureSpawnList;
    }

    public static class Start extends StructureStart
    {
        private static final String __OBFID = "CL_00000472";

        public Start()
        {
        }

        public Start(World par1World, Random par2Random, int par3, int par4)
        {
            super(par3, par4);
            BiomeGenBase var5 = par1World.getBiomeGenForCoords(par3 * 16 + 8, par4 * 16 + 8);

            if (var5 != BiomeGenBase.jungle && var5 != BiomeGenBase.jungleHills)
            {
                if (var5 == BiomeGenBase.swampland)
                {
                    ComponentScatteredFeaturePieces.SwampHut var8 = new ComponentScatteredFeaturePieces.SwampHut(par2Random, par3 * 16, par4 * 16);
                    components.add(var8);
                }
                else
                {
                    ComponentScatteredFeaturePieces.DesertPyramid var7 = new ComponentScatteredFeaturePieces.DesertPyramid(par2Random, par3 * 16, par4 * 16);
                    components.add(var7);
                }
            }
            else
            {
                ComponentScatteredFeaturePieces.JunglePyramid var6 = new ComponentScatteredFeaturePieces.JunglePyramid(par2Random, par3 * 16, par4 * 16);
                components.add(var6);
            }

            updateBoundingBox();
        }
    }
}
