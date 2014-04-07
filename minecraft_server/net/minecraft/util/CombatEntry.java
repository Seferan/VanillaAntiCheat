package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;

public class CombatEntry
{
    private final DamageSource damageSrc;
    private final int field_94567_b;
    private final float field_94568_c;
    private final float field_94565_d;
    private final String field_94566_e;
    private final float field_94564_f;
    private static final String __OBFID = "CL_00001519";

    public CombatEntry(DamageSource par1DamageSource, int par2, float par3, float par4, String par5Str, float par6)
    {
        damageSrc = par1DamageSource;
        field_94567_b = par2;
        field_94568_c = par4;
        field_94565_d = par3;
        field_94566_e = par5Str;
        field_94564_f = par6;
    }

    /**
     * Get the DamageSource of the CombatEntry instance.
     */
    public DamageSource getDamageSrc()
    {
        return damageSrc;
    }

    public float func_94563_c()
    {
        return field_94568_c;
    }

    public boolean func_94559_f()
    {
        return damageSrc.getEntity() instanceof EntityLivingBase;
    }

    public String func_94562_g()
    {
        return field_94566_e;
    }

    public IChatComponent func_151522_h()
    {
        return getDamageSrc().getEntity() == null ? null : getDamageSrc().getEntity().getUsernameAsIChatComponent();
    }

    public float func_94561_i()
    {
        return damageSrc == DamageSource.outOfWorld ? Float.MAX_VALUE : field_94564_f;
    }
}
