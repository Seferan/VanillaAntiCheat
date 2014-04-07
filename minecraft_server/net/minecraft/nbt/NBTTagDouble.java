package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTBase.NBTPrimitive
{
    /** The double value for the tag. */
    private double data;
    private static final String __OBFID = "CL_00001218";

    NBTTagDouble()
    {
    }

    public NBTTagDouble(double p_i45130_1_)
    {
        data = p_i45130_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeDouble(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        data = par1DataInput.readDouble();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)6;
    }

    public String toString()
    {
        return "" + data + "d";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagDouble(data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagDouble var2 = (NBTTagDouble)par1Obj;
            return data == var2.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        long var1 = Double.doubleToLongBits(data);
        return super.hashCode() ^ (int)(var1 ^ var1 >>> 32);
    }

    public long func_150291_c()
    {
        return (long)Math.floor(data);
    }

    public int func_150287_d()
    {
        return MathHelper.floor_double(data);
    }

    public short func_150289_e()
    {
        return (short)(MathHelper.floor_double(data) & 65535);
    }

    public byte func_150290_f()
    {
        return (byte)(MathHelper.floor_double(data) & 255);
    }

    public double func_150286_g()
    {
        return data;
    }

    public float func_150288_h()
    {
        return (float)data;
    }
}
