package net.minecraft.creativetab;

import net.minecraft.enchantment.EnumEnchantmentType;

public abstract class CreativeTabs
{
    public static final CreativeTabs[] creativeTabArray = new CreativeTabs[12];
    public static final CreativeTabs tabBlock = new CreativeTabs(0, "buildingBlocks")
    {
        private static final String __OBFID = "CL_00000010";
    };
    public static final CreativeTabs tabDecorations = new CreativeTabs(1, "decorations")
    {
        private static final String __OBFID = "CL_00000011";
    };
    public static final CreativeTabs tabRedstone = new CreativeTabs(2, "redstone")
    {
        private static final String __OBFID = "CL_00000012";
    };
    public static final CreativeTabs tabTransport = new CreativeTabs(3, "transportation")
    {
        private static final String __OBFID = "CL_00000014";
    };
    public static final CreativeTabs tabMisc = (new CreativeTabs(4, "misc")
    {
        private static final String __OBFID = "CL_00000015";
    }).func_111229_a(new EnumEnchantmentType[] {EnumEnchantmentType.all});
    public static final CreativeTabs tabAllSearch = (new CreativeTabs(5, "search")
    {
        private static final String __OBFID = "CL_00000016";
    }).setBackgroundImageName("item_search.png");
    public static final CreativeTabs tabFood = new CreativeTabs(6, "food")
    {
        private static final String __OBFID = "CL_00000017";
    };
    public static final CreativeTabs tabTools = (new CreativeTabs(7, "tools")
    {
        private static final String __OBFID = "CL_00000018";
    }).func_111229_a(new EnumEnchantmentType[] {EnumEnchantmentType.digger, EnumEnchantmentType.fishing_rod, EnumEnchantmentType.breakable});
    public static final CreativeTabs tabCombat = (new CreativeTabs(8, "combat")
    {
        private static final String __OBFID = "CL_00000007";
    }).func_111229_a(new EnumEnchantmentType[] {EnumEnchantmentType.armor, EnumEnchantmentType.armor_feet, EnumEnchantmentType.armor_head, EnumEnchantmentType.armor_legs, EnumEnchantmentType.armor_torso, EnumEnchantmentType.bow, EnumEnchantmentType.weapon});
    public static final CreativeTabs tabBrewing = new CreativeTabs(9, "brewing")
    {
        private static final String __OBFID = "CL_00000008";
    };
    public static final CreativeTabs tabMaterials = new CreativeTabs(10, "materials")
    {
        private static final String __OBFID = "CL_00000009";
    };
    public static final CreativeTabs tabInventory = (new CreativeTabs(11, "inventory")
    {
        private static final String __OBFID = "CL_00000006";
    }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;

    /** Texture to use. */
    private String theTexture = "items.png";
    private boolean hasScrollbar = true;

    /** Whether to draw the title in the foreground of the creative GUI */
    private boolean drawTitle = true;
    private EnumEnchantmentType[] field_111230_s;
    private static final String __OBFID = "CL_00000005";

    public CreativeTabs(int par1, String par2Str)
    {
        this.tabIndex = par1;
        this.tabLabel = par2Str;
        creativeTabArray[par1] = this;
    }

    public CreativeTabs setBackgroundImageName(String par1Str)
    {
        this.theTexture = par1Str;
        return this;
    }

    public CreativeTabs setNoTitle()
    {
        this.drawTitle = false;
        return this;
    }

    public CreativeTabs setNoScrollbar()
    {
        this.hasScrollbar = false;
        return this;
    }

    public CreativeTabs func_111229_a(EnumEnchantmentType ... par1ArrayOfEnumEnchantmentType)
    {
        this.field_111230_s = par1ArrayOfEnumEnchantmentType;
        return this;
    }
}
