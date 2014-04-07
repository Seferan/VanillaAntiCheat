package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;

public enum EnumCreatureType
{
    monster(IMob.class, 70, Material.air, false, false), creature(EntityAnimal.class, 10, Material.air, true, true), ambient(EntityAmbientCreature.class, 15, Material.air, true, false), waterCreature(EntityWaterMob.class, 5, Material.field_151586_h, true, false);

    /**
     * The root class of creatures associated with this EnumCreatureType (IMobs
     * for aggressive creatures, EntityAnimals for friendly ones)
     */
    private final Class creatureClass;
    private final int maxNumberOfCreature;
    private final Material creatureMaterial;

    /** A flag indicating whether this creature type is peaceful. */
    private final boolean isPeacefulCreature;

    /** Whether this creature type is an animal. */
    private final boolean isAnimal;
    private static final String __OBFID = "CL_00001551";

    private EnumCreatureType(Class par3Class, int par4, Material par5Material, boolean par6, boolean par7)
    {
        creatureClass = par3Class;
        maxNumberOfCreature = par4;
        creatureMaterial = par5Material;
        isPeacefulCreature = par6;
        isAnimal = par7;
    }

    public Class getCreatureClass()
    {
        return creatureClass;
    }

    public int getMaxNumberOfCreature()
    {
        return maxNumberOfCreature;
    }

    public Material getCreatureMaterial()
    {
        return creatureMaterial;
    }

    /**
     * Gets whether or not this creature type is peaceful.
     */
    public boolean getPeacefulCreature()
    {
        return isPeacefulCreature;
    }

    /**
     * Return whether this creature type is an animal.
     */
    public boolean getAnimal()
    {
        return isAnimal;
    }
}
