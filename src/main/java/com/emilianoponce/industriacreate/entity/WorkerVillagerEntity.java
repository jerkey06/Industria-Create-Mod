package com.emilianoponce.industriacreate.entity;

import com.emilianoponce.industriacreate.IndustriaCreate;
import com.emilianoponce.industriacreate.logic.TaskSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

public class WorkerVillagerEntity extends Animal {
    private static final EntityDataAccessor<Integer> DATA_MOTIVATION = SynchedEntityData.defineId(WorkerVillagerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_TASK = SynchedEntityData.defineId(WorkerVillagerEntity.class, EntityDataSerializers.STRING);

    // Worker properties
    private BlockPos workZone;
    private BlockPos targetPos;
    private int salary;
    private int energy;
    private int maxEnergy = 100;

    public WorkerVillagerEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TaskSystem.MoveToTaskGoal(this, 1.0D)); // Higher priority for task execution
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_MOTIVATION, 100);
        this.entityData.define(DATA_TASK, "idle");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Motivation", this.getMotivation());
        tag.putString("Task", this.getTask());
        tag.putInt("Salary", this.salary);
        tag.putInt("Energy", this.energy);

        if (this.workZone != null) {
            tag.putInt("WorkZoneX", this.workZone.getX());
            tag.putInt("WorkZoneY", this.workZone.getY());
            tag.putInt("WorkZoneZ", this.workZone.getZ());
        }

        if (this.targetPos != null) {
            tag.putInt("TargetX", this.targetPos.getX());
            tag.putInt("TargetY", this.targetPos.getY());
            tag.putInt("TargetZ", this.targetPos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setMotivation(tag.getInt("Motivation"));
        this.setTask(tag.getString("Task"));
        this.salary = tag.getInt("Salary");
        this.energy = tag.getInt("Energy");

        if (tag.contains("WorkZoneX")) {
            this.workZone = new BlockPos(
                    tag.getInt("WorkZoneX"),
                    tag.getInt("WorkZoneY"),
                    tag.getInt("WorkZoneZ")
            );
        }

        if (tag.contains("TargetX")) {
            this.targetPos = new BlockPos(
                    tag.getInt("TargetX"),
                    tag.getInt("TargetY"),
                    tag.getInt("TargetZ")
            );
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Handle interaction with worker whistle
        if (itemstack.getItem() instanceof com.emilianoponce.industriacreate.item.WorkerWhistleItem) {
            // The whistle handles the interaction logic
            return InteractionResult.PASS;
        }

        if (itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            // Show worker info when right-clicked with empty hand
            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal("Worker Status:"));
                player.sendSystemMessage(Component.literal("Task: " + this.getTask()));
                player.sendSystemMessage(Component.literal("Motivation: " + this.getMotivation() + "%"));
                player.sendSystemMessage(Component.literal("Energy: " + this.energy + "/" + this.maxEnergy));

                // Show work zone info if assigned
                if (this.workZone != null) {
                    player.sendSystemMessage(Component.literal("Work Zone: " + this.workZone.toShortString()));
                }

                // Show target position if assigned
                if (this.targetPos != null) {
                    player.sendSystemMessage(Component.literal("Target: " + this.targetPos.toShortString()));
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Decrease energy and motivation over time
            if (this.random.nextInt(6000) == 0) { // Roughly every 5 minutes
                this.decreaseEnergy(1);
                this.decreaseMotivation(1);
            }

            // TODO: Implement task execution logic
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // Workers don't breed
        return null;
    }

    // Getter and setter methods
    public int getMotivation() {
        return this.entityData.get(DATA_MOTIVATION);
    }

    public void setMotivation(int motivation) {
        this.entityData.set(DATA_MOTIVATION, Math.max(0, Math.min(100, motivation)));
    }

    public void increaseMotivation(int amount) {
        this.setMotivation(this.getMotivation() + amount);
    }

    public void decreaseMotivation(int amount) {
        this.setMotivation(this.getMotivation() - amount);
    }

    public String getTask() {
        return this.entityData.get(DATA_TASK);
    }

    public void setTask(String task) {
        this.entityData.set(DATA_TASK, task);
    }

    public void setWorkZone(BlockPos pos) {
        this.workZone = pos;
    }

    public BlockPos getWorkZone() {
        return this.workZone;
    }

    public void setTargetPos(BlockPos pos) {
        this.targetPos = pos;
    }

    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public int getSalary() {
        return this.salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getEnergy() {
        return this.energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(this.maxEnergy, energy));
    }

    public void increaseEnergy(int amount) {
        this.setEnergy(this.energy + amount);
    }

    public void decreaseEnergy(int amount) {
        this.setEnergy(this.energy - amount);
    }
}
