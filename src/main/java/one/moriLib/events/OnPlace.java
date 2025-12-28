package one.moriLib.events;

import one.moriLib.entities.Block;
import one.moriLib.patcher.Patcher;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class OnPlace implements Listener {
    private final Patcher patcher;

    public OnPlace(Patcher patcher) {
        this.patcher = patcher;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasCustomModelData()) return;

        int customModelData = meta.getCustomModelData();

        org.bukkit.block.Block block = event.getBlockPlaced();
        if (block.getType() != Material.NOTE_BLOCK) return;

        for (Block b : patcher.getBlocks()) {
            if (b.getId() == customModelData) {
                BlockData data = block.getBlockData();

                if (data instanceof NoteBlock noteBlock) {
                    noteBlock.setPowered(b.getPowered());
                    noteBlock.setNote(b.getNote());
                    noteBlock.setInstrument(b.getInstrument());
                    block.setBlockData(noteBlock, true);
                }
            }
        }
    }
}
