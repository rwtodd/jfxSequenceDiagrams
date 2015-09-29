package rwt.diagrams.sequence;

import javafx.application.Platform;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polyline;
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
  private static final double WRAPPING_WIDTH = TEXT_SIZE * 10.0;
  private static final double ARROW_SEP = TEXT_SIZE * 2.0;
  private static final double AHEAD_SIZE = TEXT_SIZE * 0.8;


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
         txt.setTextAlignment(TextAlignment.CENTER);
         txt.setText(a.displayName);
         txt.setTextOrigin(javafx.geometry.VPos.TOP);
         if(txt.getLayoutBounds().getWidth() > WRAPPING_WIDTH) {
            txt.setWrappingWidth(WRAPPING_WIDTH);
         }
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
        if(evt.from.name.equals(evt.to.name)) {
           if(evt.note) {
              ylevel = renderNote(ylevel, evt, texts, arrows);
           } else {
              ylevel = renderSelfArrow(ylevel, evt, texts, arrows);
           }
        } else {
           ylevel = renderArrow(ylevel, evt, texts, arrows); 
        }
     }

     drawActorLanes(ylevel);
     shapes.addAll(arrows); 
     shapes.addAll(texts); 
  }

  private double renderNote(double ylevel, 
                            Diagram.Event evt,
                            List<Shape> texts,
                            List<Shape> arrows) {
     double ax = actorPlacement.get(evt.from.name);
     double midpt = (ax + ax + computedBoxSep) / 2.0;
     
     // render text to 80% of the length
     final Text words = renderArrowText(computedBoxSep, evt.desc);
     final double wordsWidth = words.getLayoutBounds().getWidth();
     final double wordsHeight = words.getLayoutBounds().getHeight();
     words.setY(ylevel + MARGIN);
     words.setX(midpt - wordsWidth/2.0);
     texts.add(words);

     // draw the surrounding box
     final double rwidth = wordsWidth + 2*MARGIN;
     final double rheight = wordsHeight + 2*MARGIN;
     Rectangle r = new Rectangle(midpt - rwidth/2.0, 
                                 ylevel, 
                                 rwidth,
                                 rheight);
     r.getStyleClass().add("note-box");
     
     // draw a line heading to the box...
     Line l = new Line(ax, 
                       ylevel + rheight/2.0,
                       midpt,
                       ylevel + rheight/2.0);
     l.getStyleClass().add("arrow-solid");                    
     arrows.add(l); 
     arrows.add(r); 

     return (ylevel + rheight + ARROW_SEP);
  }

  private Text renderArrowText(double len, 
                               String desc) {
     Text txt = new Text();
     txt.setFont(TEXT_FONT);
     txt.setWrappingWidth(len*0.8);
     txt.setTextAlignment(TextAlignment.CENTER);
     txt.setText(desc);
     txt.setTextOrigin(javafx.geometry.VPos.TOP);
     return txt;
  }


  private double renderSelfArrow(double ylevel, 
                                 Diagram.Event evt,
                                 List<Shape> texts,
                                 List<Shape> arrows) {
     // determine the midpoint between the nearest actors
     final double ax = actorPlacement.get(evt.from.name);
     final double midpt =  (ax + ax + computedBoxSep)/2.0;
     
     // render text to 80% of the length...
     final Text words = renderArrowText(computedBoxSep, evt.desc);
     final double wordsWidth = words.getLayoutBounds().getWidth();
     final double wordsHeight = words.getLayoutBounds().getHeight();
     words.setY(ylevel);
     words.setX(midpt - wordsWidth/2.0);
     texts.add(words);

     // mvoe down by the size of the text...
     ylevel += wordsHeight + MARGIN/2.0;


     // draw the polyline...
     final double bottom = ylevel + ARROW_SEP;
     Polyline p = new Polyline(ax,ylevel,
                               midpt,ylevel,
                               midpt,bottom,
                               ax,bottom);
     p.getStyleClass().add(evt.dashed?"arrow-dashed":"arrow-solid"); 
     arrows.add(p);
     drawArrowHead(1.0,arrows,ax,bottom);

     return bottom + ARROW_SEP; 

  }
  private double renderArrow(double ylevel, 
                             Diagram.Event evt,
                             List<Shape> texts,
                             List<Shape> arrows) {
     final double ax = actorPlacement.get(evt.from.name);
     final double bx = actorPlacement.get(evt.to.name);
     final double length = Math.abs(ax - bx);
     final double midpt = (ax + bx)/2.0; 

     final Text words = renderArrowText(length,evt.desc);
     final double wordsWidth = words.getLayoutBounds().getWidth();
     final double wordsHeight = words.getLayoutBounds().getHeight();
     words.setY(ylevel);
     words.setX(midpt - wordsWidth/2.0);

     // add a translucent backing to the words...
     Rectangle r = new Rectangle(words.getX(),words.getY(),wordsWidth,wordsHeight);
     r.getStyleClass().add("text-backing");
     texts.add(r);

     texts.add(words);
     
     // move down by the size of the text...
     ylevel += wordsHeight + MARGIN/2.0;

     // draw the line...
     Line l = new Line(ax,ylevel,bx,ylevel);
     l.getStyleClass().add(evt.dashed?"arrow-dashed":"arrow-solid"); 
     arrows.add(l);
     drawArrowHead(Math.signum(ax-bx),arrows,bx,ylevel);

     return ylevel + ARROW_SEP;
  }

  private void drawArrowHead(final double direction, final List<Shape> arrows, final double x, final double y) {
     final double backX = x + direction*AHEAD_SIZE;
     final double deltaY = AHEAD_SIZE/2.0;
     Polyline p = new Polyline( backX, y+deltaY,
                                x,y,
                                backX, y-deltaY);

     p.getStyleClass().add("arrow-solid");
     arrows.add(p);   
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
