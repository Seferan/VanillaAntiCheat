package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase.NBTPrimitive
{
    /** The short value for the tag. */
    private short data;
    private static final String __OBFID = "CL_00001227";

    public NBTTagShort()
    {
    }

    public NBTTagShort(short p_i45135_1_)
    {
        data = p_i45135_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeShort(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        data = par1DataInput.readShort();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)2;
    }

    public String toString()
    {
        return "" + data + "s";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagShort(data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagShort var2 = (NBTTagShort)par1Obj;
            return data == var2.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ data;
    }

    public long func_150291_c()
    {
        return data;
    }

    public int func_150287_d()
    {
        return data;
    }

    public short func_150289_e()
    {
        return data;
    }

    public byte func_150290_f()
    {
        return (byte)(data & 255);
    }

    public double func_150286_g()
    {
        return data;
    }

    public float func_150288_h()
    {
        return data;
    }
}
