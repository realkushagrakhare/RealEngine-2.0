package guis;

import org.lwjgl.util.vector.Vector2f;

public class GuiTexture {
	private int texture;
	private Vector2f scale;
	private Vector2f position;
	
	public GuiTexture(int texture, Vector2f scale, Vector2f position) {
		super();
		this.texture = texture;
		this.scale = scale;
		this.position = position;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getScale() {
		return scale;
	}

	public Vector2f getPosition() {
		return position;
	}
	
}
