package codesW23;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jdesktop.j3d.examples.sound.PointSoundBehavior;
import org.jdesktop.j3d.examples.sound.audio.JOALMixer;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.PointSound;
import org.jogamp.java3d.PositionInterpolator;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.vecmath.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;



public class game extends JPanel implements KeyListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static final int OBJ_NUM = 3;
	private static Main_Menu[] Opt = new Main_Menu[5];
	private static pacMan[] object3D = new pacMan[OBJ_NUM];
	private static PickTool pickTool;
	private Canvas3D canvas;
	
	/* a public function to build the base labeled with 'str' */
	public static TransformGroup Menu_Objects() {
		Opt[0] = new Option_Shapes();
		ColorString[] clr_str = new ColorString[4];
		String[] str = {"New Game","Maze2","Maze3", "Quit"};
		TransformGroup menuTG = new TransformGroup(); 
		clr_str[0] = new ColorString(str[0], CommonsVP.Yellow, 0.5,new Point3f(-str[0].length() / 3.5f, 2.0f, 0.5f));	
		clr_str[1] = new ColorString(str[1], CommonsVP.Yellow, 0.5,new Point3f(-str[1].length() / 3.5f, 0.68f, 0.5f));
		clr_str[2] = new ColorString(str[2], CommonsVP.Yellow, 0.5,new Point3f(-str[2].length() / 3.5f, -0.7f, 0.5f));	
		clr_str[3] = new ColorString(str[3], CommonsVP.Yellow, 0.5,new Point3f(-str[3].length() / 3.5f, -1.98f, 0.5f));
		menuTG.addChild(clr_str[0].position_Object());
		menuTG.addChild(clr_str[1].position_Object());
		menuTG.addChild(clr_str[2].position_Object());
		menuTG.addChild(clr_str[3].position_Object());
		menuTG.addChild(Opt[0].position_Object());
		return menuTG;
	}
	
	/* a function to build the content branch, including the fan and other environmental settings */
	public static BranchGroup create_Menu() {
		BranchGroup sceneBG = new BranchGroup();
		sceneBG.addChild(Menu_Objects());                  
		sceneBG.addChild(CommonsVP.add_Lights(CommonsVP.White, 1));
		pickTool = new PickTool( sceneBG );                  // allow picking of objects in 'sceneBG'
		pickTool.setMode(PickTool.GEOMETRY);                 // pick by bounding volume
		return sceneBG;
	}
	
	/* a function to create the desk fan */
	private static TransformGroup create_game() {
		TransformGroup fanTG = new TransformGroup();

		object3D[0] = new mazeMaking();                             // Making the Maze
		fanTG = object3D[0].position_Object();          
		object3D[1] = new pacmanBody();                             // Making the pacMan Body
		fanTG.addChild(object3D[1].position_Object());
		object3D[2] = new pacmanEyes();                             // Making the pacMan eyes
		object3D[1].add_Child(object3D[2].position_Object());       // Connecting the pacMan Body to pacMan Eyes
		
		return fanTG;
	}
	
	private static TransformGroup create_game1() {
		TransformGroup fanTG = new TransformGroup();

		object3D[0] = new maze1Making();                            // Making the Maze
		fanTG = object3D[0].position_Object();          
		object3D[1] = new pacmanBody();                             // Making the pacMan Body
		fanTG.addChild(object3D[1].position_Object());
		object3D[2] = new pacmanEyes();                             // Making the pacMan eyes
		object3D[1].add_Child(object3D[2].position_Object());       // Connecting the pacMan Body to pacMan Eyes
		
		return fanTG;
	}
	
	private static TransformGroup create_game2() {
		TransformGroup fanTG = new TransformGroup();

		object3D[0] = new maze2Making();                            // Making the Maze
		fanTG = object3D[0].position_Object();          
		object3D[1] = new pacmanBody();                             // Making the pacMan Body
		fanTG.addChild(object3D[1].position_Object());
		object3D[2] = new pacmanEyes();                             // Making the pacMan eyes
		object3D[1].add_Child(object3D[2].position_Object());       // Connecting the pacMan Body to pacMan Eyes
		
		return fanTG;
	}
	
	// Making the Audio
    /* a function to enable audio device via JOAL */
	private void enableAudio(SimpleUniverse simple_U) {
		JOALMixer mixer = null;		                         // create a null mixer as a joalmixer
		Viewer viewer = simple_U.getViewer();
		viewer.getView().setBackClipDistance(20.0f);         // make object(s) disappear beyond 20f 

		if (mixer == null && viewer.getView().getUserHeadToVworldEnable()) {
			mixer = new JOALMixer(viewer.getPhysicalEnvironment());
			if (!mixer.initialize()) {                       // add mixer as audio device if successful
				System.out.println("Open AL failed to init");
				viewer.getPhysicalEnvironment().setAudioDevice(null);
			}
		}
	}
		
	/* a function to create a PointSound at the origin of its reference frame */
	public static PointSound pointSound() {
		URL url = null;
		String filename = "pacManAudio.wav";
		try {
			url = new URL("file", "localhost", filename);
		} 
		catch (Exception e) {
			System.out.println("Can't open " + filename);
		}
		PointSound ps = new PointSound();                    // create and position a point sound
		PointSoundBehavior player = new PointSoundBehavior(ps, url, new Point3f(0.0f, 0.0f, 0.0f));
		player.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
		ps.setCapability(PointSound.ALLOW_ENABLE_WRITE);
		return ps;
	}
	
	//Adding Maze1 to BranchGroup
	public static BranchGroup create_Maze1() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		
		sceneTG.addChild(pointSound());
		sceneTG.addChild(create_game());
		sceneBG.addChild(sceneTG);
		sceneBG.addChild(CommonsVP.add_Lights(CommonsVP.White, 1));
		return sceneBG;
	}
	
	//Adding Maze2 to BranchGroup
	public static BranchGroup create_Maze2() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		
		sceneTG.addChild(pointSound());
		sceneTG.addChild(create_game1());
		sceneBG.addChild(sceneTG);
		sceneBG.addChild(CommonsVP.add_Lights(CommonsVP.White, 1));
		return sceneBG;
	}
	
	//Adding Maze3 to BranchGroup
	public static BranchGroup create_Maze3() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		
		sceneTG.addChild(pointSound());
		sceneTG.addChild(create_game2());
		sceneBG.addChild(sceneTG);
		sceneBG.addChild(CommonsVP.add_Lights(CommonsVP.White, 1));
		return sceneBG;
	}
	

    //Method to make the content
	public game(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);   	
		canvas.addKeyListener(this);  
		canvas.addMouseListener(this);
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		CommonsVP.define_Viewer(su, new Point3d(0.0d, 0.0d, 5.0d));
		
        
        enableAudio(su); // Making the sound
		sceneBG.addChild(CommonsVP.add_Lights(CommonsVP.White, 10));
		sceneBG.addChild(CommonsVP.key_Navigation(su));     // allow key navigation
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}
	
	//Main method to load Main menu in the frame
	public static void MainFrame() {
        // Set up the main frame and panel
        frame = new JFrame("Main Menu");
        frame.getContentPane().add(new game(create_Menu()));  // start the program
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
	
	//Main method to load Maze1 in the frame
	public static void Maze1Frame() {
		// Set up the main frame and panel
        JFrame Newframe = new JFrame("Maze1");
        Newframe.getContentPane().add(new game(create_Maze1()));  // start the program
        Newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Newframe.setSize(800, 600); // set the size explicitly
        Newframe.setVisible(true);
        frame.dispose();
    }
	
	//Main method to load Maze2 in the frame
	public static void Maze2Frame() {
        // Set up the main frame and panel
        JFrame Newframe = new JFrame("Maze2");
        Newframe.getContentPane().add(new game(create_Maze2()));  // start the program
        Newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Newframe.setSize(800, 600); // set the size explicitly
        Newframe.setVisible(true);
        frame.dispose();
    }
	
	//Main method to load Maze3 in the frame
	public static void Maze3Frame() {
        // Set up the main frame and panel
        JFrame Newframe = new JFrame("Maze3");
        Newframe.getContentPane().add(new game(create_Maze3()));  // start the program
        Newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Newframe.setSize(800, 600); // set the size explicitly
        Newframe.setVisible(true);
        frame.dispose();
    }
		
	// Main method
	public static void main(String[] args){
		MainFrame();
	}
	
	public void keyPressed(KeyEvent evt) {
   
		PositionInterpolator[] P = new PositionInterpolator[4];
		P[0] = pacmanBody.Pos_Int[0];
		P[1] = pacmanBody.Pos_Int[1];
		P[2] = pacmanBody.Pos_Int[2];
		P[3] = pacmanBody.Pos_Int[3];
		
	    if (evt.getKeyCode() == KeyEvent.VK_D) {
	    	//System.out.println("D is pressed");
	        P[0].getAlpha().resume();
	    } 
	    
	    /*if (evt.getKeyCode() == KeyEvent.VK_A) {
	    	//System.out.println("A is pressed");
	    	P[1].getAlpha().resume();
	    } */
	    
	    if (evt.getKeyCode() == KeyEvent.VK_W) {
	    	//System.out.println("W is pressed");
	    	P[2].getAlpha().resume();
	    } 
	   /* if (evt.getKeyCode() == KeyEvent.VK_S) {
	    	//System.out.println("S is pressed");
	    	P[3].getAlpha().resume();
	    }*/
	}

	public void keyReleased(KeyEvent evt) {
		PositionInterpolator[] P = new PositionInterpolator[4];
		P[0] = pacmanBody.Pos_Int[0];
		P[1] = pacmanBody.Pos_Int[1];
		P[2] = pacmanBody.Pos_Int[2];
		P[3] = pacmanBody.Pos_Int[3];
		
		if (evt.getKeyCode() == KeyEvent.VK_D) {
	        P[0].getAlpha().pause();
	    } 
	    
	    if (evt.getKeyCode() == KeyEvent.VK_A) {
	    	P[1].getAlpha().pause();
	    } 
	    
	    if (evt.getKeyCode() == KeyEvent.VK_W) {
	    	P[2].getAlpha().pause();
	    } 
	    if (evt.getKeyCode() == KeyEvent.VK_S) {
	    	P[3].getAlpha().pause();
	    }
	}
	
    public void keyTyped(KeyEvent e) {}
    
    @Override
	public void mouseClicked(MouseEvent event) {
		int x = event.getX(); int y = event.getY();        // mouse coordinates
		Point3d point3d = new Point3d(), center = new Point3d();
		canvas.getPixelLocationInImagePlate(x, y, point3d);// obtain AWT pixel in ImagePlate coordinates
		canvas.getCenterEyeInImagePlate(center);           // obtain eye's position in IP coordinates
		
		Transform3D transform3D = new Transform3D();       // matrix to relate ImagePlate coordinates~
		canvas.getImagePlateToVworld(transform3D);         // to Virtual World coordinates
		transform3D.transform(point3d);                    // transform 'point3d' with 'transform3D'
		transform3D.transform(center);                     // transform 'center' with 'transform3D'

		Vector3d mouseVec = new Vector3d();
		mouseVec.sub(point3d, center);
		mouseVec.normalize();
		pickTool.setShapeRay(point3d, mouseVec);           // send a PickRay for intersection
		
		//Creating New RotationInterpolator to add each rings rotation to it
		PositionInterpolator[] Switch = new PositionInterpolator[2]; 
		Switch[0] = Option_Shapes.Pos_Int[0];
		Switch[1] = ColorString.Pos_Int[1];

		if (pickTool.pickClosest() != null) {
			PickResult pickResult = pickTool.pickClosest();// obtain the closest hit
			Box plank = (Box)pickResult.getNode(PickResult.PRIMITIVE);
			//System.out.println("I pass");
	
			if((int)plank.getUserData()==0) { 
				
	            if (plank.getName()=="Opt1") {
	            	Maze1Frame();
	            }
	            
	            if (plank.getName()=="Opt2") {
	            	Maze2Frame();
	            }
	            
	            if (plank.getName()=="Opt3") {
	            	Maze3Frame();
	            }
				
				if(plank.getName()=="Opt4") {
					System.exit(0);
				}
			}
			
		} 
	}
    
    @Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
}