//
// Goal facilitates pathfinding of EvolvingSheepEntity to EvolutionBlock and subsequent consumption
//

package evo.mod.sheep;

import evo.mod.blocks.EvolutionBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;

public class EatTreeGoal extends Goal {
    protected final PathAwareEntity mob;
    protected World world;
    private final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;
    private int timer = -1;
    private BlockPos tree;
    private boolean inRange;

    public EatTreeGoal(PathAwareEntity mob, double speed) {
        this.mob = mob;
        this.world = mob.world;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    // Only begin goal if a valid tree has been found by the entity, who cannot be a child
    @Override
    public boolean canStart() { return (((EvolvingSheepAccess)this.mob).updateTreeTarget() && !this.mob.isBaby()); }

    // Helper method to break tree BlockPos into positional arguments
    protected boolean findTarget() {
        if (!((EvolvingSheepAccess)this.mob).updateTreeTarget()) {
            return false;
        } else {
            this.targetX = this.tree.getX();
            this.targetY = this.tree.getY();
            this.targetZ = this.tree.getZ();
            return true;
        }
    }

    public boolean isActive() {
        return this.active;
    }

    // Start navigating to the tree
    public void start() {
        this.tree = ((EvolvingSheepAccess)this.mob).getTreeTarget();
        this.findTarget();
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.inRange = false;
        this.active = true;
    }

    // After the specified number of timer ticks, strip tree bark if things are successful otherwise indicate failure
    public void stop() {
        this.timer = -1;
        this.active = false;
        BlockState treeState = this.world.getBlockState(this.tree);
        // Make sure tree block has not disappeared and is actually within a handful of blocks of the sheep
        if (treeState.getBlock() instanceof EvolutionBlock && this.inRange) {
            int stage = treeState.get(EvolutionBlock.STAGE);
            if (stage == 0){
                world.setBlockState(this.tree, Blocks.AIR.getDefaultState());
            } else if (stage >= 1 && stage <= 5) {
                world.setBlockState(this.tree, treeState.with(EvolutionBlock.STAGE, stage + 5));
            }
            ((EvolvingSheepAccess)this.mob).onEatingTree(true);
        } else {
            ((EvolvingSheepAccess)this.mob).onEatingTree(false);
        }
    }

    // Only complete the goal if the timer reaches 0
    public boolean shouldContinue() {
        return (this.timer != 0);
    }

    // After starting this method will begin ticking frequently. Most ticks it will do nothing, but it is built to
    // handle the eating sequence in a visually appealing manner.
    public void tick() {
        // Once the timer is running, keep the sheep looking toward the trunk until it reaches 0
        if (this.timer > 0) {
            this.timer = Math.max(0, this.timer - 1);
            this.mob.getNavigation().stop();
            this.mob.getLookControl().lookAt(new Vec3d(0, -1, 0));
            // When the timer hits 0, play the munching noise
            if (this.timer == 0) {
                this.world.playSoundFromEntity((PlayerEntity)null, this.mob, SoundEvents.ENTITY_STRIDER_EAT, SoundCategory.NEUTRAL, 0.7F, 0.9F);
            }
        // Once the sheep has stopped, make sure it reached the tree and then start the timer and begin eating motion
        } else if (this.mob.getNavigation().isIdle()) {
            // Make sure sheep is in range and not stopped because of poor navigation or eating grass
            BlockPos curPos = this.mob.getBlockPos();
            Vec3d lookAtVec = new Vec3d(this.tree.getX() - curPos.getX(), this.tree.getY() - curPos.getY(), this.tree.getZ() - curPos.getZ());
            // If out of range give up, otherwise time to munch
            if (lookAtVec.length() > 2) {
                this.timer = 0;
            } else {
                this.inRange = true;
                // Mimic eating motion
                this.mob.getLookControl().lookAt(lookAtVec);
                this.mob.emitGameEvent(GameEvent.EAT, this.mob.getCameraBlockPos());
                this.mob.getNavigation().stop();
                // Set timer to strip log after a short time
                this.timer = 6;
            }
        // If the tree died or was eaten, stop everything
        } else if (!((EvolvingSheepAccess)this.mob).updateTreeTarget()) {
            this.timer = 0;
        }
    }

}
