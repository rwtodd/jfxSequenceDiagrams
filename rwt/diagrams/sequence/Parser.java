package rwt.diagrams.sequence;

import java.util.Iterator;

/** Just provides a static method to
  * parse the tokens into a rwt.diagrams.sequence.Diagram.
  * @author Richard Todd
  */
public class Parser {
    public static Diagram parse(String source) {
        Iterator<Token> t = new Tokenizer(source).iterator();
        Diagram ans = new Diagram();
     
        while( !ans.hasErrors() && t.hasNext() )
        {
            Token next = t.next();
            switch (next.type)
            {
                case TOK_EOL:  // empty lines are ok
                    break;   
                case TOK_TITLE:  // we have a title
                    getTitle(t,ans);
                    break;
                case TOK_NOTE:  // we have a note
                    getNote(t, ans);
                    break;
                case TOK_IDENTIFIER:
                    parseIdentifier(t, ans, next);
                    break;
                default:  // anything else is a problem!
                    ans.setErrors(true);
                    break;
            }
        }

        return ans;
    }

    private static void getNote(Iterator<Token> t, Diagram d) {
        // a NOTE takes the form:  NOTE Actor: the note is here....
        Token id = t.next();
        if (id.type != Token.Type.TOK_IDENTIFIER) { 
             d.setErrors(true); 
             return; 
        }
        Diagram.Actor actor = d.maybeNewActor(id.data);

        Token str = t.next();
        String desc = null;
        switch(str.type) {
            case TOK_STRING:
                desc = str.data;
                break;
            case TOK_EOL:
                desc = "";
                break;
            default:
                d.setErrors(true);
                return;
        }

        Diagram.ActorLine ln = d.addLine(actor, actor);
        ln.desc = desc;
        ln.note = true;
    }

    private static void parseIdentifier(Iterator<Token> t, 
                                        Diagram d, 
                                        Token first) {
        // OK, we got an identifier... let's make sure the 
        // Diagram knows about it...
        Diagram.Actor left = d.maybeNewActor(first.data);
        
        //  Valid continuations are:
        //   EOL          ... just define the actor so we have a good order
        //   STRING EOL   ... define the actor with a display name
        //   TO ID [DASHED] [STRING] EOL  ... define an arrow
        Token second = t.next();
        switch (second.type)
        {
            case TOK_EOL:  // no problem here...
                break;
            case TOK_STRING: // giving a display name...
                left.displayName = second.data;
                break;
            case TOK_TO:  // defining an arrow...
                parseArrow(t, d, left);
                break;
            default:   // something went wrong here...
                d.setErrors(true);
                break;
        }
    }

    private static void parseArrow(Iterator<Token> t, 
                                   Diagram d, 
                                   Diagram.Actor left) {
        Token rightID = t.next();
        
        // this should be an identifier... or SELF...
        Diagram.Actor right = null;

        switch (rightID.type)
        {
            case TOK_IDENTIFIER:
                right = d.maybeNewActor(rightID.data);
                break;
            case TOK_SELF:
                right = left;
                break;
            default:
                d.setErrors(true);
                return;
        }

        // we definitely have a line now...
        Diagram.ActorLine line = d.addLine(left, right);

        // OK, now we have a possible DASHED and STRING...
        Token rest = t.next();
        while (rest.type != Token.Type.TOK_EOL)
        {
            switch (rest.type)
            {
                case TOK_DASHED:
                    line.dashed = true;
                    break;
                case TOK_STRING:
                    line.desc = rest.data;
                    break;
                default:
                    d.setErrors(true); 
                    return;
            }
            rest = t.next();
        }
    }

    private static void getTitle(Iterator<Token> t, Diagram d) {
        Token str = t.next();
        if(str.type == Token.Type.TOK_STRING) {
           d.setTitle(str.data);
        }
        else
        {
            d.setErrors(true);
        }
    }
}

