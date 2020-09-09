import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/* I declare that this code is my own work */
/* Author Alistair Cook - adcook1@sheffield.ac.uk */

public class Main extends JFrame implements ActionListener {
	  private static final int WIDTH = 1024;
	  private static final int HEIGHT = 768;
	  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
	  private GLCanvas canvas;
	  private GLEventListener glEventListener;
	  private final FPSAnimator animator;
	  
	  public static void main(String[] args) {
		  Main f = new Main("Snowy Scene");
		  f.getContentPane().setPreferredSize(dimension);
		  f.pack();
		  f.setVisible(true);
	  }
	  
	  public Main(String textForTitleBar) {
		  super(textForTitleBar);
		  GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
		  canvas = new GLCanvas(glcapabilities);
		  Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
		  glEventListener = new Main_GLEventListener(camera);					
		  canvas.addGLEventListener(glEventListener);
		  canvas.addMouseMotionListener(new MyMouseInput(camera));
		  canvas.addKeyListener(new MyKeyboardInput(camera));
		  getContentPane().add(canvas, BorderLayout.CENTER);
		  
		  // The control button panel
		  int buttonHeight = 26;
		  JPanel container = new JPanel();
		  JPanel lights = new JPanel();
		      JButton button = new JButton("Global Light");
		      button.addActionListener(this);
		      button.setPreferredSize(new Dimension(160, buttonHeight));
		      lights.add(button);
		      button = new JButton("Spotlight");
		      button.addActionListener(this);
		      button.setPreferredSize(new Dimension(160, buttonHeight));
		      lights.add(button);
		  JPanel animate = new JPanel();
		      button = new JButton("Roll");
		      button.addActionListener(this);
		      button.setPreferredSize(new Dimension(80, buttonHeight));
		      animate.add(button);
		      button = new JButton("Rock");
		      button.addActionListener(this);
		      button.setPreferredSize(new Dimension(80, buttonHeight));
		      animate.add(button);
		      button = new JButton("Slide");
		      button.addActionListener(this);
		      button.setPreferredSize(new Dimension(80, buttonHeight));
		      animate.add(button);
		      button = new JButton("Slide, Rock and Roll");
		      button.addActionListener(this);
		      animate.add(button);
		      button = new JButton("Reset All");
		      button.addActionListener(this);
		      animate.add(button);
		  TitledBorder lightTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Toggle Light Dimming");
		  lightTitle.setTitleJustification(TitledBorder.CENTER);
		  lights.setBorder(lightTitle);
		  TitledBorder animateTitle = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Animate Snowman");
		  animateTitle.setTitleJustification(TitledBorder.CENTER);
		  animate.setBorder(animateTitle);
		  
		  container.setLayout(new GridLayout(1,2));
		  container.add(lights);
		  container.add(animate);
		  this.add(container, BorderLayout.SOUTH);

		  addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent e) {
				  animator.stop();
				  remove(canvas);
				  dispose();
				  System.exit(0);
			  }	
		  });
		  animator = new FPSAnimator(canvas, 60);
		  animator.start();
	  }
	  
	  // Calling the action functions
	  public void actionPerformed(ActionEvent event) {
		      if (event.getActionCommand().equalsIgnoreCase("Global Light")) {
		          ((Main_GLEventListener) glEventListener).globalLightToggle();
		      } else if (event.getActionCommand().equalsIgnoreCase("Spotlight")) {
		          ((Main_GLEventListener) glEventListener).lightToggle();
		      }  else if (event.getActionCommand().equalsIgnoreCase("Roll")) {
		    	  ((Main_GLEventListener) glEventListener).toggleRoll();
		      } else if (event.getActionCommand().equalsIgnoreCase("Rock")) {
		          ((Main_GLEventListener) glEventListener).toggleRock();
		      } else if (event.getActionCommand().equalsIgnoreCase("Slide")) {
		    	  ((Main_GLEventListener) glEventListener).toggleSlide();
		      } else if (event.getActionCommand().equalsIgnoreCase("Slide, Rock and Roll")) {
		          ((Main_GLEventListener) glEventListener).enableAll();
		      } else if (event.getActionCommand().equalsIgnoreCase("Reset All")) {
		          ((Main_GLEventListener) glEventListener).stopAll();
		      }
		}
}

// Camera control
class MyKeyboardInput extends KeyAdapter  {
	private Camera camera; 
	public MyKeyboardInput(Camera camera) {
	  this.camera = camera;
	}
	public void keyPressed(KeyEvent e) {
		Camera.Movement m = Camera.Movement.NO_MOVEMENT;
		switch (e.getKeyCode()) {
	    	case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
	    	case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
	    	case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
	    	case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
	    	case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
	    	case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
	    }
	camera.keyboardInput(m);
	}
}

class MyMouseInput extends MouseMotionAdapter {
	private Point lastpoint;
	private Camera camera;
	float dx = 492;
	public MyMouseInput(Camera camera) {
		this.camera = camera;
	}
	  
	public void mouseDragged(MouseEvent e) {
	    Point ms = e.getPoint();
	    float sensitivity = 0.001f;
	    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
	    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
	    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
	    	camera.updateYawPitch(dx, -dy);
	    lastpoint = ms;
	}

	public void mouseMoved(MouseEvent e) {   
		  lastpoint = e.getPoint(); 
	}
}