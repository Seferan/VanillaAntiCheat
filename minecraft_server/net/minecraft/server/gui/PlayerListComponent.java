package net.minecraft.server.gui;

import java.util.Vector;

import javax.swing.JList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class PlayerListComponent extends JList implements IUpdatePlayerListBox
{
    private MinecraftServer field_120015_a;
    private int field_120014_b;
    private static final String __OBFID = "CL_00001795";

    public PlayerListComponent(MinecraftServer par1MinecraftServer)
    {
        field_120015_a = par1MinecraftServer;
        par1MinecraftServer.func_82010_a(this);
    }

    /**
     * Updates the JList with a new model.
     */
    public void update()
    {
        if (field_120014_b++ % 20 == 0)
        {
            Vector var1 = new Vector();

            for (int var2 = 0; var2 < field_120015_a.getConfigurationManager().playerEntityList.size(); ++var2)
            {
                var1.add(((EntityPlayerMP)field_120015_a.getConfigurationManager().playerEntityList.get(var2)).getUsername());
            }

            this.setListData(var1);
        }
    }
}
