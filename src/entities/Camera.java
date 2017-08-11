package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0,2,0);
	private float pitch = 20;
	private float yaw;
	private float roll;
	
	private Player player;
	
	public Camera(Player player)
	{
		this.player = player;
	}
	
	public void move()
	{
		calculateZoom();
		calculateAngAroundPlayer();
		calculatePitch();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance,verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}
	
	private void calculateCameraPosition(float horizontalDistance,float verticalDistance)
	{
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float)(horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float)(horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance;
	}
	
	private float calculateHorizontalDistance()
	{
		return (float)(distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance()
	{
		return (float)(distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom()
	{
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch()
	{
		if(Mouse.isButtonDown(1))
		{
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calculateAngAroundPlayer()
	{
		if(Mouse.isButtonDown(0))
		{
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
}
