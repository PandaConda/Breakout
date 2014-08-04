import java.awt.Color;

/**
 * the BreakoutModel maintains the state of the game
 * @author aa46
 *
 */
public class BreakoutModel {

	private int playfieldWidth;
	private int playfieldHeight;
	private Paddle paddle;
	private Ball[] ball;
	private Brick[][] brick;
	private boolean isPaused = false;
	private boolean spawnageMode = false;
	private boolean multiBall = false;
	private static String color = "red";
	private int lives;
	private int score;
	private boolean win;
	private boolean powerUpsEnabled;
	private String powerUp;
	private int pwrTimeLeft;

	/**
	 * set default values
	 * @param pwidth
	 * @param pheight
	 */
	public BreakoutModel(int pwidth, int pheight) {
		playfieldWidth = pwidth;
		playfieldHeight = pheight;
		paddle = new Paddle(pwidth, pheight);
		ball = new Ball[1];
		brick = new Brick[0][0];
		lives = 3;
		score = 0;
		win = false;
		powerUpsEnabled = true;
		powerUp = "None";
		pwrTimeLeft = 0;
	}
	//getters and setters start here
	public String getPowerUp() {
		return powerUp;
	}

	public int getBallCount() {
		return Ball.getBallCount();
	}
	
	public int getNumLives() {
		return lives;
	}
	
	public int getTime() {
		return pwrTimeLeft;
	}
	
	public void setIndestructoBall(boolean indestructoball) {
		Ball.setIndestructoBall(indestructoball);
	}
	//getters and setters end here
	
	/**
	 * loads a level
	 */
	public void loadLevel(String name) {
		//reset variables
		win = false;
		lives = 3;
		score = 0;
		ball = new Ball[1];
		
		powerUp = "None";
		activatePowerUp();
		
		//load level depending on which was chosen (I did not have time to implement a better way to do this t_t)
		if (name.equals("empty")) {
			brick = new Brick[0][0];
		} else if (name.equals("classic")) {
			brick = new Brick[4][16];
			
			for (int i = 0; i < brick[0].length; i++) {
				brick[0][i] = new Brick((int)(i * (playfieldWidth / brick[0].length)), (int)(((1) * (playfieldHeight / (brick[0].length * 2)))), playfieldWidth / brick[0].length, playfieldHeight / (brick.length * 8), 4, powerUpsEnabled, Ball.getDifficultyFactor());
			}
			
			for (int i = 0; i < brick[1].length; i++) {
				brick[1][i] = new Brick((int)(i * (playfieldWidth / brick[1].length)), (int)(((2) * (playfieldHeight / (brick[1].length * 2)))), playfieldWidth / brick[1].length, playfieldHeight / (brick.length * 8), 3, powerUpsEnabled, Ball.getDifficultyFactor());
			}
			
			for (int i = 0; i < brick[2].length; i++) {
				brick[2][i] = new Brick((int)(i * (playfieldWidth / brick[2].length)), (int)(((3) * (playfieldHeight / (brick[2].length * 2)))), playfieldWidth / brick[2].length, playfieldHeight / (brick.length * 8), 2, powerUpsEnabled, Ball.getDifficultyFactor());
			}
			
			for (int i = 0; i < brick[3].length; i++) {
				brick[3][i] = new Brick((int)(i * (playfieldWidth / brick[3].length)), (int)(((4) * (playfieldHeight / (brick[3].length * 2)))), playfieldWidth / brick[3].length, playfieldHeight / (brick.length * 8), 1, powerUpsEnabled, Ball.getDifficultyFactor());
			}
		} else if (name.equals("invinciblock")) {
			brick = new Brick[10][10];
			for (int i = 0; i < brick.length; i++) {
				for (int j = 0; j < brick[i].length; j++) {
					brick[i][j] = new Brick((int)(j * (playfieldWidth / brick[i].length)), (int)(((i) * (playfieldHeight / (brick[i].length * 2)))), playfieldWidth / brick[i].length, (int)(playfieldHeight / (brick.length * 2.4)), 0, powerUpsEnabled, Ball.getDifficultyFactor());
				}
			}
			int x = (int)(10 * Math.random());
			int y = (int)(10 * Math.random());
			brick[x][y] = new Brick((int)(y * (playfieldWidth / brick[x].length)), (int)(((x + 1) * (playfieldHeight / (brick[x].length * 2)))), playfieldWidth / brick[x].length, (int)(playfieldHeight / (brick.length * 2.4)), -1, powerUpsEnabled, Ball.getDifficultyFactor());
		}
	}
	
	/**
	 * enable powerup effect whenever a power-up is collected
	 */
	private void activatePowerUp() {
		
		if (powerUp.equals("Medium Balls")) {
			Ball.setBallSize(30);
		} else if (powerUp.equals("Big Balls")) {
			Ball.setBallSize(45);
		} else {
			Ball.setBallSize(15);
		}
		
		if (powerUp.equals("Small Paddle")) {
			this.setPaddleWidth(40);
		} else if (powerUp.equals("Big Paddle")) {
			this.setPaddleWidth(120);
		} else {
			this.setPaddleWidth(80);
		}
		
		if (powerUp.equals("Indestructoball")) {
			for (int i = 0; i < ball.length; i++) ball[i].setIndestructoBall(true);
		} else {
			for (int i = 0; i < ball.length; i++) ball[i].setIndestructoBall(false);
		}
		
		if (powerUp.equals("Torus Mode")) {
			Ball.setTorusMode(true);
		} else {
			Ball.setTorusMode(false);
		}
		
		if (powerUp.equals("HP Up")) {
			if (pwrTimeLeft == 120) lives++; //only increase lives once
		}
		
		if (powerUp.equals("Vertical Mode")) {
			paddle.setVerticalMode(true);
		} else {
			paddle.setVerticalMode(false);
			paddle.moveToBottom(playfieldHeight); //move paddle to bottom when deactivated
		}
		
		if (powerUp.equals("Multiball Mode")) {
			multiBall = true;
			if (ball.length == 1) makeNewBall();
		} else {
			multiBall = false;
		}
		
		if (powerUp.equals("Spawnage Mode")) {
			spawnageMode = true;
		} else {
			spawnageMode = false;
		}
	}
	
	public void setPowerUps(boolean powerUpsEnabled) {
		this.powerUpsEnabled = powerUpsEnabled;
	}
	
	/**
	 * updates the game state
	 */
	public void update() {
		if (!isPaused) { //only update if game is not paused
			
			//decrease time if a power up is in effect
			if (pwrTimeLeft > 0) {
				pwrTimeLeft--;
				if (pwrTimeLeft == 0) {
					if (powerUp.equals("Multiball Mode")) {
						removeBall(ball.length - 1);
					}
					
					powerUp = "None"; //deactive power-up when time is up
					activatePowerUp();
				}
			}
			
			//move ball
			if (ball[0] == null) ball[0] = new Ball(paddle.getXPos() + (paddle.getWidth() / 2) - (Ball.getDiameter() / 2), paddle.getYPos() - 75); //if there are no balls on the field make a new ball
			for (int i = 0; i < ball.length; i++) { //for every ball
				for (int j = 0; j < ball[i].getSpeed(); j++) { //move once * speed
					ball[i].move(playfieldWidth, playfieldHeight);
					//handle paddle-ball collisions
					if (ball[i].getYPos() + Ball.getDiameter() <= paddle.getYPos() + paddle.getHeight() && ball[i].getYPos() + Ball.getDiameter() >= paddle.getYPos() && ball[i].getYDir() > 0) {
						ball[i].checkForTopPaddleCollision(paddle.getXPos(), paddle.getXPos() + paddle.getWidth(), paddle.getYPos(), spawnageMode);
					} else if (ball[i].getYPos() + (Ball.getDiameter() / 2) > paddle.getYPos() && ball[i].getYPos() <= paddle.getYPos() + paddle.getHeight()) {
						ball[i].checkForSidePaddleCollision(paddle.getXPos(), paddle.getXPos() + paddle.getWidth(), spawnageMode);
					}
					if (ball[i].gameOver()) { //remove ball once it reaches bottom of screen
						
						if (ball.length == 1) {//remove a life if all balls are dead
							lives--;
							powerUp = "None";
							activatePowerUp();
						}
						
						removeBall(i);
					}
					
					//handle ball-brick collisions
					boolean flag = false;
					if (brick.length == 0) flag = true;
					while (!flag && !win) {
						for (int k = 0; k < brick.length; k++) { //for every row
							if (i >= ball.length) break;
							for (int l = 0; l < brick[k].length; l++) { //for every brick
								if (i >= ball.length) break;
								
								boolean brickOnLeft = false;
								boolean brickOnTop = false;
								boolean brickOnRight = false;
								boolean brickOnBottom = false;
								
								//determine if there are any bricks touching brick
								if (l > 0) brickOnLeft = (brick[k][l - 1].getHP() != 0 && brick[k][l - 1].getRightEdge() == brick[k][l].getLeftEdge());
								if (k > 0) brickOnTop = (brick[k - 1][l].getHP() != 0 && brick[k - 1][l].getBottomEdge() == brick[k][l].getTopEdge());
								if (l < brick[k].length - 1) brickOnRight = (brick[k][l + 1].getHP() != 0 && brick[k][l + 1].getLeftEdge() == brick[k][l].getRightEdge());
								if (k < brick.length - 1) brickOnBottom = (brick[k + 1][l].getHP() != 0 && brick[k + 1][l].getBottomEdge() == brick[k][l].getBottomEdge());
								
								try {
									if (ball[i].checkForBrickCollision(brick[k][l].getHP(), brick[k][l].getLeftEdge(), brick[k][l].getTopEdge(), brick[k][l].getRightEdge(), brick[k][l].getBottomEdge(), brickOnLeft, brickOnTop, brickOnRight, brickOnBottom)) { //if there is a brick-collision
										flag = true;
										//bounce ball unless it is an indestructoball
										if (!Ball.isIndestructoBall()) { //if ball is not an indestructoball
											int numHits;
											if (powerUp.equals("Big Balls")) numHits = 5; //if ball is big remove 5hp
											else if (powerUp.equals("Medium Balls")) numHits = 3; //if ball is medium remove 3hp
											else numHits = 1; //if ball is small remove 1hp
											for (int m = 0; m < numHits; m++) { //remove hp
												if (brick[k][l].getHP() > 0) score += 16 / Ball.getDifficultyFactor();
												if (brick[k][l].getHP() != 0) brick[k][l].hit();
												if (brick[k][l].getHP() == 0) { //if brick dies activate it's power-up (if there is one)
													if (!brick[k][l].getPowerUp().equals("None") && (ball.length == 1 || multiBall && (ball.length == 1 || ball.length == 2))) {
														powerUp = brick[k][l].getPowerUp();
														pwrTimeLeft = brick[k][l].getTime();
														activatePowerUp();
													}
												}
											}
										} else { //if ball is indestructoball do not bounce, kill brick instantly, and do not collect power-up
											if (brick[k][l].getHP() > 0) score += brick[k][l].getHP() * (16 / Ball.getDifficultyFactor());
											brick[k][l].kill();
										}
									}
								} catch (NullPointerException e) { //prevents crashing
									
								}
							}
						}
						//if player has killed all blocks on screen he/she wins the game
						boolean flag3 = true;
						for (int k = 0; k < brick.length; k++) {
							for (int l = 0; l < brick[k].length; l++) {
								if (brick[k][l].getHP() != 0) flag3 = false;
							}
						}
						if (flag3) {
							score += Math.pow((double)(16 / Ball.getDifficultyFactor()), (double)(lives - 1)); //if player wins add bonus to score based on number of lives left and difficulty
							win = true;
						}
						flag = true;
					}
					if (i >= ball.length) break; //prevents crashing
				}
				if (i >= ball.length) break; //prevents crashing
			}
			
			if (!spawnageMode && !multiBall && ball.length > 1 && ball[ball.length - 1].getSpeed() == 0) removeBall(ball.length - 1); //remove ball above paddle once multi/spawnage mode times out
			if (multiBall && ball[ball.length - 1].getSpeed() != 0) this.makeNewBall(); //if multiball is on create another ball when old one is fired
			if (spawnageMode) { //mass-spawn balls is spawnage mode is enable
				if (ball[0].getSpeed() == 0) {
					ball[0].rndDir();
					ball[0].setSpeed(16 / Ball.getDifficultyFactor());
				}
				makeNewBall();										//THIS
				ball[ball.length - 1].setSpeed(ball[0].getSpeed());	//IS
				ball[ball.length - 1].rndDir();						//SPAWNAGE
			}
		}
	}
	 /**
	   * called when the JFrame is activated / deiconified
	   */
	  public void resumeGame() {
		  isPaused = false;
	  } 

	  /**
	   * called when the JFrame is deactivated / iconified
	   */
	  public void pauseGame() {
		  isPaused = true;
	  }
	  
	  //more getters and setters go here
	  public boolean isPaused() {
		  return isPaused;
	  }
	  
	  public int getPaddleX() {
		  return paddle.getXPos();
	  }
	  
	  public int getPaddleY() {
		  return paddle.getYPos();
	  }
	  
	  public int getPaddleWidth() {
		  return paddle.getWidth();
	  }
	  
	  public int getPaddleHeight() {
		  return paddle.getHeight();
	  }
	  
	  public void movePaddle(int centerX) {
		  if (!isPaused) paddle.move(centerX);
	  }
	  
	  public void movePaddleVertically(int topY) {
		  if (!isPaused) paddle.moveVertically(topY);
	  }
	  
	  public boolean verticalModeEnabled() {
		  return paddle.isVerticalModeEnabled();
	  }
	  
	  public int getBallX(int i) {
		  boolean check = true;
		  do {
			  try {
				  return ball[i].getXPos();
			  } catch (NullPointerException e) {
				  return 0;
			  } catch (ArrayIndexOutOfBoundsException e) {
				  return 0;
			  }
		  } while (!check); //prevents the occasional crash
	  }
	  
	  public int getBallY(int i) {
		  boolean check = true;
		  do {
			  try {
				  return ball[i].getYPos();
			  } catch (NullPointerException e) {
				  return 0;
			  } catch (ArrayIndexOutOfBoundsException e) {
				  return 0;
			  }
		  } while (!check); //prevents the occasional crash
	  }
	  
	  public int getBallDiameter() {
		  return Ball.getDiameter();
	  }
	  
	  public int getBallSpeed(int i) {
		  boolean check = true;
		  do {
			  try {
				  return ball[i].getSpeed();
			  } catch (NullPointerException e) {
				  return 0;
			  }
		  } while (!check); //prevents the occasional crash
	  }
	  
	  public void setBallSpeed(int speed, int i) {
		  ball[i].setSpeed(speed);
	  }
	  
	  public void ballRndDir(int i) {
		  ball[i].rndDir();
	  }
	  
	  public int getNumBricks() {
		  if (!(brick.length == 0)) {
			  return brick[0].length;
		  } else {
			  return 0; //if there are no bricks return 0
		  }
	  }
	  
	  public int getNumRows() {
		  return brick.length;
	  }
	  
	  public Color getBrickColor(int i, int j) {
		  return brick[i][j].getColor();
	  }
	  
	  public int getBrickX(int i, int j) {
		  return brick[i][j].getLeftEdge();
	  }
	  
	  public int getBrickY(int i, int j) {
		  return brick[i][j].getTopEdge();
	  }
	  
	  public int getBrickWidth(int i, int j) {
		  return (brick[i][j].getWidth());
	  }
	  
	  public int getBrickHeight(int i, int j) {
		  return (brick[i][j].getHeight());
	  }
	  
	  public int getBrickHP(int i, int j) {
		  boolean check = true;
		  do {
			  try {
				  return brick[i][j].getHP();
			  } catch (NullPointerException e) {
				  check = false;
			  }
		  } while (!check); //prevents the occasional crash
		  return 0;
	  }
	  
	  public int getScore() {
		  return score;
	  }
	  
	  public boolean checkWin() {
		  return win;
	  }
	  
	  //getters and setters end here
	  
	  /**
	   * adds a brick to a row of bricks (called when a level is loaded)
	   */
	  public void makeBrick(int row, int xPos, int yPos, int width, int height, int hp) {
		  Brick[] temp = brick[row];
		  ball = new Ball[ball.length + 1];
		  for (int i = 0; i < brick[row].length - 1; i++) brick[row][i] = temp[i];
		  brick[row][brick.length - 1] = new Brick(xPos, yPos, width, height, hp, powerUpsEnabled, Ball.getDifficultyFactor());
	  }
	  
	  /**
	   * makes a new ball by adding it to the end of an array
	   */
	  public void makeNewBall() {
		  if (lives != 0) {
			  Ball[] temp = ball;
			  ball = new Ball[ball.length + 1];
			  for (int i = 0; i < ball.length - 1; i++) ball[i] = temp[i];
			  ball[ball.length - 1] = new Ball(paddle.getXPos() + (paddle.getWidth() / 2) - (Ball.getDiameter() / 2) , paddle.getYPos() - 75);
		  }
	  }
	  
	  //even more getters and setter go here!
	  public int getBallArrayLength() {
		  return ball.length;
	  }
	  
	  public String getColor() {
		  return color;
	  }
	  
	  public void setColor(String newColor) {
		  color = newColor;
	  }
	  
	  //getters and setters go here
	  
	  /**
	   * removes a ball from and array, moves all balls that come after it back 1 slot and shortens the array
	   */
	  private void removeBall(int x) {
		  
		  if (ball.length > 1) { //if there is more than one ball on the screen
			  Ball[] temp = ball; //create temporary ball array
			  for (int i = x; i < ball.length - 1; i++) temp[i] = temp[i + 1]; //shift balls created after ball to be removed 1 step left in array
			  ball = new Ball[temp.length - 1]; //remove 1 slot from ball array
			  for (int i = 0; i < ball.length; i++) ball[i] = temp[i]; //move balls from temporary array to ball array
		  } else {
			  ball[0] = new Ball(paddle.getXPos() + (paddle.getWidth() / 2) - (Ball.getDiameter() / 2) , paddle.getYPos() - 75);
		  }
	  }
	  
	  /**
	   * moves the ball diagonally with the paddle
	   * @param xPos
	   */
	  public void moveBall(int xPos) {
		  if (!isPaused) ball[ball.length - 1].setXPos(xPos);
	  }
	  
	  /**
	   * moves the ball vertically with the paddle (when vertical mode is on
	   * @param yPos
	   */
	  public void moveBallVertically(int yPos) {
		  if (!isPaused) ball[ball.length - 1].setYPos(yPos);
	  }
	  
	  //and some more getters and setters...
	  public boolean multiBallIsOn() {
		  return multiBall;
	  }
	  
	  public void setMultiBall(boolean multiBall) {
		  this.multiBall = multiBall;
	  }
	  
	  public void setSpawnageMode(boolean spawnageMode) {
		  this.spawnageMode = spawnageMode;
	  }
	  
	  public boolean spawnageModeEnabled() {
		  return spawnageMode;
	  }
	  public void setDifficultyFactor(int difficultyFactor) {
		  Ball.setDifficultyFactor(difficultyFactor);
		  if (ball[ball.length - 1].getSpeed() == 0) {
			  ball[ball.length - 1] = new Ball(paddle.getXPos() + (paddle.getWidth() / 2) - (Ball.getDiameter() / 2), paddle.getYPos() - 75);
		  }
	  }
	  
	  public void setBallSize(int diameter) {
		  Ball.setBallSize(diameter);
		  if (ball[ball.length - 1].getSpeed() == 0) {
			  ball[ball.length - 1].setXPos(paddle.getXPos() + (paddle.getWidth() / 2) - (Ball.getDiameter() / 2));
			  ball[ball.length - 1].setYPos(paddle.getYPos() - 75 - (Ball.getDiameter() / 2));
		  }
	  }
	  
	  public void setPaddleWidth(int width) {
		  paddle.setXPos(paddle.getXPos() + ((paddle.getWidth() - width) / 2));
		  paddle.setWidth(width);
	  }
	  
	  public void setVerticalMode(boolean verticalMode) {
		  paddle.setVerticalMode(verticalMode);
	  }
	  
	  public void movePaddleToBottom() {
		  paddle.moveToBottom(playfieldHeight);
		  if (ball[ball.length - 1].getSpeed() == 0) ball[ball.length - 1].setYPos(paddle.getYPos() - (Ball.getDiameter() / 2) - 75);
	  }
	  
	  public void setTorusMode(boolean torusMode) {
		  Ball.setTorusMode(torusMode);
	  }
	  //getters and setters end here
}
