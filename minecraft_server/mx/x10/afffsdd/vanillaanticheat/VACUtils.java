package mx.x10.afffsdd.vanillaanticheat;

import java.util.Iterator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import java.util.logging.Level;

public class VACUtils {
	public static void notifyAdmins(String message)
	{
		Iterator it = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();

        while (it.hasNext())
        {
            EntityPlayerMP player = (EntityPlayerMP) it.next();

            if (MinecraftServer.isPlayerOpped(player))
            {
            	player.addChatMessage("" +  EnumChatFormatting.BLUE + "[VAC]: " + message);
            }
        }
	}
	
	public static void notifyAndLog(String message)
	{
		notifyAdmins(message);
		MinecraftServer.getServer().logVAC(message);
	}
}
