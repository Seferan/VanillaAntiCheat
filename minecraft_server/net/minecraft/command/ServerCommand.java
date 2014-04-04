package net.minecraft.command;

public class ServerCommand
{
    /** The command string. */
    public final String command;
    public final ICommandSender sender;
    private static final String __OBFID = "CL_00001779";

    public ServerCommand(String par1Str, ICommandSender par2ICommandSender)
    {
        this.command = par1Str;
        this.sender = par2ICommandSender;
    }
}
