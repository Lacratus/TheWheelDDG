package be.live.jonas2000.TheWheelDDG.listeners;

import be.live.jonas2000.TheWheelDDG.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WheelListener implements Listener {
    Main plugin;

    public WheelListener() {
    }


    @EventHandler
    public void onClick(InventoryClickEvent e){


        //Zorgt ervoor dat niemand iets uit het rad kan halen
        if(ChatColor.translateAlternateColorCodes('&',e.getClickedInventory().getTitle()).equalsIgnoreCase(ChatColor.DARK_RED + "Spinning the wheel!")) {
            e.setCancelled(true);
        }
    }
}
