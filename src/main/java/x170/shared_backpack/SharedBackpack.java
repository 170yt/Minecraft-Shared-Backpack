package x170.shared_backpack;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x170.shared_backpack.command.BackpackCommand;
import x170.shared_backpack.inventory.BackpackInventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SharedBackpack implements ModInitializer {
	public static final String MOD_ID = "shared-backpack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Path PATH_BACKPACK_DATA = FabricLoader.getInstance().getConfigDir().resolve(SharedBackpack.MOD_ID).resolve("backpack.dat");
	public static final Text BACKPACK_NAME = Text.literal("Shared Backpack");
	public static MinecraftServer SERVER;
	public static BackpackInventory BACKPACK_INVENTORY;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(BackpackCommand::register);

		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

		LOGGER.info("Shared Backpack mod loaded");
		backupBackpackData();
	}

	private void onServerStarted(MinecraftServer server) {
		SERVER = server;
		BACKPACK_INVENTORY = new BackpackInventory();
	}

	private void backupBackpackData() {
		try {
			if (PATH_BACKPACK_DATA.toFile().exists()) {
				Path backupPath = PATH_BACKPACK_DATA.getParent().resolve("backpack.dat_old");
				Files.deleteIfExists(backupPath);
				Files.copy(PATH_BACKPACK_DATA, backupPath);
				LOGGER.info("Backed up backpack data to {}", backupPath);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to back up backpack data: {}", e.getMessage());
		}
	}
}
