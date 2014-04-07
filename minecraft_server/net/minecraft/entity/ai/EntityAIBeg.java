package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAIBeg extends EntityAIBase
{
    private EntityWolf theWolf;
    private EntityPlayer thePlayer;
    private World worldObject;
    private float minPlayerDistance;
    private int field_75384_e;
    private static final String __OBFID = "CL_00001576";

    public EntityAIBeg(EntityWolf par1EntityWolf, float par2)
    {
        theWolf = par1EntityWolf;
        worldObject = par1EntityWolf.worldObj;
        minPlayerDistance = par2;
        setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        thePlayer = worldObject.getClosestPlayerToEntity(theWolf, minPlayerDistance);
        return thePlayer == null ? false : hasPlayerGotBoneInHand(thePlayer);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !thePlayer.isEntityAlive() ? false : (theWolf.getDistanceSqToEntity(thePlayer) > minPlayerDistance * minPlayerDistance ? false : field_75384_e > 0 && hasPlayerGotBoneInHand(thePlayer));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        theWolf.func_70918_i(true);
        field_75384_e = 40 + theWolf.getRNG().nextInt(40);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        theWolf.func_70918_i(false);
        thePlayer = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        theWolf.getLookHelper().setLookPosition(thePlayer.posX, thePlayer.posY + thePlayer.getEyeHeight(), thePlayer.posZ, 10.0F, theWolf.getVerticalFaceSpeed());
        --field_75384_e;
    }

    /**
     * Gets if the Player has the Bone in the hand.
     */
    private boolean hasPlayerGotBoneInHand(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();
        return var2 == null ? false : (!theWolf.isTamed() && var2.getItem() == Items.bone ? true : theWolf.isBreedingItem(var2));
    }
}
