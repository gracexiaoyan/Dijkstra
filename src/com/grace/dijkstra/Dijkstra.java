package com.grace.dijkstra;

import java.util.ArrayList;
import java.util.List;

public class Dijkstra {
	private final int            MAX_VERTS    = 5;
    private final int            INFINITY    = 1000000;
    private Vertex[]            vertexList;            // list of vertices
    private int[][]                adjMat;                // adjacency matrix, include edge distance info
    private int                    nVerts;                // current number of vertices
    private int                    nTree;                    // number of vertices in tree
    private DistanceParent[]    shortestPath;                    // array for shortest-path data
    private int                    currentVertex;            // current vertex
    private int                    startToCurrentDistance;        // distance to currentVert

    public static void main(String[] args){
    	Dijkstra wdg = new Dijkstra();
    	wdg.addVertex('A');
    	wdg.addVertex('B');
    	wdg.addVertex('C');
    	wdg.addVertex('D');
    	wdg.addVertex('E');
    	
    	wdg.addEdge(0, 1, 5);
    	wdg.addEdge(1, 2, 4);
    	wdg.addEdge(2, 3, 8);
    	wdg.addEdge(3, 2, 8);
    	wdg.addEdge(3, 4, 6);
    	wdg.addEdge(0, 3, 5);
    	wdg.addEdge(2, 4, 2);
    	wdg.addEdge(4, 1, 3);
    	wdg.addEdge(0, 4, 7);
    	
//    	wdg.addEdge(2, 0, 10);
//    	wdg.addEdge(4, 0, 3);
    	
    	wdg.findShortestPath(2);
    }
    
    public Dijkstra()
    {
        vertexList = new Vertex[MAX_VERTS];
        // adjacency matrix
        adjMat = new int[MAX_VERTS][MAX_VERTS];
        nVerts = 0;
        nTree = 0;
        for ( int j = 0; j < MAX_VERTS; j++ )
        {
            // set adjacency
            for ( int k = 0; k < MAX_VERTS; k++ )
            {
                adjMat[j][k] = INFINITY;
            }
        }        
        shortestPath = new DistanceParent[MAX_VERTS]; // shortest paths
    }

    /**
     * Dijkstra
     */
    public void findShortestPath(int startTree)
    {
        vertexList[startTree].isInTree = true;//put the src vertex in tree
        nTree = 1;
        
        // initiate the shortest path, get distance from adjMat
        for ( int i = 0; i < nVerts; i++ )
        {
            shortestPath[i] = new DistanceParent( startTree, adjMat[startTree][i] );
            shortestPath[i].addParent(vertexList[startTree].label);
        }
        
        while( nTree < nVerts )
        {
            int indexMin = getMinFromShortestPath();// get the index of vertex that has the shortest distance to src

            int minDist = shortestPath[indexMin].distance;
            
            if ( minDist == INFINITY ) 
            { 
                System.out.println( "There are unreachable vertices" );
                break; // sPath is complete
            } else
            {
                currentVertex = indexMin; // choose the vertex which has the shortest distance as the next vertex in tree
                startToCurrentDistance = shortestPath[indexMin].distance;//get the shortest distance
            }

            vertexList[currentVertex].isInTree = true; // put the vertex in tree
            nTree++;
            adjust_sPath(); // update the shortest paths
        }//end while

        displayPaths(); // display sPath[] contents
        
        nTree = 0; // clear tree
        for(int j=0; j<nVerts; j++)
        {
            vertexList[j].isInTree = false;
        }
    }

    /**
     * update the shortest path
     */
    public void adjust_sPath()
    {
        for ( int i=0; i < nVerts; i++ )
        {
        	// calculate distance for one sPath entry
            int currentToFringe = adjMat[currentVertex][i];//  the distance from current vertex to other vertex
            // calculate distance from src to the next vertex:
            // the distance from src to current vertex plus the distance from current vertex to the next vertex
            int startToFringe = startToCurrentDistance + currentToFringe;
            
            // compare the calculated distance to the distance in shortest path
            if ( startToFringe < shortestPath[i].distance ) // if the new distance is shorter then update shortest path
            { 
                shortestPath[i].parentVert = currentVertex;
                shortestPath[i].distance = startToFringe;
                // update parent list, just for print the path. if we only need the distance then we don't need this code
                List<Character> newParent = shortestPath[currentVertex].getParentList();
                shortestPath[i].replaceOldParent(newParent);
                shortestPath[i].addParent(vertexList[currentVertex].label);
            }//end if
        }//end for
    }

    /**
     * get the index of vertex that has the shortest path from src
     * @return the index of vertex that has the shortest path from src
     */
    public int getMinFromShortestPath()
    {
        
        int minDist = INFINITY; // assume minimum
        int indexMin = 0;
        for ( int j = 0; j < nVerts; j++ ) // for each vertex,

        { // only get vertex that is not in tree and
            if ( !vertexList[j].isInTree && // smaller than old one
                    shortestPath[j].distance < minDist )
            {
                minDist = shortestPath[j].distance;
                indexMin = j; // update minimum
            }
        } // end for

        return indexMin;
    }
    
    /**
     * dispaly the path
     */
    public void displayPaths()
    {
        for ( int j = 0; j < nVerts; j++ ) // display contents of sPath[]
        {
            System.out.print( "Destination " + vertexList[j].label + "=" ); // B=

            if ( shortestPath[j].distance == INFINITY )
            {
                System.out.print( "inf" ); // inf
            }
            else
            {
                System.out.print( shortestPath[j].distance ); // 50
            }
//            char parent = vertexList[shortestPath[j].parentVert].label;
//            System.out.print( "(" + parent + ") " ); // (A)
            System.out.print( "(" + shortestPath[j].printParent() + ") " ); 
            System.out.println( "" );
        }
        
    }
    
    /**
     * add distance
     * @param start 
     * @param end 
     * @param distance 
     */
    public void addEdge( int start, int end, int distance )
    {
        adjMat[start][end] = distance;

    }
    
    /**
     * add vertex
     * 
     * @param lab
     */
    public void addVertex( char lab ) // argument is label
    {
        vertexList[nVerts++ ] = new Vertex( lab );
    }
}

class DistanceParent {
	public int    distance;    // distance from start to this vertex
    public int    parentVert; // current parent of this vertex    
    private List<Character> parentList;

    public DistanceParent( int pv, int d ) // constructor
    {
        distance = d;
        parentVert = pv;
    }
    
    public void addParent(char parent){
    	if(parentList == null){
    		parentList = new ArrayList<Character>();
    	}
    	parentList.add(parent);
    }
    
    public List<Character> getParentList(){
    	return parentList;
    }
    
    public void replaceOldParent(List<Character> newParent){
    	if(parentList == null){
    		parentList = new ArrayList<Character>();
    	}
    	else{
    		parentList.clear();
    	}
    	parentList.addAll(newParent);
    }
    
    public String printParent(){
    	if(parentList == null){
    		return "";
    	}
    	String str = "";
    	for(Character c : parentList){
    		str = str + c + " ";
    	}
    	return str;
    }
}

class Vertex {
	public char        label;        // label (e.g. 'A')
    public boolean    isInTree;

    public Vertex( char lab ) // constructor
    {
        label = lab;
        isInTree = false;
    }
}
