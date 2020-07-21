package com.untamedears.jukealert.model.actions.impl;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.untamedears.jukealert.model.actions.abstr.LoggablePlayerVictimAction;

import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;

public class DismountEntityAction extends LoggablePlayerVictimAction {
	
	public static final String ID = "DISMOUNT_ENTITY";

	public DismountEntityAction(long time, UUID player, Location location, String victim) {
		super(time, player, location, victim);
	}

	@Override
	public IClickable getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.SADDLE);
		super.enrichGUIItem(is);
		return new DecorationStack(is);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	protected String getChatRepresentationIdentifier() {
		return "Dismounted " + getVictim();
	}

}
