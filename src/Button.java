/*
 * Game button class
 */
interface Action { // My first time using interfaces :D
	void perform();
}

public class Button {
	public int x;
	public int y;
	public int hitboxWidth;
	public int hitboxHeight;
	String type;
	final int ID;
	
	public Button(int x, int y, int hitboxWidth, int hitboxHeight, String type, int ID) {
		this.x = x;
		this.y = y;
		this.hitboxWidth = hitboxWidth;
		this.hitboxHeight = hitboxHeight;
		this.type = type;
		this.ID = ID;
		Panel.buttons.add(this);
	}
	
	/**
	 * Performs an action (Lambda Expression) when clicked
	 * @param a
	 */
	public void onClick(Action a) {
		if(Frame.mouseClickX > x && Frame.mouseClickX < x + hitboxWidth && Frame.mouseClickY > y && Frame.mouseClickY < y + hitboxHeight) {
			a.perform();
			Frame.mouseClickX = -1;
			Frame.mouseClickY = -1;
		}
	}
}