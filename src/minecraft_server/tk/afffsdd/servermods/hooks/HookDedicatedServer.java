package tk.afffsdd.servermods.hooks;

import java.io.File;
import java.io.IOException;

import net.minecraft.src.DedicatedServer;

public class HookDedicatedServer extends DedicatedServer
{
    public HookDedicatedServer(File par1File)
    {
        super(par1File);
    }
    
    protected boolean startServer() throws IOException
    {
    	boolean var1 = super.startServer();
        this.setGreentextRestrictionLevel(this.getIntProperty("greentexting-level", 1));
        this.setAllCharactersRestrictionLevel(this.getIntProperty("all-characters-level", 1));
        return var1;
    }
}
