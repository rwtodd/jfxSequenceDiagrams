package rwt.diagrams.sequence;

import java.util.List;
import java.util.ArrayList;

public class Diagram {
    // a pure data object, no encapsulation or boilerplate
    class Actor {
      String name;
      String displayName;
      Actor(String name, String displayName) {
         this.name = name;
         this.displayName = displayName;
      } 
    }
    
    // a pure data object, no encapsulation or boilerplate
    class ActorLine {
      Actor from;
      Actor to;
      boolean dashed;
      boolean note;
      String desc;
    
      ActorLine(Actor from, Actor to) {
         this.from = from;
         this.to = to;
         dashed = false;
         note = false;
         desc = "";
      }
    }

   private String title;
   String getTitle() { return title; }
   void setTitle(String t) { title = t; }

   private boolean errorState; 
   boolean hasErrors() { return errorState; }
   void setErrors(boolean e) { errorState = e; }

   private List<Actor> actors;
   List<Actor> getActors() { return actors; }

   private List<ActorLine> lines;
   List<ActorLine> getLines() { return lines; } 

   public Diagram()
   {
       errorState = false;
       title = "Untitled";
       actors = new ArrayList<Actor>();
       lines = new ArrayList<ActorLine>();
   }

   Actor maybeNewActor(String name)
   {
       String searchFor = name.toUpperCase();
       int idx = actors.indexOf(searchFor);
       Actor ans; 
       
       if (idx >= 0) {
           ans = actors.get(idx);
       } else {
           ans = new Actor(searchFor,name);
           actors.add(ans);
       }
       return ans;
   }

   ActorLine addLine(Actor from, Actor to)
   {
       ActorLine ans = new ActorLine(from, to);
       lines.add(ans);
       return ans;
   }
}
