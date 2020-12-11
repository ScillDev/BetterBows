package me.scill.betterbows.bows;

import me.scill.betterbows.BetterBows;
import me.scill.betterbows.BlockType;
import me.scill.betterbows.CustomBow;
import me.scill.betterbows.utilities.CommonUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TrapBow extends CustomBow {

	private final BetterBows plugin;

	public TrapBow(final BetterBows plugin) {
		super(plugin, "trap");
		this.plugin = plugin;
	}

	@Override
	public void activateAbility(final EntityDamageByEntityEvent event) {
		final Location arrowLocation = CommonUtil.getAccurateProjectileHit((Projectile) event.getDamager(), true);
		final List<Location> trapLocations = CommonUtil.getLocationsBetween
				(arrowLocation.clone().add(2, 2, 2), arrowLocation.clone().add(-1, 0, -1));

		final List<BlockType> trapBlocks = new ArrayList<>();

		trapLocations.stream()
				.filter(trapLocation -> trapLocation.getBlock().getType() == Material.AIR
						&& !(trapLocation.getBlockX() == arrowLocation.getBlockX()
						&& trapLocation.getBlockY() == arrowLocation.getBlockY()
						&& trapLocation.getBlockZ() == arrowLocation.getBlockZ()))
				.forEach(trapLocation -> {
			final Block block = trapLocation.getBlock();
			trapBlocks.add(new BlockType(block));

			if (trapLocation.getBlockY() == arrowLocation.getBlockY() + 1)
				block.getLocation().getBlock().setType(Material.OBSIDIAN);
			else
				block.getLocation().getBlock().setType(Material.GLASS);
		});

		final int[] timer = {0};
		new BukkitRunnable() {
			@Override
			public void run() {
				if (timer[0]++ == 20) {
					for (BlockType trapBlock : trapBlocks)
						trapBlock.getLocation().getBlock().setType(trapBlock.getMaterial());
					cancel();
				}
			}
		}.runTaskTimer(plugin,0L,5L);
	}
}