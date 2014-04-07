package net.minecraft.server.management;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LowerStringMap implements Map
{
    private final Map internalMap = new LinkedHashMap();
    private static final String __OBFID = "CL_00001488";

    public int size()
    {
        return internalMap.size();
    }

    public boolean isEmpty()
    {
        return internalMap.isEmpty();
    }

    public boolean containsKey(Object par1Obj)
    {
        return internalMap.containsKey(par1Obj.toString().toLowerCase());
    }

    public boolean containsValue(Object par1Obj)
    {
        return internalMap.containsKey(par1Obj);
    }

    public Object get(Object par1Obj)
    {
        return internalMap.get(par1Obj.toString().toLowerCase());
    }

    public Object put(String par1Str, Object par2Obj)
    {
        return internalMap.put(par1Str.toLowerCase(), par2Obj);
    }

    public Object remove(Object par1Obj)
    {
        return internalMap.remove(par1Obj.toString().toLowerCase());
    }

    public void putAll(Map par1Map)
    {
        Iterator var2 = par1Map.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();
            this.put((String)var3.getKey(), var3.getValue());
        }
    }

    public void clear()
    {
        internalMap.clear();
    }

    public Set keySet()
    {
        return internalMap.keySet();
    }

    public Collection values()
    {
        return internalMap.values();
    }

    public Set entrySet()
    {
        return internalMap.entrySet();
    }

    public Object put(Object par1Obj, Object par2Obj)
    {
        return this.put((String)par1Obj, par2Obj);
    }
}
