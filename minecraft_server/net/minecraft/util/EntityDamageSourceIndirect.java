package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource
{
    private Entity indirectEntity;
    private static final String __OBFID = "CL_00001523";

    public EntityDamageSourceIndirect(String par1Str, Entity par2Entity,
            Entity par3Entity)
    {
        super(par1Str, par2Entity);
        this.indirectEntity = par3Entity;
    }

    public Entity getSourceOfDamage()
    {
        return this.damageSourceEntity;
    }

    public Entity getEntity()
    {
        return this.indirectEntity;
    }

    public IChatComponent func_151519_b(EntityLivingBase p_151519_1_)
    {
        IChatComponent var2 = this.indirectEntity == null ? this.damageSourceEntity
                .getUsernameAsIChatComponent() : this.indirectEntity
                .getUsernameAsIChatComponent();
        ItemStack var3 = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity)
                .getHeldItem() : null;
        String var4 = "death.attack." + this.damageType;
        String var5 = var4 + ".item";
        return var3 != null && var3.hasDisplayName()
                && StatCollector.canTranslate(var5) ? new ChatComponentTranslation(
                var5, new Object[] {p_151519_1_.getUsernameAsIChatComponent(),
                        var2, var3.func_151000_E()})
                : new ChatComponentTranslation(var4, new Object[] {
                        p_151519_1_.getUsernameAsIChatComponent(), var2});
    }
}
