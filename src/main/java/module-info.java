module fr.utbm.ap4b.trio {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens fr.utbm.ap4b to javafx.fxml;
    opens fr.utbm.ap4b.view to javafx.fxml;

    exports fr.utbm.ap4b;
    exports fr.utbm.ap4b.view;
}