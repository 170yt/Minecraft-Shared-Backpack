package x170.shared_backpack.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import x170.shared_backpack.SharedBackpack;

import java.util.ArrayList;
import java.util.List;

// https://docs.fabricmc.net/develop/saved-data
public class BackpackInventory extends SavedData {
    private static final Codec<BackpackInventory> CODEC = ItemStackWithSlot.CODEC.listOf().xmap(BackpackInventory::new, BackpackInventory::toItemList);
    private static final SavedDataType<BackpackInventory> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(SharedBackpack.MOD_ID, SharedBackpack.MOD_ID), BackpackInventory::new, CODEC, null);
    private final SimpleContainer container;

    public BackpackInventory() {
        this.container = new SimpleContainer(54) {
            public void setChanged() {
                super.setChanged();
                BackpackInventory.this.setDirty();
            }
        };
    }

    public BackpackInventory(List<ItemStackWithSlot> items) {
        this();

        for (ItemStackWithSlot itemStackWithSlot : items) {
            if (itemStackWithSlot.isValidInContainer(this.container.getContainerSize())) {
                this.container.setItem(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
    }

    public static Container getContainer(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE).container;
    }

    public List<ItemStackWithSlot> toItemList() {
        List<ItemStackWithSlot> items = new ArrayList<>();
        for (int i = 0; i < this.container.getContainerSize(); i++) {
            ItemStack itemStack = this.container.getItem(i);
            if (!itemStack.isEmpty()) {
                items.add(new ItemStackWithSlot(i, itemStack));
            }
        }
        return items;
    }
}
