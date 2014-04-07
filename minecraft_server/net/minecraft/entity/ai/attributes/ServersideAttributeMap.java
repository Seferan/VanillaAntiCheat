package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.management.LowerStringMap;

import com.google.common.collect.Sets;

public class ServersideAttributeMap extends BaseAttributeMap
{
    private final Set attributeInstanceSet = Sets.newHashSet();
    protected final Map descriptionToAttributeInstanceMap = new LowerStringMap();
    private static final String __OBFID = "CL_00001569";

    public ModifiableAttributeInstance getAttributeInstance(IAttribute par1Attribute)
    {
        return (ModifiableAttributeInstance)super.getAttributeInstance(par1Attribute);
    }

    public ModifiableAttributeInstance getAttributeInstanceByName(String par1Str)
    {
        IAttributeInstance var2 = super.getAttributeInstanceByName(par1Str);

        if (var2 == null)
        {
            var2 = (IAttributeInstance)descriptionToAttributeInstanceMap.get(par1Str);
        }

        return (ModifiableAttributeInstance)var2;
    }

    /**
     * Registers an attribute with this AttributeMap, returns a modifiable
     * AttributeInstance associated with this map
     */
    public IAttributeInstance registerAttribute(IAttribute par1Attribute)
    {
        if (attributesByName.containsKey(par1Attribute.getAttributeUnlocalizedName()))
        {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        else
        {
            ModifiableAttributeInstance var2 = new ModifiableAttributeInstance(this, par1Attribute);
            attributesByName.put(par1Attribute.getAttributeUnlocalizedName(), var2);

            if (par1Attribute instanceof RangedAttribute && ((RangedAttribute)par1Attribute).getDescription() != null)
            {
                descriptionToAttributeInstanceMap.put(((RangedAttribute)par1Attribute).getDescription(), var2);
            }

            attributes.put(par1Attribute, var2);
            return var2;
        }
    }

    public void addAttributeInstance(ModifiableAttributeInstance par1ModifiableAttributeInstance)
    {
        if (par1ModifiableAttributeInstance.getAttribute().getShouldWatch())
        {
            attributeInstanceSet.add(par1ModifiableAttributeInstance);
        }
    }

    public Set getAttributeInstanceSet()
    {
        return attributeInstanceSet;
    }

    public Collection getWatchedAttributes()
    {
        HashSet var1 = Sets.newHashSet();
        Iterator var2 = getAllAttributes().iterator();

        while (var2.hasNext())
        {
            IAttributeInstance var3 = (IAttributeInstance)var2.next();

            if (var3.getAttribute().getShouldWatch())
            {
                var1.add(var3);
            }
        }

        return var1;
    }
}
