package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class StaticShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE="src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE="src/shaders/fragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColour;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	
	public StaticShader()
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
		this.location_shineDamper = super.getUniformLocation("shineDamper");
		this.location_reflectivity = super.getUniformLocation("reflectivity");
		this.location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		this.location_skyColour = super.getUniformLocation("skyColour");
		this.location_numberOfRows = super.getUniformLocation("numberOfRows");
		this.location_offset = super.getUniformLocation("offset");
		this.location_plane = super.getUniformLocation("plane");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour =  new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i=0;i<MAX_LIGHTS;i++)
		{
			location_lightPosition[i] = super.getUniformLocation("lightPositio["+i+"]");
			location_lightColour[i] = super.getUniformLocation("lightColour["+i+"]");
			location_attenuation[i] = super.getUniformLocation("attenuation["+i+"]");
		}
		
	}
	
	public void loadClipPlnae(Vector4f plnae)
	{
		super.loadVector(location_plane, plnae);
	}
	
	public void loadNumberOfRows(int numberOfRows)
	{
		super.loadFloat(location_numberOfRows,numberOfRows);
	}
	
	public void loadOffset(float x,float y)
	{
		super.load2DVector(location_offset,new Vector2f(x,y));
	}
	
	public void loadSkyColour(float r,float g,float b)
	{
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	public void loadFakeLighting(boolean useFake)
	{
		super.loadBoolean(location_useFakeLighting, useFake);
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
	
	public void loadLights(List<Light> lights)
	{
		for(int i=0;i<MAX_LIGHTS;i++)
		{
			if(i<lights.size())
			{
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}
			else
			{
				super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector(location_lightColour[i], new Vector3f(0,0,0));
				super.loadVector(location_attenuation[i], new Vector3f(1,0,0));
			}
		}
	}
}
