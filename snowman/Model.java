import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/* I declare any modifcations to this code (sourced from tutorial exercises) is my own work */
/* Author Alistair Cook - adcook1@sheffield.ac.uk */

public class Model {
  
  private Mesh mesh;
  private int[] textureId1; 
  private int[] textureId2;
  private int[] textureId3;
  private int[] textureId4;
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light light;
  private Light light2;
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, Light light2) {
		this.mesh = mesh;
		this.material = material;
		this.modelMatrix = modelMatrix;
		this.shader = shader;
		this.camera = camera;
		this.light = light;
		this.textureId1 = textureId1;
		this.light2 = light2;
}
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
	    this.mesh = mesh;
	    this.material = material;
	    this.modelMatrix = modelMatrix;
	    this.shader = shader;
	    this.camera = camera;
	    this.light = light;
	    this.textureId1 = textureId1;
	    this.textureId2 = textureId2;
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, Light light2) {
		this.mesh = mesh;
		this.material = material;
		this.modelMatrix = modelMatrix;
		this.shader = shader;
		this.camera = camera;
		this.light = light;
		this.textureId1 = textureId1;
		this.textureId2 = textureId2;
		this.light2 = light2;
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, int[] textureId3) {
	  this.mesh = mesh;
	  this.material = material;
	  this.modelMatrix = modelMatrix;
	  this.shader = shader;
	  this.camera = camera;
	  this.light = light;
	  this.textureId1 = textureId1;
	  this.textureId2 = textureId2;
	  this.textureId3 = textureId3;
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, int[] textureId3, Light light2) {
	  this.mesh = mesh;
	  this.material = material;
	  this.modelMatrix = modelMatrix;
	  this.shader = shader;
	  this.camera = camera;
	  this.light = light;
	  this.textureId1 = textureId1;
	  this.textureId2 = textureId2;
	  this.textureId3 = textureId3;
	  this.light2 = light2;
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, int[] textureId3, int[] textureId4) {
	  this.mesh = mesh;
	  this.material = material;
	  this.modelMatrix = modelMatrix;
	  this.shader = shader;
	  this.camera = camera;
	  this.light = light;
	  this.textureId1 = textureId1;
	  this.textureId2 = textureId2;
	  this.textureId3 = textureId3;
	  this.textureId4 = textureId4;
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, int[] textureId3, int[] textureId4, Light light2) {
	  this.mesh = mesh;
	  this.material = material;
	  this.modelMatrix = modelMatrix;
	  this.shader = shader;
	  this.camera = camera;
	  this.light = light;
	  this.textureId1 = textureId1;
	  this.textureId2 = textureId2;
	  this.textureId3 = textureId3;
	  this.textureId4 = textureId4;
	  this.light2 = light2;
  }
  
  // add constructors without modelMatrix? and then set to identity as the default?
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void setLight(Light light) {
    this.light = light;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());
    shader.setVec3(gl, "light.direction", light.getFront());
    shader.setFloat(gl, "light.cutOff", (float) Math.cos(Math.toRadians(15f)));
    
    if (light2 != null) {
    	shader.setVec3(gl, "light2.position", light2.getPosition());
        shader.setVec3(gl, "light2.ambient", light2.getMaterial().getAmbient());
        shader.setVec3(gl, "light2.diffuse", light2.getMaterial().getDiffuse());
        shader.setVec3(gl, "light2.specular", light2.getMaterial().getSpecular());  
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    if (textureId3!=null) {
        shader.setInt(gl, "third_texture", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId3[0]);
    }
    if (textureId4!=null) {
        shader.setInt(gl, "fourth_texture", 1);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId4[0]);
    }
    mesh.render(gl);
  } 
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
    if (textureId3!=null) gl.glDeleteBuffers(1, textureId3, 0);
  }
  
}