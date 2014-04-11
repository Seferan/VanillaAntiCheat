package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public class ItemInWorldManager
{
    /** The world object that this object is connected to. */
    public World theWorld;

    /** The EntityPlayerMP object that this object is connected to. */
    public EntityPlayerMP thisPlayerMP;
    private WorldSettings.GameType gameType;

    /** True if the player is destroying a block */
    private boolean isDestroyingBlock;
    private int initialDamage;
    private int curBlockX;
    private int curBlockY;
    private int curBlockZ;
    private int curblockDamage;

    /**
     * Set to true when the "finished destroying block" packet is received but
     * the block wasn't fully damaged yet. The block will not be destroyed while
     * this is false.
     */
    private boolean receivedFinishDiggingPacket;
    private int posX;
    private int posY;
    private int posZ;
    private int initialBlockDamage;
    private int durabilityRemainingOnBlock;
    private static final String __OBFID = "CL_00001442";

    public ItemInWorldManager(World par1World)
    {
        gameType = WorldSettings.GameType.NOT_SET;
        durabilityRemainingOnBlock = -1;
        theWorld = par1World;
    }

    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        gameType = par1EnumGameType;
        par1EnumGameType.configurePlayerCapabilities(thisPlayerMP.capabilities);
        thisPlayerMP.sendPlayerAbilities();
    }

    public WorldSettings.GameType getGameType()
    {
        return gameType;
    }

    /**
     * Get if we are in creative game mode.
     */
    public boolean isCreative()
    {
        return gameType.isCreative();
    }

    /**
     * if the gameType is currently NOT_SET then change it to par1
     */
    public void initializeGameType(WorldSettings.GameType par1EnumGameType)
    {
        if (gameType == WorldSettings.GameType.NOT_SET)
        {
            gameType = par1EnumGameType;
        }

        setGameType(gameType);
    }

    public void updateBlockRemoving()
    {
        ++curblockDamage;
        float var3;
        int var4;

        if (receivedFinishDiggingPacket)
        {
            int var1 = curblockDamage - initialBlockDamage;
            Block var2 = theWorld.getBlock(posX, posY, posZ);

            if (var2.getMaterial() == Material.air)
            {
                receivedFinishDiggingPacket = false;
            }
            else
            {
                var3 = var2.getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, posX, posY, posZ) * (var1 + 1);
                var4 = (int)(var3 * 10.0F);

                if (var4 != durabilityRemainingOnBlock)
                {
                    theWorld.destroyBlockInWorldPartially(thisPlayerMP.getEntityId(), posX, posY, posZ, var4);
                    durabilityRemainingOnBlock = var4;
                }

                if (var3 >= 1.0F)
                {
                    receivedFinishDiggingPacket = false;
                    tryHarvestBlock(posX, posY, posZ);
                }
            }
        }
        else if (isDestroyingBlock)
        {
            Block var5 = theWorld.getBlock(curBlockX, curBlockY, curBlockZ);

            if (var5.getMaterial() == Material.air)
            {
                theWorld.destroyBlockInWorldPartially(thisPlayerMP.getEntityId(), curBlockX, curBlockY, curBlockZ, -1);
                durabilityRemainingOnBlock = -1;
                isDestroyingBlock = false;
            }
            else
            {
                int var6 = curblockDamage - initialDamage;
                var3 = var5.getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, curBlockX, curBlockY, curBlockZ) * (var6 + 1);
                var4 = (int)(var3 * 10.0F);

                if (var4 != durabilityRemainingOnBlock)
                {
                    theWorld.destroyBlockInWorldPartially(thisPlayerMP.getEntityId(), curBlockX, curBlockY, curBlockZ, var4);
                    durabilityRemainingOnBlock = var4;
                }
            }
        }
    }

    /**
     * if not creative, it calls destroyBlockInWorldPartially untill the block
     * is broken first. par4 is the specific side. tryHarvestBlock can also be
     * the result of this call
     */
    public void onBlockClicked(int par1, int par2, int par3, int par4)
    {
        if (!gameType.isAdventure() || thisPlayerMP.isCurrentToolAdventureModeExempt(par1, par2, par3))
        {
            if (isCreative())
            {
                if (!theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, par4))
                {
                    tryHarvestBlock(par1, par2, par3);
                }
            }
            else
            {
                theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, par4);
                initialDamage = curblockDamage;
                float var5 = 1.0F;
                Block var6 = theWorld.getBlock(par1, par2, par3);

                if (var6.getMaterial() != Material.air)
                {
                    var6.onBlockClicked(theWorld, par1, par2, par3, thisPlayerMP);
                    var5 = var6.getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, par1, par2, par3);
                }

                if (var6.getMaterial() != Material.air && var5 >= 1.0F)
                {
                    tryHarvestBlock(par1, par2, par3);
                }
                else
                {
                    isDestroyingBlock = true;
                    curBlockX = par1;
                    curBlockY = par2;
                    curBlockZ = par3;
                    int var7 = (int)(var5 * 10.0F);
                    theWorld.destroyBlockInWorldPartially(thisPlayerMP.getEntityId(), par1, par2, par3, var7);
                    durabilityRemainingOnBlock = var7;
                }
            }
        }
    }

    public void blockRemoving(int par1, int par2, int par3)
    {
        if (par1 == curBlockX && par2 == curBlockY && par3 == curBlockZ)
        {
            int var4 = curblockDamage - initialDamage;
            Block var5 = theWorld.getBlock(par1, par2, par3);

            if (var5.getMaterial() != Material.air)
            {
                float var6 = var5.getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, par1, par2, par3) * (var4 + 1);

                if (var6 >= 0.7F)
                {
                    isDestroyingBlock = false;
                    theWorld.destroyBlockInWorldPartially(thisPlayerMP.getEntityId(), par1, par2, par3, -1);
                    tryHarvestBlock(par1, par2, par3);
                }
                else if (!receivedFinishDiggingPacket)
                {
                    isDestroyingBlock = false;
                    receivedFinishDiggingPacket = true;
                    posX = par1;
                    posY = par2;
                    posZ = par3;
                    initialBlockDamage = initialDamage;
                }
            }
        }
    }

    /**
     * note: this ignores the pars passed in and continues to destroy the
     * onClickedBlock
     */
    public void cancelDestroyingBlock(int par1, int par2, int par3)
    {
        isDestroyingBlock = false;
        theWorld.destroyBlockInWorldPartially(thisPlayerMP.getEntityId(), curBlockX, curBlockY, curBlockZ, -1);
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    private boolean removeBlock(int par1, int par2, int par3)
    {
        Block var4 = theWorld.getBlock(par1, par2, par3);
        int var5 = theWorld.getBlockMetadata(par1, par2, par3);
        var4.onBlockHarvested(theWorld, par1, par2, par3, var5, thisPlayerMP);
        boolean var6 = theWorld.setBlockToAir(par1, par2, par3);

        if (var6)
        {
            var4.onBlockDestroyedByPlayer(theWorld, par1, par2, par3, var5);
        }

        return var6;
    }

    /**
     * Attempts to harvest a block at the given coordinate
     */
    public boolean tryHarvestBlock(int par1, int par2, int par3)
    {
        if (gameType.isAdventure() && !thisPlayerMP.isCurrentToolAdventureModeExempt(par1, par2, par3))
        {
            return false;
        }
        else if (gameType.isCreative() && thisPlayerMP.getHeldItem() != null && thisPlayerMP.getHeldItem().getItem() instanceof ItemSword)
        {
            return false;
        }
        else
        {
            Block var4 = theWorld.getBlock(par1, par2, par3);
            int var5 = theWorld.getBlockMetadata(par1, par2, par3);
            theWorld.playAuxSFXAtEntity(thisPlayerMP, 2001, par1, par2, par3, Block.getIdFromBlock(var4) + (theWorld.getBlockMetadata(par1, par2, par3) << 12));
            boolean var6 = removeBlock(par1, par2, par3);

            if (isCreative())
            {
                thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(par1, par2, par3, theWorld));
            }
            else
            {
                ItemStack var7 = thisPlayerMP.getCurrentEquippedItem();
                boolean var8 = thisPlayerMP.canHarvestBlock(var4);

                if (var7 != null)
                {
                    var7.func_150999_a(theWorld, var4, par1, par2, par3, thisPlayerMP);

                    if (var7.stackSize == 0)
                    {
                        thisPlayerMP.destroyCurrentEquippedItem();
                    }
                }

                if (var6 && var8)
                {
                    var4.harvestBlock(theWorld, thisPlayerMP, par1, par2, par3, var5);
                }
            }

            return var6;
        }
    }

    /**
     * Attempts to right-click use an item by the given EntityPlayer in the
     * given World
     */
    public boolean tryUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        int var4 = par3ItemStack.stackSize;
        int var5 = par3ItemStack.getItemDamage();
        ItemStack var6 = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);

        if (var6 == par3ItemStack && (var6 == null || var6.stackSize == var4 && var6.getMaxItemUseDuration() <= 0 && var6.getItemDamage() == var5))
        {
            return false;
        }
        else
        {
            par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = var6;

            if (isCreative())
            {
                var6.stackSize = var4;

                if (var6.isItemStackDamageable())
                {
                    var6.setItemDamage(var5);
                }
            }

            if (var6.stackSize == 0)
            {
                par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
            }

            if (!par1EntityPlayer.isUsingItem())
            {
                ((EntityPlayerMP)par1EntityPlayer).sendContainerToPlayer(par1EntityPlayer.inventoryContainer);
            }

            return true;
        }
    }

    /**
     * Activate the clicked on block, otherwise use the held item. Args: player,
     * world, itemStack, x, y, z, side, xOffset, yOffset, zOffset
     */
    public boolean activateBlockOrUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if ((!par1EntityPlayer.isSneaking() || par1EntityPlayer.getHeldItem() == null) && par2World.getBlock(par4, par5, par6).onBlockActivated(par2World, par4, par5, par6, par1EntityPlayer, par7, par8, par9, par10))
        {
            return true;
        }
        else if (par3ItemStack == null)
        {
            return false;
        }
        else if (isCreative())
        {
            int var11 = par3ItemStack.getItemDamage();
            int var12 = par3ItemStack.stackSize;
            boolean var13 = par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);
            par3ItemStack.setItemDamage(var11);
            par3ItemStack.stackSize = var12;
            return var13;
        }
        else
        {
            return par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);
        }
    }

    /**
     * Sets the world instance.
     */
    public void setWorld(WorldServer par1WorldServer)
    {
        theWorld = par1WorldServer;
    }
}
