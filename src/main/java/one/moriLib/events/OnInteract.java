package one.moriLib.events;

import one.moriLib.patcher.Patcher;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class OnInteract implements Listener {
    private final JavaPlugin plugin;
    private final Patcher patcher;

    public OnInteract(JavaPlugin plugin, Patcher patcher) {
        this.plugin = plugin;
        this.patcher = patcher;
    }

    @EventHandler
    public void onRedstoneUpdate(BlockRedstoneEvent event) {
        if (event.getBlock().getType() == Material.NOTE_BLOCK) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getClickedBlock() != null &&
                event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);

            Block clickedBlock = event.getClickedBlock();
            BlockFace face = event.getBlockFace();
            Block targetBlock = clickedBlock.getRelative(face);

            if (targetBlock.getType() == Material.AIR) {
                ItemStack handItem = event.getItem();
                Material toPlace = (handItem != null && handItem.getType() != Material.AIR)
                        ? handItem.getType()
                        : Material.AIR;

                resetNoteBlock(targetBlock, 0, 1, 0);
                resetNoteBlock(targetBlock, 0, -1, 0);
                targetBlock.setType(toPlace);

                if (toPlace.equals(Material.NOTE_BLOCK)) {
                    if (!handItem.hasItemMeta()) return;

                    ItemMeta meta = handItem.getItemMeta();
                    if (!meta.hasCustomModelData()) return;

                    int customModelData = meta.getCustomModelData();

                    for (one.moriLib.entities.Block b : patcher.getBlocks()) {
                        if (b.getId() == customModelData) {
                            BlockData data = targetBlock.getBlockData();

                            if (data instanceof NoteBlock noteBlock) {
                                noteBlock.setPowered(b.getPowered());
                                noteBlock.setNote(b.getNote());
                                noteBlock.setInstrument(b.getInstrument());
                                targetBlock.setBlockData(noteBlock, true);
                            }
                        }
                    }
                }

                if (handItem != null && handItem.getType() != Material.AIR &&
                        event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    handItem.setAmount(handItem.getAmount() - 1);
                    event.getPlayer().getInventory().setItemInMainHand(handItem);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onNoteBlockPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        } else {
            Block placedBlock = event.getBlock();
            resetNoteBlock(placedBlock, 0, -1, 0);
        }
    }

    public void resetNoteBlock(Block block_event, int x, int y, int z) {
        Block above = block_event.getRelative(x, y, z);

        if (above.getType() != Material.NOTE_BLOCK) return;

        NoteBlock noteBlock = (NoteBlock) above.getBlockData();
        Instrument originalInstrument = noteBlock.getInstrument();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Block block = above;
            if (block.getBlockData() instanceof NoteBlock nb) {
                nb.setInstrument(originalInstrument);
                block.setBlockData(nb, false);
            }
        }, 1L);
    }

    @EventHandler
    public void onBlockPlacedUnderNoteBlock(BlockPlaceEvent event) {
        Block placedBlock1 = event.getBlockPlaced();
        resetNoteBlock(placedBlock1, 0, 1, 0);
        Block placedBlock2 = event.getBlock();
        resetNoteBlock(placedBlock2, 0, -1, 0);
    }
    @EventHandler
    public void onBlockPlacedUnderNoteBlock(BlockBreakEvent event) {
        Block placedBlock = event.getBlock();
        resetNoteBlock(placedBlock, 0, -1, 0);
    }
}
