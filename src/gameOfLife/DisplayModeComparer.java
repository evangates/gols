package gameOfLife;

import java.util.Comparator;
import org.lwjgl.opengl.DisplayMode;

/**
 *
 * @author kuhlmancer
 */
public class DisplayModeComparer implements Comparator<DisplayMode> {
	public int compare(DisplayMode lhs, DisplayMode rhs) {
		if (lhs.getWidth() > rhs.getWidth()) {
			return 1;
		}
		if (lhs.getWidth() < rhs.getWidth()) {
			return -1;
		}
		
		// equal widths
		
		if (lhs.getHeight() > rhs.getHeight()) {
			return 1;
		}
		if (lhs.getHeight() < rhs.getHeight()) {
			return -1;
		}
		
		// equal heights
		
		if (lhs.getBitsPerPixel() > rhs.getBitsPerPixel()) {
			return 1;
		}
		if (lhs.getBitsPerPixel() < rhs.getBitsPerPixel()) {
			return -1;
		}
		
		// equal pixel depths
		
		if (lhs.getFrequency() > rhs.getFrequency()) {
			return 1;
		}
		if (lhs.getFrequency() < rhs.getFrequency()) {
			return -1;
		}
		
		// everything we care about is equal
		return 0;
	}
	
}
