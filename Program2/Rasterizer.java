//
//  Rasterizer.java
//  
//
//  Created by Joe Geigel on 1/21/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//
//	Modified by Chirag Salian on 3/01/15
// 	Using scanline polygon filling algorithm to fill pixels
// 	in between the polygons.
//	

/**
 * 
 * This is a class that performs rasterization algorithms
 *
 */

import java.util.*;

public class Rasterizer {
    
    int n_scanlines;							// Number of scanlines
    LinkedList<Bucket> aet;						// Linked list to hold Active Edge Tables
    TreeMap<Integer,LinkedList<Bucket>> et;		// TreeMap data structure to hold our Edge Tables

    /**
     * Constructor
     *
     * @param n - number of scanlines
     *
     */
    Rasterizer (int n)
    {
        n_scanlines = n;
    }
    
    /**
     * Nested class that would hold the strutuce of the bucket for us
     */
    class Bucket {  	
    	int ymax, x, dx, dy, sum;
    	/*
    	 * ymax - maximum y for the edge
    	 * x    - x value of the edge
    	 * dx   - (xmax - xmin)
    	 * dy   - (ymax - ymin)
    	 * sum  - holds the sum value that determines how much our x increments
    	 */
    }
    
    /**
     * Draw a filled polygon in the simpleCanvas C.
     *
     * The polygon has n distinct vertices. The 
     * coordinates of the vertices making up the polygon are stored in the 
     * x and y arrays.  The ith vertex will have coordinate  (x[i], y[i])
     *
     * You are to add the implementation here using only calls
     * to C.setPixel()
     */
    public void drawPolygon(int n, int x[], int y[], simpleCanvas C)
    {
        aet = new LinkedList<>();		// new instance for every polygon
        et = new TreeMap<>();			// new instance for every polygon
        
        
    	for(int i=0; i< n-1; i++) {
    		if(y[i] < y[i+1]) addToEdgeTable(x[i],y[i],x[i+1],y[i+1]);
    		else if(y[i] > y[i+1]) addToEdgeTable(x[i+1],y[i+1],x[i],y[i]);
    	}
    	if(y[0] < y[n-1]) addToEdgeTable(x[0],y[0],x[n-1],y[n-1]);
    	else if(y[0] > y[n-1]) addToEdgeTable(x[n-1],y[n-1],x[0],y[0]);
    	
    	
    	// dispETValues();				// Display edge table values to debug
    	fillPolygon(C);					// start method to fill polygons
    }
    
    /**
     * Method to generate the appropriate buckets to add into our edge table
     * 
     * @param 	xmin	lower edge(determined by y) x value
     * @param 	ymin	lower edge(determined by y) y value
     * @param 	xmax	higher edge(determined by y) x value
     * @param 	ymax	higher edge(determined by y) y value
     */
    void addToEdgeTable( int xmin, int ymin, int xmax, int ymax) {
    	Bucket b = new Bucket();		// generate and intialize the bucket
    	b.ymax = ymax;
    	b.dx = xmax - xmin;
    	b.x = xmin;
    	b.dy = ymax - ymin;
    	b.sum = 0;
    	
    	if(et.get(ymin) != null) {		// bucket already exists in earlier edge table location
    		LinkedList<Bucket> ab = (LinkedList<Bucket>)et.get(ymin);
    		boolean hasAdded = false;
    		
    		for(int i=0; i<ab.size(); i++) {
    			Bucket btemp = ab.get(i);
    			float m1 = (float) b.dy/b.dx;
    			float m2 = (float) btemp.dy/btemp.dx;
    			//System.out.println("m1:"+m1+" | m2:"+m2);
    			if(b.x < btemp.x) {						// for lower x values
    				ab.add(i, b);
    				hasAdded = true;
    				break;
    			}
    			if(b.x == btemp.x && (1/m1 < 1/m2)) {	// for same x values determine lower slope
    				ab.add(i, b);
    				hasAdded = true;
    				break;
    			}
    		}
    		if(!hasAdded) ab.add(b);	// if not added before bucket add after the earlier bucket
    		et.put(ymin, ab);			// put linklist into Tree Map edge table
    	}
    	else {							// else if edge table at location is empty add linked list directly
    		LinkedList<Bucket> ab = new LinkedList<>();
    		ab.add(b);
    		et.put(ymin, ab);			// put linklist into Tree Map edge table
    	}
    }
    
    /**
     * Method to help debug and display Edge Table values at any called instance
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	void dispETValues() {
    	Set s = et.entrySet();
    	Iterator i = s.iterator();
    	
    	while(i.hasNext()) {
    		Map.Entry me = (Map.Entry)i.next();
    		System.out.print("Key:"+me.getKey()+" || ");
    		LinkedList<Bucket> ab = (LinkedList<Bucket>)me.getValue();
    		for(int index=0; index < ab.size(); index++) {
    			Bucket b = ab.get(index);
    			System.out.print("ymax:"+b.ymax+ " || x:"+b.x+" || dx:"+b.dx+" || dy:"+b.dy+" || sum:"+b.sum+" ---> ");
    		}
    		System.out.println();
    	}
    }
    
    
    /**
     * Method to use the edge tables and active edge tables to begin
     * filling the inside of polygons
     * 
     * @param 	C	Simple Canvas object to help us draw the pixels	
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	void fillPolygon(simpleCanvas C) {
    	Set s = et.entrySet();
    	Iterator i = s.iterator();
    	
    	while(i.hasNext()) {		// until end of treeMap Edge Table
    		Map.Entry me = (Map.Entry)i.next();
    		LinkedList<Bucket> ab = (LinkedList<Bucket>)me.getValue();
    		boolean hasAdded = false;
    		if(aet.size() == 0) {	// if Active Edge table is empty
    			aet = ab;			// directly push first values to AET
    		}
    		else {					// Else add depending on its order of its sort
    			Bucket btemp = ab.getFirst();
    			for(int index=0; index<aet.size(); ++index) {
    				Bucket b = aet.get(index);
    				if(btemp.x < b.x) {
    					aet.add(index, btemp);
    					hasAdded = true;
    					break;
    				}
    			}
    			if(!hasAdded) aet.add(btemp);
    		}
    		
    		for(int index=0; index<aet.size()-1; index+=2) {	// for every two buckets of the active edge table
				Bucket bucket1 = aet.get(index);		// first active edge table bucket
				Bucket bucket2 = aet.get(index+1);		// second active edge table bucket
				// printBucket(bucket1,"bucket1");
				// printBucket(bucket2,"bucket2");
				if(bucket1 == null || bucket2 == null) break;	// test condition to break
				
				int y = Integer.parseInt(me.getKey().toString());
				int y_Max = bucket1.ymax > bucket2.ymax? bucket2.ymax : bucket1.ymax;
				int xmax = bucket2.x;
				int x;
				
				while(y < y_Max) {					// draw from lower y to ymax
					x = bucket1.x;
					xmax = bucket2.x;
					
					while(x < xmax) {				// until x max setpixel for x and y
						C.setPixel(x,y);
						++x;
					}
					
					/*
					 *  This section is to correct the amount by which we need to update 
					 *  bucket1 x while we update y by 1
					 */
					bucket1.sum += Math.abs(bucket1.dx);
					while(bucket1.sum >= Math.abs(bucket1.dy)) {
						if(bucket1.dx !=0) {
							if((bucket1.dy >0 && bucket1.dx > 0) || (bucket1.dy<0 && bucket1.dx<0)) ++bucket1.x;
							else --bucket1.x;
						}
						bucket1.sum -= Math.abs(bucket1.dy);
					}
					
					/*
					 *  This section is to correct the amount by which we need to update x
					 *  bucket 2 x while we update y by 1
					 */
					bucket2.sum += Math.abs(bucket2.dx);
					while(bucket2.sum >= Math.abs(bucket2.dy)) {
						if(bucket2.dx !=0) {
							if((bucket2.dy >0 && bucket2.dx > 0) || (bucket2.dy<0 && bucket2.dx<0)) ++bucket2.x;
							else --bucket2.x;
						}
						bucket2.sum -= Math.abs(bucket2.dy);
					}
					++y;		// update y for next iteration
				}
				updateAET(y_Max);	// check if we have crossed a ymax to update our Active Edge table
			}
    	}
    }
    
    /**
     * This method is to update our existing Active Edge tables by removing out buckets
     * that have crossed its ymax
     * 
     * @param 	y	y value to compare to check if its crossed or not
     */
    void updateAET(int y) {
    	for(int index=0; index<aet.size()-1; index+=2) {
    		Bucket bucket1 = aet.get(index);
			Bucket bucket2 = aet.get(index+1);
			
    		if(bucket1.ymax == y) {			// if bucket1 y has crossed remove bucket 1
    			aet.remove(index);
    		}
    		else if(bucket2.ymax == y) {	// if bucket2 y has crossed remove bucket 2
    			aet.remove(index+1);
    		}
    	}
    }
    
    /**
     * Method to debug values of the bucket
     * 
     * @param 	b	bucket to output values
     * @param 	s	string to identify to the user the bucket being printed
     */
    void printBucket(Bucket b, String s) {
    	System.out.println("Bucket "+s+"--> ymax:"+b.ymax+ " || x:"+b.x+" || dx:"+b.dx+" || dy:"+b.dy+" || sum:"+b.sum+" ||");
    }
}
