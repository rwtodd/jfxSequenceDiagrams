package rwt.diagrams.sequence;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;


public class Controller implements Initializable {

  @FXML private TextArea sourceArea;
  @FXML private Pane drawingArea;


  public void initialize(java.net.URL url, java.util.ResourceBundle bundle) {
     sourceArea.textProperty().addListener( (obs,old,cur) -> {
        /* need to set a timer here, and parse "cur" if the timer goes off */
        Diagram d = Parser.parse(cur);
        setError(d.hasErrors());
        Renderer r = new Renderer(d, drawingArea);
        r.render();
        r.makeVisible(); 
     }); 
  }

  public void setError(boolean isError) {
    sourceArea.getStyleClass().removeAll("sourceError","sourceNormal");
    sourceArea.getStyleClass().add(isError ? "sourceError" : "sourceNormal" );
  }
}
