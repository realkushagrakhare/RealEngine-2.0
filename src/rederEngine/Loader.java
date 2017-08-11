package rederEngine;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

public class Loader {
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	public RawModel loadToVAO(float[] positions,float[] textureCoords,float[] normals, int indices[])
	{
		int vaoId = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeLists(0,3,positions);
		storeDataInAttributeLists(1,2,textureCoords);
		storeDataInAttributeLists(2,3,normals);
		unbindVAO();
		return new RawModel(vaoId,indices.length);
	}
	
	public RawModel loadToVao(float[] positions)
	{
		int vaoId = createVAO();
		this.storeDataInAttributeLists(0, 2, positions);
		unbindVAO();
		return new RawModel(vaoId,positions.length/2);
	}
	
	public int loadTexture(String fileName)
	{
		Texture texture=null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_MAX_TEXTURE_LOD_BIAS,(float) -0.4);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int textureId = texture.getTextureID();
		textures.add(textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		return textureId;
	}
	
	private int createVAO()
	{
		int vaoId = GL30.glGenVertexArrays();
		vaos.add(vaoId);
		GL30.glBindVertexArray(vaoId);
		return vaoId;
	}
	
	private void storeDataInAttributeLists(int attributeNumber,int coordinateSize, float[] data)
	{
		int vboId = GL15.glGenBuffers();
		vbos.add(vboId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		FloatBuffer buffer = storeDataInFloatBufffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER , buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void unbindVAO()
	{
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesBuffer(int[] indices)
	{
		int vboId = GL15.glGenBuffers();
		vbos.add(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer,GL15.GL_STATIC_DRAW);
	}
	
	private IntBuffer storeDataInIntBuffer(int[] v)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(v.length * 16);
		buf.order(ByteOrder.nativeOrder());
		IntBuffer buffer = buf.asIntBuffer();
		buffer.put(v);
		buffer.position(0);
		//buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBufffer(float[] v)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(v.length * 16);
		buf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = buf.asFloatBuffer();
		buffer.put(v);
		buffer.position(0);
		//buffer.flip();
		return buffer;
	}
	
	public void cleanUp()
	{
		for(int vao:vaos)
		{
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos)
		{
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture:textures)
		{
			GL11.glDeleteTextures(texture);
		}
	}
}
