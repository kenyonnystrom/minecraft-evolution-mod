//
// This extension allows for the addition of new types of damage sources to the list already provided in DamageSource.
//

package evo.mod.features;

import net.minecraft.entity.damage.DamageSource;

public class DamageSourceExt extends DamageSource {
    protected DamageSourceExt(String name) {
        super(name);
    }
    public static final DamageSource HEATSTROKE = (new DamageSourceExt("heatstroke")).setBypassesArmor();
    public static final DamageSource HYPOTHERMIA = (new DamageSourceExt("hypothermia")).setBypassesArmor();
    public static final DamageSource BITE = (new DamageSourceExt("bite"));
    public static final DamageSource OLD_AGE = (new DamageSourceExt("old_age")).setBypassesArmor();
}
