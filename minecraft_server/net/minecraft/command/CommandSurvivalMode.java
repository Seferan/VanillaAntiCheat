package net.minecraft.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings.GameType;

public class CommandSurvivalMode extends CommandBase
{
    private static final String __OBFID = "CL_00000448";

    public String getCommandName()
    {
        return "survival";
    }

    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"gm0"});
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
        return "/survival";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        GameType gameType = GameType.SURVIVAL;
        EntityPlayerMP player = getCommandSenderAsPlayer(par1ICommandSender);
        ChatComponentTranslation var5 = new ChatComponentTranslation("gameMode." + gameType.getName(), new Object[0]);
        player.setGameType(gameType);
        player.fallDistance = 0.0F;
        notifyAdmins(par1ICommandSender, 1, "commands.gamemode.success.self", new Object[] {var5});
    }
}
