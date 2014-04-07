package net.minecraft.command;

import java.util.Iterator;

import net.minecraft.command.server.CommandAchievement;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.command.server.CommandBanPlayer;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandDeOp;
import net.minecraft.command.server.CommandDeOwner;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.command.server.CommandListBans;
import net.minecraft.command.server.CommandListPlayers;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.command.server.CommandMessageRaw;
import net.minecraft.command.server.CommandMotd;
import net.minecraft.command.server.CommandOp;
import net.minecraft.command.server.CommandOwner;
import net.minecraft.command.server.CommandPardonIp;
import net.minecraft.command.server.CommandPardonPlayer;
import net.minecraft.command.server.CommandPublishLocalServer;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.command.server.CommandSetDefaultSpawnpoint;
import net.minecraft.command.server.CommandStop;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.command.server.CommandTestFor;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraft.command.server.CommandWhitelist;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class ServerCommandManager extends CommandHandler implements IAdminCommand
{
    private static final String __OBFID = "CL_00000922";

    public ServerCommandManager()
    {
        registerCommand(new CommandTime());
        registerCommand(new CommandGameMode());
        registerCommand(new CommandDifficulty());
        registerCommand(new CommandDefaultGameMode());
        registerCommand(new CommandKill());
        registerCommand(new CommandToggleDownfall());
        registerCommand(new CommandWeather());
        registerCommand(new CommandXP());
        registerCommand(new CommandTeleport());
        registerCommand(new CommandGive());
        registerCommand(new CommandEffect());
        registerCommand(new CommandEnchant());
        registerCommand(new CommandEmote());
        registerCommand(new CommandShowSeed());
        registerCommand(new CommandHelp());
        registerCommand(new CommandDebug());
        registerCommand(new CommandMessage());
        registerCommand(new CommandBroadcast());
        registerCommand(new CommandSetSpawnpoint());
        registerCommand(new CommandSetDefaultSpawnpoint());
        registerCommand(new CommandGameRule());
        registerCommand(new CommandClearInventory());
        registerCommand(new CommandTestFor());
        registerCommand(new CommandSpreadPlayers());
        registerCommand(new CommandPlaySound());
        registerCommand(new CommandScoreboard());
        registerCommand(new CommandAchievement());
        registerCommand(new CommandSummon());
        registerCommand(new CommandSetBlock());
        registerCommand(new CommandTestForBlock());
        registerCommand(new CommandMessageRaw());
        registerCommand(new CommandMyIP());

        if (MinecraftServer.getServer().isDedicatedServer())
        {
            registerCommand(new CommandOp());
            registerCommand(new CommandDeOp());
            registerCommand(new CommandOwner());
            registerCommand(new CommandDeOwner());
            registerCommand(new CommandStop());
            registerCommand(new CommandSaveAll());
            registerCommand(new CommandSaveOff());
            registerCommand(new CommandSaveOn());
            registerCommand(new CommandBanIp());
            registerCommand(new CommandPardonIp());
            registerCommand(new CommandBanPlayer());
            registerCommand(new CommandListBans());
            registerCommand(new CommandPardonPlayer());
            registerCommand(new CommandServerKick());
            registerCommand(new CommandListPlayers());
            registerCommand(new CommandWhitelist());
            registerCommand(new CommandSetPlayerTimeout());
            registerCommand(new CommandMotd());
        }
        else
        {
            registerCommand(new CommandPublishLocalServer());
        }

        CommandBase.setAdminCommander(this);
    }

    /**
     * Sends a message to the admins of the server from a given CommandSender
     * with the given resource string and given extra srings. If the int par2 is
     * even or zero, the original sender is also notified.
     */
    public void notifyAdmins(ICommandSender par1ICommandSender, int par2, String par3Str, Object... par4ArrayOfObj)
    {
        boolean var5 = true;

        if (par1ICommandSender instanceof CommandBlockLogic && !MinecraftServer.getServer().worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput"))
        {
            var5 = false;
        }

        ChatComponentTranslation var6 = new ChatComponentTranslation("chat.type.admin", new Object[] {par1ICommandSender.getUsername(), new ChatComponentTranslation(par3Str, par4ArrayOfObj)});
        var6.getChatStyle().setColor(EnumChatFormatting.GRAY);
        var6.getChatStyle().setItalic(Boolean.valueOf(true));

        if (var5)
        {
            Iterator var7 = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();

            while (var7.hasNext())
            {
                EntityPlayerMP var8 = (EntityPlayerMP)var7.next();

                if (var8 != par1ICommandSender && MinecraftServer.isPlayerOpped(var8))
                {
                    var8.addChatMessage(var6);
                }
            }
        }

        if (par1ICommandSender != MinecraftServer.getServer())
        {
            MinecraftServer.getServer().addChatMessage(var6);
        }

        if ((par2 & 1) != 1)
        {
            par1ICommandSender.addChatMessage(new ChatComponentTranslation(par3Str, par4ArrayOfObj));
        }
    }
}
