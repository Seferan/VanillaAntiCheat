package net.minecraft.src;

import net.minecraft.server.MinecraftServer;
import java.lang.reflect.*;

import tk.afffsdd.servermods.DamageSourceSuicide;

public class CommandSuicide extends CommandBase
{
	public CommandSuicide()
	{
		super();
	}
	
    public String getCommandName()
    {
        return "suicide";
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/suicide";
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
    	EntityPlayerMP sender = getCommandSenderAsPlayer(par1ICommandSender);
    	Method m;
    	sender.damageEntity(new DamageSourceSuicide(), 9001);
    	MinecraftServer.getServer().getConfigurationManager().sendChatMsg(sender.username + " decided to end it all.");
    }
}