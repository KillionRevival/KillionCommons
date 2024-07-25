package co.killionrevival.killioncommons.listeners;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.items.Crowbar;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class CrowbarListeners implements Listener {
    @EventHandler
    public void onCrowbarPlace(final BlockPlaceEvent event) {
        if (Crowbar.isCrowbar(event.getItemInHand())) {
            event.setCancelled(true);
            KillionCommons.getApi()
                          .getMessageUtil()
                          .sendMessage(event.getPlayer(), "You can't place a crowbar.");
        }
    }

    @EventHandler
    public void onCrowbarInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        final Block block = event.getClickedBlock();

        if (block.getType() != Material.SPAWNER) return;

        final ItemStack itemInHand = event.getItem();

        if (itemInHand == null) return;
        if (!Crowbar.isCrowbar(itemInHand)) return;

        final CreatureSpawner spawner = (CreatureSpawner) block.getState();
        final ItemStack spawnerAsItem = new ItemStack(spawner.getType(), 1);
        final BlockStateMeta meta = (BlockStateMeta) spawnerAsItem.getItemMeta();
        final CreatureSpawner spawnerMeta = (CreatureSpawner) meta.getBlockState();
        spawnerMeta.setSpawnedType(spawner.getSpawnedType());
        meta.setBlockState(spawnerMeta);
        spawnerAsItem.setItemMeta(meta);

        block.setType(Material.AIR);
        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }
        else {
            event.getPlayer().getInventory().remove(itemInHand);
        }

        event.getPlayer().getInventory().addItem(spawnerAsItem);
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpawnerPlace(final BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        Block block = event.getBlockPlaced();
        if (itemInHand.getType() == Material.SPAWNER) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemInHand.getItemMeta();
            CreatureSpawner creatureSpawner = (CreatureSpawner) blockStateMeta.getBlockState();
            CreatureSpawner blockCreatureSpawner = (CreatureSpawner) block.getState();
            blockCreatureSpawner.setSpawnedType(creatureSpawner.getSpawnedType());
            blockCreatureSpawner.update();
        }
    }
}
