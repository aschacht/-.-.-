package Box.Box;

import flatLand.trainingGround.Sprites.TerminalSprite;

public class PromptObserver implements Observer {

	private TerminalSprite sprite2;

	public PromptObserver(TerminalSprite sprite2) {
		this.sprite2 = sprite2;
		// TODO Auto-generated constructor stub
	}

	public void notify(String string) {
		sprite2.notify(string);
	}
}
