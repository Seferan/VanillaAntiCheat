package net.minecraft.util;

import mx.x10.afffsdd.vanillaanticheat.VACUtils;
import mx.x10.afffsdd.vanillaanticheat.module.VACState;
import net.minecraft.entity.player.EntityPlayer;
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
        foodLevel = Math.min(par1 + foodLevel, 20);
        foodSaturationLevel = Math.min(foodSaturationLevel + par1 * par2 * 2.0F, foodLevel);
    }

    public void func_151686_a(ItemFood p_151686_1_, ItemStack p_151686_2_)
    {
        addStats(p_151686_1_.func_150905_g(p_151686_2_), p_151686_1_.func_150906_h(p_151686_2_));
    }

    /**
     * Handles the food game logic.
     */
    public void onUpdate(EntityPlayer entityPlayer)
    {
        EnumDifficulty var2 = entityPlayer.worldObj.difficultySetting;
        prevFoodLevel = foodLevel;

        if (foodExhaustionLevel > 4.0F)
        {
            foodExhaustionLevel -= 4.0F;

            if (foodSaturationLevel > 0.0F)
            {
                foodSaturationLevel = Math.max(foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (var2 != EnumDifficulty.PEACEFUL)
            {
                foodLevel = Math.max(foodLevel - 1, 0);
            }
        }

        if (entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && foodLevel >= 18 && entityPlayer.shouldHeal())
        {
            ++foodTimer;

            if (foodTimer >= 80)
            {
                foodTimer = 0;

                VACState vacState = entityPlayer.getVACState();
                long ticksTaken = vacState.aRegen.getTicksSinceLastHeal();
                if (ticksTaken < MinecraftServer.getServer().getHealthRegenTickCount() && ticksTaken > -1 && !MinecraftServer.isPlayerOpped(entityPlayer))
                {
                    if (!vacState.aRegen.hasBeenLogged())
                    {
                        StringBuilder message = new StringBuilder();
                        message.append(entityPlayer.getUsername());
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
                    addExhaustion(3.0F);
                }
                entityPlayer.getVACState().aRegen.heal();
            }
        }
        else if (foodLevel <= 0)
        {
            ++foodTimer;

            if (foodTimer >= 80)
            {
                if (entityPlayer.getHealth() > 10.0F || var2 == EnumDifficulty.HARD || entityPlayer.getHealth() > 1.0F && var2 == EnumDifficulty.NORMAL)
                {
                    entityPlayer.attackEntityFrom(DamageSource.starve, 1.0F);
                }

                foodTimer = 0;
            }
        }
        else
        {
            foodTimer = 0;
        }
    }

    /**
     * Reads the food data for the player.
     */
    public void readNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.func_150297_b("foodLevel", 99))
        {
            foodLevel = par1NBTTagCompound.getInteger("foodLevel");
            foodTimer = par1NBTTagCompound.getInteger("foodTickTimer");
            foodSaturationLevel = par1NBTTagCompound.getFloat("foodSaturationLevel");
            foodExhaustionLevel = par1NBTTagCompound.getFloat("foodExhaustionLevel");
        }
    }

    /**
     * Writes the food data for the player.
     */
    public void writeNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("foodLevel", foodLevel);
        par1NBTTagCompound.setInteger("foodTickTimer", foodTimer);
        par1NBTTagCompound.setFloat("foodSaturationLevel", foodSaturationLevel);
        par1NBTTagCompound.setFloat("foodExhaustionLevel", foodExhaustionLevel);
    }

    /**
     * Get the player's food level.
     */
    public int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Get whether the player must eat food.
     */
    public boolean needFood()
    {
        return foodLevel < 20;
    }

    /**
     * adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion(float par1)
    {
        foodExhaustionLevel = Math.min(foodExhaustionLevel + par1, 40.0F);
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel()
    {
        return foodSaturationLevel;
    }
}
