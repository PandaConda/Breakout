import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.text.DecimalFormat;

/**
 * The game's drawing surface
 * @author Based on code by Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
 */
public final class BreakoutPanel extends JPanel implements Runnable, MouseMotionListener, MouseListener {
	
	private final int BORDER = 2;
  /**
   * Parent frame and model
   */
  private BreakoutFrame theFrame;            
  private BreakoutModel theModel;
  /**
   * Size of panel
   */
  private static final int PWIDTH = 800;
  private static final int PHEIGHT = 640; 

  /**
   * Thread that performs the animation  
   */
  private Thread animator;           

  /**
   * used to stop the animation thread
   */
  private boolean running = false;
  private boolean isPaused = false;

  /** 
   * Period between drawing in _nanosecs_
   */
  private long period;                
 
  /** 
   *  Number of frames with a delay of 0 ms before the animation thread yields
   *  to other running threads.
   */
  private static final int NO_DELAYS_PER_YIELD = 16;

  /**
   * no. of frames that can be skipped in any one animation loop
   * i.e the games state is updated but not rendered
   */
  private static int MAX_FRAME_SKIPS = 5;

  /**
   * number of FPS values stored to get an average
   */
  private static int NUM_FPS = 10;
  
  /**
   * Off-screen rendering
   */
  private Graphics dbg; 
  private Image dbImage = null;
    
  /** 
   * Stats 
   */
  private static long MAX_STATS_INTERVAL = 1000000000L;
  
  private long statsInterval = 0L;                                   // in ns
  private long prevStatsTime;   
  private long totalElapsedTime = 0L;
  private long gameStartTime;
  private int timeSpentInGame = 0;                              // in seconds

  private long frameCount = 0;
  private double fpsStore[];
  private long statsCount = 0;
  private double averageFPS = 0.0;

  private long framesSkipped = 0L;
  private long totalFramesSkipped = 0L;
  private double upsStore[];
  private double averageUPS = 0.0;

  private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
  /** 
   * Creates a blank, white panel of the dimensions specified in PWIDTH, PHEIGHT.
   */
  public BreakoutPanel(BreakoutFrame inFrame, long period) {
    theFrame = inFrame ;
    this.period = period;
    
    //disable cursor
	Cursor noCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "nocursor");
	this.setCursor(noCursor);

    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));
    setFocusable(true);
    requestFocus();    // the JPanel now has focus, so receives key events
    readyForTermination();
    
    // initialise timing elements
    fpsStore = new double[NUM_FPS];
    upsStore = new double[NUM_FPS];
    for (int i=0; i < NUM_FPS; i++) {
      fpsStore[i] = 0.0;
      upsStore[i] = 0.0;
    }
    
    theModel = new BreakoutModel(PWIDTH, PHEIGHT);
    theFrame.setBreakoutModel(theModel);
    
    addMouseMotionListener(this);
    addMouseListener(this);
  }

  /**
   * Makes paddle follow mouse
   */
  public void mouseMoved(MouseEvent e) {
	  if (!theModel.verticalModeEnabled()) { //if vertical mode is off only respond to horizontal changes in mouse position
		  if (e.getX() - (theModel.getPaddleWidth() / 2) > 0 && e.getX() + (theModel.getPaddleWidth() / 2) <= PWIDTH) {
			  theModel.movePaddle(e.getX()); //if mouse is in middle of screen follow with paddle
			  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(e.getX() - (theModel.getBallDiameter() / 2)); //follow mouse with ball if speed == 0
		  } else if (e.getX() - (theModel.getPaddleWidth() / 2) <= 0) {
			  theModel.movePaddle(theModel.getPaddleWidth() / 2); //make sure paddle doesn't go off left edge of screen
			  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall((theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2)); //keep ball over center of paddle if speed == 0
		  } else if (e.getX() + (theModel.getPaddleWidth() / 2) > PWIDTH) {
			  theModel.movePaddle(PWIDTH - (theModel.getPaddleWidth() / 2) - 1); //make sure paddle doesn't go off right side of screen
			  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(PWIDTH - (theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2)); //keep ball over center of paddle if speed == 0
		  }
		  
		  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(theModel.getPaddleY() - 75 - (theModel.getBallDiameter() / 2)); //if speed is 0 move ball with paddle
		  
	  } else if (theModel.verticalModeEnabled()) { //if vertical mode is turned on respond to horizontal and vertical changes in mouse position
		  
		  boolean flag = true;
		  boolean flag2 = true;
		  for (int i = 0; i < theModel.getNumRows(); i++) { //for every row
			  for (int j = 0; j < theModel.getNumBricks(); j++) { //for every brick
				  if (theModel.getBrickHP(i, j) != 0) { //if brick is not dead
					  if ((e.getX() + (theModel.getPaddleWidth() / 2) >= theModel.getBrickX(i, j) && e.getX() + (theModel.getPaddleWidth() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j)) || (e.getX() - (theModel.getPaddleWidth() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) && e.getX() - (theModel.getPaddleWidth() / 2) >= theModel.getBrickX(i, j))) { //if horizontal movement is obstructed
						  if ((e.getY() + theModel.getPaddleHeight() >= theModel.getBrickY(i, j) && e.getY() + theModel.getPaddleHeight() <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)) || ((theModel.getBallSpeed(theModel.getBallArrayLength() - 1) != 0 && ((e.getY() <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)) && (e.getY() >= theModel.getBrickY(i, j)))) || theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0 && e.getX() + (theModel.getBallDiameter() / 2) >= theModel.getBrickX(i, j) && e.getX() - (theModel.getBallDiameter() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) && ((e.getY() - theModel.getBallDiameter() <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)) && (e.getY() - theModel.getBallDiameter() >= theModel.getBrickY(i, j))))) { //if vertical movement is obstructed
							  flag = false; //do not move paddle
						  }
						  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0 && (e.getX() + (theModel.getBallDiameter() / 2) >= theModel.getBrickX(i, j) && (e.getX() + (theModel.getBallDiameter() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) || e.getX() - (theModel.getBallDiameter() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) && e.getX() - (theModel.getBallDiameter() / 2) >= theModel.getBrickX(i, j)) && (e.getY() >= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j) && e.getY() - 75 - (theModel.getBallDiameter() / 2) <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)))) { //if paddle can be moved but ball is obstructed
							  theModel.moveBallVertically(theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)); //move ball down
							  flag2 = false;
						  }
					  }
				  }
			  }
		  }
		  if (flag) { //if move was legal run same horizontal checks as if vertical mode was turned off and move paddle and ball accordingly
			  if (e.getX() - (theModel.getPaddleWidth() / 2) > 0 && e.getX() + (theModel.getPaddleWidth() / 2) <= PWIDTH) {
				  theModel.movePaddle(e.getX());
				  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(e.getX() - (theModel.getBallDiameter() / 2));
			  } else if (e.getX() - (theModel.getPaddleWidth() / 2) <= 0) {
				  theModel.movePaddle(theModel.getPaddleWidth() / 2);
				  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall((theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2));
			  } else if (e.getX() + (theModel.getPaddleWidth() / 2) > PWIDTH) {
				  theModel.movePaddle(PWIDTH - (theModel.getPaddleWidth() / 2) - 1);
				  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(PWIDTH - (theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2));
			  }
			  
			  if (e.getY() >= PHEIGHT - 30) { //make sure paddle does not move below lava
				  theModel.movePaddleToBottom();
				  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(theModel.getPaddleY() - 75 - (theModel.getBallDiameter() / 2));
			  } else if (e.getY() - theModel.getBallDiameter() < 0 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) { //if there a ball hovering above the paddle
				  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(0); //make sure ball does not go off screen
				  theModel.movePaddleVertically(theModel.getBallDiameter() + 1); //paddle can't go above ball
			  } else { //if there is no ball hovering above paddle
				  theModel.movePaddleVertically(e.getY()); //paddle can move to top of screen
				  if ((theModel.getPaddleY()) - 75 - (theModel.getBallDiameter() / 2) <= 0) {
					  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(0); //if ball is obstructed by celing make sure ball stays on screen
				  } else {
					  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(theModel.getPaddleY() - 75 - (theModel.getBallDiameter() / 2)); //otherwize determine ball's vertical position relative to paddle
				  }
			  }
		  }
	  }
  }
  
  public void mouseDragged(MouseEvent e) { //exactly the same as mouseMoved
	  if (!theModel.verticalModeEnabled()) {
		  if (e.getX() - (theModel.getPaddleWidth() / 2) > 0 && e.getX() + (theModel.getPaddleWidth() / 2) <= PWIDTH) {
			  theModel.movePaddle(e.getX());
			  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(e.getX() - (theModel.getBallDiameter() / 2));
		  } else if (e.getX() - (theModel.getPaddleWidth() / 2) <= 0) {
			  theModel.movePaddle(theModel.getPaddleWidth() / 2);
			  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall((theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2));
		  } else if (e.getX() + (theModel.getPaddleWidth() / 2) > PWIDTH) {
			  theModel.movePaddle(PWIDTH - (theModel.getPaddleWidth() / 2) - 1);
			  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(PWIDTH - (theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2));
		  }
		  
		  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(theModel.getPaddleY() - 75 - (theModel.getBallDiameter() / 2)); //if speed is 0 move ball with paddle
		  
	  } else if (theModel.verticalModeEnabled()) { //if vertical mode is turned on
		  
		  boolean flag = true;
		  boolean flag2 = true;
		  for (int i = 0; i < theModel.getNumRows(); i++) { //for every row
			  for (int j = 0; j < theModel.getNumBricks(); j++) { //for every brick
				  if (theModel.getBrickHP(i, j) != 0) { //if brick is not dead
					  if ((e.getX() + (theModel.getPaddleWidth() / 2) >= theModel.getBrickX(i, j) && e.getX() + (theModel.getPaddleWidth() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j)) || (e.getX() - (theModel.getPaddleWidth() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) && e.getX() - (theModel.getPaddleWidth() / 2) >= theModel.getBrickX(i, j))) { //if horizontal movement is obstructed
						  if ((e.getY() + theModel.getPaddleHeight() >= theModel.getBrickY(i, j) && e.getY() + theModel.getPaddleHeight() <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)) || ((theModel.getBallSpeed(theModel.getBallArrayLength() - 1) != 0 && ((e.getY() <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)) && (e.getY() >= theModel.getBrickY(i, j)))) || theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0 && e.getX() + (theModel.getBallDiameter() / 2) >= theModel.getBrickX(i, j) && e.getX() - (theModel.getBallDiameter() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) && ((e.getY() - theModel.getBallDiameter() <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)) && (e.getY() - theModel.getBallDiameter() >= theModel.getBrickY(i, j))))) {
							  flag = false;
						  }
						  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0 && (e.getX() + (theModel.getBallDiameter() / 2) >= theModel.getBrickX(i, j) && (e.getX() + (theModel.getBallDiameter() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) || e.getX() - (theModel.getBallDiameter() / 2) <= theModel.getBrickX(i, j) + theModel.getBrickWidth(i, j) && e.getX() - (theModel.getBallDiameter() / 2) >= theModel.getBrickX(i, j)) && (e.getY() >= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j) && e.getY() - 75 - (theModel.getBallDiameter() / 2) <= theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j)))) {
							  theModel.moveBallVertically(theModel.getBrickY(i, j) + theModel.getBrickHeight(i, j));
							  flag2 = false;
						  }
					  }
				  }
			  }
		  }
		  if (flag) {
			  if (e.getX() - (theModel.getPaddleWidth() / 2) > 0 && e.getX() + (theModel.getPaddleWidth() / 2) <= PWIDTH) {
				  theModel.movePaddle(e.getX());
				  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(e.getX() - (theModel.getBallDiameter() / 2));
			  } else if (e.getX() - (theModel.getPaddleWidth() / 2) <= 0) {
				  theModel.movePaddle(theModel.getPaddleWidth() / 2);
				  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall((theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2));
			  } else if (e.getX() + (theModel.getPaddleWidth() / 2) > PWIDTH) {
				  theModel.movePaddle(PWIDTH - (theModel.getPaddleWidth() / 2) - 1);
				  if (theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBall(PWIDTH - (theModel.getPaddleWidth() / 2) - (theModel.getBallDiameter() / 2));
			  }
			  
			  if (e.getY() >= PHEIGHT - 30) {
				  theModel.movePaddleToBottom();
				  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(theModel.getPaddleY() - 75 - (theModel.getBallDiameter() / 2));
			  } else if (e.getY() - theModel.getBallDiameter() < 0 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) { //if there a ball hovering above the paddle
				  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(0);
				  theModel.movePaddleVertically(theModel.getBallDiameter() + 1); //paddle can't go above ceiling
			  } else {
				  theModel.movePaddleVertically(e.getY());
				  if ((theModel.getPaddleY()) - 75 - (theModel.getBallDiameter() / 2) <= 0) {
					  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(0);
				  } else {
					  if (flag2 && theModel.getBallSpeed(theModel.getBallArrayLength() - 1) == 0) theModel.moveBallVertically(theModel.getPaddleY() - 75 - (theModel.getBallDiameter() / 2));
				  }
			  }
		  }
	  }
  }
  
  /**
   * if mouse is pressed fire off a ball if this is legal
   */
  public void mousePressed(MouseEvent e) {
	  if ((theModel.getBallSpeed(0) == 0 || theModel.multiBallIsOn()) && theModel.getNumLives() > 0) { //fire off a ball is speed == 0 and lives > 0
		  theModel.setBallSpeed((16 / Ball.getDifficultyFactor()), theModel.getBallArrayLength() - 1); //set ball speed depending on difficulty
		  theModel.ballRndDir(theModel.getBallArrayLength() - 1); //determine random direction of ball based on difficulty
		  if (theModel.multiBallIsOn()) theModel.makeNewBall(); //make another ball if multiball is turned on
	  }
  }
  
  public void mouseReleased(MouseEvent e) {
	  
  }

  public void mouseEntered(MouseEvent e) {
	  
  }

  public void mouseExited(MouseEvent e) {
	  
  }

  public void mouseClicked(MouseEvent e) {

  }
  /**
   * Listen for esc, q, end, ctrl-c on the canvas to allow a convent
   * exit from the full screen configuration
   */
  private void readyForTermination() {
	addKeyListener( new KeyAdapter() {
       public void keyPressed(KeyEvent e)
       { int keyCode = e.getKeyCode();
         if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
            (keyCode == KeyEvent.VK_END) ||
            ((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
           running = false;
         } else if(keyCode == KeyEvent.VK_P) {
        	 if(theModel.isPaused()) {
        		 theModel.resumeGame();
        	 } else {
        		 theModel.pauseGame();
        	 }
         }
       }
     });
  }

  /** 
   * Notifies this component that it now has a parent component
   * wait for the JPanel to be added to the JFrame before starting
   */
  public void addNotify() { 
    super.addNotify();   // creates the peer
    startGame();         // start the thread
  }

  /**
   * initialise and start the thread 
   */
  private void startGame() { 
    if (animator == null || !running) {
      animator = new Thread(this);
	  animator.start();
    }
  }
    
  // ------------- game life cycle methods ---------------------------------
  // called by the JFrame's window listener methods

  /**
   * called when the JFrame is closing
   */
  public void stopGame() { running = false; }

  // ----------------------------------------------------------------------

  /**
   * Required by Runnable interface.
   * The frames of the animation are drawn inside the while loop.
   */
  public void run() {
    long beforeTime, afterTime, timeDiff, sleepTime;
    long overSleepTime = 0L;
    int noDelays = 0;
    long excess = 0L;

    gameStartTime = System.nanoTime();
    prevStatsTime = gameStartTime;
    beforeTime = gameStartTime;

    running = true;

    while(running) {
      theModel.update();
      theFrame.setBreakoutModel(theModel);
      gameRender();
      paintScreen();

      afterTime = System.nanoTime();
      timeDiff = afterTime - beforeTime;
      sleepTime = (period - timeDiff) - overSleepTime;  

      if (sleepTime > 0) {   // some time left in this cycle
        try {
          Thread.sleep(sleepTime/1000000L);  // nano -> ms
        }
        catch(InterruptedException ex){}
        overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
      }
      else {    // sleepTime <= 0; the frame took longer than the period
        excess -= sleepTime;                  // store excess time value
        overSleepTime = 0L;

        if (++noDelays >= NO_DELAYS_PER_YIELD) {
          Thread.yield();         // give another thread a chance to run
          noDelays = 0; 
        }
      }

      beforeTime = System.nanoTime();
	  
      /* If frame animation is taking too long, update the game state
         without rendering it, to get the updates/sec nearer to
         the required FPS. */
      int skips = 0;
      while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
        excess -= period;
	    theModel.update();                  // update state but don't render
	    theFrame.setBreakoutModel(theModel);
        skips++;
      }
      framesSkipped += skips;

      storeStats();
	}

    printStats();
    System.exit(0);   // so window disappears
  }

  /** 
   * Render the game objects onto dbImage
   */
  private void gameRender() {
    if (dbImage == null){
      dbImage = createImage(PWIDTH, PHEIGHT);
      if (dbImage == null) {
        System.out.println("dbImage is null");
        return;
      }
      else
        dbg = dbImage.getGraphics();
    }

    // set background colors depending on theme chosen
    if (theModel.getColor().equals("red")) {
	    dbg.setColor(Color.black);
	    dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
    } else if (theModel.getColor().equals("blue")) {
    	dbg.setColor(Color.white);
	    dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
    } else if (theModel.getColor().equals("green")) {
    	dbg.setColor(new Color(0, 180, 0));
    	dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
    }
	

    //display bricks, ball(s) (unless lives == 0), lava and paddle
    drawBricks();
    if (theModel.getNumLives() > 0) drawBall();
    drawLava();
    drawPaddle();
  }

  /**
   * use active rendering to put the buffered image on-screen
   */	 
  private void paintScreen() { 
    Graphics g;
    try {
      g = this.getGraphics();
      if ((g != null) && (dbImage != null))
        g.drawImage(dbImage, 0, 0, null);
      g.dispose();
    }
    catch (Exception e)
    { System.out.println("Graphics context error: " + e);  }
  }

  /** 
   *  The statistics:
   *    - the summed periods for all the iterations in this interval
   *      (period is the amount of time a single frame iteration should take), 
   *      the actual elapsed time in this interval, 
   *      the error between these two numbers;
   *
   *    - the total frame count, which is the total number of calls to run();
   *
   *    - the frames skipped in this interval, the total number of frames
   *      skipped. A frame skip is a game update without a corresponding render;
   *
   *    - the FPS (frames/sec) and UPS (updates/sec) for this interval, 
   *      the average FPS & UPS over the last NUM_FPSs intervals.
   *
   *  The data is collected every MAX_STATS_INTERVAL  (1 sec).
   */
  private void storeStats() { 
    frameCount++;
    statsInterval += period;

    if (statsInterval >= MAX_STATS_INTERVAL) {     // record stats every MAX_STATS_INTERVAL
      long timeNow = System.nanoTime() ;
      timeSpentInGame = (int) ((timeNow - gameStartTime)/1000000000L);  // ns --> secs

      long realElapsedTime = timeNow - prevStatsTime;   // time since last stats collection
      totalElapsedTime += realElapsedTime;

      double timingError = 
         ((double)(realElapsedTime - statsInterval) / statsInterval) * 100.0;

      totalFramesSkipped += framesSkipped;

      double actualFPS = 0;     // calculate the latest FPS and UPS
      double actualUPS = 0;
      if (totalElapsedTime > 0) {
        actualFPS = (((double)frameCount / totalElapsedTime) * 1000000000L);
        actualUPS = (((double)(frameCount + totalFramesSkipped) / totalElapsedTime) 
                                                             * 1000000000L);
      }

      // store the latest FPS and UPS
      fpsStore[ (int)statsCount%NUM_FPS ] = actualFPS;
      upsStore[ (int)statsCount%NUM_FPS ] = actualUPS;
      statsCount = statsCount+1;

      double totalFPS = 0.0;     // total the stored FPSs and UPSs
      double totalUPS = 0.0;
      for (int i=0; i < NUM_FPS; i++) {
        totalFPS += fpsStore[i];
        totalUPS += upsStore[i];
      }

      if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
        averageFPS = totalFPS/statsCount;
        averageUPS = totalUPS/statsCount;
      }
      else {
        averageFPS = totalFPS/NUM_FPS;
        averageUPS = totalUPS/NUM_FPS;
      }
      theFrame.setFPS("Your Score: " + theModel.getScore()); //output current score
      //output current power-up
      if (theModel.getPowerUp() != "None" && theModel.getPowerUp() != "HP Up") {
          theFrame.setPWR("Power-Up: " + theModel.getPowerUp() + " (Expires in " + ((theModel.getTime() / 60) + 1) + ")");
      } else {
    	  theFrame.setPWR("Power-Up: " + theModel.getPowerUp());
      }
      
      //output current number of lives
      if (theModel.getNumLives() > 0 && !theModel.checkWin()) {
    	  theFrame.setUPS("Lives Left: " + theModel.getNumLives());	  
      } else if (theModel.checkWin()) {
    	  theFrame.setUPS("A winner is you!");
      } else {
    	  theFrame.setUPS("Game Over");	  
      }

      framesSkipped = 0;
      prevStatsTime = timeNow;
      statsInterval = 0L;   // reset
    }
  }

  /**
   * draws a paddle (colors depend on theme chosen)
   */
  private void drawPaddle() {
	  if (theModel.getColor().equals("red")) {
		  dbg.setColor(Color.red);
		  dbg.fillRect(theModel.getPaddleX(), theModel.getPaddleY(), theModel.getPaddleWidth(), theModel.getPaddleHeight());
		  dbg.setColor(Color.yellow);
		  dbg.fillRect(theModel.getPaddleX() + BORDER, theModel.getPaddleY() + BORDER, theModel.getPaddleWidth() - (BORDER * 2), theModel.getPaddleHeight() - (BORDER * 2));
	  } else if (theModel.getColor().equals("blue")) {
		  dbg.setColor(Color.blue);
		  dbg.fillRect(theModel.getPaddleX(), theModel.getPaddleY(), theModel.getPaddleWidth(), theModel.getPaddleHeight());
		  dbg.setColor(Color.cyan);
		  dbg.fillRect(theModel.getPaddleX() + BORDER, theModel.getPaddleY() + BORDER, theModel.getPaddleWidth() - (BORDER * 2), theModel.getPaddleHeight() - (BORDER * 2));
	  } else if (theModel.getColor().equals("green")) {
		  dbg.setColor(new Color(0, 60, 0));
		  dbg.fillRect(theModel.getPaddleX(), theModel.getPaddleY(), theModel.getPaddleWidth(), theModel.getPaddleHeight());
		  dbg.setColor(new Color(0, 180, 0));
		  dbg.fillRect(theModel.getPaddleX() + BORDER, theModel.getPaddleY() + BORDER, theModel.getPaddleWidth() - (BORDER * 2), theModel.getPaddleHeight() - (BORDER * 2));
	  }
  }
  
  /**
   * draws a ball (colors depend on theme chosen)
   */
  private void drawBall() {
	  for (int i = 0; i < theModel.getBallArrayLength(); i++) {
		  if (theModel.getColor().equals("red")) {
			  dbg.setColor(Color.red);
			  dbg.fillOval(theModel.getBallX(i), theModel.getBallY(i), theModel.getBallDiameter(), theModel.getBallDiameter());
			  dbg.setColor(Color.orange);
			  dbg.fillOval(theModel.getBallX(i) + 2, theModel.getBallY(i) + 2, theModel.getBallDiameter() - 4, theModel.getBallDiameter() - 4);
			  dbg.setColor(Color.yellow);
			  dbg.fillOval(theModel.getBallX(i) + 4, theModel.getBallY(i) + 4, theModel.getBallDiameter() - 8, theModel.getBallDiameter() - 8);
		  } else if (theModel.getColor().equals("blue")) {
			  dbg.setColor(Color.blue);
			  dbg.fillOval(theModel.getBallX(i), theModel.getBallY(i), theModel.getBallDiameter(), theModel.getBallDiameter());
			  dbg.setColor(Color.lightGray);
			  dbg.fillOval(theModel.getBallX(i) + 2, theModel.getBallY(i) + 2, theModel.getBallDiameter() - 4, theModel.getBallDiameter() - 4);
			  dbg.setColor(Color.cyan);
			  dbg.fillOval(theModel.getBallX(i) + 4, theModel.getBallY(i) + 4, theModel.getBallDiameter() - 8, theModel.getBallDiameter() - 8);
		  } else if (theModel.getColor().equals("green")) {
			  dbg.setColor(new Color(0, 60, 0));
			  dbg.fillOval(theModel.getBallX(i), theModel.getBallY(i), theModel.getBallDiameter(), theModel.getBallDiameter());
			  dbg.setColor(new Color(0, 120, 0));
			  dbg.fillOval(theModel.getBallX(i) + 2, theModel.getBallY(i) + 2, theModel.getBallDiameter() - 4, theModel.getBallDiameter() - 4);
			  dbg.setColor(new Color(0, 180, 0));
			  dbg.fillOval(theModel.getBallX(i) + 4, theModel.getBallY(i) + 4, theModel.getBallDiameter() - 8, theModel.getBallDiameter() - 8);
		  }
	  }
  }
  
  /**
   * draws the lava (colors depend on theme chosen)
   */
  private void drawLava() {
	  if (theModel.getColor().equals("red")) {
		  dbg.setColor(Color.red);
		  dbg.fillRect(0, PHEIGHT - 15, PWIDTH, PHEIGHT);
		  dbg.setColor(Color.orange);
		  dbg.fillRect(0, PHEIGHT - 10, PWIDTH, PHEIGHT);
		  dbg.setColor(Color.yellow);
		  dbg.fillRect(0, PHEIGHT - 5, PWIDTH, PHEIGHT);
	  } else if (theModel.getColor().equals("blue")) {
		  dbg.setColor(Color.blue);
		  dbg.fillRect(0, PHEIGHT - 15, PWIDTH, PHEIGHT);
		  dbg.setColor(Color.lightGray);
		  dbg.fillRect(0, PHEIGHT - 10, PWIDTH, PHEIGHT);
		  dbg.setColor(Color.cyan);
		  dbg.fillRect(0, PHEIGHT - 5, PWIDTH, PHEIGHT);
	  } else if (theModel.getColor().equals("green")) {
		  dbg.setColor(new Color(0, 60, 0));
		  dbg.fillRect(0, PHEIGHT - 15, PWIDTH, PHEIGHT);
		  dbg.setColor(new Color(0, 120, 0));
		  dbg.fillRect(0, PHEIGHT - 10, PWIDTH, PHEIGHT);
		  dbg.setColor(new Color(0, 180, 0));
		  dbg.fillRect(0, PHEIGHT - 5, PWIDTH, PHEIGHT);
	  }
  }
  
  /**
   * draws a brick (colors depend on theme chosen)
   */
  public void drawBricks() {
	  for (int i = 0; i < theModel.getNumRows(); i++) {
		  for (int j = 0; j < theModel.getNumBricks(); j++) {
			  if (theModel.getBrickHP(i, j) != 0) {
				  dbg.setColor(theModel.getBrickColor(i, j));
				  dbg.fillRect(theModel.getBrickX(i, j), theModel.getBrickY(i, j), theModel.getBrickWidth(i, j), theModel.getBrickHeight(i, j));
			  }
		  }
	  }
  }
  /**
   * Displays a stat summary, called upon termination.
   */
  private void printStats() {
    System.out.println("Frame Count/Loss: " + frameCount + " / " + totalFramesSkipped);
	System.out.println("Average FPS: " + df.format(averageFPS));
	System.out.println("Average UPS: " + df.format(averageUPS));
    System.out.println("Time Spent: " + timeSpentInGame + " secs");
  }
}