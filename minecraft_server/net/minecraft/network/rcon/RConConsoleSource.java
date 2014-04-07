package net.minecraft.network.rcon;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender
{
    /** Single instance of RConConsoleSource */
    public static final RConConsoleSource instance = new RConConsoleSource();

    /** RCon string buffer for log. */
    private StringBuffer buffer = new StringBuffer();
    private static final String __OBFID = "CL_00001800";

    /**
     * Clears the RCon log
     */
    public void resetLog()
    {
        buffer.setLength(0);
    }

    /**
     * Gets the contents of the RCon log
     */
    public String getLogContents()
    {
        return buffer.toString();
    }

    /**
     * Gets the name of this command sender (usually username, but possibly
     * "Rcon")
     */
    public String getUsername()
    {
        return "Rcon";
    }

    public IChatComponent getUsernameAsIChatComponent()
    {
        return new ChatComponentText(getUsername());
    }

    /**
     * Notifies this sender of some sort of information. This is for messages
     * intended to display to the user. Used for typical output (like
     * "you asked for whether or not this game rule is set, so here's your answer"
     * ), warnings (like "I fetched this block for you by ID, but I'd like you
     * to know that every time you do this, I die a little
     * inside"), and errors (like "it's not called iron_pixacke, silly").
     */
    public void addChatMessage(IChatComponent var1)
    {
        buffer.append(var1.getUnformattedText());
    }

    public void addChatMessage(String message)
    {
        addChatMessage(new ChatComponentText(message));
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean canCommandSenderUseCommand(int par1, String par2Str)
    {
        return true;
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates getCommandSenderPosition()
    {
        return new ChunkCoordinates(0, 0, 0);
    }

    public World getEntityWorld()
    {
        return MinecraftServer.getServer().getEntityWorld();
    }
}
