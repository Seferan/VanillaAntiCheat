package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase.NBTPrimitive
{
    /** The integer value for the tag. */
    private int data;
    private static final String __OBFID = "CL_00001223";

    NBTTagInt()
    {
    }

    public NBTTagInt(int p_i45133_1_)
    {
        data = p_i45133_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        data = par1DataInput.readInt();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)3;
    }

    public String toString()
    {
        return "" + data;
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagInt(data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagInt var2 = (NBTTagInt)par1Obj;
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
        return (short)(data & 65535);
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
