#version 120

// Gouraud shading fragment shader
varying vec4 finalColor;

void main()
{
    gl_FragColor = finalColor;
}
