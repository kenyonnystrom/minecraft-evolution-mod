package evo.mod;

import net.minecraft.entity.damage.DamageSource;


public class DamageSourceExt extends DamageSource {
    protected DamageSourceExt(String name) {
        super(name);
    }
    public static final DamageSource HEATSTROKE = (new DamageSourceExt("heatstroke")).setBypassesArmor();
    public static final DamageSource HYPOTHERMIA = (new DamageSourceExt("hypothermia")).setBypassesArmor();
}
