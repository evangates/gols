uniform sampler2D m_Texture;
uniform int m_TexWidth;
uniform int m_TexHeight;

uniform vec4 m_AliveColor;
uniform vec4 m_DeadColor;

varying vec2 TexCoord;

float gapS = 1.0 / m_TexWidth;		// horizontal gap between two texels/pixels
float gapT = 1.0 / m_TexHeight;		// vertical gap between two texels/pixels

int[3] countNeighbors(vec2 offsets[8]) {
  int neighbors[3];
  for(int i = 0; i < 3; i++) {
   neighbors[i] = 0;
  }

  // red
	if ( texture2D(m_Texture, TexCoord + offsets[0]).r == m_AliveColor.r) {
		neighbors[0]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[1]).r == m_AliveColor.r) {
		neighbors[0]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[2]).r == m_AliveColor.r) {
		neighbors[0]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[3]).r == m_AliveColor.r) {
		neighbors[0]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[4]).r == m_AliveColor.r) {
		neighbors[0]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[5]).r == m_AliveColor.r) {
		neighbors[0]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[6]).r == m_AliveColor.r) {
		neighbors[0]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[7]).r == m_AliveColor.r) {
		neighbors[0]++;
	}

  // green
	if ( texture2D(m_Texture, TexCoord + offsets[0]).g == m_AliveColor.g) {
		neighbors[1]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[1]).g == m_AliveColor.g) {
		neighbors[1]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[2]).g == m_AliveColor.g) {
		neighbors[1]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[3]).g == m_AliveColor.g) {
		neighbors[1]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[4]).g == m_AliveColor.g) {
		neighbors[1]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[5]).g == m_AliveColor.g) {
		neighbors[1]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[6]).g == m_AliveColor.g) {
		neighbors[1]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[7]).g == m_AliveColor.g) {
		neighbors[1]++;
	}

  // blue
	if ( texture2D(m_Texture, TexCoord + offsets[0]).b == m_AliveColor.b) {
		neighbors[2]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[1]).b == m_AliveColor.b) {
		neighbors[2]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[2]).b == m_AliveColor.b) {
		neighbors[2]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[3]).b == m_AliveColor.b) {
		neighbors[2]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[4]).b == m_AliveColor.b) {
		neighbors[2]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[5]).b == m_AliveColor.b) {
		neighbors[2]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[6]).b == m_AliveColor.b) {
		neighbors[2]++;
	}
	if ( texture2D(m_Texture, TexCoord + offsets[7]).b == m_AliveColor.b) {
		neighbors[2]++;
	}

  return neighbors;
}

/**
* determine if a cell should be alive given the number of neighbors and
* current living state
*/
bool alive(int neighbors, bool alreadyAlive) {
  if (alreadyAlive) {
    return neighbors >= 2 && neighbors <= 3;
  }
  else {
    return neighbors == 3;
  }
}

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

  int neighbors[3] = countNeighbors(offsets);

	vec4 color = texture2D(m_Texture, TexCoord);

  gl_FragColor = vec4(
    alive(neighbors[0], color.r == m_AliveColor.r) ? m_AliveColor.r : m_DeadColor.r,
    alive(neighbors[1], color.g == m_AliveColor.g) ? m_AliveColor.g : m_DeadColor.g,
    alive(neighbors[2], color.b == m_AliveColor.b) ? m_AliveColor.b : m_DeadColor.b,
    1
  );
}