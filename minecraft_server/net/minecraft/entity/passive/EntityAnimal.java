package net.minecraft.entity.passive;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals
{
    private int inLove;

    /**
     * This is representation of a counter for reproduction progress. (Note that
     * this is different from the inLove which represent being in Love-Mode)
     */
    private int breeding;
    private EntityPlayer field_146084_br;
    private static final String __OBFID = "CL_00001638";

    public EntityAnimal(World par1World)
    {
        super(par1World);
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick()
    {
        if (getGrowingAge() != 0)
        {
            inLove = 0;
        }

        super.updateAITick();
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (getGrowingAge() != 0)
        {
            inLove = 0;
        }

        if (inLove > 0)
        {
            --inLove;
            String var1 = "heart";

            if (inLove % 10 == 0)
            {
                double var2 = rand.nextGaussian() * 0.02D;
                double var4 = rand.nextGaussian() * 0.02D;
                double var6 = rand.nextGaussian() * 0.02D;
                worldObj.spawnParticle(var1, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var2, var4, var6);
            }
        }
        else
        {
            breeding = 0;
        }
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden
     * by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        if (par1Entity instanceof EntityPlayer)
        {
            if (par2 < 3.0F)
            {
                double var3 = par1Entity.posX - posX;
                double var5 = par1Entity.posZ - posZ;
                rotationYaw = (float)(Math.atan2(var5, var3) * 180.0D / Math.PI) - 90.0F;
                hasAttacked = true;
            }

            EntityPlayer var7 = (EntityPlayer)par1Entity;

            if (var7.getCurrentEquippedItem() == null || !isBreedingItem(var7.getCurrentEquippedItem()))
            {
                entityToAttack = null;
            }
        }
        else if (par1Entity instanceof EntityAnimal)
        {
            EntityAnimal var8 = (EntityAnimal)par1Entity;

            if (getGrowingAge() > 0 && var8.getGrowingAge() < 0)
            {
                if (par2 < 2.5D)
                {
                    hasAttacked = true;
                }
            }
            else if (inLove > 0 && var8.inLove > 0)
            {
                if (var8.entityToAttack == null)
                {
                    var8.entityToAttack = this;
                }

                if (var8.entityToAttack == this && par2 < 3.5D)
                {
                    ++var8.inLove;
                    ++inLove;
                    ++breeding;

                    if (breeding % 4 == 0)
                    {
                        worldObj.spawnParticle("heart", posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, 0.0D, 0.0D, 0.0D);
                    }

                    if (breeding == 60)
                    {
                        procreate((EntityAnimal)par1Entity);
                    }
                }
                else
                {
                    breeding = 0;
                }
            }
            else
            {
                breeding = 0;
                entityToAttack = null;
            }
        }
    }

    /**
     * Creates a baby animal according to the animal type of the target at the
     * actual position and spawns 'love' particles.
     */
    private void procreate(EntityAnimal par1EntityAnimal)
    {
        EntityAgeable var2 = createChild(par1EntityAnimal);

        if (var2 != null)
        {
            if (field_146084_br == null && par1EntityAnimal.func_146083_cb() != null)
            {
                field_146084_br = par1EntityAnimal.func_146083_cb();
            }

            if (field_146084_br != null)
            {
                field_146084_br.triggerAchievement(StatList.field_151186_x);

                if (this instanceof EntityCow)
                {
                    field_146084_br.triggerAchievement(AchievementList.field_150962_H);
                }
            }

            setGrowingAge(6000);
            par1EntityAnimal.setGrowingAge(6000);
            inLove = 0;
            breeding = 0;
            entityToAttack = null;
            par1EntityAnimal.entityToAttack = null;
            par1EntityAnimal.breeding = 0;
            par1EntityAnimal.inLove = 0;
            var2.setGrowingAge(-24000);
            var2.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);

            for (int var3 = 0; var3 < 7; ++var3)
            {
                double var4 = rand.nextGaussian() * 0.02D;
                double var6 = rand.nextGaussian() * 0.02D;
                double var8 = rand.nextGaussian() * 0.02D;
                worldObj.spawnParticle("heart", posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var4, var6, var8);
            }

            worldObj.spawnEntityInWorld(var2);
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            fleeingTick = 60;

            if (!isAIEnabled())
            {
                IAttributeInstance var3 = getEntityAttribute(SharedMonsterAttributes.movementSpeed);

                if (var3.getModifier(field_110179_h) == null)
                {
                    var3.applyModifier(field_110181_i);
                }
            }

            entityToAttack = null;
            inLove = 0;
            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    /**
     * Takes a coordinate in and returns a weight to determine how likely this
     * creature will try to path to the block. Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return worldObj.getBlock(par1, par2 - 1, par3) == Blocks.grass ? 10.0F : worldObj.getLightBrightness(par1, par2, par3) - 0.5F;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("InLove", inLove);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        inLove = par1NBTTagCompound.getInteger("InLove");
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this
     * Entity isn't interested in attacking (Animals, Spiders at day, peaceful
     * PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        if (fleeingTick > 0)
        {
            return null;
        }
        else
        {
            float var1 = 8.0F;
            List var2;
            int var3;
            EntityAnimal var4;

            if (inLove > 0)
            {
                var2 = worldObj.getEntitiesWithinAABB(this.getClass(), boundingBox.expand(var1, var1, var1));

                for (var3 = 0; var3 < var2.size(); ++var3)
                {
                    var4 = (EntityAnimal)var2.get(var3);

                    if (var4 != this && var4.inLove > 0) { return var4; }
                }
            }
            else if (getGrowingAge() == 0)
            {
                var2 = worldObj.getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(var1, var1, var1));

                for (var3 = 0; var3 < var2.size(); ++var3)
                {
                    EntityPlayer var5 = (EntityPlayer)var2.get(var3);

                    if (var5.getCurrentEquippedItem() != null && isBreedingItem(var5.getCurrentEquippedItem())) { return var5; }
                }
            }
            else if (getGrowingAge() > 0)
            {
                var2 = worldObj.getEntitiesWithinAABB(this.getClass(), boundingBox.expand(var1, var1, var1));

                for (var3 = 0; var3 < var2.size(); ++var3)
                {
                    var4 = (EntityAnimal)var2.get(var3);

                    if (var4 != this && var4.getGrowingAge() < 0) { return var4; }
                }
            }

            return null;
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this
     * entity.
     */
    public boolean getCanSpawnHere()
    {
        int var1 = MathHelper.floor_double(posX);
        int var2 = MathHelper.floor_double(boundingBox.minY);
        int var3 = MathHelper.floor_double(posZ);
        return worldObj.getBlock(var1, var2 - 1, var3) == Blocks.grass && worldObj.getFullBlockLightValue(var1, var2, var3) > 8 && super.getCanSpawnHere();
    }

    /**
     * Get number of ticks, at least during which the living entity will be
     * silent.
     */
    public int getTalkInterval()
    {
        return 120;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return false;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        return 1 + worldObj.rand.nextInt(3);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed
     * it (wheat, carrots or seeds depending on the animal type)
     */
    public boolean isBreedingItem(ItemStack par1ItemStack)
    {
        return par1ItemStack.getItem() == Items.wheat;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && isBreedingItem(var2) && getGrowingAge() == 0 && inLove <= 0)
        {
            if (!par1EntityPlayer.capabilities.isCreativeMode)
            {
                --var2.stackSize;

                if (var2.stackSize <= 0)
                {
                    par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                }
            }

            func_146082_f(par1EntityPlayer);
            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }

    public void func_146082_f(EntityPlayer p_146082_1_)
    {
        inLove = 600;
        field_146084_br = p_146082_1_;
        entityToAttack = null;
        worldObj.setEntityState(this, (byte)18);
    }

    public EntityPlayer func_146083_cb()
    {
        return field_146084_br;
    }

    /**
     * Returns if the entity is currently in 'love mode'.
     */
    public boolean isInLove()
    {
        return inLove > 0;
    }

    public void resetInLove()
    {
        inLove = 0;
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean canMateWith(EntityAnimal par1EntityAnimal)
    {
        return par1EntityAnimal == this ? false : (par1EntityAnimal.getClass() != this.getClass() ? false : isInLove() && par1EntityAnimal.isInLove());
    }
}
