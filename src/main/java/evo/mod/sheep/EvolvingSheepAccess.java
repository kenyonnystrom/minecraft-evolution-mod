//
// Allows access of methods from the EvolvingSheepEntity mixin elsewhere.
//

package evo.mod.sheep;

import evo.mod.features.WoolType;
import net.minecraft.util.math.BlockPos;

public interface EvolvingSheepAccess {
    WoolType getWool();
    BlockPos getTreeTarget();
    boolean updateTreeTarget();
    void onEatingTree(boolean success);
}
