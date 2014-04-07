package net.minecraft.event;

import java.util.Map;

import net.minecraft.util.IChatComponent;

import com.google.common.collect.Maps;

public class HoverEvent
{
    private final HoverEvent.Action action;
    private final IChatComponent value;
    private static final String __OBFID = "CL_00001264";

    public HoverEvent(HoverEvent.Action p_i45158_1_, IChatComponent p_i45158_2_)
    {
        action = p_i45158_1_;
        value = p_i45158_2_;
    }

    /**
     * Gets the action to perform when this event is raised.
     */
    public HoverEvent.Action getAction()
    {
        return action;
    }

    /**
     * Gets the value to perform the action on when this event is raised. For
     * example, if the action is "show item", this would be the item to show.
     */
    public IChatComponent getValue()
    {
        return value;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            HoverEvent var2 = (HoverEvent)par1Obj;

            if (action != var2.action)
            {
                return false;
            }
            else
            {
                if (value != null)
                {
                    if (!value.equals(var2.value)) { return false; }
                }
                else if (var2.value != null) { return false; }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return "HoverEvent{action=" + action + ", value=\'" + value + '\'' + '}';
    }

    public int hashCode()
    {
        int var1 = action.hashCode();
        var1 = 31 * var1 + (value != null ? value.hashCode() : 0);
        return var1;
    }

    public static enum Action
    {
        SHOW_TEXT("SHOW_TEXT", 0, "show_text", true), SHOW_ACHIEVEMENT("SHOW_ACHIEVEMENT", 1, "show_achievement", true), SHOW_ITEM("SHOW_ITEM", 2, "show_item", true);
        private static final Map nameMapping = Maps.newHashMap();
        private final boolean allowedInChat;
        private final String canonicalName;

        private static final HoverEvent.Action[] $VALUES = new HoverEvent.Action[] {SHOW_TEXT, SHOW_ACHIEVEMENT, SHOW_ITEM};
        private static final String __OBFID = "CL_00001265";

        private Action(String p_i45157_1_, int p_i45157_2_, String p_i45157_3_, boolean p_i45157_4_)
        {
            canonicalName = p_i45157_3_;
            allowedInChat = p_i45157_4_;
        }

        public boolean shouldAllowInChat()
        {
            return allowedInChat;
        }

        public String getCanonicalName()
        {
            return canonicalName;
        }

        public static HoverEvent.Action getValueByCanonicalName(String p_150684_0_)
        {
            return (HoverEvent.Action)nameMapping.get(p_150684_0_);
        }

        static
        {
            HoverEvent.Action[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                HoverEvent.Action var3 = var0[var2];
                nameMapping.put(var3.getCanonicalName(), var3);
            }
        }
    }
}
