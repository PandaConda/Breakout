import java.awt.Color;

/**
 * bricks lose hp when they are hit by a ball and change their color accordingly. When their hp is reduced to 0 whatever powerup
 * they contain is automatically activated
 * @author aa46
 *
 */
public class Brick {
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	private int hp;
	private Color color;
	private PowerUp powerUp;
	
	/**
	 * initialize values when a brick is created. determine color based on hp
	 * @param xPos
	 * @param yPos
	 * @param width
	 * @param height
	 * @param hp
	 * @param powerUpsEnabled
	 * @param difficulty
	 */
	public Brick(int xPos, int yPos, int width, int height, int hp, boolean powerUpsEnabled, int difficulty) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.hp = hp;
		this.powerUp = new PowerUp(powerUpsEnabled, difficulty);
		
		switch(hp) {
		case -1: this.color = Color.gray; break;
		case 0: this.color = Color.black; break;
		case 1: this.color = Color.yellow; break;
		case 2: this.color = Color.orange; break;
		case 3: this.color = Color.blue; break;
		case 4: this.color = Color.red; break;
		}
	}
	
	//getters and setters start here
	public int getLeftEdge() {
		return xPos;
	}
	
	public int getRightEdge() {
		return xPos + width;
	}
	
	public int getTopEdge() {
		return yPos;
	}
	
	public int getBottomEdge() {
		return yPos + height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	public int getHP() {
		return hp;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public String getPowerUp() {
		return powerUp.getPowerUp();
	}
	
	public int getTime() {
		return powerUp.getTimeLeft();
	}
	//getters and setters end here
	
	/**
	 * reduce hp by 1 and change color when a brick is hit by the ball
	 */
	public void hit() {
		hp--;
		switch(hp) {
		case 0: this.color = Color.black; break;
		case 1: this.color = Color.yellow; break;
		case 2: this.color = Color.orange; break;
		case 3: this.color = Color.blue; break;
		case 4: this.color = Color.red; break;
		}
	}
	
	//destroy the brick if it is hit by an indestructoball
	public void kill() {
		hp = 0;
		this.color = Color.black;
	}
}
