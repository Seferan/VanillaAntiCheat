package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandToggleDownfall extends CommandBase
{
    private static final String __OBFID = "CL_00001184";

    public String getCommandName()
    {
        return "toggledownfall";
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
        return "commands.downfall.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (MinecraftServer.isPlayerOwner(par1ICommandSender))
        {
            this.toggleDownfall();
            notifyAdmins(par1ICommandSender, "commands.downfall.success", new Object[0]);
        }
        else
        {
            notifyAdmins(par1ICommandSender, "Tried to use /toggledownfall!");
        }
    }

    /**
     * Toggle rain and enable thundering.
     */
    protected void toggleDownfall()
    {
        WorldInfo var1 = MinecraftServer.getServer().worldServers[0].getWorldInfo();
        var1.setRaining(!var1.isRaining());
    }
}
