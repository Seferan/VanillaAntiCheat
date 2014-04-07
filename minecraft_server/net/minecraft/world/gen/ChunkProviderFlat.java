package net.minecraft.world.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;

public class ChunkProviderFlat implements IChunkProvider
{
    private World worldObj;
    private Random random;
    private final Block[] cachedBlockIDs = new Block[256];
    private final byte[] cachedBlockMetadata = new byte[256];
    private final FlatGeneratorInfo flatWorldGenInfo;
    private final List structureGenerators = new ArrayList();
    private final boolean hasDecoration;
    private final boolean hasDungeons;
    private WorldGenLakes waterLakeGenerator;
    private WorldGenLakes lavaLakeGenerator;
    private static final String __OBFID = "CL_00000391";

    public ChunkProviderFlat(World par1World, long par2, boolean par4, String par5Str)
    {
        worldObj = par1World;
        random = new Random(par2);
        flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(par5Str);

        if (par4)
        {
            Map var6 = flatWorldGenInfo.getWorldFeatures();

            if (var6.containsKey("village"))
            {
                Map var7 = (Map)var6.get("village");

                if (!var7.containsKey("size"))
                {
                    var7.put("size", "1");
                }

                structureGenerators.add(new MapGenVillage(var7));
            }

            if (var6.containsKey("biome_1"))
            {
                structureGenerators.add(new MapGenScatteredFeature((Map)var6.get("biome_1")));
            }

            if (var6.containsKey("mineshaft"))
            {
                structureGenerators.add(new MapGenMineshaft((Map)var6.get("mineshaft")));
            }

            if (var6.containsKey("stronghold"))
            {
                structureGenerators.add(new MapGenStronghold((Map)var6.get("stronghold")));
            }
        }

        hasDecoration = flatWorldGenInfo.getWorldFeatures().containsKey("decoration");

        if (flatWorldGenInfo.getWorldFeatures().containsKey("lake"))
        {
            waterLakeGenerator = new WorldGenLakes(Blocks.water);
        }

        if (flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake"))
        {
            lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
        }

        hasDungeons = flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
        Iterator var9 = flatWorldGenInfo.getFlatLayers().iterator();

        while (var9.hasNext())
        {
            FlatLayerInfo var10 = (FlatLayerInfo)var9.next();

            for (int var8 = var10.getMinY(); var8 < var10.getMinY() + var10.getLayerCount(); ++var8)
            {
                cachedBlockIDs[var8] = var10.func_151536_b();
                cachedBlockMetadata[var8] = (byte)var10.getFillBlockMeta();
            }
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int par1, int par2)
    {
        return provideChunk(par1, par2);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it
     * will generates all the blocks for the specified chunk from the map seed
     * and chunk seed
     */
    public Chunk provideChunk(int par1, int par2)
    {
        Chunk var3 = new Chunk(worldObj, par1, par2);
        int var6;

        for (int var4 = 0; var4 < cachedBlockIDs.length; ++var4)
        {
            Block var5 = cachedBlockIDs[var4];

            if (var5 != null)
            {
                var6 = var4 >> 4;
                ExtendedBlockStorage var7 = var3.getBlockStorageArray()[var6];

                if (var7 == null)
                {
                    var7 = new ExtendedBlockStorage(var4, !worldObj.provider.hasNoSky);
                    var3.getBlockStorageArray()[var6] = var7;
                }

                for (int var8 = 0; var8 < 16; ++var8)
                {
                    for (int var9 = 0; var9 < 16; ++var9)
                    {
                        var7.func_150818_a(var8, var4 & 15, var9, var5);
                        var7.setExtBlockMetadata(var8, var4 & 15, var9, cachedBlockMetadata[var4]);
                    }
                }
            }
        }

        var3.generateSkylightMap();
        BiomeGenBase[] var10 = worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, par1 * 16, par2 * 16, 16, 16);
        byte[] var11 = var3.getBiomeArray();

        for (var6 = 0; var6 < var11.length; ++var6)
        {
            var11[var6] = (byte)var10[var6].biomeID;
        }

        Iterator var12 = structureGenerators.iterator();

        while (var12.hasNext())
        {
            MapGenStructure var13 = (MapGenStructure)var12.next();
            var13.func_151539_a(this, worldObj, par1, par2, (Block[])null);
        }

        var3.generateSkylightMap();
        return var3;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int par1, int par2)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
        int var4 = par2 * 16;
        int var5 = par3 * 16;
        BiomeGenBase var6 = worldObj.getBiomeGenForCoords(var4 + 16, var5 + 16);
        boolean var7 = false;
        random.setSeed(worldObj.getSeed());
        long var8 = random.nextLong() / 2L * 2L + 1L;
        long var10 = random.nextLong() / 2L * 2L + 1L;
        random.setSeed(par2 * var8 + par3 * var10 ^ worldObj.getSeed());
        Iterator var12 = structureGenerators.iterator();

        while (var12.hasNext())
        {
            MapGenStructure var13 = (MapGenStructure)var12.next();
            boolean var14 = var13.generateStructuresInChunk(worldObj, random, par2, par3);

            if (var13 instanceof MapGenVillage)
            {
                var7 |= var14;
            }
        }

        int var17;
        int var16;
        int var18;

        if (waterLakeGenerator != null && !var7 && random.nextInt(4) == 0)
        {
            var16 = var4 + random.nextInt(16) + 8;
            var17 = random.nextInt(256);
            var18 = var5 + random.nextInt(16) + 8;
            waterLakeGenerator.generate(worldObj, random, var16, var17, var18);
        }

        if (lavaLakeGenerator != null && !var7 && random.nextInt(8) == 0)
        {
            var16 = var4 + random.nextInt(16) + 8;
            var17 = random.nextInt(random.nextInt(248) + 8);
            var18 = var5 + random.nextInt(16) + 8;

            if (var17 < 63 || random.nextInt(10) == 0)
            {
                lavaLakeGenerator.generate(worldObj, random, var16, var17, var18);
            }
        }

        if (hasDungeons)
        {
            for (var16 = 0; var16 < 8; ++var16)
            {
                var17 = var4 + random.nextInt(16) + 8;
                var18 = random.nextInt(256);
                int var15 = var5 + random.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(worldObj, random, var17, var18, var15);
            }
        }

        if (hasDecoration)
        {
            var6.decorate(worldObj, random, var4, var5);
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go. If
     * passed false, save up to two chunks. Return true if all chunks have been
     * saved.
     */
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk. Not saved during autosave,
     * only during world unload. Currently unimplemented.
     */
    public void saveExtraData()
    {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to
     * unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "FlatLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the
     * given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        BiomeGenBase var5 = worldObj.getBiomeGenForCoords(par2, par4);
        return var5.getSpawnableList(par1EnumCreatureType);
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        if ("Stronghold".equals(p_147416_2_))
        {
            Iterator var6 = structureGenerators.iterator();

            while (var6.hasNext())
            {
                MapGenStructure var7 = (MapGenStructure)var6.next();

                if (var7 instanceof MapGenStronghold) { return var7.func_151545_a(p_147416_1_, p_147416_3_, p_147416_4_, p_147416_5_); }
            }
        }

        return null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(int par1, int par2)
    {
        Iterator var3 = structureGenerators.iterator();

        while (var3.hasNext())
        {
            MapGenStructure var4 = (MapGenStructure)var3.next();
            var4.func_151539_a(this, worldObj, par1, par2, (Block[])null);
        }
    }
}
