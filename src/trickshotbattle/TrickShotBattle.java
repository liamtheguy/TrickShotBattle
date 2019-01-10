package trickshotbattle;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.StringTokenizer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class TrickShotBattle extends JPanel implements Runnable, ActionListener {

    public static boolean highscoreSet = false;
    public static boolean firstHighScore = false;
    public static int p1Score;
    public static int p2Score;
    public static String[] highscoreNames = new String[6];
    public static int[] highscore = new int[6];
    public static boolean timerRunning = false; //Timer for the re-drawing and updates for game (True = Running)
    public static ActionListener al; //ActionListener for paintComponent updates
    public static boolean buttonUpdate = true; //If the buttons need to be drawn, etc.
    public static boolean endGame; //If the game is ending (Shows the end screen for the game)
    public static boolean quitScreen = false; //If the end Screen needs to be shown
    public static boolean muteSound = false; //true = mute, false = not muted.
    public static boolean repaintVar = false; //False == repaint needed, True == repaint done. Used when game is over.
    public static JButton muteButton = new JButton("Mute"); //Mute button
    public static JButton menuButton = new JButton("Main Menu"); //Menu button (Once game is lost/won)
    public static JButton quitButton = new JButton("Quit Game"); //Quit button to close the game
    public static String shootDir = "Right"; //Direction the sling will travel.
    public static String lastKey = "Right"; //The last key the player hit on the keyboard (Left/Right)
    public static int timeElapsed = 0; //Time sling has been in the air.
    public static boolean slingShot = true; //If a sling has been fired by player 1.
    public static boolean p1Win = false; //Player 1 has won, otherwise Player 2 won
    public static JTextField nameField = new JTextField(""); //Field for network name
    public static String p1Name; //Player 1 name
    public static String p2Name; //Player 2 name
    public static Font font1 = new Font("Score", Font.BOLD, 26); //Font for all text
    public static Timer gameTimer; //The timer for paintComponent
    public static JButton startButton = new JButton("Start Game");
    public static JRadioButton serverButton = new JRadioButton("Host"); //Radio button selection for Host
    public static JRadioButton clientButton = new JRadioButton("Client"); //Radio button selection for Client
    public static JTextField ipField = new JTextField(10); //Variable for textbox for ip address input.
    public static JFrame f = new JFrame("Java Game"); //Opens JFrame for game playing
    public static String ipServer = "localhost"; //IP address of the server to connect to.
    public static boolean gameLost = false; //If the game has been lost and needs to be reset.
    public static boolean networkHOST = false; //If this compuer is pulling host for the Game!
    public static int p1Health = 100; //Player 1 health
    public static int p2Health = 100; //Player 2 health
    public static AudioInputStream menuMusicStream; //Menu sound
    public static AudioInputStream shootSoundStream; //Shoot sound
    public static Clip clipMenu; //Menu/Game music
    public static Clip shootSound; //Shoot sound
    public static boolean soundPlay = false; //If menusound is currently playing.
    public static String menuMusic = ("res/menu.ogg");
    public static int[] p1 = new int[2]; //Player 1 X/Y coordinates
    public static boolean menu = true; //Sets the mainmenu to show.
    public static boolean gameRun = false; //Whether a game is being played or not.
    public static int[] p2 = new int[2]; //Player 2 X/Y coordinates
    public static int[] arrowP1 = new int[2]; //Player 1's Arrow coordinates
    public static int[] arrowP2 = new int[2]; //Player 2's Arrow coordinates
    public static int[] slingP1 = new int[2]; //Player 1's Arrow coordinates
    public static int[] slingP2 = new int[2]; //Player 2's Arrow coordinates
    int keyCode[] = new int[10]; //Stores keys which have been pressed.
    boolean init; //Whether or not the arrow/sling positioning has been setup for the first run.
    boolean arrowShot; //If the player is able to fire another arrow (prevents spamming of arrows).
    int exp = 0; //  private Image dbImage;
    //private Graphics dbg;
    Image netBackground;
    Image quitBackground; //End screen for the game.
    Image player1Sprite; //Player 1 Sprite.
    Image menuBackground; //Mainmenu background.
    Image player2Sprite; //Player 2 Sprite.
    Image background; //Background of the game.
    Image slingSprite; //Sling sprite.
    Image arrowSprite; //Arrow Sprite.
    Image controlsSprite;
    Image controlsInfo;

    public void resetGame() { //Method to reset the game for another round.
        menuButton.hide();
        quitButton.setBounds(1150, 600, 80, 50);
        /*Resets Networking properties*/
        TrickShotBattle.clientButton.setSelected(true);
        TrickShotBattle.networkHOST = false; //Sets network mode to client
        ipField.setEditable(true);
        ipField.setText("Server IP");
        /*--------------*/
        TrickShotBattle.buttonUpdate = true;
        TrickShotBattle.gameRun = false;
        TrickShotBattle.ipServer = "localhost";
        gameLost = false;
        arrowP1[1] = -45; //So the arrow's physics is not activated
        //Sets coordinates for initial Slings/Arrows of both Player 1 and 2
        TrickShotBattle.slingP1[0] = 0;
        TrickShotBattle.slingP1[1] = -45;
        TrickShotBattle.slingP2[0] = 0;
        TrickShotBattle.slingP2[1] = -45;
        TrickShotBattle.p1Health = 100;
        TrickShotBattle.p2Health = 100;
        TrickShotBattle.p1[0] = 610; //X coordinate for Player 1 start.
        TrickShotBattle.p1[1] = 600; //Y coordinate for Player 1 start.
        TrickShotBattle.p2[0] = 0; //X coordinate for Player 2 start.
        TrickShotBattle.p2[1] = 0; //Y coordinate for Player 2 start.
    }

    @Override
    public void actionPerformed(ActionEvent e) { //Action listener for Radio/Button Buttons.
        if (e.getActionCommand().equals("menu")) { //Exists game, and goes to mainmenu
           if (p1Score > p2Score) {
            TrickShotBattle.highscore[5] = TrickShotBattle.p1Score;
            TrickShotBattle.highscoreNames[5] = TrickShotBattle.p1Name;
           }
           else if (p2Score > p1Score) {
               TrickShotBattle.highscore[5] = TrickShotBattle.p2Score;
               TrickShotBattle.highscoreNames[5] = TrickShotBattle.p2Name;
           }
            int tempscore;
            String tempname;
            for (int i=0; i <highscore.length-1; i++) {
                for (int j=1; j < highscore.length-i; j++) {
                    if(TrickShotBattle.highscore[j-1] < TrickShotBattle.highscore[j]) {
                        tempscore=TrickShotBattle.highscore[j-1];
                        tempname = TrickShotBattle.highscoreNames[j-1];
                        TrickShotBattle.highscore[j-1] = TrickShotBattle.highscore[j];
                        TrickShotBattle.highscoreNames[j-1] = TrickShotBattle.highscoreNames[j];
                        TrickShotBattle.highscore[j] = tempscore;
                        TrickShotBattle.highscoreNames[j] = tempname;
                       
                    }
                }
            }
            resetGame(); //Resets game to be ready for another round
            
            if (TrickShotBattle.muteSound == true) { //Stops music if is not set to be muted (So music doesn't run twice at one time)
                TrickShotBattle.clipMenu.stop();
            }
            //Resets Player Health
            TrickShotBattle.p1Health = 100;
            TrickShotBattle.p2Health = 100;
            TrickShotBattle.timerRunning = true;
            TrickShotBattle.menu = true; //Menu screen state
            TrickShotBattle.buttonUpdate = true; //Set to true so buttons are re-painted in the menu

        }
        if (menu == true) { //Only occurs in the menu
            if (e.getActionCommand().equals("client")) { //Sets Network state to Client
                TrickShotBattle.networkHOST = false; //Sets network mode to client
                ipField.setEditable(true);
                ipField.setText("Server IP");
            }
            if (e.getActionCommand().equals("server")) { //Sets Network state to Host
                TrickShotBattle.networkHOST = true; //Sets network mode to server
                ipField.setEditable(false);
                try {
                    ipField.setText("Server Address: " + InetAddress.getLocalHost().toString()); //Sets IP Address to use for server
                } catch (UnknownHostException ex) {
                    Logger.getLogger(TrickShotBattle.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (e.getActionCommand().equals("start")) { //Starts game
                try {
                    if (TrickShotBattle.timerRunning == true) {
                        //Resets the timer for the game so it doesn't start another timer
                        TrickShotBattle.gameTimer.stop();
                        TrickShotBattle.gameTimer.removeActionListener(al);
                        TrickShotBattle.gameTimer = new Timer(10, al);
                        TrickShotBattle.gameTimer.start();
                    }
                    TrickShotBattle.repaintVar = false; //If this isn't set back/forth the repaint doesn't work properly :)
                    TrickShotBattle.repaintVar = true;
                    TrickShotBattle.p1Name = nameField.getText(); //Sets player 1 name
                    //Resets Player stats and hides all buttons for in-game playing
                    gameLost = false;
                    p1Health = 100;
                    p2Health = 100;
                    ipServer = ipField.getText();
                    TrickShotBattle.menu = false;
                    menuButton.hide();
                    muteButton.hide();
                    quitButton.hide();
                    clientButton.hide();
                    serverButton.hide();
                    startButton.hide();
                    ipField.hide();
                    nameField.hide();
                    (new Thread(new TrickShotBattle())).start();
                } catch (Exception ex) {
                    Logger.getLogger(TrickShotBattle.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (e.getActionCommand().equals("mute")) { //Mutes sound
                if (TrickShotBattle.muteSound == false) { //Mutes game sounds
                    TrickShotBattle.muteSound = true;
                    TrickShotBattle.clipMenu.stop(); //Stops game music
                } else if (TrickShotBattle.muteSound == true) { //UNmutes game sounds
                    TrickShotBattle.muteSound = false;
                    TrickShotBattle.clipMenu.loop(100); //Starts game music
                }
            }
            if (e.getActionCommand().equals("quit")) { //Quits game
                for (int i = 0; i < 5; i++) {
                    System.out.println(TrickShotBattle.highscore[i]);
                }
                TrickShotBattle.gameRun = false; //Stops loops for drawing updates
                TrickShotBattle.quitScreen = true; //Shows end-screen
                TrickShotBattle.menu = false;
                TrickShotBattle.muteSound = true;
                TrickShotBattle.clipMenu.stop(); //Mutes all sounds for quit
            }
        }
    }

    public class AL extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == e.VK_LEFT) {
                keyCode[1] = 1; //Left key set to true
                TrickShotBattle.lastKey = "Left"; //Key for when Arrow fire direction
            }
            if (e.getKeyCode() == e.VK_RIGHT) {
                keyCode[2] = 1; //Right key set to true
                TrickShotBattle.lastKey = "Right"; //Key for when Arrow fire direction
            }
            if (e.getKeyCode() == e.VK_SPACE) {
                keyCode[6] = 1; //Just so the other key doesn't think it got released.
                keyCode[3] = 1; //Space Key set to True
                keyCode[6] = 0;
            }
            if (e.getKeyCode() == e.VK_CONTROL) {
                keyCode[5] = 1; //Just so the other key doesn't think it got released.
                keyCode[4] = 1; //Control key set to true
                keyCode[5] = 0;
            }

        }

        public void keyReleased(KeyEvent e) {
            for (int i = 0; i < 2; i++) {
                if (e.getKeyCode() == 39 && keyCode[6] != 1 && keyCode[5] != 1) { //Ensures key has been released 
                    keyCode[2] = 0; //Right key set to false
                    keyCode[6] = 0;
                }
                if (e.getKeyCode() == 37 && keyCode[6] != 1 && keyCode[5] != 1) { //Ensures key has been released 
                    keyCode[1] = 0; //Left key set to false
                    keyCode[6] = 0;
                }
                if (e.getKeyCode() == 32 && keyCode[5] != 1) { //Ensures key has been released 
                    keyCode[6] = 0; //Space key set to false
                    keyCode[3] = 0;
                }
                if (e.getKeyCode() == 17 && keyCode[6] != 1) { //Ensures key has been released 
                    keyCode[5] = 0;
                    keyCode[4] = 0; //Control key set to false
                }
            }
        }
    }

    public void playSound() { //Initializes music for game-play
        if (menu == true && soundPlay == false) { //Ensures music should be started
            soundPlay = true;
            try {
                if (muteSound == false) { //Ensures music is not set to mute
                    clipMenu.loop(100); //Starts music
                }
            } catch (Exception ex) {
                //Error Playing Audio
                System.out.println("***ERROR PLAYING AUDIO***");
            }

        }
    }

    public TrickShotBattle() throws Exception { //Initial variables and game properties
        for (int i = 0; i < 5; i++) {
            TrickShotBattle.highscore[i] = 0;
            TrickShotBattle.highscoreNames[i] = "Player";
        }
        //Loads sounds
        AudioInputStream menuMusicStream = AudioSystem.getAudioInputStream(new File("res/menu.wav"));
        AudioInputStream shootSoundStream = AudioSystem.getAudioInputStream(new File("res/shoot.wav"));
        clipMenu = AudioSystem.getClip(); //Opens menu music
        clipMenu.open(menuMusicStream);
        shootSound = AudioSystem.getClip(); //Opens game shoot sound
        shootSound.open(shootSoundStream);
        // Load images
        netBackground = ImageIO.read(new File("res/netbackground.PNG"));
        quitBackground = ImageIO.read(new File("res/quitBackground.png"));
        player1Sprite = ImageIO.read(new File("res/alien1.png"));
        menuBackground = ImageIO.read(new File("res/bck0.PNG"));
        controlsSprite = ImageIO.read(new File("res/controls.PNG"));
        player2Sprite = ImageIO.read(new File("res/alien2.png"));
        arrowSprite = ImageIO.read(new File("res/bullet.png"));
        background = ImageIO.read(new File("res/bck2.PNG"));
        controlsInfo = ImageIO.read(new File("res/controlsinfo.PNG"));
        playSound(); //Starts game music!
        setFocusable(true);

        // Game properties
        addKeyListener(new AL()); //Adds ActionListener 
        arrowP1[1] = -45; //So the arrow's physics is not activate
        //Initializes coordinates for player and objects
        TrickShotBattle.slingP1[0] = 0;
        TrickShotBattle.slingP1[1] = -45;
        TrickShotBattle.slingP2[0] = 0;
        TrickShotBattle.slingP2[1] = -45;
        TrickShotBattle.p1[0] = 610; //X coordinate for Player 1 start.
        TrickShotBattle.p1[1] = 600; //Y coordinate for Player 1 start.
        TrickShotBattle.p2[0] = 0; //X coordinate for Player 2 start.
        TrickShotBattle.p2[1] = 0; //Y coordinate for Player 2 start.

        ActionListener al = new ActionListener() { //ActionListener for timer of the game
            public void actionPerformed(ActionEvent ae) {
                physicsUpdate();
                repaint();
            }
        };
        TrickShotBattle.gameTimer = new Timer(10, al);
        TrickShotBattle.gameTimer.start(); //Starts Game Timer for paint/game updates
    }

    public void run() { //Network stuff (The whole reason this project is so cool, also the reason for many sleepless nights)
        String dataRx; //Data received over the network
        String dataTx; //Data sent over the network
        if (networkHOST == true) { //If this game is Hosting the game
            try {
                int cTosPortNumber = 1777; //Port for network
                ServerSocket servSocket = new ServerSocket(cTosPortNumber); //Opens socket
                Socket fromClientSocket = servSocket.accept(); //Connects to other game
                PrintWriter pw = new PrintWriter(fromClientSocket.getOutputStream(), true); //Opens print writer
                BufferedReader br = new BufferedReader(new InputStreamReader(fromClientSocket.getInputStream())); //Opens buffered reader
                while (1 == 1) { //Infinite loop
                    Thread.sleep(1);
                    //Reading data.
                    dataRx = br.readLine();
                    //Output data to screen.
                    StringTokenizer dataProcessed = new StringTokenizer(dataRx); //Receives data
                    TrickShotBattle.p2[0] = Integer.parseInt(dataProcessed.nextToken()); //Sets Player 2 X
                    TrickShotBattle.arrowP2[0] = Integer.parseInt(dataProcessed.nextToken()); //Sets Player 2 Arrow X
                    TrickShotBattle.arrowP2[1] = (Integer.parseInt(dataProcessed.nextToken()) * -1 + 720); //Sets Player 2 Arrow Y
                    TrickShotBattle.slingP2[0] = (Integer.parseInt(dataProcessed.nextToken())); //Sets Player 2 Sling X
                    TrickShotBattle.slingP2[1] = (Integer.parseInt(dataProcessed.nextToken()) * -1 + 720); //Sets Player 2 Sling Y
                    if (Integer.parseInt(dataProcessed.nextToken()) == 0) { //Game is not running
                        TrickShotBattle.menu = false;
                        TrickShotBattle.gameRun = true;
                    } else { //Game is running
                        TrickShotBattle.menu = true;
                        TrickShotBattle.gameRun = false;
                    }
                    TrickShotBattle.p2Health = Integer.parseInt(dataProcessed.nextToken()); //Player 2 health input
                    TrickShotBattle.p2Name = dataProcessed.nextToken(); //Player 2 name
                    /*Debug Output:*/ //System.out.println("Player Pos : " + p2 + " Arrow X : " + arrowP2[0] + " Arrow Y : " + arrowP2[1] + " " + 0);
                    Thread.sleep(1);
                    dataTx = (TrickShotBattle.p1[0] + " " + TrickShotBattle.arrowP1[0] + " " + TrickShotBattle.arrowP1[1] + " " + TrickShotBattle.slingP1[0] + " " + TrickShotBattle.slingP1[1] + " " + 0 + " " + TrickShotBattle.p1Health + " " + TrickShotBattle.p1Name);
                    pw.println(dataTx); //Sends Data back over the network to client
                    if (gameLost == true) { //Disconnects network when game is lost.
                        TrickShotBattle.arrowP2[1] = -100;
                        Thread.sleep(2000);
                        br.close(); //Closes networking ports and stuff
                        pw.close();
                        servSocket.close();
                    }
                }

            } catch (Exception e) {
                //Catching Exception.
            }
        } else if (networkHOST == false) { //If this game is acting as a client
            try {
                Socket socket1;
                int portNumber = 1777; //Network port
                socket1 = new Socket(InetAddress.getByName(ipServer), portNumber); //Opens socket
                BufferedReader br = new BufferedReader(new InputStreamReader(socket1.getInputStream())); //Opens buffered reader
                PrintWriter pw = new PrintWriter(socket1.getOutputStream(), true); //Opens print reader
                //Processing data to be sent via Network.
                dataTx = (TrickShotBattle.p1[0] + " " + TrickShotBattle.arrowP1[0] + " " + TrickShotBattle.arrowP1[1] + " " + TrickShotBattle.slingP1[0] + " " + TrickShotBattle.slingP1[1] + " " + 0 + " " + TrickShotBattle.p1Health + " " + TrickShotBattle.p1Name);
                pw.println(dataTx); //Sends Data over the network
                while (1 == 1) {
                    Thread.sleep(1);
                    dataRx = br.readLine(); //Receives data from network
                    StringTokenizer dataProcessed = new StringTokenizer(dataRx);
                    TrickShotBattle.p2[0] = Integer.parseInt(dataProcessed.nextToken()); //Sets Player 2 X
                    TrickShotBattle.arrowP2[0] = Integer.parseInt(dataProcessed.nextToken()); //Sets Player 2 Arrow X
                    TrickShotBattle.arrowP2[1] = (Integer.parseInt(dataProcessed.nextToken()) * -1 + 720); //Sets Player 2 Arrow Y
                    TrickShotBattle.slingP2[0] = Integer.parseInt(dataProcessed.nextToken()); //Sets Player 2 Sling X
                    TrickShotBattle.slingP2[1] = (Integer.parseInt(dataProcessed.nextToken()) * -1 + 720); //Sets Player 2 Sling Y
                    if (Integer.parseInt(dataProcessed.nextToken()) == 0) { //If the game is running or lost
                        TrickShotBattle.menu = false;
                        TrickShotBattle.gameRun = true;
                    } else { //If the game is stopped
                        TrickShotBattle.menu = true;
                        TrickShotBattle.gameRun = false;
                    }
                    TrickShotBattle.p2Health = Integer.parseInt(dataProcessed.nextToken()); //Sets Player 2 health
                    TrickShotBattle.p2Name = dataProcessed.nextToken(); //Sets player name to network player name.
                    //System.out.println("P2 Health : " + TrickShotBattle.p2Health);
                    //System.out.println("P1 Health: " + TrickShotBattle.p1Health);
                    dataTx = (TrickShotBattle.p1[0] + " " + TrickShotBattle.arrowP1[0] + " " + TrickShotBattle.arrowP1[1] + " " + TrickShotBattle.slingP1[0] + " " + TrickShotBattle.slingP1[1] + " " + 0 + " " + TrickShotBattle.p1Health + " " + TrickShotBattle.p1Name);
                    pw.println(dataTx); //Sends Data back over the network
                    if (gameLost == true) { //Disconnects network when game is lost.
                        Thread.sleep(1000);
                        TrickShotBattle.arrowP2[1] = -100; //Resets Arrow coordinates
                        TrickShotBattle.slingP2[1] = -100;
                        br.close(); //Closes networking stuff
                        pw.close();
                        socket1.close();
                    }
                }
            } catch (Exception e) {
                //Catching Exception.
            }
        }
    }

    public synchronized void physicsUpdate() { //Controls movement animate object other than the player.
        if (arrowP1[1] == -45) { //If the Arrow needs to be reset
            arrowShot = true;
        }
        if (slingP1[1] == -45) { //If the Sling needs to be reset
            slingShot = true;
            timeElapsed = 0;
        }
        if (arrowP1[1] != -45) { //If the Player 1 arrow needs to be reset
            arrowP1[1] = arrowP1[1] - 7;
        }
    }

    public void arrowShoot(int[] playerPos, boolean init) throws Exception { //Shoots the Arrow
        if (init == true) { //If Arrow needs to be initialized
            if (muteSound == false) {
                shootSound.loop(1);
            }
            arrowP1[0] = playerPos[0] + 21; //Sets Arrow X pos
            arrowP1[1] = playerPos[1] - 15; //Sets Arrow Y pos
        }
    }

    public void slingShoot(int[] playerPos, boolean init) { //Shoots the Sling
        if (init = true) { //If Sling needs to be initialized
            slingP1[0] = playerPos[0] + 21; //Sets Arrow X pos
            slingP1[1] = playerPos[1] - 15; //Sets Arrow Y pos
        }
    }

    public synchronized void paintComponent(Graphics g) { //GRAPHICS AND UPDATES FOR MAIN GAME LOOP       
        super.paintComponent(g);

        if (TrickShotBattle.quitScreen == true) { //Quit screen rendering
            g.drawImage(quitBackground, 0, 0, this); //Draws ending screen
            /*Hides buttons for quit screen*/
            muteButton.hide();
            ipField.hide();
            nameField.hide();
            startButton.hide();
            quitButton.hide();
            serverButton.hide();
            clientButton.hide();
            menuButton.hide();
            /*-------------------*/
            if (endGame == true) { //Closing game/Show end screen
                try {
                    Thread.sleep(1500); //Gives time for the end screen to show before closing game
                } catch (InterruptedException ex) {
                    Logger.getLogger(TrickShotBattle.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0); //Closes the game
            }
            TrickShotBattle.endGame = true;
        }
        if (TrickShotBattle.menu == false && TrickShotBattle.gameRun == false && TrickShotBattle.quitScreen == false) { //If client is waiting for another game to connect to it.
            if (TrickShotBattle.networkHOST == true) { //If this game is Hosting
                try {
                    g.drawImage(netBackground, 0, 0, this); //Draws gradiant background
                    g.setFont(font1);
                    g.drawString("Waiting For Client...", 500, 250); //Displays network wait message
                    g.drawString(InetAddress.getLocalHost().toString(), 500, 300); //Displays the server's IP Address 
                } catch (UnknownHostException ex) {
                    Logger.getLogger(TrickShotBattle.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else { //If this game is a client
                g.drawImage(netBackground, 0, 0, this); //Draws gadiant background
                g.setFont(font1);
                g.drawString("Connecting to Server...", 500, 250);
                g.drawString(TrickShotBattle.ipServer, 500, 300); //Displays Address client is attemping to connect to
            }
        }

        if (TrickShotBattle.slingShot == false) { //Sling shot sideways acceleration math
            if (shootDir.equals("Right")) {
                TrickShotBattle.slingP1[0] += Math.pow(1.02, timeElapsed); //Adds Right acceleration to sling being shot
            }
            if (shootDir.equals("Left")) {
                TrickShotBattle.slingP1[0] -= Math.pow(1.02, timeElapsed); //Adds Left acceleration to sling being shot
            }
            TrickShotBattle.slingP1[1] -= 9;
            timeElapsed++; //Adds time for acceleration to be increased
        }

        Rectangle p1Rect = new Rectangle(TrickShotBattle.p1[0], TrickShotBattle.p1[1], 60, 75); //Player 1 hitbox
        Rectangle arrowP2Rect = new Rectangle(TrickShotBattle.arrowP2[0], TrickShotBattle.arrowP2[1], 17, 18); //Player 2 Arrow hitbox
        Rectangle slingP2Rect = new Rectangle(TrickShotBattle.slingP2[0], TrickShotBattle.slingP2[1], 17, 18); //Player 2 Sling hitbox

        if (TrickShotBattle.p1Health <= 0) { //If player 1 is dead
            TrickShotBattle.p1Win = false; //Records who won
            TrickShotBattle.gameLost = true; //Used to display game lost screen
        }
        if (TrickShotBattle.p2Health <= 0) {
            TrickShotBattle.p1Win = true; //Records who won
            TrickShotBattle.gameLost = true; //Used to display game won screen
        }
//Mute button
        muteButton.setBounds(1140, 25, 100, 50);
        muteButton.setActionCommand("mute");

        if (gameLost == true) { //Gameover Screen
            g.fillRect(440, 210, 400, 300);
            if (p1Win == true) { //Player 1 won
                g.setColor(Color.green);
                g.setFont(font1);
                g.drawString("You won!", 520, 400); //Display win message
                TrickShotBattle.p1Score = TrickShotBattle.p1Health;
                TrickShotBattle.p2Score = 0;
                System.out.println("P1 Score : " + p1Score);
                System.out.println("P2 Score : " + p2Score);
                menuButton.setBounds(590, 420, 100, 30);
                menuButton.show(); //Shows mainmenu button

            }
            if (p1Win == false) { //Player 1 lost
                g.setColor(Color.red);
                g.setFont(font1);
                g.drawString("You lost!", 520, 400); //Displays lose message
                TrickShotBattle.p1Score = 0;
                TrickShotBattle.p2Score = TrickShotBattle.p2Health;
                System.out.println("P1 Score : " + p1Score);
                System.out.println("P2 Score : " + p2Score);
                menuButton.setBounds(590, 420, 100, 30);
                menuButton.show(); //Shows mainmenu button
            }
        }

        if (gameRun == true && TrickShotBattle.gameLost == false) { //Draws the In-game stuff
            if (keyCode[1] == 1) { //Left Arrow Key pressed.
                if (p1[0] <= 8) {
                    p1[0] = 8; //Prevents player from moving too far left
                } else {
                    p1[0] += -10; //Moves player left
                }
            }
            if (keyCode[2] == 1) { //Right arrow Key pressed.
                if (p1[0] >= 1000) {
                    p1[0] = 1000; //Prevents player from moving too far right
                } else {
                    p1[0] += +10; //Moves player right
                }
            }
            if (keyCode[3] == 1) { //Spacebar pressed (to fire weapon)
                if (arrowShot == true) {
                    try {
                        arrowShot = false; //Ensures player cannot spam fire arrows.
                        init = true; //Tells arrowShoot method that arrow had not been fired yet.
                        arrowShoot(p1, init); //Fires arrow
                        init = false; //Tells arrowShoot method that arrow is in the air.
                    } catch (Exception ex) {
                        Logger.getLogger(TrickShotBattle.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (keyCode[4] == 1) { //Control pressed (to fire sling)
                if (slingShot == true) {
                    TrickShotBattle.shootDir = TrickShotBattle.lastKey; //Sets the direction the sling will be shot.
                    init = true; //Tells slingShoot sling has not been fired yet
                    slingShoot(p1, init);
                    TrickShotBattle.slingShot = false; //Ensure player cannot spam fire arrows.
                    init = false; //Tells slingShoot sling is currently in the air
                }
            }

            // here, you have a 'drawimage' command for each object you're moving
            // if you have 2 players, you have another drawImage for that one
            // if you have a bullet, you have another for it. You have to keep
            //track of each object's x,y coordinates and then draw the image at that position.
            //you'll need some collision detection in there to see if bullets/players are
            //in the same position and then act accordingly.
            g.drawImage(background, 0, 0, null); //Draw background
            g.drawImage(arrowSprite, arrowP1[0], arrowP1[1], null); //Draws Player 1's arrow
            g.drawImage(arrowSprite, arrowP2[0], arrowP2[1], null); //Draws Player 2's arrow
            g.drawImage(arrowSprite, TrickShotBattle.slingP1[0], TrickShotBattle.slingP1[1], null); //Draws Player 1's sling
            g.drawImage(arrowSprite, TrickShotBattle.slingP2[0], TrickShotBattle.slingP2[1], null); //Draws Player 1's sling
            g.drawImage(player1Sprite, TrickShotBattle.p1[0], p1[1], this); //Draws Player 1
            g.drawImage(player2Sprite, TrickShotBattle.p2[0], p2[1], this); //Draws Player 2
            g.setFont(font1);
            g.fillRect(1100, 0, 180, 720); //Black bar for scores, etc.
            //Draws Health bar for both players.
            if (TrickShotBattle.p2Health >= 60) {
                g.setColor(Color.green); //Green health bar (good health)
            }
            if (TrickShotBattle.p2Health < 60 && TrickShotBattle.p2Health > 20) {
                g.setColor(Color.orange); //Orange health bar (poor health)
            }
            if (TrickShotBattle.p2Health <= 20) {
                g.setColor(Color.red); //Red health bar (critical health)
            }
            g.fillRect(TrickShotBattle.p2[0], TrickShotBattle.p2[1], 10, TrickShotBattle.p2Health);
            g.drawString(Integer.toString(TrickShotBattle.p2Health), 1170, 230); //Puts Player 2 Health on sidebar
            if (TrickShotBattle.p1Health >= 60) {
                g.setColor(Color.green); //Green health bar (good health)
            }
            if (TrickShotBattle.p1Health < 60 && TrickShotBattle.p1Health > 20) {
                g.setColor(Color.orange); //Orange health bar (poor health)
            }
            if (TrickShotBattle.p1Health <= 20) {
                g.setColor(Color.red); //Red health bar (critical health)
            }
            g.drawString(Integer.toString(TrickShotBattle.p1Health), 1170, 590);
            g.fillRect(TrickShotBattle.p1[0], TrickShotBattle.p1[1] + 100, 10, -1 * TrickShotBattle.p1Health); //Puts Player 1 Health on sidebar
            g.setColor(Color.red); //Color for player names
            g.drawString(p1Name, 1160, 540); //Draws Player 1 name
            g.drawString(p2Name, 1160, 180); //Draws Player 2 name
            if (p1Rect.intersects(arrowP2Rect) || p1Rect.intersects(slingP2Rect)) { //If Player 2s arrow hits Player 1
                TrickShotBattle.p1Health -= 3; //Takes away from Player 1 health
            }
        }
        if (menu == true) { //Menu screen
            g.drawImage(menuBackground, 0, 0, this); //Draws menu background
            g.drawImage(controlsSprite, 100, 300, this); //Control title
            g.drawImage(controlsInfo, 110, 370, this); //Controls information
            g.setColor(Color.CYAN);
            g.fillRect(520, 300, 200, 5);
            g.fillRect(617, 300, 6, 200);
            g.setColor(Color.ORANGE);
            g.setFont(font1);
            g.drawString("Highscores", 550, 290);
            for (int i = 0; i <= 4; i++) {
                g.drawString(Integer.toString(TrickShotBattle.highscore[i]), 650, 330 + (i * 40));
                g.drawString(TrickShotBattle.highscoreNames[i], 530, 330 + (i * 40));
            }
        }

        if (menu == true && TrickShotBattle.buttonUpdate == true) { //If in the menu and button Update is required
            TrickShotBattle.buttonUpdate = false; //Prevents buttons from being reset
            ButtonGroup group = new ButtonGroup(); //Adds radio buttons to group
            group.add(serverButton); //Grouping radio buttons
            group.add(clientButton); //^^
            clientButton.setSelected(true); //Sets default selection to client when starting game
            /* Adds Action Listeners for all buttons*/
            quitButton.addActionListener(this);
            menuButton.addActionListener(this);
            clientButton.addActionListener(this);
            serverButton.addActionListener(this);
            startButton.addActionListener(this);
            muteButton.addActionListener(this);
            /*--------------------------*/
            /*Sets Action commands for all buttons*/
            clientButton.setActionCommand("client");
            serverButton.setActionCommand("server");
            quitButton.setActionCommand("quit");
            menuButton.setActionCommand("menu");
            startButton.setActionCommand("start");
            /**
             * *********************************
             * /*Sets positions for all buttons
             */
            nameField.setBounds(1000, 600, 100, 20);
            quitButton.setBounds(1150, 600, 100, 50);
            startButton.setBounds(975, 525, 150, 50);
            serverButton.setBounds(1000, 470, 100, 25);
            clientButton.setBounds(1000, 445, 100, 25);
            ipField.setBounds(900, 500, 300, 20);
            clientButton.show();
            ipField.show();
            quitButton.show();
            nameField.show();
            serverButton.show();
            muteButton.show();
            startButton.show();
            /*-------------------*/
            /*Adds all buttons to the JFrame*/
            f.add(startButton);
            f.add(quitButton);
            f.add(menuButton);
            f.add(ipField);
            f.add(clientButton);
            f.add(serverButton);
            f.add(nameField);
            f.add(muteButton);
            /*-------------------*/
        }
    }

    public static void main(String[] args) throws Exception {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    f.add(ipField, BorderLayout.PAGE_START);
                    f.setSize(1280, 720); //Sets size for game screen
                    f.setResizable(false); //Prevents resizing of game
                    f.setVisible(true); //Makes JFrame visible
                    f.setBackground(Color.BLACK); //Default background
                    f.setContentPane(new TrickShotBattle()); //Creates JFrame for the Game
                    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
