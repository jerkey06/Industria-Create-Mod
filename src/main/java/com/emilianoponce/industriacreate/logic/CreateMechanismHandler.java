package com.emilianoponce.industriacreate.logic;

import com.emilianoponce.industriacreate.IndustriaCreate;
import com.emilianoponce.industriacreate.entity.WorkerVillagerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles interactions between workers and Create mod mechanisms
 */
public class CreateMechanismHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateMechanismHandler.class);

    /**
     * Mechanism types from the Create mod that workers can interact with
     */
    public enum MechanismType {
        MECHANICAL_PRESS,
        MECHANICAL_MIXER,
        DEPLOYER,
        MECHANICAL_SAW,
        MECHANICAL_DRILL,
        MECHANICAL_BELT,
        MECHANICAL_PUMP,
        MECHANICAL_PISTON,
        GEARBOX,
        CLUTCH,
        WATER_WHEEL,
        WINDMILL,
        UNKNOWN
    }

    /**
     * Identifies the type of Create mechanism at the given position
     * @param level The level
     * @param pos The position to check
     * @return The type of mechanism, or UNKNOWN if not a Create mechanism
     */
    public static MechanismType identifyMechanism(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return MechanismType.UNKNOWN;
        }

        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        String blockId = block.getDescriptionId();

        // Check block ID to identify Create mechanisms
        if (blockId.contains("create")) {
            LOGGER.info("Found Create mechanism: {}", blockId);
            
            if (blockId.contains("mechanical_press")) {
                return MechanismType.MECHANICAL_PRESS;
            } else if (blockId.contains("mechanical_mixer")) {
                return MechanismType.MECHANICAL_MIXER;
            } else if (blockId.contains("deployer")) {
                return MechanismType.DEPLOYER;
            } else if (blockId.contains("mechanical_saw")) {
                return MechanismType.MECHANICAL_SAW;
            } else if (blockId.contains("mechanical_drill")) {
                return MechanismType.MECHANICAL_DRILL;
            } else if (blockId.contains("belt")) {
                return MechanismType.MECHANICAL_BELT;
            } else if (blockId.contains("mechanical_pump")) {
                return MechanismType.MECHANICAL_PUMP;
            } else if (blockId.contains("mechanical_piston")) {
                return MechanismType.MECHANICAL_PISTON;
            } else if (blockId.contains("gearbox")) {
                return MechanismType.GEARBOX;
            } else if (blockId.contains("clutch")) {
                return MechanismType.CLUTCH;
            } else if (blockId.contains("water_wheel")) {
                return MechanismType.WATER_WHEEL;
            } else if (blockId.contains("windmill")) {
                return MechanismType.WINDMILL;
            }
        }
        
        return MechanismType.UNKNOWN;
    }

    /**
     * Interacts with a Create mechanism
     * @param worker The worker performing the interaction
     * @param pos The position of the mechanism
     * @return True if the interaction was successful, false otherwise
     */
    public static boolean interactWithMechanism(WorkerVillagerEntity worker, BlockPos pos) {
        Level level = worker.level();
        if (level.isClientSide) {
            return false;
        }

        MechanismType mechanismType = identifyMechanism(level, pos);
        if (mechanismType == MechanismType.UNKNOWN) {
            LOGGER.warn("Worker tried to interact with unknown mechanism at {}", pos);
            return false;
        }

        LOGGER.info("Worker {} interacting with {} at {}", worker.getUUID(), mechanismType, pos);

        // Position for the worker to face the mechanism
        Vec3 lookPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        worker.getLookControl().setLookAt(lookPos.x, lookPos.y, lookPos.z);

        // Different interactions based on mechanism type
        switch (mechanismType) {
            case MECHANICAL_PRESS:
                return operateMechanicalPress(worker, level, pos);
            case MECHANICAL_MIXER:
                return operateMechanicalMixer(worker, level, pos);
            case DEPLOYER:
                return operateDeployer(worker, level, pos);
            case MECHANICAL_SAW:
                return operateMechanicalSaw(worker, level, pos);
            case MECHANICAL_DRILL:
                return operateMechanicalDrill(worker, level, pos);
            case MECHANICAL_BELT:
                return operateMechanicalBelt(worker, level, pos);
            case MECHANICAL_PUMP:
                return operateMechanicalPump(worker, level, pos);
            case MECHANICAL_PISTON:
                return operateMechanicalPiston(worker, level, pos);
            case GEARBOX:
                return operateGearbox(worker, level, pos);
            case CLUTCH:
                return operateClutch(worker, level, pos);
            case WATER_WHEEL:
                return maintainWaterWheel(worker, level, pos);
            case WINDMILL:
                return maintainWindmill(worker, level, pos);
            default:
                return false;
        }
    }

    /**
     * Operates a mechanical press
     */
    private static boolean operateMechanicalPress(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker placing items in the press and activating it
        LOGGER.info("Worker operating mechanical press at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(2);
        return true;
    }

    /**
     * Operates a mechanical mixer
     */
    private static boolean operateMechanicalMixer(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker adding ingredients to the mixer
        LOGGER.info("Worker operating mechanical mixer at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(2);
        return true;
    }

    /**
     * Operates a deployer
     */
    private static boolean operateDeployer(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker configuring the deployer
        LOGGER.info("Worker operating deployer at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(1);
        return true;
    }

    /**
     * Operates a mechanical saw
     */
    private static boolean operateMechanicalSaw(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker feeding materials to the saw
        LOGGER.info("Worker operating mechanical saw at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(3);
        return true;
    }

    /**
     * Operates a mechanical drill
     */
    private static boolean operateMechanicalDrill(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker positioning the drill
        LOGGER.info("Worker operating mechanical drill at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(3);
        return true;
    }

    /**
     * Operates a mechanical belt
     */
    private static boolean operateMechanicalBelt(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker placing or removing items from the belt
        LOGGER.info("Worker operating mechanical belt at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(1);
        return true;
    }

    /**
     * Operates a mechanical pump
     */
    private static boolean operateMechanicalPump(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker maintaining the pump
        LOGGER.info("Worker operating mechanical pump at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(2);
        return true;
    }

    /**
     * Operates a mechanical piston
     */
    private static boolean operateMechanicalPiston(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker adjusting the piston
        LOGGER.info("Worker operating mechanical piston at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(2);
        return true;
    }

    /**
     * Operates a gearbox
     */
    private static boolean operateGearbox(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker adjusting the gearbox
        LOGGER.info("Worker operating gearbox at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(1);
        return true;
    }

    /**
     * Operates a clutch
     */
    private static boolean operateClutch(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker toggling the clutch
        LOGGER.info("Worker operating clutch at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(1);
        return true;
    }

    /**
     * Maintains a water wheel
     */
    private static boolean maintainWaterWheel(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker maintaining the water wheel
        LOGGER.info("Worker maintaining water wheel at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(2);
        return true;
    }

    /**
     * Maintains a windmill
     */
    private static boolean maintainWindmill(WorkerVillagerEntity worker, Level level, BlockPos pos) {
        // Simulate worker maintaining the windmill
        LOGGER.info("Worker maintaining windmill at {}", pos);
        worker.swing(InteractionHand.MAIN_HAND);
        worker.decreaseEnergy(2);
        return true;
    }
}