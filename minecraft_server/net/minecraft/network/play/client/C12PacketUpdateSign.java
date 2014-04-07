package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C12PacketUpdateSign extends Packet
{
    private int field_149593_a;
    private int field_149591_b;
    private int field_149592_c;
    private String[] field_149590_d;
    private static final String __OBFID = "CL_00001370";

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149593_a = p_148837_1_.readInt();
        field_149591_b = p_148837_1_.readShort();
        field_149592_c = p_148837_1_.readInt();
        field_149590_d = new String[4];

        for (int var2 = 0; var2 < 4; ++var2)
        {
            field_149590_d[var2] = p_148837_1_.readStringFromBuffer(15);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(field_149593_a);
        p_148840_1_.writeShort(field_149591_b);
        p_148840_1_.writeInt(field_149592_c);

        for (int var2 = 0; var2 < 4; ++var2)
        {
            p_148840_1_.writeStringToBuffer(field_149590_d[var2]);
        }
    }

    public void func_148833_a(INetHandlerPlayServer p_149587_1_)
    {
        p_149587_1_.handleUpdateSign(this);
    }

    public int getX()
    {
        return field_149593_a;
    }

    public int getY()
    {
        return field_149591_b;
    }

    public int getZ()
    {
        return field_149592_c;
    }

    public String[] func_149589_f()
    {
        return field_149590_d;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}
