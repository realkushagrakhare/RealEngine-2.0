package rederEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;
import java.util.List;
import java.util.Map;
public class EntityRenderer {
	
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader,Matrix4f projectionMatrix)
	{
		this.shader = shader;
		//createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<TexturedModel,List<Entity>> entities)
	{
		for(TexturedModel model:entities.keySet())
		{
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch)
			{
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES,model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model)
	{
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		if(texture.isHasTransparency())
		{
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLighting(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
	}
	
	private void unbindTexturedModel()
	{
		MasterRenderer.disableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotX(),entity.getRotY(),entity.getRotZ(),entity.getScale());
		shader.loadTransformMatrix(transformationMatrix);
	}
	
	/*public void render(Entity entity, StaticShader shader)
	{
		TexturedModel texturedModel = entity.getModel();
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotX(),entity.getRotY(),entity.getRotZ(),entity.getScale());
		shader.loadTransformMatrix(transformationMatrix);
		ModelTexture texture = texturedModel.getTexture();
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getId());
		GL11.glDrawElements(GL11.GL_TRIANGLES,model.getVertexCount(),GL11.GL_UNSIGNED_INT, 0);
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}*/
	
}