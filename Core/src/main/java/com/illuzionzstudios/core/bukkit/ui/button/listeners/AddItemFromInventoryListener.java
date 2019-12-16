package com.illuzionzstudios.core.bukkit.ui.button.listeners;

import com.illuzionzstudios.core.bukkit.ui.button.InterfaceClickListener;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public abstract class AddItemFromInventoryListener implements InterfaceClickListener {

    private boolean moveAll = false;

    public AddItemFromInventoryListener(boolean moveAll) {
        this.moveAll = moveAll;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        int firstSlot = event.getInventory().getSize() + 27;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        ItemStack toAdd = event.getCurrentItem().clone();
        ItemStack remaining = event.getCurrentItem();

        if (!moveAll) {
            toAdd.setAmount(1);
        }

        if (addItem(player, toAdd)) {
            if (!moveAll) {
                if (remaining.getAmount() > 1) {
                    remaining.setAmount(remaining.getAmount() - 1);
                } else {
                    remaining = new ItemStack(Material.AIR);
                }
            } else {
                remaining = new ItemStack(Material.AIR);
            }

            player.getInventory().setItem(event.getSlot(), remaining);
        }
    }

    public abstract boolean addItem(Player wp, ItemStack i);
}
