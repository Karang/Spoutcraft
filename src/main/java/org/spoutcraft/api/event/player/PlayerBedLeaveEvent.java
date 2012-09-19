/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft is licensed under the GNU Lesser General Public License.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.api.event.player;

import org.spoutcraft.api.block.Block;
import org.spoutcraft.api.entity.Player;
import org.spoutcraft.api.event.HandlerList;

public class PlayerBedLeaveEvent extends PlayerEvent<PlayerBedLeaveEvent> {
	protected Block bed;

	protected PlayerBedLeaveEvent(Player player, Block bed) {
		super(player);
		this.bed = bed;
	}

	public Block getBed() {
		return bed;
	}

	public static final HandlerList<PlayerBedLeaveEvent> handlers = new HandlerList<PlayerBedLeaveEvent>();

	@Override
	public HandlerList<PlayerBedLeaveEvent> getHandlers() {
		return handlers;
	}

	@Override
	protected String getEventName() {
		return "Player Bed Enter Event";
	}
}
