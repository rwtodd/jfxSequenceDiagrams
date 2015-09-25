package rwt.diagrams.sequence;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

  @FXML private TextArea sourceArea;
  @FXML private Pane drawingArea;
  private Timer renderTimer;
  private TimerTask currentTask; 

  public void initialize(java.net.URL url, java.util.ResourceBundle bundle) {
     sourceArea.textProperty().addListener( (obs,old,cur) -> {
         
        if(currentTask != null) currentTask.cancel();
        currentTask = new TimerTask() { 
          @Override
          public void run() {
            Diagram d = Parser.parse(cur);
            Renderer r = new Renderer(d, drawingArea);
            r.render();
            r.makeVisible(); 
            Platform.runLater( () -> setError(d.hasErrors()) );
          }
         };
        renderTimer.purge();
        renderTimer.schedule(currentTask,1500);
     }); 
  }

  public void setError(boolean isError) {
    sourceArea.getStyleClass().removeAll("sourceError","sourceNormal");
    sourceArea.getStyleClass().add(isError ? "sourceError" : "sourceNormal" );
  }

  public Controller() {
     renderTimer = new Timer(true);
     currentTask = null;
  }
}
