package tk.afffsdd.vanillaanticheat.hooks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NetServerHandler;

public class HookNetServerHandler extends NetServerHandler
{
	public HookNetServerHandler(MinecraftServer par1, INetworkManager par2, EntityPlayerMP par3)
	{
		super(par1, par2, par3);
	}
	
}
