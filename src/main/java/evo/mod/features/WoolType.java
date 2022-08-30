//
// Details a WoolType object with useful methods for interacting with it.
// DyeColor served as inspiration.
//

package evo.mod.features;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Comparator;

// Details a WoolType object with useful methods for interacting with it. DyeColor served as inspiration.
public enum WoolType implements StringIdentifiable {
    NO_WOOL(0, "no_wool"),
    THIN_WOOL(1, "thin_wool"),
    STD_WOOL(2, "standard_wool"),
    THICK_WOOL(3, "thick_wool");

    // Makes list out of wool types for use in methods
    private static final WoolType[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(WoolType::getId)).toArray(WoolType[]::new);

    private final int id;
    private final String name;

    WoolType(int woolId, String name) {
        this.id = woolId;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    // Identify wool type by numeric id value
    public static WoolType byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            id = 0;
        }
        return VALUES[id];
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
