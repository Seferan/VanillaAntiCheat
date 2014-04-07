package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective extends Packet
{
    private String field_149343_a;
    private String field_149341_b;
    private int field_149342_c;
    private static final String __OBFID = "CL_00001333";

    public S3BPacketScoreboardObjective()
    {
    }

    public S3BPacketScoreboardObjective(ScoreObjective p_i45224_1_, int p_i45224_2_)
    {
        field_149343_a = p_i45224_1_.getName();
        field_149341_b = p_i45224_1_.getDisplayName();
        field_149342_c = p_i45224_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149343_a = p_148837_1_.readStringFromBuffer(16);
        field_149341_b = p_148837_1_.readStringFromBuffer(32);
        field_149342_c = p_148837_1_.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeStringToBuffer(field_149343_a);
        p_148840_1_.writeStringToBuffer(field_149341_b);
        p_148840_1_.writeByte(field_149342_c);
    }

    public void func_148833_a(INetHandlerPlayClient p_149340_1_)
    {
        p_149340_1_.handleScoreboardObjective(this);
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
