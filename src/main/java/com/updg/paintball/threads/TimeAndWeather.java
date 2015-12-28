package com.updg.paintball.threads;

import org.bukkit.World;

/**
 * Created by Alex
 * Date: 26.02.14  22:51
 */
public class TimeAndWeather extends Thread {

    private World world;

    public TimeAndWeather(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        world.setTime(6500);
    }


}
