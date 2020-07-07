# Hearthstone project: Phase 3
### Hasti Toossi (Student No. 98100464)

- The maven build tool is used.
- Because of the upload restriction, I have uploaded the assets and the javafx library on github instead.

## Explanation 

- The Inventory class holds the heros, cards and decks that a system owns, as well as the current deck. 
- The Player class only has data about the system's username, balance and other account info, as well as its inventory.
- The GamePlayer class has the inventory and handles its own game actions. (Player and GamePlayer are independant of each other.)
- After each action, each GamePlayer invokes the doAction method on each of its cards which is related to the current event, e.g. doActionOnDraw(), doActionOnDamaged(), etc.
- Each card calls the callDoAction() method on any ability which needs to be activated.
- In the Activity class, the target is determined and then the action is done in the related subclass.

## Sources

- The build tool [maven](https://maven.apache.org) was used in this project.
- The cards used in the game (and the pictures, etc.) were found through [Hearthstone Wiki](https://hearthstone.gamepedia.com).
- The assets were found through the inspect elements feature on [HS Replay](https://HSReplay.net).


## External Libraries

- Javafx v11.0.2: This library was used because of the variation of its tools and also because it can be used together with fxml and css, resulting in more concise code.
- Gson v2.8.6: Through the classes Gson, JsonReader, etc., this library offers a variety of options for different needs and with varying degrees of difficulty.


## Positive Points

- The abilities were implemented a lot of generalization and abstraction.
- It's possible to implement many abilities with the current source code.
- The Configor class reads the objects from json very cleanly and it's independant of the particular class. Also there's a lot of Reflection and Generics used in that class, making the code more concise.

## Negative Points

- Some classes might have too much access to change fields. 
- The game page has no animation and not a really good user interface. (It looks good generally, but doesn't show errors, etc.).
- Some logic classes have access to and change graphic classes and don't work independantly enough.