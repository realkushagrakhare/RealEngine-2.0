package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class TerrainShader extends ShaderProgram {
	private static final String VERTEX_FILE="src/shaders/TerrainVertexShader.txt";
	private static final String FRAGMENT_FILE="src/shaders/TerrainFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_backgorundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	
	public TerrainShader()
	{
		super(VERTEX_FILE,FRAGMENT_FILE);
	}
	@Override
	protected void bindAttributes()
	{
		super.bindAttributes(0, "position");
		super.bindAttributes(1,"textureCoords");
		super.bindAttributes(2, "normal");
	}
	@Override
	protected void getAllUniformLocations() {
		this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		this.location_viewMatrix = super.getUniformLocation("viewMatrix");
		this.location_lightPosition = super.getUniformLocation("lightPosition");
		this.location_lightColour = super.getUniformLocation("lightColour");
		this.location_shineDamper = super.getUniformLocation("shineDamper");
		this.location_reflectivity = super.getUniformLocation("reflectivity");
		this.location_skyColour = super.getUniformLocation("skyColour");
		this.location_backgorundTexture = super.getUniformLocation("backgroundTexture");
		this.location_rTexture = super.getUniformLocation("rTexture");
		this.location_gTexture = super.getUniformLocation("gTexture");
		this.location_bTexture = super.getUniformLocation("bTexture");
		this.location_blendMap = super.getUniformLocation("blendMap");
	}
	
	public void connectTextureUnits()
	{
		super.loadInt(location_backgorundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
	}
	
	public void loadSkyColour(float r,float g,float b)
	{
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	public void loadShineVariables(float damper,float reflectivity)
	{
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadTransformMatrix(Matrix4f matrix)
	{
		super.loadMatrix(this.location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		super.loadMatrix(this.location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera)
	{
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	public void loadLight(Light light)
	{
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
	}
}

