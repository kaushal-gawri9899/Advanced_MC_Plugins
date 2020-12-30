package org.jpwilliamson.arena.model.paintBall;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.EntityUtil;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;

public class PaintBall implements Listener {

	public PaintBall() {
	}

	@EventHandler
	public void onPlayerinteract(PlayerInteractEvent event) {

		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem().hasItemMeta()) {
				Player player = event.getPlayer();
				ItemStack item = event.getItem();
				if (event.getItem().getType() == Material.CARROT_ON_A_STICK) {
					if (item.getItemMeta().getCustomModelData() == 1000015 || item.getItemMeta().getCustomModelData() == 1000016) {
						Arrow ar = (Arrow)player.getWorld().spawn(player.getEyeLocation(), Arrow.class);
						ar.setVelocity(player.getEyeLocation().getDirection().multiply(6.0D));
						ar.setGravity(false);
						if (item.getItemMeta().getCustomModelData() == 1000015) {
							CompMetadata.setMetadata(ar, "paintball red");
						} else {
							CompMetadata.setMetadata(ar, "paintball blue");
						}

						EntityUtil.trackFlying(ar, () -> {
							CompParticle.FIREWORKS_SPARK.spawn(ar.getLocation());
						});
					}
				}
			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (CompMetadata.hasMetadata(event.getEntity(), "paintball red") || CompMetadata.hasMetadata(event.getEntity(), "paintball blue")) {
			Projectile projectile = event.getEntity();
			Entity ent = event.getHitEntity();
			if (ent instanceof LivingEntity) {
				LivingEntity lent = (LivingEntity)ent;
				projectile.remove();
				lent.damage(10.0D);
				projectile.remove();
			} else {
				Block b = event.getHitBlock();
				if (b != null) {
					if (CompMetadata.hasMetadata(event.getEntity(), "paintball red")) {
						b.setType(Material.RED_WOOL);
					} else {
						b.setType(Material.BLUE_WOOL);
					}
				}

				projectile.remove();
			}
		}
	}
}

