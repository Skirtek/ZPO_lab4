package pl.lab.helpers;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class DialogsHelper {
    public Optional<String> showInputDialog(String header, String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(header);
        dialog.setHeaderText(null);
        dialog.setContentText(message);

        return dialog.showAndWait();
    }

    public Optional<Constructor<?>> getSelectedConstructor(Constructor<?>[] constructors){
        ChoiceDialog<Constructor<?>> dialog = new ChoiceDialog<>(constructors[0], constructors);
        dialog.setTitle("Wybierz konstruktor");
        dialog.setContentText("Konstruktorem użytym do inicjalizacji będzie:");
        return dialog.showAndWait();
    }

    public void showAlert(Alert.AlertType type, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
