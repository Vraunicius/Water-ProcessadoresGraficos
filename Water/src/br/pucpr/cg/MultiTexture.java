package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import br.pucpr.mage.FrameBuffer;
import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Mesh;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Shader;
import br.pucpr.mage.Texture;
import br.pucpr.mage.Window;
import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.MultiTextureMaterial;
import br.pucpr.mage.phong.NormalMapMaterial;
import br.pucpr.mage.phong.PhongMaterial;
import br.pucpr.mage.postfx.PostFXMaterial;

public class MultiTexture implements Scene {
    private static final String PATH = "/Users/Pichau/Desktop/img/opengl/";
    
    private Keyboard keys = Keyboard.getInstance();
    
    //Dados da cena
    private Camera camera = new Camera();
    private DirectionalLight light;
    private DirectionalLight luz;
    
    //Dados da malha
    private Mesh mesh;
    
    //private MultiTextureMaterial material; 
    private PhongMaterial material2; 
    private PhongMaterial materialini; 
    private NormalMapMaterial materialNM;
    private Texture text;
    private NormalMapMaterial materialNM2;
    private Texture text2;
    
    private Mesh canvas;
    private FrameBuffer fb;
    private PostFXMaterial postFX;
    private Mesh Chao;
    private Mesh Parede[] = new Mesh[4];
    
    //Coisas Inimigos
    private Mesh Inimigo[] = new Mesh[10];
    private Vector3f pos_ini[] = new Vector3f[10];
    Vector3f desiredVelocity[] = new Vector3f[10];
	private boolean atingido[] = new boolean[10];
	private boolean persegue[] = new boolean[10];
	Vector3f Menor_dist = new Vector3f();
	Vector3f distance = new Vector3f();
	Vector3f distance_real[] = new Vector3f[10];
	private int indice_ini;
	private float angle_ini[] = new float[10] ;
	private Vector3f points[] = new Vector3f[4];
	
	//Coisas arma
	private Mesh mesh_arma;
	private Vector3f pos_arma = new Vector3f();
	private Matrix4f mat_arma = new Matrix4f();
    private boolean animation = false;
	private float angle = 0;
    private boolean subindo = false, recarga = false, puxada = false;
	
    
    private Matrix4f mat_camera = new Matrix4f();
    
    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glPolygonMode(GL_FRONT_FACE, GL_LINE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //-0.2f, 0.3f, 0.8f
       
        camera.getPosition().set(0.f, 2.0f, 0.f);
        pos_arma.set(0.2f, -0.3f, -0.8f);
        Menor_dist.set(999, 999, 999);
        
        for (int i = 0; i < 10; i ++)
        {
        	pos_ini[i] = new Vector3f();
        	desiredVelocity[i] = new Vector3f();
        	distance_real[i] = new Vector3f();
        	Inimigo[i] = MeshFactory.createCube(7.25f, 12.25f, 7.25f);
        	pos_ini[i].set(265.08377f - (i * 24), -5.0f, -218.2655f);
        	atingido[i] = false;
        	persegue[i] = true;
        }
       
        points[0] = new Vector3f(-206, -5,  213);
        points[1] = new Vector3f(-230, -5,  220);
        points[2] = new Vector3f( 220, -5, -230);
        points[3] = new Vector3f( 245, -5,  230);
        
        mesh_arma = MeshFactory.createCube(0.1f, 0.11f, 0.7f);
        
        Parede[0] = MeshFactory.createSquareWithNormalMap(600, 600, 0);
        Parede[1] = MeshFactory.createSquareWithNormalMap(600, 600, 0);
        Parede[2] = MeshFactory.createSquareWithNormalMap(600, 600, 0);
        Parede[3] = MeshFactory.createSquareWithNormalMap(600, 600, 0);
        
        Chao = MeshFactory.createSquareWithNormalMap(500, 500, 0);
        
        light = new DirectionalLight(
                new Vector3f(-1.0f, -1.0f, -1.0f),    //direction
                new Vector3f( 0.1f,  0.1f,  0.1f), 	  //ambient
                new Vector3f( 1.0f,  1.0f,  1.0f),    //diffuse
                new Vector3f( 1.0f,  1.0f,  1.0f));   //specular
        try {
            mesh = MeshFactory.loadTerrain(new File(PATH + "heights/island.png"), 1.f, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        
       luz = new DirectionalLight(
                new Vector3f( 1.0f, -1.0f,  1.0f),    //direction
                new Vector3f( 0.1f,  0.1f,  0.1f), 	  //ambient
                new Vector3f( 1.0f,  1.0f,  1.0f),    //diffuse
                new Vector3f( 1.0f,  1.0f,  1.0f));   //specular
        try {
            mesh = MeshFactory.loadTerrain(new File(PATH + "heights/island.png"), 1.f, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        
        material2 = new PhongMaterial(
        		 new Vector3f(1.0f, 1.0f, 1.0f), //ambient
                 new Vector3f(0.9f, 0.9f, 0.9f), //diffuse
                 new Vector3f(0.0f, 0.0f, 0.0f), //specular
                 0.0f);                          //specular power
        
        material2.setTexture("uTexture" ,new Texture(PATH + "textures/hand.png"));
        
        materialini = new PhongMaterial(
       		    new Vector3f(1.0f, 1.0f, 1.0f), //ambient
                new Vector3f(0.9f, 0.9f, 0.9f), //diffuse
                new Vector3f(0.0f, 0.0f, 0.0f), //specular
                0.0f);                          //specular power
       
        materialini.setTexture("uTexture" ,new Texture(PATH + "textures/dog.png"));
        
        text = new Texture((PATH + "textures/stone_t.png"));
        materialNM = new NormalMapMaterial(
                new Vector3f(1.0f, 1.0f, 1.0f), //ambient
                new Vector3f(0.9f, 0.9f, 0.9f), //diffuse
                new Vector3f(0.0f, 0.0f, 0.0f), //specular
                64.0f);                          //specular power
        materialNM.setTexture(text);
        materialNM.setNormalMap(new Texture(PATH + "normals/stone_n.png"));
    
        text2 = new Texture((PATH + "textures/bricks_t.jpg"));
        materialNM2 = new NormalMapMaterial(
                new Vector3f(1.0f, 1.0f, 1.0f), //ambient
                new Vector3f(0.9f, 0.9f, 0.9f), //diffuse
                new Vector3f(0.0f, 0.0f, 0.0f), //specular
                64.0f);                          //specular power
        materialNM2.setTexture(text2);
        materialNM2.setNormalMap(new Texture(PATH + "normals/bricks_n.jpg"));
        
        
        canvas = MeshFactory.createCanvas();
        fb = FrameBuffer.forCurrentViewport();
        postFX = PostFXMaterial.defaultPostFX("HDR", fb);
    }

    @Override
    public void update(float secs) {
        
        //SEEK
        for (int i = 0; i < 10; i++)
        {
        	distance = pos_ini[i].sub(camera.getPosition(), distance);
        	distance_real[i] = new Vector3f(distance);
        	
        	if (distance_real[i].length() >= 25 || distance_real[i].length() <= -25){
        		desiredVelocity[i] = camera.getPosition().sub(pos_ini[i], desiredVelocity[i]);
		    	desiredVelocity[i].normalize();
		    	pos_ini[i].add(desiredVelocity[i].mul(0.5f), pos_ini[i]);
        	}

            angle_ini[i] = (float) Math.atan2((double)distance_real[i].z, (double)distance_real[i].x);
	    }
     
        
        if (animation)
        {
        	if (angle >= 60)
        		subindo = false;
        	if (!subindo && !recarga)
        		angle -= 100 * secs;
        	if (angle < 0)
        	{
            	angle = 1;
        		recarga = true;
        		puxada = true;
        	}
        	if (subindo  && !recarga)
        		angle += 340 * secs;
        	
        	
        	if(recarga && puxada)
        		pos_arma.z += 2 * secs;
        	if (pos_arma.z >= -0.5f)
        		puxada = false;
        	if (recarga && !puxada)
        		pos_arma.z -= 2 * secs;
        	if (pos_arma.z < -0.8f && !puxada && recarga)
        	{
        		pos_arma.z = -0.8f;
        		recarga = false;
        		animation = false;
        	}
        }
        
        
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }
        
        if (keys.isDown(GLFW_KEY_W)) {
           camera.moveFront(20.0f);
        }

        if (keys.isDown(GLFW_KEY_S)) {
        	camera.moveFront(-20.0f);
        }
        
        if(keys.isDown(GLFW_KEY_A))
        {
        	camera.strafeLeft(10.0f);
        }
        
        if(keys.isDown(GLFW_KEY_D))
        {
        	camera.strafeRight(10.0f);
        }
        
        if(keys.isDown(GLFW_KEY_LEFT))
        {
        	camera.rotateY((float)Math.toRadians(60) * secs);
        }
        
        if(keys.isDown(GLFW_KEY_RIGHT))
        {
        	camera.rotateY((float)-Math.toRadians(60) * secs);
        }
        
        if(keys.isDown(GLFW_KEY_F))
        {
        	System.out.println(camera.getPosition().x +  " " +  camera.getPosition().z);
        }
        
        if (keys.isPressed(GLFW_KEY_SPACE) && !animation)
        {
        	animation = true;
        	subindo = true;
        	
        	Vector3f dir_camera = new Vector3f(camera.getDirection());
        	dir_camera.normalize();
        	for (int i = 0; i < 10; i ++){
	        	distance.normalize();
	        	
	        	
	        	float angle_tiro = distance.dot(dir_camera);
	        	angle_tiro = (float) Math.toDegrees(angle_tiro);
	        	
	        	//System.out.println(coisa);
	        	//System.out.println(distance_real.length());
	    		if ((angle_tiro >= 55.8f && angle_tiro <= 57.6) &&
	    			(distance_real[i].length() <= 160) && (distance_real[i].length() >= 0))
	        	{
	    			atingido[i] = true;
	        	}
	    	
	    	}
        	
        	
        	for (int i = 0; i < 10; i++){
        		if (atingido[i] == true && distance_real[i].length() <= Menor_dist.length())
        		{
        			Menor_dist.set(distance_real[i]);
        			indice_ini = i;
        		}
        	}

            Random gerador = new Random();
            int numero = gerador.nextInt(4);
            
        	pos_ini[indice_ini].set(points[numero]);
        	Menor_dist.set(999, 999, 999);
        	indice_ini = 0;
        }
        
        if (camera.getPosition().x >= 248.f)
        	camera.getPosition().set(camera.getPosition().x - 100 * secs, camera.getPosition().y, camera.getPosition().z);
        if (camera.getPosition().x <= -240.f)
        	camera.getPosition().set(camera.getPosition().x + 100 * secs, camera.getPosition().y, camera.getPosition().z);
        if (camera.getPosition().z <= -241.f)
        	camera.getPosition().set(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z  + 100 * secs);
        if (camera.getPosition().z >= 245.7f)
        	camera.getPosition().set(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z  - 100 * secs);
     		
        
        new Matrix4f().mul(new Matrix4f().translation(camera.getPosition())
        			  .mul(new Matrix4f().rotationY(camera.angle)),mat_camera);
        mat_camera.mul(new Matrix4f().translation(pos_arma), mat_arma);
    }

    public void drawScene() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        Shader shader = materialNM.getShader();
        shader.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", camera.getViewMatrix())
            .setUniform("uCameraPosition", camera.getPosition());        
        light.apply(shader);        
        shader.unbind();
        
        Chao.setUniform("uWorld", new Matrix4f().translate(0,-20,0).rotateY((float)Math.toRadians(90)));
        Chao.draw(materialNM);
        
        Shader shaderParede = materialNM2.getShader();
        shaderParede.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", camera.getViewMatrix())
            .setUniform("uCameraPosition", camera.getPosition());        
        light.apply(shaderParede);        
        shaderParede.unbind();
        
        Parede[0].setUniform("uWorld", new Matrix4f().translate(-240,-20,-37.f).rotateXYZ((float)Math.toRadians(-90), 0, (float)Math.toRadians(-90)));
        Parede[0].draw(materialNM2);
        Parede[2].setUniform("uWorld", new Matrix4f().translate(28,-20,-241.3f).rotateXYZ((float)Math.toRadians(90), (float)Math.toRadians(180), 0));
        Parede[2].draw(materialNM2);
        
        Shader shaderParede2 = materialNM2.getShader();
        shaderParede2.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", camera.getViewMatrix())
            .setUniform("uCameraPosition", camera.getPosition());        
        luz.apply(shaderParede2);        
        shaderParede2.unbind();
        
        Parede[1].setUniform("uWorld", new Matrix4f().translate(248,-20,-37.f).rotateXYZ((float)Math.toRadians(-90), 0, (float)Math.toRadians(90)));
        Parede[1].draw(materialNM2);
        Parede[3].setUniform("uWorld", new Matrix4f().translate(2,-20,245.7f).rotateXYZ((float)Math.toRadians(-90), (float)Math.toRadians(180), 0));
        Parede[3].draw(materialNM2);
        
        Shader shader2 = material2.getShader();
        shader2.bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", camera.getViewMatrix())
            .setUniform("uCameraPosition", camera.getPosition());        
        light.apply(shader2);        
        shader2.unbind();
        
        mesh_arma.setUniform("uWorld", mat_arma.rotateX((float)Math.toRadians(angle)));
        mesh_arma.draw(material2);
        
        for (int i = 0; i < 10; i ++){
	        Shader shaderini[] = new Shader[10];
	        shaderini[i] =	materialini.getShader();
	        shaderini[i].bind()
	            .setUniform("uProjection", camera.getProjectionMatrix())
	            .setUniform("uView", camera.getViewMatrix())
	            .setUniform("uCameraPosition", camera.getPosition());        
	        light.apply(shaderini[i]);        
	        shaderini[i].unbind();
	
	        Inimigo[i].setUniform("uWorld", new Matrix4f().translate(pos_ini[i]).rotateY(-angle_ini[i]));
	        Inimigo[i].draw(materialini);
        }
    }
    
    @Override
    public void draw() {        
        fb.bind();
        drawScene();
        fb.unbind();
        
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);        
        Shader shader = postFX.getShader();
        shader.bind()
        .setUniform("tonalidade", 1.0f);
        shader.unbind();
        canvas.draw(postFX);
    }

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {        
        new Window(new MultiTexture(), "Multi texturing", 1024, 748).show();
    }
}
