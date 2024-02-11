package codesW23;

import java.awt.Font;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;

	
public abstract class Main_Menu {
	
	protected TransformGroup objTG = new TransformGroup(); // use 'objTG' to position an object
	protected abstract Node create_Object();               // allow derived classes to create different objects
	
	public Node position_Object() {	                       // retrieve 'objTG' to which 'obj_shape' is attached
		return objTG;   
	}
	
	protected Appearance app;                              // allow each object to define its own appearance
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);                            // A3: attach the next transformGroup to 'objTG'
	}
}
	
//Box and Sphere Shape on the top of the cylinder
class Option_Shapes extends Main_Menu {
	public static PositionInterpolator[] Pos_Int = new PositionInterpolator[2];
	
	private TransformGroup objTG;
	public Option_Shapes() {
		objTG = new TransformGroup();
	    objTG.addChild(create_Object());
	}
	
	protected Node create_Object() {
		
		//Box Positions
		Vector3f position[] = new Vector3f[4];
		position[0] = new Vector3f(0.0f,1.2f,0.0f); 
		position[1] = new Vector3f(0.0f,0.5f,0.0f);   
		position[2] = new Vector3f(0.0f,-0.2f,0.0f);   
		position[3] = new Vector3f(0.0f,-0.9f,0.0f);   
		
		app = CommonsVP.obj_Appearance(CommonsVP.White);   // set the appearance for the base
		app.setTexture(textured_App("WoodenTexture"));     // set texture for the base
		TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.SCREEN_DOOR, 0.5f);  // value: FASTEST NICEST SCREEN_DOOR BLENDED NONE
		app.setTransparencyAttributes(ta);                 // set transparency for the base
		
		//Primitive Shapes to create Boxes
        Primitive[] priShape = new Primitive[4];
        priShape[0] = new Box(1.5f, 0.3f, 0.0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, app);
        priShape[0].setUserData(0); //setUserData 
        priShape[0].setName("Opt1");//setName to track the Mouse Clicking
        
	    priShape[1] = new Box(1.5f, 0.3f, 0.0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, app);
	    priShape[1].setUserData(0);
	    priShape[1].setName("Opt2");
	    
	    priShape[2] = new Box(1.5f, 0.3f, 0.0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, app);
	    priShape[2].setUserData(0);
	    priShape[2].setName("Opt3");
	    
	    priShape[3] = new Box(1.5f, 0.3f, 0.0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, app);
	    priShape[3].setUserData(0);
	    priShape[3].setName("Opt4");
	    
	    TransformGroup objTG1 = new TransformGroup();
	    Transform3D trans3d = new Transform3D();
        TransformGroup trans = null;
       
	    //Adding all the boxes to the TransformGroup
	    for(int i=0;i<4;i++) {
	        trans3d.setScale(new Vector3d(1,1,1)); 
	    	trans3d.setTranslation(position[i]);
	    	trans = new TransformGroup(trans3d);
	        objTG1.addChild(trans);
	        trans.addChild(priShape[i]);
	    }
	    return objTG1;
	}
	
	//Loading texture to the Boxes
	private static Texture textured_App(String name) {
		String filename = name + ".jpg";       // tell the folder of the image
		TextureLoader loader = new TextureLoader(filename, null);
		ImageComponent2D image = loader.getImage();        // load the image
		if (image == null)
			System.out.println("Cannot load file: " + filename);

		Texture2D texture = new Texture2D(Texture.BASE_LEVEL,Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);                        // set image for the texture

		return texture;
	}
	
	//Setting up movement so that boxes looks like floating 
	public Node position_Object() {
		Transform3D Axis_Position = new Transform3D();
		Axis_Position.rotX(Math.PI/2);                                          
	   
		TransformGroup posTG = new TransformGroup();
		posTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Alpha alpha = new Alpha(-1,Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE,0,0,2000,2500,0,2000,0,200); 
		Pos_Int[0] = new PositionInterpolator(alpha, posTG, Axis_Position,(float)Math.PI*-0.1f, (float) Math.PI*0.1f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
		Pos_Int[0].setSchedulingBounds(bounds);
		posTG.addChild(Pos_Int[0]);
		posTG.addChild(objTG);
		return posTG;
	}
}

/* a derived class to create a string label and place it to the bottom of the self-made cone */
class ColorString extends Main_Menu {
	public static PositionInterpolator[] Pos_Int = new PositionInterpolator[2];
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
		rotator.rotY(Math.PI/180);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(rotator);                                 // apply rotation second
		trfm.mul(scaler);                                  // apply scaling first
		objTG = new TransformGroup(trfm);                  // set the combined transformation
		objTG.addChild(create_Object());                   // attach the object to 'objTG'		
	}
	protected Node create_Object() {
		Font my2DFont = new Font("Serif", Font.PLAIN, 1);  // font's name, style, size
		FontExtrusion myExtrude = new FontExtrusion();
		Font3D font3D = new Font3D(my2DFont, myExtrude);	
		Text3D text3D = new Text3D(font3D, str, pos);      // create 'text3D' for 'str' at position of 'pos'
		
		Appearance app = CommonsVP.obj_Appearance(clr);    // use appearance to specify the string color
		return new Shape3D(text3D, app);                   // return a string label with the appearance
	}
	
	public Node position_Object() {
		
		Transform3D Axis_Position = new Transform3D();
		Axis_Position.rotX(Math.PI/2);                                          
	   
		TransformGroup posTG = new TransformGroup();
		posTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Alpha alpha = new Alpha(-1,Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE,0,0,2000,2500,0,2000,0,200); 
		Pos_Int[1] = new PositionInterpolator(alpha, posTG, Axis_Position,(float)Math.PI*-0.1f, (float) Math.PI*0.1f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
		Pos_Int[1].setSchedulingBounds(bounds);
		posTG.addChild(Pos_Int[1]);
		posTG.addChild(objTG);
		return posTG;
	}
		
}

	