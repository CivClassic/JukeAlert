package com.untamedears.JukeAlert.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.untamedears.JukeAlert.SnitchManager;
import com.untamedears.JukeAlert.model.Snitch;
import com.untamedears.JukeAlert.model.SnitchFactory;
import com.untamedears.JukeAlert.model.SnitchTypeManager;
import com.untamedears.JukeAlert.model.actions.internal.DestroySnitchAction;
import com.untamedears.JukeAlert.model.actions.internal.DestroySnitchAction.Cause;

import vg.civcraft.mc.citadel.events.ReinforcementBypassEvent;
import vg.civcraft.mc.citadel.events.ReinforcementCreationEvent;
import vg.civcraft.mc.citadel.events.ReinforcementDestructionEvent;
import vg.civcraft.mc.citadel.model.Reinforcement;

public class SnitchLifeCycleListener implements Listener {

	private SnitchTypeManager configManager;
	private SnitchManager snitchManager;
	private Map<Location, SnitchFactory> pendingSnitches;
	private Logger logger;

	public SnitchLifeCycleListener(SnitchManager snitchManager, SnitchTypeManager configManager, Logger logger) {
		this.configManager = configManager;
		this.snitchManager = snitchManager;
		this.logger = logger;
		this.pendingSnitches = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack inHand = event.getItemInHand();
		SnitchFactory type = configManager.getConfig(inHand);
		if (type != null) {
			pendingSnitches.put(event.getBlock().getLocation(), type);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		SnitchFactory snitchConfig = pendingSnitches.remove(block.getLocation());
		if (snitchConfig == null) {
			return;
		}
		if (block.getType() == snitchConfig.getItem().getType()) {
			event.setDropItems(false);
			block.getWorld().dropItemNaturally(block.getLocation(), snitchConfig.getItem());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void createReinforcement(ReinforcementCreationEvent e) {
		Location location = e.getReinforcement().getLocation();
		SnitchFactory snitchConfig = pendingSnitches.get(location);
		if (snitchConfig == null) {
			return;
		}
		pendingSnitches.remove(location);
		Snitch snitch = snitchConfig.create(-1, location, "", e.getReinforcement().getGroupId(), true);
		Player p = e.getPlayer();
		logger.info(String.format("Created snitch of type %s at %s by %s", snitch.getType().getName(),
				snitch.getLocation().toString(), p != null ? p.getName() : "null"));
		snitchManager.addSnitch(snitch);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void reinforcementDestroyed(ReinforcementDestructionEvent e) {
		reinforcementGone(e.getReinforcement());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void reinforcementDestroyed(ReinforcementBypassEvent e) {
		reinforcementGone(e.getReinforcement());
	}

	private void reinforcementGone(Reinforcement rein) {
		Snitch snitch = snitchManager.getSnitchAt(rein.getLocation());
		if (snitch != null) {
			snitchManager.removeSnitch(snitch);
			snitch.processAction(new DestroySnitchAction(System.currentTimeMillis(), snitch, null, Cause.PLAYER));
			logger.info(String.format("Destroyed snitch of type %s at %s", snitch.getType().getName(),
					snitch.getLocation().toString()));
		}
	}

}
