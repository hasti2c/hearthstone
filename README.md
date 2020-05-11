# Hearthstone project: Phase 2
### Hasti Toossi (Student No. 98100464)

- This project might not work properly on windows systems. It would be better if it was tested on a Unix-based operating system such as Linux or MacOS.
- The cli interface also works for this phase, although the commands might not be very easy to guess! :D 
- To test the main version (with graphics) you can run the Hearthstone class but to test the cli you can run HearthstoneConsole. (It's nothing special.)


## Explanation 

- Each command given by the user is parsed and represented by a Command object. Then a CommandRunner object runs the command (by giving orders to different game objects).
- The Console class and the GraphicsController / DirectoryGraphics classes are responsible for communicating with the user respectively through cli and graphics. (It's important to note that these UIs shouldn't be used at the same time.)
- Each page has a Directory class and a DirectoryGraphics class. The Directory class isn't much more than an easier representation of each page. The DirectoryGraphics is more involved with logic, although mostly through CommandRunner.


## Sources

- The cards used in the game (and the pictures, etc.) where found through: [playhearthstone.com](The Play Hearthstone Website) and [hearthstone.gamepedia.com](Hearthstone Wiki).
- The following websites where used as learning materials: [Jenkov](jenkov.com), [stackoverflow.com](Stack Overflow).
- The build tool [maven.apache.org](Maven) was used in this project.


## External Libraries

- Javafx v11.0.2: This library was used because of the variation of its tools and also because it can be used together with fxml and css, resulting in more concise code.
- Gson v2.8.6: Through the classes Gson, JsonReader, etc., this library offers a variety of options for different needs and with varying degrees of difficulty.


## Positive Points

- A good degree of abstraction and cleanness was used in the source code, though it could be improved.
- A decent user interface, especially in the collections / store section with the Options feature.
- CLI feature
- Proper use of different java features such as Enums, Inner Classees, Interfaces, etc.


## Negative Points

- The scopes (logic and graphics) could be a lot more seperate. 
- The interface in the game section isn't that good. (Especially for weapons / hero powers :D ).
- The home page takes a long time to load when logging in. 
- There are too many classes, for example two different classes for each page might be excessive.