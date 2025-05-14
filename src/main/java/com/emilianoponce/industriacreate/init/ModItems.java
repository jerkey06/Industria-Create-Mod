package com.emilianoponce.industriacreate.init;

import com.emilianoponce.industriacreate.IndustriaCreate;
import com.emilianoponce.industriacreate.item.WorkerWhistleItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all items in the mod
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, IndustriaCreate.MODID);

    // Worker whistle - used to assign tasks to workers
    public static final RegistryObject<Item> WORKER_WHISTLE = ITEMS.register("worker_whistle",
            () -> new WorkerWhistleItem(new Item.Properties().stacksTo(1)));

    /**
     * Register the items with the event bus
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
