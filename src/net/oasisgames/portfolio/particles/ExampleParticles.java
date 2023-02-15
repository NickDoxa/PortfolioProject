package net.oasisgames.portfolio.particles;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ExampleParticles {

	private final Player player;
	public ExampleParticles(Player p) {
		player = p;
	}
	
	public enum ParticleShape {
		CIRCLE,
		TRIANGLE,
	}
	
	public void createParticleShape(ParticleShape shape, double radius) {
		ParticlePoint creator = new ParticlePoint(radius, player.getWorld());
		switch (shape) {
			case CIRCLE:
				creator.drawCircle(Particle.VILLAGER_HAPPY, player.getLocation());
				break;
			case TRIANGLE:
				creator.drawTriangle(Particle.SOUL_FIRE_FLAME, player.getLocation());
				break;
			default:
				player.sendMessage(ChatColor.RED + "Invalid shape selected. Speak with developer.");
				break;
		}
	}
	
}

class ParticlePoint {
	
	private final double radius;
	private final World world;
	public ParticlePoint(double r, World w) {
		radius = r;
		world = w;
	}
	
	public void drawCircle(Particle particle, Location location) {
		double[][] circlePoints = getCirclePoints(location.getX(), location.getY(), location.getZ());
		int numPoints = (int)(2*Math.PI*radius/0.1);
		Location[] locs = convertPointsToLocations(circlePoints, numPoints);
		for (int i = 0; i < locs.length; i++) {
			world.spawnParticle(particle, locs[i], 1);
		}
	}
	
	public void drawTriangle(Particle particle, Location location) {
		double[][] trianglePoints = getTrianglePoints(location.getX(), location.getY(), location.getZ());
		int numPoints = (int)(2*Math.PI*radius/0.1);
		Location[] locs = convertPointsToLocations(trianglePoints, numPoints);
		for (int i = 0; i < locs.length; i++) {
			world.spawnParticle(particle, locs[i], 1);
		}
	}
	
	private double[][] getCirclePoints(double x, double y, double z) {
	    int numPoints = (int)(2*Math.PI*radius/0.1);
	    double[][] points = new double[numPoints][3];
	    double theta = 0;
	    for(int i = 0; i < numPoints; i++, theta += 0.1/radius) {
	        points[i][0] = x + radius * Math.cos(theta);
	        points[i][1] = y + radius * Math.sin(theta);
	        points[i][2] = z;
	    }
	    return points; 
	}
	
	private double[][] getTrianglePoints(double x, double y, double z) {
	    double[][] points = new double[3][2];

	    points[0][0] = x + radius * Math.cos(Math.toRadians(60));
	    points[0][1] = y + radius * Math.sin(Math.toRadians(60));
	    points[1][0] = x;
	    points[1][1] = y + radius;
	    points[2][0] = x - radius * Math.cos(Math.toRadians(60));
	    points[2][1] = y + radius * Math.sin(Math.toRadians(60));

	    points[0][2] = z;
	    points[1][2] = z;
	    points[2][2] = z;

	    return points;
	}
	
	private Location[] convertPointsToLocations(double[][] points, int numPoints) {
		Location[] locs = new Location[] {};
		for (int i = 0; i < numPoints; i++) {
			locs[i] = new Location(world, points[i][0], points[i][1], points[i][2]);
		}
		return locs;
	}
	
}
