import java.awt.Rectangle;
/**
 * the paddle follow the mouse horizontally (and vertically if vertical mode is enabled). balls in motion hitting the top or side of the paddle will bounce off it.
 * @author aa46
 *
 */
public class Paddle {
	
	private int width;
	private int height;
	private int xPos;
	private int yPos;
	private boolean verticalMode;
	
	/**
	 * initializes paddle position and size
	 * @param playfieldWidth
	 * @param playfieldHeight
	 */
	public Paddle(int playfieldWidth, int playfieldHeight) {
		width = 80;
		height = 10;
		xPos = (playfieldWidth / 2) - (width / 2);
		yPos = playfieldHeight - 30;
		verticalMode = false;
	}
	
	//getters and setters start here
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getXPos() {
		return xPos;
	}
	
	public void setXPos(int xPos) {
		this.xPos = xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setVerticalMode(boolean verticalMode) {
		this.verticalMode = verticalMode;
	}
	
	public void move(int centerX) {
		xPos = centerX - (width / 2);
	}
	
	public void moveToBottom(int playfieldHeight) {
		yPos = playfieldHeight - 30;
	}
	
	public boolean isVerticalModeEnabled() {
		return verticalMode;
	}
	
	public void moveVertically(int topY) {
		yPos = topY;
	}
	//getters and setters end here
}
