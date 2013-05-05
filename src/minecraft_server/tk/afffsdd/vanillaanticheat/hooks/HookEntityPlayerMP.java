package tk.afffsdd.vanillaanticheat.hooks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.World;

public class HookEntityPlayerMP extends EntityPlayerMP {

	private double mapEdge = 65000D;
	private int ticksSinceLastWarning = 0;
	
	public HookEntityPlayerMP(MinecraftServer par1MinecraftServer, World par2World, String par3Str, ItemInWorldManager par4ItemInWorldManager) {
		super(par1MinecraftServer, par2World, par3Str, par4ItemInWorldManager);
	}

    public void onUpdate()
    {
    	super.onUpdate();
    	if(Math.abs(posX) > mapEdge || Math.abs(posZ) > mapEdge)
    	{
    		if(ticksSinceLastWarning >= 10)
    		{
        		sendChatToPlayer(EnumChatFormatting.RED + "This is the edge of the map! Do not go further!");	
    		}
    		if(Math.abs(posX) > mapEdge)
    		{
    			setPositionAndUpdate(mapEdge, posY, posZ);
    		} else {
    			setPositionAndUpdate(posX, posY, mapEdge);
    		}
    		ticksSinceLastWarning = 0;
    	} else {
    		ticksSinceLastWarning++;
    	}
    }
}
