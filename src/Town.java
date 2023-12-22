/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private boolean searched;
    private boolean digged;
    private TreasureHunter treasureHunter;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        treasureHunter = new TreasureHunter();

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        int t =(int) (Math.random() * 4) + 1;
        if (t == 1){
            treasure = "crown";
        }else if (t == 2){
            treasure = "trophy";
        }else if (t == 3){
            treasure = "gem";
        }else{
            treasure = "dust";
        }
        searched = false;
        digged = false;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }

            return true;
        }
        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";

        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        printMessage = "You left the shop";
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (treasureHunter.isEasyMode()) {
            noTroubleChance = 0.10;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (hunter.hasItemInKit("sword")) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + Colors.RESET + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                System.out.println(printMessage);
                hunter.changeGold(-goldDiff);
                if (TreasureHunter.getLose() == true){
                    System.out.println(Colors.RED + "You have a debt of " + Colors.YELLOW + hunter.getGold() + " gold.");
                    System.out.println("You spend the rest of your days paying back the debt.");
                    System.out.println(Colors.WHITE + "You died of old age." + Colors.RESET);
                }
            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .2) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .4) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .6) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .8) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .9){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (treasureHunter.isEasyMode()) {
            return false;
        }
        double rand = Math.random();
        return (rand < 0.5);
    }
    public void hunt(){
        if (searched){
            System.out.println("You already searched this town!");
        }else{
            System.out.println("You found a " + treasure);
            hunter.addTreasure(treasure);
            searched = true;
        }
    }

    public void dig() {
        if (!digged) {
            if (hunter.hasItemInKit("shovel")) {
                int chance = (int) (Math.random() * 2) + 1;
                if (chance == 1) {
                    int rand = (int) (Math.random() * 20) + 1;
                    printMessage = "You dug up " + rand + " gold!";
                    hunter.changeGold(rand);
                } else {
                    printMessage = "You dug but only found dirt";
                }
                digged = true;
                if (checkItemBreak()) {
                    hunter.removeItemFromKit("shovel");
                    printMessage += "\nUnfortunately, you lost your shovel";
                }
            } else {
                printMessage = "You can't dig for gold without a shovel";
            }
        } else {
            printMessage = "You already dug for gold in this town.";
        }
    }
}