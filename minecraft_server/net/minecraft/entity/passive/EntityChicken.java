package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityChicken extends EntityAnimal
{
    public float field_70886_e;
    public float destPos;
    public float field_70884_g;
    public float field_70888_h;
    public float field_70889_i = 1.0F;

    /** The time until the next egg is spawned. */
    public int timeUntilNextEgg;
    private static final String __OBFID = "CL_00001639";

    public EntityChicken(World par1World)
    {
        super(par1World);
        setSize(0.3F, 0.7F);
        timeUntilNextEgg = rand.nextInt(6000) + 6000;
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.4D));
        tasks.addTask(2, new EntityAIMate(this, 1.0D));
        tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds, false));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        tasks.addTask(5, new EntityAIWander(this, 1.0D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        field_70888_h = field_70886_e;
        field_70884_g = destPos;
        destPos = (float)(destPos + (onGround ? -1 : 4) * 0.3D);

        if (destPos < 0.0F)
        {
            destPos = 0.0F;
        }

        if (destPos > 1.0F)
        {
            destPos = 1.0F;
        }

        if (!onGround && field_70889_i < 1.0F)
        {
            field_70889_i = 1.0F;
        }

        field_70889_i = (float)(field_70889_i * 0.9D);

        if (!onGround && motionY < 0.0D)
        {
            motionY *= 0.6D;
        }

        field_70886_e += field_70889_i * 2.0F;

        if (!isChild() && !worldObj.isClient && --timeUntilNextEgg <= 0)
        {
            playSound("mob.chicken.plop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
            func_145779_a(Items.egg, 1);
            timeUntilNextEgg = rand.nextInt(6000) + 6000;
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.chicken.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.chicken.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.chicken.hurt";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("mob.chicken.step", 0.15F, 1.0F);
    }

    protected Item func_146068_u()
    {
        return Items.feather;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = rand.nextInt(3) + rand.nextInt(1 + par2);

        for (int var4 = 0; var4 < var3; ++var4)
        {
            func_145779_a(Items.feather, 1);
        }

        if (isBurning())
        {
            func_145779_a(Items.cooked_chicken, 1);
        }
        else
        {
            func_145779_a(Items.chicken, 1);
        }
    }

    public EntityChicken createChild(EntityAgeable par1EntityAgeable)
    {
        return new EntityChicken(worldObj);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed
     * it (wheat, carrots or seeds depending on the animal type)
     */
    public boolean isBreedingItem(ItemStack par1ItemStack)
    {
        return par1ItemStack != null && par1ItemStack.getItem() instanceof ItemSeeds;
    }
}
