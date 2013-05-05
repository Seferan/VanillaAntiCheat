package tk.afffsdd.vanillaanticheat;

import net.minecraft.src.CommandVersion;

public class VanillaAntiCheat
{
	public static String version = "1.4";
	public static String[] authors = {"afffsdd", "ah33t3r"};
	
	public static String getAuthors()
	{
		StringBuilder authorsStr = new StringBuilder(64);
		for(int i = 0; i < authors.length; i++)
		{
			authorsStr.append(authors[i]);
			if(i != authors.length - 1) //hammer time? No, not Hammer Time. Grammar Time.
			{
				if(i == authors.length - 2)
				{
					if(authors.length != 2)
					{
						authorsStr.append(',');
					}
					authorsStr.append(" and ");
				} else {
					authorsStr.append(", ");
				}
			}
		}
		return authorsStr.toString();
	}
}
