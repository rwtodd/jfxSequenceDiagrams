package rwt.diagrams.sequence;

import javafx.application.Platform;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/** Draws a rwt.diagrams.sequence.Diagram
  * onto a JavaFX 'parent' node.
  */
public class Renderer {
  // some constants for rendering *************************
  private static final double MARGIN = 10.0;
  private static final double TEXT_SIZE = 14.0;
  private static final Font TEXT_FONT = Font.font("Helvetica",TEXT_SIZE);
  private static final double MIN_BOX_SEP = TEXT_SIZE * 10.0;


  // some variables used during rendering *****************
  private double topOfDiagram;    // y-value of top of diagram
  private double topOfLines;      // y-value of the start of the lane lines
  private double computedBoxSep;  // how much to separate the actors
  private double boxHeight;  // y-size of actor boxes
  private double boxWidth;   // x-size of actor boxes


  // other private state *************************
  private final Diagram diagram;
  private final Pane node;
  private final List<Shape> shapes;
  private final Map<String,Double> actorPlacement; // centerline placement

  public Renderer(Diagram d, Pane p) {
    diagram = d;
    node = p;
    shapes = new ArrayList<>();
    actorPlacement = new HashMap<>();
  }

  public void render() {
     Text title = titleWords();
     List<Shape> actorBoxes = null;

     topOfDiagram = MARGIN + title.getLayoutBounds().getHeight() + MARGIN; 

     if(diagram.getActors().size() > 0) {
        actorBoxes = drawActorBoxes();
        drawEvents();

        shapes.addAll(actorBoxes);
     }

     centerTitle(title);
     shapes.add(title);
  }

  public void makeVisible() {
    Platform.runLater( () -> {
       node.getChildren().clear(); 
       node.getChildren().addAll(shapes);
    });
  }

  private List<Shape> drawActorBoxes() {
     ArrayList<Text> texts = new ArrayList<>();
     ArrayList<Shape> answer = new ArrayList<>();

     for(Diagram.Actor a : diagram.getActors()) {
         Text txt = new Text();
         txt.setFont(TEXT_FONT);
         txt.setWrappingWidth(TEXT_SIZE * 10.0);
         txt.setTextAlignment(TextAlignment.CENTER);
         txt.setText(a.displayName);
         txt.setTextOrigin(javafx.geometry.VPos.TOP);
         texts.add(txt);
     }
   
     double xSoFar = MARGIN;

     // note it would be more efficient to reduce() over a pair of (width,height) tuples
     // but the two searches below are easier to read and write, and the number of 
     // actors is small enough that it doesn't matter anyway
     double maxHeight = texts.stream()
                             .mapToDouble( t -> t.getLayoutBounds().getHeight() )
                             .max()
                             .getAsDouble();
     double maxWidth = texts.stream()
                             .mapToDouble( t -> t.getLayoutBounds().getWidth() )
                             .max()
                             .getAsDouble();

     boxHeight = maxHeight + 2.0 * MARGIN; 
     boxWidth  = maxWidth  + 2.0 * MARGIN; 
     topOfLines = topOfDiagram + boxHeight;
     computedBoxSep = Math.max( MIN_BOX_SEP, boxWidth * 1.5 );

     for(Text txt : texts) {

        Rectangle r = new Rectangle(xSoFar,topOfDiagram,boxWidth,boxHeight);
        r.getStyleClass().add("actor-box");
        answer.add(r);

        txt.setX(xSoFar + (boxWidth - txt.getLayoutBounds().getWidth())/2.0 );
 	txt.setY(topOfDiagram + (boxHeight - txt.getLayoutBounds().getHeight())/2.0 );
        answer.add(txt);

        xSoFar += computedBoxSep;
     }

     xSoFar = MARGIN + boxWidth/2.0;

     // finally... set up the line placement dictionary
     for(Diagram.Actor a : diagram.getActors()) {
        actorPlacement.put(a.name, xSoFar);
        xSoFar += computedBoxSep;
     }

     return answer;
  }

  private void drawActorLanes(double bottom) {
     for(double x : actorPlacement.values()) {
        Line line = new Line(x,topOfLines,x,bottom);
        line.getStyleClass().add("actor-lane");
        shapes.add(line);
     }
  }

  private void drawEvents() {
     double ylevel = MARGIN + topOfLines;
     ArrayList<Shape> arrows = new ArrayList<>();
     ArrayList<Shape> texts = new ArrayList<>();
 
     for(Diagram.Event evt : diagram.getEvents()) {
        ylevel += 3*MARGIN; // fixme render arrows here!
     }

     drawActorLanes(ylevel);
     shapes.addAll(arrows); 
     shapes.addAll(texts); 
  }

  private Text titleWords() {
    Text title = new Text();
    title.setFont(Font.font("Helvetica", TEXT_SIZE*1.5));
    title.setTextAlignment(TextAlignment.CENTER);
    title.setText(diagram.getTitle());
    title.setTextOrigin(javafx.geometry.VPos.TOP);
    return title;
  }

  private void centerTitle(Text title) {
    // again, it would be better to calc the max and min in one traversal, but
    // the number of actors should be so small that it really doesn't matter.
    double maxpt = actorPlacement.values().stream().max(Double::compare).orElse(0.0);
    double minpt = actorPlacement.values().stream().min(Double::compare).orElse(0.0);
    double midpt = (maxpt+minpt)/2.0;
    double offset = midpt - (title.getLayoutBounds().getWidth()/2.0);
    if(offset < 0) offset = MARGIN;
    title.setX(offset);
    title.setY(MARGIN);
  }
}
