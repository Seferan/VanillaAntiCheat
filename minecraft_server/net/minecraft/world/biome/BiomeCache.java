package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache
{
    /** Reference to the WorldChunkManager */
    private final WorldChunkManager chunkManager;

    /** The last time this BiomeCache was cleaned, in milliseconds. */
    private long lastCleanupTime;

    /**
     * The map of keys to BiomeCacheBlocks. Keys are based on the chunk x, z
     * coordinates as (x | z << 32).
     */
    private LongHashMap cacheMap = new LongHashMap();

    /** The list of cached BiomeCacheBlocks */
    private List cache = new ArrayList();
    private static final String __OBFID = "CL_00000162";

    public BiomeCache(WorldChunkManager par1WorldChunkManager)
    {
        chunkManager = par1WorldChunkManager;
    }

    /**
     * Returns a biome cache block at location specified.
     */
    public BiomeCache.Block getBiomeCacheBlock(int par1, int par2)
    {
        par1 >>= 4;
        par2 >>= 4;
        long var3 = par1 & 4294967295L | (par2 & 4294967295L) << 32;
        BiomeCache.Block var5 = (BiomeCache.Block)cacheMap.getValueByKey(var3);

        if (var5 == null)
        {
            var5 = new BiomeCache.Block(par1, par2);
            cacheMap.add(var3, var5);
            cache.add(var5);
        }

        var5.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
        return var5;
    }

    /**
     * Returns the BiomeGenBase related to the x, z position from the cache.
     */
    public BiomeGenBase getBiomeGenAt(int par1, int par2)
    {
        return getBiomeCacheBlock(par1, par2).getBiomeGenAt(par1, par2);
    }

    /**
     * Removes BiomeCacheBlocks from this cache that haven't been accessed in at
     * least 30 seconds.
     */
    public void cleanupCache()
    {
        long var1 = MinecraftServer.getCurrentTimeMillis();
        long var3 = var1 - lastCleanupTime;

        if (var3 > 7500L || var3 < 0L)
        {
            lastCleanupTime = var1;

            for (int var5 = 0; var5 < cache.size(); ++var5)
            {
                BiomeCache.Block var6 = (BiomeCache.Block)cache.get(var5);
                long var7 = var1 - var6.lastAccessTime;

                if (var7 > 30000L || var7 < 0L)
                {
                    cache.remove(var5--);
                    long var9 = var6.xPosition & 4294967295L | (var6.zPosition & 4294967295L) << 32;
                    cacheMap.remove(var9);
                }
            }
        }
    }

    /**
     * Returns the array of cached biome types in the BiomeCacheBlock at the
     * given location.
     */
    public BiomeGenBase[] getCachedBiomes(int par1, int par2)
    {
        return getBiomeCacheBlock(par1, par2).biomes;
    }

    public class Block
    {
        public float[] rainfallValues = new float[256];
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;
        private static final String __OBFID = "CL_00000163";

        public Block(int par2, int par3)
        {
            xPosition = par2;
            zPosition = par3;
            chunkManager.getRainfall(rainfallValues, par2 << 4, par3 << 4, 16, 16);
            chunkManager.getBiomeGenAt(biomes, par2 << 4, par3 << 4, 16, 16, false);
        }

        public BiomeGenBase getBiomeGenAt(int par1, int par2)
        {
            return biomes[par1 & 15 | (par2 & 15) << 4];
        }
    }
}
