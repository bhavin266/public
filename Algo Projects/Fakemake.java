/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Bhavin
 */

import java.io.*;
import java.util.Scanner;
import java.util.*;
import java.util.Map.Entry;
import java.util.Stack;


class Fakemake
{
    private int V;   // No. of vertices
    private LinkedList<Integer> adj[];
    private int[] timestamp;
    public static int clock;
    private boolean[] completed;
    private boolean[] upd_flag;
    HashMap<Integer, String> hmap;


    // Constructor
    @SuppressWarnings("unchecked")    
    Fakemake(int v)
    {
        V = v;
         adj = (LinkedList<Integer>[]) new LinkedList<?>[v];
        timestamp=new int[v];
        hmap = new HashMap<Integer, String>();
        for (int i=0; i<v; ++i)
        {
            adj[i] = new LinkedList();
            timestamp[i]=0;
        }
    }
    boolean checkCycle()
    {
         boolean visited[] = new boolean[V];
         Stack<Integer> stack= new Stack<Integer>();
         for(int i = 0; i < V; i++)
         {   
   //          System.out.println("call with:"+i+"="+hmap.get(i));
             if (check(i, visited, stack))
                return true;
             else
                 stack.removeAllElements();
         }
  return false;
    }
 
   private boolean check(int v, boolean[] visited, Stack<Integer> stack) {
     visited[v] = true;
     stack.push(v);
   //  System.out.println("<Stack>");
     //for(int k=0;k<stack.size();k++)
    // System.out.print(stack.elementAt(k)+" ");
    // System.out.println();
     Iterator<Integer> i = adj[v].listIterator();
     while(i.hasNext())
      {
          int n=i.next();
          if (stack.contains(n))
                return true;
          if ( !visited[n] && check(n, visited, stack) )
                return true;
      }
     
    return false;
    }

    //Function to add an edge into the graph
    void addEdge(int v, int w)
    {
        adj[v].add(w);  // Add w to v's list.
    }
 
    // A function for DFS traversal
    void traverse(int v,int current,boolean visited[])
    {
     if(v!=current)
        visited[current] = true;
    // System.out.println("\t\tCALL("+hmap.get(v)+","+hmap.get(current)+")"+timestamp[v]+" t_cur:"+timestamp[current]+" child:"+adj[current].size()+" completed:"+completed[v]);
        
     if(adj[current].size()==0)
      {
            if(timestamp[v]<timestamp[current])
                {
                    if(adj[v].size()>0&&completed[v])
                    { 
                    System.out.println("making "+hmap.get(v)+"...");
                    System.out.println("\tupdate "+hmap.get(v)+" with "+clock);
                    timestamp[v]=clock++;
                    }
                    else 
                        upd_flag[v]=true;
               
                
                }
        else
               if(completed[v]==true)
                System.out.println(hmap.get(v)+" is upto date.");
      }
      else
      { 
          Iterator<Integer> i = adj[current].listIterator();
        //  visited[v]=true;
          int n=i.next();
          int t=n;
          boolean flag=true;
          while(i.hasNext())
          {  
              flag=flag&&visited[t];
              if(timestamp[t]>timestamp[n])
                  n=t;
              t=i.next();
          }
          if(!flag)
          {
          traverse(current,n,visited);
          }
       else
          {
                 if(timestamp[current]<timestamp[n])
              { 
                  System.out.println("making "+hmap.get(current)+"...");
                  System.out.println("\tupdate "+hmap.get(current)+" with "+clock);
                    timestamp[current]=clock++;
                    upd_flag[current]=false;
              }
              else if(completed[current])
                  System.out.println(hmap.get(current)+" is upto date!");
         }
          
        if(timestamp[v]<timestamp[current])
            { if(adj[v].size()>0 && visited[v])
            {   System.out.println("making "+hmap.get(v)+"...");
                System.out.println("\tupdate "+hmap.get(v)+" with "+clock);
                timestamp[v]=clock++;
                upd_flag[v]=false;
            }
            }
       
      }
     
    
        Iterator<Integer> i = adj[v].listIterator();
        while (i.hasNext())
        {
          int n = i.next();
          if(!visited[n])
            {
                traverse(v,n,visited);
            }
                else if( visited[v] && timestamp[v]<timestamp[n])
                {
                  if(adj[v].size()>0 )
                  System.out.println("making "+hmap.get(v)+"...");
                System.out.println("\tupdate "+hmap.get(v)+" with "+clock);
                    timestamp[v]=clock++;
                    upd_flag[v]=false;
                }
             
        }
        if(upd_flag[v]&& visited[v])
            {
               if(adj[v].size()>0 )
               System.out.println("4.making "+hmap.get(v)+"...");
               System.out.println("\t4.update "+hmap.get(v)+" with "+clock);
                    timestamp[v]=clock++;
                    upd_flag[v]=false;
            }
        completed[v]=true;
     }
 
    // The function to do DFS traversal. 
    void DFS(int v)
    {
        boolean visited[] = new boolean[V];
           completed = new boolean[V];
            upd_flag = new boolean[V];
        Iterator<Integer> i = adj[v].listIterator();
            int n = i.next();
            traverse(v,n,visited);
            traverse(v,v,visited);
    }
    
    public static < Integer,String> Integer getKeyByValue(HashMap<Integer,String> map, String value) {
    for (Entry<Integer, String> entry : map.entrySet()) {
        if (Objects.equals(value, entry.getValue())) {
            return (Integer) entry.getKey();
        }
    }
    return null;
}
 
    public static void main(String args[])
    {
         Fakemake g ;
       
         int i = 0;
         if (args.length == 0) {
            System.out.println("No Command Line arguments supplied!");
            System.out.print("Enter: Fakemake filename");
            System.exit(0);
        }
        try{
            FileInputStream fstream = new FileInputStream(args[0]);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;  
            while ((strLine = br.readLine()) != null) 
              i++;
            fstream.close();         //Close the input stream
        } catch (Exception e) {
            System.err.println("Error counting nodes: " + e.getMessage());
        }
            g = new Fakemake(i);
            
      try{
           FileInputStream fstream = new FileInputStream(args[0]);
           BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
           String strLine;  i=0;
           while ((strLine = br.readLine()) != null) 
           {
           String[] tokens = strLine.split(":");
         //  System.out.print(tokens[0]);
           if(g.hmap.containsValue(tokens[0].trim()))
               i=getKeyByValue(g.hmap,tokens[0].trim());
           else
           g.hmap.put(i, tokens[0].trim()); 
           if(tokens.length>1)
           {
           //    System.out.println(tokens[0].trim() + " :" + tokens[1].trim());
               String[] tokens1 = tokens[1].trim().split(" ");
               for(int j=0;j< tokens1.length;j++)
               {
           // System.out.println(getKeyByValue(g.hmap,tokens1[j].trim())+" "+tokens1[j].trim());
               g.addEdge(i, getKeyByValue(g.hmap,tokens1[j].trim()));
           //    System.out.println("Edge from "+tokens[0].trim()+" to "+tokens1[j].trim());
              }
            }
             i++;   
            }    
            fstream.close();         //Close the input stream
        } catch (Exception e) {
            System.err.println("Error: Invalid file structure: Please check the input file");
            System.exit(0);
        }
 //     System.out.print(g.checkCycle());
      if(g.checkCycle())
      {
      System.out.println("Invalid dependencies in the file. Dependencies must be acyclic!");
            System.exit(0);
      }
      clock=1;
while(true){ 
System.out.print(">");
Scanner in = new Scanner(System.in);
String s =in.nextLine();
if(s.equals("quit")||s.equals("exit"))
    System.exit(0);
else if(s.startsWith("touch"))
{
     String[] tokens = s.split(" ");
    if(tokens.length!=2)
    {System.out.println("Invalid command. Use: touch filename");
    continue;}
    try{
     if(g.adj[getKeyByValue(g.hmap, tokens[1].trim())].size()==0)
     {g.timestamp[getKeyByValue(g.hmap, tokens[1].trim())]=clock;
     System.out.println("File \'"+tokens[1]+"\' has been modified");
     clock++;}
     else
         System.out.println("File \'"+tokens[1]+"\' is not a basic file and so it cannot be modified");
     continue;
    }
     catch (Exception e)
    {
        System.out.println("filename not found. Try again");
        continue;
    }
}
else if(s.startsWith("timestamp"))
{
     String[] tokens = s.split(" ");
     try{
     System.out.println(g.timestamp[getKeyByValue(g.hmap, tokens[1].trim())]); 
     }
      catch (Exception e)
    {
        System.out.println("filename not found. Try again");
        continue;
    }
      continue;
}
else if (s.equals("time"))
{
    System.out.println(clock);
    continue;
}
else if (s.startsWith("make"))
{
    String[] tokens = s.split(" ");
    if(tokens.length!=2)
    {System.out.println("Invalid command. Use: make filename");
    continue;}
        
    try{
    if(g.adj[getKeyByValue(g.hmap, tokens[1].trim())].size()==0)
     { System.out.println("File \'"+tokens[1]+"\' is a basic file. Make cannot be used.");
     continue;
     }
    }
    catch (Exception e)
    {
        System.out.println("filename not found. Try again");
        continue;
    }
// System.out.println(getKeyByValue(g.hmap, tokens[1].trim()));
    g.DFS(getKeyByValue(g.hmap, tokens[1].trim()));
    continue;
}
else
    System.out.println("Unidentified command\n\t Commands allowed: touch, make, timestamp, time and exit/quit");
    
    
}
     
    }

}