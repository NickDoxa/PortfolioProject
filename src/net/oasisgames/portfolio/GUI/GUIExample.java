package net.oasisgames.portfolio.GUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class GUIExample extends GUI implements Listener {

	private final Player owner;
	
	public GUIExample(Player player, int size, String title) {
		super(player, size, title);
		owner = player;
		this.initializeGUI();
	}
	
	private void initializeGUI() {
		Object[] skull = this.getPlayerSkull(owner.getName());
		addItem((ItemStack) skull[0], true, (SkullMeta) skull[1], 0, false, false);
		addItem(new ItemStack(Material.DIAMOND), false, null, 1, false, true);
		addItem(new ItemStack(Material.REDSTONE), false, null, 2, true, false);
	}

	private Object[] getPlayerSkull(String playerName) {
		Object[] outputs = new Object[2];
	    ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
	    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
	    skullMeta.setOwningPlayer(Bukkit.getPlayer(playerName));
	    skull.setItemMeta(skullMeta);
	    outputs[0] = skull;
	    outputs[1] = skullMeta;
	    return outputs;
	}
	
	@Override
	public void onClick(Player player, int slot, InventoryClickEvent event) {
		if (slot == 2) {
			player.closeInventory();
		}
	}
	
}
