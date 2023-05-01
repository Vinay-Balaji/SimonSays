import java.awt.Color;
import java.util.Random;

import tester.Tester;
import javalib.funworld.*;
import javalib.worldimages.*;


// Represents a game of Simon Says
class SimonWorld extends World {
  //add fields needed to keep track of the state of the world
  boolean click;
  boolean flash;
  boolean gameEnd;
  ILoButton seq;
  ILoButton current;
  Random rand;

  /*
   * TEMPLATE:
   * ... this.click ...           --  boolean
   * ... this.flash ...           --  boolean
   * ... this.gameEnd ...         --  boolean
   * ... this.seq ...             --  ILoButton
   * ... this.current ...         --  ILoButton
   * ... this.rand ...            --  int
   * 
   * METHODS:
   * ... this.pickButton() ...    --  Button
   * ... this.allButtonsOnBoard() --  ILoButton
   * ... this.makeScene()         -- WorldScene
   * ... this.onTick()            -- World
   * ... this.onMouseClicked()   -- SimonWorld 
   * 
   * METHODS FOR FIELDS:
   * ... this.allButtonsOnBoard()     -- ILoButton
   * ... this.buttonsCreate()         -- WorldScene
   * ... this.current.firstButton()   -- World Scene
   * ... this.current.takeRest()      -- ILoButton
   * ... this.current.next()          -- SimonWorld 
   * ... this.current.nextAnimation() -- SimonWorld
   * ... this.current.begins          -- boolean
   */

  // Constructor when the game is started up 
  SimonWorld() {
    this.click = false;
    this.flash = true;
    this.gameEnd = false;
    this.rand = new Random();
    ILoButton seq = new ConsLoButton(this.pickButton(), new MtLoButton());
    this.seq = seq;
    this.current = seq;
  }

  // Full Constructor during the game 
  SimonWorld(boolean click, boolean flash, boolean gameEnd, ILoButton seq, ILoButton current) {
    this.click = click;
    this.flash = flash;
    this.gameEnd = gameEnd;
    this.seq = seq;
    this.current = current;
    this.rand = new Random();
  }

  // Game End Constructor 
  SimonWorld(boolean click, boolean flash, ILoButton seq, ILoButton current) {
    this(click, flash, false, seq, current); // uses constructors on line 22
  }

  // Fourth constructor - primarily used for testing purposes 
  SimonWorld(boolean click, boolean flash, 
      boolean gameEnd, ILoButton seq, ILoButton current, int rand) {
    this.click = click;
    this.flash = flash;
    this.gameEnd = gameEnd;
    this.seq = seq;
    this.current = current;
    this.rand = new Random();
  }

  // This method picks a random button with the use of the rand function
  Button pickButton() {
    if (this.rand.nextInt(4) == 0) {
      return new Button(Color.GREEN, 70, 70);
    } else if (this.rand.nextInt(4) == 1) {
      return new Button(Color.RED, 180, 70);
    } else if (this.rand.nextInt(4) == 2) {
      return new Button(Color.YELLOW, 70, 180);
    } else {
      return new Button(Color.BLUE, 180, 180);
    }
  }

  // This method returns the 4 buttons as a list of buttons that are on the board 
  ILoButton allButtonsOnBoard() {
    return new ConsLoButton(new Button(Color.GREEN, 70, 70), 
        new ConsLoButton(new Button(Color.RED, 180, 70), 
            new ConsLoButton(new Button(Color.YELLOW, 70, 180),
                new ConsLoButton(new Button(Color.BLUE, 180, 180), new MtLoButton()))));
  }

  // Draw the current state of the world
  public WorldScene makeScene() {
    WorldScene makeScenefalse = this.allButtonsOnBoard()
        .buttonsCreate(new WorldScene(250, 250), false);
    WorldScene makeScenetrue = this.current
        .firstButton(this.allButtonsOnBoard()
            .buttonsCreate(new WorldScene(250, 250), false), true);
    if (this.flash) {
      return makeScenetrue;
    } else {
      return makeScenefalse;
    }
  }

  // updating the world state
  public World onTick() {
    Button random = this.pickButton();
    if (this.click) {
      if (this.flash) {
        return new SimonWorld(true, false, this.seq, this.current.takeRest());
      }
      else if (this.gameEnd) {
        return this.endOfWorld("");
      }  else {
        return this.current.next(this.seq, random);
      }
    } else if (this.flash) {
      return new SimonWorld(this.click, false, this.gameEnd, this.seq, this.current);
    } else {
      return this.current.nextAnimation(this.seq);
    }

  }

  // handles mouse clicks and is given the mouse location
  public SimonWorld onMouseClicked(Posn pos) {
    boolean checkButton = this.allButtonsOnBoard().clickedOnButton(pos) && this.click;
    Button selectedButton = this.allButtonsOnBoard().firstButtonClicked(pos);
    if (!checkButton) {
      return this;
    } else if (this.current.begins(selectedButton.color)) {
      return new SimonWorld(true, true, this.seq, this.current);
    } else {
      // The End
      return new SimonWorld(true, false, true, this.seq, this.current);
    }
  }
}

// Represents a list of buttons
interface ILoButton {
  // Creates the buttons and has a boolean to determine whether the first btn in 
  // the sequence should  be lit 
  WorldScene buttonsCreate(WorldScene background, boolean firstBtn);

  // Creates the first button in the sequence
  WorldScene firstButton(WorldScene background, boolean firstBtn);

  // Add a button to the list 
  ConsLoButton addButton(Button btn);

  // Handles the SimonWorld after a user clicks on the world scene
  SimonWorld next(ILoButton seq, Button rand);

  // Checks whether the provided color is the respective color button 
  // that the list begins with 
  boolean begins(Color color);

  // Creates the following animation after the first animation
  SimonWorld nextAnimation(ILoButton seq);

  // Checks and returns which button was clicked first
  Button firstButtonClicked(Posn position);

  // Checks whether the user clicked within the world scene space 
  boolean clickedOnButton(Posn pos);

  // Takes the rest of the list 
  ILoButton takeRest();
} 

/*
 * TEMPLATE:
 * METHODS:
 * ... this.buttonsCreate(WorldScene Boolean) ...    --  WorldScene
 * ... this.firstButton(WorldScene Boolean) ...      --  WorldScene
 * ... this.addButton(Button) ...                    -- ConsLoBotton
 * ... this.next(ILoButton Button) ...               -- SimoonWorld
 * ... this.begins(Color) ...                        -- boolean
 * ... this.nextAnimation(ILoButton) ...             -- SimonWorld
 * ... this.firstButtonClicked(Posn) ...             -- Button
 * ... this.clickedOnButton(Posn) ...                -- boolean
 * ... this.takeRest() ...                           -- ILoButton 
 */

// Represents an empty list of buttons
class MtLoButton implements ILoButton {

  // Creates the buttons and has a boolean to determine whether the first btn in 
  // the sequence should  be lit 
  public WorldScene buttonsCreate(WorldScene background, boolean firstBtn) {
    return background;
  }

  // Creates the first button in the sequence
  public WorldScene firstButton(WorldScene background, boolean firstBtn) {
    return background;
  }

  //Add a button to the list 
  public ConsLoButton addButton(Button btn) {
    return new ConsLoButton(btn, this);
  }

  //Handles the SimonWorld after a user clicks on the world scene
  public SimonWorld next(ILoButton seq, Button rand) {
    return new SimonWorld(false, true, seq.addButton(rand), seq.addButton(rand));
  }

  // Checks whether the provided color is the respective color button 
  // that the list begins with 
  public boolean begins(Color color) {
    return false;
  }

  //Creates the following animation after the first animation
  public SimonWorld nextAnimation(ILoButton seq) {
    return new SimonWorld(true, false, seq, seq);
  }

  //Checks and returns which button was clicked first
  public Button firstButtonClicked(Posn position) {
    throw new RuntimeException("No Button");
  }

  //Checks whether the user clicked within the world scene space 
  public boolean clickedOnButton(Posn position) {
    return false;
  }

  // Takes the rest of the list 
  public ILoButton takeRest() {
    return this;
  }

} 

// Represents a non-empty list of buttons
class ConsLoButton implements ILoButton {
  Button first;
  ILoButton rest;

  ConsLoButton(Button first, ILoButton rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * TEMPLATE:
   * ... this.first ...                                -- Button
   * ... this.rest ...                                 -- ILoButton
   * METHODS:
   * ... this.buttonsCreate(WorldScene boolean) ...    -- WorldScene
   * ... this.firstButton(Button)                      -- WorldScene
   * ... this.addButton(Button)                        -- ConsLoButton
   * ... this.next(Button)                             -- SimoonWorld
   * ... this.begins(Color)                            -- boolean
   * ... this.nextAnimation(ILoButton)                 -- SimonWorld
   * ... this.firstButtonClicked(Posn)                 -- Button
   * ... this.clickedOnButton(Posn)                    -- Boolean
   * ... this.takeRest()                               -- ILoButton 
   * METHODS FOR FIELDS:
   * ... this.rest.buttonsCreate(WorldScene boolean)   -- WorldScene 
   * ... this.first.draw(WorldScene boolean)           -- WorldScene
   * ... this.rest.addButton(Button)                   -- ConsLoButton
   * ... this.first.checkPlace(Posn)                   -- boolean
   * ... this.rest.firstButtonClicked(Button)          -- Button
   * ... this.rest.clickedOnButton(position)           -- Boolean
   */


  // Creates the buttons and has a boolean to determine whether the first btn in 
  // the sequence should  be lit 
  public WorldScene buttonsCreate(WorldScene background, boolean firstBtn) {
    return this.rest.buttonsCreate(firstButton(background, firstBtn), false);
  }

  // Creates the first button in the sequence
  public WorldScene firstButton(WorldScene background, boolean firstBtn) {
    return this.first.draw(background, firstBtn);
  }

  //Add a button to the list 
  public ConsLoButton addButton(Button btn) {
    return new ConsLoButton(this.first, this.rest.addButton(btn));
  }

  //Handles the SimonWorld after a user clicks on the world scene
  public SimonWorld next(ILoButton seq, Button rand) {
    return new SimonWorld(true, false, seq, this);
  }

  // Checks whether the provided color is the respective color button 
  // that the list begins with 
  public boolean begins(Color color) {
    return this.first.color.equals(color);
  }

  //Creates the following animation after the first animation
  public SimonWorld nextAnimation(ILoButton seq) {
    return new SimonWorld(false, true, seq, this.rest);
  }

  //Checks and returns which button was clicked first
  public Button firstButtonClicked(Posn position) {
    boolean positionCheck = this.first.checkPlace(position);
    if (positionCheck) {
      return this.first;
    } else {
      return this.rest.firstButtonClicked(position);
    }
  }

  //Checks whether the user clicked within the world scene space 
  public boolean clickedOnButton(Posn position) {
    return this.first.checkPlace(position) 
        || this.rest.clickedOnButton(position); 
  }

  //Takes the rest of the list 
  public ILoButton takeRest() {
    return this.rest;
  }
} 

// Represents one of the four buttons you can click
class Button {
  Color color;
  int x;
  int y;

  Button(Color color, int x, int y) {
    this.color = color;
    this.x = x;
    this.y = y;
  }

  /*
   * TEMPLATE:
   * ... this.color ...                                -- color
   * ... this.x ...                                    -- int
   * ... this.y ...                                    -- int
   * METHODS:
   * ... this.drawButton(Color) ...                    -- WorldImage
   * ... this.drawDark() ...                           -- WorldImage
   * ... this.drawLit() ...                            -- WorldImage
   * ... this.draw(WorldScene boolean) ...             -- WorldScene
   * ... this.checkPlace(Posn) ...                     -- boolean
   * METHODS FOR FIELDS:
   * ... this.color.darker() ...                       -- WorldImage
   * ... this.color.brighter() ...                     -- WorldImage
   */

  WorldImage drawButton(Color color) {
    return new CircleImage(55, OutlineMode.SOLID, color);
  }

  // Draw this button dark
  WorldImage drawDark() {
    return this.drawButton(this.color.darker().darker());
  }

  // Draw this button lit
  WorldImage drawLit() {
    return this.drawButton(this.color.brighter().brighter());
  }

  // Places the buttons on the WorldScene background
  WorldScene draw(WorldScene background, boolean check) {
    WorldScene checkFalse = background.placeImageXY(this.drawDark(), this.x, this.y);
    WorldScene checkTrue = background.placeImageXY(this.drawLit(), this.x, this.y);
    if (!check) {
      return checkFalse;
    } else {
      return checkTrue;
    }
  }

  // Helper function that checks whether a user clicked within the position of a button
  boolean checkPlace(Posn position) {
    return (position.x < this.x + 60) && (position.x > this.x - 60)
        &&  (position.y < this.y + 60) && (position.y > this.y - 60);
  }

}


//Examples
class ExamplesSimon {
  //put all of your examples and tests here
  Button Button1 = new Button(Color.RED, 100, 100);
  Button Button2 = new Button(Color.BLUE, 80, 100);
  Button Button3 = new Button(Color.YELLOW, 100, 80);
  Button Button4 = new Button(Color.GREEN, 80, 80);
  Button Button5 = new Button(Color.GREEN, 100, 80);
  ILoButton empty = new MtLoButton();
  ILoButton list1 = new ConsLoButton(Button1, 
      new ConsLoButton(Button2, new ConsLoButton(Button3, new ConsLoButton(Button4, empty))));
  Posn posn1 = new Posn(0, 0);
  Posn posn2 = new Posn(75, 125);
  WorldScene background = new WorldScene(100, 100);
  ILoButton list2 = new ConsLoButton(Button2, 
      new ConsLoButton(Button3, new ConsLoButton(Button4, empty)));

  SimonWorld s0 = new SimonWorld();
  SimonWorld s5 = new SimonWorld(false, false, false, empty, empty);
  SimonWorld s2 = new SimonWorld(false, true, false, empty, empty);
  SimonWorld s6 = new SimonWorld(false, false, false, list1, list1, 4);


  // tests for checkPlace method
  boolean testCheckPlace(Tester t) {
    return t.checkExpect(Button1.checkPlace(posn2), true);
  }

  // tests for the draw method 
  boolean testDraw(Tester t) {
    return t.checkExpect(Button1.draw(background, true), 
        background.placeImageXY(Button1.drawButton(Color.RED
            .brighter().brighter()), 100, 100))
        && t.checkExpect(Button1.draw(background, false), 
            background.placeImageXY(Button1
                .drawButton(Color.RED.darker().darker()), 100, 100));
  }

  // tests for the drawButton method 
  boolean testDrawButton(Tester t) {
    return t.checkExpect(Button1.drawButton(Color.BLUE), 
        new CircleImage(55, OutlineMode.SOLID, Color.BLUE));
  }

  // tests for firstButton method
  boolean testFirstButton(Tester t) {
    return t.checkExpect(list1.firstButton(background, false), Button1.draw(background, false))
        && t.checkExpect(list1.firstButton(background, true), Button1.draw(background, true))
        && t.checkExpect(empty.firstButton(background, false), background)
        && t.checkExpect(empty.firstButton(background, true), background);
  }

  // tests for addButton method
  boolean testAddButton(Tester t) {
    return t.checkExpect(list1.addButton(Button5), 
        new ConsLoButton(Button1, new ConsLoButton(Button2, 
        new ConsLoButton(Button3, new ConsLoButton(Button4, 
            new ConsLoButton(Button5, empty))))))
        && t.checkExpect(empty.addButton(Button1), new ConsLoButton(Button1, empty));
  }

  // tests for next method
  boolean testNext(Tester t) {
    return t.checkExpect(list1.next(list2, Button1), 
        new SimonWorld(true, false, list2, list1))
        && t.checkExpect(empty.next(list2, Button1), 
            new SimonWorld(false, true, list2.addButton(Button1), 
                list2.addButton(Button1)));
  }

  // tests for begins method
  boolean testBegins(Tester t) {
    return t.checkExpect(list1.begins(Color.RED), true)
        && t.checkExpect(list1.begins(Color.BLUE), false)
        && t.checkExpect(empty.begins(Color.BLUE), false);
  }

  // tests for buttonsCreate method
  boolean testButtonsCreate(Tester t) {
    return t.checkExpect(list1.buttonsCreate(background, false), new WorldScene(100,100)
        .placeImageXY(Button1.drawButton(Color.RED.darker().darker()),100,100)
        .placeImageXY(Button2.drawButton(Color.BLUE.darker().darker()),80,100)
        .placeImageXY(Button3.drawButton(Color.YELLOW.darker().darker()),100,80)
        .placeImageXY(Button4.drawButton(Color.GREEN.darker().darker()),80,80))
        && t.checkExpect(empty.buttonsCreate(background, false), background);
  }

  // tests for nextAnimation method 
  boolean testNextAnimation(Tester t) {
    return t.checkExpect(list1.nextAnimation(list2), new SimonWorld(false, true, list2, list2)) 
        && t.checkExpect(empty.nextAnimation(list2), new SimonWorld(true, false, list2, list2));
  }

  // tests for firstButtonClicked method
  boolean testFirstButtonClicked(Tester t) {
    return t.checkExpect(list1.firstButtonClicked(new Posn(100, 100)), Button1) 
        && t.checkExpect(list2.firstButtonClicked(new Posn(30, 30)), Button4) 
        && t.checkExceptionType(NoSuchMethodException.class, empty, "No Button", -1);
  }

  // tests for clickedOnButton method
  boolean testClickedOnButton(Tester t) {
    return t.checkExpect(list1.clickedOnButton(posn2), true)
        && t.checkExpect(list2.clickedOnButton(posn1), false)
        && t.checkExpect(empty.clickedOnButton(posn1), false);
  }

  // tests for takeRest method
  boolean testTakeRest(Tester t) {
    return t.checkExpect(list1.takeRest(), 
        new ConsLoButton(Button2, 
            new ConsLoButton(Button3, 
                new ConsLoButton(Button4, empty))))
        && t.checkExpect(list2.takeRest(), 
            new ConsLoButton(Button3, 
                new ConsLoButton(Button4, empty)))
        && t.checkExpect(empty.takeRest(), empty);
  }


  //tests for allButtonsOnBoard method
  boolean testAllButtonsOnBoard(Tester t) {
    return t.checkExpect(s0.allButtonsOnBoard(), new ConsLoButton(new Button(Color.GREEN, 70, 70), 
        new ConsLoButton(new Button(Color.RED, 180, 70), 
            new ConsLoButton(new Button(Color.YELLOW, 70, 180),
                new ConsLoButton(new Button(Color.BLUE, 180, 180), new MtLoButton())))));
  }

  //tests for makeScene method
  boolean testMakeScene(Tester t) {
    return t.checkExpect(s5.makeScene(), 
        s5.allButtonsOnBoard()
        .buttonsCreate(new WorldScene(250, 250), false))
        && t.checkExpect(s2.makeScene(), 
            s2.current.firstButton(s2.allButtonsOnBoard()
                .buttonsCreate(new WorldScene(250, 250), false), true));
  }

  // tests for onMouseClicked method
  boolean testOnMouseClicked(Tester t) {
    return t.checkExpect(s5.onMouseClicked(new Posn(100, 100)), 
        new SimonWorld(false,false, false, empty, empty))
        && t.checkExpect(s6.onMouseClicked(new Posn(100, 100)), 
            new SimonWorld(false,false, false, list1, list1));
  }

  //tests for onTick method
  boolean testOnTick(Tester t) {
    return t.checkExpect(s6.onTick(), new SimonWorld(false, true, false, list1, list1.takeRest()))
        && t.checkExpect(s5.onTick(), new SimonWorld(true, false, false, empty, empty.takeRest()));
  }

  // runs the game by creating a world and calling bigBang
  boolean testSimonSays(Tester t) {
    SimonWorld starterWorld = new SimonWorld();
    int sceneSize = 500;
    return starterWorld.bigBang(sceneSize, sceneSize, 1);
  }

}
