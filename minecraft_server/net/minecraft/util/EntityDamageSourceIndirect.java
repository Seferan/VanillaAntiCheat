package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource
{
    private Entity indirectEntity;
    private static final String __OBFID = "CL_00001523";

    public EntityDamageSourceIndirect(String par1Str, Entity par2Entity, Entity par3Entity)
    {
        super(par1Str, par2Entity);
        indirectEntity = par3Entity;
    }

    public Entity getSourceOfDamage()
    {
        return damageSourceEntity;
    }

    public Entity getEntity()
    {
        return indirectEntity;
    }

    public IChatComponent func_151519_b(EntityLivingBase p_151519_1_)
    {
        IChatComponent var2 = indirectEntity == null ? damageSourceEntity.getUsernameAsIChatComponent() : indirectEntity.getUsernameAsIChatComponent();
        ItemStack var3 = indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)indirectEntity).getHeldItem() : null;
        String var4 = "death.attack." + damageType;
        String var5 = var4 + ".item";
        return var3 != null && var3.hasDisplayName() && StatCollector.canTranslate(var5) ? new ChatComponentTranslation(var5, new Object[] {p_151519_1_.getUsernameAsIChatComponent(), var2, var3.func_151000_E()}) : new ChatComponentTranslation(var4, new Object[] {p_151519_1_.getUsernameAsIChatComponent(), var2});
    }
}
