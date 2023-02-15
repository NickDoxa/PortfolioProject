package net.oasisgames.portfolio.GUI;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class GUI implements Listener {
	
	private final int size;
	private final String title;
	private final InventoryHolder owner;
	
	/*
	 * Use GUI constructor method in extended class constructor with super()!
	 * GUI API Made by Nick Doxa
	 */
	
	protected GUI(InventoryHolder o, int s, String t) {
		size = s;
		title = ChatColor.translateAlternateColorCodes('&', t);
		owner = o;
	}
	
	protected int getInventorySize() {
		return size;
	}
	
	protected String getInventoryTitle() {
		return title;
	}
	
	protected InventoryHolder getInventoryHolder() {
		return owner;
	}

	private Map<Player, Inventory> inv_map = new HashMap<Player, Inventory>();
	
	private void setPlayerInventory(Player player, Inventory inv) {
		inv_map.put(player, inv);
	}
	
	protected Inventory getInventory(Player player) {
		if (inv_map.containsKey(player)) {
			return inv_map.get(player);
		} else {
			this.createInventory(player);
			return inv_map.get(player);
		}
	}
	
	private Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	private Map<Integer, Boolean> click = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> take = new HashMap<Integer, Boolean>();
	
	private boolean hasAction(int slot) {
		return click.containsKey(slot) ? click.get(slot) : false;
	}
	
	private void addAction(int slot) {
		click.put(slot, true);
	}
	
	private ItemStack getItem(int slot) {
		return items.containsKey(slot) ? items.get(slot) : new ItemStack(Material.AIR);
	}
	
	private void setItemTake(int slot, boolean takeObject) {
		take.put(slot, takeObject);
	}
	
	private boolean allowObjectTake(int slot) {
		return take.containsKey(slot) ? take.get(slot) : false;
	}
	
	private void addItemToList(ItemStack i, int slot) {
		items.put(slot, i);
	}
	
	/* Public method for adding an item to the GUI
	 * Method Variable Definitions (in order of left to right):
	 * @param i = ItemStack of item to add, 
	 * @param useMeta = Whether or not you will apply custom ItemMeta to the item, 
	 * @param meta = the ItemMeta to apply to the item,
	 * @param slot = What slot # in the GUI will the item be in, 
	 * @param clickable = Does this item have an action associated or is it just for display,
	 * @param takeObject = Whether or not the player should be able to remove the object from the GUI and place it in their own inventory
	 */
	public void addItem(ItemStack i, boolean useMeta, ItemMeta meta, int slot, boolean clickable, boolean takeObject) {
		if (useMeta) {
			i.setItemMeta(meta);
		}
		if (clickable) {
			this.addAction(slot);
		}
		this.setItemTake(slot, takeObject);
		this.addItemToList(i, slot);
	}
	
	//This method is called within the openGUI method but in the event that it needs to be called outside of that it is protected not private
	protected void createInventory(Player player) {
		Inventory inv = Bukkit.createInventory(owner, size, title);
		for (int slot=0;slot<size;slot++) {
			ItemStack i = this.getItem(slot);
			inv.setItem(slot, i);
		}
		this.setPlayerInventory(player, inv);
	}
	
	//Public method for creating the GUI and opening it
	public void openGUI(Player player) {
		Inventory inv = this.getInventory(player);
		player.openInventory(inv);
	}
	
	//CREATE ACTIONS FOR CLICKABLE OBJECTS WITH THIS IN YOUR CLASS EXTENDED FROM THIS
	public abstract void onClick(Player player, int slot, InventoryClickEvent event);
	
	//Public event for GUI click
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			if(!event.getView().getTitle().contains(title))
				return;
			if (event.getCurrentItem() == null)
				return;
			if (event.getCurrentItem().getItemMeta() == null)
				return;
			int slot = event.getSlot();
			event.setCancelled(this.allowObjectTake(slot));
			if(event.getClickedInventory().getType() == InventoryType.PLAYER)
				return;
			
			Player player = (Player) event.getWhoClicked();
			if (this.hasAction(slot)) {
				this.onClick(player, slot, event);
			} else {
				return;
			}
		}
	}
	
}