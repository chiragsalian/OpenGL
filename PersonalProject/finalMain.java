//
// shaderMain.java
//
// Main class for lighting / shading assignment.
//
// 
// Modified by Chirag Salian on 05/21/15
//

import java.awt.*;
import java.nio.*;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.*;

import com.jogamp.opengl.util.Animator;



public class finalMain implements GLEventListener, KeyListener
{

    /**
     * We need four vertex buffers and four element buffers:
     * two for the torus (flat shading and non-flat shading) and
     * two for the teapot (flat shading and non-flat shading).
     *
     * Array layout:
     *         column 0      column 1
     * row 0:  torus flat    torus non-flat
     * row 1:  teapot flat   teapot non-flat
     */
    private int vbuffer[][];
    private int ebuffer[][];
    private int numVerts[][];
    private int numOfObjects;
    /**
     * Animation control
     */
    Animator anime;
    boolean animating;

    // texture values
    public textureParams tex;
    
    /**
     * Initial animation rotation angles
     */
    float angles[];

    /**
     * Current shader type:  flat vs. non-flat
     */
    int currentShader;

    /**
     * Program IDs - current, and all variants
     */
    public int program;
    public int flat;
    public int phong;
    public int gouraud;
	private float diffuseColor[] = new float[4];
								
	private float ambientColor[][] = {
										{0.8f, 0.7f, 0.6f, 1.0f}, // for room
										{0.5f, 0.5f, 0.5f, 1.0f}, // for whiteblock
										{0.5f, 0.5f, 0.5f, 1.0f}, // for redSofa
										{0.5f, 0.5f, 0.5f, 1.0f}, // for whitesofa
										{0.5f, 0.5f, 0.5f, 1.0f}, // for painting red
										{0.5f, 0.5f, 0.5f, 1.0f}, // for painting green
										{0.0f, 0.0f, 0.0f, 1.0f},  // for painting blue
										{0.5f, 0.5f, 0.5f, 1.0f},  // for painting yellow
										{0.5f, 0.5f, 0.5f, 1.0f}  // other incase
									};
	
	private float eyeX, eyeY, eyeZ, lookatX, lookatY, lookatZ, upX, upY, upZ;
	private int state = 1;
    /**
     * Shape info
     */
    shapes myShape;

    /**
     * Lighting information
     */
    lightingParams myPhong;

    /**
     * Viewing information
     */
    viewParams myView;

    /**
     * My canvas
     */
    GLCanvas myCanvas;

    /**
     * Constructor
     */
    public finalMain( GLCanvas G )
    {
    	numOfObjects = 7;
        vbuffer = new int[numOfObjects][2];
        ebuffer = new int[numOfObjects][2];
        numVerts = new int[numOfObjects][2];
        eyeX = 0.2f; // 0.2 original (-2 to 2)
        eyeY = 0.4f; // 0.4 - 2.0
        eyeZ = 6.5f; // 6.5
        
        angles = new float[2];

        animating = false;
        tex = new textureParams();
        
        angles[0] = 0.0f;
        angles[1] = 0.0f;

        myCanvas = G;

        // Initialize lighting and view
        myPhong = new lightingParams();
        myView = new viewParams();

        // Set up event listeners
        G.addGLEventListener (this);
        G.addKeyListener (this);
    }

    private void errorCheck (GL2 gl2)
    {
        int code = gl2.glGetError();
        if (code == GL.GL_NO_ERROR)
            System.err.println ("All is well");
        else
            System.err.println ("Problem - error code : " + code);

    }


    /**
     * Simple animate function
     */
    public void animate() {
    	switch(state) {
    		case 1 : eyeX += 0.01;
    				 if(eyeX >= 2.0f) state = 2;
    				 break;
    		case 2 : eyeY += 0.01;
					 if(eyeY >= 2.0f) state = 3;
					 break;
    		case 3 : eyeX -= 0.01;
					 if(eyeX <= -2.0f) state = 4;
					 break;
    		case 4 : eyeY -= 0.01;
					 if(eyeY <= -0.4f) state = 1;
					 break;
			default: System.out.println("Something seems to have gone wrong");
					break;
    	}
    }

    /**
     * Called by the drawable to initiate OpenGL rendering by the client.
     */
    public void display(GLAutoDrawable drawable)
    {
        // get GL
        GL2 gl2 = (drawable.getGL()).getGL2();

        // clear and draw params..
        gl2.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

        // use the correct program
        gl2.glUseProgram( program );

        
        // set up the camera
        myView.setUpCamera( program, gl2,
        	eyeX, eyeY, eyeZ,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        );

        // set up transformations for the Room -------
        setDiffuseColor(0.807f, 0.768f, 0.67f, 1.0f);
        tex.loadTexture ("floor.jpg");
        tex.setUpTextures (program, gl2);
        
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_ROOM]);
        myView.setUpFrustum( program, gl2 );
        
        myView.setUpTransforms( program, gl2,
            3.5f, 3.5f, 3.5f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f
        );

        // draw it
        selectBuffers( gl2, shapes.OBJ_ROOM, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_ROOM][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );

        
        
        // set up transformations for the WhiteBlock  -------
        setDiffuseColor(1.4f, 1.4f, 1.4f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_WHITEBLOCK]);
        myView.setUpFrustum( program, gl2 );
        
        myView.setUpTransforms( program, gl2,
        		1.0f, 1.0f, 0.6f,
                0.0f, 0.0f, 0.0f,
                0.0f, -1.5f, 0.0f
        );

        // draw it
        selectBuffers( gl2, shapes.OBJ_WHITEBLOCK, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_WHITEBLOCK][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for the WhiteBlock  -------
        setDiffuseColor(1.6f, 1.6f, 1.6f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_WHITEBLOCK]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		1.0f, 0.9f, 0.7f,
                0.0f, 90.0f, 0.0f,
                0.1f, -1.5f, -1.4f
        );

        // draw it
        selectBuffers( gl2, shapes.OBJ_WHITEBLOCK, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_WHITEBLOCK][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for the redSofa on the left -------
        tex.loadTexture ("pattern_red.png");
        tex.setUpTextures (program, gl2);
        setDiffuseColor(0.54f, 0.03f, 0.08f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_RedSofa]);
        myView.setUpFrustum( program, gl2 );
        
        myView.setUpTransforms( program, gl2,
        		1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f,
                -1.2f, -1.4f, 0.5f
        );
        drawRedSofa(gl2);
        
        // set up transformations for the redSofa on the right -------
        setDiffuseColor(0.54f, 0.03f, 0.08f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_RedSofa]);
        myView.setUpFrustum( program, gl2 );
        
        myView.setUpTransforms( program, gl2,
        		1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f,
                1.2f, -1.4f, 0.5f
        );
        drawRedSofa(gl2);
        
        // set up transformations for the redSofaBase on the left  -------
        setDiffuseColor(0.74f, 0.66f, 0.58f, 1.0f);
        tex.loadTexture ("floor.jpg");
        tex.setUpTextures (program, gl2);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_WHITEBLOCK]);
        myView.setUpFrustum( program, gl2 );
        
        myView.setUpTransforms( program, gl2,
        		1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f,
                -1.2f, -1.2f, 0.5f
        );

        // draw it
        selectBuffers( gl2, shapes.OBJ_RedSofaBase, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_RedSofaBase][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        
        // set up transformations for the redSofaBase on the left  -------
        setDiffuseColor(0.74f, 0.66f, 0.58f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_WHITEBLOCK]);
        myView.setUpFrustum( program, gl2 );
        
        myView.setUpTransforms( program, gl2,
        		1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f,
                1.22f, -1.2f, 0.5f
        );

        // draw it
        selectBuffers( gl2, shapes.OBJ_RedSofaBase, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_RedSofaBase][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for the white sofa -------
        setDiffuseColor(1.4f, 1.4f, 1.4f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_WhiteSofa]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		2.5f, 2.5f, 1.5f,
                0.0f, 0.0f, 0.0f,
                0.1f, -1.3f, -7.1f
        );
        selectBuffers( gl2, shapes.OBJ_WhiteSofa, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_WhiteSofa][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for the white pillow on the left-------
        setDiffuseColor(0.77f, 0.69f, 0.46f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Pillow]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.6f, 0.6f, 0.5f,
                -45.0f, 45.0f, 30.0f,
                -1.2f, -1.0f, -5.8f
        );
        selectBuffers( gl2, shapes.OBJ_Pillow, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Pillow][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for the black pillow on the left-------
        setDiffuseColor(0.0f, 0.0f, 0.0f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Pillow]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.6f, 0.6f, 0.5f,
                -45.0f, 45.0f, 30.0f,
                -1.0f, -1.0f, -5.8f
        );
        selectBuffers( gl2, shapes.OBJ_Pillow, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Pillow][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for the white pillow on the right-------
        setDiffuseColor(0.77f, 0.69f, 0.46f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Pillow]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.6f, 0.6f, 0.5f,
                -20.0f, -30.0f, -10.0f,
                1.4f, -1.0f, -5.8f
        );
        selectBuffers( gl2, shapes.OBJ_Pillow, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Pillow][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for the black pillow on the left-------
        setDiffuseColor(0.0f, 0.0f, 0.0f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Pillow]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.6f, 0.6f, 0.5f,
                -20.0f, -30.0f, -10.0f,
                1.2f, -1.0f, -5.8f
        );
        selectBuffers( gl2, shapes.OBJ_Pillow, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Pillow][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        
        // set up transformations for painting row 1 column 1 - type red -------
        setDiffuseColor(0.83f, 0.01f, 0.09f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                -1.2f, 1.0f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 1 column 2 - type green -------
        setDiffuseColor(0.5f, 0.97f, 0.23f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                -0.6f, 1.0f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 1 column 3 - type red -------
        setDiffuseColor(0.8f, 0.16f, 0.15f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 1 column 4 - type yellow -------
        setDiffuseColor(0.99f, 0.69f, 0.01f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                0.6f, 1.0f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 1 column 5 - type red -------
        setDiffuseColor(0.88f, 0.11f, 0.14f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                1.2f, 1.0f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 2 column 1 - type green -------
        setDiffuseColor(0.14f, 0.83f, 0.6f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                -1.2f, 0.4f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 2 column 2 - type yellow -------
        setDiffuseColor(0.98f, 0.98f, 0.04f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                -0.6f, 0.4f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 2 column 3 - type blue -------
        setDiffuseColor(0.14f, 0.74f, 1.0f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.4f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 2 column 4 - type red -------
        setDiffuseColor(0.95f, 0.31f, 0.0f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                0.6f, 0.4f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 2 column 5 - type blue -------
        setDiffuseColor(0.18f, 0.83f, 0.96f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                1.2f, 0.4f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 3 column 1 - type blue -------
        setDiffuseColor(0.11f, 0.77f, 0.96f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                -1.2f, -0.2f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 3 column 2 - type red -------
        setDiffuseColor(0.97f, 0.13f, 0.22f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                -0.6f, -0.2f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 3 column 3 - type blue -------
        setDiffuseColor(0.03f, 0.21f, 0.42f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                0.0f, -0.2f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 3 column 4 - type red -------
        setDiffuseColor(0.78f, 0.0f, 0.0f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                0.6f, -0.2f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // set up transformations for painting row 3 column 5 - type green -------
        setDiffuseColor(0.02f, 0.43f, 0.25f, 1.0f);
        myPhong.setUpPhong( program, gl2, diffuseColor, ambientColor[shapes.OBJ_Painting]);
        myView.setUpFrustum( program, gl2 );
        myView.setUpTransforms( program, gl2,
        		0.35f, 0.35f, 0.35f,
                0.0f, 0.0f, 0.0f,
                1.2f, -0.2f, -6.5f
        );
        selectBuffers( gl2, shapes.OBJ_Painting, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_Painting][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
        
        // perform any required animation for the next time
        if( animating ) {
            animate();
        }
    }
    
    private void setDiffuseColor(float r, float g, float b, float a) {
		// TODO Auto-generated method stub
		diffuseColor[0] = r;
		diffuseColor[1] = g;
		diffuseColor[2] = b;
		diffuseColor[3] = a;
	}

	private void drawRedSofa(GL2 gl2) {
		// TODO Auto-generated method stub
    	selectBuffers( gl2, shapes.OBJ_RedSofa, currentShader );
        gl2.glDrawElements( GL.GL_TRIANGLES,
            numVerts[shapes.OBJ_RedSofa][currentShader],
            GL.GL_UNSIGNED_SHORT, 0l
        );
	}

	public void drawStuff() {
    	
    }

    /**
     * Notifies the listener to perform the release of all OpenGL
     * resources per GLContext, such as memory buffers and GLSL
     * programs.
     */
    public void dispose(GLAutoDrawable drawable)
    {
    }

    /**
     * Verify shader creation
     */
    private void checkShaderError( shaderSetup myShaders, int program,
        String which )
    {
        if( program == 0 ) {
            System.err.println( "Error setting " + which +
                " shader - " +
                myShaders.errorString(myShaders.shaderErrorCode)
            );
            System.exit( 1 );
        }
    }

    /**
     * Called by the drawable immediately after the OpenGL context is
     * initialized.
     */
    public void init(GLAutoDrawable drawable)
    {
        // get the gl object
        GL2 gl2 = drawable.getGL().getGL2();

        // create the Animator now that we have the drawable
        anime = new Animator( drawable );

        // Load shaders, verifying each
        shaderSetup myShaders = new shaderSetup();

        flat = myShaders.readAndCompile( gl2, "flat.vert", "flat.frag" );
        checkShaderError( myShaders, flat, "flat" );

        gouraud = myShaders.readAndCompile(gl2,"gouraud.vert","gouraud.frag");
        checkShaderError( myShaders, gouraud, "gouraud" );

        phong = myShaders.readAndCompile( gl2, "phong.vert", "phong.frag" );
        checkShaderError( myShaders, phong, "phong" );

        // Default shader program
        program = flat;

        // Create all four shapes
        createShape( gl2, shapes.OBJ_ROOM, shapes.SHADE_FLAT );
        createShape( gl2, shapes.OBJ_WHITEBLOCK, shapes.SHADE_FLAT );
        createShape( gl2, shapes.OBJ_RedSofa, shapes.SHADE_FLAT ); 
        createShape( gl2, shapes.OBJ_WhiteSofa, shapes.SHADE_FLAT );
        createShape( gl2, shapes.OBJ_Painting, shapes.SHADE_FLAT );
        createShape( gl2, shapes.OBJ_RedSofaBase, shapes.SHADE_FLAT );
        createShape( gl2, shapes.OBJ_Pillow, shapes.SHADE_FLAT );

        // Other GL initialization
        gl2.glEnable( GL.GL_DEPTH_TEST );
        gl2.glEnable( GL.GL_CULL_FACE );
        gl2.glCullFace(  GL.GL_BACK );
        gl2.glFrontFace( GL.GL_CCW );
        gl2.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
        gl2.glDepthFunc( GL.GL_LEQUAL );
        gl2.glClearDepth( 1.0f );
        
    }


    /**
     * Called by the drawable during the first repaint after the component
     * has been resized.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                     int height)
    {
    }


    /**
     * Create vertex and element buffers for a shape
     */
    public void createShape(GL2 gl2, int obj, int flat )
    {
        // clear the old shape
        myShape = new shapes();

        // make the shape
        myShape.makeShape( obj, flat );

        // save the vertex count
        numVerts[obj][flat] = myShape.nVertices();

        // get the vertices
        Buffer points = myShape.getVertices();
        long dataSize = myShape.nVertices() * 4l * 4l;

        // get the normals
        Buffer normals = myShape.getNormals();
        long ndataSize = myShape.nVertices() * 3l * 4l;

        // get the element data
        Buffer elements = myShape.getElements();
        long edataSize = myShape.nVertices() * 2l;

        Buffer texCoords = myShape.getUV();
        
        // generate the vertex buffer
        int bf[] = new int[1];

        gl2.glGenBuffers( 1, bf, 0 );
        vbuffer[obj][flat] = bf[0];
        gl2.glBindBuffer( GL.GL_ARRAY_BUFFER, vbuffer[obj][flat] );
        gl2.glBufferData( GL.GL_ARRAY_BUFFER, dataSize + ndataSize, null,
        GL.GL_STATIC_DRAW );
        gl2.glBufferSubData( GL.GL_ARRAY_BUFFER, 0, dataSize, points );
        gl2.glBufferSubData( GL.GL_ARRAY_BUFFER, dataSize, ndataSize, normals );
//        gl2.glBufferSubData ( GL.GL_ARRAY_BUFFER, dataSize, ndataSize, texCoords);
        
        // generate the element buffer
        gl2.glGenBuffers (1, bf, 0);
        ebuffer[obj][flat] = bf[0];
        gl2.glBindBuffer( GL.GL_ELEMENT_ARRAY_BUFFER, ebuffer[obj][flat] );
        gl2.glBufferData( GL.GL_ELEMENT_ARRAY_BUFFER, edataSize, elements,
            GL.GL_STATIC_DRAW );
        
        

    }

    /**
     * Bind the correct vertex and element buffers
     *
     * Assumes the correct shader program has already been enabled
     */
    private void selectBuffers( GL2 gl2, int obj, int flat )
    {
        // bind the buffers
        gl2.glBindBuffer( GL.GL_ARRAY_BUFFER, vbuffer[obj][flat] );
        gl2.glBindBuffer( GL.GL_ELEMENT_ARRAY_BUFFER, ebuffer[obj][flat] );

        // calculate the number of bytes of vertex data
        long dataSize = numVerts[obj][flat] * 4l * 4l;

        // set up the vertex attribute variables
        int vPosition = gl2.glGetAttribLocation( program, "vPosition" );
        gl2.glEnableVertexAttribArray( vPosition );
        gl2.glVertexAttribPointer( vPosition, 4, GL.GL_FLOAT, false,
                                       0, 0l );
        int vNormal = gl2.glGetAttribLocation( program, "vNormal" );
        gl2.glEnableVertexAttribArray( vNormal );
        gl2.glVertexAttribPointer( vNormal, 3, GL.GL_FLOAT, false,
                                   0, dataSize );
        
        int  vTex = gl2.glGetAttribLocation (program, "vTexCoord");
        gl2.glEnableVertexAttribArray ( vTex );
        gl2.glVertexAttribPointer (vTex, 2, GL.GL_FLOAT, false,
                                   0, dataSize);
        
    }

    /**
     * Because I am a Key Listener...we'll only respond to key presses
     */
    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){}

    /**
     * Invoked when a key has been pressed.
     */
    public void keyPressed(KeyEvent e)
    {
        // Get the key that was pressed
        char key = e.getKeyChar();

        // Respond appropriately
        switch( key ) {

            case 'a':    // animate to move the camera to get a better look of the room
                animating = true;
                anime.start();
                break;

            case 's':    // stop animating
                animating = false;
                anime.stop();
                break;

            case 'q': case 'Q':
                System.exit( 0 );
                break;
        }

        // do a redraw
        myCanvas.display();
    }


    /**
     * main program
     */
    public static void main(String [] args)
    {
        // GL setup
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        // create your tessMain
        finalMain myMain = new finalMain(canvas);


        Frame frame = new Frame("CG - Shading Assignment");
        frame.setSize(600, 600);
        frame.add(canvas);
        frame.setVisible(true);

        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
