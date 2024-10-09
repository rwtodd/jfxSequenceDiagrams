plugins {
    java
    application
    id("org.openjfx.javafxplugin") version ("0.1.0") 
}

group = "org.rwtodd"
version = "1.0.0"

repositories {
   mavenCentral()
}

javafx {
   version = "23"
   modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
}

dependencies {
}


tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 21
}

application {
    mainModule = "rwt.diagrams"
    mainClass = "rwt.diagrams.Sequence"
}

