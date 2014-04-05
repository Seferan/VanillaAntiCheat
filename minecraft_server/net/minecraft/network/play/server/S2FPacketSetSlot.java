package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2FPacketSetSlot extends Packet
{
    private int field_149179_a;
    private int field_149177_b;
    private ItemStack field_149178_c;
    private static final String __OBFID = "CL_00001296";

    public S2FPacketSetSlot()
    {
    }

    public S2FPacketSetSlot(int p_i45188_1_, int p_i45188_2_,
            ItemStack p_i45188_3_)
    {
        this.field_149179_a = p_i45188_1_;
        this.field_149177_b = p_i45188_2_;
        this.field_149178_c = p_i45188_3_ == null ? null : p_i45188_3_.copy();
    }

    public void func_148833_a(INetHandlerPlayClient p_149176_1_)
    {
        p_149176_1_.handleSetSlot(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149179_a = p_148837_1_.readUnsignedByte();
        this.field_149177_b = p_148837_1_.readShort();
        this.field_149178_c = p_148837_1_.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.field_149179_a);
        p_148840_1_.writeShort(this.field_149177_b);
        p_148840_1_.writeItemStackToBuffer(this.field_149178_c);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
