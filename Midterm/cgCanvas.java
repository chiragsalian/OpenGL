//
//  cgCanvas.java
//
//
//  Created by Joe Geigel on 1/21/10.
//  Copyright 2010 Rochester Institute of Technology. All rights reserved.
//
//	Modified by Chirag Salian on 3/21/15
//	This project helps us better understand the 2D pipeline and the routines
//	to implement to convert model coordinates into their respective world
//	coordinates under different viewport, clippings or transformations.
//
//	
/**
 * This is a simple canvas class for adding functionality for the
 * 2D portion of Computer Graphics.
 *
 */

import Jama.*;

import java.util.*;

@SuppressWarnings("serial")
public class cgCanvas extends simpleCanvas {
	
	ArrayList<polygon> Polygon = new ArrayList<polygon>();	// Arraylist to hold all the defined polygon model coordinates
	int polyId = -1;										// ID given to polygons based on their order of creation
	float x0_Clip, y0_Clip, x1_Clip, y1_Clip;				// clip coordinates
	int viewX, viewY, viewWidth, viewHeight;				// viewport coordinates
	Matrix preTransformations = Matrix.identity(3, 3);		// matrix holding the transformations of polygons before viewport transformations
	
	/**
	 * Class to hold the vertices of all the polygons defined
	 * 
	 * @author chirag salian
	 *
	 */
	class polygon {
		int[] x;		// x coordinates of all the vertices
		int[] y;		// y coordinates of all the vertices
		int size;		// number of vertices
		
		/**
		 * Constructor to initialize vertices of polygon
		 * 
		 * @param 	p		array of x coordinates of the polygon
		 * @param 	q		array y coordinates of the polygon
		 * @param 	size	number of vertices
		 */
		polygon(float[] p,float[] q, int size) {
			x = new int[size];
			y = new int[size];
			this.size = size; 
			
			for(int i=0; i<size; i++) {
				x[i] = (int) p[i];
				y[i] = (int) q[i];
			}
		}
	}
	
    /**
     * Constructor
     *
     * @param w width of canvas
     * @param h height of canvas
     */
    cgCanvas (int w, int h)
    {
        super (w, h);
        // YOUR IMPLEMENTATION HERE if you need to modify the constructor
    }

    /**
     * addPoly - Adds and stores a polygon to the canvas.  Note that this
     *           method does not draw the polygon, but merely stores it for
     *           later draw.  Drawing is initiated by the draw() method.
     *
     *           Returns a unique integer id for the polygon.
     *
     * @param x - Array of x coords of the vertices of the polygon to be added
     * @param y - Array of y coords of the vertices of the polygon to be added
     * @param n - Number of vertices in polygon
     *
     * @return a unique integer identifier for the polygon
     */
    public int addPoly (float x[], float y[], int n)
    {
        polygon p = new polygon(x,y,n);		// pushes value to a polygon object
    	Polygon.add(p);						// Adds the polygon to the Arraylist of Polygons
    	++polyId;							// increments ID by 1
        return polyId;						// return the same to user
    }

    /**
     * drawPoly - Draw the polygon with the given id.  Should draw the
     *        polygon after applying the current transformation on the
     *        vertices of the polygon.
     *
     * @param polyID - the ID of the polygon to be drawn
     */
    public void drawPoly (int polyID)
    {
        polygon p = Polygon.get(polyID);	// Get polygon object based on polygon ID
    	Rasterizer R = new Rasterizer(601);	// Create an object of Rasterizer to handle our polygon filling
    	clipper Clip = new clipper();		// Create an object to handle our clipping
    	float[] inx = new float[p.size];	// original polygon model x coordinates
		float[] iny = new float[p.size];	// original polygon model y coordinates
		int[] outx = new int[50];			// final polygon world x coordinates
		int[] outy = new int[50];			// final polygon world y coordinates
		
		// initialize inx and iny
		for (int i = 0; i < p.size; i++) {
			inx[i] = p.x[i];
			iny[i] = p.y[i];
		}
		// call clip polygon and get the number of new vertices in outn 
		int outn = Clip.clipPolygon(p.size, inx, iny, outx, outy, x0_Clip, y0_Clip, x1_Clip, y1_Clip);
		
		// Matrix clipped translations for world coordinates
		Matrix translate = Matrix.identity(3, 3);
		translate.set(0, 2, -x0_Clip);
		translate.set(1, 2, -y0_Clip);

		// Matrix scale translations for world coordinates
		Matrix scale = Matrix.identity(3, 3);
		scale.set(0, 0, viewWidth / (x1_Clip - x0_Clip));
		scale.set(1, 1, viewHeight / (y1_Clip - y0_Clip));

		// Matrix viewport translations for world coordinates
		Matrix viewPortTranslate = Matrix.identity(3, 3);
		viewPortTranslate.set(0, 2, viewX);
		viewPortTranslate.set(1, 2, viewY);
		
		/*
		 *  perform operations to modify all vertices of the polygon to get 
		 *   their resulting world coordinates
		 */
		for(int i=0; i<outn; i++) {
			double[][] currentVertice = { 
											{outx[i]}, 
											{outy[i]}, 
											{1.0}
										};
			// System.out.println("Old: "+ i + ": " + outx[i] + "\t" + outy[i]);
			Matrix current = new Matrix(currentVertice);	// convert our coordinates to a 3x1 matrix
			current = preTransformations.times(current);	// apply our earlier transformations
			current = translate.times(current);				// multiply to move the coordinates with respect to clipped window
			current = scale.times(current);					// multiply to move the coordinates with respect to clipped scale
			current = viewPortTranslate.times(current);		// multiply to move the coordinates with respect to viewport values
			outx[i] = (int) current.get(0, 0);				// final x coordinates
			outy[i] = (int) current.get(1, 0);				// final y coordinates
			// System.out.println("New: "+ i + ": " + outx[i] + "\t" + outy[i]);
		}
		
		// Draw the final coordinates using our earlier polygon filling code
		R.drawPolygon(outn, outx, outy, this);
    }

    /**
     * clearTransform - Set the current transformation to the identity matrix.
     */
    public void clearTransform()
    {
        preTransformations = Matrix.identity(3, 3);
    }

    /**
     * translate - Add a translation to the current transformation by
     *             pre-multiplying the appropriate translation matrix to
     *             the current transformation matrix.
     *
     * @param x - Amount of translation in x
     * @param y - Amount of translation in y
     */
    public void translate (float x, float y)
    {
        Matrix translate = Matrix.identity(3, 3);
    	translate.set(0, 2, x);
    	translate.set(1, 2, y);
    	preTransformations = translate.times(preTransformations);
    }

    /**
     * rotate - Add a rotation to the current transformation by
     *          pre-multiplying the appropriate rotation matrix to the
     *          current transformation matrix.
     *
     * @param degrees - Amount of rotation in degrees
     */
    public void rotate (float degrees)
    {
        Matrix rotate = Matrix.identity(3, 3);
    	float cos = (float) Math.cos(Math.toRadians(degrees));
    	float sin = (float) Math.sin(Math.toRadians(degrees));
    	rotate.set(0, 0, cos);
    	rotate.set(0, 1, -sin);
    	rotate.set(1, 0, sin);
    	rotate.set(1, 1, cos);
    	preTransformations = rotate.times(preTransformations);
    }

    /**
     * scale - Add a scale to the current transformation by pre-multiplying
     *         the appropriate scaling matrix to the current transformation
     *         matrix.
     *
     * @param x - Amount of scaling in x
     * @param y - Amount of scaling in y
     */
    public void scale (float x, float y)
    {
        Matrix scale = Matrix.identity(3, 3);
    	scale.set(0, 0, x);
    	scale.set(1, 1, y);
    	preTransformations = scale.times(preTransformations);
    }

    /**
     * setClipWindow - defines the clip window
     *
     * @param bottom - y coord of bottom edge of clip window (in world coords)
     * @param top - y coord of top edge of clip window (in world coords)
     * @param left - x coord of left edge of clip window (in world coords)
     * @param right - x coord of right edge of clip window (in world coords)
     */
    public void setClipWindow (float bottom, float top, float left, float right)
    {
        x0_Clip = left; 
    	y0_Clip = bottom; 
    	x1_Clip = right; 
    	y1_Clip = top;
    }

    /**
     * setViewport - defines the viewport
     *
     * @param xmin - x coord of lower left of view window (in screen coords)
     * @param ymin - y coord of lower left of view window (in screen coords)
     * @param width - width of view window (in world coords)
     * @param height - width of view window (in world coords)
     */
    public void setViewport (int x, int y, int width, int height)
    {
        viewX = x;
    	viewY = y;
    	viewWidth = width;
    	viewHeight = height;
    }
    
    /**
     * Print Matrix helps with debugging our matrices 
     * 
     * @param 	m 	matrix to output
     */
    public void printMatrix(Matrix m) {
		double[][] tempMatrix = m.getArray();
		for(int i = 0; i < tempMatrix.length; i++) {
		    for(int j = 0; j < tempMatrix[i].length; j++) {        
		        System.out.print( " " + tempMatrix[i][j] );
		    }
		    System.out.println();
		}
    }

}
