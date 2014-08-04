/**
 * powerups spawn after a brick's hp is reduced to 0 and change some factor in the game. They all have a time which is displayed in the BreakoutPanel along with
 * the powerup's name while it is active.
 * @author aa46
 *
 */
public class PowerUp {
	private String type;
	private int timeLeft;
	
	/**
	 * create a random powerup based on difficulty. times divided by the fps (60) converts them to seconds
	 * @param powerUpsEnabled
	 * @param difficulty
	 */
	public PowerUp(boolean powerUpsEnabled, int difficulty) {
		if (difficulty == 4) difficulty = 3;
		if (difficulty == 8) difficulty = 4;
		if (difficulty == 16) difficulty = 5;
		int rnd = (int) (100 * Math.random() + 1);
		if (powerUpsEnabled) {
			if (rnd <= 100 - ((6 - difficulty) * 10)) {
				type = "None";
			} else if (rnd <= 100 - ((6 - difficulty) * 9)) {
				type = "Medium Balls";
				timeLeft = 600;
			} else if (rnd <= 100 - ((6 - difficulty) * 8)) {
				type = "Big Balls";
				timeLeft = 600;
			} else if (rnd <= 100 - ((6 - difficulty) * 7)) {
				type = "Small Paddle";
				timeLeft = 600;
			} else if (rnd <= 100 - ((6 - difficulty) * 6)) {
				type = "Big Paddle";
				timeLeft = 600;
			} else if (rnd <= 100 - ((6 - difficulty) * 5)) {
				type = "Indestructoball";
				timeLeft = 600;
			} else if (rnd <= 100 - ((6 - difficulty) * 4)) {
				type = "Torus Mode";
				timeLeft = 1200;
			} else if (rnd <= 100 - ((6 - difficulty) * 3)) {
				type = "HP Up";
				timeLeft = 120;
			} else if (rnd <= 100 - ((6 - difficulty) * 2)) {
				type = "Vertical Mode";
				timeLeft = 1200;
			} else if (rnd <= 100 - (6 - difficulty)) {
				type = "Multiball Mode";
				timeLeft = 300;
			} else if (rnd <= 100) {
				type = "Spawnage Mode";
				timeLeft = 60;
			}
		} else {
			type = "None";
		}
	}

	//getters and settes start here
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public String getPowerUp() {
		return type;
	}
	//getters and setters end here
}
