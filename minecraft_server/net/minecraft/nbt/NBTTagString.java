package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase
{
    /** The string value for the tag (cannot be empty). */
    private String data;
    private static final String __OBFID = "CL_00001228";

    public NBTTagString()
    {
        data = "";
    }

    public NBTTagString(String par1Str)
    {
        data = par1Str;

        if (par1Str == null) { throw new IllegalArgumentException("Empty string not allowed"); }
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeUTF(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        data = par1DataInput.readUTF();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)8;
    }

    public String toString()
    {
        return "\"" + data + "\"";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagString(data);
    }

    public boolean equals(Object par1Obj)
    {
        if (!super.equals(par1Obj))
        {
            return false;
        }
        else
        {
            NBTTagString var2 = (NBTTagString)par1Obj;
            return data == null && var2.data == null || data != null && data.equals(var2.data);
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ data.hashCode();
    }

    public String func_150285_a_()
    {
        return data;
    }
}
