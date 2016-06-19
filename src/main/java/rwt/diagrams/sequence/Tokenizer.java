package rwt.diagrams.sequence;

import java.util.Iterator;

/** A tokenizer for the diagram language.
  * @author Richard Todd
  */ 
public class Tokenizer implements Iterable<Token> {
  private final String src;

  public Tokenizer(String text) {
      src = text;
  }

  public Iterator<Token> iterator() {
      return new TokenIterator();
  }

  /** Iterates over the tokens in the sequence-diagram
    * language.  I refactored the WPF implementation
    * to use an actual iterator to get access to all
    * the java.util.stream functionality, and hopefully
    * make the port more idiomatic.
    */
  private class TokenIterator implements Iterator<Token> {
     private int index;
  
     public TokenIterator() {
        index = 0;
     }
  
     public Token next() {
          Token ans = null; // placeholder for the answer
  
          skipWS();
          char c = nextChar();
          switch (c)
          {
             case '#':   // comment
                 getToEOL();
                 ans = new Token(Token.Type.TOK_EOL, null);
                 break;
             case '\n':  // end of line/statement
                 ans = new Token(Token.Type.TOK_EOL, null);
                 break;
             case ':':   // start of free-form String arg
                 ans = new Token(Token.Type.TOK_STRING, getToEOL());
                 break;
             default:    // must be a keyword or identifier...
                 ans = processIdentifier(c);
                 break;
          }

          return ans;
    }
  
     /** Find out if there are any more tokens.
       * Since the tokenizer will return EOLs 
       * forever at the end of the input, we need
       * this to decide when we're done. 
       */
     public boolean hasNext() { return index <= src.length(); }
  
  
     // process characters and figure out if it's a keyword or not.
     private Token processIdentifier(char first)
     {
         Token ans = null;
         StringBuilder sb = new StringBuilder();
         sb.append(first);
  
         char c = nextChar();
         while (c != ':' && !Character.isWhitespace(c))
         {
             sb.append(c);
             c = nextChar();
         }
         ungetChar(); // push back whatever char we stopped on.
  
         // now we have an identifier... we need to see if it's a keyword...
         String ident = sb.toString();
         switch (ident.toUpperCase())
         {
             case "TO":
                 ans = new Token(Token.Type.TOK_TO, ident);
                 break;
             case "SELF":
                 ans = new Token(Token.Type.TOK_SELF, ident);
                 break;
             case "DASHED":
                 ans = new Token(Token.Type.TOK_DASHED, ident);
                 break;
             case "TITLE":
                 ans = new Token(Token.Type.TOK_TITLE, ident);
                 break;
             case "NOTE":
                 ans = new Token(Token.Type.TOK_NOTE, ident);
                 break;
             default:
                 ans = new Token(Token.Type.TOK_IDENTIFIER, ident);
                 break;
         }
         return ans;
     }
  
     // get a String up to the EOL
     private String getToEOL()
     {
         StringBuilder sb = new StringBuilder();
         char c = nextChar();
         while ((c != '\n') && (c != '#'))
         {
             sb.append(c);
             c = nextChar();
         }
         ungetChar(); // push back the '\n'
         return sb.toString().trim();
     }
  
     private void ungetChar() { index--; }
     private char nextChar()
     {
         char ans = '\n';
         if (index <  src.length()) { ans = src.charAt(index); }
         index++;
         return ans;
     }
  
     private void skipWS() {
         while(true) {
             char c = nextChar();
             if(c == '\n' || !Character.isWhitespace(c)) break;
         }
         ungetChar(); // put back whatever stopped us...
     }
    
  }


}

