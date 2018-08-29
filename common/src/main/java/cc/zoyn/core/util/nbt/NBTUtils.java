package cc.zoyn.core.util.nbt;

import cc.zoyn.core.util.NMSUtils;
import cc.zoyn.core.util.reflect.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class NBTUtils {

    private static Class<?> NMS_ITEM_STACK;
    private static Class<?> NBT_TAG_COMPOUND;
    private static Class<?> NBT_TAG_LIST;

    /* The method of NMSItem */
    private static Method HAS_TAG;
    private static Method GET_TAG;
    private static Method SET_TAG;

    /* The method of NBTTagCompound */
    private static Method SET_DATA;
    private static Method SET_INT;
    private static Method SET_STRING;
    private static Method SET_SHORT;
    private static Method SET_BYTE;
    private static Method SET_LONG;
    private static Method SET_FLOAT;
    private static Method SET_DOUBLE;
    private static Method SET_INT_ARRAY;
    private static Method SET_BYTE_ARRAY;
    private static Method SET_BOOLEAN;

    /* The method of NBTTagList */
    private static Method ADD;
    private static Method GET;
    private static Method REMOVE;

    static {
        try {
            NMS_ITEM_STACK = NMSUtils.getNMSClass("ItemStack");
            NBT_TAG_COMPOUND = NMSUtils.getNMSClass("NBTTagCompound");
            NBT_TAG_LIST = NMSUtils.getNMSClass("NBTTagList");

            HAS_TAG = ReflectionUtils.getMethod(NMS_ITEM_STACK, "hasTag");
            GET_TAG = ReflectionUtils.getMethod(NMS_ITEM_STACK, "getTag");
            SET_TAG = ReflectionUtils.getMethod(NMS_ITEM_STACK, "setTag", NBT_TAG_COMPOUND);

            SET_INT = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setInt", String.class, Integer.TYPE);
            SET_STRING = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setString", String.class, String.class);
            SET_SHORT = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setShort", String.class, Short.TYPE);
            SET_BYTE = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setByte", String.class, Byte.TYPE);
            SET_LONG = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setInt", String.class, Long.TYPE);
            SET_FLOAT = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setFloat", String.class, Float.TYPE);
            SET_DOUBLE = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setDouble", String.class, Double.TYPE);
            SET_INT_ARRAY = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setIntArray", String.class, int[].class);
            SET_BYTE_ARRAY = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setByteArray", String.class, byte[].class);
            SET_BOOLEAN = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "setBoolean", String.class, Boolean.TYPE);
            SET_DATA = ReflectionUtils.getMethod(NBT_TAG_COMPOUND, "set", String.class, NMSUtils.getNMSClass("NBTBase"));

            ADD = ReflectionUtils.getMethod(NBT_TAG_LIST, "add", NMSUtils.getNMSClass("NBTBase"));
            GET = ReflectionUtils.getMethod(NBT_TAG_LIST, "get", Integer.TYPE);
            REMOVE = ReflectionUtils.getMethod(NBT_TAG_LIST, "remove", Integer.TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Prevent accidental construction
    private NBTUtils() {
    }

    /**
     * Get the item nbt
     *
     * @param itemStack the ItemStack
     * @return {@link TagCompound}
     */
    public static TagCompound getItemStackNBT(ItemStack itemStack) {
        return new TagCompound(getOriginalItemStackNBT(itemStack));
    }

    /**
     * Get the original item nbt
     *
     * @param itemStack the ItemStack
     * @return {@link Object}
     */
    public static Object getOriginalItemStackNBT(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            return null;
        }
        Object nmsItem = NMSUtils.getNMSItem(itemStack);
        Object nbtTag = null;
        try {
            // check the item has nbttag
            if (ReflectionUtils.invokeMethod(HAS_TAG, nmsItem).equals(true)) {
                nbtTag = ReflectionUtils.invokeMethod(GET_TAG, nmsItem);
            } else {
                nbtTag = newNBTTagCompound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbtTag;
    }


    /**
     * Return a empty NBTTagCompound
     *
     * @return {@link Object}
     */
    public static Object newNBTTagCompound() {
        try {
            return ReflectionUtils.instantiateObject(NBT_TAG_COMPOUND.getDeclaredConstructor());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return a empty NBTTagList
     *
     * @return {@link Object}
     */
    public static Object newNBTTagList() {
        try {
            return ReflectionUtils.instantiateObject(NBT_TAG_LIST.getDeclaredConstructor());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set the item nbt, and return the BukkitItem
     *
     * @param nbtTagCompound the nbt
     * @param itemStack      the item
     * @return {@link ItemStack}
     */
    public static ItemStack setItemStackNBT(Object nbtTagCompound, ItemStack itemStack) {
        Object nmsItem = NMSUtils.getNMSItem(itemStack);
        Object bukkitItem = null;
        try {
            ReflectionUtils.invokeMethod(SET_TAG, nmsItem, nbtTagCompound);
            bukkitItem = NMSUtils.getBukkitItem(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (ItemStack) bukkitItem;
    }

    public static void removeTagCompound(Object nbtTagList, int index) throws Exception {
        ReflectionUtils.invokeMethod(REMOVE, nbtTagList, index);
    }

    public static TagCompound getTagCompound(Object nbtTagList, int index) throws Exception {
        return new TagCompound(ReflectionUtils.invokeMethod(GET, nbtTagList, index));
    }
//
//    public static void setListTagCompound(Object nbtTagCompound, String key, List<TagCompound> value) throws Exception {
//        Object nbtTagList = newNBTTagList();
//        value.forEach(tagCompound -> {
//            try {
//                ReflectionUtils.invokeMethod(ADD, nbtTagList, tagCompound.build());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        System.out.println(nbtTagList);
//        setTagCompound(nbtTagCompound, key, nbtTagList);
//    }
//
//    public static void setTagCompound(Object nbtTagCompound, String key, TagCompound value) throws Exception {
//        System.out.println("value: " + value.build());
//        setTagCompound(nbtTagCompound, key, value.build());
//    }
//
//    public static void setTagCompound(Object nbtTagCompound, String key, Object value) throws Exception {
//        System.out.println("value: " + value);
//        ReflectionUtils.invokeMethod(SET_DATA, nbtTagCompound, key, value);
//    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setInt(Object nbtTagCompound, String key, int value) throws Exception {
        ReflectionUtils.invokeMethod(SET_INT, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setString(Object nbtTagCompound, String key, String value) throws Exception {
        ReflectionUtils.invokeMethod(SET_STRING, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setShort(Object nbtTagCompound, String key, short value) throws Exception {
        ReflectionUtils.invokeMethod(SET_SHORT, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setByte(Object nbtTagCompound, String key, byte value) throws Exception {
        ReflectionUtils.invokeMethod(SET_BYTE, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setLong(Object nbtTagCompound, String key, long value) throws Exception {
        ReflectionUtils.invokeMethod(SET_LONG, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setFloat(Object nbtTagCompound, String key, float value) throws Exception {
        ReflectionUtils.invokeMethod(SET_FLOAT, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setDouble(Object nbtTagCompound, String key, double value) throws Exception {
        ReflectionUtils.invokeMethod(SET_DOUBLE, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setIntArray(Object nbtTagCompound, String key, int[] value) throws Exception {
        ReflectionUtils.invokeMethod(SET_INT_ARRAY, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setByteArray(Object nbtTagCompound, String key, byte[] value) throws Exception {
        ReflectionUtils.invokeMethod(SET_BYTE_ARRAY, nbtTagCompound, key, value);
    }

    /**
     * 利用给定的数据来设置nbt
     * <p>
     * Use the given data to set nbt
     *
     * @param nbtTagCompound the nbt
     * @param key            the key of json object
     * @param value          the value of json object
     * @throws Exception When the Set method is null, it will return an excpetion
     */
    public static void setBoolean(Object nbtTagCompound, String key, boolean value) throws Exception {
        ReflectionUtils.invokeMethod(SET_BOOLEAN, nbtTagCompound, key, value);
    }

}