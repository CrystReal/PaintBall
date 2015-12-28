package com.updg.paintball.Listeners;

import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.PaintballPlugin;
import com.updg.CR_API.Events.BungeeReturnIdEvent;
import com.updg.CR_API.Events.LobbyUpdateCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Alex
 * Date: 26.01.14  1:23
 */
public class SystemListener implements Listener {
    @EventHandler
    public void onId(BungeeReturnIdEvent e) {
        PPlayer p = PaintballPlugin.game.getPPlayer(e.getUsername());
        if (p != null) {
            p.setId(e.getId());
            p.setRang(e.getRang());
            p.setVip(e.getVip());
        }
    }

    @EventHandler
    public void onReqUpdate(LobbyUpdateCheckEvent e) {
        PaintballPlugin.game.send();
    }
}
