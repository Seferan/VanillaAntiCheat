package net.minecraft.command.server;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandGc extends CommandBase
{
    public String getCommandName()
    {
        return "gc";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/gc";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        System.gc();
        notifyAdmins(par1ICommandSender, "Performed garbage collection (System.gc())");
    }
}
