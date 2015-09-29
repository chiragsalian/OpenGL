#version 120

attribute vec4 vPosition;
uniform vec3 theta;
uniform vec3 vTranslation;
uniform vec3 vScaling;
uniform vec3 eyepoint;
uniform vec3 lookat;
uniform vec3 up;
uniform float right;
uniform float left;
uniform float top;
uniform float bottom;
uniform float near;
uniform float far;
uniform float projectionType; // true -> orthographic, else -> frustum 

void main()
{
  	// Compute the sines and cosines of each rotation
  	// about each axis
  	vec3 angles = radians( theta );
  	vec3 c = cos( angles );
  	vec3 s = sin( angles );
		mat4 projection;
		vec3 n;
		vec3 u;
		vec3 v;
		
		
		// Translation
		mat4 translate = mat4 (1.0 , 0.0 , 0.0 , 0.0,
														0.0 , 1.0 , 0.0 , 0.0,
														0.0 , 0.0 , 1.0 , 0.0,
														vTranslation.x, vTranslation.y, vTranslation.z, 1.0);

  	// rotation matrices
  	mat4 rx = mat4 ( 1.0,  0.0,  0.0,  0.0,
                   0.0,  c.x,  s.x,  0.0,
                   0.0, -s.x,  c.x,  0.0,
                   0.0,  0.0,  0.0,  1.0 );

  	mat4 ry = mat4 ( c.y,  0.0, -s.y,  0.0,
                   0.0,  1.0,  0.0,  0.0,
                   s.y,  0.0,  c.y,  0.0,
                   0.0,  0.0,  0.0,  1.0 );

  	mat4 rz = mat4 ( c.z,  s.z,  0.0,  0.0,
                  -s.z,  c.z,  0.0,  0.0,
                   0.0,  0.0,  1.0,  0.0,
                   0.0,  0.0,  0.0,  1.0 );
    
    // Scaling
    mat4 scale = mat4 (vScaling.x , 0.0 , 0.0 , 0.0,
    									0.0 , vScaling.y , 0.0 , 0.0,
    									0.0 , 0.0 , vScaling.z, 0.0,
    									0.0 , 0.0 , 0.0 , 1.0);
		
		
		// World to Camera Transformations
		n = normalize(eyepoint - lookat);
		u = normalize(cross(up, n));
		v = cross(n, u);
		
		mat4 worldToCamera = mat4 (u.x , v.x , n.x , 0.0,
															u.y , v.y , n.y , 0.0,
															u.z , v.z , n.z , 0.0,
															-(dot(u,eyepoint)), -(dot(v,eyepoint)), -(dot(n,eyepoint)), 1.0);
		
		if(projectionType == 2.0) {
			projection = mat4 ( 2.0/(right-left) , 0.0 , 0.0 , 0.0 ,
													0.0 , 2.0/(top-bottom) , 0.0 , 0.0 ,
													0.0 , 0.0 , -2.0/(far-near) , 0.0 ,
													-(right+left)/(right-left) , -(top+bottom)/(top-bottom) , -(far+near)/(far-near) , 1.0);
		}
		else {
			projection = mat4 ( (2.0*near)/(right-left) , 0.0 , 0.0 , 0.0 ,
													0.0 , (2.0*near)/(top-bottom) , 0.0 , 0.0 ,
													(right+left)/(right-left) , (top+bottom)/(top-bottom) , -(far+near)/(far-near) , -1.0,
													0.0 , 0.0 , -(2.0*far*near)/(far-near) , 0.0);
		}
		
		
  	gl_Position = projection * worldToCamera * translate * rz * ry * rx * scale * vPosition;
    
}
