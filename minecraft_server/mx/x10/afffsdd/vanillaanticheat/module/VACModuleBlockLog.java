package mx.x10.afffsdd.vanillaanticheat.module;

import mx.x10.afffsdd.vanillaanticheat.BlockHistoryLogItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;

public class VACModuleBlockLog implements IVacModule
{   
    /**
     * The previous message logged.
     */
    public String previousMessage;
    /**
     * The number of ticks out of the second.
     */
    public int ticksInSecond;
    /**
     * The number of messages sent during this second.
     */
    public int messagesInSecond;
    
    public VACModuleBlockLog()
    {
        previousMessage = "";
        ticksInSecond = 0;
        messagesInSecond = 0;
    }
    
    public String getModuleName()
    {
        return "Block Log";
    }
    
    public void log(Block block, EntityPlayerMP player, int state, int x, int y, int z)
    {
        log(block.getItem(), player, state, x, y, z);
    }
    
    public void log(Item item, EntityPlayerMP player, int state, int x, int y, int z)
    {
        log(new BlockHistoryLogItem(item, player, state, x, y, z));
    }
    
    public void log(BlockHistoryLogItem logItem)
    {
        String line = logItem.toString();
        if (line != null && !previousMessage.equals(line))
        {
            if (messagesInSecond++ <= MinecraftServer.getServer().getBlockLogMaxLogsPerSecond())
            {
                MinecraftServer.getServer().getConfigurationManager().addBlockHistory(logItem);
                previousMessage = line;   
            }
        }
    }

    public void updateState()
    {
        if (ticksInSecond++ == 20)
        {
            ticksInSecond = 0;
            messagesInSecond = 0;
        }
    }
}
