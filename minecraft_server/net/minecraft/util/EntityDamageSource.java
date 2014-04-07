package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource
{
    protected Entity damageSourceEntity;
    private static final String __OBFID = "CL_00001522";

    public EntityDamageSource(String par1Str, Entity par2Entity)
    {
        super(par1Str);
        damageSourceEntity = par2Entity;
    }

    public Entity getEntity()
    {
        return damageSourceEntity;
    }

    public IChatComponent func_151519_b(EntityLivingBase p_151519_1_)
    {
        ItemStack var2 = damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)damageSourceEntity).getHeldItem() : null;
        String var3 = "death.attack." + damageType;
        String var4 = var3 + ".item";
        return var2 != null && var2.hasDisplayName() && StatCollector.canTranslate(var4) ? new ChatComponentTranslation(var4, new Object[] {p_151519_1_.getUsernameAsIChatComponent(), damageSourceEntity.getUsernameAsIChatComponent(), var2.func_151000_E()}) : new ChatComponentTranslation(var3, new Object[] {p_151519_1_.getUsernameAsIChatComponent(), damageSourceEntity.getUsernameAsIChatComponent()});
    }

    /**
     * Return whether this damage source will have its damage amount scaled
     * based on the current difficulty.
     */
    public boolean isDifficultyScaled()
    {
        return damageSourceEntity != null && damageSourceEntity instanceof EntityLivingBase && !(damageSourceEntity instanceof EntityPlayer);
    }
}
