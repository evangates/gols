uniform sampler2D m_Texture;
uniform int m_TexWidth;
uniform int m_TexHeight;

const float step = 0.1;
const float lonelyThreshold = 2.0;
const float overcrowdedThreshold = 3.0;
const float comeToLifeValue = 3.0;
const float comeToLifeTolerance = 0.01;
const float aliveThreshold = 1.0;
const float fullyDecayed = 0.0;
const float alive = 1.0;

varying vec2 TexCoord;

const vec4 m_DeadColor = vec4(0.0);
const vec4 m_AliveColor = vec4(1.0);

float gapS = 1.0 / m_TexWidth;		// horizontal gap between two texels/pixels
float gapT = 1.0 / m_TexHeight;		// vertical gap between two texels/pixels

float nextValue(float value, float sum) {
	// alive
	if (value >= aliveThreshold) {
		// decay
		if (sum < lonelyThreshold || sum > overcrowdedThreshold) {
			return mix(value, fullyDecayed, step);
		}
		// refresh
		else {
			return alive;
		}
	}
	// dead
	else {
		// comes to life
		if (lonelyThreshold <= sum && sum <= overcrowdedThreshold) {
			return alive;
		}
		// decay
		else {
			return mix(value, fullyDecayed, step);
		}
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

	int neighbors = 0;

	vec4 colorSum = vec4(0.0);
	vec4 color = texture2D(m_Texture, TexCoord);

	for (int i = 0; i < 8; i++) {
		colorSum += texture2D(m_Texture, TexCoord + offsets[i]);
	}

	color.r = nextValue(color.r, colorSum.r);
	color.g = nextValue(color.g, colorSum.g);
	color.b = nextValue(color.b, colorSum.b);

/*
	// count neighbors
	for (int i = 0; i < 8; i++) {
		if (texture2D(m_Texture, TexCoord + offsets[i]) == m_AliveColor) {
			neighbors++;
		}
	}
	
	// living
	if (color == m_AliveColor) {
		// cell dies
		if (neighbors < 2 || neighbors > 3) {
			color = mix(color, m_DeadColor, 0.1);
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
			color = mix(color, m_DeadColor, 0.05);
		}
	}
*/

	gl_FragColor = color;
}