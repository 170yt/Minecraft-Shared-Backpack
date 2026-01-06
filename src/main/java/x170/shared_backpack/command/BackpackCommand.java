package x170.shared_backpack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import x170.shared_backpack.SharedBackpack;
import x170.shared_backpack.inventory.SavedBackpackInventory;

public abstract class BackpackCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        LiteralCommandNode<CommandSourceStack> literalCommandNode = dispatcher.register(
                Commands.literal("backpack")
                        .executes(context -> executeBackpack(context.getSource()))
        );
        dispatcher.register(Commands.literal("bp")
                .executes(context -> executeBackpack(context.getSource()))
                .redirect(literalCommandNode));
    }

    private static int executeBackpack(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        // Send error message if the command was called by a non-player
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command"));
            return 0;
        }

        player.openMenu(
                new SimpleMenuProvider((syncId, playerInventory, playerx) ->
                        ChestMenu.sixRows(syncId, playerInventory, SavedBackpackInventory.getSavedBackpackInventory(SharedBackpack.SERVER).getBackpackInventory()),
                        SharedBackpack.BACKPACK_NAME
                )
        );
        return 1;
    }
}
