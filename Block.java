public abstract class Block {
	private int hp;
	private double regenTime;
	private String pic;
	
	public Block(int hp, double regenTime, String pic) {
		this.hp = hp;
		this.regenTime = regenTime;
		this.pic = pic;
	}
}
