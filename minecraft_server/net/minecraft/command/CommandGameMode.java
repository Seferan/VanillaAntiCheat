package net.minecraft.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;

public class CommandGameMode extends CommandBase
{
    private static final String __OBFID = "CL_00000448";

    public String getCommandName()
    {
        return "gamemode";
    }
    
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"gm"});
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.gamemode.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0)
        {
            WorldSettings.GameType var3 = getGameModeFromCommand(par1ICommandSender, par2ArrayOfStr[0]);
            EntityPlayerMP var4;
            ChatComponentTranslation var5 = new ChatComponentTranslation("gameMode." + var3.getName(), new Object[0]);
            if (par2ArrayOfStr.length >= 2)
            {
                var4 = getPlayer(par1ICommandSender, par2ArrayOfStr[1]);
                if (isTargetNonOp(var4, par1ICommandSender))
                {
                    notifyAdmins(par1ICommandSender, "Tried to set non-op " + var4.getUsername() + "'s game mode to " + var5.getUnformattedText() + "!");
                    return;
                }
            }
            else
            {
                var4 = getCommandSenderAsPlayer(par1ICommandSender);
            }
            var4.setGameType(var3);
            var4.fallDistance = 0.0F;

            if (var4 != par1ICommandSender)
            {
                notifyAdmins(par1ICommandSender, 1, "commands.gamemode.success.other", new Object[] {var4.getUsername(), var5});
            }
            else
            {
                ChatComponentText cc = new ChatComponentText("Warning: Use /creative or /survival next time if you're setting your own gamemode.");
                cc.getChatStyle().setColor(EnumChatFormatting.GRAY);
                par1ICommandSender.addChatMessage(cc);
                notifyAdmins(par1ICommandSender, 1, "commands.gamemode.success.self", new Object[] {var5});
            }
        }
        else
        {
            throw new WrongUsageException("commands.gamemode.usage", new Object[0]);
        }
    }

    /**
     * Gets the Game Mode specified in the command.
     */
    protected WorldSettings.GameType getGameModeFromCommand(ICommandSender par1ICommandSender, String par2Str)
    {
        return !par2Str.equalsIgnoreCase(WorldSettings.GameType.SURVIVAL.getName()) && !par2Str.equalsIgnoreCase("s") ? (!par2Str.equalsIgnoreCase(WorldSettings.GameType.CREATIVE.getName()) && !par2Str.equalsIgnoreCase("c") ? (!par2Str.equalsIgnoreCase(WorldSettings.GameType.ADVENTURE.getName()) && !par2Str.equalsIgnoreCase("a") ? WorldSettings.getGameTypeById(parseIntBounded(par1ICommandSender, par2Str, 0, WorldSettings.GameType.values().length - 2)) : WorldSettings.GameType.ADVENTURE) : WorldSettings.GameType.CREATIVE) : WorldSettings.GameType.SURVIVAL;
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"survival", "creative", "adventure"}) : (par2ArrayOfStr.length == 2 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, getListOfPlayerUsernames()) : null);
    }

    /**
     * Returns String array containing all player usernames in the server.
     */
    protected String[] getListOfPlayerUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username
     * parameter.
     */
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 1;
    }
}
