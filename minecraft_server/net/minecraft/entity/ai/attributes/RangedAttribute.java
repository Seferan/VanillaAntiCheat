package net.minecraft.entity.ai.attributes;

public class RangedAttribute extends BaseAttribute
{
    private final double minimumValue;
    private final double maximumValue;
    private String description;
    private static final String __OBFID = "CL_00001568";

    public RangedAttribute(String par1Str, double par2, double par4, double par6)
    {
        super(par1Str, par2);
        minimumValue = par4;
        maximumValue = par6;

        if (par4 > par6)
        {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        else if (par2 < par4)
        {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        else if (par2 > par6) { throw new IllegalArgumentException("Default value cannot be bigger than maximum value!"); }
    }

    public RangedAttribute setDescription(String par1Str)
    {
        description = par1Str;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public double clampValue(double par1)
    {
        if (par1 < minimumValue)
        {
            par1 = minimumValue;
        }

        if (par1 > maximumValue)
        {
            par1 = maximumValue;
        }

        return par1;
    }
}
