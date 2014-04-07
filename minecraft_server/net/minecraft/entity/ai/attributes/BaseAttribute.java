package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute
{
    private final String unlocalizedName;
    private final double defaultValue;
    private boolean shouldWatch;
    private static final String __OBFID = "CL_00001565";

    protected BaseAttribute(String par1Str, double par2)
    {
        unlocalizedName = par1Str;
        defaultValue = par2;

        if (par1Str == null) { throw new IllegalArgumentException("Name cannot be null!"); }
    }

    public String getAttributeUnlocalizedName()
    {
        return unlocalizedName;
    }

    public double getDefaultValue()
    {
        return defaultValue;
    }

    public boolean getShouldWatch()
    {
        return shouldWatch;
    }

    public BaseAttribute setShouldWatch(boolean par1)
    {
        shouldWatch = par1;
        return this;
    }

    public int hashCode()
    {
        return unlocalizedName.hashCode();
    }
}
