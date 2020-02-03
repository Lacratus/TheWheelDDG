package be.live.jonas2000.TheWheelDDG.commands;

import be.live.jonas2000.TheWheelDDG.help.ItemConverter;
import be.live.jonas2000.TheWheelDDG.help.ItemTagHandler;
import be.live.jonas2000.TheWheelDDG.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;

public class WheelCommand implements CommandExecutor {

    Main main;

    public WheelCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                ItemStack inHand = player.getInventory().getItemInMainHand();
                if (ItemTagHandler.hasString(inHand, "nbt.mtcustom")) {
                    if (ItemTagHandler.getString(inHand, "nbt.mtcustom").equals("wheelcoin")) {
                        player.sendMessage("GOEDZO!!!!!!");
                        // Draai rad
                        openCasinoWheel(player);
                        int amountOfCoins = inHand.getAmount();
                        inHand.setAmount(amountOfCoins - 1);
                        player.getInventory().setItemInMainHand(inHand);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Jij hebt op dit moment geen" + ChatColor.RED + " Wheelcoin " + ChatColor.YELLOW + "in bezit.");
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Jij hebt op dit moment geen" + ChatColor.RED + " Wheelcoin " + ChatColor.YELLOW + "in bezit.");
                }
            } else if (args.length == 1) {
                //Geeft de speler een coin
                if (args[0].equalsIgnoreCase("getcoin")) {
                    ItemStack ironIngot = new ItemStack(Material.IRON_INGOT);
                    ItemStack wheelCoin = ItemTagHandler.addString(ironIngot, "nbt.mtcustom", "wheelcoin");
                    ItemMeta meta = wheelCoin.getItemMeta();
                    meta.setDisplayName("WheelCoin");
                    wheelCoin.setItemMeta(meta);
                    player.getInventory().addItem(wheelCoin);
                    //Voeg een item toe aan de prijzen
                } else if (args[0].equalsIgnoreCase("additem")) {
                    ItemStack inHand = player.getInventory().getItemInMainHand();
                    int size =main.getRadItems().size();
                    if(size > 54){
                        player.sendMessage("Er is een maximaal aantal van 54 prijzen");
                    } else {
                        main.addItem(inHand);
                    }
                    //Verwijdert een item uit de prijzen
                } else if (args[0].equalsIgnoreCase("removeitem")) {
                    if (main.getRadItems().isEmpty()) {
                        player.sendMessage("Itemlijst is leeg");
                    } else {
                        ItemStack inHand = player.getInventory().getItemInMainHand();
                        main.removeItem(inHand);
                    }
                    //Geeft alle prijzen weer
                } else if (args[0].equalsIgnoreCase("list")) {
                    openCasinoPrices(player);
                } else {
                    player.sendMessage("/wheel <getcoin,additem,removeitem,list>");
                }
            } else {
                player.sendMessage("/wheel <getcoin,additem,removeitem,list>");
            }
        }
        return false;
    }

    //Opent lijst met alle prijzen(Alleen voor lead)
    private void openCasinoPrices(Player player) {
        int i = 0;
        Inventory gui = Bukkit.createInventory(null,54,ChatColor.DARK_RED + "Prizes");
        for (ItemStack is : main.getRadItems()) {
            gui.setItem(i,is);
            i++;
        }
        player.openInventory(gui);
    }

    //Opent het rad en laat het spinnen
    private void openCasinoWheel(Player player) {

            new BukkitRunnable() {
                int countdown = 5;
                Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_RED + "Spinning the wheel...");
                public void run() {
                    ItemStack glasspane = new ItemStack(Material.STAINED_GLASS);
                    //glass
                    gui.setItem(0, glasspane);
                    gui.setItem(1, glasspane);
                    gui.setItem(2, glasspane);
                    for (int i = 6; i <= 11; i++) {
                        gui.setItem(i, glasspane);
                    }
                    for (int i = 15; i <= 20; i++) {
                        gui.setItem(i, glasspane);
                    }
                    gui.setItem(24, glasspane);
                    gui.setItem(25, glasspane);
                    gui.setItem(26, glasspane);

                    //Items
                    int size = main.getRadItems().size() - 1;
                    for (int i = 3; i <= 5; i++) {
                        int random = (int) (Math.random() * ((size - 1) + 1)) + 1;
                        ItemStack randomitem = main.getRadItems().get(random);
                        gui.setItem(i, randomitem);
                    }
                    for (int i = 12; i <= 14; i++) {
                        int random = (int) (Math.random() * ((size - 1) + 1)) + 1;
                        ItemStack randomitem = main.getRadItems().get(random);
                        gui.setItem(i, randomitem);
                    }
                    for (int i = 21; i <= 23; i++) {
                        int random = (int) (Math.random() * ((size - 1) + 1)) + 1;
                        ItemStack randomitem = main.getRadItems().get(random);
                        gui.setItem(i, randomitem);
                    }
                    player.openInventory(gui);

                    countdown--;
                    if (countdown == 0) {
                        cancel();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ItemStack wonItem = gui.getItem(13);
                                player.getInventory().addItem(wonItem);
                                player.closeInventory();
                            }


                        }.runTaskLater(main,50L);
                    }
                }
            }.runTaskTimer(main,0,20L);

    }
}