package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;

import org.lwjgl.input.Keyboard;
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
import toolbox.MousePicker;
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
		
		int type = 1;
		if(type == 2)
		{
			return;
		}
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
				loader.loadTexture("playerTexture")));

		Player player = new Player(stanfordBunny, new Vector3f(100, 5, -150), 0, 180, 0, 0.6f);
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());

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

		//Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, 256);
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		
		entities.add(player);
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		
		normalMapEntities.add(new Entity(barrelModel, new Vector3f(75,10,-75),0,0,0,1f));
		
		TexturedModel cherryModel = new TexturedModel(OBJLoader.loadObjModel("cherry", loader),
				new ModelTexture(loader.loadTexture("cherry")));
		cherryModel.getTexture().setHasTransparency(true);
		cherryModel.getTexture().setShineDamper(10);
		cherryModel.getTexture().setReflectivity(0.5f);
		cherryModel.getTexture().setExtraInfoMap(loader.loadTexture("cherryS"));
		
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
			if (i % 5 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(cherryModel,random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360,
						0, random.nextFloat() * 1.8f + 2.0f));
			}
		}

		Light light = new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.4f, 0.4f, 0.4f));
		List<Light> lights = new ArrayList<Light>();
		//lights.add(light);
		Light sun = new Light(new Vector3f(0000, 100000, -100000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);
		lights.add(new Light(new Vector3f(185,10,-293), new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(370,17,-300), new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
		
		ModelTexture lampTexture = new ModelTexture(loader.loadTexture("lantern"));
		lampTexture.setUseFakeLighting(true);
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lantern", loader),lampTexture);
		lampTexture.setExtraInfoMap(loader.loadTexture("lanternS"));
		entities.add(new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1));
		entities.add(new Entity(lamp, new Vector3f(370,+4.2f,-300),0,0,0,1));
		entities.add(new Entity(lamp, new Vector3f(293,-6.8f,-305),0,0,0,1));
		//entities.add(new Entity(lamp, new Vector3f(185,-4.7f,-293),0,0,0,1));
		
		
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(),
				new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		//guiTextures.add(shadowMap);

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
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);
		
		ParticleSystem system = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 1);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(0.8f);
		
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight()); 
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE); 
		PostProcessing.init(loader);
		
		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			
			system.generateParticles(player.getPosition());
			ParticleMaster.update(camera);
			
			renderer.renderShadowMap(entities, sun);
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			//reflection
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y-water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities,normalMapEntities,terrains,lights,camera, new Vector4f(0,1,0,-water.getHeight()+0f));
			ParticleMaster.renderParticles(camera);
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			//refraction
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities,normalMapEntities,terrains,lights,camera, new Vector4f(0,-1,0,water.getHeight()));
			//ParticleMaster.renderParticles(camera);
			
			//Screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			
			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(entities,normalMapEntities,terrains,lights,camera, new Vector4f(0,-1,0,1000));
			waterRenderer.render(waters, camera, light);
			
			ParticleMaster.renderParticles(camera);
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToScreen();
			//multisampleFbo.resolveToFbo(outputFbo);
			//PostProcessing.doPostProcessing(outputFbo.getColourTexture());
			
			guiRenderer.render(guiTextures);
			TextMaster.render();
			
			DisplayManager.updateDisplay();
		}
		
		PostProcessing.cleanUp();
		outputFbo.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

	
	

	// Main 2
	
	
	
	
	
	
	
	
	/*public static void main2() {
		 
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        TextMaster.init(loader);
         
        FontType font = new FontType(loader.loadTexture("harrington"), new File("res/harrington.fnt"));
        GUIText text = new GUIText("This is some text!", 3f, font, new Vector2f(0f, 0f), 1f, true);
        text.setColour(1, 0, 0);
 
        // *********TERRAIN TEXTURE STUFF**********
         
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
 
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
                gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
 
        // *****************************************
 
        TexturedModel rocks = new TexturedModel(OBJLoader.loadObjModel("rocks", loader),
                new ModelTexture(loader.loadTexture("rocks")));
 
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
 
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
                fernTextureAtlas);
 
        TexturedModel bobble = new TexturedModel(OBJLoader.loadObjModel("pine", loader),
                new ModelTexture(loader.loadTexture("pine")));
        bobble.getTexture().setHasTransparency(true);
 
        fern.getTexture().setHasTransparency(true);
 
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "customHeightMap");
        List<Terrain> terrains = new ArrayList<Terrain>();
        terrains.add(terrain);
 
        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
                new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUseFakeLighting(true);
 
        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> normalMapEntities = new ArrayList<Entity>();
         
        //******************NORMAL MAP MODELS************************
         
        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
                new ModelTexture(loader.loadTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
                new ModelTexture(loader.loadTexture("crate")));
        crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
        crateModel.getTexture().setShineDamper(10);
        crateModel.getTexture().setReflectivity(0.5f);
         
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
                new ModelTexture(loader.loadTexture("boulder")));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.5f);
         
         
        //************ENTITIES*******************
         
        Entity entity = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
        Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
        Entity entity3 = new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.04f);
        normalMapEntities.add(entity);
        normalMapEntities.add(entity2);
        normalMapEntities.add(entity3);
         
        Random random = new Random(5666778);
        for (int i = 0; i < 60; i++) {
            if (i % 3 == 0) {
                float x = random.nextFloat() * 150;
                float z = random.nextFloat() * -150;
                if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
                } else {
                    float y = terrain.getHeightOfTerrain(x, z);
 
                    entities.add(new Entity(fern, 3, new Vector3f(x, y, z), 0,
                            random.nextFloat() * 360, 0, 0.9f));
                }
            }
            if (i % 2 == 0) {
 
                float x = random.nextFloat() * 150;
                float z = random.nextFloat() * -150;
                if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
 
                } else {
                    float y = terrain.getHeightOfTerrain(x, z);
                    entities.add(new Entity(bobble, 1, new Vector3f(x, y, z), 0,
                            random.nextFloat() * 360, 0, random.nextFloat() * 0.6f + 0.8f));
                }
            }
        }
        entities.add(new Entity(rocks, new Vector3f(75, 4.6f, -75), 0, 0, 0, 75));
         
        //*******************OTHER SETUP***************
 
        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);
 
        MasterRenderer renderer = new MasterRenderer(loader);
 
        RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
        TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
                loader.loadTexture("playerTexture")));
 
        Player player = new Player(stanfordBunny, new Vector3f(75, 5, -75), 0, 100, 0, 0.6f);
        entities.add(player);
        Camera camera = new Camera(player);
        List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
     
        //**********Water Renderer Set-up************************
         
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<WaterTile>();
        WaterTile water = new WaterTile(75, -75, 0);
        for(int i = 1; i < 5; i++){
        	for(int j = 1; j < 5; j++){
        		waters.add(new WaterTile(i * 150, -j * 150, -20));
        	}
        }
        //waters.add(water);
         
        
        
        //****************Game Loop Below*********************
 
        while (!Display.isCloseRequested()) {
            player.move(terrain);
            camera.move();
            picker.update();
            entity.increaseRotation(0, 1, 0);
            entity2.increaseRotation(0, 1, 0);
            entity3.increaseRotation(0, 1, 0);
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
             
            //render reflection teture
            buffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()+1));
            camera.getPosition().y += distance;
            camera.invertPitch();
             
            //render refraction texture
            buffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
             
            //render to screen
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer(); 
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));    
            waterRenderer.render(waters, camera, sun);
            guiRenderer.render(guiTextures);
            TextMaster.render();
             
            DisplayManager.updateDisplay();
        }
 
        //*********Clean Up Below**************
         
        //ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        buffers.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }*/
}
