package com.updg.paintball.Listeners;

import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.Models.enums.GameStatus;
import com.updg.paintball.PaintballPlugin;
import com.updg.paintball.Utils.EconomicSettings;
import com.updg.paintball.Utils.Utils;
import com.updg.CR_API.DataServer.DSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

/**
 * @author Sceri
 */
public class GameListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getItemInHand().getType() != Material.SNOW_BALL)
            e.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }
}