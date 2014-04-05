package net.minecraft.command.server;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandDeOwner extends CommandBase
{

    public String getCommandName()
    {
        return "deowner";
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
        return "/deowner <player>";
    }

    public void processCommand(ICommandSender par1ICommandSender,
            String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1 && par2ArrayOfStr[0].length() > 0)
        {
            String name = par2ArrayOfStr[0];
            if (MinecraftServer.isPlayerOwner(par1ICommandSender))
            {
                MinecraftServer.getServer().getConfigurationManager()
                        .removeOwner(name);
                notifyAdmins(par1ICommandSender, "De-ownered " + name);
            }
            else
            {
                notifyAdmins(par1ICommandSender, "Tried to de-owner " + name
                        + "!");
            }
        }
        else
        {
            throw new WrongUsageException(getCommandUsage(par1ICommandSender),
                    new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender,
            String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsFromIterableMatchingLastWord(
                par2ArrayOfStr, MinecraftServer.getServer()
                        .getConfigurationManager().getOwners()) : null;
    }
}
