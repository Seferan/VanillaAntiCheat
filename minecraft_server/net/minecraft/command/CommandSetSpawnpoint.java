package net.minecraft.command;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandSetSpawnpoint extends CommandBase
{
    private static final String __OBFID = "CL_00001026";

    public String getCommandName()
    {
        return "spawnpoint";
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
        return "commands.spawnpoint.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        EntityPlayerMP var3;
        if (par2ArrayOfStr.length == 0)
        {
            var3 = getCommandSenderAsPlayer(par1ICommandSender);
        }
        else
        {
            if (par2ArrayOfStr[0].equals("!WORLD"))
            {
                var3 = null;
            }
            else
            {
                var3 = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
            }
        }
        
        if (var3 == null)
        {
            WorldServer worldServer = MinecraftServer.getServer().worldServers[0];
            WorldInfo worldInfo = worldServer.getWorldInfo();
            if (par2ArrayOfStr.length == 4)
            {
                byte var4 = 1;
                int var5 = 30000000;
                int var10 = var4 + 1;
                int x = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var4], -var5, var5);
                int y = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var10++], 0, 256);
                int z = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var10++], -var5, var5);
                worldInfo.setSpawnPosition(x, y, z);                
                notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {"World", Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z)});
            }
            else
            {
                if (par2ArrayOfStr.length > 1) { throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]); }

                ChunkCoordinates var11 = par1ICommandSender.getCommandSenderPosition();
                worldInfo.setSpawnPosition(var11.posX, var11.posY, var11.posZ);
                notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {"World", Integer.valueOf(var11.posX), Integer.valueOf(var11.posY), Integer.valueOf(var11.posZ)});
            }
        }
        else
        {
            if (par2ArrayOfStr.length == 4)
            {
                byte var4 = 1;
                int var5 = 30000000;
                int var10 = var4 + 1;
                int x = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var4], -var5, var5);
                int y = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var10++], 0, 256);
                int z = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var10++], -var5, var5);
                if (var3.worldObj != null)
                {
                    var3.setSpawnChunk(new ChunkCoordinates(x, y, z), true);
                    notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {var3.getUsername(), Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z)});
                }   
            }
            else
            {
                if (par2ArrayOfStr.length > 1) { throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]); }

                ChunkCoordinates var11 = var3.getCommandSenderPosition();
                var3.setSpawnChunk(var11, true);
                notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {var3.getUsername(), Integer.valueOf(var11.posX), Integer.valueOf(var11.posY), Integer.valueOf(var11.posZ)});
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length != 1 && par2ArrayOfStr.length != 2 ? null : getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username
     * parameter.
     */
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 0;
    }
}
