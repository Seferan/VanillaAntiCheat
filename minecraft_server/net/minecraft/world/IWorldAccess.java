package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IWorldAccess
{
    void func_147586_a(int var1, int var2, int var3);

    void func_147588_b(int var1, int var2, int var3);

    void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6);

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

    /**
     * Plays sound to all near players except the player reference given
     */
    void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10);

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12);

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    void onEntityCreate(Entity var1);

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    void onEntityDestroy(Entity var1);

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    void playRecord(String var1, int var2, int var3, int var4);

    void broadcastSound(int var1, int var2, int var3, int var4, int var5);

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6);

    void func_147587_b(int var1, int var2, int var3, int var4, int var5);

    void func_147584_b();
}
