package net.minecraft.command.server;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandOwner extends CommandBase
{

    public String getCommandName()
    {
        return "owner";
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return MinecraftServer.isPlayerOwner(par1ICommandSender);
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
        return "/owner <player>";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1 && par2ArrayOfStr[0].length() > 0)
        {
            String name = par2ArrayOfStr[0];
            MinecraftServer.getServer().getConfigurationManager().addOwner(name);
            notifyAdmins(par1ICommandSender, "Ownered " + name);
        }
        else
        {
            throw new WrongUsageException(getCommandUsage(par1ICommandSender), new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
        {
            String var3 = par2ArrayOfStr[par2ArrayOfStr.length - 1];
            ArrayList var4 = new ArrayList();
            String[] var5 = MinecraftServer.getServer().getAllUsernames();
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7)
            {
                String var8 = var5[var7];

                if (!MinecraftServer.getServer().getConfigurationManager().isPlayerOwner(var8) && doesStringStartWith(var3, var8))
                {
                    var4.add(var8);
                }
            }

            return var4;
        }
        else
        {
            return null;
        }
    }
}
