public class Game
{
    private Parser parser;
    private Room currentRoom;
    public Player p1;
    int count=0;
    int pokeCount=0;
    int evoCount=0;
    int pid=0;
    String starter ="";
    Room bedroom, livingRoom, palletTown, profLab, wilderness;
    Item potion, pokeBall, pokedex, bulbasaur, charmander, squirtle;
    /**
     * Create the game and initialise its internal map.
     */
    public Game()
    {
        createRooms();
        parser = new Parser();
        p1 = new Player();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {

        potion = new Item("used to regain health");
        pokeBall = new Item("used to capture Pokémon");
        pokedex = new Item("gives information on wild Pokémon");
        bulbasaur = new Item("The grass Pokémon,Bulbasaur");
        charmander = new Item("The fire Pokémon,Charmander");
        squirtle = new Item ("The water Pokémon,Squirtle");


        // create the rooms
        bedroom = new Room("in your bedroom");
        livingRoom = new Room("in your living room, you can see your mother across the table");
        palletTown = new Room("outside Pallet Town");
        profLab = new Room("in the Professor's laboratory");
        wilderness = new Room("now within the wilderness");

        // initialise room exits
        bedroom.setExit("south", livingRoom);

        livingRoom.setExit("north", bedroom);
        livingRoom.setExit("south", palletTown);

        palletTown.setExit("north", livingRoom);
        palletTown.setExit("east", profLab);
        palletTown.setExit("south", wilderness);

        profLab.setExit("west", palletTown);

        wilderness.setExit("north", palletTown);

        currentRoom = bedroom;  // start game outside

        bedroom.setItem("Potion", potion);
        wilderness.setItem("Pokeball", pokeBall);
        profLab.setItem("Pokedex", pokedex);
        profLab.setItem("Bulbasaur", bulbasaur);
        profLab.setItem("Charmander", charmander);
        profLab.setItem("Squirtle", squirtle);
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play()
    {
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
            if(p1.getLevel()==100){
                finished=true;
            }
            else if(p1.getHealth()<1){
                System.out.println("You have been defeated in battle, you rushed home to see your mother and get your "+starter+" healed");
                p1.recover();
                currentRoom = livingRoom;
            }
        }
        System.out.println("Congratulations, you have reached level 100 with your "+starter+", which means that you have become a Pokémon Master!");
        System.out.println("Thank you for playing Pokémon Rian Red. Farewell!");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println("_///////            _//                                                     _///////                                _///////                   _//");
        System.out.println("_//    _//          _//                                                     _//    _//   _/                         _//    _//                 _//");
        System.out.println("_//    _//   _//    _//  _//   _//    _/// _// _//    _//    _// _//        _//    _//        _//    _// _//        _//    _//     _//         _//");
        System.out.println("_///////   _//  _// _// _//  _/   _//  _//  _/  _// _//  _//  _//  _//      _/ _//      _// _//  _//  _//  _//      _/ _//       _/   _//  _// _//");
        System.out.println("_//       _//    _//_/_//   _///// _// _//  _/  _//_//    _// _//  _//      _//  _//    _//_//   _//  _//  _//      _//  _//    _///// _//_/   _//");
        System.out.println("_//        _//  _// _// _// _/         _//  _/  _// _//  _//  _//  _//      _//    _//  _//_//   _//  _//  _//      _//    _//  _/        _/   _//");
        System.out.println("_//          _//    _//  _//  _////   _///  _/  _//   _//    _///  _//      _//      _//_//  _// _///_///  _//      _//      _//  _////    _// _//");
        System.out.println();
        System.out.println("Welcome to the World of Pokémon!");
        System.out.println("Pokémon Rian Red is a new, incredibly awesome adventure game.");
        System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command)
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;

            case HELP:
                printHelp();
                break;

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;

            case LOOK:
                look();
                break;

            case FIGHT:
                fight(command);
                break;

            case RUN:
                run();
                break;

            case BAG:
                bag();
                break;

            case GRAB:
                grab(command);
                break;

            case USE:
                use(command);
                break;

            case STATS:
                stats(command);
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the
     * command words.
     */
    private void printHelp()
    {
        System.out.println("You have awoken to embark on your Pokémon journey!");
        System.out.println("You are located around Pallet Town.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /**
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
            if(currentRoom.getShortDescription().equals("in the Professor's laboratory")){
                count++;
                if(count==1){
                    System.out.println("Hello, I'm Professor Oak, welcome to my Laboratory, "+"\n"+ "you have a choice between three of the wonderful starter Pokémon!"+"\n"+"Bulbasaur"+"\n"+"Charmander"+"\n"+"Squirtle");
                }
                else{
                    System.out.println("Welcome to the Professor's Laboratory, you have already recieved the chance to obtain a Pokémon, go to the wilderness to begin your jorney!");
                }
            }
            else if(currentRoom.getShortDescription().equals("now within the wilderness")){
                System.out.println("You are located within a patch of grass, you now have the ability to encounter wild Pokémon!");
            }
        }
    }

    /**
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command)
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }

    private void look(){
        System.out.println("You took a glance around the surronding area...");
        System.out.println(currentRoom.getLongDescription());
    }

    private void fight(Command command){
        if(!command.hasSecondWord() && currentRoom == wilderness && starter!=""){
            int randNum1 =(int)(Math.random()*100);
            if(randNum1>80){
                System.out.println("Your "+starter+" took damage!");
                p1.damage();
            }
            else{
                System.out.println("Your "+starter+" leveled up!");
                p1.xpUp();
                p1.healthUp();
                if(p1.getLevel()==16){
                    evoCount++;
                }
                else if(p1.getLevel()==36){
                    evoCount++;
                }
                if(evoCount!=0 && p1.getLevel()<37){
                    if(pid==1){
                        if(evoCount==1){
                            System.out.println("Congratulations your "+starter+" has evolved into an Ivysaur!");
                            starter="Ivysaur";
                        }
                        if(evoCount==2){
                            System.out.println("Congratulations your "+starter+" has evolved into a Venusaur!");
                            starter="Venusaur";
                        }
                    }
                    else if(pid==2){
                        if(evoCount==1){
                            System.out.println("Congratulations your "+starter+" has evolved into a Charmeleon!");
                            starter="Charmeleon";
                        }
                        if(evoCount==2){
                            System.out.println("Congratulations your "+starter+" has evolved into a Charizard!");
                            starter="Charizard";
                        }
                    }
                    else if(pid==3){
                        if(evoCount==1){
                            System.out.println("Congratulations your "+starter+" has evolved into a Wartortle!");
                            starter="Wartortle";
                        }
                        if(evoCount==2){
                            System.out.println("Congratulations your "+starter+" has evolved into a Blastoise");
                            starter="Blastoise";
                        }
                    }
                }
            }
        }
        else{
            System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
            return;
        }
    }

    private void run(){
        if(currentRoom==wilderness){
            currentRoom=palletTown;
            System.out.println("You have fled from your encounter");
            System.out.println(currentRoom.getLongDescription());
        }
        else{
            System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
        }
    }

    private void bag(){
        System.out.println(p1.returnInventory());
    }

    private void grab(Command command){
        if(!command.hasSecondWord()){
            System.out.println("grab what?");
            return;
        }
        else if(currentRoom.getItem(command.getSecondWord())==null){
            System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
            return;
        }
        else{
            if(command.getSecondWord().equals("Bulbasaur")){
                        p1.setItem(command.getSecondWord(), currentRoom.removeItem(command.getSecondWord()));
                        System.out.println("You placed the " + command.getSecondWord() + " in your bag");
                        currentRoom.removeItem("Charmander");
                        currentRoom.removeItem("Squirtle");
                        starter="Bulbasaur";
                        pokeCount++;
                        pid=1;
                    }
                    else if(command.getSecondWord().equals("Charmander")){
                        p1.setItem(command.getSecondWord(), currentRoom.removeItem(command.getSecondWord()));
                        System.out.println("You placed the " + command.getSecondWord() + " in your bag");
                        currentRoom.removeItem("Bulbasaur");
                        currentRoom.removeItem("Squirtle");
                        starter="Charmander";
                        pokeCount++;
                        pid=2;
                    }
                    else if(command.getSecondWord().equals("Squirtle")){
                        p1.setItem(command.getSecondWord(), currentRoom.removeItem(command.getSecondWord()));
                        System.out.println("You placed the " + command.getSecondWord() + " in your bag");
                        currentRoom.removeItem("Bulbasaur");
                        currentRoom.removeItem("Charmander");
                        starter="Squirtle";
                        pokeCount++;
                        pid=3;
                    }
                    else{
                        p1.setItem(command.getSecondWord(), currentRoom.removeItem(command.getSecondWord()));
                        System.out.println("You placed the " + command.getSecondWord() + " in your bag");
                    }
        }
    }

    private void use(Command command){
        if(!command.hasSecondWord()){
            System.out.println("use what?");
            return;
        }
        else if(p1.getItem(command.getSecondWord())==false){
            System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
            return;
        }
        else{
            if(command.getSecondWord().equals("Potion")){
                if(starter!=""){
                    p1.healthUp();
                    p1.healthUp();
                    System.out.println("You have healed your "+starter+" by 10 HP!");
                }
                else{
                    System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
                }
            }
            if(command.getSecondWord().equals("Pokeball")){
                if(currentRoom==wilderness){
                    int randNum2 =(int)(Math.random()*100);
                    if(randNum2<80){
                        System.out.println("You threw the Pokeball at the wild Pokémon!");
                        System.out.println("...");
                        System.out.println("The wild Pokémon was successfully caught!");
                        pokeCount++;
                    }
                    else{
                        System.out.println("You threw the Pokeball at the wild Pokémon!");
                        System.out.println("...");
                        System.out.println("Aww shoot! the wild Pokémon escaped the Pokeball!");
                    }
                }
                else{
                    System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
                }
            }
            if(command.getSecondWord().equals("Pokedex")){
                System.out.println("You have captured a total of "+pokeCount+" Pokémon!");
            }
            if(command.getSecondWord().equals("Bulbasaur")){
                System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
            }
            if(command.getSecondWord().equals("Charmander")){
                System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
            }
            if(command.getSecondWord().equals("Squirtle")){
                System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
            }
        }
    }

    private void stats(Command command){
       if(starter!=""){
          System.out.println("Health: "+p1.getHealth()+"\n"+"Level: "+p1.getLevel());
       }
       else{
           System.out.println("Oak's words echoed... There's a time and place for everything, but not now!");
       }
    }
}
