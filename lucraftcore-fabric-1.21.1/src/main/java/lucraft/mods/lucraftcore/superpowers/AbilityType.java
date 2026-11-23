package lucraft.mods.lucraftcore.superpowers;

public enum AbilityType {
    /** Always active while granted. */
    CONSTANT,
    /** Toggled on and off (flight, invisibility, ...). */
    TOGGLE,
    /** Active while the key is held. */
    HELD,
    /** Triggered once per use (teleport, energy blast, ...). */
    ACTION
}
