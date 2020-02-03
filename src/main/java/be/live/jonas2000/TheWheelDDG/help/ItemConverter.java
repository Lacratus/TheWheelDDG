package be.live.jonas2000.TheWheelDDG.help;

import org.bukkit.inventory.ItemStack;

public class ItemConverter {
    public static ItemStack toItem(String jsonString) {
        return GsonFactory.getNewGson(true).fromJson(jsonString, ItemStack[].class)[0];
    }
    public static String fromItem(ItemStack itemStack) {
        ItemStack[] itemStacks = new ItemStack[1];
        itemStacks[0] = itemStack;
        return GsonFactory.getNewGson(true).toJson(itemStacks);
    }
}