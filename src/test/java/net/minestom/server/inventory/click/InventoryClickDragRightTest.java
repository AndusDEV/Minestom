package net.minestom.server.inventory.click;

import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class InventoryClickDragRightTest extends ClickUtils {

    static {
        // Required for now
        MinecraftServer.init();
    }

    @Test
    public void empty() {
        assertDouble((playerInventory, inventory) -> ClickProcessor.rightDrag(playerInventory, inventory, ItemStack.AIR, List.of()),
                ItemStack.AIR, Map.of(), Map.of());
    }

    @Test
    public void dragStack() {
        assertDouble((playerInventory, inventory) -> ClickProcessor.rightDrag(playerInventory, inventory, ItemStack.of(Material.DIAMOND, 64), List.of(Pair.of(playerInventory, 0), Pair.of(playerInventory, 1))),
                ItemStack.of(Material.DIAMOND, 62), Map.of(0, ItemStack.of(Material.DIAMOND), 1, ItemStack.of(Material.DIAMOND)), Map.of());
    }

    @Test
    public void dragTooMuch() {
        assertDouble((playerInventory, inventory) -> ClickProcessor.rightDrag(playerInventory, inventory, ItemStack.of(Material.DIAMOND, 2), List.of(Pair.of(playerInventory, 0), Pair.of(playerInventory, 1), Pair.of(playerInventory, 2))),
                ItemStack.AIR, Map.of(0, ItemStack.of(Material.DIAMOND), 1, ItemStack.of(Material.DIAMOND)), Map.of());
    }

    @Test
    public void override() {
        assertDouble((playerInventory, inventory) -> {
            playerInventory.setItemStack(1, ItemStack.of(Material.DIAMOND, 1));
            return ClickProcessor.rightDrag(playerInventory, inventory, ItemStack.of(Material.DIAMOND, 64), List.of(Pair.of(playerInventory, 0), Pair.of(playerInventory, 1), Pair.of(playerInventory, 2)));
        }, ItemStack.of(Material.DIAMOND, 61), Map.of(0, ItemStack.of(Material.DIAMOND), 1, ItemStack.of(Material.DIAMOND, 2), 2, ItemStack.of(Material.DIAMOND)), Map.of());
    }

    @Test
    public void full() {
        assertDouble((playerInventory, inventory) -> {
            playerInventory.setItemStack(1, ItemStack.of(Material.DIAMOND, 64));
            return ClickProcessor.rightDrag(playerInventory, inventory, ItemStack.of(Material.DIAMOND, 64), List.of(Pair.of(playerInventory, 0), Pair.of(playerInventory, 1), Pair.of(playerInventory, 2)));
        }, ItemStack.of(Material.DIAMOND, 62), Map.of(0, ItemStack.of(Material.DIAMOND), 2, ItemStack.of(Material.DIAMOND)), Map.of());
    }
}
