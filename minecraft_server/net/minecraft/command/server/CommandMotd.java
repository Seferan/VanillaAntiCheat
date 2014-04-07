package net.minecraft.command.server;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandMotd extends CommandBase
{
    public String getCommandName()
    {
        return "motd";
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
        return "/motd";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (MinecraftServer.getServer().getConfigurationManager().getMotd().length == 0)
        {
            par1ICommandSender.addChatMessage("(No motd)");
            return;
        }

        boolean first = true;
        for(String line : MinecraftServer.getServer().getConfigurationManager().getMotd())
        {
            ChatComponentText message = new ChatComponentText(line);
            if (line.contains("\\!"))
            {
                String[] parts = line.split("\\\\!");
                message = new ChatComponentText(parts[0]);

                ChatComponentText sibling = new ChatComponentText(parts[1]);
                sibling.getChatStyle().setBold(true);
                message.appendSibling(sibling);
            }
            if (line.startsWith("\\- "))
            {
                ChatComponentText sibling;
                if (message.getSiblings().size() > 0)
                {
                    sibling = (ChatComponentText)message.getSiblings().get(0);
                }
                else
                {
                    sibling = new ChatComponentText(line.substring(3));
                }
                sibling.getChatStyle().setColor(EnumChatFormatting.RED);

                message = new ChatComponentText("- ");
                message.getChatStyle().setColor(EnumChatFormatting.GRAY);
                message.appendSibling(sibling);
            }
            if (first)
            {
                message.getChatStyle().setColor(EnumChatFormatting.GOLD);
                first = false;
            }
            par1ICommandSender.addChatMessage(message);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length >= 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
