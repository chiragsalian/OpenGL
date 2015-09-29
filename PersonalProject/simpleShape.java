//
// simpleShape.java
//
// Class that represnts a shape to be tessellated; cgShape, which includes
// all student code, is derived from this class.
//
// Of note is the protected method addTriangles() which is what students
// should use to define their tessellations.
//
// Students are not to modify this file.
//

import java.awt.*;
import java.nio.*;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import java.util.*;

public class simpleShape
{
    /**
     * our vertex points
     */
    private Vector<Float> points;
    private short nVerts;

    /**
     * our array elements
     */
    private Vector<Short> elements;

    /**
     * our normals
     */
    private Vector<Float> normals;

    /**
     * our texture coords 
     */
    private Vector<Float> uv;
    
    /**
     * constructor
     */
    public simpleShape()
    {
        points = new Vector<Float>();
        elements = new Vector<Short>();
        normals = new Vector<Float>();
        uv = new Vector<Float>();
        nVerts = 0;
    }
    
    /**
     * add a triangle to the shape
     */
    protected void addTriangle (float x0, float y0, float z0, 
                                float x1, float y1, float z1, 
                                float x2, float y2, float z2)
    {
    	float u0, v0, u1, v1, u2, v2;
    	
    	u0 = getU(x0, y0, z0);
    	v0 = getV(x0, y0, z0);
    	u1 = getU(x1, y1, z1);
    	v1 = getV(x1, y1, z1);
    	u2 = getU(x2, y2, z2);
    	v2 = getV(x2, y2, z2);
    	
        points.add (new Float(x0));
        points.add (new Float(y0));
        points.add (new Float(z0));
        points.add (new Float(1.0f));
        elements.add (new Short(nVerts));
        nVerts++;
        
        points.add (new Float(x1));
        points.add (new Float(y1));
        points.add (new Float(z1));
        points.add (new Float(1.0f));
        elements.add (new Short(nVerts));
        nVerts++;
        
        points.add (new Float(x2));
        points.add (new Float(y2));
        points.add (new Float(z2));
        points.add (new Float(1.0f));
        elements.add (new Short(nVerts));
        nVerts++;
        
        // calculate the normal
        float ux = x1 - x0;
        float uy = y1 - y0;
        float uz = z1 - z0;
        
        float vx = x2 - x0;
        float vy = y2 - y0;
        float vz = z2 - z0;
        
        float nx = (uy * vz) - (uz * vy);
        float ny = (uz * vx) - (ux * vz);
        float nz = (ux * vy) - (uy * vx);
        
        // Attach the normal to all 3 vertices
        for (int i=0; i < 3; i++) {
            normals.add (new Float (nx));
            normals.add (new Float (ny));
            normals.add (new Float (nz));
        }
        
        // Attach the texture coords
        System.out.println(u0+" : "+v0+" : "+u1+" : "+v1+" : "+u2+" : "+v2);
        uv.add (new Float (u0));
        uv.add (new Float (v0));
        uv.add (new Float (u1));
        uv.add (new Float (v1));
        uv.add (new Float (u2));
        uv.add (new Float (v2));
    }

    
    private float getU(float x, float y, float z) {
    	float u;
    	u = (float) (Math.toDegrees(Math.acos(z / Math.sqrt((x * x) + (y * y) + (z * z)))) + 90) / 180f;
    	//u = (float) (Math.toDegrees(Math.atan(x/z))) / 360.0f ;
    	//u = (float) (Math.toDegrees(Math.acos(z / Math.sqrt((x * x) + (y * y) + (z * z)))) + 90) / 3.14f;
    	//u = (float) (Math.toDegrees(Math.atan(x/z))) / (2.0f * 3.14f) ;
    	//u = atan2(x/radius,y/radius)/2*pi; pi = 3.14
    	return u;
	}
    
    private float getV(float x, float y, float z) {
    	float v ;
    	v = (float) (Math.toDegrees(Math.atan(y / x)) + 180) / 360f;
    	//v = (float) ((float) 1 - (Math.toDegrees(Math.acos(y)) / 180.0f)); 
    	//v = (float) (Math.toDegrees(Math.atan(y / x)) + 180) / (2.0f * 3.14f);
    	//v = (float) ((float) 1 - (Math.toDegrees(Math.acos(y)) / 3.14f));
    	// v = asin(y/radius)/pi;
    	return v;
	}

	
    public Buffer getUV ()
    {
        float v[] = new float[uv.size()];
        for (int i=0; i < uv.size(); i++) {
            v[i] = (uv.elementAt(i)).floatValue();
        }
        return FloatBuffer.wrap (v);
    }

	/**
     * add a triangle to the current shape using supplied normals
     */
    protected void addTriangleWithNorms(
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float nx0, float ny0, float nz0,
        float nx1, float ny1, float nz1,
        float nx2, float ny2, float nz2 )
    {
        points.add( new Float(x0) );
        points.add( new Float(y0) );
        points.add( new Float(z0) );
        points.add( new Float(1.0f) );
        elements.add( new Short(nVerts));
        nVerts++;

        normals.add( new Float(nx0) );
        normals.add( new Float(ny0) );
        normals.add( new Float(nz0) );

        points.add( new Float(x1) );
        points.add( new Float(y1) );
        points.add( new Float(z1) );
        points.add( new Float(1.0f) );
        elements.add( new Short(nVerts));
        nVerts++;

        normals.add( new Float(nx1) );
        normals.add( new Float(ny1) );
        normals.add( new Float(nz1) );

        points.add( new Float(x2) );
        points.add( new Float(y2) );
        points.add( new Float(z2) );
        points.add( new Float(1.0f) );
        elements.add( new Short(nVerts));
        nVerts++;

        normals.add( new Float(nx2) );
        normals.add( new Float(ny2) );
        normals.add( new Float(nz2) );
    }

    /**
     * clear the shape
     */
    public void clear()
    {
        points= new Vector<Float>();
        elements = new Vector<Short>();
        normals = new Vector<Float>();
        nVerts = 0;
    }

    /**
     * get the vertex points for the current shape
     */
    public Buffer getVertices()
    {
        float v[] = new float[points.size()];
        for( int i=0; i < points.size(); i++ ) {
            v[i] = (points.elementAt(i)).floatValue();
        }
        return FloatBuffer.wrap( v );
    }

    /**
     * get the normals for the current shape
     */
    public Buffer getNormals()
    {
        float v[] = new float[normals.size()];
        for( int i=0; i < normals.size(); i++ ) {
            v[i] = (normals.elementAt(i)).floatValue();
        }
        return FloatBuffer.wrap( v );
    }

    /**
     * get the array of elements for the current shape
     */
    public Buffer getElements()
    {
        short e[] = new short[elements.size()];
        for( int i=0; i < elements.size(); i++ ) {
            e[i] = (elements.elementAt(i)).shortValue();
        }

        return ShortBuffer.wrap( e );
    }

    public short nVertices()
    {
        return nVerts;
    }

}
