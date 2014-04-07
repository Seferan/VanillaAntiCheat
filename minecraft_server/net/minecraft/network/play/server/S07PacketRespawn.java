package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class S07PacketRespawn extends Packet
{
    private int field_149088_a;
    private EnumDifficulty field_149086_b;
    private WorldSettings.GameType field_149087_c;
    private WorldType field_149085_d;
    private static final String __OBFID = "CL_00001322";

    public S07PacketRespawn()
    {
    }

    public S07PacketRespawn(int p_i45213_1_, EnumDifficulty p_i45213_2_, WorldType p_i45213_3_, WorldSettings.GameType p_i45213_4_)
    {
        field_149088_a = p_i45213_1_;
        field_149086_b = p_i45213_2_;
        field_149087_c = p_i45213_4_;
        field_149085_d = p_i45213_3_;
    }

    public void func_148833_a(INetHandlerPlayClient p_149084_1_)
    {
        p_149084_1_.handleRespawn(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_149088_a = p_148837_1_.readInt();
        field_149086_b = EnumDifficulty.func_151523_a(p_148837_1_.readUnsignedByte());
        field_149087_c = WorldSettings.GameType.getByID(p_148837_1_.readUnsignedByte());
        field_149085_d = WorldType.parseWorldType(p_148837_1_.readStringFromBuffer(16));

        if (field_149085_d == null)
        {
            field_149085_d = WorldType.DEFAULT;
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(field_149088_a);
        p_148840_1_.writeByte(field_149086_b.func_151525_a());
        p_148840_1_.writeByte(field_149087_c.getID());
        p_148840_1_.writeStringToBuffer(field_149085_d.getWorldTypeName());
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
