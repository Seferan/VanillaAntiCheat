package net.minecraft.command.server;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;

public class CommandSpawn extends CommandBase
{
    public String getCommandName()
    {
        return "spawn";
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
        return "/spawn";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(par1ICommandSender);
        ChunkCoordinates spawnPoint = player.getEntityWorld().getSpawnPoint();
        if (MinecraftServer.isPlayerOpped(par1ICommandSender))
        {
            player.setPositionAndUpdate(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ);
        }
        else
        {
            StringBuilder message = new StringBuilder();
            message.append("World's spawnpoint is at ");
            message.append(spawnPoint.posX);
            message.append(", ");
            message.append(spawnPoint.posY);
            message.append(", ");
            message.append(spawnPoint.posZ);
            player.addChatMessage(message.toString());
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
