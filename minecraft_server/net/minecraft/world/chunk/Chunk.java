package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * Determines if the chunk is lit or not at a light value greater than 0.
     */
    public static boolean isLit;

    /**
     * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps,
     * and metadata. Each entry corresponds to a logical segment of 16x16x16
     * blocks, stacked vertically.
     */
    private ExtendedBlockStorage[] storageArrays;

    /**
     * Contains a 16x16 mapping on the X/Z plane of the biome ID to which each
     * colum belongs.
     */
    private byte[] blockBiomeArray;

    /**
     * A map, similar to heightMap, that tracks how far down precipitation can
     * fall.
     */
    public int[] precipitationHeightMap;

    /** Which columns need their skylightMaps updated. */
    public boolean[] updateSkylightColumns;

    /** Whether or not this Chunk is currently loaded into the World */
    public boolean isChunkLoaded;

    /** Reference to the World object. */
    public World worldObj;
    public int[] heightMap;

    /** The x coordinate of the chunk. */
    public final int xPosition;

    /** The z coordinate of the chunk. */
    public final int zPosition;
    private boolean isGapLightingUpdated;

    /** A Map of ChunkPositions to TileEntities in this chunk */
    public Map chunkTileEntityMap;

    /**
     * Array of Lists containing the entities in this Chunk. Each List
     * represents a 16 block subchunk.
     */
    public List[] entityLists;

    /** Boolean value indicating if the terrain is populated. */
    public boolean isTerrainPopulated;
    public boolean isLightPopulated;
    public boolean field_150815_m;

    /**
     * Set to true if the chunk has been modified and needs to be updated
     * internally.
     */
    public boolean isModified;

    /**
     * Whether this Chunk has any Entities and thus requires saving on every
     * tick
     */
    public boolean hasEntities;

    /** The time according to World.worldTime when this chunk was last saved */
    public long lastSaveTime;

    /**
     * Updates to this chunk will not be sent to clients if this is false. This
     * field is set to true the first time the chunk is sent to a client, and
     * never set to false.
     */
    public boolean sendUpdates;

    /** Lowest value in the heightmap. */
    public int heightMapMinimum;

    /** the cumulative number of ticks players have been in this chunk */
    public long inhabitedTime;

    /**
     * Contains the current round-robin relight check index, and is implied as
     * the relight check location as well.
     */
    private int queuedLightChecks;
    private static final String __OBFID = "CL_00000373";

    public Chunk(World par1World, int par2, int par3)
    {
        storageArrays = new ExtendedBlockStorage[16];
        blockBiomeArray = new byte[256];
        precipitationHeightMap = new int[256];
        updateSkylightColumns = new boolean[256];
        chunkTileEntityMap = new HashMap();
        queuedLightChecks = 4096;
        entityLists = new List[16];
        worldObj = par1World;
        xPosition = par2;
        zPosition = par3;
        heightMap = new int[256];

        for (int var4 = 0; var4 < entityLists.length; ++var4)
        {
            entityLists[var4] = new ArrayList();
        }

        Arrays.fill(precipitationHeightMap, -999);
        Arrays.fill(blockBiomeArray, (byte)-1);
    }

    public Chunk(World p_i45446_1_, Block[] p_i45446_2_, int p_i45446_3_, int p_i45446_4_)
    {
        this(p_i45446_1_, p_i45446_3_, p_i45446_4_);
        int var5 = p_i45446_2_.length / 256;
        boolean var6 = !p_i45446_1_.provider.hasNoSky;

        for (int var7 = 0; var7 < 16; ++var7)
        {
            for (int var8 = 0; var8 < 16; ++var8)
            {
                for (int var9 = 0; var9 < var5; ++var9)
                {
                    Block var10 = p_i45446_2_[var7 << 11 | var8 << 7 | var9];

                    if (var10 != null && var10.getMaterial() != Material.air)
                    {
                        int var11 = var9 >> 4;

                        if (storageArrays[var11] == null)
                        {
                            storageArrays[var11] = new ExtendedBlockStorage(var11 << 4, var6);
                        }

                        storageArrays[var11].func_150818_a(var7, var9 & 15, var8, var10);
                    }
                }
            }
        }
    }

    public Chunk(World p_i45447_1_, Block[] p_i45447_2_, byte[] p_i45447_3_, int p_i45447_4_, int p_i45447_5_)
    {
        this(p_i45447_1_, p_i45447_4_, p_i45447_5_);
        int var6 = p_i45447_2_.length / 256;
        boolean var7 = !p_i45447_1_.provider.hasNoSky;

        for (int var8 = 0; var8 < 16; ++var8)
        {
            for (int var9 = 0; var9 < 16; ++var9)
            {
                for (int var10 = 0; var10 < var6; ++var10)
                {
                    int var11 = var8 * var6 * 16 | var9 * var6 | var10;
                    Block var12 = p_i45447_2_[var11];

                    if (var12 != null && var12 != Blocks.air)
                    {
                        int var13 = var10 >> 4;

                        if (storageArrays[var13] == null)
                        {
                            storageArrays[var13] = new ExtendedBlockStorage(var13 << 4, var7);
                        }

                        storageArrays[var13].func_150818_a(var8, var10 & 15, var9, var12);
                        storageArrays[var13].setExtBlockMetadata(var8, var10 & 15, var9, p_i45447_3_[var11]);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the chunk is at the X/Z location specified
     */
    public boolean isAtLocation(int par1, int par2)
    {
        return par1 == xPosition && par2 == zPosition;
    }

    /**
     * Returns the value in the height map at this x, z coordinate in the chunk
     */
    public int getHeightValue(int par1, int par2)
    {
        return heightMap[par2 << 4 | par1];
    }

    /**
     * Returns the topmost ExtendedBlockStorage instance for this Chunk that
     * actually contains a block.
     */
    public int getTopFilledSegment()
    {
        for (int var1 = storageArrays.length - 1; var1 >= 0; --var1)
        {
            if (storageArrays[var1] != null) { return storageArrays[var1].getYLocation(); }
        }

        return 0;
    }

    /**
     * Returns the ExtendedBlockStorage array for this Chunk.
     */
    public ExtendedBlockStorage[] getBlockStorageArray()
    {
        return storageArrays;
    }

    /**
     * Generates the initial skylight map for the chunk upon generation or load.
     */
    public void generateSkylightMap()
    {
        int var1 = getTopFilledSegment();
        heightMapMinimum = Integer.MAX_VALUE;

        for (int var2 = 0; var2 < 16; ++var2)
        {
            int var3 = 0;

            while (var3 < 16)
            {
                precipitationHeightMap[var2 + (var3 << 4)] = -999;
                int var4 = var1 + 16 - 1;

                while (true)
                {
                    if (var4 > 0)
                    {
                        if (func_150808_b(var2, var4 - 1, var3) == 0)
                        {
                            --var4;
                            continue;
                        }

                        heightMap[var3 << 4 | var2] = var4;

                        if (var4 < heightMapMinimum)
                        {
                            heightMapMinimum = var4;
                        }
                    }

                    if (!worldObj.provider.hasNoSky)
                    {
                        var4 = 15;
                        int var5 = var1 + 16 - 1;

                        do
                        {
                            int var6 = func_150808_b(var2, var5, var3);

                            if (var6 == 0 && var4 != 15)
                            {
                                var6 = 1;
                            }

                            var4 -= var6;

                            if (var4 > 0)
                            {
                                ExtendedBlockStorage var7 = storageArrays[var5 >> 4];

                                if (var7 != null)
                                {
                                    var7.setExtSkylightValue(var2, var5 & 15, var3, var4);
                                    worldObj.func_147479_m((xPosition << 4) + var2, var5, (zPosition << 4) + var3);
                                }
                            }

                            --var5;
                        } while (var5 > 0 && var4 > 0);
                    }

                    ++var3;
                    break;
                }
            }
        }

        isModified = true;
    }

    /**
     * Propagates a given sky-visible block's light value downward and upward to
     * neighboring blocks as necessary.
     */
    private void propagateSkylightOcclusion(int par1, int par2)
    {
        updateSkylightColumns[par1 + par2 * 16] = true;
        isGapLightingUpdated = true;
    }

    private void recheckGaps(boolean p_150803_1_)
    {
        worldObj.theProfiler.startSection("recheckGaps");

        if (worldObj.doChunksNearChunkExist(xPosition * 16 + 8, 0, zPosition * 16 + 8, 16))
        {
            for (int var2 = 0; var2 < 16; ++var2)
            {
                for (int var3 = 0; var3 < 16; ++var3)
                {
                    if (updateSkylightColumns[var2 + var3 * 16])
                    {
                        updateSkylightColumns[var2 + var3 * 16] = false;
                        int var4 = getHeightValue(var2, var3);
                        int var5 = xPosition * 16 + var2;
                        int var6 = zPosition * 16 + var3;
                        int var7 = worldObj.getChunkHeightMapMinimum(var5 - 1, var6);
                        int var8 = worldObj.getChunkHeightMapMinimum(var5 + 1, var6);
                        int var9 = worldObj.getChunkHeightMapMinimum(var5, var6 - 1);
                        int var10 = worldObj.getChunkHeightMapMinimum(var5, var6 + 1);

                        if (var8 < var7)
                        {
                            var7 = var8;
                        }

                        if (var9 < var7)
                        {
                            var7 = var9;
                        }

                        if (var10 < var7)
                        {
                            var7 = var10;
                        }

                        checkSkylightNeighborHeight(var5, var6, var7);
                        checkSkylightNeighborHeight(var5 - 1, var6, var4);
                        checkSkylightNeighborHeight(var5 + 1, var6, var4);
                        checkSkylightNeighborHeight(var5, var6 - 1, var4);
                        checkSkylightNeighborHeight(var5, var6 + 1, var4);

                        if (p_150803_1_)
                        {
                            worldObj.theProfiler.endSection();
                            return;
                        }
                    }
                }
            }

            isGapLightingUpdated = false;
        }

        worldObj.theProfiler.endSection();
    }

    /**
     * Checks the height of a block next to a sky-visible block and schedules a
     * lighting update as necessary.
     */
    private void checkSkylightNeighborHeight(int par1, int par2, int par3)
    {
        int var4 = worldObj.getHeightValue(par1, par2);

        if (var4 > par3)
        {
            updateSkylightNeighborHeight(par1, par2, par3, var4 + 1);
        }
        else if (var4 < par3)
        {
            updateSkylightNeighborHeight(par1, par2, var4, par3 + 1);
        }
    }

    private void updateSkylightNeighborHeight(int par1, int par2, int par3, int par4)
    {
        if (par4 > par3 && worldObj.doChunksNearChunkExist(par1, 0, par2, 16))
        {
            for (int var5 = par3; var5 < par4; ++var5)
            {
                worldObj.func_147463_c(EnumSkyBlock.Sky, par1, var5, par2);
            }

            isModified = true;
        }
    }

    /**
     * Initiates the recalculation of both the block-light and sky-light for a
     * given block inside a chunk.
     */
    private void relightBlock(int par1, int par2, int par3)
    {
        int var4 = heightMap[par3 << 4 | par1] & 255;
        int var5 = var4;

        if (par2 > var4)
        {
            var5 = par2;
        }

        while (var5 > 0 && func_150808_b(par1, var5 - 1, par3) == 0)
        {
            --var5;
        }

        if (var5 != var4)
        {
            worldObj.markBlocksDirtyVertical(par1 + xPosition * 16, par3 + zPosition * 16, var5, var4);
            heightMap[par3 << 4 | par1] = var5;
            int var6 = xPosition * 16 + par1;
            int var7 = zPosition * 16 + par3;
            int var8;
            int var12;

            if (!worldObj.provider.hasNoSky)
            {
                ExtendedBlockStorage var9;

                if (var5 < var4)
                {
                    for (var8 = var5; var8 < var4; ++var8)
                    {
                        var9 = storageArrays[var8 >> 4];

                        if (var9 != null)
                        {
                            var9.setExtSkylightValue(par1, var8 & 15, par3, 15);
                            worldObj.func_147479_m((xPosition << 4) + par1, var8, (zPosition << 4) + par3);
                        }
                    }
                }
                else
                {
                    for (var8 = var4; var8 < var5; ++var8)
                    {
                        var9 = storageArrays[var8 >> 4];

                        if (var9 != null)
                        {
                            var9.setExtSkylightValue(par1, var8 & 15, par3, 0);
                            worldObj.func_147479_m((xPosition << 4) + par1, var8, (zPosition << 4) + par3);
                        }
                    }
                }

                var8 = 15;

                while (var5 > 0 && var8 > 0)
                {
                    --var5;
                    var12 = func_150808_b(par1, var5, par3);

                    if (var12 == 0)
                    {
                        var12 = 1;
                    }

                    var8 -= var12;

                    if (var8 < 0)
                    {
                        var8 = 0;
                    }

                    ExtendedBlockStorage var10 = storageArrays[var5 >> 4];

                    if (var10 != null)
                    {
                        var10.setExtSkylightValue(par1, var5 & 15, par3, var8);
                    }
                }
            }

            var8 = heightMap[par3 << 4 | par1];
            var12 = var4;
            int var13 = var8;

            if (var8 < var4)
            {
                var12 = var8;
                var13 = var4;
            }

            if (var8 < heightMapMinimum)
            {
                heightMapMinimum = var8;
            }

            if (!worldObj.provider.hasNoSky)
            {
                updateSkylightNeighborHeight(var6 - 1, var7, var12, var13);
                updateSkylightNeighborHeight(var6 + 1, var7, var12, var13);
                updateSkylightNeighborHeight(var6, var7 - 1, var12, var13);
                updateSkylightNeighborHeight(var6, var7 + 1, var12, var13);
                updateSkylightNeighborHeight(var6, var7, var12, var13);
            }

            isModified = true;
        }
    }

    public int func_150808_b(int p_150808_1_, int p_150808_2_, int p_150808_3_)
    {
        return func_150810_a(p_150808_1_, p_150808_2_, p_150808_3_).getLightOpacity();
    }

    public Block func_150810_a(final int p_150810_1_, final int p_150810_2_, final int p_150810_3_)
    {
        Block var4 = Blocks.air;

        if (p_150810_2_ >> 4 < storageArrays.length)
        {
            ExtendedBlockStorage var5 = storageArrays[p_150810_2_ >> 4];

            if (var5 != null)
            {
                try
                {
                    var4 = var5.func_150819_a(p_150810_1_, p_150810_2_ & 15, p_150810_3_);
                }
                catch (Throwable var9)
                {
                    CrashReport var7 = CrashReport.makeCrashReport(var9, "Getting block");
                    CrashReportCategory var8 = var7.makeCategory("Block being got");
                    var8.addCrashSectionCallable("Location", new Callable()
                    {
                        private static final String __OBFID = "CL_00000374";

                        public String call()
                        {
                            return CrashReportCategory.getLocationInfo(p_150810_1_, p_150810_2_, p_150810_3_);
                        }
                    });
                    throw new ReportedException(var7);
                }
            }
        }

        return var4;
    }

    /**
     * Return the metadata corresponding to the given coordinates inside a
     * chunk.
     */
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par2 >> 4 >= storageArrays.length)
        {
            return 0;
        }
        else
        {
            ExtendedBlockStorage var4 = storageArrays[par2 >> 4];
            return var4 != null ? var4.getExtBlockMetadata(par1, par2 & 15, par3) : 0;
        }
    }

    public boolean func_150807_a(int p_150807_1_, int p_150807_2_, int p_150807_3_, Block p_150807_4_, int p_150807_5_)
    {
        int var6 = p_150807_3_ << 4 | p_150807_1_;

        if (p_150807_2_ >= precipitationHeightMap[var6] - 1)
        {
            precipitationHeightMap[var6] = -999;
        }

        int var7 = heightMap[var6];
        Block var8 = func_150810_a(p_150807_1_, p_150807_2_, p_150807_3_);
        int var9 = getBlockMetadata(p_150807_1_, p_150807_2_, p_150807_3_);

        if (var8 == p_150807_4_ && var9 == p_150807_5_)
        {
            return false;
        }
        else
        {
            ExtendedBlockStorage var10 = storageArrays[p_150807_2_ >> 4];
            boolean var11 = false;

            if (var10 == null)
            {
                if (p_150807_4_ == Blocks.air) { return false; }

                var10 = storageArrays[p_150807_2_ >> 4] = new ExtendedBlockStorage(p_150807_2_ >> 4 << 4, !worldObj.provider.hasNoSky);
                var11 = p_150807_2_ >= var7;
            }

            int var12 = xPosition * 16 + p_150807_1_;
            int var13 = zPosition * 16 + p_150807_3_;

            if (!worldObj.isClient)
            {
                var8.onBlockPreDestroy(worldObj, var12, p_150807_2_, var13, var9);
            }

            var10.func_150818_a(p_150807_1_, p_150807_2_ & 15, p_150807_3_, p_150807_4_);

            if (!worldObj.isClient)
            {
                var8.breakBlock(worldObj, var12, p_150807_2_, var13, var8, var9);
            }
            else if (var8 instanceof ITileEntityProvider && var8 != p_150807_4_)
            {
                worldObj.removeTileEntity(var12, p_150807_2_, var13);
            }

            if (var10.func_150819_a(p_150807_1_, p_150807_2_ & 15, p_150807_3_) != p_150807_4_)
            {
                return false;
            }
            else
            {
                var10.setExtBlockMetadata(p_150807_1_, p_150807_2_ & 15, p_150807_3_, p_150807_5_);

                if (var11)
                {
                    generateSkylightMap();
                }
                else
                {
                    int var14 = p_150807_4_.getLightOpacity();
                    int var15 = var8.getLightOpacity();

                    if (var14 > 0)
                    {
                        if (p_150807_2_ >= var7)
                        {
                            relightBlock(p_150807_1_, p_150807_2_ + 1, p_150807_3_);
                        }
                    }
                    else if (p_150807_2_ == var7 - 1)
                    {
                        relightBlock(p_150807_1_, p_150807_2_, p_150807_3_);
                    }

                    if (var14 != var15 && (var14 < var15 || getSavedLightValue(EnumSkyBlock.Sky, p_150807_1_, p_150807_2_, p_150807_3_) > 0 || getSavedLightValue(EnumSkyBlock.Block, p_150807_1_, p_150807_2_, p_150807_3_) > 0))
                    {
                        propagateSkylightOcclusion(p_150807_1_, p_150807_3_);
                    }
                }

                TileEntity var16;

                if (var8 instanceof ITileEntityProvider)
                {
                    var16 = func_150806_e(p_150807_1_, p_150807_2_, p_150807_3_);

                    if (var16 != null)
                    {
                        var16.updateContainingBlockInfo();
                    }
                }

                if (!worldObj.isClient)
                {
                    p_150807_4_.onBlockAdded(worldObj, var12, p_150807_2_, var13);
                }

                if (p_150807_4_ instanceof ITileEntityProvider)
                {
                    var16 = func_150806_e(p_150807_1_, p_150807_2_, p_150807_3_);

                    if (var16 == null)
                    {
                        var16 = ((ITileEntityProvider)p_150807_4_).createNewTileEntity(worldObj, p_150807_5_);
                        worldObj.setTileEntity(var12, p_150807_2_, var13, var16);
                    }

                    if (var16 != null)
                    {
                        var16.updateContainingBlockInfo();
                    }
                }

                isModified = true;
                return true;
            }
        }
    }

    /**
     * Set the metadata of a block in the chunk
     */
    public boolean setBlockMetadata(int par1, int par2, int par3, int par4)
    {
        ExtendedBlockStorage var5 = storageArrays[par2 >> 4];

        if (var5 == null)
        {
            return false;
        }
        else
        {
            int var6 = var5.getExtBlockMetadata(par1, par2 & 15, par3);

            if (var6 == par4)
            {
                return false;
            }
            else
            {
                isModified = true;
                var5.setExtBlockMetadata(par1, par2 & 15, par3, par4);

                if (var5.func_150819_a(par1, par2 & 15, par3) instanceof ITileEntityProvider)
                {
                    TileEntity var7 = func_150806_e(par1, par2, par3);

                    if (var7 != null)
                    {
                        var7.updateContainingBlockInfo();
                        var7.blockMetadata = par4;
                    }
                }

                return true;
            }
        }
    }

    /**
     * Gets the amount of light saved in this block (doesn't adjust for
     * daylight)
     */
    public int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        ExtendedBlockStorage var5 = storageArrays[par3 >> 4];
        return var5 == null ? (canBlockSeeTheSky(par2, par3, par4) ? par1EnumSkyBlock.defaultLightValue : 0) : (par1EnumSkyBlock == EnumSkyBlock.Sky ? (worldObj.provider.hasNoSky ? 0 : var5.getExtSkylightValue(par2, par3 & 15, par4)) : (par1EnumSkyBlock == EnumSkyBlock.Block ? var5.getExtBlocklightValue(par2, par3 & 15, par4) : par1EnumSkyBlock.defaultLightValue));
    }

    /**
     * Sets the light value at the coordinate. If enumskyblock is set to sky it
     * sets it in the skylightmap and if its a block then into the
     * blocklightmap. Args enumSkyBlock, x, y, z, lightValue
     */
    public void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5)
    {
        ExtendedBlockStorage var6 = storageArrays[par3 >> 4];

        if (var6 == null)
        {
            var6 = storageArrays[par3 >> 4] = new ExtendedBlockStorage(par3 >> 4 << 4, !worldObj.provider.hasNoSky);
            generateSkylightMap();
        }

        isModified = true;

        if (par1EnumSkyBlock == EnumSkyBlock.Sky)
        {
            if (!worldObj.provider.hasNoSky)
            {
                var6.setExtSkylightValue(par2, par3 & 15, par4, par5);
            }
        }
        else if (par1EnumSkyBlock == EnumSkyBlock.Block)
        {
            var6.setExtBlocklightValue(par2, par3 & 15, par4, par5);
        }
    }

    /**
     * Gets the amount of light on a block taking into account sunlight
     */
    public int getBlockLightValue(int par1, int par2, int par3, int par4)
    {
        ExtendedBlockStorage var5 = storageArrays[par2 >> 4];

        if (var5 == null)
        {
            return !worldObj.provider.hasNoSky && par4 < EnumSkyBlock.Sky.defaultLightValue ? EnumSkyBlock.Sky.defaultLightValue - par4 : 0;
        }
        else
        {
            int var6 = worldObj.provider.hasNoSky ? 0 : var5.getExtSkylightValue(par1, par2 & 15, par3);

            if (var6 > 0)
            {
                isLit = true;
            }

            var6 -= par4;
            int var7 = var5.getExtBlocklightValue(par1, par2 & 15, par3);

            if (var7 > var6)
            {
                var6 = var7;
            }

            return var6;
        }
    }

    /**
     * Adds an entity to the chunk. Args: entity
     */
    public void addEntity(Entity par1Entity)
    {
        hasEntities = true;
        int var2 = MathHelper.floor_double(par1Entity.posX / 16.0D);
        int var3 = MathHelper.floor_double(par1Entity.posZ / 16.0D);

        if (var2 != xPosition || var3 != zPosition)
        {
            logger.error("Wrong location! " + par1Entity);
            Thread.dumpStack();
        }

        int var4 = MathHelper.floor_double(par1Entity.posY / 16.0D);

        if (var4 < 0)
        {
            var4 = 0;
        }

        if (var4 >= entityLists.length)
        {
            var4 = entityLists.length - 1;
        }

        par1Entity.addedToChunk = true;
        par1Entity.chunkCoordX = xPosition;
        par1Entity.chunkCoordY = var4;
        par1Entity.chunkCoordZ = zPosition;
        entityLists[var4].add(par1Entity);
    }

    /**
     * removes entity using its y chunk coordinate as its index
     */
    public void removeEntity(Entity par1Entity)
    {
        removeEntityAtIndex(par1Entity, par1Entity.chunkCoordY);
    }

    /**
     * Removes entity at the specified index from the entity array.
     */
    public void removeEntityAtIndex(Entity par1Entity, int par2)
    {
        if (par2 < 0)
        {
            par2 = 0;
        }

        if (par2 >= entityLists.length)
        {
            par2 = entityLists.length - 1;
        }

        entityLists[par2].remove(par1Entity);
    }

    /**
     * Returns whether is not a block above this one blocking sight to the sky
     * (done via checking against the heightmap)
     */
    public boolean canBlockSeeTheSky(int par1, int par2, int par3)
    {
        return par2 >= heightMap[par3 << 4 | par1];
    }

    public TileEntity func_150806_e(int p_150806_1_, int p_150806_2_, int p_150806_3_)
    {
        ChunkPosition var4 = new ChunkPosition(p_150806_1_, p_150806_2_, p_150806_3_);
        TileEntity var5 = (TileEntity)chunkTileEntityMap.get(var4);

        if (var5 == null)
        {
            Block var6 = func_150810_a(p_150806_1_, p_150806_2_, p_150806_3_);

            if (!var6.hasTileEntity()) { return null; }

            var5 = ((ITileEntityProvider)var6).createNewTileEntity(worldObj, getBlockMetadata(p_150806_1_, p_150806_2_, p_150806_3_));
            worldObj.setTileEntity(xPosition * 16 + p_150806_1_, p_150806_2_, zPosition * 16 + p_150806_3_, var5);
        }

        if (var5 != null && var5.isInvalid())
        {
            chunkTileEntityMap.remove(var4);
            return null;
        }
        else
        {
            return var5;
        }
    }

    public void func_150813_a(TileEntity p_150813_1_)
    {
        int var2 = p_150813_1_.xCoord - xPosition * 16;
        int var3 = p_150813_1_.yCoord;
        int var4 = p_150813_1_.zCoord - zPosition * 16;
        func_150812_a(var2, var3, var4, p_150813_1_);

        if (isChunkLoaded)
        {
            worldObj.field_147482_g.add(p_150813_1_);
        }
    }

    public void func_150812_a(int p_150812_1_, int p_150812_2_, int p_150812_3_, TileEntity p_150812_4_)
    {
        ChunkPosition var5 = new ChunkPosition(p_150812_1_, p_150812_2_, p_150812_3_);
        p_150812_4_.setWorldObj(worldObj);
        p_150812_4_.xCoord = xPosition * 16 + p_150812_1_;
        p_150812_4_.yCoord = p_150812_2_;
        p_150812_4_.zCoord = zPosition * 16 + p_150812_3_;

        if (func_150810_a(p_150812_1_, p_150812_2_, p_150812_3_) instanceof ITileEntityProvider)
        {
            if (chunkTileEntityMap.containsKey(var5))
            {
                ((TileEntity)chunkTileEntityMap.get(var5)).invalidate();
            }

            p_150812_4_.validate();
            chunkTileEntityMap.put(var5, p_150812_4_);
        }
    }

    public void removeTileEntity(int p_150805_1_, int p_150805_2_, int p_150805_3_)
    {
        ChunkPosition var4 = new ChunkPosition(p_150805_1_, p_150805_2_, p_150805_3_);

        if (isChunkLoaded)
        {
            TileEntity var5 = (TileEntity)chunkTileEntityMap.remove(var4);

            if (var5 != null)
            {
                var5.invalidate();
            }
        }
    }

    /**
     * Called when this Chunk is loaded by the ChunkProvider
     */
    public void onChunkLoad()
    {
        isChunkLoaded = true;
        worldObj.func_147448_a(chunkTileEntityMap.values());

        for (int var1 = 0; var1 < entityLists.length; ++var1)
        {
            Iterator var2 = entityLists[var1].iterator();

            while (var2.hasNext())
            {
                Entity var3 = (Entity)var2.next();
                var3.onChunkLoad();
            }

            worldObj.addLoadedEntities(entityLists[var1]);
        }
    }

    /**
     * Called when this Chunk is unloaded by the ChunkProvider
     */
    public void onChunkUnload()
    {
        isChunkLoaded = false;
        Iterator var1 = chunkTileEntityMap.values().iterator();

        while (var1.hasNext())
        {
            TileEntity var2 = (TileEntity)var1.next();
            worldObj.func_147457_a(var2);
        }

        for (int var3 = 0; var3 < entityLists.length; ++var3)
        {
            worldObj.unloadEntities(entityLists[var3]);
        }
    }

    /**
     * Sets the isModified flag for this Chunk
     */
    public void setChunkModified()
    {
        isModified = true;
    }

    /**
     * Fills the given list of all entities that intersect within the given
     * bounding box that aren't the passed entity Args: entity, aabb, listToFill
     */
    public void getEntitiesWithinAABBForEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector)
    {
        int var5 = MathHelper.floor_double((par2AxisAlignedBB.minY - 2.0D) / 16.0D);
        int var6 = MathHelper.floor_double((par2AxisAlignedBB.maxY + 2.0D) / 16.0D);
        var5 = MathHelper.clamp_int(var5, 0, entityLists.length - 1);
        var6 = MathHelper.clamp_int(var6, 0, entityLists.length - 1);

        for (int var7 = var5; var7 <= var6; ++var7)
        {
            List var8 = entityLists[var7];

            for (int var9 = 0; var9 < var8.size(); ++var9)
            {
                Entity var10 = (Entity)var8.get(var9);

                if (var10 != par1Entity && var10.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(var10)))
                {
                    par3List.add(var10);
                    Entity[] var11 = var10.getParts();

                    if (var11 != null)
                    {
                        for (int var12 = 0; var12 < var11.length; ++var12)
                        {
                            var10 = var11[var12];

                            if (var10 != par1Entity && var10.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(var10)))
                            {
                                par3List.add(var10);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all entities that can be assigned to the specified class. Args:
     * entityClass, aabb, listToFill
     */
    public void getEntitiesOfTypeWithinAAAB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector)
    {
        int var5 = MathHelper.floor_double((par2AxisAlignedBB.minY - 2.0D) / 16.0D);
        int var6 = MathHelper.floor_double((par2AxisAlignedBB.maxY + 2.0D) / 16.0D);
        var5 = MathHelper.clamp_int(var5, 0, entityLists.length - 1);
        var6 = MathHelper.clamp_int(var6, 0, entityLists.length - 1);

        for (int var7 = var5; var7 <= var6; ++var7)
        {
            List var8 = entityLists[var7];

            for (int var9 = 0; var9 < var8.size(); ++var9)
            {
                Entity var10 = (Entity)var8.get(var9);

                if (par1Class.isAssignableFrom(var10.getClass()) && var10.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(var10)))
                {
                    par3List.add(var10);
                }
            }
        }
    }

    /**
     * Returns true if this Chunk needs to be saved
     */
    public boolean needsSaving(boolean par1)
    {
        if (par1)
        {
            if (hasEntities && worldObj.getTotalWorldTime() != lastSaveTime || isModified) { return true; }
        }
        else if (hasEntities && worldObj.getTotalWorldTime() >= lastSaveTime + 600L) { return true; }

        return isModified;
    }

    public Random getRandomWithSeed(long par1)
    {
        return new Random(worldObj.getSeed() + xPosition * xPosition * 4987142 + xPosition * 5947611 + zPosition * zPosition * 4392871L + zPosition * 389711 ^ par1);
    }

    public boolean isEmpty()
    {
        return false;
    }

    public void populateChunk(IChunkProvider par1IChunkProvider, IChunkProvider par2IChunkProvider, int par3, int par4)
    {
        if (!isTerrainPopulated && par1IChunkProvider.chunkExists(par3 + 1, par4 + 1) && par1IChunkProvider.chunkExists(par3, par4 + 1) && par1IChunkProvider.chunkExists(par3 + 1, par4))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3, par4);
        }

        if (par1IChunkProvider.chunkExists(par3 - 1, par4) && !par1IChunkProvider.provideChunk(par3 - 1, par4).isTerrainPopulated && par1IChunkProvider.chunkExists(par3 - 1, par4 + 1) && par1IChunkProvider.chunkExists(par3, par4 + 1) && par1IChunkProvider.chunkExists(par3 - 1, par4 + 1))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3 - 1, par4);
        }

        if (par1IChunkProvider.chunkExists(par3, par4 - 1) && !par1IChunkProvider.provideChunk(par3, par4 - 1).isTerrainPopulated && par1IChunkProvider.chunkExists(par3 + 1, par4 - 1) && par1IChunkProvider.chunkExists(par3 + 1, par4 - 1) && par1IChunkProvider.chunkExists(par3 + 1, par4))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3, par4 - 1);
        }

        if (par1IChunkProvider.chunkExists(par3 - 1, par4 - 1) && !par1IChunkProvider.provideChunk(par3 - 1, par4 - 1).isTerrainPopulated && par1IChunkProvider.chunkExists(par3, par4 - 1) && par1IChunkProvider.chunkExists(par3 - 1, par4))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3 - 1, par4 - 1);
        }
    }

    /**
     * Gets the height to which rain/snow will fall. Calculates it if not
     * already stored.
     */
    public int getPrecipitationHeight(int par1, int par2)
    {
        int var3 = par1 | par2 << 4;
        int var4 = precipitationHeightMap[var3];

        if (var4 == -999)
        {
            int var5 = getTopFilledSegment() + 15;
            var4 = -1;

            while (var5 > 0 && var4 == -1)
            {
                Block var6 = func_150810_a(par1, var5, par2);
                Material var7 = var6.getMaterial();

                if (!var7.blocksMovement() && !var7.isLiquid())
                {
                    --var5;
                }
                else
                {
                    var4 = var5 + 1;
                }
            }

            precipitationHeightMap[var3] = var4;
        }

        return var4;
    }

    public void func_150804_b(boolean p_150804_1_)
    {
        if (isGapLightingUpdated && !worldObj.provider.hasNoSky && !p_150804_1_)
        {
            recheckGaps(worldObj.isClient);
        }

        field_150815_m = true;

        if (!isLightPopulated && isTerrainPopulated)
        {
            func_150809_p();
        }
    }

    public boolean func_150802_k()
    {
        return field_150815_m && isTerrainPopulated && isLightPopulated;
    }

    /**
     * Gets a ChunkCoordIntPair representing the Chunk's position.
     */
    public ChunkCoordIntPair getChunkCoordIntPair()
    {
        return new ChunkCoordIntPair(xPosition, zPosition);
    }

    /**
     * Returns whether the ExtendedBlockStorages containing levels (in blocks)
     * from arg 1 to arg 2 are fully empty (true) or not (false).
     */
    public boolean getAreLevelsEmpty(int par1, int par2)
    {
        if (par1 < 0)
        {
            par1 = 0;
        }

        if (par2 >= 256)
        {
            par2 = 255;
        }

        for (int var3 = par1; var3 <= par2; var3 += 16)
        {
            ExtendedBlockStorage var4 = storageArrays[var3 >> 4];

            if (var4 != null && !var4.isEmpty()) { return false; }
        }

        return true;
    }

    public void setStorageArrays(ExtendedBlockStorage[] par1ArrayOfExtendedBlockStorage)
    {
        storageArrays = par1ArrayOfExtendedBlockStorage;
    }

    /**
     * This method retrieves the biome at a set of coordinates
     */
    public BiomeGenBase getBiomeGenForWorldCoords(int par1, int par2, WorldChunkManager par3WorldChunkManager)
    {
        int var4 = blockBiomeArray[par2 << 4 | par1] & 255;

        if (var4 == 255)
        {
            BiomeGenBase var5 = par3WorldChunkManager.getBiomeGenAt((xPosition << 4) + par1, (zPosition << 4) + par2);
            var4 = var5.biomeID;
            blockBiomeArray[par2 << 4 | par1] = (byte)(var4 & 255);
        }

        return BiomeGenBase.func_150568_d(var4) == null ? BiomeGenBase.plains : BiomeGenBase.func_150568_d(var4);
    }

    /**
     * Returns an array containing a 16x16 mapping on the X/Z of block positions
     * in this Chunk to biome IDs.
     */
    public byte[] getBiomeArray()
    {
        return blockBiomeArray;
    }

    /**
     * Accepts a 256-entry array that contains a 16x16 mapping on the X/Z plane
     * of block positions in this Chunk to biome IDs.
     */
    public void setBiomeArray(byte[] par1ArrayOfByte)
    {
        blockBiomeArray = par1ArrayOfByte;
    }

    /**
     * Resets the relight check index to 0 for this Chunk.
     */
    public void resetRelightChecks()
    {
        queuedLightChecks = 0;
    }

    /**
     * Called once-per-chunk-per-tick, and advances the round-robin relight
     * check index by up to 8 blocks at a time. In a worst-case scenario, can
     * potentially take up to 25.6 seconds, calculated via (4096/8)/20, to
     * re-check all blocks in a chunk, which may explain lagging light updates
     * on initial world generation.
     */
    public void enqueueRelightChecks()
    {
        for (int var1 = 0; var1 < 8; ++var1)
        {
            if (queuedLightChecks >= 4096) { return; }

            int var2 = queuedLightChecks % 16;
            int var3 = queuedLightChecks / 16 % 16;
            int var4 = queuedLightChecks / 256;
            ++queuedLightChecks;
            int var5 = (xPosition << 4) + var3;
            int var6 = (zPosition << 4) + var4;

            for (int var7 = 0; var7 < 16; ++var7)
            {
                int var8 = (var2 << 4) + var7;

                if (storageArrays[var2] == null && (var7 == 0 || var7 == 15 || var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15) || storageArrays[var2] != null && storageArrays[var2].func_150819_a(var3, var7, var4).getMaterial() == Material.air)
                {
                    if (worldObj.getBlock(var5, var8 - 1, var6).getLightValue() > 0)
                    {
                        worldObj.func_147451_t(var5, var8 - 1, var6);
                    }

                    if (worldObj.getBlock(var5, var8 + 1, var6).getLightValue() > 0)
                    {
                        worldObj.func_147451_t(var5, var8 + 1, var6);
                    }

                    if (worldObj.getBlock(var5 - 1, var8, var6).getLightValue() > 0)
                    {
                        worldObj.func_147451_t(var5 - 1, var8, var6);
                    }

                    if (worldObj.getBlock(var5 + 1, var8, var6).getLightValue() > 0)
                    {
                        worldObj.func_147451_t(var5 + 1, var8, var6);
                    }

                    if (worldObj.getBlock(var5, var8, var6 - 1).getLightValue() > 0)
                    {
                        worldObj.func_147451_t(var5, var8, var6 - 1);
                    }

                    if (worldObj.getBlock(var5, var8, var6 + 1).getLightValue() > 0)
                    {
                        worldObj.func_147451_t(var5, var8, var6 + 1);
                    }

                    worldObj.func_147451_t(var5, var8, var6);
                }
            }
        }
    }

    public void func_150809_p()
    {
        isTerrainPopulated = true;
        isLightPopulated = true;

        if (!worldObj.provider.hasNoSky)
        {
            if (worldObj.checkChunksExist(xPosition * 16 - 1, 0, zPosition * 16 - 1, xPosition * 16 + 1, 63, zPosition * 16 + 1))
            {
                for (int var1 = 0; var1 < 16; ++var1)
                {
                    for (int var2 = 0; var2 < 16; ++var2)
                    {
                        if (!func_150811_f(var1, var2))
                        {
                            isLightPopulated = false;
                            break;
                        }
                    }
                }

                if (isLightPopulated)
                {
                    Chunk var3 = worldObj.getChunkFromBlockCoords(xPosition * 16 - 1, zPosition * 16);
                    var3.func_150801_a(3);
                    var3 = worldObj.getChunkFromBlockCoords(xPosition * 16 + 16, zPosition * 16);
                    var3.func_150801_a(1);
                    var3 = worldObj.getChunkFromBlockCoords(xPosition * 16, zPosition * 16 - 1);
                    var3.func_150801_a(0);
                    var3 = worldObj.getChunkFromBlockCoords(xPosition * 16, zPosition * 16 + 16);
                    var3.func_150801_a(2);
                }
            }
            else
            {
                isLightPopulated = false;
            }
        }
    }

    private void func_150801_a(int p_150801_1_)
    {
        if (isTerrainPopulated)
        {
            int var2;

            if (p_150801_1_ == 3)
            {
                for (var2 = 0; var2 < 16; ++var2)
                {
                    func_150811_f(15, var2);
                }
            }
            else if (p_150801_1_ == 1)
            {
                for (var2 = 0; var2 < 16; ++var2)
                {
                    func_150811_f(0, var2);
                }
            }
            else if (p_150801_1_ == 0)
            {
                for (var2 = 0; var2 < 16; ++var2)
                {
                    func_150811_f(var2, 15);
                }
            }
            else if (p_150801_1_ == 2)
            {
                for (var2 = 0; var2 < 16; ++var2)
                {
                    func_150811_f(var2, 0);
                }
            }
        }
    }

    private boolean func_150811_f(int p_150811_1_, int p_150811_2_)
    {
        int var3 = getTopFilledSegment();
        boolean var4 = false;
        boolean var5 = false;
        int var6;

        for (var6 = var3 + 16 - 1; var6 > 63 || var6 > 0 && !var5; --var6)
        {
            int var7 = func_150808_b(p_150811_1_, var6, p_150811_2_);

            if (var7 == 255 && var6 < 63)
            {
                var5 = true;
            }

            if (!var4 && var7 > 0)
            {
                var4 = true;
            }
            else if (var4 && var7 == 0 && !worldObj.func_147451_t(xPosition * 16 + p_150811_1_, var6, zPosition * 16 + p_150811_2_)) { return false; }
        }

        for (; var6 > 0; --var6)
        {
            if (func_150810_a(p_150811_1_, var6, p_150811_2_).getLightValue() > 0)
            {
                worldObj.func_147451_t(xPosition * 16 + p_150811_1_, var6, zPosition * 16 + p_150811_2_);
            }
        }

        return true;
    }
}
