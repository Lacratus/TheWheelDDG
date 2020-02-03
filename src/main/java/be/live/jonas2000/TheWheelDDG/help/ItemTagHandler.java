package be.live.jonas2000.TheWheelDDG.help;

import org.bukkit.inventory.ItemStack;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemTagHandler {
    private static NBTTagCompound getTag(ItemStack item) {
        net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (itemNms.hasTag()) tag = itemNms.getTag();
        else tag = new NBTTagCompound();
        return tag;
    }
    private static org.bukkit.inventory.ItemStack setTag(ItemStack item, NBTTagCompound tag) {
        net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(item);
        itemNms.setTag(tag);
        return CraftItemStack.asBukkitCopy(itemNms);
    }
    public static org.bukkit.inventory.ItemStack addString(ItemStack item, String name, String value) {
        NBTTagCompound tag = ItemTagHandler.getTag(item);
        tag.setString(name, value);
        return ItemTagHandler.setTag(item, tag);
    }
    public static boolean hasString(ItemStack item, String name) {
        NBTTagCompound tag = ItemTagHandler.getTag(item);
        return tag.hasKey(name);
    }
    public static String getString(ItemStack item, String name) {
        NBTTagCompound tag = ItemTagHandler.getTag(item);
        return tag.getString(name);
    }
    public static org.bukkit.inventory.ItemStack removeString(ItemStack itemStack, String name) {
        NBTTagCompound tag = ItemTagHandler.getTag(itemStack);
        NBTTagCompound newTag = new NBTTagCompound();
        tag.c().stream().filter(name::equals).forEach(string -> {
            newTag.set(string, tag.get(string));
        });
        return setTag(itemStack, newTag);
    }
}