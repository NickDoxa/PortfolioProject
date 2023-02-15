package net.oasisgames.portfolio.APIs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;

import net.oasisgames.portfolio.Main;

public class ModelEngineExample {

	private final ModeledEntity modelEntity;
	private final String modelName;
	private final Player owner;
	private final Entity entity;
	private final EntityType mobType;
	
	private static Map<UUID, ModeledEntity> entityMap = new HashMap<UUID, ModeledEntity>();
	
	public ModelEngineExample(Player player, String name, World world, Location spawn, EntityType entityType, boolean invisible) {
		modelName = Main.getPluginConfig().getString("models." + name);
		mobType = entityType;
		owner = player;
		ActiveModel model = ModelEngineAPI.createActiveModel(modelName);
		Entity mob = world.spawnEntity(spawn, mobType);
		modelEntity = ModelEngineAPI.createModeledEntity(mob);
		entity = mob;
		modelEntity.addModel(model, true);
		modelEntity.setBaseEntityVisible(invisible);
		entityMap.put(owner.getUniqueId(), modelEntity);
	}
	
	public void teleportToOwner() {
		entity.teleport(owner.getLocation());
	}
	
	public void setDisplayName(String name) {
		entity.setCustomName(name);
		entity.setCustomNameVisible(true);
	}
	
	public String getModelName() {
		return modelName;
	}
	
	/*
	 * Static access to all current entities or specific static access to an entity by key of owner
	 */
	
	public static ModeledEntity[] getAllLivingModels() {
		ModeledEntity[] entities = new ModeledEntity[] {};
		int count = 0;
		for (UUID set : entityMap.keySet()) {
			if (!entityMap.containsKey(set)) continue;
			entities[count++] = entityMap.get(set);
		}
		return entities;
	}
	
	public static ModeledEntity getLivingModel(Player player) {
		if (!entityMap.containsKey(player.getUniqueId())) return null;
		return entityMap.get(player.getUniqueId());
	}
	
}
