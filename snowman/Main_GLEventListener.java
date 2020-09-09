import snowman.gmaths.*;
import com.jogamp.opengl.*;

/* I declare that this code is my own work */
/* Author Alistair Cook - adcook1@sheffield.ac.uk */

public class Main_GLEventListener implements GLEventListener {
	public Main_GLEventListener(Camera camera) {
	    this.camera = camera;
	    this.camera.setPosition(new Vec3(-2f,6f,25f));
	    this.camera.setTarget(new Vec3(-5f,5f,-10f));
	}
	
	public void init(GLAutoDrawable drawable) {
	    GL3 gl = drawable.getGL().getGL3();
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
	    gl.glClearDepth(1.0f);
	    gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glDepthFunc(GL.GL_LESS); 
	    gl.glFrontFace(GL.GL_CCW);
	    gl.glEnable(GL.GL_CULL_FACE);
	    gl.glCullFace(GL.GL_BACK);
	    initialise(gl);
	    startTime = getSeconds();
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	    GL3 gl = drawable.getGL().getGL3();
	    gl.glViewport(x, y, width, height);
	    float aspect = (float)width/(float)height;
	    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
	}
	
	public void display(GLAutoDrawable drawable) {
	    GL3 gl = drawable.getGL().getGL3();
		render(gl);
	}
	
	public void dispose(GLAutoDrawable drawable) {
	    GL3 gl = drawable.getGL().getGL3();
	    disposeModels(gl);
	}
	
	private Camera camera;
	private Model snowmanObj, stoneObj, carrotNose, capSphere1, capSphere2,
	signObj, plane1, plane2, securityLight, poleObj;
	private Light light, globalLight;
	private SGNode snowmanRoot, signRoot, lightRoot;
	float angle, angle2, rollAngle, slideX, slideY;
	private TransformNode rotateBody, tiltHead, rollHead, slideNode, lightTransform2;
	private TransformNode headTranslate1, headTranslate2, headTranslate3;
	
	private NameNode button1, button2, button3;
	private ModelNode button1Shape, button2Shape, button3Shape;
	private TransformNode button1Transform, button2Transform, button3Transform;
	
	private NameNode bottom, head;
	private TransformNode bottomTransform, headTransform;
	private ModelNode bottomShape, headShape;
	
	private NameNode eye1, eye2, mouth, nose;
	private TransformNode eye1Transform, eye2Transform, mouthTransform, noseTransform;
	private ModelNode eye1Shape, eye2Shape, mouthShape, noseShape;
	
	private NameNode cap1, cap2, cap3;
	private TransformNode cap1Transform, cap2Transform, cap3Transform;
	private ModelNode cap1Shape, cap2Shape, cap3Shape;
	
	private Mat4 modeltransform, modelMatrix;
	private Mesh sphereMesh, cubeMesh, plane;
	private Shader sphereShader, animShader, cubeShader, planeShader;
	private Material material;
	
	float offsetX;
	private boolean enableRock = false;
	private boolean enableRoll = false;
	private boolean enableSlide = false;
	private int[] textureId1,
	textureId2, textureId3,
	textureId4, textureId5,
	textureId6, textureId7,
	textureId8, textureId9,
	textureId10, textureId11,
	textureId12, textureId13,
	textureId14, textureId15,
	textureId16, textureId17,
	textureId18, textureId19;
	private NameNode sign, leg1, leg2;
	private TransformNode signTransform, leg1Transform, leg2Transform;
	private ModelNode signShape, leg1Shape, leg2Shape;
	private NameNode pole;
	private TransformNode poleTransform;
	private ModelNode poleShape;
	private TransformNode lightRotate;
	private TransformNode lightTransform;
	private ModelNode lightShape;
	private NameNode lightNode;
	
	private void getTextures(GL3 gl) {
		// Loading the textures
	    textureId1 = TextureLibrary.loadTexture(gl, "textures/snow.jpg");
	    textureId2 = TextureLibrary.loadTexture(gl, "textures/snow_specular.jpg");
	    textureId3 = TextureLibrary.loadTexture(gl, "textures/stone.jpg");
	    textureId4 = TextureLibrary.loadTexture(gl, "textures/stone_specular.jpg");
	    textureId5 = TextureLibrary.loadTexture(gl, "textures/carrot.jpg");
	    textureId6 = TextureLibrary.loadTexture(gl, "textures/carrot_specular.jpg");
	    textureId7 = TextureLibrary.loadTexture(gl, "textures/cap1.jpg");
	    textureId8 = TextureLibrary.loadTexture(gl, "textures/cap1_specular.jpg");
	    textureId9 = TextureLibrary.loadTexture(gl, "textures/cap2.jpg");
	    textureId10 = TextureLibrary.loadTexture(gl, "textures/sign.jpg");
	    textureId11 = TextureLibrary.loadTexture(gl, "textures/sign_specular.jpg");
	    textureId12 = TextureLibrary.loadTexture(gl, "textures/sign_diff.jpg");
	    textureId13 = TextureLibrary.loadTexture(gl, "textures/house.jpg");
	    textureId14 = TextureLibrary.loadTexture(gl, "textures/house_specular.jpg");
	    textureId15 = TextureLibrary.loadTexture(gl, "textures/steel.jpg");
	    textureId16 = TextureLibrary.loadTexture(gl, "textures/steel_specular.jpg");
	    textureId17 = TextureLibrary.loadTexture(gl, "textures/snowFloor.jpg");
	    textureId18 = TextureLibrary.loadTexture(gl, "textures/snowFloor_specular.jpg");
	    textureId19 = TextureLibrary.loadTexture(gl, "textures/fallingSnow.jpg");
	}
	
	private void setLights(GL3 gl) {
		// Initiating the lights in the scene
	    light = new Light(gl);
	    light.setCamera(camera);
	    globalLight = new Light(gl);
	    globalLight.setCamera(camera);
	}
	
	private void shaders(GL3 gl) {
		sphereShader = new Shader(gl, "vs_sphere.txt", "fs_sphere.txt");
		cubeShader = new Shader(gl, "vs_cube.txt", "fs_cube.txt");
		animShader = new Shader(gl, "vs_planeAnim.txt", "fs_planeAnim.txt");
		planeShader = new Shader(gl, "vs_plane.txt", "fs_plane.txt");
	}
	
	private void createMesh(GL3 gl) {
	    // Setting up Meshes, materials and objects
		shaders(gl);
	    sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
	    cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
	    plane = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
	    material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), 2f);
	    setModels(gl);
	}
	
	private void setModels(GL3 gl) {
		modelMatrix = Mat4.multiply(Mat4Transform.scale(3,3,3), Mat4Transform.translate(0,0.5f,0));
	    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,4,0), modelMatrix);
	    snowmanObj = new Model(gl, camera, light, sphereShader, material, modelMatrix, sphereMesh, textureId1, textureId2, textureId1, globalLight);
	    stoneObj = new Model(gl, camera, light, sphereShader, material, modelMatrix, sphereMesh, textureId3, textureId4, globalLight);
	    carrotNose = new Model(gl, camera, light, sphereShader, material, modelMatrix, sphereMesh, textureId5, textureId6, globalLight);
	    capSphere1 = new Model(gl, camera, light, sphereShader, material, modelMatrix, sphereMesh, textureId7, textureId8, globalLight);
	    capSphere2 = new Model(gl, camera, light, sphereShader, material, modelMatrix, sphereMesh, textureId9, textureId8, globalLight);
	    securityLight = new Model(gl, camera, light, sphereShader, material, modelMatrix, sphereMesh, textureId15, textureId15, textureId15, globalLight);
	    poleObj = new Model(gl, camera, light, cubeShader, material, modelMatrix, cubeMesh, textureId15, textureId16, textureId15, globalLight);
	    signObj = new Model(gl, camera, light, cubeShader, material, modelMatrix, cubeMesh, textureId10, textureId11, textureId12, globalLight);
	}
	
	private void disposeModels(GL3 gl) {
		snowmanObj.dispose(gl);
		stoneObj.dispose(gl);
		carrotNose.dispose(gl);
		capSphere1.dispose(gl);
		capSphere2.dispose(gl);
		securityLight.dispose(gl);
		poleObj.dispose(gl);
		signObj.dispose(gl);
		plane1.dispose(gl);
		plane2.dispose(gl);
	}
	
	public void initialise(GL3 gl) {
		// Initiating models, textures and meshes
		getTextures(gl);
		setLights(gl);
		createMesh(gl);
		drawPlanes(gl);

		// Model transformation functions
	    setSnowmanTransforms();
	    setCapTransforms();
	    setSignTransforms();
	    setLightTransforms();
	    
	    // Building model hierarchy
	    snowmanTree();
	    signTree();
	    lightTree();
	}
	
	public void render(GL3 gl) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// Setting up the lights
		globalLight.setPosition(new Vec3((float) 20,(float) 20,(float) 0));
		globalLight.render(gl);
		light.setFront(getLightTarget());
		light.setPosition(new Vec3((float) -10,(float) 10,(float) 10));
		
		// Animated texture
		double elapsedTime = getSeconds() - startTime;
	    animShader.use(gl);
	    double t = elapsedTime*0.1;
	    float offsetX = (float)(-(t - Math.floor(t)));
	    float offsetY = (float)(t - Math.floor(t));
	    animShader.setFloat(gl, "offset", offsetX, offsetY);
	    animShader.setInt(gl, "first_texture", 0);
	    animShader.setInt(gl, "fourth_texture", 1);
	    
	    // Animation functions
	    rock();
	    roll();
	    slide();
	    light();
	    
	    // Scene floor and back plane
	    plane1.render(gl);
	    plane2.render(gl);
	    
	    // Rendering the objects in the scene
	    snowmanRoot.draw(gl);
	    signRoot.draw(gl);
	    lightRoot.draw(gl);
	}
	
	private void drawPlanes(GL3 gl) {
		// Composing the floor and background planes
	    modelMatrix = Mat4.multiply(Mat4Transform.translate(-5f,15f,-10f), Mat4Transform.rotateAroundX(90));
	    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(50f,1f,30f));
	    plane1 = new Model(gl, camera, light, animShader, material, modelMatrix, plane, textureId13, textureId14, textureId13, textureId19, globalLight);
	    modelMatrix = Mat4Transform.translate(-5f,0,0);
	    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(50,1f,50));
	    plane2 = new Model(gl, camera, light, planeShader, material, modelMatrix, plane, textureId17, globalLight);
	}
	
	private void setSnowmanTransforms() {
		// Composing the snowman and all subfeatures
	    snowmanRoot = new NameNode("snowman structure");
	    bottom = new NameNode("bottom");
	    bottomTransform = new TransformNode("translate(0,2f,0)", 
	    		Mat4.multiply(Mat4Transform.scale(4f,4f,4f), Mat4Transform.translate(0,0.5f,0)));
	    bottomShape = new ModelNode("Base", snowmanObj);

	    head = new NameNode("head");
	    headTransform = new TransformNode("scale(3f,3f,3f);translate(0,0.5f,0)", 
	    		Mat4.multiply(Mat4Transform.scale(3f,3f,3f), Mat4Transform.translate(0,0.5f,0)));
	    headShape = new ModelNode("Head", snowmanObj);
	    
	    eye1 = new NameNode("eye1");
	    eye1Transform = new TransformNode("scale(0.5f,0.5f,0.5f);translate(1.1f,0f,2.5f)", 
	    		Mat4.multiply(Mat4Transform.scale(0.5f,0.5f,0.5f), Mat4Transform.translate(1.1f,0f,2.5f)));
	    eye1Shape = new ModelNode("Eye", stoneObj);
	    
	    eye2 = new NameNode("eye2");
	    eye2Transform = new TransformNode("scale(0.5f,0.5f,0.5f);translate(-1.1f,0f,2.5f)", 
	    		Mat4.multiply(Mat4Transform.scale(0.5f,0.5f,0.5f), Mat4Transform.translate(-1.1f,0f,2.5f)));
	    eye2Shape = new ModelNode("Eye", stoneObj);
	    
	    mouth = new NameNode("mouth");
	    mouthTransform = new TransformNode("scale(1f,0.5f,1f);translate(0,-2.2f,1f)", 
	    		Mat4.multiply(Mat4Transform.scale(1f,0.5f,1f), Mat4Transform.translate(0,-2.2f,1f)));
	    mouthShape = new ModelNode("Mouth", stoneObj);
	    
	    nose = new NameNode("nose");
	    noseTransform = new TransformNode("scale(0.5f,0.5f,2f);translate(0,-1f,0.6f)", 
	    		Mat4.multiply(Mat4Transform.scale(0.5f,0.5f,2f), Mat4Transform.translate(0,-1f,0.6f)));
	    noseShape = new ModelNode("Nose", carrotNose);
	    
	    button1 = new NameNode("button1");
	    button1Transform = new TransformNode("scale(0.5f,0.5f,0.5f);translate(0,5.5f,3.5f)", 
	    		Mat4.multiply(Mat4Transform.scale(0.5f,0.5f,0.5f), Mat4Transform.translate(0,5.5f,3.5f)));
	    button1Shape = new ModelNode("Button", stoneObj);
	    
	    button2 = new NameNode("button2");
	    button2Transform = new TransformNode("scale(0.5f,0.5f,0.5f);translate(0,4.2f,3.8f)", 
	    		Mat4.multiply(Mat4Transform.scale(0.5f,0.5f,0.5f), Mat4Transform.translate(0,4.2f,3.8f)));
	    button2Shape = new ModelNode("Button", stoneObj);
	    
	    button3 = new NameNode("button3");
	    button3Transform = new TransformNode("scale(0.5f,0.5f,0.5f);translate(0,2.9f,3.6f)",
	    		Mat4.multiply(Mat4Transform.scale(0.5f,0.5f,0.5f), Mat4Transform.translate(0,2.9f,3.6f)));
	    button3Shape = new ModelNode("Button", stoneObj);
	    
	    // Transformation variables to construct features of the snowman
	    headTranslate1 = new TransformNode("translate(0,2f,0)",Mat4Transform.translate(0,2f,0));
	    headTranslate2 = new TransformNode("translate(0,2f,0)",Mat4Transform.translate(0,2f,0));
	    headTranslate3 = new TransformNode("scale(0.333f,0.333f,0.333f)*translate(0,0.5f,0)",
	    		Mat4.multiply(Mat4Transform.scale(0.333f,0.333f,0.333f), Mat4Transform.translate(0,0.5f,0)));
	    tiltHead = new TransformNode("rotateAroundZ(angle)", Mat4Transform.rotateAroundZ(angle));
	    rollHead = new TransformNode("rotateAroundZ(angle)", Mat4Transform.rotateAroundZ(angle));
	    slideNode = new TransformNode("translate(slideX, slideY, 0f)", Mat4Transform.translate(0f,0f,0f));
	    rotateBody = new TransformNode("rotateAroundZ(angle)", Mat4Transform.rotateAroundZ(angle));
	}
	
	private void setCapTransforms() {
		// The snowman's cap
	    cap1 = new NameNode("cap1");
	    cap1Transform = new TransformNode("scale(2.7f,2.7f,2.7f);translate(0,-0.08f,0)", 
	    		Mat4.multiply(Mat4Transform.scale(2.7f,2.7f,2.7f), Mat4Transform.translate(0,-0.08f,0)));
	    cap1Shape = new ModelNode("Cap", capSphere1);
	    
	    cap2 = new NameNode("cap2");
	    modeltransform = Mat4Transform.rotateAroundY(330);
	    modeltransform = Mat4.multiply(modeltransform, Mat4Transform.scale(2.5f,0.2f,3f));
	    modeltransform = Mat4.multiply(modeltransform, Mat4Transform.translate(0,2.1f,0.3f));
	    cap2Transform = new TransformNode("rotateAroundY(330);scale(2.5f,0.2f,3f);translate(0,2.1f,0.3f);rotateAroundX(20)", 
	    		Mat4.multiply(modeltransform, Mat4Transform.rotateAroundX(20)));
	    cap2Shape = new ModelNode("CapShade", capSphere2);
	    
	    cap3 = new NameNode("cap3");
	    cap3Transform = new TransformNode("scale(0.5f,0.2f,0.5f);translate(0,5.5f,0)", 
	    		Mat4.multiply(Mat4Transform.scale(0.5f,0.2f,0.5f), Mat4Transform.translate(0,5.5f,0)));
	    cap3Shape = new ModelNode("CapButton", capSphere2);
	}
	
	private void setSignTransforms() {
		// Composing the sign next to the snowman
	    signRoot = new NameNode("sign structure");
	    sign = new NameNode("sign");
	    modeltransform = Mat4.multiply(Mat4Transform.rotateAroundY(45), Mat4Transform.scale(0.175f,2,4));
	    modeltransform = Mat4.multiply(modeltransform, Mat4Transform.translate(40f,1.5f,1f));
	    signTransform = new TransformNode("rotateAroundY(45)*scale(0.175f,2,4)*translate(40f,1.5f,1f)", modeltransform);
	    signShape = new ModelNode("Sign", signObj);
	    
	    leg1 = new NameNode("leg1");
	    modeltransform = Mat4.multiply(Mat4Transform.rotateAroundY(45), Mat4Transform.scale(0.15f,2f,0.15f));
	    modeltransform = Mat4.multiply(modeltransform, Mat4Transform.translate(46.6f,0.5f,15f));
	    leg1Transform = new TransformNode("rotateAroundY(45)*scale(0.15f,2f,0.15f)*translate(46.6f,0.5f,15f)", modeltransform);
	    leg1Shape = new ModelNode("SignLeg", poleObj);
	    
	    leg2 = new NameNode("leg2");
	    modeltransform = Mat4.multiply(Mat4Transform.rotateAroundY(45), Mat4Transform.scale(0.15f,2f,0.15f));
	    modeltransform = Mat4.multiply(modeltransform, Mat4Transform.translate(46.6f,0.5f,38f));
	    leg2Transform = new TransformNode("translate(0,2f,0)", modeltransform);
	    leg2Shape = new ModelNode("SignLeg", poleObj);
	}
	
	private void setLightTransforms() {
		// Composing the security light pole
	    lightRoot = new NameNode("Security-Light");
	    pole = new NameNode("pole");
	    modeltransform = Mat4Transform.scale(0.3f,10f,0.3f);
	    modeltransform = Mat4.multiply(modeltransform, Mat4Transform.translate(-33.5f,0.5f,33.5f));
	    poleTransform = new TransformNode("translate(0,2f,0)", modeltransform);
	    poleShape = new ModelNode("LightPole", poleObj);
	    
	    lightNode = new NameNode("light");
	    lightRotate = new TransformNode("translate(0,2f,0)", Mat4.multiply(Mat4Transform.rotateAroundX(-20), 
	    		Mat4Transform.scale(1f,1f,2.5f)));
	    lightTransform = new TransformNode("translate(-10f,10f,10f)", Mat4Transform.translate(-10f,10f,10f));
	    lightShape = new ModelNode("LightBox", securityLight);
	    lightTransform2 = new TransformNode("translate(slideX, slideY, 0f)", Mat4Transform.translate(0f,0f,0f));
	}
	
	private void snowmanTree() {
		// Snowman hierarchical model
	    snowmanRoot.addChild(bottom);
	    	bottom.addChild(slideNode);
	    		slideNode.addChild(rotateBody);
	    			rotateBody.addChild(bottomTransform);
	    				bottomTransform.addChild(bottomShape);
	    			rotateBody.addChild(headTranslate1);
	    				headTranslate1.addChild(tiltHead);
	    					tiltHead.addChild(headTranslate2);
	    				headTranslate2.addChild(head);
	    					head.addChild(headTransform);
	    						headTransform.addChild(rollHead);
	    							rollHead.addChild(headShape);
	    								rollHead.addChild(headTranslate3);
	    									headTranslate3.addChild(eye1);
	    										eye1.addChild(eye1Transform);
	    											eye1Transform.addChild(eye1Shape);
	    									headTranslate3.addChild(eye2);
	    										eye2.addChild(eye2Transform);
	    											eye2Transform.addChild(eye2Shape);
	    									headTranslate3.addChild(mouth);
	    										mouth.addChild(mouthTransform);
	    											mouthTransform.addChild(mouthShape);
	    									headTranslate3.addChild(nose);
	    										nose.addChild(noseTransform);
	    											noseTransform.addChild(noseShape);
	    									headTranslate3.addChild(cap1);
	    										hat();
	    			rotateBody.addChild(button1);
	    				button1.addChild(button1Transform);
	    					button1Transform.addChild(button1Shape);
	    			rotateBody.addChild(button2);
	    				button2.addChild(button2Transform);
	    					button2Transform.addChild(button2Shape);
	    			rotateBody.addChild(button3);
	    				button3.addChild(button3Transform);
	    					button3Transform.addChild(button3Shape);
	    snowmanRoot.update();
	}
	
	private void signTree() {
		// Object next to snowman hierarchical model
	    signRoot.addChild(sign);
	    	sign.addChild(signTransform);
	    		signTransform.addChild(signShape);
	    	sign.addChild(leg1);
	    		leg1.addChild(leg1Transform);
	    			leg1Transform.addChild(leg1Shape);
	    	sign.addChild(leg2);
	    		leg2.addChild(leg2Transform);
	    			leg2Transform.addChild(leg2Shape);
	    signRoot.update();
	}
	
	public void lightTree() {
		// Security spotlight hierarchical model
	    lightRoot.addChild(pole);
	    	pole.addChild(poleTransform);
	    		poleTransform.addChild(poleShape);
    		pole.addChild(lightNode);
    			lightNode.addChild(lightTransform);
    			lightTransform.addChild(lightTransform2);
					lightTransform2.addChild(lightRotate);
    					lightRotate.addChild(lightShape);
		lightRoot.update();
	}
	
	private void hat() {
		cap1.addChild(cap1Transform);
			cap1Transform.addChild(cap1Shape);
		cap1.addChild(cap2);
			cap2.addChild(cap2Transform);
				cap2Transform.addChild(cap2Shape);
		cap1.addChild(cap3);
			cap3.addChild(cap3Transform);
				cap3Transform.addChild(cap3Shape);
	}
	
	// Setting the direction the security light points to
	private Vec3 getLightTarget() {
		double elapsedTime = getSeconds()-startTime;
		float x = 1f;
	    float y = 2.5f;
	    float z = -12.0f*(float)(Math.cos(Math.toRadians(elapsedTime*25))) - 4.5f;
	    return new Vec3(x,y,z);   
	}
	
	// Moving the security light object
	private void light() {
		double elapsedTime = getSeconds()-startTime;
	    float angleY = 320+30.0f*(float)(Math.cos(Math.toRadians(elapsedTime*25))) - 4.5f;
	    lightTransform2.setTransform(Mat4Transform.rotateAroundY(angleY));
	    lightRoot.update();
	}
	
	// Sub-functions for enabling/disabling the animations
	public void toggleRock() {
		animStartTime = getSeconds();
		enableRock = true;
		enableRoll = false;
		enableSlide = false;
	}
	public void toggleRoll() {
		animStartTime = getSeconds();
		enableRoll = true;
		enableRock = false;
		enableSlide = false;
		
	}
	public void toggleSlide() {
		animStartTime = getSeconds();
		enableSlide = true;
		enableRock = false;
		enableRoll = false;
	}
	
	public void enableAll() {
		animStartTime = getSeconds();
		enableRock = true;
		enableRoll = true;
		enableSlide = true;
	}
	
	public void stopAll() {
		enableRock = false;
		enableRoll = false;
		enableSlide = false;
	}
	
	// Animation functions
	public void rock() {
		if (enableRock) {
			double elapsedTime = getSeconds()-animStartTime;
			angle = 20.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
			angle2 = 20.0f*(float)(Math.sin(Math.toRadians(elapsedTime*100)));
			rotateBody.setTransform(Mat4.multiply(Mat4Transform.rotateAroundZ(angle),Mat4Transform.rotateAroundX(angle2)));
			snowmanRoot.update();
		} else {
			rotateBody.setTransform(Mat4.multiply(Mat4Transform.rotateAroundZ(0),Mat4Transform.rotateAroundX(0)));
			snowmanRoot.update();
		}
	}
	
	public void roll() {
		if (enableRoll) {
			double elapsedTime = getSeconds()-animStartTime;
			rollAngle = 29.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
			tiltHead.setTransform(Mat4Transform.rotateAroundZ(rollAngle));
			rollHead.setTransform(Mat4Transform.rotateAroundZ(rollAngle));
			snowmanRoot.update();
		} else {
			tiltHead.setTransform(Mat4Transform.rotateAroundZ(0));
			rollHead.setTransform(Mat4Transform.rotateAroundZ(0));
			snowmanRoot.update();
		}
	}
	
	public void slide() {
		if (enableSlide) {
			double elapsedTime = getSeconds()-animStartTime;
			slideX = 2.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
			slideY = 2.0f*(float)(Math.sin(Math.toRadians(elapsedTime*100)));
			slideNode.setTransform(Mat4Transform.translate(-slideX,0f,slideY));
			snowmanRoot.update();
		} else {
			slideNode.setTransform(Mat4Transform.translate(0f,0f,0f));
			snowmanRoot.update();
		}
	}
	
	// Toggles to dim the lights
	public void globalLightToggle() {
		globalLight.enable();
	}
	
	public void lightToggle() {
		light.enable();
	}
	
	private double startTime;
	private double animStartTime;
	  
	private double getSeconds() {
		return System.currentTimeMillis()/1000.0;
	}
}
