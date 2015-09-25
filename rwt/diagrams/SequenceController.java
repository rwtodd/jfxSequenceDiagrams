package rwt.diagrams;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;


public class SequenceController implements Initializable {

  @FXML private TextArea sourceArea;
  @FXML private Pane drawingArea;


  public void initialize(java.net.URL l, java.util.ResourceBundle r) {
     sourceArea.textProperty().addListener( (obs,old,cur) -> {
        /* need to set a timer here, and parse "cur" if the timer goes off */
     }); 
  }

  public void setError(boolean isError) {
    sourceArea.getStyleClass().removeAll("sourceError","sourceNormal");
    sourceArea.getStyleClass().add(isError ? "sourceError" : "sourceNormal" );
  }
}
