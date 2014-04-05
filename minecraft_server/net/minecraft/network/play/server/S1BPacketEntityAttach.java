package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1BPacketEntityAttach extends Packet
{
    private int field_149408_a;
    private int field_149406_b;
    private int field_149407_c;
    private static final String __OBFID = "CL_00001327";

    public S1BPacketEntityAttach()
    {
    }

    public S1BPacketEntityAttach(int p_i45218_1_, Entity p_i45218_2_,
            Entity p_i45218_3_)
    {
        this.field_149408_a = p_i45218_1_;
        this.field_149406_b = p_i45218_2_.getEntityId();
        this.field_149407_c = p_i45218_3_ != null ? p_i45218_3_.getEntityId()
                : -1;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149406_b = p_148837_1_.readInt();
        this.field_149407_c = p_148837_1_.readInt();
        this.field_149408_a = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_149406_b);
        p_148840_1_.writeInt(this.field_149407_c);
        p_148840_1_.writeByte(this.field_149408_a);
    }

    public void func_148833_a(INetHandlerPlayClient p_149405_1_)
    {
        p_149405_1_.handleEntityAttach(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
