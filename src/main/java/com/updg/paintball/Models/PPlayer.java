package com.updg.paintball.Models;

import com.updg.paintball.Game;
import com.updg.paintball.Models.enums.GameStatus;
import com.updg.paintball.PaintballPlugin;

import java.util.logging.Level;

import com.updg.paintball.Utils.ColorizeArmor;
import com.updg.paintball.Utils.EconomicSettings;
import com.updg.CR_API.Bungee.Bungee;
import com.updg.CR_API.DataServer.DSUtils;
import com.updg.CR_API.Utils.L;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R1.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * User: Alex
 * Date: 14.06.13
 * Time: 18:24
 */
public class PPlayer {
    private Player bukkitPlayer;
    private String name;
    private PPlayerStat stat;

    private int id;
    private int score = 0;

    private long shotTime = 0;

    private boolean reload = false;
    private int rang;
    private int vip;

    public PTeam team;

    public PPlayer(Player player) {
        this.bukkitPlayer = player;
        this.name = player.getName();
        this.stat = new PPlayerStat();
    }

    public void playEffect(String particleName, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
        try {
            Player player = this.getBukkitPlayer();
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particleName, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addFrag(PPlayer victim) {
        DSUtils.addPlayerExpAndMoney(this.getBukkitPlayer(), EconomicSettings.kill, 0);

        stat.addKill(System.currentTimeMillis() / 1000, victim.getId());
        victim.getTeam().minusPoint();
        victim.addDeaths();

        Bukkit.broadcastMessage(Game.pluginPrefix + "Игрок " + getName() + " убил " + victim.getName());
    }

    public PPlayerStat getStat() {
        return this.stat;
    }

    public void forceToLobby() {
        Bungee.teleportPlayer(getBukkitPlayer(), "lobby");
    }

    public void addExp(int exp) {
        DSUtils.addPlayerExpAndMoney(this.getBukkitPlayer(), exp, 0);
    }

    public void addDeaths() {
        this.stat.addDeath();
    }

    public void sendMessage(String msg) {
        bukkitPlayer.sendMessage(msg);
    }

    public void setAdventureMode() {
        this.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
    }

    public void takeKitStart() {
        if (PaintballPlugin.game.getStatus() == GameStatus.INGAME && !this.isSpectator()) {
            this.getBukkitPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
            this.getBukkitPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
        } else {
            // LOBBY STAFF
        }
    }

    public void setDefaultArmor(PTeam team) {
        ItemStack chestplate = ColorizeArmor.c(new ItemStack(Material.LEATHER_CHESTPLATE, 1), team);
        this.getBukkitPlayer().getInventory().setChestplate(chestplate);
        ItemStack boots = ColorizeArmor.c(new ItemStack(Material.LEATHER_BOOTS, 1), team);
        this.getBukkitPlayer().getInventory().setBoots(boots);
        ItemStack helmet = ColorizeArmor.c(new ItemStack(Material.LEATHER_HELMET, 1), team);
        this.getBukkitPlayer().getInventory().setHelmet(helmet);
        ItemStack leggins = ColorizeArmor.c(new ItemStack(Material.LEATHER_LEGGINGS, 1), team);
        this.getBukkitPlayer().getInventory().setLeggings(leggins);
    }

    public void clearInventory() {
        this.getBukkitPlayer().setFireTicks(0);
        this.getBukkitPlayer().closeInventory();
        this.getBukkitPlayer().setHealth(20);
        this.getBukkitPlayer().setExp(0);
        this.getBukkitPlayer().setFoodLevel(20);
        this.getBukkitPlayer().getInventory().clear();
        this.getBukkitPlayer().getInventory().setHelmet(null);
        this.getBukkitPlayer().getInventory().setChestplate(null);
        this.getBukkitPlayer().getInventory().setLeggings(null);
        this.getBukkitPlayer().getInventory().setBoots(null);
    }

    public void addBalls(int count) {
        this.getBukkitPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, count));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShotTime(long time) {
        this.shotTime = time;
    }

    public int getScore() {
        return score;
    }

    public boolean isReloading() {
        return this.reload;
    }

    public void setReloading(boolean reload) {
        this.reload = reload;
    }

    public long getShotTime() {
        return this.shotTime;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Player getBukkitPlayer() {
        return this.bukkitPlayer;
    }

    public int plusScore() {
        return ++this.score;
    }

    public void playSound(String sound) {
        Player p = this.getBukkitPlayer();
        PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ(), 1.0F, 1.0F);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public boolean isSpectator() {
        //TODO
        return false;
    }

    public PTeam getTeam() {
        return team;
    }

    public boolean isActive() {
        return !(isSpectator() || getStat().isExit());
    }
}
