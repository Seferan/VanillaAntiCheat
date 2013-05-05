package net.minecraft.src;

import tk.afffsdd.vanillaanticheat.VanillaAntiCheat;

public class CommandChangelog extends CommandBase {

	public CommandChangelog()
	{
		super();
	}
	
	public String getCommandName()
	{
		return "changelog";
	}
	
    public int getRequiredPermissionLevel()
    {
        return 3;
    }
    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/changelog";
    }

	public void processCommand(ICommandSender var1, String[] var2)
	{
		EntityPlayerMP sender = getCommandSenderAsPlayer(var1);
    	sender.sendChatToPlayer(EnumChatFormatting.BLUE + "VanillaAntiCheat Changelog:");
    	sender.sendChatToPlayer(EnumChatFormatting.AQUA + "Version 1.4:");
    	sender.sendChatToPlayer("Disabled TNT placing for non-ops and creative players");
    	sender.sendChatToPlayer("Disabled lightning fires");
    	sender.sendChatToPlayer("Console logs from VAC now have the [VAC] prefix instead of the [WARNING] [VAC] prefix.");
	}
}
