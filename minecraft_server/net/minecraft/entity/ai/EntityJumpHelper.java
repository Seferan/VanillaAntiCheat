package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityJumpHelper
{
    private EntityLiving entity;
    private boolean isJumping;
    private static final String __OBFID = "CL_00001571";

    public EntityJumpHelper(EntityLiving par1EntityLiving)
    {
        entity = par1EntityLiving;
    }

    public void setJumping()
    {
        isJumping = true;
    }

    /**
     * Called to actually make the entity jump if isJumping is true.
     */
    public void doJump()
    {
        entity.setJumping(isJumping);
        isJumping = false;
    }
}
