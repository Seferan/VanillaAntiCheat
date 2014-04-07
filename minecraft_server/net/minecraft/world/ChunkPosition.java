package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChunkPosition
{
    public final int chunkPosX;
    public final int chunkPosY;
    public final int chunkPosZ;
    private static final String __OBFID = "CL_00000132";

    public ChunkPosition(int p_i45363_1_, int p_i45363_2_, int p_i45363_3_)
    {
        chunkPosX = p_i45363_1_;
        chunkPosY = p_i45363_2_;
        chunkPosZ = p_i45363_3_;
    }

    public ChunkPosition(Vec3 p_i45364_1_)
    {
        this(MathHelper.floor_double(p_i45364_1_.xCoord), MathHelper.floor_double(p_i45364_1_.yCoord), MathHelper.floor_double(p_i45364_1_.zCoord));
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof ChunkPosition))
        {
            return false;
        }
        else
        {
            ChunkPosition var2 = (ChunkPosition)par1Obj;
            return var2.chunkPosX == chunkPosX && var2.chunkPosY == chunkPosY && var2.chunkPosZ == chunkPosZ;
        }
    }

    public int hashCode()
    {
        return chunkPosX * 8976890 + chunkPosY * 981131 + chunkPosZ;
    }
}
