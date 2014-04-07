package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1FPacketSetExperience extends Packet
{
    private float field_149401_a;
    private int field_149399_b;
    private int field_149400_c;
    private static final String __OBFID = "CL_00001331";

    public S1FPacketSetExperience()
    {
    }

    public S1FPacketSetExperience(float p_i45222_1_, int p_i45222_2_, int p_i45222_3_)
    {
        field_149401_a = p_i45222_1_;
        field_149399_b = p_i45222_2_;
        field_149400_c = p_i45222_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149401_a = p_148837_1_.readFloat();
        field_149400_c = p_148837_1_.readShort();
        field_149399_b = p_148837_1_.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeFloat(field_149401_a);
        p_148840_1_.writeShort(field_149400_c);
        p_148840_1_.writeShort(field_149399_b);
    }

    public void func_148833_a(INetHandlerPlayClient p_149398_1_)
    {
        p_149398_1_.handleSetExperience(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
