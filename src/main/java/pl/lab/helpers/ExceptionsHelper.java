package pl.lab.helpers;

import javafx.scene.control.TextArea;
import pl.lab.Utils;

public class ExceptionsHelper {
    public void handleException(TextArea console, Exception ex) {
        if (ex instanceof ClassNotFoundException) {
            console.setText(Utils.addLog(console.getText(), Utils.getTimeToLog() + " Klasa nie została odnaleziona"));
            return;
        }

        ex.printStackTrace();
    }
}
