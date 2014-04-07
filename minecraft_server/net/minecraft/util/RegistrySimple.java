package net.minecraft.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

public class RegistrySimple implements IRegistry
{
    private static final Logger logger = LogManager.getLogger();

    /** Objects registered on this registry. */
    protected final Map registryObjects = createUnderlyingMap();
    private static final String __OBFID = "CL_00001210";

    /**
     * Creates the Map we will use to map keys to their registered values.
     */
    protected Map createUnderlyingMap()
    {
        return Maps.newHashMap();
    }

    public Object getObject(Object par1Obj)
    {
        return registryObjects.get(par1Obj);
    }

    /**
     * Register an object on this registry.
     */
    public void putObject(Object par1Obj, Object par2Obj)
    {
        if (registryObjects.containsKey(par1Obj))
        {
            logger.warn("Adding duplicate key \'" + par1Obj + "\' to registry");
        }

        registryObjects.put(par1Obj, par2Obj);
    }

    /**
     * Gets all the keys recognized by this registry.
     */
    public Set getKeys()
    {
        return Collections.unmodifiableSet(registryObjects.keySet());
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    public boolean containsKey(Object p_148741_1_)
    {
        return registryObjects.containsKey(p_148741_1_);
    }
}
