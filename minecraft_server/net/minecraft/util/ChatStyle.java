package net.minecraft.util;

import java.lang.reflect.Type;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ChatStyle
{
    /**
     * The parent of this ChatStyle. Used for looking up values that this
     * instance does not override.
     */
    private ChatStyle parentStyle;
    private EnumChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent chatClickEvent;
    private HoverEvent chatHoverEvent;

    /**
     * The base of the ChatStyle hierarchy. All ChatStyle instances are
     * implicitly children of this.
     */
    private static final ChatStyle rootStyle = new ChatStyle()
    {
        private static final String __OBFID = "CL_00001267";

        public EnumChatFormatting getColor()
        {
            return null;
        }

        public boolean getBold()
        {
            return false;
        }

        public boolean getItalic()
        {
            return false;
        }

        public boolean getStrikethrough()
        {
            return false;
        }

        public boolean getUnderlined()
        {
            return false;
        }

        public boolean getObfuscated()
        {
            return false;
        }

        public ClickEvent getChatClickEvent()
        {
            return null;
        }

        public HoverEvent getChatHoverEvent()
        {
            return null;
        }

        public ChatStyle setColor(EnumChatFormatting p_150238_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setBold(Boolean p_150227_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setItalic(Boolean p_150217_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setStrikethrough(Boolean p_150225_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setUnderlined(Boolean p_150228_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setObfuscated(Boolean p_150237_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setChatClickEvent(ClickEvent p_150241_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setChatHoverEvent(HoverEvent p_150209_1_)
        {
            throw new UnsupportedOperationException();
        }

        public ChatStyle setParentStyle(ChatStyle p_150221_1_)
        {
            throw new UnsupportedOperationException();
        }

        public String toString()
        {
            return "Style.ROOT";
        }

        public ChatStyle createShallowCopy()
        {
            return this;
        }

        public ChatStyle createDeepCopy()
        {
            return this;
        }
    };
    private static final String __OBFID = "CL_00001266";

    /**
     * Gets the effective color of this ChatStyle.
     */
    public EnumChatFormatting getColor()
    {
        return color == null ? getParent().getColor() : color;
    }

    /**
     * Whether or not text of this ChatStyle should be in bold.
     */
    public boolean getBold()
    {
        return bold == null ? getParent().getBold() : bold.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be italicized.
     */
    public boolean getItalic()
    {
        return italic == null ? getParent().getItalic() : italic.booleanValue();
    }

    /**
     * Whether or not to format text of this ChatStyle using strikethrough.
     */
    public boolean getStrikethrough()
    {
        return strikethrough == null ? getParent().getStrikethrough() : strikethrough.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be underlined.
     */
    public boolean getUnderlined()
    {
        return underlined == null ? getParent().getUnderlined() : underlined.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be obfuscated.
     */
    public boolean getObfuscated()
    {
        return obfuscated == null ? getParent().getObfuscated() : obfuscated.booleanValue();
    }

    /**
     * Whether or not this style is empty (inherits everything from the parent).
     */
    public boolean isEmpty()
    {
        return bold == null && italic == null && strikethrough == null && underlined == null && obfuscated == null && color == null && chatClickEvent == null && chatHoverEvent == null;
    }

    /**
     * The effective chat click event.
     */
    public ClickEvent getChatClickEvent()
    {
        return chatClickEvent == null ? getParent().getChatClickEvent() : chatClickEvent;
    }

    /**
     * The effective chat hover event.
     */
    public HoverEvent getChatHoverEvent()
    {
        return chatHoverEvent == null ? getParent().getChatHoverEvent() : chatHoverEvent;
    }

    /**
     * Sets the color for this ChatStyle to the given value. Only use color
     * values for this; set other values using the specific methods.
     */
    public ChatStyle setColor(EnumChatFormatting p_150238_1_)
    {
        color = p_150238_1_;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be in bold. Set to
     * false if, e.g., the parent style is bold and you want text of this style
     * to be unbolded.
     */
    public ChatStyle setBold(Boolean p_150227_1_)
    {
        bold = p_150227_1_;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be italicized. Set to
     * false if, e.g., the parent style is italicized and you want to override
     * that for this style.
     */
    public ChatStyle setItalic(Boolean p_150217_1_)
    {
        italic = p_150217_1_;
        return this;
    }

    /**
     * Sets whether or not to format text of this ChatStyle using strikethrough.
     * Set to false if, e.g., the parent style uses strikethrough and you want
     * to override that for this style.
     */
    public ChatStyle setStrikethrough(Boolean p_150225_1_)
    {
        strikethrough = p_150225_1_;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be underlined. Set to
     * false if, e.g., the parent style is underlined and you want to override
     * that for this style.
     */
    public ChatStyle setUnderlined(Boolean p_150228_1_)
    {
        underlined = p_150228_1_;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be obfuscated. Set to
     * false if, e.g., the parent style is obfuscated and you want to override
     * that for this style.
     */
    public ChatStyle setObfuscated(Boolean p_150237_1_)
    {
        obfuscated = p_150237_1_;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is clicked
     * on.
     */
    public ChatStyle setChatClickEvent(ClickEvent p_150241_1_)
    {
        chatClickEvent = p_150241_1_;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is hovered
     * over.
     */
    public ChatStyle setChatHoverEvent(HoverEvent p_150209_1_)
    {
        chatHoverEvent = p_150209_1_;
        return this;
    }

    /**
     * Sets the fallback ChatStyle to use if this ChatStyle does not override
     * some value. Without a parent, obvious defaults are used (bold: false,
     * underlined: false, etc).
     */
    public ChatStyle setParentStyle(ChatStyle p_150221_1_)
    {
        parentStyle = p_150221_1_;
        return this;
    }

    /**
     * Gets the immediate parent of this ChatStyle.
     */
    private ChatStyle getParent()
    {
        return parentStyle == null ? rootStyle : parentStyle;
    }

    public String toString()
    {
        return "Style{hasParent=" + (parentStyle != null) + ", color=" + color + ", bold=" + bold + ", italic=" + italic + ", underlined=" + underlined + ", obfuscated=" + obfuscated + ", clickEvent=" + getChatClickEvent() + ", hoverEvent=" + getChatHoverEvent() + '}';
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ChatStyle))
        {
            return false;
        }
        else
        {
            ChatStyle var2 = (ChatStyle)par1Obj;
            boolean var10000;

            if (getBold() == var2.getBold() && getColor() == var2.getColor() && getItalic() == var2.getItalic() && getObfuscated() == var2.getObfuscated() && getStrikethrough() == var2.getStrikethrough() && getUnderlined() == var2.getUnderlined())
            {
                label56:
                {
                    if (getChatClickEvent() != null)
                    {
                        if (!getChatClickEvent().equals(var2.getChatClickEvent()))
                        {
                            break label56;
                        }
                    }
                    else if (var2.getChatClickEvent() != null)
                    {
                        break label56;
                    }

                    if (getChatHoverEvent() != null)
                    {
                        if (!getChatHoverEvent().equals(var2.getChatHoverEvent()))
                        {
                            break label56;
                        }
                    }
                    else if (var2.getChatHoverEvent() != null)
                    {
                        break label56;
                    }

                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public int hashCode()
    {
        int var1 = color.hashCode();
        var1 = 31 * var1 + bold.hashCode();
        var1 = 31 * var1 + italic.hashCode();
        var1 = 31 * var1 + underlined.hashCode();
        var1 = 31 * var1 + strikethrough.hashCode();
        var1 = 31 * var1 + obfuscated.hashCode();
        var1 = 31 * var1 + chatClickEvent.hashCode();
        var1 = 31 * var1 + chatHoverEvent.hashCode();
        return var1;
    }

    /**
     * Creates a shallow copy of this style. Changes to this instance's values
     * will not be reflected in the copy, but changes to the parent style's
     * values WILL be reflected in both this instance and the copy, wherever
     * either does not override a value.
     */
    public ChatStyle createShallowCopy()
    {
        ChatStyle var1 = new ChatStyle();
        var1.bold = bold;
        var1.italic = italic;
        var1.strikethrough = strikethrough;
        var1.underlined = underlined;
        var1.obfuscated = obfuscated;
        var1.color = color;
        var1.chatClickEvent = chatClickEvent;
        var1.chatHoverEvent = chatHoverEvent;
        var1.parentStyle = parentStyle;
        return var1;
    }

    /**
     * Creates a deep copy of this style. No changes to this instance or its
     * parent style will be reflected in the copy.
     */
    public ChatStyle createDeepCopy()
    {
        ChatStyle var1 = new ChatStyle();
        var1.setBold(Boolean.valueOf(getBold()));
        var1.setItalic(Boolean.valueOf(getItalic()));
        var1.setStrikethrough(Boolean.valueOf(getStrikethrough()));
        var1.setUnderlined(Boolean.valueOf(getUnderlined()));
        var1.setObfuscated(Boolean.valueOf(getObfuscated()));
        var1.setColor(getColor());
        var1.setChatClickEvent(getChatClickEvent());
        var1.setChatHoverEvent(getChatHoverEvent());
        return var1;
    }

    public static class Serializer implements JsonDeserializer, JsonSerializer
    {
        private static final String __OBFID = "CL_00001268";

        public ChatStyle deserialize(JsonElement p_150204_1_, Type p_150204_2_, JsonDeserializationContext p_150204_3_)
        {
            if (p_150204_1_.isJsonObject())
            {
                ChatStyle var4 = new ChatStyle();
                JsonObject var5 = p_150204_1_.getAsJsonObject();

                if (var5.has("bold"))
                {
                    var4.bold = Boolean.valueOf(var5.get("bold").getAsBoolean());
                }

                if (var5.has("italic"))
                {
                    var4.italic = Boolean.valueOf(var5.get("italic").getAsBoolean());
                }

                if (var5.has("underlined"))
                {
                    var4.underlined = Boolean.valueOf(var5.get("underlined").getAsBoolean());
                }

                if (var5.has("strikethrough"))
                {
                    var4.strikethrough = Boolean.valueOf(var5.get("strikethrough").getAsBoolean());
                }

                if (var5.has("obfuscated"))
                {
                    var4.obfuscated = Boolean.valueOf(var5.get("obfuscated").getAsBoolean());
                }

                if (var5.has("color"))
                {
                    var4.color = (EnumChatFormatting)p_150204_3_.deserialize(var5.get("color"), EnumChatFormatting.class);
                }

                JsonObject var6;

                if (var5.has("clickEvent"))
                {
                    var6 = var5.getAsJsonObject("clickEvent");
                    ClickEvent.Action var7 = ClickEvent.Action.getValueByCanonicalName(var6.getAsJsonPrimitive("action").getAsString());
                    String var8 = var6.getAsJsonPrimitive("value").getAsString();

                    if (var7 != null && var8 != null && var7.shouldAllowInChat())
                    {
                        var4.chatClickEvent = new ClickEvent(var7, var8);
                    }
                }

                if (var5.has("hoverEvent"))
                {
                    var6 = var5.getAsJsonObject("hoverEvent");
                    HoverEvent.Action var9 = HoverEvent.Action.getValueByCanonicalName(var6.getAsJsonPrimitive("action").getAsString());
                    IChatComponent var10 = (IChatComponent)p_150204_3_.deserialize(var6.get("value"), IChatComponent.class);

                    if (var9 != null && var10 != null && var9.shouldAllowInChat())
                    {
                        var4.chatHoverEvent = new HoverEvent(var9, var10);
                    }
                }

                return var4;
            }
            else
            {
                return null;
            }
        }

        public JsonElement serialize(ChatStyle p_150203_1_, Type p_150203_2_, JsonSerializationContext p_150203_3_)
        {
            if (p_150203_1_.isEmpty())
            {
                return null;
            }
            else
            {
                JsonObject var4 = new JsonObject();

                if (p_150203_1_.bold != null)
                {
                    var4.addProperty("bold", p_150203_1_.bold);
                }

                if (p_150203_1_.italic != null)
                {
                    var4.addProperty("italic", p_150203_1_.italic);
                }

                if (p_150203_1_.underlined != null)
                {
                    var4.addProperty("underlined", p_150203_1_.underlined);
                }

                if (p_150203_1_.strikethrough != null)
                {
                    var4.addProperty("strikethrough", p_150203_1_.strikethrough);
                }

                if (p_150203_1_.obfuscated != null)
                {
                    var4.addProperty("obfuscated", p_150203_1_.obfuscated);
                }

                if (p_150203_1_.color != null)
                {
                    var4.add("color", p_150203_3_.serialize(p_150203_1_.color));
                }

                JsonObject var5;

                if (p_150203_1_.chatClickEvent != null)
                {
                    var5 = new JsonObject();
                    var5.addProperty("action", p_150203_1_.chatClickEvent.getAction().getCanonicalName());
                    var5.addProperty("value", p_150203_1_.chatClickEvent.getValue());
                    var4.add("clickEvent", var5);
                }

                if (p_150203_1_.chatHoverEvent != null)
                {
                    var5 = new JsonObject();
                    var5.addProperty("action", p_150203_1_.chatHoverEvent.getAction().getCanonicalName());
                    var5.add("value", p_150203_3_.serialize(p_150203_1_.chatHoverEvent.getValue()));
                    var4.add("hoverEvent", var5);
                }

                return var4;
            }
        }

        public JsonElement serialize(Object par1Obj, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
        {
            return this.serialize((ChatStyle)par1Obj, par2Type, par3JsonSerializationContext);
        }
    }
}
