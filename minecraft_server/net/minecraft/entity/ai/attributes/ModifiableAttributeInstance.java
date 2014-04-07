package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;

public class ModifiableAttributeInstance implements IAttributeInstance
{
    /** The BaseAttributeMap this attributeInstance can be found in */
    private final BaseAttributeMap attributeMap;

    /** The Attribute this is an instance of */
    private final IAttribute genericAttribute;
    private final Map mapByOperation = Maps.newHashMap();
    private final Map mapByName = Maps.newHashMap();
    private final Map mapByUUID = Maps.newHashMap();
    private double baseValue;
    private boolean needsUpdate = true;
    private double cachedValue;
    private static final String __OBFID = "CL_00001567";

    public ModifiableAttributeInstance(BaseAttributeMap par1BaseAttributeMap, IAttribute par2Attribute)
    {
        attributeMap = par1BaseAttributeMap;
        genericAttribute = par2Attribute;
        baseValue = par2Attribute.getDefaultValue();

        for (int var3 = 0; var3 < 3; ++var3)
        {
            mapByOperation.put(Integer.valueOf(var3), new HashSet());
        }
    }

    /**
     * Get the Attribute this is an instance of
     */
    public IAttribute getAttribute()
    {
        return genericAttribute;
    }

    public double getBaseValue()
    {
        return baseValue;
    }

    public void setBaseValue(double par1)
    {
        if (par1 != getBaseValue())
        {
            baseValue = par1;
            flagForUpdate();
        }
    }

    public Collection getModifiersByOperation(int par1)
    {
        return (Collection)mapByOperation.get(Integer.valueOf(par1));
    }

    public Collection func_111122_c()
    {
        HashSet var1 = new HashSet();

        for (int var2 = 0; var2 < 3; ++var2)
        {
            var1.addAll(getModifiersByOperation(var2));
        }

        return var1;
    }

    /**
     * Returns attribute modifier, if any, by the given UUID
     */
    public AttributeModifier getModifier(UUID par1UUID)
    {
        return (AttributeModifier)mapByUUID.get(par1UUID);
    }

    public void applyModifier(AttributeModifier par1AttributeModifier)
    {
        if (getModifier(par1AttributeModifier.getID()) != null)
        {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        else
        {
            Object var2 = mapByName.get(par1AttributeModifier.getName());

            if (var2 == null)
            {
                var2 = new HashSet();
                mapByName.put(par1AttributeModifier.getName(), var2);
            }

            ((Set)mapByOperation.get(Integer.valueOf(par1AttributeModifier.getOperation()))).add(par1AttributeModifier);
            ((Set)var2).add(par1AttributeModifier);
            mapByUUID.put(par1AttributeModifier.getID(), par1AttributeModifier);
            flagForUpdate();
        }
    }

    private void flagForUpdate()
    {
        needsUpdate = true;
        attributeMap.addAttributeInstance(this);
    }

    public void removeModifier(AttributeModifier par1AttributeModifier)
    {
        for (int var2 = 0; var2 < 3; ++var2)
        {
            Set var3 = (Set)mapByOperation.get(Integer.valueOf(var2));
            var3.remove(par1AttributeModifier);
        }

        Set var4 = (Set)mapByName.get(par1AttributeModifier.getName());

        if (var4 != null)
        {
            var4.remove(par1AttributeModifier);

            if (var4.isEmpty())
            {
                mapByName.remove(par1AttributeModifier.getName());
            }
        }

        mapByUUID.remove(par1AttributeModifier.getID());
        flagForUpdate();
    }

    public double getAttributeValue()
    {
        if (needsUpdate)
        {
            cachedValue = computeValue();
            needsUpdate = false;
        }

        return cachedValue;
    }

    private double computeValue()
    {
        double var1 = getBaseValue();
        AttributeModifier var4;

        for (Iterator var3 = getModifiersByOperation(0).iterator(); var3.hasNext(); var1 += var4.getAmount())
        {
            var4 = (AttributeModifier)var3.next();
        }

        double var7 = var1;
        Iterator var5;
        AttributeModifier var6;

        for (var5 = getModifiersByOperation(1).iterator(); var5.hasNext(); var7 += var1 * var6.getAmount())
        {
            var6 = (AttributeModifier)var5.next();
        }

        for (var5 = getModifiersByOperation(2).iterator(); var5.hasNext(); var7 *= 1.0D + var6.getAmount())
        {
            var6 = (AttributeModifier)var5.next();
        }

        return genericAttribute.clampValue(var7);
    }
}
