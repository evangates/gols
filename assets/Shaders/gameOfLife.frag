uniform sampler2D m_Texture;
uniform int m_TexWidth;
uniform int m_TexHeight;
uniform float m_Seed;

const float step = 0.005;
const int lonelyThreshold = 2;
const int overcrowdedThreshold = 3;
const int comeToLifeValue = 3;
const float aliveThreshold = 1.0;
const float fullyDecayed = 0.0;
const float alive = 1.0;
const float spontaneousBirthThreshold = 0.9995;

varying vec2 TexCoord;

float gapS = 1.0 / m_TexWidth;		// horizontal gap between two texels/pixels
float gapT = 1.0 / m_TexHeight;		// vertical gap between two texels/pixels

float rand()
{
  return fract(sin(dot(vec2(m_Seed + TexCoord.x, m_Seed + TexCoord.y), vec2(12.9898, 78.233)))* 43758.5453);
}

float nextValue(float value, int neighbors) {
	// alive
	if (value >= aliveThreshold) {
		// decay
		if (neighbors < lonelyThreshold || neighbors > overcrowdedThreshold) {
			return max(value - step, 0.0);
		}
		// refresh
		else {
			return alive;
		}
	}
	// dead
	else {
		// comes to life
		if (neighbors == comeToLifeValue) {
			return alive;
		}
		// decay
		else {
			return max(value - step, 0.0);
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

	int nr = 0;
	int ng = 0;
	int nb = 0;

	vec4 color = texture2D(m_Texture, TexCoord);

	for (int i = 0; i < 8; i++) {
		vec4 offsetValue = texture2D(m_Texture, TexCoord + offsets[i]);

		if (offsetValue.r >= aliveThreshold)
			nr++;
		if (offsetValue.g >= aliveThreshold)
			ng++;
		if (offsetValue.b >= aliveThreshold)
			nb++;
	}

	color.r = nextValue(color.r, nr);
//	color.rb = vec2(0.0);
	color.g = nextValue(color.g, ng);
	color.b = nextValue(color.b, nb);

	// sometimes bring a dead pixel to life
	if (all(lessThan(color.rgb, vec3(aliveThreshold))) && rand() >= spontaneousBirthThreshold) {
		color = vec4(1.0);
		//color.g = 1.0;
	}

	gl_FragColor = color;
}