package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload extends Packet
{
    private String field_149562_a;
    private int field_149560_b;
    private byte[] field_149561_c;
    private static final String __OBFID = "CL_00001356";

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149562_a = p_148837_1_.readStringFromBuffer(20);
        field_149560_b = p_148837_1_.readShort();

        if (field_149560_b > 0 && field_149560_b < 32767)
        {
            field_149561_c = new byte[field_149560_b];
            p_148837_1_.readBytes(field_149561_c);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeStringToBuffer(field_149562_a);
        p_148840_1_.writeShort((short)field_149560_b);

        if (field_149561_c != null)
        {
            p_148840_1_.writeBytes(field_149561_c);
        }
    }

    public void func_148833_a(INetHandlerPlayServer p_149557_1_)
    {
        p_149557_1_.func_147349_a(this);
    }

    public String func_149559_c()
    {
        return field_149562_a;
    }

    public byte[] func_149558_e()
    {
        return field_149561_c;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayServer)p_148833_1_);
    }
}
