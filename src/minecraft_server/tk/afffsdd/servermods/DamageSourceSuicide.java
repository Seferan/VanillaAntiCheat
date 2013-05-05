package tk.afffsdd.servermods;

import net.minecraft.src.DamageSource;

public class DamageSourceSuicide extends DamageSource
{
	
	public DamageSourceSuicide()
	{
		super("suicide");
		setDamageBypassesArmor();
		setDamageAllowedInCreativeMode();
	}
}
