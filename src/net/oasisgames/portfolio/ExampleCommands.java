package net.oasisgames.portfolio;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.oasisgames.portfolio.APIs.LuckpermsExample;
import net.oasisgames.portfolio.APIs.ModelEngineExample;
import net.oasisgames.portfolio.GUI.GUIExample;
import net.oasisgames.portfolio.particles.ExampleParticles;
import net.oasisgames.portfolio.particles.ExampleParticles.ParticleShape;

public class ExampleCommands implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!label.equalsIgnoreCase("example")) return false;
			if (args.length != 1) return false;
			switch (args[0].toLowerCase()) {
			
			//MODEL ENGINE EXAMPLE COMMAND
				case "modelengine" :
					ModelEngineExample model = new ModelEngineExample(player, "example", player.getWorld(), player.getLocation(), EntityType.PIG, true);
					model.setDisplayName(player.getName() + "'s Model Example!");
					player.sendMessage(ChatColor.GREEN + "Model Engine API spawned the model from config named: " + model.getModelName());
					break;
					
			//LUCK PERMS EXAMPLE COMMAND
				case "luckperms" :
					String node = "Example.Example";
					LuckpermsExample lp = new LuckpermsExample(player, true);
					//True if the node is not in the list and needs to be added, false if it is in the list and needs to be removed
					boolean AddRemove = !lp.doesPlayerHaveNode(node);
					lp.sendPermissionData(node, AddRemove);
					String msg = AddRemove ? ChatColor.GREEN + "The node '" + node + "' has been added to your permissions" : 
						ChatColor.RED + "The node '" + node + "' has been removed from your permissions";
					player.sendMessage(msg);
					break;
					
			//PARTICLES EXAMPLE
				case "circle" :
					ExampleParticles circleParticles = new ExampleParticles(player);
					circleParticles.createParticleShape(ParticleShape.CIRCLE, 3);
					player.sendMessage(ChatColor.GREEN + "Generating circle particle!");
					break;
				case "triangle" :
					ExampleParticles triangleParticles = new ExampleParticles(player);
					triangleParticles.createParticleShape(ParticleShape.CIRCLE, 3);
					player.sendMessage(ChatColor.GREEN + "Generating circle particle!");
					break;
					
			//GUI EXAMPLE
				case "gui" :
					GUIExample gui = new GUIExample(player, 27, ChatColor.AQUA + "Portfolio GUI");
					gui.openGUI(player);
					break;
			}
		} else {
			System.out.println("Sorry console, you cant test this plugin!");
		}
		return false;
	}

}
