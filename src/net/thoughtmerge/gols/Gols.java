package net.thoughtmerge.gols;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.awt.Color;
import java.util.Random;

/**
 * Game of Life Simulation with Shaders
 *
 * @author evan.gates
 */
public class Gols extends SimpleApplication {
  
  private Geometry offQuad;
  private int width;
  private int height;
  
  private ViewPort offView;
  private Camera offCamera;
  
  private FrameBuffer offBuffer;
  private Texture2D offTexture;
  private Material offMaterial;
  private Material quadMaterial;
  
  private final static ColorRGBA deadColor = ColorRGBA.Black;
  private final static ColorRGBA aliveColor = ColorRGBA.White;
  private final static float chanceToBeAlive = 0.5f;

  public static void main(String[] args) {
    AppSettings settings = new AppSettings(true);
    settings.setVSync(true);
    settings.setFullscreen(false);

    Gols app = new Gols();
    app.setSettings(settings);
//    app.setShowSettings(false);
    app.setDisplayFps(false);
    app.setDisplayStatView(false);
    
    app.start();
  }

  @Override
  public void simpleInitApp() {
    width = cam.getWidth();
    height = cam.getHeight();
    
    setupOffscreenView();
    
    // setup main scene
    Quad quadShape = new Quad(width, height);
    Geometry quad = new Geometry("mainQuad", quadShape);
    quadMaterial = new Material(assetManager, "MatDefs/Misc/SimpleTextured.j3md");
    quadMaterial.setTexture("m_ColorMap", offTexture);
    
    quad.setMaterial(quadMaterial);
    
    quad.move(0, 0, -1);
    
    guiNode.attachChild(quad);
  }

//  @Override
//  public void simpleUpdate(float tpf) {
//    //TODO: add update code
//  }
//
//  @Override
//  public void simpleRender(RenderManager rm) {
//    //TODO: add render code
//  }
  
  private void setupOffscreenView() {
    offCamera = new Camera(width, height);
    
    // create a pre-view, a view that is rendered before the main view
    offView = renderManager.createPreView("Offscreen View", offCamera);
    offView.setClearColor(false);
    
    // setup framebuffer's camera
    offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
    offCamera.setLocation(new Vector3f(0f, 0f, -5f));
    offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
    
    // setup framebuffer's texture
    offTexture = new Texture2D(width, height, Image.Format.RGBA8);
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
    offMaterial.setTexture("Texture", offTexture);
    offMaterial.setInt("TexWidth", width);
    offMaterial.setInt("TexHeight", height);
    offMaterial.setColor("DeadColor", deadColor);
    offMaterial.setColor("AliveColor", aliveColor);
    
    offQuad = new Geometry("offquad", new Quad(width, height));
    offQuad.setMaterial(offMaterial);
    offQuad.setQueueBucket(RenderQueue.Bucket.Gui);
        
    // attach the scene and set rendering to the off buffer
    offView.attachScene(offQuad);
    offView.setOutputFrameBuffer(offBuffer);
    
    offQuad.updateGeometricState();
  }
  
  private Texture createSeedTexture() {
    PaintableTexture paintableTexture = new PaintableTexture(width, height);
    Random r = new Random();
    
    final Color aliveColorAwt = toAWTColor(aliveColor);
    final Color deadColorAwt = toAWTColor(deadColor);
    
    paintableTexture.setBackground(deadColorAwt);
    
    // red
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (r.nextFloat() >= chanceToBeAlive) {
          paintableTexture.setPixelR(i, j, aliveColorAwt);
        }
      }
    }
    
    // green
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (r.nextFloat() >= chanceToBeAlive) {
          paintableTexture.setPixelG(i, j, aliveColorAwt);
        }
      }
    }

    // blue
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (r.nextFloat() >= chanceToBeAlive) {
          paintableTexture.setPixelB(i, j, aliveColorAwt);
        }
      }
    }

    // alpha
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (r.nextFloat() >= chanceToBeAlive) {
          paintableTexture.setPixelA(i, j, aliveColorAwt);
        }
      }
    }
    
    return paintableTexture.getTexture();
  }
  
  private static Color toAWTColor(ColorRGBA input) {
    return new Color(input.r, input.g, input.b, input.a);
  }
}
