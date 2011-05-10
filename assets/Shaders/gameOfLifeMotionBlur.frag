const vec4 livingFadeFrom	= vec4(0.0, 1.0, 0.0, 1.0);		// new living cell
const vec4 livingFadeTo		= vec4(0.0, 1.0, 1.0, 1.0);		// alive but old cell
const vec4 deadFadeFrom		= vec4(0.0, 0.5, 0.0, 1.0);		// newly dead cell
const vec4 deadFadeTo		= vec4(0.0, 0.0, 0.0, 1.0);		// been dead a while

void main(void) {
	if (gl_FragColor.g > 0.5) {
		gl_FragColor = livingFadeFrom * gl_FragColor.r + livingFadeTo * (1.0 - gl_FragColor.r);
	}
	else {
		gl_FragColor = deadFadeFrom * gl_FragColor.r + deadFadeTo * (1.0 - gl_FragColor.r);
	}
}