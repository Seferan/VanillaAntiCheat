package net.minecraft.entity.boss;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IRangedAttackMob
{
    private float[] field_82220_d = new float[2];
    private float[] field_82221_e = new float[2];
    private float[] field_82217_f = new float[2];
    private float[] field_82218_g = new float[2];
    private int[] field_82223_h = new int[2];
    private int[] field_82224_i = new int[2];
    private int field_82222_j;

    /** Selector used to determine the entities a wither boss should attack. */
    private static final IEntitySelector attackEntitySelector = new IEntitySelector()
    {
        private static final String __OBFID = "CL_00001662";

        public boolean isEntityApplicable(Entity par1Entity)
        {
            return par1Entity instanceof EntityLivingBase && ((EntityLivingBase)par1Entity).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
        }
    };
    private static final String __OBFID = "CL_00001661";

    public EntityWither(World par1World)
    {
        super(par1World);
        setHealth(getMaxHealth());
        setSize(0.9F, 4.0F);
        isImmuneToFire = true;
        getNavigator().setCanSwim(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 40, 20.0F));
        tasks.addTask(5, new EntityAIWander(this, 1.0D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, false, attackEntitySelector));
        experienceValue = 50;
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(17, new Integer(0));
        dataWatcher.addObject(18, new Integer(0));
        dataWatcher.addObject(19, new Integer(0));
        dataWatcher.addObject(20, new Integer(0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Invul", func_82212_n());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        func_82215_s(par1NBTTagCompound.getInteger("Invul"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.wither.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.wither.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.wither.death";
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        motionY *= 0.6000000238418579D;
        double var4;
        double var6;
        double var8;

        if (!worldObj.isClient && getWatchedTargetId(0) > 0)
        {
            Entity var1 = worldObj.getEntityByID(getWatchedTargetId(0));

            if (var1 != null)
            {
                if (posY < var1.posY || !isArmored() && posY < var1.posY + 5.0D)
                {
                    if (motionY < 0.0D)
                    {
                        motionY = 0.0D;
                    }

                    motionY += (0.5D - motionY) * 0.6000000238418579D;
                }

                double var2 = var1.posX - posX;
                var4 = var1.posZ - posZ;
                var6 = var2 * var2 + var4 * var4;

                if (var6 > 9.0D)
                {
                    var8 = MathHelper.sqrt_double(var6);
                    motionX += (var2 / var8 * 0.5D - motionX) * 0.6000000238418579D;
                    motionZ += (var4 / var8 * 0.5D - motionZ) * 0.6000000238418579D;
                }
            }
        }

        if (motionX * motionX + motionZ * motionZ > 0.05000000074505806D)
        {
            rotationYaw = (float)Math.atan2(motionZ, motionX) * (180F / (float)Math.PI) - 90.0F;
        }

        super.onLivingUpdate();
        int var20;

        for (var20 = 0; var20 < 2; ++var20)
        {
            field_82218_g[var20] = field_82221_e[var20];
            field_82217_f[var20] = field_82220_d[var20];
        }

        int var21;

        for (var20 = 0; var20 < 2; ++var20)
        {
            var21 = getWatchedTargetId(var20 + 1);
            Entity var3 = null;

            if (var21 > 0)
            {
                var3 = worldObj.getEntityByID(var21);
            }

            if (var3 != null)
            {
                var4 = func_82214_u(var20 + 1);
                var6 = func_82208_v(var20 + 1);
                var8 = func_82213_w(var20 + 1);
                double var10 = var3.posX - var4;
                double var12 = var3.posY + var3.getEyeHeight() - var6;
                double var14 = var3.posZ - var8;
                double var16 = MathHelper.sqrt_double(var10 * var10 + var14 * var14);
                float var18 = (float)(Math.atan2(var14, var10) * 180.0D / Math.PI) - 90.0F;
                float var19 = (float)(-(Math.atan2(var12, var16) * 180.0D / Math.PI));
                field_82220_d[var20] = func_82204_b(field_82220_d[var20], var19, 40.0F);
                field_82221_e[var20] = func_82204_b(field_82221_e[var20], var18, 10.0F);
            }
            else
            {
                field_82221_e[var20] = func_82204_b(field_82221_e[var20], renderYawOffset, 10.0F);
            }
        }

        boolean var22 = isArmored();

        for (var21 = 0; var21 < 3; ++var21)
        {
            double var23 = func_82214_u(var21);
            double var5 = func_82208_v(var21);
            double var7 = func_82213_w(var21);
            worldObj.spawnParticle("smoke", var23 + rand.nextGaussian() * 0.30000001192092896D, var5 + rand.nextGaussian() * 0.30000001192092896D, var7 + rand.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);

            if (var22 && worldObj.rand.nextInt(4) == 0)
            {
                worldObj.spawnParticle("mobSpell", var23 + rand.nextGaussian() * 0.30000001192092896D, var5 + rand.nextGaussian() * 0.30000001192092896D, var7 + rand.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
            }
        }

        if (func_82212_n() > 0)
        {
            for (var21 = 0; var21 < 3; ++var21)
            {
                worldObj.spawnParticle("mobSpell", posX + rand.nextGaussian() * 1.0D, posY + rand.nextFloat() * 3.3F, posZ + rand.nextGaussian() * 1.0D, 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
            }
        }
    }

    protected void updateAITasks()
    {
        int var1;

        if (func_82212_n() > 0)
        {
            var1 = func_82212_n() - 1;

            if (var1 <= 0)
            {
                worldObj.newExplosion(this, posX, posY + getEyeHeight(), posZ, 7.0F, false, worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
                worldObj.playBroadcastSound(1013, (int)posX, (int)posY, (int)posZ, 0);
            }

            func_82215_s(var1);

            if (ticksExisted % 10 == 0)
            {
                heal(10.0F);
            }
        }
        else
        {
            super.updateAITasks();
            int var12;

            for (var1 = 1; var1 < 3; ++var1)
            {
                if (ticksExisted >= field_82223_h[var1 - 1])
                {
                    field_82223_h[var1 - 1] = ticksExisted + 10 + rand.nextInt(10);

                    if (worldObj.difficultySetting == EnumDifficulty.NORMAL || worldObj.difficultySetting == EnumDifficulty.HARD)
                    {
                        int var10001 = var1 - 1;
                        int var10003 = field_82224_i[var1 - 1];
                        field_82224_i[var10001] = field_82224_i[var1 - 1] + 1;

                        if (var10003 > 15)
                        {
                            float var2 = 10.0F;
                            float var3 = 5.0F;
                            double var4 = MathHelper.getRandomDoubleInRange(rand, posX - var2, posX + var2);
                            double var6 = MathHelper.getRandomDoubleInRange(rand, posY - var3, posY + var3);
                            double var8 = MathHelper.getRandomDoubleInRange(rand, posZ - var2, posZ + var2);
                            func_82209_a(var1 + 1, var4, var6, var8, true);
                            field_82224_i[var1 - 1] = 0;
                        }
                    }

                    var12 = getWatchedTargetId(var1);

                    if (var12 > 0)
                    {
                        Entity var14 = worldObj.getEntityByID(var12);

                        if (var14 != null && var14.isEntityAlive() && getDistanceSqToEntity(var14) <= 900.0D && canEntityBeSeen(var14))
                        {
                            func_82216_a(var1 + 1, (EntityLivingBase)var14);
                            field_82223_h[var1 - 1] = ticksExisted + 40 + rand.nextInt(20);
                            field_82224_i[var1 - 1] = 0;
                        }
                        else
                        {
                            func_82211_c(var1, 0);
                        }
                    }
                    else
                    {
                        List var13 = worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, boundingBox.expand(20.0D, 8.0D, 20.0D), attackEntitySelector);

                        for (int var16 = 0; var16 < 10 && !var13.isEmpty(); ++var16)
                        {
                            EntityLivingBase var5 = (EntityLivingBase)var13.get(rand.nextInt(var13.size()));

                            if (var5 != this && var5.isEntityAlive() && canEntityBeSeen(var5))
                            {
                                if (var5 instanceof EntityPlayer)
                                {
                                    if (!((EntityPlayer)var5).capabilities.disableDamage)
                                    {
                                        func_82211_c(var1, var5.getEntityId());
                                    }
                                }
                                else
                                {
                                    func_82211_c(var1, var5.getEntityId());
                                }

                                break;
                            }

                            var13.remove(var5);
                        }
                    }
                }
            }

            if (getAttackTarget() != null)
            {
                func_82211_c(0, getAttackTarget().getEntityId());
            }
            else
            {
                func_82211_c(0, 0);
            }

            if (field_82222_j > 0)
            {
                --field_82222_j;

                if (field_82222_j == 0 && worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                {
                    var1 = MathHelper.floor_double(posY);
                    var12 = MathHelper.floor_double(posX);
                    int var15 = MathHelper.floor_double(posZ);
                    boolean var18 = false;

                    for (int var17 = -1; var17 <= 1; ++var17)
                    {
                        for (int var19 = -1; var19 <= 1; ++var19)
                        {
                            for (int var7 = 0; var7 <= 3; ++var7)
                            {
                                int var20 = var12 + var17;
                                int var9 = var1 + var7;
                                int var10 = var15 + var19;
                                Block var11 = worldObj.getBlock(var20, var9, var10);

                                if (var11.getMaterial() != Material.air && var11 != Blocks.bedrock && var11 != Blocks.end_portal && var11 != Blocks.end_portal_frame && var11 != Blocks.command_block)
                                {
                                    var18 = worldObj.func_147480_a(var20, var9, var10, true) || var18;
                                }
                            }
                        }
                    }

                    if (var18)
                    {
                        worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, (int)posX, (int)posY, (int)posZ, 0);
                    }
                }
            }

            if (ticksExisted % 20 == 0)
            {
                heal(1.0F);
            }
        }
    }

    public void func_82206_m()
    {
        func_82215_s(220);
        setHealth(getMaxHealth() / 3.0F);
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
    }

    /**
     * Returns the current armor value as determined by a call to
     * InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return 4;
    }

    private double func_82214_u(int par1)
    {
        if (par1 <= 0)
        {
            return posX;
        }
        else
        {
            float var2 = (renderYawOffset + 180 * (par1 - 1)) / 180.0F * (float)Math.PI;
            float var3 = MathHelper.cos(var2);
            return posX + var3 * 1.3D;
        }
    }

    private double func_82208_v(int par1)
    {
        return par1 <= 0 ? posY + 3.0D : posY + 2.2D;
    }

    private double func_82213_w(int par1)
    {
        if (par1 <= 0)
        {
            return posZ;
        }
        else
        {
            float var2 = (renderYawOffset + 180 * (par1 - 1)) / 180.0F * (float)Math.PI;
            float var3 = MathHelper.sin(var2);
            return posZ + var3 * 1.3D;
        }
    }

    private float func_82204_b(float par1, float par2, float par3)
    {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (var4 > par3)
        {
            var4 = par3;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        return par1 + var4;
    }

    private void func_82216_a(int par1, EntityLivingBase par2EntityLivingBase)
    {
        func_82209_a(par1, par2EntityLivingBase.posX, par2EntityLivingBase.posY + par2EntityLivingBase.getEyeHeight() * 0.5D, par2EntityLivingBase.posZ, par1 == 0 && rand.nextFloat() < 0.001F);
    }

    private void func_82209_a(int par1, double par2, double par4, double par6, boolean par8)
    {
        worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, (int)posX, (int)posY, (int)posZ, 0);
        double var9 = func_82214_u(par1);
        double var11 = func_82208_v(par1);
        double var13 = func_82213_w(par1);
        double var15 = par2 - var9;
        double var17 = par4 - var11;
        double var19 = par6 - var13;
        EntityWitherSkull var21 = new EntityWitherSkull(worldObj, this, var15, var17, var19);

        if (par8)
        {
            var21.setInvulnerable(true);
        }

        var21.posY = var11;
        var21.posX = var9;
        var21.posZ = var13;
        worldObj.spawnEntityInWorld(var21);
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2)
    {
        func_82216_a(0, par1EntityLivingBase);
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
        else if (par1DamageSource == DamageSource.drown)
        {
            return false;
        }
        else if (func_82212_n() > 0)
        {
            return false;
        }
        else
        {
            Entity var3;

            if (isArmored())
            {
                var3 = par1DamageSource.getSourceOfDamage();

                if (var3 instanceof EntityArrow) { return false; }
            }

            var3 = par1DamageSource.getEntity();

            if (var3 != null && !(var3 instanceof EntityPlayer) && var3 instanceof EntityLivingBase && ((EntityLivingBase)var3).getCreatureAttribute() == getCreatureAttribute())
            {
                return false;
            }
            else
            {
                if (field_82222_j <= 0)
                {
                    field_82222_j = 20;
                }

                for (int var4 = 0; var4 < field_82224_i.length; ++var4)
                {
                    field_82224_i[var4] += 3;
                }

                return super.attackEntityFrom(par1DamageSource, par2);
            }
        }
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        func_145779_a(Items.nether_star, 1);

        if (!worldObj.isClient)
        {
            Iterator var3 = worldObj.getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(50.0D, 100.0D, 50.0D)).iterator();

            while (var3.hasNext())
            {
                EntityPlayer var4 = (EntityPlayer)var3.next();
                var4.triggerAchievement(AchievementList.field_150964_J);
            }
        }
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity()
    {
        entityAge = 0;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(PotionEffect par1PotionEffect)
    {
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6000000238418579D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    }

    public int func_82212_n()
    {
        return dataWatcher.getWatchableObjectInt(20);
    }

    public void func_82215_s(int par1)
    {
        dataWatcher.updateObject(20, Integer.valueOf(par1));
    }

    /**
     * Returns the target entity ID if present, or -1 if not @param par1 The
     * target offset, should be from 0-2
     */
    public int getWatchedTargetId(int par1)
    {
        return dataWatcher.getWatchableObjectInt(17 + par1);
    }

    public void func_82211_c(int par1, int par2)
    {
        dataWatcher.updateObject(17 + par1, Integer.valueOf(par2));
    }

    /**
     * Returns whether the wither is armored with its boss armor or not by
     * checking whether its health is below half of its maximum.
     */
    public boolean isArmored()
    {
        return getHealth() <= getMaxHealth() / 2.0F;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        ridingEntity = null;
    }
}
