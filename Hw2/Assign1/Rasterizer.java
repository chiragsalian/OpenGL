//
//  Rasterizer.java
//  
//
//  Created by Joe Geigel on 1/21/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//
//	Modified by Chirag Salian on 2/22/15
// 	Using midpoint algorithm to draw lines between
//	(x0,y0) and (x1,y1).

/**
 * 
 * A simple class for performing rasterization algorithms.
 *
 */

import java.util.*;


public class Rasterizer {
    
    /**
     * number of scanlines
     */
    int n_scanlines;
    
    /**
     * Constructor
     *
     * @param n number of scanlines
     *
     */
    Rasterizer (int n)
    {
        n_scanlines = n;
    }
    
    /**
     * Draw a line from (x0,y0) to (x1, y1) on the simpleCanvas C.
     *
     * Implementation should be using the Midpoint Method
     *
     * You are to add the implementation here using only calls
     * to C.setPixel()
     *
     * @param x0 x coord of first endpoint
     * @param y0 y coord of first endpoint
     * @param x1 x coord of second endpoint
     * @param y1 y coord of second endpoint
     * @param C  The canvas on which to apply the draw command.
     */
    public void drawLine (int x0, int y0, int x1, int y1, simpleCanvas C)
    {
    	int	y,x,dy,dx,slopeX,slopeY;
    	int	decision,incE,incNE;
     
    	dx = x1 - x0;
    	dy = y1 - y0;
     
    	slopeX = (dx < 0 ? -1 : (dx > 0 ? 1 : 0));	// finding slopeX
    	slopeY = (dy < 0 ? -1 : (dy > 0 ? 1 : 0));	// finding slopeY
     
    	dx = Math.abs(dx);				// unsigned dx
    	dy = Math.abs(dy);				// unsigned dy
     
    	if(dx > dy) {					// for longer x's than y
    		incE = 2 * dy;
    		incNE = 2 * dy - 2 * dx;
    		decision = 2 * dy - dx;		// decision to update y or not
     
    		x = x0;
    		y = y0;
    		do {
    			C.setPixel(x,y);
    			if(decision <= 0) decision += incE;
    			else{
    				decision += incNE;
    				y += slopeY;		// increment y when needed
    			}
    			x += slopeX;			// always increment x since its longer
    		}while(x != x1);			// until endpoint
    	} else {						// for longer y's than x
    		incE = 2 * dx;
    		incNE = 2 * dx - 2 * dy;
    		decision = 2 * dy - dx;		// decision to update x or not
     
    		x = x0;
    		y = y0;
    		do{
    			C.setPixel(x,y);
    			if(decision <= 0)	decision += incE;
    			else {
    				decision += incNE;
    				x += slopeX;		// increment x when needed
    			}
    			y += slopeY;			// always increment y since its longer
    		} while(y != y1);			// until endpoint
    	}
    }
}
