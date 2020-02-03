package be.live.jonas2000.TheWheelDDG.main;
import java.sql.*;
import java.util.*;


import be.live.jonas2000.TheWheelDDG.commands.WheelCommand;
import be.live.jonas2000.TheWheelDDG.help.ItemConverter;
import be.live.jonas2000.TheWheelDDG.listeners.WheelListener;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;
    private Main plugin;
    private ArrayList<ItemStack> radItems = new ArrayList<>();
    private ArrayList<ItemStack> delList = new ArrayList<>();

    public Main() {
    }

    @Override
    public void onEnable() {
        plugin = this;
        // Listeners and Commands

        getCommand("Wheel").setExecutor(new WheelCommand(this));
        Bukkit.getPluginManager().registerEvents(new WheelListener(),this);


        //Database connection
        this.host = "localhost";
        this.port = 3306;
        this.database = "thewheelddg";
        this.username = "root";
        this.password = "";

        try {
            this.openConnection();
            System.out.println("Works");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Get all ItemStacks
        try {
            ResultSet rs = prepareStatement("SELECT * FROM raditem;").executeQuery();
            while (rs.next()){
                ItemStack item = ItemConverter.toItem(rs.getString("itemstack"));
                radItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    public PreparedStatement prepareStatement(String query) {
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ps;
    }

    public Main getPlugin() {
        return plugin;
    }

    public void addItem(ItemStack item){
        radItems.add(item);
    }
    public void removeItem(ItemStack item){
        radItems.remove(item);
        delList.add(item);
    }

    public ArrayList<ItemStack> getRadItems(){
        return radItems;
    }






    @Override
    public void onDisable() {
        for(ItemStack ri : radItems){
           String string = ItemConverter.fromItem(ri);
            try {
                ResultSet rs = prepareStatement("SELECT COUNT(itemstack) FROM raditem WHERE itemstack = '" + string +"';").executeQuery();
                rs.next();
                if(rs.getInt(1) == 0) {
                    prepareStatement("INSERT INTO raditem(itemstack) VALUES ('" + string + "');").executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for(ItemStack ri : delList){
            String string = ItemConverter.fromItem(ri);
            try {
                prepareStatement("DELETE FROM raditem WHERE itemstack = '"+ string + "';").executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

