package org.jpwilliamson.multiplex.model.blackOps.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class blackOpsListener implements Listener {

	@EventHandler
	public void MonsterDeath(EntityDeathEvent event){
		Entity entity = event.getEntity();

		if(entity instanceof Monster){
			entity.getWorld().dropItem(entity.getLocation(),new ItemStack(Material.CARROT));
		}
	}
}
