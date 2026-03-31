module ufzdev.todo_list {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens ufzdev.todo_list to javafx.fxml;
    exports ufzdev.todo_list;
}