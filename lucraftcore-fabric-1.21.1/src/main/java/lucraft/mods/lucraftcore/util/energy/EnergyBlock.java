package lucraft.mods.lucraftcore.util.energy;

/** Implemented by block entities that store FE so generators can push into them. */
public interface EnergyBlock {

    EnergyStorage getEnergy();
}
