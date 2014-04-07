package net.minecraft.entity.ai.attributes;

import java.util.UUID;

import org.apache.commons.lang3.Validate;

public class AttributeModifier
{
    private final double amount;
    private final int operation;
    private final String name;
    private final UUID id;

    /**
     * If false, this modifier is not saved in NBT. Used for "natural" modifiers
     * like speed boost from sprinting
     */
    private boolean isSaved;
    private static final String __OBFID = "CL_00001564";

    public AttributeModifier(String par1Str, double par2, int par4)
    {
        this(UUID.randomUUID(), par1Str, par2, par4);
    }

    public AttributeModifier(UUID par1UUID, String par2Str, double par3, int par5)
    {
        isSaved = true;
        id = par1UUID;
        name = par2Str;
        amount = par3;
        operation = par5;
        Validate.notEmpty(par2Str, "Modifier name cannot be empty", new Object[0]);
        Validate.inclusiveBetween(Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(par5), "Invalid operation", new Object[0]);
    }

    public UUID getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getOperation()
    {
        return operation;
    }

    public double getAmount()
    {
        return amount;
    }

    /**
     * @see #isSaved
     */
    public boolean isSaved()
    {
        return isSaved;
    }

    /**
     * @see #isSaved
     */
    public AttributeModifier setSaved(boolean par1)
    {
        isSaved = par1;
        return this;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            AttributeModifier var2 = (AttributeModifier)par1Obj;

            if (id != null)
            {
                if (!id.equals(var2.id)) { return false; }
            }
            else if (var2.id != null) { return false; }

            return true;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public String toString()
    {
        return "AttributeModifier{amount=" + amount + ", operation=" + operation + ", name=\'" + name + '\'' + ", id=" + id + ", serialize=" + isSaved + '}';
    }
}
