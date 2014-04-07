package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S10PacketSpawnPainting extends Packet
{
    private int field_148973_a;
    private int field_148971_b;
    private int field_148972_c;
    private int field_148969_d;
    private int field_148970_e;
    private String field_148968_f;
    private static final String __OBFID = "CL_00001280";

    public S10PacketSpawnPainting()
    {
    }

    public S10PacketSpawnPainting(EntityPainting p_i45170_1_)
    {
        field_148973_a = p_i45170_1_.getEntityId();
        field_148971_b = p_i45170_1_.field_146063_b;
        field_148972_c = p_i45170_1_.field_146064_c;
        field_148969_d = p_i45170_1_.field_146062_d;
        field_148970_e = p_i45170_1_.hangingDirection;
        field_148968_f = p_i45170_1_.art.title;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        field_148973_a = p_148837_1_.readVarIntFromBuffer();
        field_148968_f = p_148837_1_.readStringFromBuffer(EntityPainting.EnumArt.maxArtTitleLength);
        field_148971_b = p_148837_1_.readInt();
        field_148972_c = p_148837_1_.readInt();
        field_148969_d = p_148837_1_.readInt();
        field_148970_e = p_148837_1_.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeVarIntToBuffer(field_148973_a);
        p_148840_1_.writeStringToBuffer(field_148968_f);
        p_148840_1_.writeInt(field_148971_b);
        p_148840_1_.writeInt(field_148972_c);
        p_148840_1_.writeInt(field_148969_d);
        p_148840_1_.writeInt(field_148970_e);
    }

    public void func_148833_a(INetHandlerPlayClient p_148967_1_)
    {
        p_148967_1_.handleSpawnPainting(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values.
     * Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, type=%s, x=%d, y=%d, z=%d", new Object[] {Integer.valueOf(field_148973_a), field_148968_f, Integer.valueOf(field_148971_b), Integer.valueOf(field_148972_c), Integer.valueOf(field_148969_d)});
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}
