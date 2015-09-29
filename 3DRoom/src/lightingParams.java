//
// lightingParams.java
//
// Simple class for setting up the viewing and projection transforms
// for the Shading Assignment.
//
// Students are to complete this class.
//
// Modified by Chirag Salian on 05/10/2015
//

import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.*; 

public class lightingParams
{
    // Add any global class variables you need here.
	private float position[] = {0.0f, 0.95f, 2.0f, 1.0f};
	private float color[] = {1.5f, 1.5f, 1.5f, 1.0f};
	private float specularColor[] = {1.0f,1.0f,1.0f,1.0f}; 
	 
    /**
     * constructor
     */
    public lightingParams()
    {
    	
    }
    /**
     * This functions sets up the lighting, material, and shading parameters
     * for the Phong shader.
     *
     * You will need to write this function, and maintain all of the values
     * needed to be sent to the vertex shader.
     *
     * @param program - The ID of an OpenGL (GLSL) shader program to which
     * parameter values are to be sent
     *
     * @param gl2 - GL2 object on which all OpenGL calls are to be made
     *
     */
    public void setUpPhong (int program, GL2 gl2, float[] diffuseColor, float[] ambientColor)
    {
        // Add your code here.
    	int pos_no = gl2.glGetUniformLocation( program , "position" );
   	 	gl2.glUniform4fv( pos_no , 1 , position, 0);
   	 	// gl2.glUniform4f(pos_no, 0.0f, 5.0f, 2.0f, 1.0f);
   	 
        int Color_no = gl2.glGetUniformLocation( program , "lightColor" );
        gl2.glUniform4fv( Color_no , 1 , color, 0 );
        
        int diffuse_no = gl2.glGetUniformLocation( program , "diffuseColor" );
        gl2.glUniform4fv( diffuse_no , 1 , diffuseColor, 0 );
        
        int specularColor_no = gl2.glGetUniformLocation( program , "specularColor" );
        gl2.glUniform4fv( specularColor_no , 1 , specularColor, 0 );
        
        int exponent_no = gl2.glGetUniformLocation( program , "exponent" );
        gl2.glUniform1f( exponent_no , 10.0f);
        
        int ambient_no = gl2.glGetUniformLocation( program , "ambientColor" );
        gl2.glUniform4fv( ambient_no , 1 , ambientColor, 0 );

    }
}
