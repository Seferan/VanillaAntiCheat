package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class CommandReload extends CommandBase
{
    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return MinecraftServer.isPlayerOwner(par1ICommandSender);
    }
    
    public String getCommandName()
    {
        return "reload";
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
        return "/reload";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        DedicatedPlayerList dedicatedPlayerList = (DedicatedPlayerList)MinecraftServer.getServer().getConfigurationManager();
        DedicatedServer server = (DedicatedServer)MinecraftServer.getServer();
        
        server.loadProperties();
        MinecraftServer.getServer().getConfigurationManager().loadWhiteList();
        dedicatedPlayerList.loadOpsList();
        dedicatedPlayerList.loadOwnersList();
        dedicatedPlayerList.readMotd();
        dedicatedPlayerList.loadProxyCache();
        notifyAdmins(par1ICommandSender, "Reloaded the server configuration (server.properties, OPs, Owners, MOTD, proxy check cache)");
    }
}
