package net.minecraft.entity.passive;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityMooshroom extends EntityCow
{
    private static final String __OBFID = "CL_00001645";

    public EntityMooshroom(World par1World)
    {
        super(par1World);
        setSize(0.9F, 1.3F);
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.getItem() == Items.bowl && getGrowingAge() >= 0)
        {
            if (var2.stackSize == 1)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, new ItemStack(Items.mushroom_stew));
                return true;
            }

            if (par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.mushroom_stew)) && !par1EntityPlayer.capabilities.isCreativeMode)
            {
                par1EntityPlayer.inventory.decrStackSize(par1EntityPlayer.inventory.currentItem, 1);
                return true;
            }
        }

        if (var2 != null && var2.getItem() == Items.shears && getGrowingAge() >= 0)
        {
            setDead();
            worldObj.spawnParticle("largeexplode", posX, posY + height / 2.0F, posZ, 0.0D, 0.0D, 0.0D);

            if (!worldObj.isClient)
            {
                EntityCow var3 = new EntityCow(worldObj);
                var3.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
                var3.setHealth(getHealth());
                var3.renderYawOffset = renderYawOffset;
                worldObj.spawnEntityInWorld(var3);

                for (int var4 = 0; var4 < 5; ++var4)
                {
                    worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY + height, posZ, new ItemStack(Blocks.red_mushroom)));
                }

                var2.damageItem(1, par1EntityPlayer);
                playSound("mob.sheep.shear", 1.0F, 1.0F);
            }

            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }

    public EntityMooshroom createChild(EntityAgeable par1EntityAgeable)
    {
        return new EntityMooshroom(worldObj);
    }
}
