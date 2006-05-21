package com.becker.puzzle.adventure;

/**
 * @author Barry Becker Date: Apr 22, 2006
 */
public final class SceneData {


     private static final Scene[] SCENES = {
        new Scene("start",
                  "       ****************************************\n " +
                  "      *** The Mansion of Professor Ludlow ****\n" +
                  "       ****************************************\n" +
                  "                                                    adapted from a D&D module by James Ward\n\n"+
                  "     You are on a camping trip with a large group of boyscouts. During a nighttime hike, you get separated from the rest of the group.\n" +
                  "You stumble upon a meadow. There in front of you is a large mansion. You hear an eary plea for help coming from inside. What do you do?",
                  new Choice[]{
                      new Choice("Knock on the front door", "no response"),
                      new Choice("Open the front door without knocking", "inside entrance"),
                      new Choice("Scream", "sky falls"),
                      new Choice("Return to the rest of your group", "quit")
                  }),

        new Scene("no response",
                  "No one comes to the door and you hear nothing. A few minutes pass. Still nothing. ",
                  new Choice[]{
                      new Choice("Knock again", "no response"),
                      new Choice("Open the front door without knocking", "inside entrance"),
                      new Choice("Scream", "sky falls"),
                      new Choice("Return to the rest of your group", "quit")
                  }),

        new Scene("inside entrance",
                  "The double door is unlocked and opens easily. \n" +
                  "The beam of your flashligh reveals an empty hallway with a mirror at the opposite end. \n" +
                  "There are openings that lead to rooms on the right and left. " +
                  "The hall has inch-thick red carpeting\n" +
                  "and walnut-paneled walls. The mirror at the end runs from the\n" +
                  "floor to the twenty-foot-high ceiling and covers the forty feet of wall\n" +
                  "section on that south face of the hall.",
                  new Choice[]{
                      new Choice("Go through left opening", "rat room"),
                      new Choice("Go through right opening", "boogie"),
                      new Choice("Tamper with mirror at the end of the hall.", "mirror"),
                      new Choice("exit", "quit")
                  }),

        new Scene("rat room",
                  "There are leaves on the floor, and an old worn out couch in the north east corner.\n  " +
                  "There is a musty smell to the room,\n" +
                  "and leaves are littered on the floor. The couch is an old, dusty,\n" +
                  "overstuffed relic with several cushions and rips on all parts of it. The\n" +
                  "room measures sixty feet east and west and forty feet north and\n" +
                  "south, if paced out."
                  +"There is a door on the east side of the room.",
                   new Choice[]{
                      new Choice("Inspect the couch", "rat attack"),
                      new Choice("Sift through the leaves on the floor", "key"),
                      new Choice("Go through the door on the east side.", "C"),
                  }),


        new Scene("rat attack",
                  "Rats com out from under the couch and attack you. One bites you and gives you a terrible disease.\n" +
                  "(Suggestion: maybe we should have generated melee combat here so the outcome is random.)"),

        new Scene("key",
                  "You find a golden key under the pile of leaves.\n"
                  +"Suggestion: keep an inventory of items that the player has."),

        new Scene("C"  ,
                  "   The beam of your flashlight show a hall with pink walls and a floor of red marble. " +
                  "The walls have clean squares all\n" +
                  "over them where pictures were obviously once hung, but are no\n" +
                  "longer. The floor has dried leaves littering it. If paced out, the hall is\n" +
                  "eighty feet long east and west and twenty feet long north and south." +
                  "The hall leads to another room at the end. ",
                   new Choice[]{
                      new Choice("Proceed into the room at the end of the hall.", "D"),
                  }),

        new Scene("D"  ,
                  "The beam of the flashlight shows a room with gray painted\n" +
                  "walls and a cement floor. It is filled with wooden crates and there are\n" +
                  "leaves all over the room in large piles.\n" +
                  "    There are 200 empty wooden \n" +
                  "crates of differing sizes, and the biggest concentration of leaves is in\n" +
                  "the southwest corner of the room. The room measures sixty feet east\n" +
                  "and west and forty feet north and south and has a side alcove to the\n" +
                  "south starting on the east wall that is forty feet long east and west and\n" +
                  "twenty feet long north and south; it is here that the concentration of\n" +
                  "leaves is located. Behind a big crate along the east wall, thirty feet\n" +
                  "from the north corner, one finds a door with a normal handle.\n",
                  new Choice[]{
                      new Choice("Go through door behind big crate.", "E"),
                      new Choice("Look inside all the crates.", "crates"),
                      new Choice("Sift through the pile of leaves.", "D leaves")
                  }),

        new Scene("crates"  ,
                  "  It takes you a really long time, but you finally open and inspect all the crates." +
                  "You find nothing in them except for leaves, dust, and 200 paperclips. ",
                   new Choice[]{
                      new Choice("Proceed into the room at the end of the hall.", "D"),
                  }),

        new Scene("D leaves"  ,
                  " There are snakes in the leaves. A group of 3 4 foot long rattlesnakes attack. " +
                  " (HP: 10,8,5; #AT: 1; 0:\\n\" +\n" +
                  "\"1-3; AC: 5; SA: Save versus death caused by poison). "
                  ),

        new Scene("E",
                  "    The beam of your flashlight shows a room with gray painted\n" +
                  "walls and a cement floor. It is filled with boxes and barrels.\n" +
                  "     There are thirteen barrels of grain\n" +
                  "alcohol each with the number \"50\" painted on it. There are thirteen\n" +
                  "boxes filled with pairs of white gloves. A metal chest contains a silver\n" +
                  "set made to serve eighteen people, four pairs of silver candlesticks,\n" +
                  "three huge silver carving knives (like short swords), and eighteen\n" +
                  "solid silver goblets (total worth of the silver items is 20,000 silver\n" +
                  "pieces). There are thirteen chests filled with books about plants;\n" +
                  "thirteen boxes filled with assorted sizes of clay pots; and ten large\n" +
                  "crates with mattresses in them. When paced out, the room is forty\n" +
                  "feet wide east and west and sixty feet wide north and south. A door\n" +
                  "on the east wall is originally hidden by the mattress crates.\n" +
                  "    When you enter the room,\n" +
                  "are immediately attacked by an incredibly old man dressed in\n" +
                  "rags and carrying a butcher knife (HP: 7; #AT: 1; 0: 1-6; AC: 10; SA:\n" +
                  "None). This old man leaps out from behind some of the boxes and\n" +
                  "surprises you. ",
                  //"   He will never follow a retreating group out\n" +
                  // "of the room, but will act dangerously at all other times and will never\n" +
                  //"listen to any type of reason.");
                  new Choice[]{
                      new Choice("Fight the old man.", "fight old man"),
                      new Choice("Open the barrels.", "open barrels"),
                      new Choice("Go through the door on the east wall.", "F")
                  }),


        new Scene("fight old man",
                  "He beats you up, then eats you while you are still alive."),

        new Scene("open barrels",
                  "They appear to contain a liquid that looks and smells like grain alcohol.",
                  new Choice[]{
                      new Choice("Drink the alcohol.", "drink alcohol"),
                      new Choice("Spill the barrels of alcohol.", "spill alcohol"),
                  }),

        new Scene("drink alcohol",
                  "You get really drunk and pass out. You never get a chance to wake up because the denizens of the mansion eat you first."
                  ),

        new Scene("spill alcohol",
                 "Great. You've made a huge mess. There is about 2 inches of alcohol on the floor.",
                  new Choice[]{
                      new Choice("light a match", "light alcohol"),
                  }),

        new Scene("light alcohol",
                  "The room and everything in it (including you) go up in a huge ball of fire. There is no escaping the conflagoration."),


        new Scene("F",
                  "The door opens on a brick wall. In other words it a false door."),

        new Scene("boogie",
                  "The boogie man jumps out and eats you."),

        new Scene("mirror",
                  "You find that the mirror lifts easily. There is a secrete door behind it.",
                  new Choice[]{
                      new Choice("Try to open the secret door.", "gold"),
                  }),

        new Scene("gold",
                  "Congratulations. There is a pot of gold behind the door. You go home rich."),

        new Scene("quit",
                  "You go back to the others. They don't believe your story. You enjoy the rest of the camping trip."),

        new Scene("sky falls",
                  "The sky falls on you."),

    };

    // private so instances cannot be made.
    private SceneData() {}

    public static Scene[] getScenes() {
        return  SCENES;
    }
}
