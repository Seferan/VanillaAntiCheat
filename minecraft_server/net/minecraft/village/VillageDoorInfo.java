package net.minecraft.village;

public class VillageDoorInfo
{
    public final int posX;
    public final int posY;
    public final int posZ;
    public final int insideDirectionX;
    public final int insideDirectionZ;
    public int lastActivityTimestamp;
    public boolean isDetachedFromVillageFlag;
    private int doorOpeningRestrictionCounter;
    private static final String __OBFID = "CL_00001630";

    public VillageDoorInfo(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        posX = par1;
        posY = par2;
        posZ = par3;
        insideDirectionX = par4;
        insideDirectionZ = par5;
        lastActivityTimestamp = par6;
    }

    /**
     * Returns the squared distance between this door and the given coordinate.
     */
    public int getDistanceSquared(int par1, int par2, int par3)
    {
        int var4 = par1 - posX;
        int var5 = par2 - posY;
        int var6 = par3 - posZ;
        return var4 * var4 + var5 * var5 + var6 * var6;
    }

    /**
     * Get the square of the distance from a location 2 blocks away from the
     * door considered 'inside' and the given arguments
     */
    public int getInsideDistanceSquare(int par1, int par2, int par3)
    {
        int var4 = par1 - posX - insideDirectionX;
        int var5 = par2 - posY;
        int var6 = par3 - posZ - insideDirectionZ;
        return var4 * var4 + var5 * var5 + var6 * var6;
    }

    public int getInsidePosX()
    {
        return posX + insideDirectionX;
    }

    public int getInsidePosY()
    {
        return posY;
    }

    public int getInsidePosZ()
    {
        return posZ + insideDirectionZ;
    }

    public boolean isInside(int par1, int par2)
    {
        int var3 = par1 - posX;
        int var4 = par2 - posZ;
        return var3 * insideDirectionX + var4 * insideDirectionZ >= 0;
    }

    public void resetDoorOpeningRestrictionCounter()
    {
        doorOpeningRestrictionCounter = 0;
    }

    public void incrementDoorOpeningRestrictionCounter()
    {
        ++doorOpeningRestrictionCounter;
    }

    public int getDoorOpeningRestrictionCounter()
    {
        return doorOpeningRestrictionCounter;
    }
}
