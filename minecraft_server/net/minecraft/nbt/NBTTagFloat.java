package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTBase.NBTPrimitive
{
    /** The float value for the tag. */
    private float data;
    private static final String __OBFID = "CL_00001220";

    NBTTagFloat()
    {
    }

    public NBTTagFloat(float p_i45131_1_)
    {
        data = p_i45131_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeFloat(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        data = par1DataInput.readFloat();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)5;
    }

    public String toString()
    {
        return "" + data + "f";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagFloat(data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagFloat var2 = (NBTTagFloat)par1Obj;
            return data == var2.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ Float.floatToIntBits(data);
    }

    public long func_150291_c()
    {
        return (long)data;
    }

    public int func_150287_d()
    {
        return MathHelper.floor_float(data);
    }

    public short func_150289_e()
    {
        return (short)(MathHelper.floor_float(data) & 65535);
    }

    public byte func_150290_f()
    {
        return (byte)(MathHelper.floor_float(data) & 255);
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
