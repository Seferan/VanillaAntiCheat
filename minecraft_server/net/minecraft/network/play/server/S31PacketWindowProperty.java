package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S31PacketWindowProperty extends Packet
{
    private int field_149186_a;
    private int field_149184_b;
    private int field_149185_c;
    private static final String __OBFID = "CL_00001295";

    public S31PacketWindowProperty()
    {
    }

    public S31PacketWindowProperty(int p_i45187_1_, int p_i45187_2_, int p_i45187_3_)
    {
        this.field_149186_a = p_i45187_1_;
        this.field_149184_b = p_i45187_2_;
        this.field_149185_c = p_i45187_3_;
    }

    public void func_148833_a(INetHandlerPlayClient p_149183_1_)
    {
        p_149183_1_.handleWindowProperty(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149186_a = p_148837_1_.readUnsignedByte();
        this.field_149184_b = p_148837_1_.readShort();
        this.field_149185_c = p_148837_1_.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.field_149186_a);
        p_148840_1_.writeShort(this.field_149184_b);
        p_148840_1_.writeShort(this.field_149185_c);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
