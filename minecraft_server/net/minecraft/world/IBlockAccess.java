package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;

public interface IBlockAccess
{
    Block getBlock(int var1, int var2, int var3);

    TileEntity getTileEntity(int var1, int var2, int var3);

    /**
     * Returns the block metadata at coords x,y,z
     */
    int getBlockMetadata(int var1, int var2, int var3);

    /**
     * Return the Vec3Pool object for this world.
     */
    Vec3Pool getWorldVec3Pool();

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4);
}
