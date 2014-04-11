package mx.x10.afffsdd.vanillaanticheat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mx.x10.afffsdd.vanillaanticheat.module.IVacModule;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class VACUtils
{
    public static final String VACVersion = "1.0.5";
    
    /**
     * Tell all admins a message and format it.
     * 
     * @param message
     *            the message to tell the admins
     */
    public static void notifyAdmins(String message)
    {
        Iterator it = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();

        while (it.hasNext())
        {
            EntityPlayerMP player = (EntityPlayerMP)it.next();

            if (MinecraftServer.isPlayerOpped(player))
            {
                ChatComponentText chatComponent = new ChatComponentText("[VAC]: " + message);
                chatComponent.getChatStyle().setColor(EnumChatFormatting.BLUE);
                player.addChatMessage(chatComponent);
            }
        }
    }

    /**
     * Notify all admins of a message from a specific VAC module.
     * 
     * @param module
     *            the VAC module the message is from
     * @param message
     *            the message to tell the admins
     */
    public static void notifyAdmins(IVacModule module, String message)
    {
        notifyAdmins(module.getModuleName() + ": " + message);
    }

    /**
     * Notify all admins of a message from a specific VAC module and log it.
     * 
     * @param module
     *            the VAC module the message is from
     * @param message
     *            the message to tell the admins
     */
    public static void notifyAndLog(IVacModule module, String message)
    {
        notifyAdmins(module, message);
        MinecraftServer.getServer().logVAC(module.getModuleName() + ": " + message);
    }

    /**
     * Notify all admins of a message and log it.
     * 
     * @param message
     *            the message to tell the admins
     */
    public static void notifyAndLog(String message)
    {
        notifyAdmins(message);
        MinecraftServer.getServer().logVAC(message);
    }
    
    public static final List<Class> loggedItems = Arrays.asList(new Class[] { ItemBed.class, ItemBlock.class, ItemBucket.class, 
                                                ItemDoor.class, ItemFirework.class, ItemFireworkCharge.class, 
                                                ItemFlintAndSteel.class, ItemRedstone.class, ItemReed.class,
                                                ItemSeeds.class, ItemSign.class }); 
}
