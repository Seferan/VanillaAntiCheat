package net.minecraft.network.rcon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.server.MinecraftServer;

public class RConThreadQuery extends RConThreadBase
{
    /** The time of the last client auth check */
    private long lastAuthCheckTime;

    /** The RCon query port */
    private int queryPort;

    /** Port the server is running on */
    private int serverPort;

    /** The maximum number of players allowed on the server */
    private int maxPlayers;

    /** The current server message of the day */
    private String serverMotd;

    /** The name of the currently loaded world */
    private String worldName;

    /** The remote socket querying the server */
    private DatagramSocket querySocket;

    /** A buffer for incoming DatagramPackets */
    private byte[] buffer = new byte[1460];

    /** Storage for incoming DatagramPackets */
    private DatagramPacket incomingPacket;
    private Map field_72644_p;

    /** The hostname of this query server */
    private String queryHostname;

    /** The hostname of the running server */
    private String serverHostname;

    /** A map of SocketAddress objects to RConThreadQueryAuth objects */
    private Map queryClients;

    /**
     * The time that this RConThreadQuery was constructed, from (new
     * Date()).getTime()
     */
    private long time;

    /** The RConQuery output stream */
    private RConOutputStream output;

    /** The time of the last query response sent */
    private long lastQueryResponseTime;
    private static final String __OBFID = "CL_00001802";

    public RConThreadQuery(IServer par1IServer)
    {
        super(par1IServer, "Query Listener");
        queryPort = par1IServer.getIntProperty("query.port", 0);
        serverHostname = par1IServer.getHostname();
        serverPort = par1IServer.getPort();
        serverMotd = par1IServer.getMotd();
        maxPlayers = par1IServer.getMaxPlayers();
        worldName = par1IServer.getFolderName();
        lastQueryResponseTime = 0L;
        queryHostname = "0.0.0.0";

        if (0 != serverHostname.length() && !queryHostname.equals(serverHostname))
        {
            queryHostname = serverHostname;
        }
        else
        {
            serverHostname = "0.0.0.0";

            try
            {
                InetAddress var2 = InetAddress.getLocalHost();
                queryHostname = var2.getHostAddress();
            }
            catch (UnknownHostException var3)
            {
                logWarning("Unable to determine local host IP, please set server-ip in \'" + par1IServer.getSettingsFilename() + "\' : " + var3.getMessage());
            }
        }

        if (0 == queryPort)
        {
            queryPort = serverPort;
            logInfo("Setting default query port to " + queryPort);
            par1IServer.setProperty("query.port", Integer.valueOf(queryPort));
            par1IServer.setProperty("debug", Boolean.valueOf(false));
            par1IServer.saveProperties();
        }

        field_72644_p = new HashMap();
        output = new RConOutputStream(1460);
        queryClients = new HashMap();
        time = (new Date()).getTime();
    }

    /**
     * Sends a byte array as a DatagramPacket response to the client who sent
     * the given DatagramPacket
     */
    private void sendResponsePacket(byte[] par1ArrayOfByte, DatagramPacket par2DatagramPacket) throws IOException
    {
        querySocket.send(new DatagramPacket(par1ArrayOfByte, par1ArrayOfByte.length, par2DatagramPacket.getSocketAddress()));
    }

    /**
     * Parses an incoming DatagramPacket, returning true if the packet was valid
     */
    private boolean parseIncomingPacket(DatagramPacket par1DatagramPacket) throws IOException
    {
        byte[] var2 = par1DatagramPacket.getData();
        int var3 = par1DatagramPacket.getLength();
        SocketAddress var4 = par1DatagramPacket.getSocketAddress();
        logDebug("Packet len " + var3 + " [" + var4 + "]");

        if (3 <= var3 && -2 == var2[0] && -3 == var2[1])
        {
            logDebug("Packet \'" + RConUtils.getByteAsHexString(var2[2]) + "\' [" + var4 + "]");

            switch (var2[2])
            {
            case 0:
                if (!verifyClientAuth(par1DatagramPacket).booleanValue())
                {
                    logDebug("Invalid challenge [" + var4 + "]");
                    return false;
                }
                else if (15 == var3)
                {
                    sendResponsePacket(createQueryResponse(par1DatagramPacket), par1DatagramPacket);
                    logDebug("Rules [" + var4 + "]");
                }
                else
                {
                    RConOutputStream var5 = new RConOutputStream(1460);
                    var5.writeInt(0);
                    var5.writeByteArray(getRequestID(par1DatagramPacket.getSocketAddress()));
                    var5.writeString(serverMotd);
                    var5.writeString("SMP");
                    var5.writeString(worldName);
                    var5.writeString(Integer.toString(getNumberOfPlayers()));
                    var5.writeString(Integer.toString(maxPlayers));
                    var5.writeShort((short)serverPort);
                    var5.writeString(queryHostname);
                    sendResponsePacket(var5.toByteArray(), par1DatagramPacket);
                    logDebug("Status [" + var4 + "]");
                }

            case 9:
                sendAuthChallenge(par1DatagramPacket);
                logDebug("Challenge [" + var4 + "]");
                return true;

            default:
                return true;
            }
        }
        else
        {
            logDebug("Invalid packet [" + var4 + "]");
            return false;
        }
    }

    /**
     * Creates a query response as a byte array for the specified query
     * DatagramPacket
     */
    private byte[] createQueryResponse(DatagramPacket par1DatagramPacket) throws IOException
    {
        long var2 = MinecraftServer.getCurrentTimeMillis();

        if (var2 < lastQueryResponseTime + 5000L)
        {
            byte[] var9 = output.toByteArray();
            byte[] var10 = getRequestID(par1DatagramPacket.getSocketAddress());
            var9[1] = var10[0];
            var9[2] = var10[1];
            var9[3] = var10[2];
            var9[4] = var10[3];
            return var9;
        }
        else
        {
            lastQueryResponseTime = var2;
            output.reset();
            output.writeInt(0);
            output.writeByteArray(getRequestID(par1DatagramPacket.getSocketAddress()));
            output.writeString("splitnum");
            output.writeInt(128);
            output.writeInt(0);
            output.writeString("hostname");
            output.writeString(serverMotd);
            output.writeString("gametype");
            output.writeString("SMP");
            output.writeString("game_id");
            output.writeString("MINECRAFT");
            output.writeString("version");
            output.writeString(server.getMinecraftVersion());
            output.writeString("plugins");
            output.writeString(server.getPlugins());
            output.writeString("map");
            output.writeString(worldName);
            output.writeString("numplayers");
            output.writeString("" + getNumberOfPlayers());
            output.writeString("maxplayers");
            output.writeString("" + maxPlayers);
            output.writeString("hostport");
            output.writeString("" + serverPort);
            output.writeString("hostip");
            output.writeString(queryHostname);
            output.writeInt(0);
            output.writeInt(1);
            output.writeString("player_");
            output.writeInt(0);
            String[] var4 = server.getAllUsernames();
            String[] var5 = var4;
            int var6 = var4.length;

            for (int var7 = 0; var7 < var6; ++var7)
            {
                String var8 = var5[var7];
                output.writeString(var8);
            }

            output.writeInt(0);
            return output.toByteArray();
        }
    }

    /**
     * Returns the request ID provided by the authorized client
     */
    private byte[] getRequestID(SocketAddress par1SocketAddress)
    {
        return ((RConThreadQuery.Auth)queryClients.get(par1SocketAddress)).getRequestId();
    }

    /**
     * Returns true if the client has a valid auth, otherwise false
     */
    private Boolean verifyClientAuth(DatagramPacket par1DatagramPacket)
    {
        SocketAddress var2 = par1DatagramPacket.getSocketAddress();

        if (!queryClients.containsKey(var2))
        {
            return Boolean.valueOf(false);
        }
        else
        {
            byte[] var3 = par1DatagramPacket.getData();
            return ((RConThreadQuery.Auth)queryClients.get(var2)).getRandomChallenge() != RConUtils.getBytesAsBEint(var3, 7, par1DatagramPacket.getLength()) ? Boolean.valueOf(false) : Boolean.valueOf(true);
        }
    }

    /**
     * Sends an auth challenge DatagramPacket to the client and adds the client
     * to the queryClients map
     */
    private void sendAuthChallenge(DatagramPacket par1DatagramPacket) throws IOException
    {
        RConThreadQuery.Auth var2 = new RConThreadQuery.Auth(par1DatagramPacket);
        queryClients.put(par1DatagramPacket.getSocketAddress(), var2);
        sendResponsePacket(var2.getChallengeValue(), par1DatagramPacket);
    }

    /**
     * Removes all clients whose auth is no longer valid
     */
    private void cleanQueryClientsMap()
    {
        if (running)
        {
            long var1 = MinecraftServer.getCurrentTimeMillis();

            if (var1 >= lastAuthCheckTime + 30000L)
            {
                lastAuthCheckTime = var1;
                Iterator var3 = queryClients.entrySet().iterator();

                while (var3.hasNext())
                {
                    Entry var4 = (Entry)var3.next();

                    if (((RConThreadQuery.Auth)var4.getValue()).hasExpired(var1).booleanValue())
                    {
                        var3.remove();
                    }
                }
            }
        }
    }

    public void run()
    {
        logInfo("Query running on " + serverHostname + ":" + queryPort);
        lastAuthCheckTime = MinecraftServer.getCurrentTimeMillis();
        incomingPacket = new DatagramPacket(buffer, buffer.length);

        try
        {
            while (running)
            {
                try
                {
                    querySocket.receive(incomingPacket);
                    cleanQueryClientsMap();
                    parseIncomingPacket(incomingPacket);
                }
                catch (SocketTimeoutException var7)
                {
                    cleanQueryClientsMap();
                }
                catch (PortUnreachableException var8)
                {
                    ;
                }
                catch (IOException var9)
                {
                    stopWithException(var9);
                }
            }
        }
        finally
        {
            closeAllSockets();
        }
    }

    /**
     * Creates a new Thread object from this class and starts running
     */
    public void startThread()
    {
        if (!running)
        {
            if (0 < queryPort && 65535 >= queryPort)
            {
                if (initQuerySystem())
                {
                    super.startThread();
                }
            }
            else
            {
                logWarning("Invalid query port " + queryPort + " found in \'" + server.getSettingsFilename() + "\' (queries disabled)");
            }
        }
    }

    /**
     * Stops the query server and reports the given Exception
     */
    private void stopWithException(Exception par1Exception)
    {
        if (running)
        {
            logWarning("Unexpected exception, buggy JRE? (" + par1Exception.toString() + ")");

            if (!initQuerySystem())
            {
                logSevere("Failed to recover from buggy JRE, shutting down!");
                running = false;
            }
        }
    }

    /**
     * Initializes the query system by binding it to a port
     */
    private boolean initQuerySystem()
    {
        try
        {
            querySocket = new DatagramSocket(queryPort, InetAddress.getByName(serverHostname));
            registerSocket(querySocket);
            querySocket.setSoTimeout(500);
            return true;
        }
        catch (SocketException var2)
        {
            logWarning("Unable to initialise query system on " + serverHostname + ":" + queryPort + " (Socket): " + var2.getMessage());
        }
        catch (UnknownHostException var3)
        {
            logWarning("Unable to initialise query system on " + serverHostname + ":" + queryPort + " (Unknown Host): " + var3.getMessage());
        }
        catch (Exception var4)
        {
            logWarning("Unable to initialise query system on " + serverHostname + ":" + queryPort + " (E): " + var4.getMessage());
        }

        return false;
    }

    class Auth
    {
        private long timestamp = (new Date()).getTime();
        private int randomChallenge;
        private byte[] requestId;
        private byte[] challengeValue;
        private String requestIdAsString;
        private static final String __OBFID = "CL_00001803";

        public Auth(DatagramPacket par2DatagramPacket)
        {
            byte[] var3 = par2DatagramPacket.getData();
            requestId = new byte[4];
            requestId[0] = var3[3];
            requestId[1] = var3[4];
            requestId[2] = var3[5];
            requestId[3] = var3[6];
            requestIdAsString = new String(requestId);
            randomChallenge = (new Random()).nextInt(16777216);
            challengeValue = String.format("\t%s%d\u0000", new Object[] {requestIdAsString, Integer.valueOf(randomChallenge)}).getBytes();
        }

        public Boolean hasExpired(long par1)
        {
            return Boolean.valueOf(timestamp < par1);
        }

        public int getRandomChallenge()
        {
            return randomChallenge;
        }

        public byte[] getChallengeValue()
        {
            return challengeValue;
        }

        public byte[] getRequestId()
        {
            return requestId;
        }
    }
}
