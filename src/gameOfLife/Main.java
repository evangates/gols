/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gameOfLife;

import com.jme3.app.SimpleApplication;
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

/**
 * This test renders a scene to a texture, then displays the texture on a cube.
 */
public class Main extends SimpleApplication {

    private Geometry offQuad;
	private int width;
	private int height;
	
	private ViewPort offView;
	private Camera offCamera;
	
	private FrameBuffer[] buffers;
	private Texture2D[] textures;
	private int currentIndex;
	private Material offMaterial;
	private Material quadMaterial;

    public static void main(String[] args){
        AppSettings settings = new AppSettings(true);
		settings.setVSync(true);
		
		Main app = new Main();
		app.setSettings(settings);

		app.setShowSettings(true);

		app.start();
    }

    public void setupOffscreenView(){
		offCamera = new Camera(width, height);

        // create a pre-view. a view that is rendered before the main view
        offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearEnabled(false);
//        offView.setBackgroundColor(ColorRGBA.DarkGray);

		//setup framebuffer's cam
        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        // create offscreen framebuffer
		currentIndex = 0;
		buffers = new FrameBuffer[2];
		buffers[0] = new FrameBuffer(width, height, 0);
		buffers[1] = new FrameBuffer(width, height, 0);
		
		//setup framebuffer's texture
		textures = new Texture2D[2];
		textures[0] = new Texture2D(width, height, Format.RGBA8);
		textures[0].setWrap(Texture.WrapMode.Repeat);
		textures[1] = new Texture2D(width, height, Format.RGBA8);
		textures[1].setWrap(Texture.WrapMode.Repeat);

        //setup framebuffer to use texture
        //offBuffer.setDepthBuffer(Format.Depth);
		buffers[0].setColorTexture(textures[0]);
		buffers[1].setColorTexture(textures[1]);
        
        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(buffers[currentIndex]);
		
        // setup framebuffer's scene
		Texture seedTexture = assetManager.loadTexture("Textures/seed.png");
		textures[0].setImage(seedTexture.getImage());
		textures[0].getImage().setWidth(width);
		textures[0].getImage().setHeight(height);
		
		Quad quad = new Quad(width, height);
        offMaterial = new Material(assetManager, "MatDefs/random.j3md");
		offMaterial.setTexture("m_Texture", textures[currentIndex]);
		offMaterial.setInt("m_TexWidth", width);
		offMaterial.setInt("m_TexHeight", height);
		
        offQuad = new Geometry("offquad", quad);
        offQuad.setMaterial(offMaterial);
		offQuad.setQueueBucket(Bucket.Gui);

        // attach the scene to the viewport to be rendered
        offView.attachScene(offQuad);
    }

    @Override
    public void simpleInitApp() {
        width = cam.getWidth();
		height = cam.getHeight();
        
		setupOffscreenView();
		
        //setup main scene
        Quad quadShape = new Quad(width, height);
		Geometry quad = new Geometry("mainQuad", quadShape);
        quadMaterial = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
        quadMaterial.setTexture("ColorMap", textures[currentIndex]);
        
		quad.setMaterial(quadMaterial);
		
		quad.move(0, 0, -1);
        guiNode.attachChild(quad);
    }

    @Override
    public void simpleUpdate(float tpf){
//        Quaternion q = new Quaternion();
//        angle += tpf;
//        angle %= FastMath.TWO_PI;
//        q.fromAngles(angle, 0, angle);
//
//        offBox.setLocalRotation(q);
        offQuad.updateLogicalState(tpf);
        offQuad.updateGeometricState();
    }
}
