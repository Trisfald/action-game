#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 vColor;
varying vec2 vTexCoord;
varying vec2 v_lightCoord;

// texture samplers
uniform sampler2D u_texture; // diffuse map
uniform sampler2D u_lightmap;   // light map

//additional parameters for the shader
uniform vec2 resolution; // resolution of screen
uniform LOWP vec3 ambient; // ambient RGB, alpha channel is intensity 

//grayscale parameter
uniform float greyscale; // intensity of the greyscale filter

void main() {
	vec4 diffuseColor = texture2D(u_texture, vTexCoord);
	vec4 light = texture2D(u_lightmap, v_lightCoord);

	vec3 intensity = ambient + light.rgb;
 	vec3 finalColor = diffuseColor.rgb * intensity;
	
	float grey = dot(finalColor.rgb, vec3(0.299, 0.587, 0.114));
	finalColor = mix(finalColor.rgb, vec3(grey, grey, grey), greyscale);
	
	gl_FragColor = vColor * vec4(finalColor, diffuseColor.a);
}
