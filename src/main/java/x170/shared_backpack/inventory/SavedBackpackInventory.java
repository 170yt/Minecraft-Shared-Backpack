package x170.shared_backpack.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import x170.shared_backpack.SharedBackpack;

import java.util.ArrayList;
import java.util.List;

// https://docs.fabricmc.net/develop/saved-data
public class SavedBackpackInventory extends SavedData {
    private static final Codec<SavedBackpackInventory> CODEC = ItemStackWithSlot.CODEC.listOf().xmap(SavedBackpackInventory::new, SavedBackpackInventory::toItemList);
    private static final SavedDataType<SavedBackpackInventory> TYPE = new SavedDataType<>(SharedBackpack.MOD_ID, SavedBackpackInventory::new, CODEC, null);
    private final BackpackInventory backpackInventory;

    public SavedBackpackInventory() {
        this.backpackInventory = new BackpackInventory();
        this.backpackInventory.addListener(container -> this.setDirty());
    }

    public SavedBackpackInventory(List<ItemStackWithSlot> items) {
        this();

        for (ItemStackWithSlot itemStackWithSlot : items) {
            if (itemStackWithSlot.isValidInContainer(this.backpackInventory.getContainerSize())) {
                this.backpackInventory.setItem(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
    }

    public static SavedBackpackInventory getSavedBackpackInventory(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public List<ItemStackWithSlot> toItemList() {
        List<ItemStackWithSlot> items = new ArrayList<>();
        for (int i = 0; i < this.backpackInventory.getContainerSize(); i++) {
            ItemStack itemStack = this.backpackInventory.getItem(i);
            if (!itemStack.isEmpty()) {
                items.add(new ItemStackWithSlot(i, itemStack));
            }
        }
        return items;
    }

    public BackpackInventory getBackpackInventory() {
        return this.backpackInventory;
    }
}
