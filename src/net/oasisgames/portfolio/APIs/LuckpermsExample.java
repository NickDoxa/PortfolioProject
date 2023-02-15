package net.oasisgames.portfolio.APIs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.metadata.NodeMetadataKey;

public class LuckpermsExample {

	private final Player player;
	private static LuckPerms luckPerms;
	private User user;
	private Group group;
	
	public static void initializeLuckperms() {
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		LuckPerms api;
		if (provider != null) {
			api = LuckPermsProvider.get();
		} else {
			api = null;
		}
		luckPerms = api;
	}
	
	public static LuckPerms getLuckPermsInstance() {
		return luckPerms;
	}
	
	public LuckPerms getLocalLuckPermsInstance() {
		return luckPerms;
	}
	
	//Determine whether data will be applied to the users group or the user themselves
	//Mark true or just user, mark false for user's entire group
	//NOTE: ONLY APPLIES TO USERS PRIMARY GROUP!
	public LuckpermsExample(Player p, boolean userOrGroup) {
		player = p;
		user = luckPerms.getUserManager().getUser(player.getUniqueId());
		group = !userOrGroup ? luckPerms.getGroupManager().getGroup(user.getPrimaryGroup()) : null;
	}
	
	public LuckpermsExample(Player p, String groupName) {
		player = p;
		group = luckPerms.getGroupManager().getGroup(groupName);
	}
	
	//Using protected to allow other API's in this package to send API specific data
	protected void sendAPIPermissionData(String node, boolean AddRemove, NodeMetadataKey<Object> data, Object API) {
		if (group == null) {
			applyUserNode(node, AddRemove, data, API);
		} else {
			applyGroupNode(node, AddRemove, data, API);
		}
	}

	/*
	 * @param Node = permission node as string
	 * @param AddRemove = boolean determining whether to add the given node (true) or remove it (false)
	 */
	public void sendPermissionData(String node, boolean AddRemove) {
		if (group == null) {
			applyUserNode(node, AddRemove);
		} else {
			applyGroupNode(node, AddRemove);
		}
	}
	
	
	/*
	 * @param Node = the node to compare for the user's node list
	 */
	private int check;
	public boolean doesPlayerHaveNode(String node) {
		check = 0;
		user.getNodes().forEach(nodes -> {
			if (nodes.getKey() == node) {
				check++;
			}
		});
		return check > 0;
	}
	
	private Node builder(String perm, boolean AddRemove, NodeMetadataKey<Object> data, Object API) {
		Node node = Node.builder(perm)
				.value(AddRemove)
				.build();
		if (data == null || API == null) return node;
		Node nodeWithData = Node.builder(perm)
				.value(AddRemove)
				.withMetadata(data, API)
				.build();
		return nodeWithData;
	}
	
	private void applyUserNode(String perm, boolean AddRemove, NodeMetadataKey<Object> data, Object API) {
		Node node = builder(perm, AddRemove, data, API);
		user.data().add(node);
		luckPerms.getUserManager().saveUser(user);
	}
	
	private void applyUserNode(String perm, boolean AddRemove) {
		Node node = builder(perm, AddRemove, null, null);
		user.data().add(node);
		luckPerms.getUserManager().saveUser(user);
	}
	
	private void applyGroupNode(String perm, boolean AddRemove, NodeMetadataKey<Object> data, Object API) {
		Node node = builder(perm, AddRemove, data, API);
		group.data().add(node);
		luckPerms.getGroupManager().saveGroup(group);
	}
	
	private void applyGroupNode(String perm, boolean AddRemove) {
		Node node = builder(perm, AddRemove, null, null);
		group.data().add(node);
		luckPerms.getGroupManager().saveGroup(group);
	}
	
	/*
	 * Static Option
	 */
	
	
	/*
	 * @param P = Player
	 * @param Node = permission node as string
	 * @param AddRemove = boolean determining whether to add the given node (true) or remove it (false)
	 * @param UserGroup = boolean determining whether to apply the given node to the user (true) or the user's group (false)
	 * @param groupName = if UserGroup is set to false for group usage, the group name must be included as a string
	 */
	public static void applyLuckPermsChange(Player p, String node, boolean AddRemove, boolean UserGroup, String groupName) {
		Node nodeData = Node.builder(node)
				.value(AddRemove)
				.build();
		if (UserGroup) {
			User user = luckPerms.getUserManager().getUser(p.getUniqueId());
			user.data().add(nodeData);
			luckPerms.getUserManager().saveUser(user);
		} else {
			Group group = luckPerms.getGroupManager().getGroup(groupName);
			group.data().add(nodeData);
			luckPerms.getGroupManager().saveGroup(group);
		}
	}
	
}
