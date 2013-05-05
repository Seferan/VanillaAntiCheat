package tk.afffsdd.vanillaanticheat;

import java.util.Iterator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumChatFormatting;
import java.util.logging.Level;

public class VanillaAntiCheatUtils
{
	public static void notifyAdmins(String message)
	{
		Iterator var1 = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();

        while (var1.hasNext())
        {
            EntityPlayerMP var7 = (EntityPlayerMP) var1.next();

            if (MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(var7.username))
            {
                var7.sendChatToPlayer("" +  EnumChatFormatting.BLUE + "[VAC]: " + message);
            }
        }
	}
	
	public static void logMessage(String message)
    {
    	MinecraftServer.getServer().getLogAgent().getServerLogger().log(new LevelVAC(), message);
    }
	
	public static void notifyAndLog(String message)
	{
		notifyAdmins(message);
		logMessage(message);
	}
	
    public static boolean isOp(String playerName)
    {
    	return MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(playerName);
    }
    
    public static boolean isOp(EntityPlayerMP player)
    {
    	return isOp(player.username);
    }
    
    public static boolean isOpOrCreative(EntityPlayerMP player)
    {
    	return isOp(player.username) || player.theItemInWorldManager.isCreative();
    }
}
