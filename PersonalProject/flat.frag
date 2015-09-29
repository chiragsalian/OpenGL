#version 120

// Flat shading fragment shader
uniform	sampler2D	texture;
varying	vec2	texCoord;
varying vec4 finalColor;
 
void main()
{
		//gl_FragColor = finalColor;
    gl_FragColor = finalColor * texture2D(texture,texCoord);
}
