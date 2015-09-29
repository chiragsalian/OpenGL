/**
 *
 * textureParams.java
 *
 * Simple class for setting up the textures for the textures
 * Assignment.
 *
 * Students are to complete this class.
 *
 */

import java.io.*;

import javax.media.opengl.*;

import com.jogamp.opengl.util.texture.*;

public class textureParams
{
	static String image_path;
	Texture mainTex;
	
    
	/**
	 * constructor
	 */
	public textureParams()
	{
		  
	}
    
    /**
     * This functions loads texture data to the GPU.
     *
     * You will need to write this function, and maintain all of the values needed
     * to be sent to the various shaders.
     *
     * @param filename - The name of the texture file.
     *
     */
    public void loadTexture (String filename)
    {
    	image_path = filename;
    }

    
    /**
     * This functions sets up the parameters
     * for texture use.
     *
     * You will need to write this function, and maintain all of the values needed
     * to be sent to the various shaders.
     *
     * @param program - The ID of an OpenGL (GLSL) program to which parameter values
     *    are to be sent
     *
     * @param gl2 - GL2 object on which all OpenGL calls are to be made
     *
     */
    public void setUpTextures (int program, GL2 gl2)
    {
    	
    	try{
    		InputStream stream = getClass().getResourceAsStream(image_path);
    		String extension;
    		if(image_path.charAt(image_path.length()-3) == 'p') extension = "png";
    		else extension = "jpg";
            TextureData data = TextureIO.newTextureData(gl2.getGLProfile(), stream, false, extension);
            mainTex = TextureIO.newTexture(data);
        }
    	catch(IOException exc) {
    		exc.printStackTrace();
    		System.out.println(image_path);
            System.exit(1);
    	}
        
    	gl2.glActiveTexture(GL.GL_TEXTURE0);
        mainTex.enable(gl2);
        mainTex.bind(gl2);
        gl2.glEnable(GL.GL_TEXTURE_2D);
        gl2.glActiveTexture(0);
        
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        
    }
    
    public void clearTexture(GL2 gl2) {
    	gl2.glDisable(GL.GL_TEXTURE_2D);
    }
}
