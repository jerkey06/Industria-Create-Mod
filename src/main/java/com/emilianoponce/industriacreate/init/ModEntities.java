package com.emilianoponce.industriacreate.init;

import com.emilianoponce.industriacreate.IndustriaCreate;
import com.emilianoponce.industriacreate.entity.WorkerVillagerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, IndustriaCreate.MODID);

    public static final RegistryObject<EntityType<WorkerVillagerEntity>> WORKER_VILLAGER = 
            ENTITY_TYPES.register("worker_villager",
                    () -> EntityType.Builder.of(WorkerVillagerEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(10)
                            .build(IndustriaCreate.MODID + ":worker_villager"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
