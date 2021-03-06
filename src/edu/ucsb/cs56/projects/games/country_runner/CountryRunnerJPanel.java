package edu.ucsb.cs56.projects.games.country_runner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.*;

import java.awt.image.BufferedImage;


/**
   This class makes the JPanel for the Country Runner game
   @author Mathew Glodack, Christina Morris
   @author Sidney Rhoads, Tom Craig
   @version cs56  W14 proj1
*/


public class CountryRunnerJPanel extends JPanel implements Runnable
{
    Runner runner = new Runner();
    Sheep sheep = new Sheep(20,20);
    Thread jumpThread; //jump thread for the runner
    Thread objectThread; //object thread for objects
    boolean runnerTrue = true; //boolean to decide
    boolean upArrowPressed = false; //keypressed boolean
    boolean crash;
    public Graphics2D g2;
    public static int sheepX = 630;
    public static final boolean debug = true;

    /**
    Constructor adds the keyListener
     */
    public CountryRunnerJPanel()
    {
    	/* Give CountryRunnerJPanel the focus of the application; it will be the field that receives the keyboard input.*/
		setFocusable(true);
		requestFocusInWindow();

		ObstacleThread obstacleThread = new ObstacleThread();

		/*
		keyPressed - when the key goes down
		keyReleased - when the key comes up
		keyTyped - when the unicode character represented
		by this key is sent by the keyboard to system input.
		*/
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
			    pressed(e, "keyPressed");
			}
			@Override
			public void keyReleased(KeyEvent e)
			{
			    //keyEvt(e, "keyReleased");
			}
			@Override
			public void keyTyped(KeyEvent e)
			{
				//keyEvt(e, "keyTyped");
			}

	    });
    }

	/**
	  *Handles all key pressed events
     */
	private void pressed(KeyEvent e, String text)
	{
	    int key = e.getKeyCode();

	    //VK_UP = Up arrow
	    if (key == KeyEvent.VK_UP)
		{
		    upArrowPressed = true;

		    //Makes a new thread so runner can jump
		    if (runner.isOnGround())
		    {
				makeThread();
			}
		}
	}

	/**This method makes a new thread for the
	 * jumper and the moving object
     */
    public void makeThread()
    {
		jumpThread = new Thread(this);
		jumpThread.start();
    }

    /**The run method is required by the Runnable interface
     *
     */
    public void run()
    {
		if (upArrowPressed)
		{
		    jump(-1);
		    jump(1);
			if (runner.isOnGround())
			{
				runOnGround();
			}
		}
		/*while ( runner.isOnGround() ){
		    if(runnerTrue && !upArrowPressed){
			runnerTrue = false;
			this.repaint();
			try{
			    Thread.sleep(500); //changed this to main thread
			}catch (InterruptedException ex){
			}
		    }
		    else if (!runnerTrue && !upArrowPressed){
			runnerTrue = true;
			this.repaint();
			try{
			    Thread.sleep(500); //changed to main thread
			}catch (InterruptedException ex){
			}
		    }
		}*/
    }


	/**
     *@param j the distance the runner jumps;
     * negative moves up, positive moves down.
     * Uses the jumpThread
     */
    public synchronized void jump(int j)
    {
		for(int i = 0; i<100; i++)
		{
		    runner.translateY(j);
		    this.repaint();

			//Sleeping this thread *must* be in a try block
		    try
		    {
				jumpThread.sleep(10);
		    } catch (InterruptedException ex){}
		}
    }














    /**The Paint Component method is required for any graphics on a
       JPanel. It draws the Runner and obstacles.
    */

    public void paintComponent(Graphics g)
    {
		g2 = (Graphics2D) g;
		Image image = new ImageIcon("res/background.jpg").getImage();
		Image heaven = new ImageIcon("res/heaven.jpg").getImage();
		g.drawImage(image, 0, 0, this);
		
		 
		ArrayList<BufferedImage> sequence = runner.getCurrentSpriteSequence();

		if(runner.isOnGround() && runnerTrue)
		{
			//g2.drawImage(runner.getRunning1(), (int)runner.getX(), (int)runner.getY(), null);
			g2.drawImage(sequence.get(0), (int)runner.getX(), (int)runner.getY(), null);
		}
		else
		{
		//g2.drawImage(runner.getRunning0(), (int)runner.getX(), (int)runner.getY(), null);
			g2.drawImage(sequence.get(1), (int)runner.getX(), (int)runner.getY(), null);
		}

		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.white);
		g2.draw(sheep);

		if ( crash )
		{
		    g.drawImage(heaven, 0, 0, this);
		}
    }


    /**This moves the Obstacles
     * Uses the objectThread thread
     */
    public class ObstacleThread implements Runnable
    {
		Thread objectThread;

		public ObstacleThread () {
	    objectThread = new Thread(this);
	    objectThread.start();
	}

		/**A run method for the Obstacles Thread
		 */
		public void run(){
	    while( true )
		{
		    if(crash(sheep, runner)) {
			crash = true;
			System.out.println("CRASH!!!!!");
		    }
		    if(sheep.getX()==sheepX)
			sheep.move(-sheepX);
		    sheep.move(10);
		    paintObstacles();
		    try{
			jumpThread.sleep(100);
		    }catch(Exception e){
		    }
		}
	}

		}
    /**Repaints the Obstacles
     */
    public void paintObstacles(){
	this.repaint();
    }

    /**Determines if the runner hits the sheep object
     *@param c sheep object
     *@param r runner object
     *@return boolean true if there is a crash, false if not
     */
    public boolean crash(Sheep c, Runner r){
	if ( c.getY() == r.getY() )
	    return c.getX()==r.getX();
	return false;
    }

    /**Switches the runners position on while on the ground
     * Uses the main Thread
     */
    public void runOnGround()
    {
		while (runner.isOnGround()) 
		{
		    if(runnerTrue)
		    {
				runnerTrue = false;
				this.repaint();
				try
				{
				    Thread.sleep(250);
				}
				catch(Exception e){}
			}
	    
		    if (!runnerTrue)
		    {
				runnerTrue = true;
				this.repaint();
				try
				{
				    Thread.sleep(250);
				}
				catch(Exception e){}
			}
		}//while loop
    }//runOnGround
}//JPanel

