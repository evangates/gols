package gameOfLife;

//---------------------------------------------------------------------------
// PaintableTexture class
// http://jmonkeyengine.org/groups/graphics/forum/topic/change-texture-data-during-runtime/#post-121416
//---------------------------------------------------------------------------
import java.awt.Color;
import java.nio.ByteBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;

public class PaintableTexture {

	protected byte[] data;
	protected Image image;
	protected Texture2D texture;
	int width;
	int height;

	public PaintableTexture(int width, int height) {
		this.width = width;
		this.height = height;
		// create black image
		data = new byte[width * height * 4];
		setBackground(new Color(0, 0, 0, 255));
		// set data to texture
		ByteBuffer buffer = BufferUtils.createByteBuffer(data);
		image = new Image(Format.RGBA8, width, height, buffer);
		texture = new Texture2D(image);
		texture.setMagFilter(Texture.MagFilter.Nearest);
	}

	public Texture getTexture() {
		ByteBuffer buffer = BufferUtils.createByteBuffer(data);
		image.setData(buffer);
		return texture;
	}

	public void setPixel(int x, int y, Color color) {
		int i = (x + y * width) * 4;
		data[i] = (byte) color.getRed(); // r
		data[i + 1] = (byte) color.getGreen(); // g
		data[i + 2] = (byte) color.getBlue(); // b
		data[i + 3] = (byte) color.getAlpha(); // a
	}

	public void setBackground(Color color) {
		for (int i = 0; i < width * height * 4; i += 4) {
			data[i] = (byte) color.getRed(); // r
			data[i + 1] = (byte) color.getGreen(); // g
			data[i + 2] = (byte) color.getBlue(); // b
			data[i + 3] = (byte) color.getAlpha(); // a
		}
	}

	public void setMagFilter(Texture.MagFilter filter) {
		texture.setMagFilter(filter);
	}
}