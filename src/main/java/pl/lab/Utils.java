package pl.lab;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {
    private List<Class> allowedTypes = Arrays.asList(String.class, Enum.class, Date.class);

    public boolean hasValidParameterType(Method method) {
        return Arrays.stream(method.getParameterTypes()).allMatch(cls -> cls.isPrimitive() || isAllowedType(cls));
    }

    public String getPropertyName(Method method) {
        return method.getName().replace("get", "").toLowerCase();
    }

    public boolean isGetter(Method method) {
        return method.getName().startsWith("get") &&
                method.getName().length() > 3 &&
                method.getParameterTypes().length == 0 &&
                !void.class.equals(method.getReturnType());
    }

    public boolean isSetter(Method method) {
        return method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterTypes().length == 1;
    }

    public boolean canInvoke(Method method) {
        return method.getParameterCount() == 0;
    }

    public boolean isNullOrWhitespace(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String getTimeToLog(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    public static String addLog(String currentText, String logContent){
        return currentText + "\n" + logContent;
    }

    private boolean isAllowedType(Class cls) {
        return allowedTypes.contains(cls);
    }
}
