package net.minecraft.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandMyIP extends CommandBase
{

    private static final String __OBFID = "CL_00000641";

    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"ip", "whatismyip"});
    }

    public String getCommandName()
    {
        return "myip";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/myip";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        EntityPlayerMP sender = getPlayer(par1ICommandSender, par1ICommandSender.getUsername());

        if (sender == par1ICommandSender) // safety precautions, just in case
        {
            String ip = sender.getPlayerIP();
            MinecraftServer.anonymousTell(sender, "Welcome! Your external IP appears to be: " + ip);
            MinecraftServer.anonymousTell(sender, "This has not been logged, nor will anyone else recieve this message.");
            MinecraftServer.anonymousTell(sender, "Have a good day!");
        }
        else
        {
            par1ICommandSender.addChatMessage("WTF? Looked up the wrong player.");
            par1ICommandSender.addChatMessage("Please report this to an admin and try again later.");
        }
    }
}
