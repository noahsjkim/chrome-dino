package dinoGame;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
//NOAH KIM
//ICS3U1 - FINAL
//Tuesday, January 25th, 2022
//Remake of google chrome's dinosaur game. 

@SuppressWarnings("serial")
public class DinoGameV7 extends JPanel implements Runnable, KeyListener, MouseListener {
	static DinoGameV7 gamePanel = new DinoGameV7 ();
	static AboutMenu menuPanel = new AboutMenu();
	static JFrame menuFrame = new JFrame ("ABOUT");
	Thread thread;
	
	//G A M E
	long startTime;//Keeps track of the time in milliseconds that the game started!
	long timeElapsed;
	int frameCount = 0;//Counts the number of frames that have passed since the start of the program (used to increase speed)
	int hi = 0;//Saves high score!
	int changeWhen;//At which frames the background should change from night to day. 
	int nightCycles = 0;//Counts the amount of times it has gone from day to night. 
	Clip jumpNoise, deathNoise, reachNoise;//Audio clips
	Boolean gameLost = false;//Tells the program if the game has been lost or not. 
	Image playAgainButton = Toolkit.getDefaultToolkit().getImage("images/restart.png");
	
	//S C R E E N
	int FPS = 60;
	int screenWidth = 1000;
	int screenHeight = 260;
	
	//O B S T A C L E S
    //3000, 3540, 4080
	Rectangle[] cactiBox = new Rectangle[3];//This array stores all of the cacti hitboxes. 
	int[] cactiType = new int[3];//This array stores what type of cacti the obstacles are.
	int afterThis = 0;
	int safeSpace = 300;
	Image cactus1 = Toolkit.getDefaultToolkit().getImage("images/cactusSmall.png");
	Image cactus2 = Toolkit.getDefaultToolkit().getImage("images/cactusBig.png");
	Image cactus3 = Toolkit.getDefaultToolkit().getImage("images/cactusDouble.png");
	Image cactus4 = Toolkit.getDefaultToolkit().getImage("images/cactusGang.png");
	Image cactus5 = Toolkit.getDefaultToolkit().getImage("images/cactusHorde.png");
	
	//B A C K G R O U N D 	
	Image bg1, bg2, cloud, aboutCloud, moon;
	int bg1X = 0;//X-value of start of first background image. 
	int bg2X = 2400;//X-value of start of second background image. 
	static int[] cloudX = {1300,1998,2787,3412};//This array stores the initial x-values of the clouds. 
	static int[] cloudY = {50,45,52,31};//This array stores the initial y-values of the clouds. 
	int moonY = 800; //Y-value of moon.
	static boolean nightMode = false;//Tells the program if the game's background is currently night or not. 

	 
	//D I N O (40x43)
	Image dino1 = Toolkit.getDefaultToolkit().getImage("images/trex1.png");
	Image dino2 = Toolkit.getDefaultToolkit().getImage("images/trex2.png");
	Image dino3 = Toolkit.getDefaultToolkit().getImage("images/trex3.png");
	Image dino4 = Toolkit.getDefaultToolkit().getImage("images/trex4.png");
	Image dinoHurt = Toolkit.getDefaultToolkit().getImage("images/dinoHurt.png");
	int dinoY = 0;//Y-value of dino. 
	int dinoType = 1;//The program animates a running dino by scrolling through 3 types of dino "types".
	Rectangle dinoboxOne = new Rectangle(35, 0, 83, 91);//Entire hitbox of dino. 	
	Rectangle dinoboxHead = new Rectangle(73, 91, 41, 28);//Head hitbox of dino. 
	Rectangle dinoboxTorso = new Rectangle(39, 63, 61, 36);//Torso hitbox of dino. 
	Rectangle dinoboxFeet = new Rectangle(54, 29, 30, 24);//Legs & feet hitbox of dino. 
	boolean jump;//Has the dino jumped or not? 
	double speed = 9;
	double jumpSpeed = 18.5;
	double gravity = 1.3;
	double yVel = 0;
	boolean airborne = true;//Is the dino in the air or not?
	
	//Description: Constructor! Sets up game's JPanel, sets up thread, and loads in audio. Setting up the thread starts the game!
	//Return Type: None
	//Parameters: None
	public DinoGameV7() {
		//Sets up JPanel:
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);
		addMouseListener(this);
		jump = false;
		//Sets up thread:
		thread = new Thread(this);
		thread.start();//starts new process - calls run()
		
	    //AUDIO:
		try {//Loads in all the audio files!
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("audio/jump.wav"));
			jumpNoise = AudioSystem.getClip();
			jumpNoise.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("audio/death.wav"));
			deathNoise = AudioSystem.getClip();
			deathNoise.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("audio/reach.wav"));
			reachNoise = AudioSystem.getClip();
			reachNoise.open(sound);
		} 
		catch (Exception e) {
		}
	}
	
	//Description: This game is all about graphics! Before you start playing, some images/other details must be loaded in to make sure that the game is ready for you to play. 
	//Return Type: Void
	//Parameters: None
	public void initialize() {
		
		
		bg1 = Toolkit.getDefaultToolkit().getImage("images/ground.png");
		bg2 = Toolkit.getDefaultToolkit().getImage("images/ground.png");
		cloud = Toolkit.getDefaultToolkit().getImage("images/cloud.png");
		aboutCloud = Toolkit.getDefaultToolkit().getImage("images/aboutCloud.png");
		moon = Toolkit.getDefaultToolkit().getImage("images/moon.png");
		
		//For each obstacle, this loop will determine what type of cactus it will be!
		for (int n = 0; n < cactiBox.length; n++) {
			//3000, 3540, 4080
			int rand = (int)(Math.random()*(100))+1;
			if (rand <= 25){//Small cactiBox
				cactiBox[n] = new Rectangle(3000 + 540*n, 193, 32, 67);
				cactiType[n] = 1;
			} else if (rand <= 50) {//Big cactiBox
				cactiBox[n] = new Rectangle(3000 + 540*n, 168, 44, 92);
				cactiType[n] = 2;
			} else if (rand <= 75) {//Double cactiBox
				cactiBox[n] = new Rectangle(3000 + 540*n, 193, 55, 67);
				cactiType[n] = 3;
			} else if (rand <= 94) {//Medium group cactiBox
				cactiBox[n] = new Rectangle(3000 + 540*n, 193, 83, 67);
				cactiType[n]=4;
			} else {//Large group cactiBox
				//Although the image makes it appear as a large cacti group, I have decided to make the hitbox shorter than the
				//image! Because the cactus' edge is a slope, if the hitbox height is set to normal, it is too tricky to jump over. 
				cactiBox[n] = new Rectangle(3000 + 540*n, 190, 120, 70);
				cactiType[n]=5;
			}
		}
		
	}
	
	//Description: Determines a x position AND randomly determines a random type of obstacle. Called whenever an old obstacle appears off-screen. 
	//Parameters: Integer n (a counter in a for loop that keeps track of which obstacle is which)
	//Return Type: Void
	public void newObstacle (int n) {

		
		//When an obstacle appears off screen, it's position must be greater than the rightmost obstacle on screen.
		//The position of this rightmost obstacle is called afterThis. 
		 if (n-1 < 0) {
			 afterThis = 2;
		 } else {
			 afterThis = (n-1);
		 }
		 
		 //Safe space is the empty space AFTER an obstacle where the dino can safely jump into. 
		 //It increases as the game speeds up to make the game more balanced.
		 //(As you progress, speed gets harder, but the spacing gets easier.)
		 if (speed < 13)
			 safeSpace = 300;
		 else if (speed < 15)
			 safeSpace = 450;
		 else if (speed > 15)
			 safeSpace = 580;
		
		int jumpTo = (int)(Math.random()*(2200 - (cactiBox[afterThis].x+safeSpace)+1))+(cactiBox[afterThis].x+safeSpace);
		//New random obstacle position will be AFTER the last obstacle to the right, let's call that lastSpot.
		//So the new random number must be in between lastSpot + safeSpace TO  2200 (+safeSpace so you have space to jump into) 
		
		
		int rand = (int)(Math.random()*(100))+1;
		//New obstacle cacti type is generated: 
		if (rand <= 25){//Small cactiBox
			cactiType[n] = 1;
			cactiBox[n].x = jumpTo;
			cactiBox[n].y = 193;
			cactiBox[n].width = 32;
			cactiBox[n].height = 67;
		} else if (rand <= 50) {//Big cactiBox
			cactiType[n] = 2;
			cactiBox[n].x = jumpTo;
			cactiBox[n].y = 168;
			cactiBox[n].width = 44;
			cactiBox[n].height = 92;
		} else if (rand <= 75) {//Double cactiBox
			cactiType[n] = 3;
			cactiBox[n].x = jumpTo;
			cactiBox[n].y = 193;
			cactiBox[n].width = 55;
			cactiBox[n].height = 67;
		} else if (rand <= 94) {//Medium group cactiBox
			cactiType[n] = 4;
			cactiBox[n].x = jumpTo;
			cactiBox[n].y = 193;
			cactiBox[n].width = 83;
			cactiBox[n].height = 67;
		} else {//Large group cactiBox
			cactiType[n] = 5;
			cactiBox[n].x = jumpTo;
			cactiBox[n].y = 190;
			cactiBox[n].width = 120;
			cactiBox[n].height = 70;
		}
	}
	
	//Description: Makes it appear as if the obstacles and background are moving towards you! This method is what moves all the images every frame. 
	//Return Type: Void
	//Parameters: None
	public void update() {
		
		//Every frame, the program must check for a collision ASAP! (This determines if the game is lost. There are many opportunities for the game to be lost every second!) 
		for(int i = 0; i < cactiBox.length; i++)
			checkCollision(cactiBox[i]);
		
		//Calculates the timeElapsed since the program was started. 
		timeElapsed = System.currentTimeMillis() - startTime;
		
		//This is what increases the speed as the game progresses, making the game harder and harder. 
		if ((frameCount) % 1000 == 0) {
			speed ++;
			if (speed >= 19) {//speed caps at 19!
				speed = 19;
			}
		}
		
		//This is what moves the background! 
		bg1X -= speed;
		if (bg1X < -2400)
			bg1X = 2380;
		bg2X -= speed;
		if (bg2X < -2400)
			bg2X = 2380;
		
		//Moves the clouds and determines a new position for one of the existing clouds if it goes out of screen. 
		for (int n = 0; n < 4; n++) {
			cloudX[n] -= (int)(Math.random()*(3))+2;
			if (cloudX[n] < -85) {
				cloudX[n] = (int)(Math.random()*(501))+1900;
				cloudY[n] = (int)(Math.random()*(51))+30;
			}
		}
		
		//Moves the cactus hitboxes! Calls newObstacle() if the dino "runs past" the old obstacle. 
		for (int n = 0; n < cactiBox.length; n++) {
			cactiBox[n].x -= speed;
			if (cactiType[n] == 1 && cactiBox[n].x < -32) {
				newObstacle(n);
			} else if (cactiType[n] == 2 && cactiBox[n].x < -44) {
				newObstacle(n);
			} else if (cactiType[n] == 3 && cactiBox[n].x < -55) {
				newObstacle(n);
			} else if (cactiType[n] == 4 && cactiBox[n].x < -83) {
				newObstacle(n);
			} else if (cactiType[n] == 5 && cactiBox[n].x < -130) {
				newObstacle(n);
			}
		}
	}
	
	//Description: Determines if the dino has collided with an obstacle's hitbox. 
	//Return Type: Void
	//Parameters: Obstacle hitboxes
	void checkCollision(Rectangle obstacle) {
		if(dinoboxHead.intersects(obstacle) || dinoboxTorso.intersects(obstacle) || dinoboxFeet.intersects(obstacle)) {
			if (timeElapsed/100 > hi)
				hi = (int) (timeElapsed/100);
			deathNoise.setFramePosition(0);
			deathNoise.start ();
			gameLost = true;
		}
	}
	
	//Description: This is the paint component - used to display images/shapes/text on the game frame's panel. 
	//However, the paint component does more then just draw! On top of drawing, it calculates current score, animates the running dino,
	//and sets the window up for when the game is lost. 
	//Return Type: Void
	//Parameters: Graphics g
	public void paintComponent(Graphics g) {//Called every time - Similar to update()!
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		//Draws background
		g2.drawImage(bg1, bg1X, 230, 2400, 26, this);
		g2.drawImage(bg2, bg2X, 230, 2400, 26, this);
		
		//Draws each cloud
		for (int i = 0; i < 4; i++) {
			g2.drawImage(cloud, cloudX[i], cloudY[i], 85, 25, this);
		}
		
		//Draws moon (at night)
		if (nightMode == true) {
			g2.drawImage(moon, moonY, 60, 30, 50, this);
			moonY--;
		}
		
		if (timeElapsed/100 < 35) {
			g2.setFont(new Font("courier", Font.PLAIN, 15));
			g2.setColor(Color.darkGray);
			g2.drawString("space to jump!", 15, 150);
		}
		
		
		//If the background is dark, you must use white text, and vice versa. 
		if (nightMode == true) {
			g2.setColor(Color.WHITE);
		} else {
			g2.setColor(Color.darkGray);
		}
		//Displays high score and current score:
		g2.setFont(new Font("courier", Font.PLAIN, 25));
		g2.drawString("" + timeElapsed/100, 910, 34);
		g2.setColor(Color.GRAY);
		g2.setFont(new Font("courier", Font.PLAIN, 25));
		g2.drawString("" + hi, 825, 34);
		g2.drawString("HI", 780, 34);
		
		//If you wanted to see the individual hitboxes that make up the dino...
//		g2.setColor(Color.CYAN);
//		g2.fill(dinoboxHead);
//		g2.setColor(Color.PINK);
//		g2.fill(dinoboxTorso);
//		g2.setColor(Color.GREEN);
//		g2.fill(dinoboxFeet);
		
		//Animates the running dino! Determines which image to use at each count.
		if (gameLost == false) {
			if (airborne) //Dino's legs are extended when he is in the air!
				g2.drawImage(dino1, 35, dinoY, 83, 91, this);
			else if (dinoType == 1 || dinoType == 2 || dinoType == 3)
				g2.drawImage(dino1, 35, dinoY, 83, 91, this);
			else if (dinoType == 4 || dinoType == 5 || dinoType == 6)
				g2.drawImage(dino3, 35, dinoY,83, 91, this);
			else if (dinoType == 7 || dinoType == 8 || dinoType == 9)
				g2.drawImage(dino4, 35, dinoY, 83, 91, this);
			
			//Brings the animation counter back to one when it exceeds 9. 
			dinoType++;
			if (dinoType > 9)
				dinoType = 1;
		} else if (gameLost == true) {
			g2.drawImage(dinoHurt, 35, dinoY, 75, 91, this);
		}


		//If you want to see the hitboxes of the obstacles...
//		g2.setColor(Color.CYAN);
//		for(int i = 0; i < cactiBox.length; i++)
//			g2.fill(cactiBox[i]);
		
		//This for loop is what draws the obstacle's images!
		//Type of cactus is determined, and then the image is drawn with corresponding dimensions. 
		for (int i = 0; i < cactiType.length; i++) {
			if (cactiType[i] == 1) {
				g2.drawImage(cactus1, cactiBox[i].x, 192, 32, 68, this);
			} else if (cactiType[i] == 2) {
				g2.drawImage(cactus2, cactiBox[i].x, 167, 44, 93, this);
			} else if (cactiType[i] == 3) {
				g2.drawImage(cactus3, cactiBox[i].x, 192, 55, 68, this);
			} else if (cactiType[i] == 4) {
				g2.drawImage(cactus4, cactiBox[i].x, 192, 83, 68, this);
			} else if (cactiType[i] == 5) {
				g2.drawImage(cactus5, cactiBox[i].x, 175, 130, 85, this);
			}
		}
		
		//When the game is LOST: (paint component is called after the game is lost)
		if (gameLost == true) {
			g2.drawImage(playAgainButton, 473, 110, 54, 48, this);
			if (nightMode == false)
				g2.setColor(Color.darkGray);
			else if (nightMode == true)
				g2.setColor(Color.WHITE);
			g2.setFont(new Font("courier", Font.BOLD, 30));
			g2.drawString("G A M E   O V E R", 345, 80);
			g2.drawImage(aboutCloud, 10, 10, 120, 40, this);
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("courier", Font.PLAIN, 15));
			g2.drawString("About", 52, 39);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	//Description: Sorts out what to do when a key is pressed!
	//Return type: Void
	//Parameters: KeyEvent e 
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_SPACE) {
			jumpNoise.setFramePosition(0);
			jumpNoise.start ();
			jump = true;
		}
	}

	//Description: Sorts out what to do when a key is released!
	//Return type: Void
	//Parameters: KeyEvent e 
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_SPACE) {
			jump = false;
		}
	}
	
	//Description: Sorts out what to do when mouse is clicked!
	//Return type: Void
	//Parameters: MouseEvent e 
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX ();
		int y = e.getY ();
		handleAction(x, y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	//Description: Moves the dino vertically when spacebar is pressed and the dino jumps!
	//Return type: Void
	//Parameters: None
	void move() {
		if(airborne) {
			yVel -= gravity;
		}else {
			if(jump) {
				airborne = true;
				yVel = jumpSpeed;
			}
		}
		//Each piece of hitbox is moved separately...
		dinoboxOne.y -= yVel;
		dinoboxHead.y -= yVel;
		dinoboxTorso.y -= yVel;
		dinoboxFeet.y -= yVel;
		dinoY -= yVel;//... and so is the image on top of the hitboxes. 
	}
	
	void keepInBound() {
		if(dinoboxOne.y < 0) {//(This is technically not necessary - the dino never goes out of bounds. His legs aren't strong enough!
			dinoboxOne.y = 0;
			dinoboxHead.y = 0;
			dinoboxTorso.y = 0;
			dinoboxFeet.y = 0;
			dinoY = 0;
			yVel = 0;
		}else if(dinoboxOne.y > screenHeight - dinoboxOne.height) {
			//All the pieces of hitbox are put in the right place:
			dinoboxOne.y = screenHeight - dinoboxOne.height;
			dinoboxHead.y = screenHeight - dinoboxHead.height - 63;
			dinoboxTorso.y = screenHeight - dinoboxHead.height - 35;
			dinoboxFeet.y = screenHeight - dinoboxHead.height;
			dinoY = screenHeight - dinoboxOne.height;//...and so is the image on top of the hitboxes. 
			airborne = false;
			yVel = 0;
		}
	}
	
	//Description: Runs the program! Calls everything from initialize() to update() to move() to paint component!
	//Return type: Void
	//Parameters: none
	@Override
	public void run() {
		
		//These next three lines are lines that must be ran BEFORE the game is started. 
		initialize();
		startTime = System.currentTimeMillis();
		timeElapsed = 0;
		
		//This while loop is what powers the game. 
		while(gameLost != true) {
			update();
			move();
			keepInBound();
			frameCount++;
			
			//Formula for determining at which frames should the background change to night/day. 
			if (nightMode == false) {
				changeWhen = 2000 + 3500*nightCycles;
			} else if (nightMode == true) {
				changeWhen = 3500;
			}
			
			//Changes the background to night/day. 
			if (frameCount % changeWhen == 0) {
				if (nightMode == false) {
					gamePanel.setBackground(Color.darkGray);
					//System.out.println("Changed to night mode at " + frameCount + " frames!");
					nightMode = true;
					moonY = 800;
					nightCycles++;
				} else if (nightMode == true) {
					gamePanel.setBackground(Color.white);
					//System.out.println("Changed to day mode at " + frameCount + " frames!");
					nightMode = false;
				}
			}
			
			//Every 100 points your each, this noise shall play. 
			if ((timeElapsed/100 % 100) == 0) {
				reachNoise.setFramePosition(0);
				reachNoise.start ();
			}
			
			//Calls paint component. 
			this.repaint();
			try {
				Thread.sleep(1000/FPS);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} 
		this.repaint();//Paint component called - WHEN GAME IS LOST! 
	}
	
	//Description: Works with the x and y values of a mouse click! Makes the images working buttons, and makes the buttons do something.
	//Where the game is restarted! Also where the about menu is opened!
	//Return type: Void 
	//Parameters: x, y (coordinates of mouse click)
	public void handleAction (int x, int y) {
		if (x > 473 && x < 527 && y > 110 && y < 158 && gameLost == true) {//If a mouse click occurs on the play again button. 
			thread = new Thread(this);
			thread.start();//starts new process - calls run()
			gameLost = false;
			gamePanel.setBackground(Color.white);
			nightMode = false;
			speed = 9;
			frameCount = 0;
			nightCycles = 0;
			moonY = 800;
		} else if (x > 10 && x < 130 && y > 10 && y < 50 && gameLost == true) {//If a mouse click occurs on the "about" cloud. 
			setPreferredSize(new Dimension(1000, 1000));
			AboutMenu nc = new AboutMenu();//Calls another class which draws the "about menu"! 
			menuFrame.setVisible(true);
			menuFrame.pack();
			menuFrame.setResizable(false);
			menuFrame.setLocationRelativeTo(null);
		}
	}
	
	//MAIN METHOD: CREATES FRAMES
	public static void main(String[] args) {	
		JFrame gameFrame = new JFrame ("chrome://dino");
		gamePanel.setBackground(Color.WHITE);
	    gameFrame.add(gamePanel);
	    menuFrame.add(menuPanel);
		gameFrame.addKeyListener(gamePanel);
		gameFrame.setVisible(true);
		gameFrame.pack();	
		gameFrame.setResizable(false);
		gameFrame.setLocationRelativeTo(null); //on start window goes to middle of screen
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


}
