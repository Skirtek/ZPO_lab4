package pl.lab.helpers;

import pl.lab.models.OperationResult;

import java.lang.reflect.Method;

public class OperationHelper {

    public OperationResult tryAssign(String source, Class<?> cls) {
        try {
            Method method = cls.getDeclaredMethod("valueOf", String.class);
            return method != null
                    ? new OperationResult(true, method.invoke(null, source))
                    : new OperationResult(false, "Wystąpił błąd przypisywania wartości");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new OperationResult(false, "Wystąpił błąd podczas przypisywania wartości");
        }
    }
}
