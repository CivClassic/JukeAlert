package com.untamedears.jukealert.model.actions.impl;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.untamedears.jukealert.model.actions.abstr.LoggablePlayerVictimAction;

import vg.civcraft.mc.civmodcore.api.ItemNames;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;

public class EnterVehicleAction extends LoggablePlayerVictimAction {

	public static final String ID = "ENTER_VEHICLE";
	
	public EnterVehicleAction(long time, UUID player, Location location, String victim) {
		super(time, player, location, victim);
	}

	@Override
	public IClickable getGUIRepresentation() {
		ItemStack is = new ItemStack(getVehicle());
		super.enrichGUIItem(is);
		return new DecorationStack(is);
	}

	/**
	 * @return Material of the entered vehicle
	 */
	public Material getVehicle() {
		return Material.valueOf(victim);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	protected String getChatRepresentationIdentifier() {
		return "Entered " + ItemNames.getItemName(getVehicle());
	}

}
