uniform sampler2D m_Texture;
uniform int m_TexWidth;
uniform int m_TexHeight;

uniform vec4 m_AliveColor;
uniform vec4 m_DeadColor;

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

	if ( texture2D(m_Texture, TexCoord + offsets[0]) == m_AliveColor) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[1]) == m_AliveColor) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[2]) == m_AliveColor) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[3]) == m_AliveColor) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[4]) == m_AliveColor) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[5]) == m_AliveColor) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[6]) == m_AliveColor) {
		neighbors++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[7]) == m_AliveColor) {
		neighbors++;
	}
	
	vec4 color = texture2D(m_Texture, TexCoord);

	// living
	if (color == m_AliveColor) {
		// cell dies
		if (neighbors < 2 || neighbors > 3) {
			color = m_DeadColor;
		}
		// cell refreshes
		else {
			color = m_AliveColor;
		}
	}
	// dead cell
	else {
		// cell comes alive
		if (neighbors == 3) {
			color = m_AliveColor;
		}
		// cell dies
		else {
			color = m_DeadColor;
		}
	}

	gl_FragColor = color;
}