package gameOfLife;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
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
import java.lang.reflect.Field;
import java.util.Random;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * Game of life simulation run in a shader program.
 */
public class GameOfLifeApplication extends SimpleApplication {

	private Geometry offQuad;
	private int width;
	private int height;
	private ViewPort offView;
	private Camera offCamera;
	private FrameBuffer offBuffer;
	private Texture2D offTexture;
	private Material offMaterial;
	private Material quadMaterial;
	private final static float chanceToBeAlive = 0.10f;
	private Random random = new Random();
	
	public static void main(String[] args) {
		DisplayMode displayMode = Display.getDisplayMode();
		AppSettings settings = new AppSettings(true);
		settings.setWidth(displayMode.getWidth());
		settings.setHeight(displayMode.getHeight());
		settings.setVSync(true);
		settings.setFullscreen(false);
		
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		
		GameOfLifeApplication app = new GameOfLifeApplication();
		app.setPauseOnLostFocus(false);
		app.setShowSettings(false);
		app.setSettings(settings);

		app.start();
	}
	
	public void setupOffscreenView() {
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

		flyCam.setEnabled(false);

		setupInput();

		setupOffscreenView();

		//setup main scene
		Quad quadShape = new Quad(width, height);
		Geometry quad = new Geometry("mainQuad", quadShape);
		quadMaterial = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
		quadMaterial.setTexture("ColorMap", offTexture);

		quad.setMaterial(quadMaterial);

//		quad.move(0, 0, -1);
		guiNode.attachChild(quad);
	}

	private void setupInput() {
		inputManager.clearMappings();

		// exit for any key
		int[] codes = allKeys();
		for (int code : codes) {
			inputManager.addMapping("exit", new KeyTrigger(code));
		}

		// exit for mouse movement
		inputManager.addMapping("exit", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("exit", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("exit", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("exit", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addMapping("exit", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		inputManager.addMapping("exit", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));

		// exit for clicking
		inputManager.addMapping("exit", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("exit", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping("exit", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

		ActionListener exitListener = new ActionListener() {
			public void onAction(String name, boolean isPressed, float tpf) {
				stop();
			}
		};
		inputManager.addListener(exitListener, new String[]{"exit"});
	}

	private static int[] allKeys() {
		try {
			Class clazz = Class.forName("com.jme3.input.KeyInput");
			Field[] fields = clazz.getDeclaredFields();
			int[] keyValues = new int[fields.length];
			for (int i = 0; i < fields.length; i++) {
				keyValues[i] = ((Integer) fields[i].get(null)).intValue();
			}

			return keyValues;
		} catch (ClassNotFoundException e) {
			return new int[0];
		} catch (IllegalAccessException e) {
			return new int[0];
		}
	}

	private Texture createSeedTexture() {
		PaintableTexture paintableTexture = new PaintableTexture(width, height);

		paintableTexture.setBackground(Color.black);
		float red, green, blue;
		int i, j;

		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				if (random.nextFloat() >= chanceToBeAlive) {
					red = random.nextBoolean() ? 1.0f : 0.0f;
					green = random.nextBoolean() ? 1.0f : 0.0f;
					blue = random.nextBoolean() ? 1.0f : 0.0f;
					paintableTexture.setPixel(i, j, new Color(red, green, blue));
				}
			}
		}

		return paintableTexture.getTexture();
	}

	@Override
	public void simpleUpdate(float tpf) {
		offMaterial.setFloat("m_Seed", random.nextFloat());
	}
}
