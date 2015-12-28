package com.updg.paintball.Listeners;

import com.updg.paintball.Game;
import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.Models.enums.GameStatus;
import com.updg.paintball.PaintballPlugin;
import com.updg.paintball.Utils.EconomicSettings;
import com.updg.paintball.Utils.PScoreboard;
import com.updg.paintball.Utils.Utils;
import com.updg.CR_API.APIPlugin;
import com.updg.CR_API.DataServer.DSUtils;
import com.updg.CR_API.Models.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * @author Sceri
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player user = event.getPlayer();

        PPlayer p = PaintballPlugin.game.getPPlayer(user.getName());
        if (p == null) {
            p = new PPlayer(user);
            if (PaintballPlugin.game.getStatus() == GameStatus.WAITING) {
                if (PaintballPlugin.game.getActivePlayers() < PaintballPlugin.game.maxPlayers)
                    PaintballPlugin.game.addPlayer(p);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(null);
        p.getInventory().clear();
        p.teleport(PaintballPlugin.game.getLobby());
        p.setGameMode(GameMode.ADVENTURE);

        final PPlayer qcp = PaintballPlugin.game.getPPlayer(e.getPlayer().getName());
        PaintballPlugin.game.send();
        PScoreboard.updateSidebarScoreboard();
        PaintballPlugin.game.refreshTimer();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player user = e.getPlayer();
        if (PaintballPlugin.game.getStatus() == GameStatus.INGAME)
            PaintballPlugin.game.kickPlayer(user);
        else
            PaintballPlugin.game.removePlayer(user.getName());
        PaintballPlugin.game.refreshTimer();
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (!p.isDead()) {
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.getDrops().clear();
        e.setDeathMessage("");
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        final PPlayer pPlayer = PaintballPlugin.game.getPPlayer(p.getName());

        p.getInventory().clear();

        if (PaintballPlugin.game.getStatus() == GameStatus.INGAME) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(PaintballPlugin.plugin, new Runnable() {
                public void run() {
                    pPlayer.clearInventory();
                    pPlayer.setAdventureMode();
                    pPlayer.setDefaultArmor(pPlayer.getTeam());
                }
            }, 2);
            e.setRespawnLocation(pPlayer.getTeam().getSpawn());
        } else {
            if (PaintballPlugin.game.getStatus() == GameStatus.WAITING || PaintballPlugin.game.getStatus() == GameStatus.WAITING_VIP) {
                e.setRespawnLocation(PaintballPlugin.game.getLobby());
            } else {
                e.setRespawnLocation(pPlayer.getTeam().getSpawn());
            }
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if ((event.getPlayer().getGameMode() != GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        e.setCancelled(true);

        if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            PPlayer shooter = PaintballPlugin.game.getPPlayer(((Player) ((Snowball) e.getDamager()).getShooter()).getName());
            PPlayer target = PaintballPlugin.game.getPPlayer(((Player) e.getEntity()).getName());


            if (PaintballPlugin.game.getStatus() == GameStatus.INGAME && shooter.isActive() && target.isActive() && shooter.getTeam() != target.getTeam()) {
                shooter.addFrag(target);
                shooter.addBalls(8);
                target.clearInventory();
                target.takeKitStart();
                target.setDefaultArmor(target.getTeam());
                target.getBukkitPlayer().teleport(target.getTeam().getSpawn());
                PaintballPlugin.game.checkTeams();
            }
        }
    }


   /* @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (PaintballPlugin.game.getStatus() == GameStatus.INGAME) {
            Player p = event.getPlayer();
            Block up = p.getLocation().getBlock();
            if (up.getType() == Material.STONE_PLATE || up.getRelative(BlockFace.DOWN).getType() == Material.STONE_PLATE) {
                p.setVelocity(p.getLocation().getDirection().multiply(1.2));
                p.setVelocity(new Vector(p.getVelocity().getX(), 1.3D, p.getVelocity().getZ()));
            }
        }
    }  */

   /* @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if (e.getBlock().getTypeId() == 39) {
            e.setCancelled(true);
        }
    }   */

    @EventHandler
    public void onChangeHunger(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        PPlayer p = PaintballPlugin.game.getPPlayer(event.getPlayer().getName());
        APIPlayer pA = APIPlugin.getPlayer(p.getName());
        if (PaintballPlugin.game.getStatus() != GameStatus.INGAME) {
            event.setFormat(pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitPlayer().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
            return;
        }
        if (p.isSpectator() || p.team == null) {
            for (PPlayer item : PaintballPlugin.game.getSpectatorsArray()) {
                item.sendMessage(ChatColor.GRAY + "[Наблюдающий] " + ChatColor.RESET + pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitPlayer().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
            }
            event.setCancelled(true);
            return;
        }
        if (!event.getMessage().startsWith("!")) {
            event.setCancelled(true);
            if (p.team != null) {
                for (PPlayer item : p.team.getPlayers().values()) {
                    item.sendMessage(ChatColor.GRAY + "[Команда] " + ChatColor.RESET + pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitPlayer().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
                }
            } else {
                event.setFormat(pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitPlayer().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
                event.setCancelled(false);
            }
        } else {
            event.setFormat(pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitPlayer().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage().substring(1));
            event.setCancelled(false);
        }
    }
}
