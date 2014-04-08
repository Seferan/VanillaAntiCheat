package net.minecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class CommandButcher extends CommandBase
{
    public static final Class[] entitiesToRemove = new Class[] { EntityItem.class, 
                                                                 EntityArrow.class, 
                                                                 EntityBoat.class, 
                                                                 EntityXPOrb.class,
                                                                 EntityEnderCrystal.class,
                                                                 EntityLiving.class };
    
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"clearmobs"});
    }
    
    public String getCommandName()
    {
        return "butcher";
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
        return "/butcher";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        List<Entity> toRemove = new ArrayList<Entity>();
        for (Entity e : (List<Entity>)par1ICommandSender.getEntityWorld().loadedEntityList)
        {
            if (e instanceof EntityPlayer) continue;
            if (e instanceof EntityTameable && ((EntityTameable)e).isTamed()) continue;
            if (e instanceof EntityAgeable) continue;
            
            for (Class clazz : entitiesToRemove)
            {
                if (clazz.isInstance(e)) toRemove.add(e);
            }
            
        }
        for (Entity e : toRemove)
        {
            e.setDead();
        }
        notifyAdmins(par1ICommandSender, "Killed " + toRemove.size() + " entities");
    }
}
