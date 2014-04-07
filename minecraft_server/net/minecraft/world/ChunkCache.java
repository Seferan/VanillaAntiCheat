package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess
{
    private int chunkX;
    private int chunkZ;
    private Chunk[][] chunkArray;

    /** set by !chunk.getAreLevelsEmpty */
    private boolean hasExtendedLevels;

    /** Reference to the World object. */
    private World worldObj;
    private static final String __OBFID = "CL_00000155";

    public ChunkCache(World par1World, int par2, int par3, int par4, int par5, int par6, int par7, int par8)
    {
        worldObj = par1World;
        chunkX = par2 - par8 >> 4;
        chunkZ = par4 - par8 >> 4;
        int var9 = par5 + par8 >> 4;
        int var10 = par7 + par8 >> 4;
        chunkArray = new Chunk[var9 - chunkX + 1][var10 - chunkZ + 1];
        hasExtendedLevels = true;
        int var11;
        int var12;
        Chunk var13;

        for (var11 = chunkX; var11 <= var9; ++var11)
        {
            for (var12 = chunkZ; var12 <= var10; ++var12)
            {
                var13 = par1World.getChunkFromChunkCoords(var11, var12);

                if (var13 != null)
                {
                    chunkArray[var11 - chunkX][var12 - chunkZ] = var13;
                }
            }
        }

        for (var11 = par2 >> 4; var11 <= par5 >> 4; ++var11)
        {
            for (var12 = par4 >> 4; var12 <= par7 >> 4; ++var12)
            {
                var13 = chunkArray[var11 - chunkX][var12 - chunkZ];

                if (var13 != null && !var13.getAreLevelsEmpty(par3, par6))
                {
                    hasExtendedLevels = false;
                }
            }
        }
    }

    public Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_)
    {
        Block var4 = Blocks.air;

        if (p_147439_2_ >= 0 && p_147439_2_ < 256)
        {
            int var5 = (p_147439_1_ >> 4) - chunkX;
            int var6 = (p_147439_3_ >> 4) - chunkZ;

            if (var5 >= 0 && var5 < chunkArray.length && var6 >= 0 && var6 < chunkArray[var5].length)
            {
                Chunk var7 = chunkArray[var5][var6];

                if (var7 != null)
                {
                    var4 = var7.func_150810_a(p_147439_1_ & 15, p_147439_2_, p_147439_3_ & 15);
                }
            }
        }

        return var4;
    }

    public TileEntity getTileEntity(int p_147438_1_, int p_147438_2_, int p_147438_3_)
    {
        int var4 = (p_147438_1_ >> 4) - chunkX;
        int var5 = (p_147438_3_ >> 4) - chunkZ;
        return chunkArray[var4][var5].func_150806_e(p_147438_1_ & 15, p_147438_2_, p_147438_3_ & 15);
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else if (par2 >= 256)
        {
            return 0;
        }
        else
        {
            int var4 = (par1 >> 4) - chunkX;
            int var5 = (par3 >> 4) - chunkZ;
            return chunkArray[var4][var5].getBlockMetadata(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Return the Vec3Pool object for this world.
     */
    public Vec3Pool getWorldVec3Pool()
    {
        return worldObj.getWorldVec3Pool();
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z,
     * direction
     */
    public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4)
    {
        return getBlock(par1, par2, par3).isProvidingStrongPower(this, par1, par2, par3, par4);
    }
}
