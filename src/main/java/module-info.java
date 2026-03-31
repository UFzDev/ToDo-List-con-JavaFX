module ufzdev.todo_list {
    requires javafx.controls;
    requires javafx.fxml;

    opens ufzdev.todo_list to javafx.fxml;
    exports ufzdev.todo_list;
}