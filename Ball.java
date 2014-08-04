/**
 * a ball whose speed equals 0 hover in a constant position relative to the paddle (right over it),
 * moving down if anything above them gets in the way. when released they collide with the paddle or bricks (unless indestructoball is activated)
 * whenever it hits them. When hitting the paddle they change their direction depending on where they hit and what speed they are
 * traveling at (this increases over time). Balls also collide with corners unless torus mode is enabled
 * @author aa46
 *
 */
public class Ball {
	private double xPos;
	private double yPos;
	private double xDir;
	private double yDir;
	private int speed;
	private int collisionCount;
	private int collisionsToSpeedUp;
	private static int difficultyFactor = 4;
	private static int diameter = 15;
	private boolean gameOver;
	private static int ballCount = 0;
	private int numDir;
	private static boolean indestructoball = false;
	private static boolean torusmode = false;
	
	/**
	 * initializes a ball above the paddle
	 * @param xPos
	 * @param yPos
	 */
	public Ball(int xPos, int yPos) {
		gameOver = false;
		this.xPos = xPos;
		this.yPos = yPos - (diameter / 2);
		ballCount++;
		xDir = 0;
		yDir = 0;
		speed = 0;
		collisionCount = 0;
		collisionsToSpeedUp = difficultyFactor;
		numDir = 32 / difficultyFactor - 1;
	}
	
	//getters and setters go here
	public static int getBallCount() {
		return ballCount;
	}
	
	public static int getDifficultyFactor() {
		return difficultyFactor;
	}
	
	public static void setDifficultyFactor(int newDifficultyFactor) {
		difficultyFactor = newDifficultyFactor;
	}
	
	public int getXPos() {
		return (int)xPos;
	}
	
	public void setXPos(int xPos) {
		this.xPos = xPos;
	}
	
	public int getYPos() {
		return (int)yPos;
	}
	
	public void setYPos(int yPos) {
		this.yPos = yPos;
	}
	
	public double getXDir() {
		return xDir;
	}
	public double getYDir() {
		return yDir;
	}
	
	public static int getDiameter() {
		return diameter;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public static void setBallSize(int newDiameter) {
		diameter = newDiameter;
	}
	
	public static void setIndestructoBall(boolean newIndestructoball) {
		indestructoball = newIndestructoball;
	}
	
	public static boolean isIndestructoBall() {
		return indestructoball;
	}
	
	
	public static void setTorusMode(boolean newTorusMode) {
		torusmode = newTorusMode;
	}
	//getters and setters end here
	
	/**
	 * sets a ball in a random direction (number of possible directions depend on difficulty factor)
	 */
	public void rndDir() {
		//determine directions (they always add up to 1 so they move at a constant speed relative to the speed variable)
		do {
			xDir = (int)((((numDir + 1) % 2) + numDir + 1) * Math.random() - (((((numDir + 1) % 2) + numDir) / 2) - (1 / 2)) - 1);
			yDir = -1;
		} while (numDir % 2 == 0 && xDir == 0); //do not fire ball straight up unless numDir is even (currently never happens)

		//further modify xDir depending on if it positive or negative
		if (xDir < 0) xDir /= ((((((numDir + 1) % 2) + numDir) - 1) / 2) + (xDir + 1));
		if (xDir > 0) xDir /= ((((((numDir + 1) % 2) + numDir) - 1) / 2) - (xDir - 1));
		xDir /= (Math.abs(xDir) + Math.abs(yDir)); //modify xDir some more
		yDir = Math.abs(xDir) - 1; //recalculate yDir so xDir + yDir = 1
	}
	
	/**
	 * moves the ball
	 * @param width
	 * @param height
	 */
	public void move(int width, int height) {
		
		//if ball hits left or right side of screen
		if ((xPos <= 0 && xDir < 0) || (xPos + diameter >= width && xDir > 0)) {
			if (!torusmode) { //if torus mode is disabled reverse xDir
				xDir = -xDir;
			} else { //if torus mode is enabled move ball to other side of screen
				if (xDir < 0 && xPos + diameter < 0) xPos = width;
				if (xDir > 0 && xPos > width) xPos = -diameter;
			}
		} else if (yPos <= 0 && yDir < 0) { //if ball hits top of screen reverse yDir
			yDir = -yDir;
		} else if (yPos >= height) { //if ball hits bottom of screen it is dead
			speed = 0;
			gameOver = true;
		}
		xPos += xDir; //move ball horizontally
		yPos += yDir; //move ball vertically
	}
	
	//respond to a collision with a brick
	public boolean checkForBrickCollision(int hp, int left, int top, int right, int bottom, boolean brickOnLeft, boolean brickOnTop, boolean brickOnRight, boolean brickOnBottom) {
		if (hp != 0) {
			if (xPos + diameter >= left && xPos < left && yPos + diameter >= top && yPos < top && xDir != 0 && yDir != 0) { // top left hit
				if (brickOnLeft) {
					yDir *= -1;
				} else if (brickOnTop) {
					xDir *= -1;
				} else if (xPos + diameter - left > yPos + diameter - top) { //top hit
					if (!indestructoball) yDir *= -1;
				} else if (xPos + diameter - left < yPos + diameter - top) { //left hit
					if (!indestructoball) xDir *= -1;
				} else if (xDir != 0 && yDir != 0) {
					if (!indestructoball) xDir--;
					if (!indestructoball) yDir--;
				} else {
					if (!indestructoball) xDir = -0.5;
					if (!indestructoball) yDir = -0.5;
				}
				return true;
			} else if (yPos + diameter >= top && yPos < top && xPos <= right && xPos + diameter > right && yPos < top && xDir != 0 && yDir != 0) { // top right hit
				if (brickOnRight) {
					yDir *= -1;
				} else if (brickOnTop) {
					xDir *= -1;
				} else if (xPos - right > yPos + diameter - top) {
					if (!indestructoball) yDir *= -1;
				} else if (xPos - right < yPos + diameter - top) {
					if (!indestructoball) xDir *= -1;
				} else if (xDir != 0 && yDir != 0){
					if (!indestructoball) xDir++;
					if (!indestructoball) yDir--;
				} else {
					if (!indestructoball) xDir = 0.5;
					if (!indestructoball) yDir = -0.5;
				}
				return true;
			} else if (xPos <= right && xPos + diameter > right && yPos <= bottom && yPos + diameter > bottom && yPos < top && xDir != 0 && yDir != 0) { // bottom right hit
				if (brickOnRight) {
					yDir *= -1;
				} else if (brickOnBottom) {
					xDir *= -1;
				} else if (xPos - right > yPos - bottom) {
					if (!indestructoball) yDir *= -1;
				} else if (xPos - right < yPos - bottom) {
					if (!indestructoball) xDir *= -1;
				} else if (xDir != 0 && yDir != 0) {
					if (!indestructoball) xDir++;
					if (!indestructoball) yDir++;
				} else {
					if (!indestructoball) xDir = 0.5;
					if (!indestructoball) yDir = 0.5;
				}
				return true;
			} else if (yPos <= bottom && yPos + diameter > bottom && xPos + diameter >= left && xPos < left && yPos < top && xDir != 0 && yDir != 0) { // bottom left hit
				if (brickOnLeft) {
					yDir *= -1;
				} else if (brickOnBottom) {
					xDir *= -1;
				} else if (xPos + diameter - left > yPos - bottom) {
					if (!indestructoball) yDir *= -1;
				} else if (xPos + diameter - left < yPos - bottom) {
					if (!indestructoball) xDir *= -1;
				} else if (xDir != 0 && yDir != 0){
					if (!indestructoball) xDir--;
					if (!indestructoball) yDir++;
				} else {
					if (!indestructoball) xDir = -0.5;
					if (!indestructoball) yDir = 0.5;
				}
				return true;
			} else if (yPos >= top && yPos + diameter <= bottom && (xPos + diameter >= left && xPos < left && xDir > 0 || xPos <= right && xPos + diameter > right && xDir < 0)) { // left or right hit
				if (!indestructoball) xDir *= -1;
				return true;
			} else if (xPos + diameter >= left && xPos <= right && (yPos + diameter >= top && yPos < top && yDir > 0 || yPos <= bottom && yPos + diameter > bottom && yDir < 0)) { // top or bottom hit
				if (!indestructoball) yDir *= -1;
				return true;
			}
		}
		return false;
	}
	
	//check if ball hits top of paddle
	public void checkForTopPaddleCollision(int left, int right, int top, boolean spawnageMode) {
		if (xPos + diameter >= left && xPos <= right) { //if ball collides with top of paddle
			if (!spawnageMode) collisionCount++;
			
			//check if ball should be speeded up
			if (collisionCount == collisionsToSpeedUp) {
				speed += 16 / difficultyFactor;
				collisionsToSpeedUp += difficultyFactor * speed / (16 / difficultyFactor);
				numDir += 2;
			}
			
			//if ball hits top of paddle determine new direction based on numDir
			if (xPos + (diameter / 2) <= left && yPos + (diameter * 3 / 4) <= top) { //left edge of paddle
				xDir = -(((((numDir + 1) % 2) + numDir) / 2) - (1 / 2));
			} else if (xPos + (diameter / 2) > right && yPos + (diameter * 3 / 4) <= top) { //right edge of paddle
				xDir = ((((numDir + 1) % 2) + numDir) / 2) - (1 / 2);
			} else { //center of paddle
				for (int i = 0; i < (((numDir + 1) % 2) + numDir); i++) {
					if (xPos + (diameter / 2) > left + (i * ((right - left) / numDir)) && xPos + (diameter / 2) <= left + ((i + 1) * ((right - left) / numDir))) {
						xDir = i - (((((numDir + 1) % 2) + numDir) / 2) - (1 / 2));
						if (numDir % 2 == 0 && xDir >= 0) xDir++;
					}
				}
			}
			
			//further modify xDir and rescale new directions so xDir + yDir = 0
			if (xDir < 0) xDir /= ((((((numDir + 1) % 2) + numDir) - 1) / 2) + (xDir + 1));
			if (xDir > 0) xDir /= ((((((numDir + 1) % 2) + numDir) - 1) / 2) - (xDir - 1));
			yDir = -1;
			xDir /= (Math.abs(xDir) + Math.abs(yDir));
			yDir = Math.abs(xDir) - 1;
		}
	}
	
	/**
	 * check if ball collides with side of paddle
	 * @param left
	 * @param right
	 * @param spawnageMode
	 */
	public void checkForSidePaddleCollision(int left, int right, boolean spawnageMode) {
		
		//if ball collides with side of paddle reverse xDir and determine whether to speed up
		if ((xDir > 0 && xPos <= left && xPos + diameter >= left) || (xDir < 0 && xPos <= right && xPos + diameter >= right)) { //if ball collides with side of paddle
			xDir = -xDir;
			if (!spawnageMode) collisionCount++;
			
			//speed up ball if necessary
			if (collisionCount == collisionsToSpeedUp) {
				speed += 16 / difficultyFactor;
				collisionsToSpeedUp += difficultyFactor * speed / (16 / difficultyFactor);
				numDir += 2;
			}
			
		} else if (xDir == 0 && yDir == 1 && xPos <= left && xPos + diameter >= left) { //if ball hits left side moving straight down send it diagonally down to the left
			xDir = -0.5;
			yDir = 0.5;
			if (!spawnageMode) collisionCount++;
			
			if (collisionCount == collisionsToSpeedUp) {
				speed += 16 / difficultyFactor;
				collisionsToSpeedUp += difficultyFactor * speed / (16 / difficultyFactor);
				numDir += 2;
			}
		} else if (xDir == 0 && yDir == -1 && xPos <= left && xPos + diameter >= left) { //if ball hits left side moving straight up send it diagonally up to the left
			xDir = -0.5;
			yDir = -0.5;
			if (!spawnageMode) collisionCount++;
			
			if (collisionCount == collisionsToSpeedUp) {
				speed += 16 / difficultyFactor;
				collisionsToSpeedUp += difficultyFactor * speed / (16 / difficultyFactor);
				numDir += 2;
			}
		} else if (xDir == 0 && yDir == 1 && xPos <= right && xPos + diameter >= right) {  //if ball hits right side moving straight down send it diagonally down to the right
			xDir = 0.5;
			yDir = 0.5;
			if (!spawnageMode) collisionCount++;
			
			if (collisionCount == collisionsToSpeedUp) {
				speed += 16 / difficultyFactor;
				collisionsToSpeedUp += difficultyFactor * speed / (16 / difficultyFactor);
				numDir += 32 / difficultyFactor;
			}
		} else if (xDir == 0 && yDir == -1 && xPos <= right && xPos + diameter >= right) { //if ball hits right side moving straight up send it diagonally up to the right
			xDir = 0.5;
			yDir = -0.5;
			if (!spawnageMode) collisionCount++;
			
			if (collisionCount == collisionsToSpeedUp) {
				speed += 16 / difficultyFactor;
				collisionsToSpeedUp += difficultyFactor * speed / (16 / difficultyFactor);
				numDir += 32 / difficultyFactor;
			}
		}
	}
	
	//returns gameOver variable
	public boolean gameOver() {
		return gameOver;
	}
}
