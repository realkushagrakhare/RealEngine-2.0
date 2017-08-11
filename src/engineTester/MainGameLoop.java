package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;

import org.lwjgl.opengl.ContextAttribs;

import rederEngine.DisplayManager;
import rederEngine.Loader;
import rederEngine.MasterRenderer;
import rederEngine.OBJLoader;
import rederEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {
	public static void main(String args[])
	{
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkflowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		/*float[] vertices = {
							-0.5f, 0.5f, 0f,
							-0.5f, -0.5f, 0f, 
							0.5f, -0.5f, 0f, 
							0.5f, 0.5f, 0f, 
							};
		int[] indices = {
						 0,1,3,
						 3,1,2
						};
		float[] textureCoords ={
				0,0,
				0,1,
				1,1,
				1,0
		};*/
		/*float[] vertices = {			
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,0.5f,-0.5f,		
				
				-0.5f,0.5f,0.5f,	
				-0.5f,-0.5f,0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				0.5f,0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				-0.5f,-0.5f,0.5f,	
				-0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,0.5f,
				-0.5f,0.5f,-0.5f,
				0.5f,0.5f,-0.5f,
				0.5f,0.5f,0.5f,
				
				-0.5f,-0.5f,0.5f,
				-0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,0.5f
				
		};
		
		float[] textureCoords = {
				
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0

				
		};
		
		int[] indices = {
				0,1,3,	
				3,1,2,	
				4,5,7,
				7,5,6,
				8,9,11,
				11,9,10,
				12,13,15,
				15,13,14,	
				16,17,19,
				19,17,18,
				20,21,23,
				23,21,22

		};
		RawModel model = loader.loadToVAO(vertices,textureCoords,indices);*/
		RawModel model = OBJLoader.loadObjModel("tree", loader);
		//ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
		TexturedModel texturedModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree")));
		ModelTexture texture = texturedModel.getTexture();
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
				new ModelTexture(loader.loadTexture("fern")));
		
		fern.getTexture().setHasTransparency(true);
		texture.setShineDamper(1);
		texture.setReflectivity(0.5f);
		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for(int i=0;i<500;i++)
		{
			entities.add(new Entity(texturedModel,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),
					0,0,0,3));
			entities.add(new Entity(grass,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),
					0,0,0,1));
			entities.add(new Entity(fern,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),
					0,0,0,0.6f));
		}
		//StaticShader shader = new StaticShader();
		//Renderer renderer = new Renderer(shader);
		Entity entity = new Entity(texturedModel,new Vector3f(0,0,-50),0,0,0,2);
		Light light = new Light(new Vector3f(0,0,-20),new Vector3f(1,1,1));
		
		Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap,"heightMap");
		Terrain terrain2 = new Terrain(-1,-1,loader,texturePack,blendMap,"heightMap");
		
		
		MasterRenderer renderer = new MasterRenderer();
		
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
				loader.loadTexture("playerTexture")));
		Player player = new Player(stanfordBunny, new Vector3f(100,0,-50),0,180,0,0.6f);
		Camera camera = new Camera(player);
		while(!Display.isCloseRequested())
		{
			//entity.increasePosition(0,0,0f);
			entity.increaseRotation(0, 1, 0);
			camera.move();
			//camera.setYaw();
			player.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain2);
			renderer.processTerrain(terrain);
			for(int i=0;i<500;i++)
				renderer.processEntity(entities.get(i));
			//renderer.prepare();
			//shader.start();
			//shader.loadLight(light);
			//shader.loadViewMatrix(camera);
			//renderer.render(entity,shader);
			//shader.stop();
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		//shader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
