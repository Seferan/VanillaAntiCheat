package net.minecraft.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandItem extends CommandBase
{
    public String getCommandName()
    {
        return "item";
    }
    
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"i"});
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender commandSender)
    {
        return "/item <item> [amount]";
    }

    public void processCommand(ICommandSender commandSender, String[] args)
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("/item <item> [amount]", new Object[0]);
        }
        else
        {
            EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
            Item item = getItemByText(commandSender, args[0]);
            
            int amount = 1;
            if (args.length >= 2)
            {
                amount = parseIntBounded(commandSender, args[1], 1, 256);
            }

            ItemStack itemStack = new ItemStack(item, amount, 0);
            EntityItem entityItem = player.dropPlayerItemWithRandomChoice(itemStack, false);
            entityItem.delayBeforeCanPickup = 0;
            entityItem.setOwner(player.getUsername());
            notifyAdmins(commandSender, "commands.give.success", new Object[] {itemStack.getFormattedItemName(), Integer.valueOf(amount), player.getUsername()});
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args)
    {
        return args.length == 2 ? getListOfStringsFromIterableMatchingLastWord(args, Item.itemRegistry.getKeys()) : null;
    }
}
