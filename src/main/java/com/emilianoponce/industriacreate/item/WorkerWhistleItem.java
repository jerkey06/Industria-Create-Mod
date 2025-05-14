package com.emilianoponce.industriacreate.item;

import com.emilianoponce.industriacreate.entity.WorkerVillagerEntity;
import com.emilianoponce.industriacreate.logic.TaskSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Whistle used to assign tasks to workers
 */
public class WorkerWhistleItem extends Item {
    private WorkerVillagerEntity selectedWorker;
    private BlockPos targetPos;
    
    public WorkerWhistleItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.industriacreate.worker_whistle.tooltip"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        
        if (player == null) {
            return InteractionResult.PASS;
        }
        
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        
        if (selectedWorker != null) {
            // If we already have a selected worker, set the target position
            targetPos = pos;
            
            // Create and assign a task to the worker
            TaskSystem.Task task = new TaskSystem.Task(
                    TaskSystem.TaskType.TRANSPORT, // Default to transport task for now
                    selectedWorker.blockPosition(),
                    targetPos,
                    "Transport items to target"
            );
            
            TaskSystem.assignTask(selectedWorker, task);
            
            player.sendSystemMessage(Component.literal("Task assigned to worker!"));
            
            // Reset selection
            selectedWorker = null;
            targetPos = null;
            
            return InteractionResult.SUCCESS;
        } else {
            // If no worker is selected, check if there's a worker nearby
            List<WorkerVillagerEntity> workers = level.getEntitiesOfClass(
                    WorkerVillagerEntity.class,
                    new AABB(pos).inflate(5.0D)
            );
            
            if (!workers.isEmpty()) {
                // Select the closest worker
                selectedWorker = workers.get(0);
                player.sendSystemMessage(Component.literal("Worker selected! Now click on a target block."));
                return InteractionResult.SUCCESS;
            } else {
                player.sendSystemMessage(Component.literal("No workers found nearby."));
                return InteractionResult.FAIL;
            }
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        if (level.isClientSide) {
            return InteractionResultHolder.success(itemstack);
        }
        
        // Reset selection when used in air
        if (selectedWorker != null) {
            selectedWorker = null;
            targetPos = null;
            player.sendSystemMessage(Component.literal("Worker selection cleared."));
        }
        
        return InteractionResultHolder.success(itemstack);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Make the whistle glow when a worker is selected
        return selectedWorker != null;
    }
}