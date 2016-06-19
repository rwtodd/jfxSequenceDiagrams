package rwt.diagrams;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.layout.Pane;

public class Sequence extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
       primaryStage.setTitle("Easy Sequence Diagrams");
       Pane myPane = (Pane)FXMLLoader.load(getClass()
                                     .getResource("sequence/sequence_diagrams.fxml"));
       Scene myScene = new Scene(myPane);
       primaryStage.setScene(myScene);
       primaryStage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}

