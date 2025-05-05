package x170.shared_backpack.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryWrapper;
import x170.shared_backpack.SharedBackpack;

import java.nio.file.Files;
import java.nio.file.Path;

public class BackpackInventory extends SimpleInventory {
    public BackpackInventory() {
        super(54);

        // Initialize the inventory from the saved NBT data
        if (Files.exists(SharedBackpack.PATH_BACKPACK_DATA)) {
            try {
                NbtCompound nbt = NbtIo.readCompressed(SharedBackpack.PATH_BACKPACK_DATA, NbtSizeTracker.ofUnlimitedBytes());
                this.readNbtList(nbt.getListOrEmpty("Items"), SharedBackpack.SERVER.getRegistryManager());
            } catch (Exception e) {
                SharedBackpack.LOGGER.error("Failed to load backpack data: {}", e.getMessage());
            }
        }

        this.addListener(sender -> saveNbt());
    }

    @Override
    public void readNbtList(NbtList list, RegistryWrapper.WrapperLookup registries) {
        for (int i = 0; i < this.size(); i++) {
            this.setStack(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < list.size(); i++) {
            NbtCompound nbtCompound = list.getCompoundOrEmpty(i);
            int j = nbtCompound.getByte("Slot", (byte)0) & 255;
            if (j >= 0 && j < this.size()) {
                this.setStack(j, ItemStack.fromNbt(registries, nbtCompound).orElse(ItemStack.EMPTY));
            }
        }
    }

    @Override
    public NbtList toNbtList(RegistryWrapper.WrapperLookup registries) {
        NbtList nbtList = new NbtList();

        for (int i = 0; i < this.size(); i++) {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                nbtList.add(itemStack.toNbt(registries, nbtCompound));
            }
        }

        return nbtList;
    }

    public void saveNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("Items", this.toNbtList(SharedBackpack.SERVER.getRegistryManager()));

        try {
            Files.createDirectories(SharedBackpack.PATH_BACKPACK_DATA.getParent());
            Files.deleteIfExists(SharedBackpack.PATH_BACKPACK_DATA);
            Path path = Files.createFile(SharedBackpack.PATH_BACKPACK_DATA);
            NbtIo.writeCompressed(nbt, path);
        } catch (Exception e) {
            SharedBackpack.LOGGER.error("Failed to save backpack data: {}", e.getMessage());
        }
    }
}
