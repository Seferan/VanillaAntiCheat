package net.minecraft.util;

public class RegistryNamespacedDefaultedByKey extends RegistryNamespaced
{
    private final String field_148760_d;
    private Object field_148761_e;
    private static final String __OBFID = "CL_00001196";

    public RegistryNamespacedDefaultedByKey(String p_i45127_1_)
    {
        this.field_148760_d = p_i45127_1_;
    }

    /**
     * Adds a new object to this registry, keyed by both the given integer ID
     * and the given string.
     */
    public void addObject(int p_148756_1_, String p_148756_2_, Object p_148756_3_)
    {
        if (this.field_148760_d.equals(p_148756_2_))
        {
            this.field_148761_e = p_148756_3_;
        }

        super.addObject(p_148756_1_, p_148756_2_, p_148756_3_);
    }

    public Object getObject(String p_148751_1_)
    {
        Object var2 = super.getObject(p_148751_1_);
        return var2 == null ? this.field_148761_e : var2;
    }

    /**
     * Gets the object identified by the given ID.
     */
    public Object getObjectForID(int p_148754_1_)
    {
        Object var2 = super.getObjectForID(p_148754_1_);
        return var2 == null ? this.field_148761_e : var2;
    }

    public Object getObject(Object par1Obj)
    {
        return this.getObject((String)par1Obj);
    }
}
