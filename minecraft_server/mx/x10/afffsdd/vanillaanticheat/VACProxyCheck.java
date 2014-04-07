package mx.x10.afffsdd.vanillaanticheat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.minecraft.server.MinecraftServer;

public class VACProxyCheck
{
    /**
     * Checks if a IP is a proxy or not.
     * 
     * @param ip
     *            the IP to check
     * @param mode
     *            how aggressive we are about checking.
     *            0 = default, unsuccessful check = not proxy
     *            1 = aggressive, unsuccessful check = proxy
     *            0 is recommended
     * @return if the IP is a proxy or not
     * @throws Exception
     */
    public static boolean isProxy(String ip, int mode)
    {
        URL url;
        String finalIp = ip;

        try
        {
            if (finalIp.startsWith("/"))
                finalIp = finalIp.substring(1);
            if (ip.contains(":"))
                finalIp = finalIp.split(":")[0];

            url = new URL("http://www.stopforumspam.com/api?ip=" + finalIp);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<appears>"))
                {
                    String result = line.split("<appears>")[1].split("</appears>")[0];
                    if (result == "yes")
                        return true;
                    else if(result == "no")
                        return false;
                }
            }
        }
        catch (IOException | ArrayIndexOutOfBoundsException e)
        {
            MinecraftServer.getServer().logSevere("Could not check if IP " + finalIp + " is a proxy: " + e.getMessage());
            e.printStackTrace();
        }
        switch(mode)
        {
        case 0:
            return false;
        case 1:
            return true;
        default:
            return false;
        }
    }
}
