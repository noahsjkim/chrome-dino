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

public class AboutMenu extends JPanel{
	Image aboutCloud = Toolkit.getDefaultToolkit().getImage("images/aboutCloud.png");
	
	//Constructor! Sets up JFrame stuff. 
	public AboutMenu() {
		setSize(400,350);
		setPreferredSize(new Dimension(400, 350));
		setLocation(600,300);
		setVisible(true);
		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	//Description: Paint component - Used to draw everything on the about menu.
	//Return type: Void. 
	//Parameters: Graphics g
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(aboutCloud, 10, 24, 120, 40, this);
		g2.drawImage(aboutCloud, 155, 30, 120, 40, this);
		g2.drawImage(aboutCloud, 270, 10, 120, 40, this);
		g2.setFont(new Font("helvetica", Font.PLAIN, 13));
		g2.setColor(Color.BLACK);
		g2.drawString("NOAH KIM", 43, 57);
		g2.setFont(new Font("helvetica", Font.PLAIN, 18));
		g2.drawString("FOR", 200, 60);
		g2.setFont(new Font("helvetica", Font.PLAIN, 15));
		g2.drawString("ICS3U1", 310, 43);
		g2.setFont(new Font("helvetica", Font.PLAIN, 23));
		g2.drawString("INSTRUCTIONS:", 110, 140);
		g2.setFont(new Font("helvetica", Font.PLAIN, 14));
		g2.drawString("-SPACE TO JUMP", 60, 180);
		g2.drawString("-GAME ENDS WHEN YOU HIT A CACTUS", 60, 210);
		g2.drawString("-SO DON'T HIT ANY CACTI", 60, 240);
		g2.drawString("-HEY I THINK I'VE PLAYED THIS BEFORE", 60, 270);
		g2.drawString("-YEAH I LIKE THIS GAME", 60, 300);
		g2.setFont(new Font("helvetica", Font.BOLD, 10));
		g2.drawString("01/25/2022", 335, 340);
		
		
	}
}
