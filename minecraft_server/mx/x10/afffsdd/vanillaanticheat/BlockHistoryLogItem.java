package mx.x10.afffsdd.vanillaanticheat;

import java.util.Date;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;

public class BlockHistoryLogItem
{
    public Item item;
    public EntityPlayerMP player;
    public int state;
    public int x;
    public int y;
    public int z;
    
    public BlockHistoryLogItem(Item item, EntityPlayerMP player, int state, int x, int y, int z)
    {
        this.item = item;
        this.player = player;
        this.state = state;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public String toString()
    {
        if (item == null) return null;
        if (player == null) return null;
        
        StringBuilder line = new StringBuilder();
        line.append(new Date());
        line.append(" ");
        line.append(player.getUsername());
        line.append(" ");
        switch (state)
        {
        case 0:
            line.append("broke");
            break;
        case 1:
            line.append("placed");
            break;
        case 2:
            line.append("used");
            break;
        case 3:
            line.append("accessed");
            break;
        }
        line.append(" ");
        line.append(item.getUnlocalizedName());
        line.append(" ");
        line.append(x);
        line.append(" ");
        line.append(y);
        line.append(" ");
        line.append(z);
        return line.toString();
    }
}
