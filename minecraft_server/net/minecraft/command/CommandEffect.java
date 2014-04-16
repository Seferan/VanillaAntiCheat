package net.minecraft.command;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase
{
    private static final String __OBFID = "CL_00000323";

    public String getCommandName()
    {
        return "effect";
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
        return "commands.effect.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.effect.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP player = getPlayer(sender, args[0]);

            if (args[1].equals("clear"))
            {
                if (player.getActivePotionEffects().isEmpty()) { throw new CommandException("commands.effect.failure.notActive.all", new Object[] {player.getUsername()}); }

                player.clearActivePotions();
                notifyAdmins(sender, "commands.effect.success.removed.all", new Object[] {player.getUsername()});
            }
            else
            {
                int effectId = parseIntWithMin(sender, args[1], 1);
                int var5 = 600;
                int effectLength = 30;
                int effectAmplifier = 0;

                if (effectId < 0 || effectId >= Potion.potionTypes.length || Potion.potionTypes[effectId] == null) { throw new NumberInvalidException("commands.effect.notFound", new Object[] {Integer.valueOf(effectId)}); }

                if (args.length >= 3)
                {
                    effectLength = parseIntBounded(sender, args[2], 0, 1000000);

                    if (Potion.potionTypes[effectId].isInstant())
                    {
                        var5 = effectLength;
                    }
                    else
                    {
                        var5 = effectLength * 20;
                    }
                }
                else if (Potion.potionTypes[effectId].isInstant())
                {
                    var5 = 1;
                }

                if (args.length >= 4)
                {
                    effectAmplifier = parseIntBounded(sender, args[3], 0, 255);
                }
                
                if (effectLength == 0)
                {
                    if (!player.isPotionActive(effectId)) { throw new CommandException("commands.effect.failure.notActive", new Object[] {new ChatComponentTranslation(Potion.potionTypes[effectId].getName(), new Object[0]), player.getUsername()}); }

                    player.removePotionEffect(effectId);
                    notifyAdmins(sender, "commands.effect.success.removed", new Object[] {new ChatComponentTranslation(Potion.potionTypes[effectId].getName(), new Object[0]), player.getUsername()});
                }
                else
                {
                    PotionEffect effect = new PotionEffect(effectId, var5, effectAmplifier);
                    if (isTargetNonOp(player, sender))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Tried to give ");
                        sb.append((new ChatComponentTranslation(effect.getEffectName())).getUnformattedText());
                        sb.append(" (ID ");
                        sb.append(effectId);
                        sb.append(") * ");
                        sb.append(effectAmplifier);
                        sb.append(" to ");
                        sb.append(player.getUsername());
                        sb.append(" for ");
                        sb.append(effectLength);
                        sb.append(" seconds!");
                        notifyAdmins(sender, sb.toString());
                    }
                    else
                    {
                        player.addPotionEffect(effect);
                        notifyAdmins(sender, "commands.effect.success", new Object[] {new ChatComponentTranslation(effect.getEffectName(), new Object[0]), Integer.valueOf(effectId), Integer.valueOf(effectAmplifier), player.getUsername(), Integer.valueOf(effectLength)});
                    }
                }
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, getAllUsernames()) : null;
    }

    protected String[] getAllUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
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
