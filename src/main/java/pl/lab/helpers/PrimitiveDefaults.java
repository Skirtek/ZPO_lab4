package pl.lab.helpers;

public class PrimitiveDefaults {
    public static Object getDefaultValue(Class clazz) {
        if (clazz.equals(boolean.class)) {
            return Boolean.FALSE;
        } else if (clazz.equals(byte.class)) {
            return Byte.valueOf("0");
        } else if (clazz.equals(short.class)) {
            return Short.valueOf("0");
        } else if (clazz.equals(int.class)) {
            return Integer.valueOf("0");
        } else if (clazz.equals(long.class)) {
            return Long.valueOf("0");
        } else if (clazz.equals(float.class)) {
            return Float.valueOf("0");
        } else if (clazz.equals(double.class)) {
            return Double.valueOf("0");
        } else {
            return null;
        }
    }
}
