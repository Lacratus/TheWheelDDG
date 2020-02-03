package be.live.jonas2000.TheWheelDDG.help;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GsonFactory {
    static final String SPLIT = ":";
    static final String PREFIX = "BASE64";
    private static Gson g = new Gson();
    private static final String CLASS_KEY = "SERIAL-ADAPTER-CLASS-KEY";
    private static Gson prettyGson;
    private static Gson compactGson;
    private static Gson newGson;

    public GsonFactory() {
    }

    public static Gson getPrettyGson() {
        if (prettyGson == null) {
            prettyGson = (new GsonBuilder()).addSerializationExclusionStrategy(new GsonFactory.ExposeExlusion()).addDeserializationExclusionStrategy(new GsonFactory.ExposeExlusion()).registerTypeHierarchyAdapter(ItemStack.class, new GsonFactory.ItemStackGsonAdapter()).registerTypeAdapter(PotionEffect.class, new GsonFactory.PotionEffectGsonAdapter()).registerTypeAdapter(Location.class, new GsonFactory.LocationGsonAdapter()).registerTypeAdapter(Date.class, new GsonFactory.DateGsonAdapter()).setPrettyPrinting().disableHtmlEscaping().create();
        }

        return prettyGson;
    }

    public static Gson getCompactGson() {
        if (compactGson == null) {
            compactGson = (new GsonBuilder()).addSerializationExclusionStrategy(new GsonFactory.ExposeExlusion()).addDeserializationExclusionStrategy(new GsonFactory.ExposeExlusion()).registerTypeHierarchyAdapter(ItemStack.class, new GsonFactory.ItemStackGsonAdapter()).registerTypeAdapter(PotionEffect.class, new GsonFactory.PotionEffectGsonAdapter()).registerTypeAdapter(Location.class, new GsonFactory.LocationGsonAdapter()).registerTypeAdapter(Date.class, new GsonFactory.DateGsonAdapter()).disableHtmlEscaping().create();
        }

        return compactGson;
    }

    public static Gson getNewGson(boolean prettyPrinting) {
        if (newGson == null) {
            GsonBuilder newGsonBuilder = (new GsonBuilder()).addSerializationExclusionStrategy(new GsonFactory.ExposeExlusion()).addDeserializationExclusionStrategy(new GsonFactory.ExposeExlusion()).registerTypeHierarchyAdapter(ItemStack.class, new GsonFactory.NewItemStackAdapter()).disableHtmlEscaping();
            if (prettyPrinting) {
                newGsonBuilder.setPrettyPrinting();
            }

            newGson = newGsonBuilder.create();
        }

        return newGson;
    }

    private static Map<String, Object> recursiveSerialization(ConfigurationSerializable o) {
        Map<String, Object> originalMap = o.serialize();
        Map<String, Object> map = new HashMap();
        Iterator var3 = originalMap.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<String, Object> entry = (Entry)var3.next();
            Object o2 = entry.getValue();
            if (o2 instanceof ConfigurationSerializable) {
                ConfigurationSerializable serializable = (ConfigurationSerializable)o2;
                Map<String, Object> newMap = recursiveSerialization(serializable);
                newMap.put("SERIAL-ADAPTER-CLASS-KEY", ConfigurationSerialization.getAlias(serializable.getClass()));
                map.put(entry.getKey(), newMap);
            }
        }

        map.put("SERIAL-ADAPTER-CLASS-KEY", ConfigurationSerialization.getAlias(o.getClass()));
        return map;
    }

    private static Map<String, Object> recursiveDoubleToInteger(Map<String, Object> originalMap) {
        Map<String, Object> map = new HashMap();
        Iterator var2 = originalMap.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, Object> entry = (Entry)var2.next();
            Object o = entry.getValue();
            if (o instanceof Double) {
                Double d = (Double)o;
                Integer i = d.intValue();
                map.put(entry.getKey(), i);
            } else if (o instanceof Map) {
                Map<String, Object> subMap = (Map)o;
                map.put(entry.getKey(), recursiveDoubleToInteger(subMap));
            } else {
                map.put(entry.getKey(), o);
            }
        }

        return map;
    }

    private static String nbtToString(NBTBase base) {
        return base.toString().replace(",}", "}").replace(",]", "]");
    }

    private static net.minecraft.server.v1_12_R1.ItemStack removeSlot(ItemStack item) {
        if (item == null) {
            return null;
        } else {
            net.minecraft.server.v1_12_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
            if (nmsi == null) {
                return null;
            } else {
                NBTTagCompound nbtt = nmsi.getTag();
                if (nbtt != null) {
                    nbtt.remove("Slot");
                    nmsi.setTag(nbtt);
                }

                return nmsi;
            }
        }
    }

    private static ItemStack removeSlotNBT(ItemStack item) {
        if (item == null) {
            return null;
        } else {
            net.minecraft.server.v1_12_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
            if (nmsi == null) {
                return null;
            } else {
                NBTTagCompound nbtt = nmsi.getTag();
                if (nbtt != null) {
                    nbtt.remove("Slot");
                    nmsi.setTag(nbtt);
                }

                return CraftItemStack.asBukkitCopy(nmsi);
            }
        }
    }

    private static String nbtToSTBase64(NBTTagCompound c) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Throwable var2 = null;

            String var34;
            try {
                DataOutputStream stream = new DataOutputStream(out);
                Throwable var4 = null;

                try {
                    NBTCompressedStreamTools.a(c, (DataOutput) stream);
                } catch (Throwable var29) {
                    var4 = var29;
                    throw var29;
                } finally {
                    if (stream != null) {
                        if (var4 != null) {
                            try {
                                stream.close();
                            } catch (Throwable var28) {
                                var4.addSuppressed(var28);
                            }
                        } else {
                            stream.close();
                        }
                    }

                }

                var34 = Base64.getEncoder().encodeToString(out.toByteArray());
            } catch (Throwable var31) {
                var2 = var31;
                throw var31;
            } finally {
                if (out != null) {
                    if (var2 != null) {
                        try {
                            out.close();
                        } catch (Throwable var27) {
                            var2.addSuppressed(var27);
                        }
                    } else {
                        out.close();
                    }
                }

            }

            return var34;
        } catch (Exception var33) {
            return "";
        }
    }

    private static NBTTagCompound stBase64ToNBT(String str) throws IOException {
        byte[] array = Base64.getDecoder().decode(str);
        ByteArrayInputStream in = new ByteArrayInputStream(array);
        Throwable var3 = null;

        Object var6;
        try {
            DataInputStream stream = new DataInputStream(in);
            Throwable var5 = null;

            try {
                var6 = NBTCompressedStreamTools.a(stream);
            } catch (Throwable var29) {
                var6 = var29;
                var5 = var29;
                throw var29;
            } finally {
                if (stream != null) {
                    if (var5 != null) {
                        try {
                            stream.close();
                        } catch (Throwable var28) {
                            var5.addSuppressed(var28);
                        }
                    } else {
                        stream.close();
                    }
                }

            }
        } catch (Throwable var31) {
            var3 = var31;
            throw var31;
        } finally {
            if (in != null) {
                if (var3 != null) {
                    try {
                        in.close();
                    } catch (Throwable var27) {
                        var3.addSuppressed(var27);
                    }
                } else {
                    in.close();
                }
            }

        }

        return (NBTTagCompound)var6;
    }

    public static String writeValue(NBTTagCompound compound) {
        return compound == null ? "" : "BASE64" + ":" + nbtToSTBase64(compound);
    }

    public static NBTTagCompound readValue(String raw) {
        if (raw.equalsIgnoreCase("")) {
            return null;
        } else {
            try {
                String[] str = raw.split(":");
                if (str[0].equalsIgnoreCase("BASE64")) {
                    return stBase64ToNBT(str[1]);
                } else {
                    throw new RuntimeException("skip to exception");
                }
            } catch (Exception var4) {
                try {
                    return MojangsonParser.parse(raw);
                } catch (Exception var3) {
                    System.err.println("---- Unable to read NBT Data! -----");
                    var3.printStackTrace();
                    System.err.println("---- Unable to read NBT Data! -----");
                    return null;
                }
            }
        }
    }

    private static class DateGsonAdapter extends TypeAdapter<Date> {
        private DateGsonAdapter() {
        }

        public void write(JsonWriter jsonWriter, Date date) throws IOException {
            if (date == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(date.getTime());
            }
        }

        public Date read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return new Date(jsonReader.nextLong());
            }
        }
    }

    private static class LocationGsonAdapter extends TypeAdapter<Location> {
        private static Type seriType = (new TypeToken<Map<String, Object>>() {
        }).getType();
        private static String UUID = "uuid";
        private static String X = "x";
        private static String Y = "y";
        private static String Z = "z";
        private static String YAW = "yaw";
        private static String PITCH = "pitch";

        private LocationGsonAdapter() {
        }

        public void write(JsonWriter jsonWriter, Location location) throws IOException {
            if (location == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(this.getRaw(location));
            }
        }

        public Location read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return this.fromRaw(jsonReader.nextString());
            }
        }

        private String getRaw(Location location) {
            Map<String, Object> serial = new HashMap();
            serial.put(UUID, location.getWorld().getUID().toString());
            serial.put(X, Double.toString(location.getX()));
            serial.put(Y, Double.toString(location.getY()));
            serial.put(Z, Double.toString(location.getZ()));
            serial.put(YAW, Float.toString(location.getYaw()));
            serial.put(PITCH, Float.toString(location.getPitch()));
            return GsonFactory.g.toJson(serial);
        }

        private Location fromRaw(String raw) {
            Map<String, Object> keys = (Map)GsonFactory.g.fromJson(raw, seriType);
            World w = Bukkit.getWorld((String)keys.get(UUID));
            return new Location(w, Double.parseDouble((String)keys.get(X)), Double.parseDouble((String)keys.get(Y)), Double.parseDouble((String)keys.get(Z)), Float.parseFloat((String)keys.get(YAW)), Float.parseFloat((String)keys.get(PITCH)));
        }
    }

    private static class PotionEffectGsonAdapter extends TypeAdapter<PotionEffect> {
        private static Type seriType = (new TypeToken<Map<String, Object>>() {
        }).getType();
        private static String TYPE = "effect";
        private static String DURATION = "duration";
        private static String AMPLIFIER = "amplifier";
        private static String AMBIENT = "ambient";

        private PotionEffectGsonAdapter() {
        }

        public void write(JsonWriter jsonWriter, PotionEffect potionEffect) throws IOException {
            if (potionEffect == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(this.getRaw(potionEffect));
            }
        }

        public PotionEffect read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return this.fromRaw(jsonReader.nextString());
            }
        }

        private String getRaw(PotionEffect potion) {
            Map<String, Object> serial = potion.serialize();
            return GsonFactory.g.toJson(serial);
        }

        private PotionEffect fromRaw(String raw) {
            Map<String, Object> keys = (Map)GsonFactory.g.fromJson(raw, seriType);
            return new PotionEffect(PotionEffectType.getById(((Double)keys.get(TYPE)).intValue()), ((Double)keys.get(DURATION)).intValue(), ((Double)keys.get(AMPLIFIER)).intValue(), (Boolean)keys.get(AMBIENT));
        }
    }

    private static class ItemStackGsonAdapter extends TypeAdapter<ItemStack> {
        private static Type seriType = (new TypeToken<Map<String, Object>>() {
        }).getType();

        private ItemStackGsonAdapter() {
        }

        public void write(JsonWriter jsonWriter, ItemStack itemStack) throws IOException {
            if (itemStack == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(this.getRaw(GsonFactory.removeSlotNBT(itemStack)));
            }
        }

        public ItemStack read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return this.fromRaw(jsonReader.nextString());
            }
        }

        private String getRaw(ItemStack item) {
            Map<String, Object> serial = item.serialize();
            if (serial.get("meta") != null) {
                ItemMeta itemMeta = item.getItemMeta();
                Map<String, Object> originalMeta = itemMeta.serialize();
                Map<String, Object> meta = new HashMap();
                Iterator var6 = originalMeta.entrySet().iterator();

                while(var6.hasNext()) {
                    Entry<String, Object> entry = (Entry)var6.next();
                    meta.put(entry.getKey(), entry.getValue());
                }

                Iterator var12 = meta.entrySet().iterator();

                while(var12.hasNext()) {
                    Entry<String, Object> entry = (Entry)var12.next();
                    Object o = entry.getValue();
                    if (o instanceof ConfigurationSerializable) {
                        ConfigurationSerializable serializable = (ConfigurationSerializable)o;
                        Map<String, Object> serialized = GsonFactory.recursiveSerialization(serializable);
                        meta.put(entry.getKey(), serialized);
                    }
                }

                serial.put("meta", meta);
            }

            return GsonFactory.g.toJson(serial);
        }

        private ItemStack fromRaw(String raw) {
            Map<String, Object> keys = (Map)GsonFactory.g.fromJson(raw, seriType);
            if (keys.get("amount") != null) {
                Double d = (Double)keys.get("amount");
                Integer i = d.intValue();
                keys.put("amount", i);
            }

            ItemStack item;
            try {
                item = ItemStack.deserialize(keys);
            } catch (Exception var6) {
                return null;
            }

            if (item == null) {
                return null;
            } else {
                if (keys.containsKey("meta")) {
                    Map<String, Object> itemmeta = (Map)keys.get("meta");
                    itemmeta = GsonFactory.recursiveDoubleToInteger(itemmeta);
                    ItemMeta meta = (ItemMeta)ConfigurationSerialization.deserializeObject(itemmeta, ConfigurationSerialization.getClassByAlias("ItemMeta"));
                    item.setItemMeta(meta);
                }

                return item;
            }
        }
    }

    public static class NewItemStackAdapter extends TypeAdapter<ItemStack> {
        public NewItemStackAdapter() {
        }

        public void write(JsonWriter jsonWriter, ItemStack itemStack) throws IOException {
            if (itemStack == null) {
                jsonWriter.nullValue();
            } else {
                net.minecraft.server.v1_12_R1.ItemStack item = GsonFactory.removeSlot(itemStack);
                if (item == null) {
                    jsonWriter.nullValue();
                } else {
                    try {
                        jsonWriter.beginObject();
                        jsonWriter.name("type");
                        jsonWriter.value(itemStack.getType().toString());
                        jsonWriter.name("amount");
                        jsonWriter.value((long)itemStack.getAmount());
                        jsonWriter.name("data");
                        jsonWriter.value((long)itemStack.getDurability());
                        jsonWriter.name("tag");
                        if (item != null && item.getTag() != null) {
                            jsonWriter.value(GsonFactory.writeValue(item.getTag()));
                        } else {
                            jsonWriter.value("");
                        }

                        jsonWriter.endObject();
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }

                }
            }
        }

        public ItemStack read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                jsonReader.beginObject();
                Material type = null;
                int amount = -1;
                int data = -1;
                String tag = "";

                for(int i = 0; i < 4; ++i) {
                    jsonReader.nextName();
                    String path = jsonReader.getPath();
                    if (path.endsWith("type")) {
                        type = Material.getMaterial(jsonReader.nextString());
                    } else if (path.endsWith("amount")) {
                        amount = jsonReader.nextInt();
                    } else if (path.endsWith("data")) {
                        data = jsonReader.nextInt();
                    } else if (path.endsWith("tag")) {
                        tag = jsonReader.nextString();
                    }
                }

                net.minecraft.server.v1_12_R1.ItemStack item = null;
                if (type != null) {
                    item = new net.minecraft.server.v1_12_R1.ItemStack(CraftMagicNumbers.getItem(type), amount, data);
                    item.setTag(GsonFactory.readValue(tag));
                }

                jsonReader.endObject();
                return CraftItemStack.asBukkitCopy(item);
            }
        }
    }

    private static class ExposeExlusion implements ExclusionStrategy {
        private ExposeExlusion() {
        }

        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            GsonFactory.Ignore ignore = (GsonFactory.Ignore)fieldAttributes.getAnnotation(GsonFactory.Ignore.class);
            if (ignore != null) {
                return true;
            } else {
                Expose expose = (Expose)fieldAttributes.getAnnotation(Expose.class);
                return expose != null && (!expose.serialize() || !expose.deserialize());
            }
        }

        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Ignore {
    }
}
