#version 120

// Flat shading fragment shader
       
varying vec4 finalColor;
 
void main()
{
    gl_FragColor = finalColor;
}
