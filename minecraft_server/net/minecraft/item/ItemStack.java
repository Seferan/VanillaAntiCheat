package net.minecraft.item;

import java.text.DecimalFormat;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public final class ItemStack
{
    public static final DecimalFormat field_111284_a = new DecimalFormat("#.###");

    /** Size of the stack. */
    public int stackSize;

    /**
     * Number of animation frames to go when receiving an item (by walking into
     * it, for example).
     */
    public int animationsToGo;
    private Item item;

    /**
     * A NBTTagMap containing data about an ItemStack. Can only be used for non
     * stackable items
     */
    public NBTTagCompound stackTagCompound;

    /** Damage dealt to the item or number of use. Raise when using items. */
    private int itemDamage;

    /** Item frame this stack is on, or null if not on an item frame. */
    private EntityItemFrame itemFrame;
    private static final String __OBFID = "CL_00000043";

    public ItemStack(Block par1Block)
    {
        this(par1Block, 1);
    }

    public ItemStack(Block par1Block, int par2)
    {
        this(par1Block, par2, 0);
    }

    public ItemStack(Block par1Block, int par2, int par3)
    {
        this(Item.getItemFromBlock(par1Block), par2, par3);
    }

    public ItemStack(Item par1Item)
    {
        this(par1Item, 1);
    }

    public ItemStack(Item par1Item, int par2)
    {
        this(par1Item, par2, 0);
    }

    public ItemStack(Item par1Item, int amount, int data)
    {
        item = par1Item;
        stackSize = amount;
        itemDamage = data;

        if (itemDamage < 0)
        {
            itemDamage = 0;
        }
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound par0NBTTagCompound)
    {
        ItemStack var1 = new ItemStack();
        var1.readFromNBT(par0NBTTagCompound);
        return var1.getItem() != null ? var1 : null;
    }

    private ItemStack()
    {
    }

    /**
     * Remove the argument from the stack size. Return a new stack object with
     * argument size.
     */
    public ItemStack splitStack(int par1)
    {
        ItemStack var2 = new ItemStack(item, par1, itemDamage);

        if (stackTagCompound != null)
        {
            var2.stackTagCompound = (NBTTagCompound)stackTagCompound.copy();
        }

        stackSize -= par1;
        return var2;
    }

    /**
     * Returns the object corresponding to the stack.
     */
    public Item getItem()
    {
        return item;
    }

    public boolean tryPlaceItemIntoWorld(EntityPlayer par1EntityPlayer, World par2World, int par3, int par4, int par5, int par6, float par7, float par8, float par9)
    {
        boolean var10 = getItem().onItemUse(this, par1EntityPlayer, par2World, par3, par4, par5, par6, par7, par8, par9);

        if (var10)
        {
            par1EntityPlayer.addStat(StatList.objectUseStats[Item.getIdFromItem(item)], 1);
        }

        return var10;
    }

    public float func_150997_a(Block p_150997_1_)
    {
        return getItem().func_150893_a(this, p_150997_1_);
    }

    /**
     * Called whenever this item stack is equipped and right clicked. Returns
     * the new item stack to put in the position where this item is. Args:
     * world, player
     */
    public ItemStack useItemRightClick(World par1World, EntityPlayer par2EntityPlayer)
    {
        return getItem().onItemRightClick(this, par1World, par2EntityPlayer);
    }

    public ItemStack onFoodEaten(World par1World, EntityPlayer par2EntityPlayer)
    {
        return getItem().onEaten(this, par1World, par2EntityPlayer);
    }

    /**
     * Write the stack fields to a NBT object. Return the new NBT object.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setShort("id", (short)Item.getIdFromItem(item));
        tagCompound.setByte("Count", (byte)stackSize);
        tagCompound.setShort("Damage", (short)itemDamage);

        if (stackTagCompound != null)
        {
            tagCompound.setTag("tag", stackTagCompound);
        }

        return tagCompound;
    }

    /**
     * Read the stack fields from a NBT object.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        item = Item.getItemById(par1NBTTagCompound.getShort("id"));
        stackSize = par1NBTTagCompound.getByte("Count");
        itemDamage = par1NBTTagCompound.getShort("Damage");

        if (itemDamage < 0)
        {
            itemDamage = 0;
        }

        if (par1NBTTagCompound.func_150297_b("tag", 10))
        {
            stackTagCompound = par1NBTTagCompound.getCompoundTag("tag");
        }
    }

    /**
     * Returns maximum size of the stack.
     */
    public int getMaxStackSize()
    {
        return getItem().getItemStackLimit();
    }

    /**
     * Returns true if the ItemStack can hold 2 or more units of the item.
     */
    public boolean isStackable()
    {
        return getMaxStackSize() > 1 && (!isItemStackDamageable() || !isItemDamaged());
    }

    /**
     * true if this itemStack is damageable
     */
    public boolean isItemStackDamageable()
    {
        return item.getMaxDamage() <= 0 ? false : !hasTagCompound() || !getTagCompound().getBoolean("Unbreakable");
    }

    public boolean getHasSubtypes()
    {
        return item.getHasSubtypes();
    }

    /**
     * returns true when a damageable item is damaged
     */
    public boolean isItemDamaged()
    {
        return isItemStackDamageable() && itemDamage > 0;
    }

    /**
     * gets the damage of an itemstack, for displaying purposes
     */
    public int getItemDamageForDisplay()
    {
        return itemDamage;
    }

    /**
     * gets the damage of an itemstack
     */
    public int getItemDamage()
    {
        return itemDamage;
    }

    /**
     * Sets the item damage of the ItemStack.
     */
    public void setItemDamage(int par1)
    {
        itemDamage = par1;

        if (itemDamage < 0)
        {
            itemDamage = 0;
        }
    }

    /**
     * Returns the max damage an item in the stack can take.
     */
    public int getMaxDamage()
    {
        return item.getMaxDamage();
    }

    /**
     * Attempts to damage the ItemStack with par1 amount of damage, If the
     * ItemStack has the Unbreaking enchantment there is a chance for each point
     * of damage to be negated. Returns true if it takes more damage than
     * getMaxDamage(). Returns false otherwise or if the ItemStack can't be
     * damaged or if all points of damage are negated.
     */
    public boolean attemptDamageItem(int par1, Random par2Random)
    {
        if (!isItemStackDamageable())
        {
            return false;
        }
        else
        {
            if (par1 > 0)
            {
                int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
                int var4 = 0;

                for (int var5 = 0; var3 > 0 && var5 < par1; ++var5)
                {
                    if (EnchantmentDurability.negateDamage(this, var3, par2Random))
                    {
                        ++var4;
                    }
                }

                par1 -= var4;

                if (par1 <= 0) { return false; }
            }

            itemDamage += par1;
            return itemDamage > getMaxDamage();
        }
    }

    /**
     * Damages the item in the ItemStack
     */
    public void damageItem(int damageAmount, EntityLivingBase entityLivingBase)
    {
        if (!(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).capabilities.isCreativeMode)
        {
            if (isItemStackDamageable())
            {
                if (attemptDamageItem(damageAmount, entityLivingBase.getRNG()))
                {
                    entityLivingBase.renderBrokenItemStack(this);
                    --stackSize;

                    if (entityLivingBase instanceof EntityPlayer)
                    {
                        EntityPlayer var3 = (EntityPlayer)entityLivingBase;
                        var3.addStat(StatList.objectBreakStats[Item.getIdFromItem(item)], 1);

                        if (stackSize == 0 && getItem() instanceof ItemBow)
                        {
                            var3.destroyCurrentEquippedItem();
                        }
                    }

                    if (stackSize < 0)
                    {
                        stackSize = 0;
                    }

                    itemDamage = 0;
                }
            }
        }
    }

    /**
     * Calls the corresponding fct in di
     */
    public void hitEntity(EntityLivingBase entityLivingBase, EntityPlayer player)
    {
        if (item.hitEntity(this, entityLivingBase, player))
        {
            player.addStat(StatList.objectUseStats[Item.getIdFromItem(item)], 1);
        }
    }

    public void func_150999_a(World p_150999_1_, Block p_150999_2_, int p_150999_3_, int p_150999_4_, int p_150999_5_, EntityPlayer p_150999_6_)
    {
        boolean var7 = item.onBlockDestroyed(this, p_150999_1_, p_150999_2_, p_150999_3_, p_150999_4_, p_150999_5_, p_150999_6_);

        if (var7)
        {
            p_150999_6_.addStat(StatList.objectUseStats[Item.getIdFromItem(item)], 1);
        }
    }

    public boolean func_150998_b(Block p_150998_1_)
    {
        return item.func_150897_b(p_150998_1_);
    }

    public boolean interactWithEntity(EntityPlayer par1EntityPlayer, EntityLivingBase par2EntityLivingBase)
    {
        return item.itemInteractionForEntity(this, par1EntityPlayer, par2EntityLivingBase);
    }

    /**
     * Returns a new stack with the same properties.
     */
    public ItemStack copy()
    {
        ItemStack itemStack = new ItemStack(item, stackSize, itemDamage);

        if (stackTagCompound != null)
        {
            itemStack.stackTagCompound = (NBTTagCompound)stackTagCompound.copy();
        }

        return itemStack;
    }

    public static boolean areItemStackTagsEqual(ItemStack itemStack1, ItemStack itemStack2)
    {
        return itemStack1 == null && itemStack2 == null ? true : (itemStack1 != null && itemStack2 != null ? (itemStack1.stackTagCompound == null && itemStack2.stackTagCompound != null ? false : itemStack1.stackTagCompound == null || itemStack1.stackTagCompound.equals(itemStack2.stackTagCompound)) : false);
    }

    /**
     * compares ItemStack argument1 with ItemStack argument2; returns true if
     * both ItemStacks are equal
     */
    public static boolean areItemStacksEqual(ItemStack itemStack1, ItemStack itemStack2)
    {
        return itemStack1 == null && itemStack2 == null ? true : (itemStack1 != null && itemStack2 != null ? itemStack1.isItemStackEqual(itemStack2) : false);
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if
     * both ItemStacks are equal
     */
    private boolean isItemStackEqual(ItemStack par1ItemStack)
    {
        return stackSize != par1ItemStack.stackSize ? false : (item != par1ItemStack.item ? false : (itemDamage != par1ItemStack.itemDamage ? false : (stackTagCompound == null && par1ItemStack.stackTagCompound != null ? false : stackTagCompound == null || stackTagCompound.equals(par1ItemStack.stackTagCompound))));
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if
     * the Items contained in both ItemStacks are equal
     */
    public boolean isItemEqual(ItemStack par1ItemStack)
    {
        return item == par1ItemStack.item && itemDamage == par1ItemStack.itemDamage;
    }

    public String getUnlocalizedName()
    {
        return item.getUnlocalizedName(this);
    }

    /**
     * Creates a copy of a ItemStack, a null parameters will return a null.
     */
    public static ItemStack copyItemStack(ItemStack itemStack)
    {
        return itemStack == null ? null : itemStack.copy();
    }

    public String toString()
    {
        return stackSize + "x" + item.getUnlocalizedName() + "@" + itemDamage;
    }

    /**
     * Called each tick as long the ItemStack in on player inventory. Used to
     * progress the pickup animation and update maps.
     */
    public void updateAnimation(World world, Entity entity, int par3, boolean par4)
    {
        if (animationsToGo > 0)
        {
            --animationsToGo;
        }

        item.onUpdate(this, world, entity, par3, par4);
    }

    public void onCrafting(World par1World, EntityPlayer par2EntityPlayer, int par3)
    {
        par2EntityPlayer.addStat(StatList.objectCraftStats[Item.getIdFromItem(item)], par3);
        item.onCreated(this, par1World, par2EntityPlayer);
    }

    public int getMaxItemUseDuration()
    {
        return getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction()
    {
        return getItem().getItemUseAction(this);
    }

    /**
     * Called when the player releases the use item button. Args: world,
     * entityplayer, itemInUseCount
     */
    public void onPlayerStoppedUsing(World world, EntityPlayer player, int itemInUseCount)
    {
        getItem().onPlayerStoppedUsing(this, world, player, itemInUseCount);
    }

    /**
     * Returns true if the ItemStack has an NBTTagCompound. Currently used to
     * store enchantments.
     */
    public boolean hasTagCompound()
    {
        return stackTagCompound != null;
    }

    /**
     * Returns the NBTTagCompound of the ItemStack.
     */
    public NBTTagCompound getTagCompound()
    {
        return stackTagCompound;
    }

    public NBTTagList getEnchantmentTagList()
    {
        return stackTagCompound == null ? null : stackTagCompound.getTagList("ench", 10);
    }

    /**
     * Assigns a NBTTagCompound to the ItemStack, minecraft validates that only
     * non-stackable items can have it.
     */
    public void setTagCompound(NBTTagCompound tagCompound)
    {
        stackTagCompound = tagCompound;
    }

    /**
     * returns the display name of the itemstack
     */
    public String getDisplayName()
    {
        String var1 = getItem().getItemStackDisplayName(this);

        if (stackTagCompound != null && stackTagCompound.func_150297_b("display", 10))
        {
            NBTTagCompound var2 = stackTagCompound.getCompoundTag("display");

            if (var2.func_150297_b("Name", 8))
            {
                var1 = var2.getString("Name");
            }
        }

        return var1;
    }

    public ItemStack setStackDisplayName(String name)
    {
        if (stackTagCompound == null)
        {
            stackTagCompound = new NBTTagCompound();
        }

        if (!stackTagCompound.func_150297_b("display", 10))
        {
            stackTagCompound.setTag("display", new NBTTagCompound());
        }

        stackTagCompound.getCompoundTag("display").setString("Name", name);
        return this;
    }

    public void func_135074_t()
    {
        if (stackTagCompound != null)
        {
            if (stackTagCompound.func_150297_b("display", 10))
            {
                NBTTagCompound var1 = stackTagCompound.getCompoundTag("display");
                var1.removeTag("Name");

                if (var1.hasNoTags())
                {
                    stackTagCompound.removeTag("display");

                    if (stackTagCompound.hasNoTags())
                    {
                        setTagCompound((NBTTagCompound)null);
                    }
                }
            }
        }
    }

    /**
     * Returns true if the itemstack has a display name
     */
    public boolean hasDisplayName()
    {
        return stackTagCompound == null ? false : (!stackTagCompound.func_150297_b("display", 10) ? false : stackTagCompound.getCompoundTag("display").func_150297_b("Name", 8));
    }

    public EnumRarity getRarity()
    {
        return getItem().getRarity(this);
    }

    /**
     * True if it is a tool and has no enchantments to begin with
     */
    public boolean isItemEnchantable()
    {
        return !getItem().isItemTool(this) ? false : !isItemEnchanted();
    }

    /**
     * Adds an enchantment with a desired level on the ItemStack.
     */
    public void addEnchantment(Enchantment enchantment, int level)
    {
        if (stackTagCompound == null)
        {
            setTagCompound(new NBTTagCompound());
        }

        if (!stackTagCompound.func_150297_b("ench", 9))
        {
            stackTagCompound.setTag("ench", new NBTTagList());
        }

        NBTTagList var3 = stackTagCompound.getTagList("ench", 10);
        NBTTagCompound var4 = new NBTTagCompound();
        var4.setShort("id", (short)enchantment.effectId);
        var4.setShort("lvl", ((byte)level));
        var3.appendTag(var4);
    }

    /**
     * True if the item has enchantment data
     */
    public boolean isItemEnchanted()
    {
        return stackTagCompound != null && stackTagCompound.func_150297_b("ench", 9);
    }

    public void setTagInfo(String par1Str, NBTBase par2NBTBase)
    {
        if (stackTagCompound == null)
        {
            setTagCompound(new NBTTagCompound());
        }

        stackTagCompound.setTag(par1Str, par2NBTBase);
    }

    public boolean canEditBlocks()
    {
        return getItem().canItemEditBlocks();
    }

    /**
     * Return whether this stack is on an item frame.
     */
    public boolean isOnItemFrame()
    {
        return itemFrame != null;
    }

    /**
     * Set the item frame this stack is on.
     */
    public void setItemFrame(EntityItemFrame newItemFrame)
    {
        itemFrame = newItemFrame;
    }

    /**
     * Return the item frame this stack is on. Returns null if not on an item
     * frame.
     */
    public EntityItemFrame getItemFrame()
    {
        return itemFrame;
    }

    /**
     * Get this stack's repair cost, or 0 if no repair cost is defined.
     */
    public int getRepairCost()
    {
        return hasTagCompound() && stackTagCompound.func_150297_b("RepairCost", 3) ? stackTagCompound.getInteger("RepairCost") : 0;
    }

    /**
     * Set this stack's repair cost.
     */
    public void setRepairCost(int par1)
    {
        if (!hasTagCompound())
        {
            stackTagCompound = new NBTTagCompound();
        }

        stackTagCompound.setInteger("RepairCost", par1);
    }

    /**
     * Gets the attribute modifiers for this ItemStack.\nWill check for an NBT
     * tag list containing modifiers for the stack.
     */
    public Multimap getAttributeModifiers()
    {
        Object multimap;

        if (hasTagCompound() && stackTagCompound.func_150297_b("AttributeModifiers", 9))
        {
            multimap = HashMultimap.create();
            NBTTagList var2 = stackTagCompound.getTagList("AttributeModifiers", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                AttributeModifier var5 = SharedMonsterAttributes.readAttributeModifierFromNBT(var4);

                if (var5.getID().getLeastSignificantBits() != 0L && var5.getID().getMostSignificantBits() != 0L)
                {
                    ((Multimap)multimap).put(var4.getString("AttributeName"), var5);
                }
            }
        }
        else
        {
            multimap = getItem().getItemAttributeModifiers();
        }

        return (Multimap)multimap;
    }

    public void setItem(Item newItem)
    {
        item = newItem;
    }

    public IChatComponent getFormattedItemName()
    {
        IChatComponent var1 = (new ChatComponentText("[")).appendText(getDisplayName()).appendText("]");

        if (item != null)
        {
            NBTTagCompound var2 = new NBTTagCompound();
            writeToNBT(var2);
            var1.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(var2.toString())));
            var1.getChatStyle().setColor(getRarity().field_77937_e);
        }

        return var1;
    }

    public int getItemId()
    {
        return Item.getIdFromItem(getItem());
    }
}
