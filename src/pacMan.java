package codesW23;

import java.awt.Font;
import java.io.FileNotFoundException;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.vecmath.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;



/* an abstract super class for create different objects */
public abstract class pacMan{
	public Alpha rotationAlpha;                           // NOTE: keep for future use
	protected BranchGroup objBG;                           // load external object to 'objBG'
	protected TransformGroup objTG;                        // use 'objTG' to position an object
	protected TransformGroup objRG;                        // use 'objRG' to rotate an object
	protected double scale;                                // use 'scale' to define scaling
	protected Vector3f post;                               // use 'post' to specify location
	protected Shape3D obj_shape;
	public static PositionInterpolator P[] = new PositionInterpolator[4];
	public static PositionInterpolator[] Pos_Int = new PositionInterpolator[4];
	public static TransformGroup posTG = new TransformGroup();
	
	public TransformGroup position_Object() {	           // retrieve 'objTG' to which 'obj_shape' is attached
		return objTG;   
	}
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            // A3: attach the next transformGroup to 'objTG'
	}
	public Alpha get_Alpha() { return rotationAlpha; };    // NOTE: keep for future use 
	
	
	/* a function to load and return object shape from the file named 'obj_name' */
	private Scene loadShape(String obj_name) {
		ObjectFile f = new ObjectFile(ObjectFile.RESIZE, (float) (60 * Math.PI / 180.0));
		Scene s = null;
		try {                                              // load object's definition file to 's'
			s = f.load(obj_name + ".obj");
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}
		return s;                                          // return the object shape in 's'
	}
	
	/* function to set 'objTG' and attach object after loading the model from external file */
	protected void transform_Object(String obj_name) {
		Transform3D scaler = new Transform3D();
		scaler.setScale(scale);                            // set scale for the 4x4 matrix
		scaler.setTranslation(post);                       // set translations for the 4x4 matrix
		objTG = new TransformGroup(scaler);                // set the translation BG with the 4x4 matrix
		objBG = loadShape(obj_name).getSceneGroup();       // load external object to 'objBG'
		obj_shape = (Shape3D) objBG.getChild(0);           // get and cast the object to 'obj_shape'
		obj_shape.setName(obj_name);                       // use the name to identify the object 
	}
	
	protected Appearance app = new Appearance();
	private int shine = 32;                                // specify common values for object's appearance
	protected Color3f[] mtl_clr = {new Color3f(1.000000f, 1.000000f, 1.000000f),
			new Color3f(0.772500f, 0.654900f, 0.000000f),	
			new Color3f(0.175000f, 0.175000f, 0.175000f),
			new Color3f(0.000000f, 0.000000f, 0.000000f)};
	
    /* a function to define object's material and use it to set object's appearance */
	protected void obj_Appearance() {		
		Material mtl = new Material();                     // define material's attributes
		mtl.setShininess(shine);
		mtl.setAmbientColor(mtl_clr[0]);                   // use them to define different materials
		mtl.setDiffuseColor(mtl_clr[1]);
		mtl.setSpecularColor(mtl_clr[2]);
		mtl.setEmissiveColor(mtl_clr[3]);                  // use it to enlighten a button
//		mtl.setLightingEnable(true);

		app.setMaterial(mtl);                              // set appearance's material
		obj_shape.setAppearance(app);                      // set object's appearance
	}	

/* a derived class to create a string label and place it to the bottom of the self-made cone */
class ColorString extends pacMan {
	String str;
	Color3f clr;
	double scl;
	Point3f pos;                                           // make the label adjustable with parameters
	public ColorString(String str_ltrs, Color3f str_clr, double s, Point3f p) {
		str = str_ltrs;	
		clr = str_clr;
		scl = s;
		pos = p;

		Transform3D scaler = new Transform3D();
		scaler.setScale(scl);                              // scaling 4x4 matrix 
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotY(Math.PI);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(rotator);                                 // apply rotation second
		trfm.mul(scaler);                                  // apply scaling first
		objTG = new TransformGroup(trfm);                  // set the combined transformation
		objTG.addChild(create_Object());                   // attach the object to 'objTG'		
	}
	protected Node create_Object() {
		Font my2DFont = new Font("Arial", Font.PLAIN, 1);  // font's name, style, size
		FontExtrusion myExtrude = new FontExtrusion();
		Font3D font3D = new Font3D(my2DFont, myExtrude);	
		Text3D text3D = new Text3D(font3D, str, pos);      // create 'text3D' for 'str' at position of 'pos'
		
		Appearance app = CommonsVP.obj_Appearance(clr);    // use appearance to specify the string color
		return new Shape3D(text3D, app);                   // return a string label with the appearance
	}
}
}

    //Making the Maze
	class mazeMaking extends pacMan {
		public mazeMaking() {
			scale = 2d;                                   
			post = new Vector3f(0.0f, 0.0f, 0.0f);        
			transform_Object("maze1");                     // set transformation to 'objTG' and load object file
			obj_Appearance();                              // set appearance after converting object node to Shape3D
		}

		public TransformGroup position_Object() {          
	        Transform3D r_axis = new Transform3D();  
	        Transform3D SecTG = new Transform3D();
	        SecTG.setEuler(new Vector3d(Math.PI/2,0,0));   // default: rotate around Y-axis
	        r_axis.rotY(Math.PI);                          // rotate around y-axis for 180 degrees
	        objRG = new TransformGroup(r_axis); 
	        objRG.setTransform(SecTG);                     
	        objTG.addChild(objRG);                         
	        objRG.addChild(objBG);                         
	        return objTG;                                      
	    }

		public void add_Child(TransformGroup nextTG) {
			objTG.addChild(nextTG);                        // attach the next transformGroup to 'objTG'
		}
	}
	
	//Making the Maze
	class maze1Making extends pacMan {
		public maze1Making() {
			scale = 2d;                                    
		    post = new Vector3f(0.0f, 0.0f, 0.0f);         
		    transform_Object("maze2");                     // set transformation to 'objTG' and load object file
		    obj_Appearance();                              // set appearance after converting object node to Shape3D
		}

		public TransformGroup position_Object() {              
			Transform3D r_axis = new Transform3D();  
		    Transform3D SecTG = new Transform3D();
		    SecTG.setEuler(new Vector3d(Math.PI/2,0,0));       // default: rotate around Y-axis
		    r_axis.rotY(Math.PI);                              // rotate around y-axis for 180 degrees
		    objRG = new TransformGroup(r_axis); 
		    objRG.setTransform(SecTG);                         
		    objTG.addChild(objRG);                             
		    objRG.addChild(objBG);                            
		    return objTG;                                      
		}

	    public void add_Child(TransformGroup nextTG) {
		    objTG.addChild(nextTG);                            // attach the next transformGroup to 'objTG'
		}
	}
		
	//Making the Maze
	class maze2Making extends pacMan {
		public maze2Making() {
			scale = 2d;                                     
		    post = new Vector3f(0.0f, 0.0f, 0.0f);           
		    transform_Object("maze3");                     // set transformation to 'objTG' and load object file
		    obj_Appearance();                              // set appearance after converting object node to Shape3D
	    }

	public TransformGroup position_Object() {              // attach object BranchGroup "FanStand" to 'objTG'
		Transform3D r_axis = new Transform3D();  
        Transform3D SecTG = new Transform3D();
		SecTG.setEuler(new Vector3d(Math.PI/2,0,0));       // default: rotate around Y-axis
	    r_axis.rotY(Math.PI);                              // rotate around y-axis for 180 degrees
		objRG = new TransformGroup(r_axis); 
		objRG.setTransform(SecTG);              
		objTG.addChild(objRG);                             
	    objRG.addChild(objBG);                             
		return objTG;                                      
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            // attach the next transformGroup to 'objTG'
		}
	}
    
	// Making the pacMan Body
	class pacmanBody extends pacMan {
		public pacmanBody() {
			scale = 0.04d;                                        // use to scale up/down original size
			post = new Vector3f(-0.33f, 0f, 0f);                   // use to move object for positioning
			transform_Object("pacmanBody1");                      // set transformation to 'objTG' and load object file
			mtl_clr[1] = new Color3f(CommonsVP.Yellow);           // set "FanStand" to a different color than the common  		                                              
			obj_Appearance();                                     // set appearance after converting object node to Shape3D
		}
		public TransformGroup position_Object() {
			Transform3D r_axis = new Transform3D();                 
	        r_axis.rotY(Math.PI/2); 
	        r_axis.setScale(scale);
	        r_axis.setTranslation(post);
	        objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	        objTG.setTransform(r_axis);  
	        
	        
	        Transform3D Axis_Position = new Transform3D();
			Axis_Position.rotX(Math.PI/2);
			
			Transform3D Axis_Position1 = new Transform3D();
			Axis_Position1.rotX(Math.PI/2);
			
			float movement = 0.1f;
		    Vector3f currPos = new Vector3f(-1.2f,0f,0f);
		    Vector3f Up = new Vector3f(-1.5f,0f,0f);
	        
		    //PositiomInterpolator to move the object in right direction
			posTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Alpha alpha = new Alpha(-1,Alpha.INCREASING_ENABLE,0,0,7000,0,0,0,0,0); 
			Pos_Int[0] = new PositionInterpolator(alpha, posTG, Axis_Position, currPos.x, (float) (Math.PI * 0.75f) );
			BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
			Pos_Int[0].setSchedulingBounds(bounds);
			currPos.x = currPos.x+movement;
			Pos_Int[0].getAlpha().pause();
			
			//PositiomInterpolator to move the object in left direction
			posTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Axis_Position.rotX(-Math.PI/2);
	        Alpha alpha1 = new Alpha(-1, Alpha.INCREASING_ENABLE,0,0,7000,0,0,0,0,0);
	        Pos_Int[1] = new PositionInterpolator(alpha1, posTG, Axis_Position, currPos.x,(float) (Math.PI * 0.75f));
	        BoundingSphere bounds1 = new BoundingSphere(new Point3d(), 100.0);
	        Pos_Int[1].setSchedulingBounds(bounds1);
	        currPos.x = currPos.x-movement;
	        Pos_Int[1].getAlpha().pause();
	        
	      //PositiomInterpolator to move the object in Up direction
	        posTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	        Axis_Position1.rotZ(Math.PI/2);
	        Alpha alpha2 = new Alpha(-1, Alpha.INCREASING_ENABLE,0,0,7000,0,0,0,0,0);
	        Pos_Int[2] = new PositionInterpolator(alpha2, posTG, Axis_Position1, Up.x,(float) (Math.PI * 0.75f));
	        BoundingSphere bounds2 = new BoundingSphere(new Point3d(), 100.0);
	        Pos_Int[2].setSchedulingBounds(bounds2);
	        //currPos.z = currPos.z+movement;
	        Pos_Int[2].getAlpha().pause();
	        
	      //PositiomInterpolator to move the object in down direction
	        posTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	        Axis_Position.rotZ(-Math.PI/2);
	        Alpha alpha3 = new Alpha(-1, Alpha.INCREASING_ENABLE,0,0,7000,0,0,0,0,0);
	        Pos_Int[3] = new PositionInterpolator(alpha3, posTG, Axis_Position, -1.2f, (float) (Math.PI * 0.75f));
	        BoundingSphere bounds3 = new BoundingSphere(new Point3d(), 100.0);
	        Pos_Int[3].setSchedulingBounds(bounds3);
	        currPos.z = currPos.z-movement;
	        Pos_Int[3].getAlpha().pause();
	        
	        
			posTG.addChild(Pos_Int[0]);
			//posTG.addChild(Pos_Int[1]);
			posTG.addChild(Pos_Int[2]);
			//posTG.addChild(Pos_Int[3]);
			objTG.addChild(objBG); 
			posTG.addChild(objTG);
			return posTG;
	                                      
		}

		public void add_Child(TransformGroup nextTG) {
			objTG.addChild(nextTG);                            // attach the next transformGroup to 'objTG'
		}
    }


    // Making the pacMan Eyes
    class pacmanEyes extends pacMan {
	public pacmanEyes() {
		scale = 1d;                                      
		post = new Vector3f(-1f, 0f, 0f);         
		transform_Object("pacmanEyes");                     
		mtl_clr[1] = new Color3f(CommonsVP.Black);
		obj_Appearance();  
	}

	public TransformGroup position_Object() {
		Transform3D trans3D = new Transform3D();
		trans3D.rotY(Math.PI);
		trans3D.setScale(scale);
		trans3D.setTranslation(post);
        objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    objTG.setTransform(trans3D);
		objTG.addChild(objBG);                             
		return objTG;                                      
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            
	}
}