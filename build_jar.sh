#!/bin/bash

# nothing fancy... I hate that the standard build tools (Ant, Maven, etc) are 
# all so full of dependencies.  A simple tool is best, in my opinion.  So for
# the moment, for utilities, I stick with a 2-line shell script. 

javac -cp . rwt/diagrams/sequence/*.java rwt/diagrams/Sequence.java
find rwt -type f \! -name '*.java' | xargs jar cvfe sequence_diagrams.jar rwt.diagrams.Sequence 

