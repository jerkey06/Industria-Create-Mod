package com.emilianoponce.industriacreate.logic;

import com.emilianoponce.industriacreate.IndustriaCreate;
import com.emilianoponce.industriacreate.entity.WorkerVillagerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages tasks for worker villagers
 */
public class TaskSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSystem.class);
    private static final Map<UUID, Task> ACTIVE_TASKS = new HashMap<>();
    
    /**
     * Task types that workers can perform
     */
    public enum TaskType {
        IDLE,
        TRANSPORT,
        OPERATE_MACHINE,
        ACTIVATE_MECHANISM
    }
    
    /**
     * Represents a task that can be assigned to a worker
     */
    public static class Task {
        private final TaskType type;
        private final BlockPos startPos;
        private final BlockPos targetPos;
        private final String description;
        private boolean completed;
        
        public Task(TaskType type, BlockPos startPos, BlockPos targetPos, String description) {
            this.type = type;
            this.startPos = startPos;
            this.targetPos = targetPos;
            this.description = description;
            this.completed = false;
        }
        
        public TaskType getType() {
            return type;
        }
        
        public BlockPos getStartPos() {
            return startPos;
        }
        
        public BlockPos getTargetPos() {
            return targetPos;
        }
        
        public String getDescription() {
            return description;
        }
        
        public boolean isCompleted() {
            return completed;
        }
        
        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
    
    /**
     * Assigns a task to a worker
     * @param worker The worker to assign the task to
     * @param task The task to assign
     */
    public static void assignTask(WorkerVillagerEntity worker, Task task) {
        ACTIVE_TASKS.put(worker.getUUID(), task);
        worker.setTask(task.getType().name().toLowerCase());
        worker.setTargetPos(task.getTargetPos());
        LOGGER.info("Assigned task {} to worker {}", task.getType(), worker.getUUID());
    }
    
    /**
     * Gets the current task for a worker
     * @param worker The worker to get the task for
     * @return The current task, or null if no task is assigned
     */
    public static Task getTask(WorkerVillagerEntity worker) {
        return ACTIVE_TASKS.get(worker.getUUID());
    }
    
    /**
     * Completes a task for a worker
     * @param worker The worker whose task is completed
     */
    public static void completeTask(WorkerVillagerEntity worker) {
        Task task = ACTIVE_TASKS.get(worker.getUUID());
        if (task != null) {
            task.setCompleted(true);
            LOGGER.info("Worker {} completed task {}", worker.getUUID(), task.getType());
            
            // Reward the worker for completing the task
            worker.increaseMotivation(10);
            
            // Remove the task
            ACTIVE_TASKS.remove(worker.getUUID());
            worker.setTask("idle");
        }
    }
    
    /**
     * Goal for workers to move to their assigned task location
     */
    public static class MoveToTaskGoal extends MoveToBlockGoal {
        private final WorkerVillagerEntity worker;
        
        public MoveToTaskGoal(WorkerVillagerEntity worker, double speedModifier) {
            super(worker, speedModifier, 16, 5);
            this.worker = worker;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        
        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            Task task = getTask(worker);
            if (task != null && task.getTargetPos() != null) {
                return pos.equals(task.getTargetPos());
            }
            return false;
        }
        
        @Override
        public boolean canUse() {
            Task task = getTask(worker);
            if (task != null && !task.isCompleted() && task.getTargetPos() != null) {
                this.blockPos = task.getTargetPos();
                return true;
            }
            return false;
        }
        
        @Override
        public void tick() {
            super.tick();
            
            Task task = getTask(worker);
            if (task != null && !task.isCompleted()) {
                if (this.isReachedTarget()) {
                    // Worker has reached the target position
                    switch (task.getType()) {
                        case TRANSPORT:
                            // TODO: Implement item transport logic
                            break;
                        case OPERATE_MACHINE:
                            // TODO: Implement machine operation logic
                            break;
                        case ACTIVATE_MECHANISM:
                            // TODO: Implement mechanism activation logic
                            break;
                        default:
                            break;
                    }
                    
                    // For now, just complete the task when the worker reaches the target
                    completeTask(worker);
                }
            }
        }
        
        @Override
        protected boolean isReachedTarget() {
            return this.worker.distanceToSqr(this.blockPos.getX() + 0.5D, this.blockPos.getY() + 0.5D, this.blockPos.getZ() + 0.5D) < 2.0D;
        }
    }
}