package net.minecraft.util;

import mx.x10.afffsdd.vanillaanticheat.VACUtils;
import mx.x10.afffsdd.vanillaanticheat.module.VACState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;

public class FoodStats
{
    /** The player's food level. */
    private int foodLevel = 20;

    /** The player's food saturation. */
    private float foodSaturationLevel = 5.0F;

    /** The player's food exhaustion. */
    private float foodExhaustionLevel;

    /** The player's food timer value. */
    private int foodTimer;
    private int prevFoodLevel = 20;
    private static final String __OBFID = "CL_00001729";

    /**
     * Args: int foodLevel, float foodSaturationModifier
     */
    public void addStats(int par1, float par2)
    {
        this.foodLevel = Math.min(par1 + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)par1 * par2 * 2.0F, (float)this.foodLevel);
    }

    public void func_151686_a(ItemFood p_151686_1_, ItemStack p_151686_2_)
    {
        this.addStats(p_151686_1_.func_150905_g(p_151686_2_), p_151686_1_.func_150906_h(p_151686_2_));
    }

    /**
     * Handles the food game logic.
     */
    public void onUpdate(EntityPlayer entityPlayer)
    {
        EnumDifficulty var2 = entityPlayer.worldObj.difficultySetting;
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > 4.0F)
        {
            this.foodExhaustionLevel -= 4.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (var2 != EnumDifficulty.PEACEFUL)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.foodLevel >= 18 && entityPlayer.shouldHeal())
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                this.foodTimer = 0;
                
                VACState vacState = entityPlayer.getVACState();
                long ticksTaken = vacState.aRegen.getTicksSinceLastHeal();
                if(ticksTaken < MinecraftServer.getServer().getHealthRegenTickCount() && ticksTaken > -1)
                {
                    if(!vacState.aRegen.hasBeenLogged())
                    {
                        StringBuilder message = new StringBuilder();
                        message.append(entityPlayer.getCommandSenderName());
                        message.append(" regenerated health too quickly! ");
                        message.append(ticksTaken);
                        message.append(" ticks  / 80");
                        VACUtils.notifyAndLog(vacState.aRegen, message.toString());
                        vacState.aRegen.log();
                    }
                }
                else
                {
                    entityPlayer.heal(1.0F);
                    this.addExhaustion(3.0F);
                }
                entityPlayer.getVACState().aRegen.heal();
            }
        }
        else if (this.foodLevel <= 0)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                if (entityPlayer.getHealth() > 10.0F || var2 == EnumDifficulty.HARD || entityPlayer.getHealth() > 1.0F && var2 == EnumDifficulty.NORMAL)
                {
                    entityPlayer.attackEntityFrom(DamageSource.starve, 1.0F);
                }

                this.foodTimer = 0;
            }
        }
        else
        {
            this.foodTimer = 0;
        }
    }

    /**
     * Reads the food data for the player.
     */
    public void readNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.func_150297_b("foodLevel", 99))
        {
            this.foodLevel = par1NBTTagCompound.getInteger("foodLevel");
            this.foodTimer = par1NBTTagCompound.getInteger("foodTickTimer");
            this.foodSaturationLevel = par1NBTTagCompound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = par1NBTTagCompound.getFloat("foodExhaustionLevel");
        }
    }

    /**
     * Writes the food data for the player.
     */
    public void writeNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("foodLevel", this.foodLevel);
        par1NBTTagCompound.setInteger("foodTickTimer", this.foodTimer);
        par1NBTTagCompound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        par1NBTTagCompound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    /**
     * Get the player's food level.
     */
    public int getFoodLevel()
    {
        return this.foodLevel;
    }

    /**
     * Get whether the player must eat food.
     */
    public boolean needFood()
    {
        return this.foodLevel < 20;
    }

    /**
     * adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion(float par1)
    {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + par1, 40.0F);
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel()
    {
        return this.foodSaturationLevel;
    }
}
