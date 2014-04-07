package net.minecraft.command;

import java.util.Date;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;

public class CommandServerKick extends CommandBase
{
    private static final String __OBFID = "CL_00000550";

    public String getCommandName()
    {
        return "kick";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.kick.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0 && par2ArrayOfStr[0].length() > 1)
        {
            boolean ban = MinecraftServer.getServer().shouldKicksBeBans();
            int banLength = MinecraftServer.getServer().getKickTempbanLength();
            EntityPlayerMP var3 = MinecraftServer.getServer().getConfigurationManager().getPlayerEntity(par2ArrayOfStr[0]);
            
            String reason = "Kicked by an operator.";
            if (ban) reason += " You will be unbanned in " + String.valueOf(banLength) + " minutes.";
            
            boolean var5 = false;

            if (var3 == null)
            {
                throw new PlayerNotFoundException();
            }
            else
            {
                if (par2ArrayOfStr.length >= 2)
                {
                    reason = func_147178_a(par1ICommandSender, par2ArrayOfStr, 1).getUnformattedText();
                    var5 = true;
                }

                if (ban)
                {
                    BanEntry banEntry = new BanEntry(par2ArrayOfStr[0]);
                    banEntry.setBannedBy(par1ICommandSender.getUsername());
                    banEntry.setBanReason(reason);
                    banEntry.setBanEndDate(new Date(new Date().getTime() + banLength * 60000L));
                    MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().put(banEntry);   
                }
                var3.playerNetServerHandler.kickPlayerFromServer(reason);

                if (var5)
                {
                    notifyAdmins(par1ICommandSender, "commands.kick.success.reason", new Object[] {var3.getUsername(), reason});
                }
                else
                {
                    notifyAdmins(par1ICommandSender, "commands.kick.success", new Object[] {var3.getUsername()});
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.kick.usage", new Object[0]);
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
