#version 120

// Phong shading fragment shader
varying vec3 v_Position;       
varying vec3 v_Normal;         
varying vec3 v_Light;

uniform vec4 lightColor;
uniform vec4 diffuseColor;
uniform vec4 ambientColor;
uniform vec4 specularColor;
uniform float exponent;

void main()
{
    gl_FragColor = vec4( 1.0, 1.0, 1.0, 1.0 );
    
    vec3 N = normalize(v_Normal);
    vec3 L = normalize(v_Light - v_Position);
    
    vec3 normalizedPosition = normalize(-v_Position);
    vec3 normalizedReflection = normalize(reflect(-L,N));
    
    vec4 diffuse = lightColor * (diffuseColor * 0.7) * dot(N,L);
    vec4 specular = lightColor * (specularColor * 1.0) * pow(max(dot(normalizedReflection,normalizedPosition),0.0),exponent); 
    
    vec4 finalColor = diffuse + (ambientColor * 0.5) + specular;
    
    gl_FragColor = finalColor;
}
