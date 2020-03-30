package pl.lab.helpers;

import pl.lab.Utils;
import pl.lab.models.MethodItem;
import pl.lab.models.Property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TableHelper {

    private Utils utils;

    public TableHelper(Utils utils){
        this.utils = utils;
    }

    public MethodItem getMethodItem(Method method) {
        return new MethodItem(method.getName(), method);
    }

    public Property getProperty(Object classInstance, Method method) {
        try {
            return new Property(utils.getPropertyName(method), method.invoke(classInstance));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
