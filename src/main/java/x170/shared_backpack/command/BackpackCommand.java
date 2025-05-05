package x170.shared_backpack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import x170.shared_backpack.SharedBackpack;

public class BackpackCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
                CommandManager.literal("backpack")
                        .executes(context -> executeBackpack(context.getSource()))
        );
        dispatcher.register(CommandManager.literal("bp")
                .executes(context -> executeBackpack(context.getSource()))
                .redirect(literalCommandNode));
    }

    private static int executeBackpack(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        // Send error message if the command was called by a non-player
        if (player == null) {
            source.sendError(Text.literal("Only players can use this command"));
            return 0;
        }

        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) ->
                        GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, SharedBackpack.BACKPACK_INVENTORY),
                        SharedBackpack.BACKPACK_NAME
                )
        );
        return 1;
    }
}
