package net.minecraft.entity.monster;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob
{
    private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier attackingSpeedBoostModifier = (new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost", 6.199999809265137D, 0)).setSaved(false);
    private static boolean[] carriableBlocks = new boolean[256];

    /**
     * Counter to delay the teleportation of an enderman towards the currently
     * attacked target
     */
    private int teleportDelay;

    /**
     * A player must stare at an enderman for 5 ticks before it becomes
     * aggressive. This field counts those ticks.
     */
    private int stareTimer;
    private Entity lastEntityToAttack;
    private boolean isAggressive;
    private static final String __OBFID = "CL_00001685";

    public EntityEnderman(World par1World)
    {
        super(par1World);
        setSize(0.6F, 2.9F);
        stepHeight = 1.0F;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, new Byte((byte)0));
        dataWatcher.addObject(17, new Byte((byte)0));
        dataWatcher.addObject(18, new Byte((byte)0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("carried", (short)Block.getIdFromBlock(func_146080_bZ()));
        par1NBTTagCompound.setShort("carriedData", (short)getCarryingData());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        func_146081_a(Block.getBlockById(par1NBTTagCompound.getShort("carried")));
        setCarryingData(par1NBTTagCompound.getShort("carriedData"));
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this
     * Entity isn't interested in attacking (Animals, Spiders at day, peaceful
     * PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        EntityPlayer var1 = worldObj.getClosestVulnerablePlayerToEntity(this, 64.0D);

        if (var1 != null)
        {
            if (shouldAttackPlayer(var1))
            {
                isAggressive = true;

                if (stareTimer == 0)
                {
                    worldObj.playSoundEffect(var1.posX, var1.posY, var1.posZ, "mob.endermen.stare", 1.0F, 1.0F);
                }

                if (stareTimer++ == 5)
                {
                    stareTimer = 0;
                    setScreaming(true);
                    return var1;
                }
            }
            else
            {
                stareTimer = 0;
            }
        }

        return null;
    }

    /**
     * Checks to see if this enderman should be attacking this player
     */
    private boolean shouldAttackPlayer(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.armorInventory[3];

        if (var2 != null && var2.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
        {
            return false;
        }
        else
        {
            Vec3 var3 = par1EntityPlayer.getLook(1.0F).normalize();
            Vec3 var4 = worldObj.getWorldVec3Pool().getVecFromPool(posX - par1EntityPlayer.posX, boundingBox.minY + height / 2.0F - (par1EntityPlayer.posY + par1EntityPlayer.getEyeHeight()), posZ - par1EntityPlayer.posZ);
            double var5 = var4.lengthVector();
            var4 = var4.normalize();
            double var7 = var3.dotProduct(var4);
            return var7 > 1.0D - 0.025D / var5 ? par1EntityPlayer.canEntityBeSeen(this) : false;
        }
    }

    /**
     * Called frequently so the entity can update its state every tick as
     * required. For example, zombies and skeletons use this to react to
     * sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (isWet())
        {
            attackEntityFrom(DamageSource.drown, 1.0F);
        }

        if (lastEntityToAttack != entityToAttack)
        {
            IAttributeInstance var1 = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            var1.removeModifier(attackingSpeedBoostModifier);

            if (entityToAttack != null)
            {
                var1.applyModifier(attackingSpeedBoostModifier);
            }
        }

        lastEntityToAttack = entityToAttack;
        int var6;

        if (!worldObj.isClient && worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
        {
            int var2;
            int var3;
            Block var4;

            if (func_146080_bZ().getMaterial() == Material.air)
            {
                if (rand.nextInt(20) == 0)
                {
                    var6 = MathHelper.floor_double(posX - 2.0D + rand.nextDouble() * 4.0D);
                    var2 = MathHelper.floor_double(posY + rand.nextDouble() * 3.0D);
                    var3 = MathHelper.floor_double(posZ - 2.0D + rand.nextDouble() * 4.0D);
                    var4 = worldObj.getBlock(var6, var2, var3);

                    if (carriableBlocks[Block.getIdFromBlock(var4)])
                    {
                        func_146081_a(var4);
                        setCarryingData(worldObj.getBlockMetadata(var6, var2, var3));
                        worldObj.setBlock(var6, var2, var3, Blocks.air);
                    }
                }
            }
            else if (rand.nextInt(2000) == 0)
            {
                var6 = MathHelper.floor_double(posX - 1.0D + rand.nextDouble() * 2.0D);
                var2 = MathHelper.floor_double(posY + rand.nextDouble() * 2.0D);
                var3 = MathHelper.floor_double(posZ - 1.0D + rand.nextDouble() * 2.0D);
                var4 = worldObj.getBlock(var6, var2, var3);
                Block var5 = worldObj.getBlock(var6, var2 - 1, var3);

                if (var4.getMaterial() == Material.air && var5.getMaterial() != Material.air && var5.renderAsNormalBlock())
                {
                    worldObj.setBlock(var6, var2, var3, func_146080_bZ(), getCarryingData(), 3);
                    func_146081_a(Blocks.air);
                }
            }
        }

        for (var6 = 0; var6 < 2; ++var6)
        {
            worldObj.spawnParticle("portal", posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height - 0.25D, posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
        }

        if (worldObj.isDaytime() && !worldObj.isClient)
        {
            float var7 = getBrightness(1.0F);

            if (var7 > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && rand.nextFloat() * 30.0F < (var7 - 0.4F) * 2.0F)
            {
                entityToAttack = null;
                setScreaming(false);
                isAggressive = false;
                teleportRandomly();
            }
        }

        if (isWet() || isBurning())
        {
            entityToAttack = null;
            setScreaming(false);
            isAggressive = false;
            teleportRandomly();
        }

        if (isScreaming() && !isAggressive && rand.nextInt(100) == 0)
        {
            setScreaming(false);
        }

        isJumping = false;

        if (entityToAttack != null)
        {
            faceEntity(entityToAttack, 100.0F, 100.0F);
        }

        if (!worldObj.isClient && isEntityAlive())
        {
            if (entityToAttack != null)
            {
                if (entityToAttack instanceof EntityPlayer && shouldAttackPlayer((EntityPlayer)entityToAttack))
                {
                    if (entityToAttack.getDistanceSqToEntity(this) < 16.0D)
                    {
                        teleportRandomly();
                    }

                    teleportDelay = 0;
                }
                else if (entityToAttack.getDistanceSqToEntity(this) > 256.0D && teleportDelay++ >= 30 && teleportToEntity(entityToAttack))
                {
                    teleportDelay = 0;
                }
            }
            else
            {
                setScreaming(false);
                teleportDelay = 0;
            }
        }

        super.onLivingUpdate();
    }

    /**
     * Teleport the enderman to a random nearby position
     */
    protected boolean teleportRandomly()
    {
        double var1 = posX + (rand.nextDouble() - 0.5D) * 64.0D;
        double var3 = posY + (rand.nextInt(64) - 32);
        double var5 = posZ + (rand.nextDouble() - 0.5D) * 64.0D;
        return teleportTo(var1, var3, var5);
    }

    /**
     * Teleport the enderman to another entity
     */
    protected boolean teleportToEntity(Entity par1Entity)
    {
        Vec3 var2 = worldObj.getWorldVec3Pool().getVecFromPool(posX - par1Entity.posX, boundingBox.minY + height / 2.0F - par1Entity.posY + par1Entity.getEyeHeight(), posZ - par1Entity.posZ);
        var2 = var2.normalize();
        double var3 = 16.0D;
        double var5 = posX + (rand.nextDouble() - 0.5D) * 8.0D - var2.xCoord * var3;
        double var7 = posY + (rand.nextInt(16) - 8) - var2.yCoord * var3;
        double var9 = posZ + (rand.nextDouble() - 0.5D) * 8.0D - var2.zCoord * var3;
        return teleportTo(var5, var7, var9);
    }

    /**
     * Teleport the enderman
     */
    protected boolean teleportTo(double par1, double par3, double par5)
    {
        double var7 = posX;
        double var9 = posY;
        double var11 = posZ;
        posX = par1;
        posY = par3;
        posZ = par5;
        boolean var13 = false;
        int var14 = MathHelper.floor_double(posX);
        int var15 = MathHelper.floor_double(posY);
        int var16 = MathHelper.floor_double(posZ);

        if (worldObj.blockExists(var14, var15, var16))
        {
            boolean var17 = false;

            while (!var17 && var15 > 0)
            {
                Block var18 = worldObj.getBlock(var14, var15 - 1, var16);

                if (var18.getMaterial().blocksMovement())
                {
                    var17 = true;
                }
                else
                {
                    --posY;
                    --var15;
                }
            }

            if (var17)
            {
                setPosition(posX, posY, posZ);

                if (worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox))
                {
                    var13 = true;
                }
            }
        }

        if (!var13)
        {
            setPosition(var7, var9, var11);
            return false;
        }
        else
        {
            short var30 = 128;

            for (int var31 = 0; var31 < var30; ++var31)
            {
                double var19 = var31 / (var30 - 1.0D);
                float var21 = (rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (rand.nextFloat() - 0.5F) * 0.2F;
                double var24 = var7 + (posX - var7) * var19 + (rand.nextDouble() - 0.5D) * width * 2.0D;
                double var26 = var9 + (posY - var9) * var19 + rand.nextDouble() * height;
                double var28 = var11 + (posZ - var11) * var19 + (rand.nextDouble() - 0.5D) * width * 2.0D;
                worldObj.spawnParticle("portal", var24, var26, var28, var21, var22, var23);
            }

            worldObj.playSoundEffect(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
            playSound("mob.endermen.portal", 1.0F, 1.0F);
            return true;
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.endermen.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.endermen.death";
    }

    protected Item func_146068_u()
    {
        return Items.ender_pearl;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        Item var3 = func_146068_u();

        if (var3 != null)
        {
            int var4 = rand.nextInt(2 + par2);

            for (int var5 = 0; var5 < var4; ++var5)
            {
                func_145779_a(var3, 1);
            }
        }
    }

    public void func_146081_a(Block p_146081_1_)
    {
        dataWatcher.updateObject(16, Byte.valueOf((byte)(Block.getIdFromBlock(p_146081_1_) & 255)));
    }

    public Block func_146080_bZ()
    {
        return Block.getBlockById(dataWatcher.getWatchableObjectByte(16));
    }

    /**
     * Set the metadata of the block an enderman carries
     */
    public void setCarryingData(int par1)
    {
        dataWatcher.updateObject(17, Byte.valueOf((byte)(par1 & 255)));
    }

    /**
     * Get the metadata of the block an enderman carries
     */
    public int getCarryingData()
    {
        return dataWatcher.getWatchableObjectByte(17);
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
            setScreaming(true);

            if (par1DamageSource instanceof EntityDamageSource && par1DamageSource.getEntity() instanceof EntityPlayer)
            {
                isAggressive = true;
            }

            if (par1DamageSource instanceof EntityDamageSourceIndirect)
            {
                isAggressive = false;

                for (int var3 = 0; var3 < 64; ++var3)
                {
                    if (teleportRandomly()) { return true; }
                }

                return false;
            }
            else
            {
                return super.attackEntityFrom(par1DamageSource, par2);
            }
        }
    }

    public boolean isScreaming()
    {
        return dataWatcher.getWatchableObjectByte(18) > 0;
    }

    public void setScreaming(boolean par1)
    {
        dataWatcher.updateObject(18, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    static
    {
        carriableBlocks[Block.getIdFromBlock(Blocks.grass)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.dirt)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.sand)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.gravel)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.yellow_flower)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.red_flower)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.brown_mushroom)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.red_mushroom)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.tnt)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.cactus)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.clay)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.pumpkin)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.melon_block)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.mycelium)] = true;
    }
}
