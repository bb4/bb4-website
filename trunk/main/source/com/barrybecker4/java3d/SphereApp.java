package com.barrybecker4.java3d;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Material;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.applet.Applet;
import java.awt.*;

/**
 * Initialize Java3D and show a sphere.
 */
public final class SphereApp extends Applet {

    private Canvas3D canvas;
    BranchGroup root;

    /**
     *  Constructor.
     *  Creates the universe.
     */
    public SphereApp() {
        setLayout(new BorderLayout());
        canvas = createCanvas();
        add("Center", canvas);
        SimpleUniverse universe = new SimpleUniverse(canvas);
        BranchGroup scene = createRotatingContent();
        scene.compile();
        universe.addBranchGraph(scene);
        universe.getViewingPlatform().setNominalViewingTransform();
    }

    /**
     *  Create a canvas to draw the 3D world on.
     */
    private Canvas3D createCanvas() {
        GraphicsConfigTemplate3D graphicsTemplate = new GraphicsConfigTemplate3D();
        GraphicsConfiguration gc1 =
          GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getBestConfiguration(graphicsTemplate);
        return new Canvas3D(gc1);
    }


    /**
     *  Fill your 3D world with content
     */
    private BranchGroup createRotatingContent() {
        root = new BranchGroup();

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        // Create a red light that shines for 100m from the origin
        Color3f light1Color = new Color3f(1.0f, 0.1f, 0.1f);
        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);

        root.addChild(light1);

        Transform3D transform = new Transform3D();
        transform.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        TransformGroup objRotate = new TransformGroup(transform);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRotate.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        root.addChild(objRotate);

        PickRotateBehavior behavior = new PickRotateBehavior(root, canvas, bounds);
        root.addChild(behavior);

        createContent(objRotate);

        return root;
    }


    /**
     *  Fill your 3D world with content
     */
    private void createContent(TransformGroup group) {

        // Create a ball and add it to the group of objects
        Sphere sphere = new Sphere(0.5f);
        group.addChild(sphere);

        //Box box = new Box(.5f, .1f, .5f, Primitive.GENERATE_TEXTURE_COORDS,
	 	//	             getAppearance(new Color3f(PathColor.green)));
        //group.addChild(box);

        ColorCube cube = new ColorCube(0.1);
        group.addChild(cube);
    }


	public static Appearance getAppearance(Color3f color) {
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
		Appearance appearance = new Appearance();
		Texture texture = new Texture2D();
		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		texture.setBoundaryModeS(Texture.WRAP);
		texture.setBoundaryModeT(Texture.WRAP);
		texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
		Material mat = new Material(); //color, black, color, white, 70f);
        mat.setDiffuseColor(color);
        mat.setAmbientColor(new Color3f(0, 0, 0));
        mat.setEmissiveColor(new Color3f(0, 0, 0));

		appearance.setTextureAttributes(texAttr);
		appearance.setMaterial(mat);
		//appearance.setTexture(texture);
		//ColoringAttributes ca = new ColoringAttributes(color,
		//		ColoringAttributes.NICEST);
		//appearance.setColoringAttributes(ca);
		return appearance;
	}



    /**
     * This is our entry point to the application.  This code is not
     * called when the program runs as an applet.
     */
    public static void main(String args[])  {
        // MainFrame allows an applet to run as an application
        Frame frame = new MainFrame(new SphereApp(), 320, 280);
        // Put the title in the application titlebar.  The titlebar
        // isn't visible when running as an applet.
        frame.setTitle("The SphereApp");
    }


}
