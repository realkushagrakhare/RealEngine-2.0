package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);

		FontType font = new FontType(loader.loadTextureAtlas("candara"), new File("res/candara.fnt"));
		GUIText text = new GUIText("This is a test text", 3, font, new Vector2f(0.0f,0.4f), 1.0f, true);
		text.setColour(0.1f, 0.1f, 0.1f);
		
		// *********TERRAIN TEXTURE STUFF**********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		// *****************************************

		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("flower")));
		
		
		

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);

		
		
		
		TexturedModel bobble = new TexturedModel(OBJLoader.loadObjModel("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));
		bobble.getTexture().setHasTransparency(true);

		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		
		normalMapEntities.add(new Entity(barrelModel, new Vector3f(75,10,-75),0,0,0,1f));
		
		Random random = new Random(676452);
		for (int i = 0; i < 400; i++) {
			if (i % 3 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360,
						0, 0.9f));
			}
			if (i % 1 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(bobble,random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360,
						0, random.nextFloat() * 0.1f + 0.6f));
			}
		}

		Light light = new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.4f, 0.4f, 0.4f));
		List<Light> lights = new ArrayList<Light>();
		//lights.add(light);
		lights.add(new Light(new Vector3f(185,10,-293), new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(370,17,-300), new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
		
		ModelTexture lampTexture = new ModelTexture(loader.loadTexture("lamp"));
		lampTexture.setUseFakeLighting(true);
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),lampTexture);
		entities.add(new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1));
		entities.add(new Entity(lamp, new Vector3f(370,+4.2f,-300),0,0,0,1));
		entities.add(new Entity(lamp, new Vector3f(293,-6.8f,-305),0,0,0,1));
		//entities.add(new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1));
		
		
		MasterRenderer renderer = new MasterRenderer(loader);

		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
				loader.loadTexture("playerTexture")));

		Player player = new Player(stanfordBunny, new Vector3f(100, 5, -150), 0, 180, 0, 0.6f);
		Camera camera = new Camera(player);
		entities.add(player);
		
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		WaterShader waterShader = new WaterShader();
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader,renderer.getProjectionMatrix(),fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water =new WaterTile(75,-75,0);
		waters.add(water);
		
		/*GuiTexture refraction = new GuiTexture(fbos.getRefractionTexture(),new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f));
		GuiTexture reflection = new GuiTexture(fbos.getReflectionTexture(),new Vector2f(-0.5f,0.5f), new Vector2f(0.25f,0.25f));
		guiTextures.add(refraction);
		guiTextures.add(reflection);*/
		
		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			//reflection
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y-water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities,normalMapEntities,terrains,lights,camera, new Vector4f(0,1,0,-water.getHeight()+0f));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			//refraction
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities,normalMapEntities,terrains,lights,camera, new Vector4f(0,-1,0,water.getHeight()));
			
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			
			renderer.renderScene(entities,normalMapEntities,terrains,lights,camera, new Vector4f(0,-1,0,1000));
			waterRenderer.render(waters, camera, light);
			guiRenderer.render(guiTextures);
			TextMaster.render();
			
			DisplayManager.updateDisplay();
		}

		TextMaster.cleanUp();
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
