package rwt.diagrams.sequence;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

/** Controls the main Sequence Diagram scene.
  * @author Richard Todd 
  */
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

  /** Augments the FXML in setting up the scene.
    */
  public void initialize(java.net.URL url, java.util.ResourceBundle bundle) {
     sourceArea.textProperty().addListener( (obs,old,cur) -> {
        if(currentTask != null) currentTask.cancel();
        renderTimer.purge();

        currentTask = new RenderLater(cur);
        renderTimer.schedule(currentTask,1500);
     }); 

     String example = "";
     try( InputStream is = (InputStream)this.getClass().getResource("example_diagram.txt").getContent();
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader br = new BufferedReader(isr) ) {
        StringBuilder sb = new StringBuilder();
        for(String line = br.readLine(); line != null; line = br.readLine()) {
            sb.append(line).append('\n');
        } 
        example = sb.toString();
     } catch(java.io.IOException e) {
        example = "Error loading example diagram.";
     }
     sourceArea.setText(example);

  }

  /** Applies styles to the souce pane when there is an error. */
  public void setError(boolean isError) {
    sourceArea.getStyleClass().removeAll("sourceError","sourceNormal");
    sourceArea.getStyleClass().add(isError ? "sourceError" : "sourceNormal" );
  }

  /** Save the current diagram as a PNG file. 
    * @param evt not used.
    */
  @FXML private void savePNG(javafx.event.ActionEvent evt) {
    try {
       final FileChooser fch = new FileChooser();
       fch.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File","*.png"));
       final java.io.File saveAs = fch.showSaveDialog(null);
       if(saveAs != null) {
          WritableImage snapshot = drawingArea.snapshot(new SnapshotParameters(),null);
          java.awt.image.BufferedImage bi = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, null);
          javax.imageio.ImageIO.write(bi,"PNG",saveAs);
       }
    } catch(java.io.IOException e) {
        System.err.println(e);
    }
  }

  public Controller() {
     renderTimer = new Timer(true);
     currentTask = null;
  }
}
