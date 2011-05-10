uniform sampler2D m_Texture;
uniform int m_TexWidth;
uniform int m_TexHeight;

varying vec2 TexCoord;

float gapS = 1.0 / m_TexWidth;		// horizontal gap between two texels/pixels
float gapT = 1.0 / m_TexHeight;		// vertical gap between two texels/pixels

void main() {

	vec2 offsets[8];
	offsets[0] = vec2( 0.0, gapT);		// north
	offsets[1] = vec2( gapS, gapT);		// northeast
	offsets[2] = vec2( gapS, 0.0);		// east
	offsets[3] = vec2( gapS, -gapT);	// southeast
	offsets[4] = vec2( 0.0, -gapT);		// south
	offsets[5] = vec2( -gapS, -gapT);	// southwest
	offsets[6] = vec2( -gapS, 0.0);		// west
	offsets[7] = vec2( -gapS, gapT);	// northwest

	int neighbors = 0;

	if ( texture2D(m_Texture, TexCoord + offsets[0]).g > 0.5) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[1]).g > 0.5) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[2]).g > 0.5) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[3]).g > 0.5) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[4]).g > 0.5) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[5]).g > 0.5) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[6]).g > 0.5) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[7]).g > 0.5) {
		neighbors++;
	}
	
	vec4 color = texture2D(m_Texture, TexCoord);
	color = vec4(0.0, color.g, 0.0, 1.0);
	vec4 deadColor = vec4(0.0, 0.0, 0.0, 1.0);
	vec4 aliveColor = vec4(0.0, 1.0, 0.0, 1.0); 

	// living
	if (color.g > 0.5) {
		// cell dies
		if (neighbors < 2 || neighbors > 3) {
			color = deadColor;
		}
		// cell refreshes
		else {
			color = aliveColor;
		}
	}
	// dead cell
	else {
		// cell comes alive
		if (neighbors == 3) {
			color = aliveColor;
		}
		// cell dies
		else {
			color = deadColor;
		}
	}

	gl_FragColor = color;
}