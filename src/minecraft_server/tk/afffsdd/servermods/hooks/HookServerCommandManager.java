package tk.afffsdd.servermods.hooks;

import net.minecraft.src.CommandChangelog;
import net.minecraft.src.CommandSuicide;
import net.minecraft.src.CommandVersion;
import net.minecraft.src.ServerCommandManager;

public class HookServerCommandManager extends ServerCommandManager
{
	public HookServerCommandManager()
	{
		super();
	    this.registerCommand(new CommandSuicide());
	    this.registerCommand(new CommandVersion());
	    this.registerCommand(new CommandChangelog());
	}
}
