module fr.utbm.ap4b.trio {
    requires javafx.controls;
    requires javafx.fxml;

    opens fr.utbm.ap4b to javafx.fxml;
    opens fr.utbm.ap4b.vue to javafx.fxml;

    exports fr.utbm.ap4b;
    exports fr.utbm.ap4b.vue;
}