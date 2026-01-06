package x170.shared_backpack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x170.shared_backpack.command.BackpackCommand;

public class SharedBackpack implements ModInitializer {
    public static final String MOD_ID = "shared-backpack";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Component BACKPACK_NAME = Component.literal("Shared Backpack");
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(BackpackCommand::register);

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

        LOGGER.info("Shared Backpack mod loaded");
    }

    private void onServerStarted(MinecraftServer server) {
        SERVER = server;
    }
}
