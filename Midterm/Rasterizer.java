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
    	ArrayList<Integer> startPosition = new ArrayList();
    	ArrayList<Integer> endPosition = new ArrayList();
    	LinkedList<Bucket> lb = et.get(et.lastKey());
    	int y_Max = 0;
    	
    	for(int i=0; i<lb.size(); i++) {
			if(lb.get(i).ymax > y_Max) y_Max = lb.get(i).ymax;
		}
    	
    	//System.out.println("Max y: "+y_Max);
    	
    	for(int y = et.firstKey() ; y < y_Max; y++ ) {
    	
    		updateAET(y);
    		
    		if(et.get(y) != null || aet.size() == 0) {
    			LinkedList<Bucket> ab = et.get(y);
        		boolean hasAdded = false;
        		if(aet.size() == 0) { aet = ab; }
        		else {
        			
        			for(int i=0; i<ab.size(); i++) {
        				Bucket btemp = ab.get(i);
        				hasAdded = false;
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
        		}
        	}
    		
    		for(int i=0; i<aet.size()-1; i+=2) {
    			Bucket bucket1 = aet.get(i);		// first active edge table bucket
				Bucket bucket2 = aet.get(i+1);
				
				if(bucket1 == null || bucket2 == null) break;	// test condition to break
				
				startPosition.add(bucket1.x);
				endPosition.add(bucket2.x);
				
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
    		}
    		
    		// printAET(aet);
    		for(int i=0; i<startPosition.size();i++) {
    			int x = startPosition.get(i);
    			int xMax = endPosition.get(i);
    			
    			// System.out.println(x+" : "+xMax+" : "+y);
    			while(x < xMax) {
    				C.setPixel(x,y);
    				++x;
    			}
    		}
    		startPosition.clear();
    		endPosition.clear();
    		
    	}
    }
    
    /**
     * This method is to update our existing Active Edge tables by removing out buckets
     * that have crossed its ymax
     * 
     * @param 	y	y value to compare to check if its crossed or not
     */
    void updateAET(int y) {
    	for(int index=aet.size()-1; index>=0; index--) {
    		Bucket bucket1 = aet.get(index);
			
			if(bucket1.ymax <= y) {			// if bucket1 y has crossed remove bucket 1
    			aet.remove(index);
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
    
    void printAET(LinkedList<Bucket> activeEdgeTable) {
    	
    	for(int i=0; i<activeEdgeTable.size(); i++) {
    		Bucket b = activeEdgeTable.get(i);
    		printBucket(b,"aet");
    	}
    	System.out.println();
    	
    }
}
