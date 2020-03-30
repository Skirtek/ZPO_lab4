package pl.lab;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.lab.helpers.*;
import pl.lab.models.MethodItem;
import pl.lab.models.OperationResult;
import pl.lab.models.Property;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class ViewController {

    public TextField classname;

    public TableView<Property> properties_table;
    public TableColumn<Property, String> property_name;
    public TableColumn<Property, Object> property_value;
    private ObservableList<Property> properties;

    public TableView<MethodItem> setters_table;
    public TableColumn<MethodItem, String> setter_name;
    private ObservableList<MethodItem> setters;

    public TableView<MethodItem> methods_table;
    public TableColumn<MethodItem, String> method_name;
    private ObservableList<MethodItem> other_methods;

    public TextArea console_area;

    public Button find_button;

    private Utils utils;
    private ExceptionsHelper exceptionsHelper;
    private TableHelper tableHelper;
    private DialogsHelper dialogsHelper;
    private OperationHelper operationHelper;

    private Object classInstance;

    private Method[] methods;

    @FXML
    public void initialize() {
        utils = new Utils();
        exceptionsHelper = new ExceptionsHelper();
        tableHelper = new TableHelper(utils);
        dialogsHelper = new DialogsHelper();
        operationHelper = new OperationHelper();

        initializeTables();
    }

    private void initializeTables() {
        properties = FXCollections.observableArrayList();
        setters = FXCollections.observableArrayList();
        other_methods = FXCollections.observableArrayList();

        properties_table.setItems(properties);
        setters_table.setItems(setters);
        methods_table.setItems(other_methods);

        property_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        property_value.setCellValueFactory(new PropertyValueFactory<>("value"));

        setters_table.setRowFactory(tableView -> {
            final TableRow<MethodItem> row = new TableRow<>();

            createContextMenu(row, "Ustaw wartość", event -> onSetValue(row.getItem()));

            return row;
        });

        methods_table.setRowFactory(tableView -> {
            final TableRow<MethodItem> row = new TableRow<>();

            createContextMenu(row, "Wywołaj", event -> onCallAction(row.getItem()));

            return row;
        });

        setter_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        method_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Rozpoczęto pracę programu"));
    }

    public void findClass() {
        try {
            String classToFind = classname.getText();

            if (utils.isNullOrWhitespace(classToFind)) {
                dialogsHelper.showAlert(Alert.AlertType.WARNING, "Błędna nazwa klasy", "Nazwa klasy nie może być pusta");
                return;
            }

            Class<?> cls = Class.forName(classToFind);
            Optional<Constructor<?>> ctor = dialogsHelper.getSelectedConstructor(cls.getConstructors());

            if (!ctor.isPresent()) {
                return;
            }

            Object[] initValues = Arrays.stream(ctor.get().getParameterTypes()).map(PrimitiveDefaults::getDefaultValue).toArray();
            classInstance = ctor.get().newInstance(initValues);

            if (!cls.isInstance(classInstance)) {
                console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Instancja klasy " + cls.getName() + " nie została utworzona"));
                return;
            }

            console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Instancja klasy " + cls.getName() + " została utworzona"));

            methods = cls.getDeclaredMethods();

            fillPropertiesCollection(methods);
            fillSettersCollection(methods);
            fillOtherMethodsCollection(methods);
        } catch (Exception ex) {
            exceptionsHelper.handleException(console_area, ex);
        }
    }

    private void createContextMenu(TableRow<MethodItem> row, String itemName, EventHandler<ActionEvent> e) {
        final ContextMenu contextMenu = new ContextMenu();

        final MenuItem setValueMenuItem = createContextMenuItem(itemName, e);

        contextMenu.getItems().addAll(setValueMenuItem);

        row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(contextMenu));
    }

    private MenuItem createContextMenuItem(String text, EventHandler<ActionEvent> value) {
        final MenuItem item = new MenuItem(text);
        item.setOnAction(value);

        return item;
    }

    private void onCallAction(MethodItem methodItem) {
        try {
            Method method = methodItem.getSource();

            if (utils.canInvoke(method)) {
                invokeMethod(method, methodItem.getName());
                return;
            }

            console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Nie można wywołać metody " + methodItem.getName() + ". Powód: posiada parametry"));

        } catch (Exception ex) {
            ex.printStackTrace();
            console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Podczas wywołania metody wystąpił krytyczny błąd"));
        }
    }

    private void invokeMethod(Method method, String methodName) throws InvocationTargetException, IllegalAccessException {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        method.invoke(classInstance);
        console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Pomyślnie wywołano metodę " + methodName));
    }

    private void onSetValue(MethodItem methodItem) {
        try {
            Optional<String> value = dialogsHelper.showInputDialog("Wpisz wartość", "Nowa wartość pola: ");

            if (!value.isPresent() || utils.isNullOrWhitespace(value.get())) {
                methodItem.getSource().invoke(classInstance, (Object) null);
                fillPropertiesCollection(methods);
                console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Pomyślnie wyczyszczono wartosć pola"));
                return;
            }

            Class<?> parameterType = ClassHelper.wrap(methodItem.getSource().getParameterTypes()[0]);

            if (parameterType == String.class) {
                methodItem.getSource().invoke(classInstance, value.get());

                fillPropertiesCollection(methods);
                console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Pomyślnie ustawiono wartosć"));
                return;
            }

            OperationResult result = operationHelper.tryAssign(value.get(), parameterType);

            if (!result.isSuccess()) {
                console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " " + result.getMessage()));
                return;
            }

            methodItem.getSource().invoke(classInstance, result.getData());

            fillPropertiesCollection(methods);
            console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Pomyślnie ustawiono wartosć"));
        } catch (Exception ex) {
            ex.printStackTrace();
            console_area.setText(Utils.addLog(console_area.getText(), Utils.getTimeToLog() + " Podczas ustawiania wartości wystąpił krytyczny błąd"));
        }
    }

    private void fillPropertiesCollection(Method[] methods) {
        properties.clear();
        properties.addAll(Arrays.stream(methods).filter(utils::isGetter).map(x -> tableHelper.getProperty(classInstance, x)).collect(Collectors.toList()));
    }

    private void fillSettersCollection(Method[] methods) {
        setters.clear();
        setters.addAll(Arrays.stream(methods).filter(x -> utils.isSetter(x) && utils.hasValidParameterType(x)).map(x -> tableHelper.getMethodItem(x)).collect(Collectors.toList()));
    }

    private void fillOtherMethodsCollection(Method[] methods) {
        other_methods.clear();
        other_methods.addAll(Arrays.stream(methods).filter(x -> !utils.isGetter(x) && !utils.isSetter(x)).map(x -> tableHelper.getMethodItem(x)).collect(Collectors.toList()));
    }
}
