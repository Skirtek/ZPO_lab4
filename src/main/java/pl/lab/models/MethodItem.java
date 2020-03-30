package pl.lab.models;

import java.lang.reflect.Method;

public class MethodItem {
    private String name;
    private Method source;

    public MethodItem(String name, Method source) {
        this.name = name;
        this.source = source;
    }

    public Method getSource() {
        return source;
    }

    public void setSource(Method source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
