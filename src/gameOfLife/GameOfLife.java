package gameOfLife;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.awt.Color;
import java.util.Random;

/**
 * Game of life simulation run in a shader program.
 */
public class GameOfLife extends SimpleApplication {

    private Geometry offQuad;
	private int width;
	private int height;
	
	private ViewPort offView;
	private Camera offCamera;
	
	private FrameBuffer offBuffer;
	private Texture2D offTexture;
	private Material offMaterial;
	private Material quadMaterial;
	
	private final static float chanceToBeAlive = 0.50f;

    public static void main(String[] args){
        AppSettings settings = new AppSettings(true);
		settings.setVSync(true);
		settings.setFullscreen(false);
		
//		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		
		GameOfLife app = new GameOfLife();
		app.setPauseOnLostFocus(false);
		app.setSettings(settings);

		app.start();
    }

    public void setupOffscreenView(){
		offCamera = new Camera(width, height);

        // create a pre-view. a view that is rendered before the main view
        offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearEnabled(false);

		//setup framebuffer's cam
        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

		//setup framebuffer's texture
		offTexture = new Texture2D(width, height, Format.RGBA8);
		offTexture.setWrap(Texture.WrapMode.Repeat);

		// create offscreen framebuffer
		offBuffer = new FrameBuffer(width, height, 0);
		offBuffer.setColorTexture(offTexture);
        
        // setup framebuffer's scene
		Texture seedTexture = createSeedTexture();
		offTexture.setImage(seedTexture.getImage());
		
		// scale seed texture
		offTexture.getImage().setWidth(width);
		offTexture.getImage().setHeight(height);
		
        offMaterial = new Material(assetManager, "MatDefs/random.j3md");
		offMaterial.setTexture("m_Texture", offTexture);
		offMaterial.setInt("m_TexWidth", width);
		offMaterial.setInt("m_TexHeight", height);
//		offMaterial.setColor("m_DeadColor", deadColor);
//		offMaterial.setColor("m_AliveColor", aliveColor);
		
        offQuad = new Geometry("offquad", new Quad(width, height));
        offQuad.setMaterial(offMaterial);
		offQuad.setQueueBucket(Bucket.Gui);
		offQuad.updateGeometricState();

        // attach the scene and set rendering to the off buffer
        offView.attachScene(offQuad);
        offView.setOutputFrameBuffer(offBuffer);
    }

    public void simpleInitApp() {
        width = cam.getWidth();
		height = cam.getHeight();
        
		setupOffscreenView();
		
        //setup main scene
        Quad quadShape = new Quad(width, height);
		Geometry quad = new Geometry("mainQuad", quadShape);
        quadMaterial = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
        quadMaterial.setTexture("ColorMap", offTexture);
        
		quad.setMaterial(quadMaterial);
		
		quad.move(0, 0, -1);
        guiNode.attachChild(quad);
    }
	
	private Texture createSeedTexture() {
		PaintableTexture paintableTexture = new PaintableTexture(width, height);
		Random r = new Random();
		
		paintableTexture.setBackground(Color.black);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (r.nextFloat() >= chanceToBeAlive) {
					paintableTexture.setPixel(i, j, new Color(r.nextFloat(), r.nextFloat(), r.nextFloat()));
				}
			}
		}
		
		return paintableTexture.getTexture();
	}
}
