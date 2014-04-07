package net.minecraft.entity.monster;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpider extends EntityMob
{
    private static final String __OBFID = "CL_00001699";

    public EntitySpider(World par1World)
    {
        super(par1World);
        setSize(1.4F, 0.9F);
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!worldObj.isClient)
        {
            setBesideClimbableBlock(isCollidedHorizontally);
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.800000011920929D);
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this
     * Entity isn't interested in attacking (Animals, Spiders at day, peaceful
     * PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        float var1 = getBrightness(1.0F);

        if (var1 < 0.5F)
        {
            double var2 = 16.0D;
            return worldObj.getClosestVulnerablePlayerToEntity(this, var2);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.spider.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.spider.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.spider.death";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        playSound("mob.spider.step", 0.15F, 1.0F);
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden
     * by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        float var3 = getBrightness(1.0F);

        if (var3 > 0.5F && rand.nextInt(100) == 0)
        {
            entityToAttack = null;
        }
        else
        {
            if (par2 > 2.0F && par2 < 6.0F && rand.nextInt(10) == 0)
            {
                if (onGround)
                {
                    double var4 = par1Entity.posX - posX;
                    double var6 = par1Entity.posZ - posZ;
                    float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
                    motionX = var4 / var8 * 0.5D * 0.800000011920929D + motionX * 0.20000000298023224D;
                    motionZ = var6 / var8 * 0.5D * 0.800000011920929D + motionZ * 0.20000000298023224D;
                    motionY = 0.4000000059604645D;
                }
            }
            else
            {
                super.attackEntity(par1Entity, par2);
            }
        }
    }

    protected Item func_146068_u()
    {
        return Items.string;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        super.dropFewItems(par1, par2);

        if (par1 && (rand.nextInt(3) == 0 || rand.nextInt(1 + par2) > 0))
        {
            func_145779_a(Items.spider_eye, 1);
        }
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return isBesideClimbableBlock();
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public boolean isPotionApplicable(PotionEffect par1PotionEffect)
    {
        return par1PotionEffect.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(par1PotionEffect);
    }

    /**
     * Returns true if the WatchableObject (Byte) is 0x01 otherwise returns
     * false. The WatchableObject is updated using setBesideClimableBlock.
     */
    public boolean isBesideClimbableBlock()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    /**
     * Updates the WatchableObject (Byte) created in entityInit(), setting it to
     * 0x01 if par1 is true or 0x00 if it is false.
     */
    public void setBesideClimbableBlock(boolean par1)
    {
        byte var2 = dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            var2 = (byte)(var2 | 1);
        }
        else
        {
            var2 &= -2;
        }

        dataWatcher.updateObject(16, Byte.valueOf(var2));
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        Object par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);

        if (worldObj.rand.nextInt(100) == 0)
        {
            EntitySkeleton var2 = new EntitySkeleton(worldObj);
            var2.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
            var2.onSpawnWithEgg((IEntityLivingData)null);
            worldObj.spawnEntityInWorld(var2);
            var2.mountEntity(this);
        }

        if (par1EntityLivingData1 == null)
        {
            par1EntityLivingData1 = new EntitySpider.GroupData();

            if (worldObj.difficultySetting == EnumDifficulty.HARD && worldObj.rand.nextFloat() < 0.1F * worldObj.func_147462_b(posX, posY, posZ))
            {
                ((EntitySpider.GroupData)par1EntityLivingData1).func_111104_a(worldObj.rand);
            }
        }

        if (par1EntityLivingData1 instanceof EntitySpider.GroupData)
        {
            int var4 = ((EntitySpider.GroupData)par1EntityLivingData1).field_111105_a;

            if (var4 > 0 && Potion.potionTypes[var4] != null)
            {
                addPotionEffect(new PotionEffect(var4, Integer.MAX_VALUE));
            }
        }

        return (IEntityLivingData)par1EntityLivingData1;
    }

    public static class GroupData implements IEntityLivingData
    {
        public int field_111105_a;
        private static final String __OBFID = "CL_00001700";

        public void func_111104_a(Random par1Random)
        {
            int var2 = par1Random.nextInt(5);

            if (var2 <= 1)
            {
                field_111105_a = Potion.moveSpeed.id;
            }
            else if (var2 <= 2)
            {
                field_111105_a = Potion.damageBoost.id;
            }
            else if (var2 <= 3)
            {
                field_111105_a = Potion.regeneration.id;
            }
            else if (var2 <= 4)
            {
                field_111105_a = Potion.invisibility.id;
            }
        }
    }
}
