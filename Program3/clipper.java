import java.util.ArrayList;
import java.util.Arrays;

//
//  Clipper.java
//
//
//  Created by Joe Geigel on 1/21/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//
//	Modified by Chirag Salian on 3/10/15
//	Using Sutherland-Hodgman polygon clipper algorithm to clip the polygons
// 	according to what lies within the clip window. Final vertices would be
//	returned to the calling program.
//

/**
 * Object for performing clipping
 *
 */

public class clipper {

	/**
	 * Class vertex holds the x and y coordinate for each vertex defined 
	 * 
	 * @author Chirag Salian
	 */
	class vertex {
    	float x,y;		// x and y coordinate of vertex
    	
    	vertex() {}		// default constructor
    	vertex(float xPoint, float yPoint) {	// parameterized constructor to set x and y
    		x = xPoint;
    		y = yPoint;
    	}
    }
	
	
    /**
     * clipPolygon
     *
     * Clip the polygon with vertex count in and vertex inx/iny
     * against the rectangular clipping region specified by lower-left corner
     * (x0,y0) and upper-right corner (x1,y1). The resulting vertex are
     * placed in outx/outy.
     *
     * The routine should return the the vertex count of the polygon
     * resulting from the clipping.
     *
     * @param in the number of vertex in the polygon to be clipped
     * @param inx - x coords of vertex of polygon to be clipped.
     * @param iny - y coords of vertex of polygon to be clipped.
     * @param outx - x coords of vertex of polygon resulting after clipping.
     * @param outy - y coords of vertex of polygon resulting after clipping.
     * @param x0 - x coord of lower left of clipping rectangle.
     * @param y0 - y coord of lower left of clipping rectangle.
     * @param x1 - x coord of upper right of clipping rectangle.
     * @param y1 - y coord of upper right of clipping rectangle.
     *
     * @return number of vertex in the polygon resulting after clipping
     *
     */
    public int clipPolygon(int in, float inx[], float iny[],
                  float outx[], float outy[],
                  float x0, float y0, float x1, float y1)
    {
        float[][] clipperPoints = {{x0,y0},{x1,y0},{x1,y1},{x0,y1}};	// set the coordinate to an array
        ArrayList<vertex> polygon = new ArrayList<vertex>();			// polygon vertex list
    	ArrayList<vertex> clipper = new ArrayList<vertex>();			// clipped window vertex list
    	ArrayList<vertex> result;										// final result vertex list
    	ArrayList<vertex> currentPoints;								// temporary points vertex list
    	int numOfVertices = 0;											// number of final vertices
    	
    	// Initialize the coordinates of the clipper in our list
    	for(int i=0; i<clipperPoints.length; i++) {
    		vertex tempV = new vertex(clipperPoints[i][0], clipperPoints[i][1]);
    		clipper.add(tempV);
    	}
    	
    	// Initialize the coordinates of the polygon in our list
    	for(int i=0; i<inx.length; i++) {
    		vertex tempV = new vertex(inx[i], iny[i]);
    		polygon.add(tempV);
    	}
    	result = new ArrayList<vertex>(polygon);	// set result to the same as polygon in the beginning
    	
    	// for all points of the clipper
    	for(int i=0; i<clipper.size() ; i++) {
    		int resultSize = result.size();					// number of vertices in our result
    		currentPoints = result;							// set current points as our result
    		result = new ArrayList<vertex>(resultSize);		// clear result
    		
    		vertex clip1 = clipper.get((i + clipper.size() - 1) % clipper.size());	// previous clip vertex
    		vertex clip2 = clipper.get(i);											// current clip vertex
    		
    		// for all vertices
    		for(int j=0; j<resultSize; j++) {
    			vertex v1 = currentPoints.get((j + resultSize - 1) % resultSize);	// previous polygon vertex
        		vertex v2 = currentPoints.get(j);									// current polygon vertex
        		
        		/* check for the 4 rules of Sutherland-Hodgman algorithm, 
        		* 2nd Rule is ignored since no vertex is added */
        		if(isInside(clip1, clip2, v1)) {		
        			result.add(v1);						// Rule #3 
        			if(!isInside(clip1, clip2, v2))	 result.add(intersection(clip1, clip2, v1, v2)); // Rule #1
        		} else if(isInside(clip1, clip2, v2)) {
        			result.add(intersection(clip1, clip2, v1, v2));		// Rule #4
        		}
    		}
    	}
    	
    	// push the values of result into outx & outy
    	for(int i=0; i<result.size(); i++) {
    		vertex v = result.get(i);
    		outx[i] = v.x;
    		outy[i] = v.y;
    		++numOfVertices;
    	}
    	
    	return numOfVertices; 	// return number of vertex in clipped poly.
    }
    
    /**
     * Method to test if our polygon vertex lies within the clip window
     * 
     * @param 	clip1	first vertex of our clip window
     * @param 	clip2	second vertex of our clip window
     * @param 	v		vertex of our polygon
     * @return	true if polygon vertex lies within the clip window else false	
     */
    boolean isInside(vertex clip1, vertex clip2, vertex v) {
    	return (clip1.x - v.x) * (clip2.y - v.y) > (clip1.y - v.y) * (clip2.x - v.x);
    }
    
    /**
     * Function to obtain the vertex coordinate wherein the polygon line intersects
     * with the clipped window
     * 
     * @param 	clip1	first vertex of our clip window
     * @param 	clip2 	second vertex of our clip window
     * @param 	v1		first vertex of our polygon
     * @param 	v2		second vertex of our polygon
     * @return	vertex  coordinate of the intersection
     */
    vertex intersection(vertex clip1, vertex clip2, vertex v1, vertex v2) {
    	vertex v = new vertex();						// intersection vertex variable
    	float clipDy = clip2.y - clip1.y;				// dy of clip line
    	float clipDx = clip1.x - clip2.x;				// dx of clip line
    	float c1 = clipDy*clip1.x + clipDx*clip1.y;	
    	
    	float vectorDy = v2.y - v1.y;					// dy of polygon line
    	float vectorDx = v1.x - v2.x;					// dx of polygon line
    	float c2 = vectorDy*v1.x + vectorDx*v1.y;
    	
    	float det = clipDy*vectorDx - vectorDy*clipDx;
    	v.x = (vectorDx*c1 - clipDx*c2) / det;
    	v.y = (clipDy*c2 - vectorDy*c1) / det;
    	return v;
    }
    
}
