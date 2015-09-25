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
  private final Timer renderTimer;
  private TimerTask currentTask; 

  /** A TimerTask for re-rendering the diagram.
    * This task makes sure we only re-render the
    * diagram once the user has stopped modifying
    * the text for 1.5 seconds.
    */ 
  private class RenderLater extends TimerTask {
     private final String text;

     RenderLater(String text) { this.text = text; }

     @Override
      public void run() {
        final Diagram d = Parser.parse(text);
        final Renderer r = new Renderer(d, drawingArea);
        r.render();
        r.makeVisible(); 
        Platform.runLater( () -> setError(d.hasErrors()) );
      }
  };

  public void initialize(java.net.URL url, java.util.ResourceBundle bundle) {
     sourceArea.textProperty().addListener( (obs,old,cur) -> {
        if(currentTask != null) currentTask.cancel();
        renderTimer.purge();

        currentTask = new RenderLater(cur);
        renderTimer.schedule(currentTask,1500);
     }); 

     sourceArea.setText("Title: Example\na to b: hello\n");
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
