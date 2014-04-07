package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses
{
    EntityLiving entityObj;

    /** Cache of entities which we can see */
    List seenEntities = new ArrayList();

    /** Cache of entities which we cannot see */
    List unseenEntities = new ArrayList();
    private static final String __OBFID = "CL_00001628";

    public EntitySenses(EntityLiving par1EntityLiving)
    {
        entityObj = par1EntityLiving;
    }

    /**
     * Clears canSeeCachePositive and canSeeCacheNegative.
     */
    public void clearSensingCache()
    {
        seenEntities.clear();
        unseenEntities.clear();
    }

    /**
     * Checks, whether 'our' entity can see the entity given as argument (true)
     * or not (false), caching the result.
     */
    public boolean canSee(Entity par1Entity)
    {
        if (seenEntities.contains(par1Entity))
        {
            return true;
        }
        else if (unseenEntities.contains(par1Entity))
        {
            return false;
        }
        else
        {
            entityObj.worldObj.theProfiler.startSection("canSee");
            boolean var2 = entityObj.canEntityBeSeen(par1Entity);
            entityObj.worldObj.theProfiler.endSection();

            if (var2)
            {
                seenEntities.add(par1Entity);
            }
            else
            {
                unseenEntities.add(par1Entity);
            }

            return var2;
        }
    }
}
