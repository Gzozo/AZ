package AZ;

/**
 * statikus Stringek, amik file neveket, vagy JSON kulcsokat jelölnek
 */
public class Const
{
    static String Resources = "/resources/";
    static String ConfigFile = "src/resources/config.ini";
    static String IconFile = "az.png";
    static String ServerIP = "IP", ServerPort = "Port", ClientPort = "CPort", Selected = "Tank", Keys = "Keys",
            playerName = "Name";
    
    public static class Music
    {
        static String shoot = "shoot.wav", tankDestroy = "explode.wav", obtainPowerUp = "power.wav", he_explode =
                "he_explode.wav", hitWall = "hit_wall.wav", playerJoined = "joined.wav";
        static String RainsOfCastemere = "rainsofcastamere.wav", LightOfTheSeven = "lightoftheseven.wav";
    }
    
    //Kliens-Server JSON
    static String clients = "Points", name = "Name", picture = "Pic", death = "Death", kill = "Kill";
    
    // Server JSON
    static String rejoin = "Rejoin", entities = "Entity", players = "Players", highlight = "Highlight", dead = "Dead"
            , music = "Music", gameState = "State";
    // Config
    static String config = "Config";
    static String labirintusSeed = "Labirintus", gridSize = "GridSize", width = "Width", height = "Height",
            tankWidth = "TWidth", tankHeight = "THeight", panelWidth = "Dimw", panelHeight = "Dimh", Tank = "Tank";
    // Kliens JSON
    static String join = "Join", disconnect = "Disconnect";
    static String keyboard = "Keys", key = "Key", pressed = "Press";
    // Test
    static String test = "Test";
}
