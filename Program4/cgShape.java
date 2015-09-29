//
//  cgShape.java
//
//  Class that includes routines for tessellating a number of basic shapes.
//
//  Students are to supply their implementations for the functions in
//  this file using the function "addTriangle()" to do the tessellation.
//
//	Modified by Chirag C Salian on 04/14/2015
//	
//  This program creates a cube, cone, cylinder and sphere objects
//  and applies tessellation on them to improve their details.

import java.awt.*;
import java.nio.*;
import java.util.ArrayList;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import java.io.*;


public class cgShape extends simpleShape
{
    /**
     * constructor
     */
    public cgShape()
    {
    }

    /**
     * Class square points to hold the coordinates of a square object
     * 
     * @author chiragcs
     */
    class squarePoints {
    	float x,y,z; 
    	
    	void addPoint(float p, float q, float r) {
    		x = p;
    		y = q; 
    		z = r;
    	}
    }
    
    /**
     * Class DoubleCoordinate to hold x,y,z from two coordinates
     * 
     * @author chiragcs
     */
    class DoubleCoordinate {
    	float x1, y1 , z1 , x2, y2, z2;
    	
    	void addCoordinate(float x1, float y1, float z1, float x2, float y2, float z2) {
    		this.x1 = x1;
    		this.y1 = y1;
    		this.z1 = z1;
    		this.x2 = x2;
    		this.y2 = y2;
    		this.z2 = z2;
    	}
    }
    
    
    /**
     * makeCube - Create a unit cube, centered at the origin, with a given
     * number of subdivisions in each direction on each face.
     *
     * @param subdivision - number of equal subdivisons to be made in each
     *        direction along each face
     *
     * Can only use calls to addTriangle()
     */
    public void makeCube (int subdivisions)
    {
        if( subdivisions < 1 )
            subdivisions = 1;

        float startX = -0.5f;
        float endX = 0.5f;
        float startY = -0.5f;
        float endY = 0.5f;
        float startZ = 0.5f;
        float endZ = -0.5f;
        
        float deltaX = (endX-startX) / subdivisions;
        float deltaY = (endY-startY) / subdivisions;
        float deltaZ = (endZ-startZ) / subdivisions;
        float tempX = startX;
        float tempY = startY;
        float tempZ = startZ;
        
        // front face
        for(int i=0; i< Math.pow(subdivisions, 2); i++) {
        	squarePoints[] squareObject = new squarePoints[4];
        			
        	for(int j=0; j< squareObject.length; j++) {
    	        squareObject[j] = new squarePoints();
            }
        	
        	// add the points to the square object
        	squareObject[0].addPoint(tempX, tempY, tempZ);
            squareObject[1].addPoint(tempX + deltaX, tempY, tempZ);
            squareObject[2].addPoint(tempX + deltaX, tempY + deltaY, tempZ);
            squareObject[3].addPoint(tempX, tempY + deltaY, tempZ);
            
            // create a triangle from the square
            createTriangle(squareObject);
            
            // move x and y
            tempX += deltaX;
            if((tempX+0.01) >= endX) {
            	tempY += deltaY;
            	tempX = startX;
            }
        }
        
        
        // right face
        tempX = endX;
        tempY = startY;
        tempZ = startZ;
        for(int i=0; i< Math.pow(subdivisions, 2); i++) {
        	squarePoints[] squareObject = new squarePoints[4];
        			
        	for(int j=0; j< squareObject.length; j++) {
    	        squareObject[j] = new squarePoints();
            }
        	
        	squareObject[0].addPoint(tempX, tempY, tempZ);
            squareObject[1].addPoint(tempX, tempY, tempZ + deltaZ);
            squareObject[2].addPoint(tempX, tempY + deltaY, tempZ + deltaZ);
            squareObject[3].addPoint(tempX, tempY + deltaY, tempZ);
            
            createTriangle(squareObject);
            
            tempZ += deltaZ;
            if((tempZ-0.01) <= endZ) {
            	tempY += deltaY;
            	tempZ = startZ;
            }
        }
        
        // left face
        tempX = startX;
        tempY = startY;
        tempZ = endZ;
        for(int i=0; i< Math.pow(subdivisions, 2); i++) {
        	squarePoints[] squareObject = new squarePoints[4];
        			
        	for(int j=0; j< squareObject.length; j++) {
    	        squareObject[j] = new squarePoints();
            }
        	
        	squareObject[0].addPoint(tempX, tempY, tempZ);
            squareObject[1].addPoint(tempX, tempY, tempZ - deltaZ);
            squareObject[2].addPoint(tempX, tempY + deltaY, tempZ - deltaZ);
            squareObject[3].addPoint(tempX, tempY + deltaY, tempZ);
            
            createTriangle(squareObject);
            
            tempZ -= deltaZ;
            if((tempZ+0.01) >= startZ) {
            	tempY += deltaY;
            	tempZ = endZ;
            }
        }
        
        // top face
        tempX = startX;
        tempY = endY;
        tempZ = startZ;
        for(int i=0; i< Math.pow(subdivisions, 2); i++) {
        	squarePoints[] squareObject = new squarePoints[4];
        			
        	for(int j=0; j< squareObject.length; j++) {
    	        squareObject[j] = new squarePoints();
            }
        	
        	squareObject[0].addPoint(tempX, tempY, tempZ);
            squareObject[1].addPoint(tempX + deltaX, tempY, tempZ);
            squareObject[2].addPoint(tempX + deltaX, tempY, tempZ + deltaZ);
            squareObject[3].addPoint(tempX, tempY, tempZ + deltaZ);
            
            createTriangle(squareObject);
            
            tempX += deltaX;
            if((tempX+0.01) >= endX) {
            	tempZ += deltaZ;
            	tempX = startX;
            }
        }
        
        // bottom face
        tempX = startX;
        tempY = startY;
        tempZ = endZ;
        for(int i=0; i< Math.pow(subdivisions, 2); i++) {
        	squarePoints[] squareObject = new squarePoints[4];
        			
        	for(int j=0; j< squareObject.length; j++) {
    	        squareObject[j] = new squarePoints();
            }
        	
        	squareObject[0].addPoint(tempX, tempY, tempZ);
            squareObject[1].addPoint(tempX + deltaX, tempY, tempZ);
            squareObject[2].addPoint(tempX + deltaX, tempY, tempZ - deltaZ);
            squareObject[3].addPoint(tempX, tempY, tempZ - deltaZ);
            
            createTriangle(squareObject);
            
            tempX += deltaX;
            if((tempX+0.01) >= endX) {
            	tempZ -= deltaZ;
            	tempX = startX;
            }
        }
        
        // back face
        tempX = endX;
        tempY = startY;
        tempZ = endZ;
        for(int i=0; i< Math.pow(subdivisions, 2); i++) {
        	squarePoints[] squareObject = new squarePoints[4];
        			
        	for(int j=0; j< squareObject.length; j++) {
    	        squareObject[j] = new squarePoints();
            }
        	
        	squareObject[0].addPoint(tempX, tempY, tempZ);
            squareObject[1].addPoint(tempX - deltaX, tempY, tempZ);
            squareObject[2].addPoint(tempX - deltaX, tempY + deltaY, tempZ);
            squareObject[3].addPoint(tempX, tempY + deltaY, tempZ);
            
            createTriangle(squareObject);
            
            tempX -= deltaX;
            if((tempX-0.01) <= startX) {
            	tempY += deltaY;
            	tempX = endX;
            }
        }
        
        
    }

    /**
     * method to accept a square object and convert it into triangles
     *  
     * @param 	s	the squarePoints object to be converted into triangles
     */
    private void createTriangle(squarePoints[] s) {
		int counter = 0;
		float[] x = new float[3];
		float[] y = new float[3];
		float[] z = new float[3];
		
		for(int i=0; i<s.length; i++) {
			if(counter>2) {
    			--i;
    			counter = 0;
    			// for first three points
    			addTriangle(x[0], y[0], z[0], 
    					x[1], y[1], z[1], 
    					x[2], y[2], z[2]);
    		}
    			
    		x[counter] = s[i].x;
    		y[counter] = s[i].y;
    		z[counter] = s[i].z;
    		    		
    		counter++;
    	}
    	
    	x[counter] = s[0].x;
		y[counter] = s[0].y;
		z[counter] = s[0].z;
		
		// for last three points
		addTriangle(x[0], y[0], z[0], 
				x[1], y[1], z[1], 
				x[2], y[2], z[2]);
	}

	/**
     * makeCylinder - Create polygons for a cylinder with unit height, centered
     * at the origin, with separate number of radial subdivisions and height
     * subdivisions.
     *
     * @param radius - Radius of the base of the cylinder
     * @param radialDivision - number of subdivisions on the radial base
     * @param heightDivisions - number of subdivisions along the height
     *
     * Can only use calls to addTriangle()
     */
    public void makeCylinder (float radius, int radialDivisions, int heightDivisions)
    {
        if( radialDivisions < 3 )
            radialDivisions = 3;

        if( heightDivisions < 1 )
            heightDivisions = 1;

        float angle = 360.0f/radialDivisions;
        float angleCounter = angle;
        float x0, y0, z0, x1, y1, z1, x2, y2, z2;
        ArrayList<DoubleCoordinate> topCoordinates = new ArrayList<DoubleCoordinate>();
        ArrayList<DoubleCoordinate> bottomCoordinates = new ArrayList<DoubleCoordinate>();
        
        // top face
        x0 = 0f;
        y0 = 0.5f;
        z0 = 0f;
        for(int i=0; i<radialDivisions; i++) {
        	DoubleCoordinate d = new DoubleCoordinate();
        	x1 = (float)(radius*Math.cos(Math.toRadians(angleCounter)));
            y1 = y0;
            z1 = (float)(radius*Math.sin(Math.toRadians(angleCounter)));
            x2 = (float)(radius*Math.cos(Math.toRadians(angleCounter-angle)));
            y2 = y0;
            z2 = (float)(radius*Math.sin(Math.toRadians(angleCounter-angle)));
        	
            // System.out.println(angleCounter+" : "+x0+" : "+y0+" : "+z0+" : "+x1+" : "+y1+" : "+z1+" : "+x2+" : "+y2+" : "+z2);
            addTriangle(x0, y0, z0,
        			x1, y1, z1,
        			x2, y2, z2);
            
            angleCounter += angle;
            d.addCoordinate(x1, y1, z1, x2, y2, z2);
            topCoordinates.add(d);	// add two coordinates into the topCoordinates arraylist
        }
        
        // bottom face
        x0 = 0f;
        y0 = -0.5f;
        z0 = 0f;
        for(int i=0; i<radialDivisions; i++) {
        	DoubleCoordinate d = new DoubleCoordinate();
        	x1 = (float)(radius*Math.cos(Math.toRadians(angleCounter-angle)));
            y1 = y0;
            z1 = (float)(radius*Math.sin(Math.toRadians(angleCounter-angle)));
            x2 = (float)(radius*Math.cos(Math.toRadians(angleCounter)));
            y2 = y0;
            z2 = (float)(radius*Math.sin(Math.toRadians(angleCounter)));
        	
            // System.out.println(angleCounter+" : "+x0+" : "+y0+" : "+z0+" : "+x1+" : "+y1+" : "+z1+" : "+x2+" : "+y2+" : "+z2);
            addTriangle(x0, y0, z0,
        			x1, y1, z1,
        			x2, y2, z2);
            
            angleCounter += angle;
            d.addCoordinate(x1, y1, z1, x2, y2, z2);
            bottomCoordinates.add(d);	// add two coordinates into the bottomCoordinates arraylist
        }
        
        
        // curved body
        for(int i=0; i< topCoordinates.size(); i++) {
        	DoubleCoordinate d1 = topCoordinates.get(i);
        	DoubleCoordinate d2 = bottomCoordinates.get(i);
        	DoubleCoordinate tempD = new DoubleCoordinate();
        	float deltaX, deltaY, deltaZ, newY;
        	
        	tempD.addCoordinate(d1.x1, d1.y1, d1.z1, d1.x2, d1.y2, d1.z2);
        	deltaY = (d2.y2 - d1.y1) / heightDivisions;
        	newY = d1.y1;
        	
        	for(int j=0; j<heightDivisions; j++) {
        		newY += deltaY;
        		
        		squarePoints[] squareObject = new squarePoints[4];
    			for(int k=0; k< squareObject.length; k++) {
        	        squareObject[k] = new squarePoints();
                }
    			
    			// connect the top and bottom coordinate arraylist by using deltaY
    			squareObject[0].addPoint(d1.x1,newY-deltaY,d1.z1);
                squareObject[1].addPoint(d2.x2,newY,d2.z2);
                squareObject[2].addPoint(d2.x1,newY,d2.z1);
                squareObject[3].addPoint(d1.x2,newY-deltaY,d1.z2);
                
                createTriangle(squareObject);
        	}
        }
    }

    /**
     * makeCone - Create polygons for a cone with unit height, centered at the
     * origin, with separate number of radial subdivisions and height
     * subdivisions.
     *
     * @param radius - Radius of the base of the cone
     * @param radialDivision - number of subdivisions on the radial base
     * @param heightDivisions - number of subdivisions along the height
     *
     * Can only use calls to addTriangle()
     */
    public void makeCone (float radius, int radialDivisions, int heightDivisions)
    {
        if( radialDivisions < 3 )
            radialDivisions = 3;

        if( heightDivisions < 1 )
            heightDivisions = 2;

        float angle = 360.0f/radialDivisions;
        float angleCounter = angle;
        float x0, y0, z0, x1, y1, z1, x2, y2, z2, endX, endY, endZ;
        float deltaX1, deltaX2, deltaY1, deltaY2, deltaZ;
        ArrayList<DoubleCoordinate> faceCoordinates = new ArrayList<DoubleCoordinate>();
        
        // top face
        x0 = 0f;
        y0 = 0f;
        z0 = 0.5f;
        for(int i=0; i<radialDivisions; i++) {
        	DoubleCoordinate d = new DoubleCoordinate();
        	x1 = (float)(radius*Math.cos(Math.toRadians(angleCounter-angle)));
            y1 = (float)(radius*Math.sin(Math.toRadians(angleCounter-angle)));
            z1 = z0;
            x2 = (float)(radius*Math.cos(Math.toRadians(angleCounter)));
            y2 = (float)(radius*Math.sin(Math.toRadians(angleCounter)));
            z2 = z0;
        	
            // System.out.println(angleCounter+" : "+x0+" : "+y0+" : "+z0+" : "+x1+" : "+y1+" : "+z1+" : "+x2+" : "+y2+" : "+z2);
            addTriangle(x0, y0, z0,
        			x1, y1, z1,
        			x2, y2, z2);
            
            angleCounter += angle;
            d.addCoordinate(x1, y1, z1, x2, y2, z2);
            faceCoordinates.add(d);		// add coordinates to faceCoordinates
        }
        
        // body
        endX = 0f;
        endY = 0f;
        endZ = -0.5f;
        for(int i=0; i< faceCoordinates.size(); i++) {
        	DoubleCoordinate d = faceCoordinates.get(i);
        	DoubleCoordinate tempD = d;
        	deltaX1 = (d.x1 - endX) / heightDivisions;
        	deltaX2 = (d.x2 - endX) / heightDivisions;
        	deltaY1 = (d.y1 - endY) / heightDivisions;
        	deltaY2 = (d.y2 - endY) / heightDivisions;
        	deltaZ = (endZ - d.z1) / heightDivisions;
        	
        	for(int j=0; j<heightDivisions; j++) {
        		
        		// for base of the cone just create a regular triangle 
        		if(j == (heightDivisions-1)) {
	        		x0 = tempD.x1;
		        	y0 = tempD.y1;
		        	z0 = tempD.z1;
		        	x1 = endX;
		        	y1 = endY;
		        	z1 = endZ;
		        	x2 = tempD.x2;
		        	y2 = tempD.y2;
		        	z2 = tempD.z2;
		        	
	        		addTriangle(x0, y0, z0,
	            			x1, y1, z1,
	            			x2, y2, z2);
	        		
	        		break;
	        	}
        		// for others create a square object
	        	else {
	        		
	        		squarePoints[] squareObject = new squarePoints[4];
	    			
	            	for(int k=0; k< squareObject.length; k++) {
	        	        squareObject[k] = new squarePoints();
	                }
	            	
	            	squareObject[0].addPoint(tempD.x1, tempD.y1, tempD.z1);
	                squareObject[1].addPoint(tempD.x1 - deltaX1, tempD.y1 - deltaY1, tempD.z1 + deltaZ);
	                squareObject[2].addPoint(tempD.x2 - deltaX2, tempD.y2 - deltaY2, tempD.z2 + deltaZ);
	                squareObject[3].addPoint(tempD.x2, tempD.y2, tempD.z2);
	            	
	            	tempD.x1 -= deltaX1;
	        		tempD.y1 -= deltaY1;
	        		tempD.z1 += deltaZ;
	        		
	        		tempD.x2 -= deltaX2;
	        		tempD.y2 -= deltaY2;
	        		tempD.z2 += deltaZ;
	        		
	        		createTriangle(squareObject);
	        	}
        	}
        	
        }
    }

    /**
     * makeSphere - Create sphere of a given radius, centered at the origin,
     * using spherical coordinates with separate number of thetha and
     * phi subdivisions.
     *
     * @param radius - Radius of the sphere
     * @param slides - number of subdivisions in the theta direction
     * @param stacks - Number of subdivisions in the phi direction.
     *
     * Can only use calls to addTriangle
     */
    public void makeSphere (float radius, int slices, int stacks)
    {
        if( slices < 3 )
            slices = 3;

        if( stacks < 3 )
            stacks = 3;

        float startX, startY, startZ;
        float theta, phi;
        float x1, y1, z1, x2, y2, z2;
        ArrayList<DoubleCoordinate> topCoordinates = new ArrayList<DoubleCoordinate>();
        
        startX = 0f;
        startY = 0f;
        startZ = 0.5f;
        
        theta = 360.0f / slices;
        phi = 180.0f / stacks;
        
        for(float p=0; p< 180; p += phi) {
        	
        	// for initial position make triangles and store values in topCoordinates
        	if(p == 0 ) {
	        	for(float t=0; t< 360; t += theta) {
	        		DoubleCoordinate d = new DoubleCoordinate();
	        		
	        		x1 = (float) (radius*Math.cos(Math.toRadians(t))*Math.sin(Math.toRadians(p + phi)));
	        		y1 = (float) (radius*Math.sin(Math.toRadians(t))*Math.sin(Math.toRadians(p + phi)));
	        		z1 = (float) (radius*Math.cos(Math.toRadians(p + phi)));
	        		
	        		x2 = (float) (radius*Math.cos(Math.toRadians(t + theta))*Math.sin(Math.toRadians(p + phi)));
	        		y2 = (float) (radius*Math.sin(Math.toRadians(t + theta))*Math.sin(Math.toRadians(p + phi)));
	        		z2 = (float) (radius*Math.cos(Math.toRadians(p + phi)));
	        				
	        		//System.out.println(t+" :: "+x0+" : "+y0+" : "+z0+" | "+x1+" : "+y1+" : "+z1+" | "+x2+" : "+y2+" : "+z2);
	        		addTriangle(startX, startY, startZ,
	        					 x1, y1, z1,
	        					x2, y2, z2);
	        		
	        		d.addCoordinate(x1, y1, z1, x2, y2, z2);
	        		topCoordinates.add(d);
	        	}
        	}
        	// for all other positions convert to square values based on 3D spherical coordinates
        	else {
        		int counter = 0;
        		for(float t=0; t < 360; t += theta) {
        			DoubleCoordinate d = topCoordinates.get(counter);
        			DoubleCoordinate tempD = new DoubleCoordinate();
        			squarePoints[] squareObject = new squarePoints[4];
	    			
	            	for(int k=0; k< squareObject.length; k++) {
	        	        squareObject[k] = new squarePoints();
	                }
	            	
	            	x1 = (float) (radius*Math.cos(Math.toRadians(t))*Math.sin(Math.toRadians(p + phi)));
	        		y1 = (float) (radius*Math.sin(Math.toRadians(t))*Math.sin(Math.toRadians(p + phi)));
	        		z1 = (float) (radius*Math.cos(Math.toRadians(p + phi)));
	        		
	        		x2 = (float) (radius*Math.cos(Math.toRadians(t + theta))*Math.sin(Math.toRadians(p + phi)));
	        		y2 = (float) (radius*Math.sin(Math.toRadians(t + theta))*Math.sin(Math.toRadians(p + phi)));
	        		z2 = (float) (radius*Math.cos(Math.toRadians(p + phi)));
	            	
	            	squareObject[0].addPoint(d.x1, d.y1, d.z1);
	                squareObject[1].addPoint(x1, y1, z1);
	                squareObject[2].addPoint(x2, y2, z2);
	                squareObject[3].addPoint(d.x2, d.y2, d.z2);
	                
	                createTriangle(squareObject);
	                
	                tempD.addCoordinate(x1, y1, z1, x2, y2, z2);
	        		topCoordinates.remove(counter);
	        		topCoordinates.add(counter++, tempD);
        		}
        	}
        }
    }

}
