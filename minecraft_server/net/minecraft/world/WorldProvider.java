package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider
{
    public static final float[] moonPhaseFactors = new float[] {1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};

    /** world object being used */
    public World worldObj;
    public WorldType terrainType;
    public String field_82913_c;

    /** World chunk manager being used to generate chunks */
    public WorldChunkManager worldChunkMgr;

    /**
     * States whether the Hell world provider is used(true) or if the normal
     * world provider is used(false)
     */
    public boolean isHellWorld;

    /**
     * A boolean that tells if a world does not have a sky. Used in calculating
     * weather and skylight
     */
    public boolean hasNoSky;

    /** Light to brightness conversion table */
    public float[] lightBrightnessTable = new float[16];

    /** The id for the dimension (ex. -1: Nether, 0: Overworld, 1: The End) */
    public int dimensionId;

    /** Array for sunrise/sunset colors (RGBA) */
    private float[] colorsSunriseSunset = new float[4];
    private static final String __OBFID = "CL_00000386";

    /**
     * associate an existing world with a World provider, and setup its
     * lightbrightness table
     */
    public final void registerWorld(World par1World)
    {
        worldObj = par1World;
        terrainType = par1World.getWorldInfo().getTerrainType();
        field_82913_c = par1World.getWorldInfo().getGeneratorOptions();
        registerWorldChunkManager();
        generateLightBrightnessTable();
    }

    /**
     * Creates the light to brightness table
     */
    protected void generateLightBrightnessTable()
    {
        float var1 = 0.0F;

        for (int var2 = 0; var2 <= 15; ++var2)
        {
            float var3 = 1.0F - var2 / 15.0F;
            lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
        }
    }

    /**
     * creates a new world chunk manager for WorldProvider
     */
    protected void registerWorldChunkManager()
    {
        if (worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT)
        {
            FlatGeneratorInfo var1 = FlatGeneratorInfo.createFlatGeneratorFromString(worldObj.getWorldInfo().getGeneratorOptions());
            worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.func_150568_d(var1.getBiome()), 0.5F);
        }
        else
        {
            worldChunkMgr = new WorldChunkManager(worldObj);
        }
    }

    /**
     * Returns a new chunk provider which generates chunks for this world
     */
    public IChunkProvider createChunkGenerator()
    {
        return terrainType == WorldType.FLAT ? new ChunkProviderFlat(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled(), field_82913_c) : new ChunkProviderGenerate(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled());
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map
     * spawn point
     */
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        return worldObj.getTopBlock(par1, par2) == Blocks.grass;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified
     * time (usually worldTime)
     */
    public float calculateCelestialAngle(long par1, float par3)
    {
        int var4 = (int)(par1 % 24000L);
        float var5 = (var4 + par3) / 24000.0F - 0.25F;

        if (var5 < 0.0F)
        {
            ++var5;
        }

        if (var5 > 1.0F)
        {
            --var5;
        }

        float var6 = var5;
        var5 = 1.0F - (float)((Math.cos(var5 * Math.PI) + 1.0D) / 2.0D);
        var5 = var6 + (var5 - var6) / 3.0F;
        return var5;
    }

    public int getMoonPhase(long par1)
    {
        return (int)(par1 / 24000L % 8L + 8L) % 8;
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the
     * Nether or End dimensions.
     */
    public boolean isSurfaceWorld()
    {
        return true;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false
     * = nether).
     */
    public boolean canRespawnHere()
    {
        return true;
    }

    public static WorldProvider getProviderForDimension(int par0)
    {
        return par0 == -1 ? new WorldProviderHell() : (par0 == 0 ? new WorldProviderSurface() : (par0 == 1 ? new WorldProviderEnd() : null));
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension.
     */
    public ChunkCoordinates getEntrancePortalLocation()
    {
        return null;
    }

    public int getAverageGroundLevel()
    {
        return terrainType == WorldType.FLAT ? 4 : 64;
    }

    /**
     * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
     */
    public abstract String getDimensionName();
}
