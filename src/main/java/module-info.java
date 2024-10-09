module rwt.diagrams {
   requires java.desktop;
   requires javafx.swing;
   requires javafx.fxml;
   requires javafx.controls;
   requires transitive javafx.graphics;
   exports rwt.diagrams to javafx.graphics;
   opens rwt.diagrams to javafx.fxml;
   opens rwt.diagrams.sequence to javafx.fxml;
}
