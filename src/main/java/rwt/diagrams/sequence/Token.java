package rwt.diagrams.sequence;

// this is just a data class... 
// no encapsulation or boilerplate
class Token {
  enum Type {
    TOK_TITLE,      // makes the title of the diagram...
    TOK_NOTE,       // makes a note on a line
    TOK_IDENTIFIER, // any non-numeric group of characters 
                    //not otherwise classified
    TOK_TO,         // indicates an arrow
    TOK_SELF,       // indicates a self-liine
    TOK_DASHED,     // indicates dashed arrow
    TOK_STRING,     // Everything after the colon ":"
    TOK_EOL         // The end of a statement/line
  }

  Type type;
  String data;

  Token(Type tt, String d) { type = tt; data = d; }

  // for debugging 
  @Override
  public String toString() {
     return String.format("Token of Type <%s> with data <%s>.", 
                          type, 
                          data);
  }
}
