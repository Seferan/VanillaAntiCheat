package net.minecraft.util;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonSerializableSet extends ForwardingSet implements IJsonSerializable
{
    /** The set for this ForwardingSet to forward methods to. */
    private final Set underlyingSet = Sets.newHashSet();
    private static final String __OBFID = "CL_00001482";

    /**
     * Gets the JsonElement that can be serialized.
     */
    public JsonElement getSerializableElement()
    {
        JsonArray var1 = new JsonArray();
        Iterator var2 = iterator();

        while (var2.hasNext())
        {
            String var3 = (String)var2.next();
            var1.add(new JsonPrimitive(var3));
        }

        return var1;
    }

    protected Set delegate()
    {
        return underlyingSet;
    }
}
