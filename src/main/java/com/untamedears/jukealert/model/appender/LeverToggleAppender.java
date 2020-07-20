package com.untamedears.jukealert.model.appender;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.events.LoggableActionEvent;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.appender.annotations.AppenderEventHandler;
import com.untamedears.jukealert.model.appender.config.LeverToggleConfig;

public class LeverToggleAppender extends ConfigurableSnitchAppender<LeverToggleConfig> {
	
	public static final String ID = "levertoggle";
	
	private boolean shouldToggle;

	public LeverToggleAppender(Snitch snitch, ConfigurationSection config) {
		super(snitch, config);
		if (snitch.getId() != -1) {
			this.shouldToggle = JukeAlert.getInstance().getDAO().getToggleLever(snitch.getId());
		}
	}

	@Override
	public boolean runWhenSnitchInactive() {
		return false;
	}

	@AppenderEventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void acceptAction(LoggableActionEvent event) {
		if (!shouldToggle) {
			return;
		}
		for(LeverToggleConfig.SideEntry entry : config.getEntries(event.getAction().getIdentifier())) {
			Block leverBlock = snitch.getLocation().getBlock().getRelative(entry.getFace());
			if (leverBlock.getType() != Material.LEVER) {
				continue;
			}
			Directional dir = (Directional) leverBlock.getBlockData();
			if (dir.getFacing() != entry.getFace()) {
				continue;
			}
			Powerable power = (Powerable) leverBlock.getBlockData();
			power.setPowered(true);
		}
	}
	
	public boolean shouldToggle() {
		return shouldToggle;
	}
	
	public void switchState() {
		shouldToggle = !shouldToggle;
		snitch.setDirty();
	}

	@Override
	public Class<LeverToggleConfig> getConfigClass() {
		return LeverToggleConfig.class;
	}

}
