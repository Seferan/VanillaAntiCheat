package net.minecraft.src;

import tk.afffsdd.vanillaanticheat.VanillaAntiCheat;

public class CommandVersion extends CommandBase
{
	public CommandVersion()
	{
		super();
	}
	
	public String getCommandName()
	{
		return "version";
	}
	
    public int getRequiredPermissionLevel()
    {
        return 3;
    }
    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/version";
    }

	public void processCommand(ICommandSender var1, String[] var2)
	{
		EntityPlayerMP sender = getCommandSenderAsPlayer(var1);
    	sender.sendChatToPlayer(EnumChatFormatting.BLUE + "This server is currently running VanillaAntiCheat version " + VanillaAntiCheat.version + " by " + VanillaAntiCheat.getAuthors() + ".");
    	sender.sendChatToPlayer(EnumChatFormatting.AQUA + "VanillaAntiCheat is a custom anticheat. It is closed source.");
    	sender.sendChatToPlayer(EnumChatFormatting.AQUA + "If you have any questions reguarding it, ask one of the devs/authors.");
    	sender.sendChatToPlayer("To see the changelog, use the command /changelog.");
	}
}
