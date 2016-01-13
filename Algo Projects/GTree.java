/**
 * CS401 DP Project Formulation 1 & 3
 * For formulation 3: use command GTree PinInputfile gateLibraryfile
 * 
 * For formulation 1: use command GTree PinInputfile -d delay_value
 * for default value: use command GTree PinInputfile
 * @author Bhavin Chauhan 
 *
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.in;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GTree {

    List<Float> pins;                          // Stores arrival times of pins from input file
    int pin_size;                               // Size of input
    int gate_size;                              // Size of gate library
    int cost;                                   // Cost of solution
    float[][] opt;                             // Memoize Optimal solution for subsequence of pins
    int[][] c_opt;                             // To Construct Optimal solution
    List<Float> gates;                         // Stores gate details from gate library file
    List<String> result;                        // Store postfix result of optimal tree
    TreeNode root;                              // Root of gateTree used for formulation 3 

    private class TreeNode {

        TreeNode left;
        TreeNode right;
        int type;                               // To track if node is leaf or not
        int gate[] = new int[gates.size()*2];       // Array of possible gates
        int sol_size;                           // Total possible solutions at any level
        float delay[] = new float[gates.size()*2];// Delay for each of solution
        float cost[] = new float[gates.size()*2]; // Cost for each solution
        String rs[] = new String[gates.size()*2];   // Postfix notation for each solution

        TreeNode(TreeNode left, int type, TreeNode right) {
            this.left = left;
            this.type = type;
            this.right = right;
            float c1, c2, d1 = 0, d2;

            if (type >= 0 && (left == null || right == null)) {
                this.sol_size = 1;                        /* Leaf node: Pin itself */

                this.cost[0] = 0;
                this.delay[0] = pins.get(type);
                this.rs[0] = "p" + this.type;
            } else if (type == 0) {

                int j = 0;                              /* Basic gate node with two leaf nodes as input*/

                for (int i = 0; i < gates.size() / 2; i++) {
                    c1 = gates.get(2 * i);
                    d1 = max(left.delay[0], right.delay[0]) + gates.get(2 * i + 1);
                    boolean flag = false;
                    for (int t = j - 1; t >= 0; t--) {
                        if (delay[t] == d1 && cost[t] >= c1) {
                            this.cost[t] = c1;
                            this.delay[t] = d1;
                            this.gate[t] = i;
                            this.rs[t] = left.rs[0] + " " + right.rs[0] + " g" + i;
                            flag = true;
                        }
                        if (delay[t] == d1 && cost[t] < c1) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        this.cost[j] = c1;
                        this.delay[j] = d1;
                        this.gate[j] = i;
                        rs[j] = left.rs[0] + " " + right.rs[0] + " g" + i;
                        j++;
                        c2 = c1;
                        d2 = d1;
                    }
                }
                this.sol_size = j;
                /*    System.out.println(" Distinct sol:" + j);
                 for (int i = 0; i < j; i++) 
                 System.out.println("\t<" + cost[i] + "," + delay[i] + ">");*/
            } else {
                int j = 0, k = 0, i = 0, n = 0;       // Case when both children of node are not leaf nodes 

                for (i = 0; i < gates.size() / 2; i++) {
                    j = 0;
                    k = 0;
                    while (j < left.sol_size && k < right.sol_size) {
                        c2 = left.cost[j] + right.cost[k] + gates.get(2 * i);
                        d2 = max(left.delay[j], right.delay[k]) + gates.get(2 * i + 1);
                        if (n > 0) {
                            boolean flag = false;
                            for (int t = n - 1; t >= 0; t--) {
                                if (delay[t] == d2 && cost[t] >= c2) {
                                    this.cost[t] = c2;
                                    this.delay[t] = d2;
                                    this.gate[t] = i;
                                    this.rs[t] = left.rs[j] + " " + right.rs[k] + " g" + i;
                                    c1 = c2;
                                    d1 = d2;
                                    flag = true;
                                    break;
                                }
                                if (delay[t] == d2 && cost[t] < c2) {
                                    flag = true;
                                    break;
                                }

                            }
                            if (!flag) {
                                this.cost[n] = c2;
                                this.delay[n] = d2;
                                this.gate[n] = i;
                                this.rs[n] = left.rs[j] + " " + right.rs[k] + " g" + i;

                                n++;
                                c1 = c2;
                                d1 = d2;
                            }
                        } else {
                            this.cost[n] = c2;
                            this.delay[n] = d2;
                            this.gate[n] = i;
                            this.rs[n] = left.rs[j] + " " + right.rs[k] + " g" + i;

                            n++;
                            c1 = c2;
                            d1 = d2;
                        }
                        if (left.delay[j] >= right.delay[k]) {
                            k++;
                        } else if (left.delay[j] < right.delay[k]) {
                            j++;
                        } else {
                            j++;
                            k++;
                        }
                    }

                }
                /*     System.out.println(" Distinct sol:" + n);
                 for (i = 0; i < n; i++) {
                 System.out.println("<" + cost[i] + "," + delay[i] + ">");
                 }*/
                sol_size = n;

            }
        }
    }

    float min(float a, float b) {
        return a < b ? a : b;
    }

    float max(float a, float b) {
        return a > b ? a : b;
    }

    GTree(String s) {                                           //GTree constructor
        int n = 0;
        try {
            FileInputStream fstream = new FileInputStream(s + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            strLine = br.readLine();
            n = Integer.parseInt(strLine);
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        pin_size = n;
        opt = new float[n][n];
        c_opt = new int[n][n];

        for (int i = 0; i < n; i++) {                   /*Initialize Memoization tables*/

            for (int j = 0; j < n; j++) {
                opt[i][j] = 0f;
                c_opt[i][j] = 0;
            }
        }

    }

    void getPins(String arg) {
        try {
            pins = new ArrayList<>();
            FileInputStream fstream = new FileInputStream(arg + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            System.out.println("Input:");
            br.readLine();           //Skip input size
            for (int i = 0; i < pin_size; i++) {
                if ((strLine = br.readLine()) != null) {             //Read File Line By Line
                    System.out.println("p" + (i) + " :" + strLine);
                    pins.add(Float.parseFloat(strLine));

                } else {
                    System.out.println("Invalid pin file");
                    break;
                }
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    void getGates(String arg) {
        gates = new ArrayList<>();
        Float[] t = new Float[2];
        try {

            FileInputStream fstream = new FileInputStream(arg + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            strLine = br.readLine();            //Read File Line By Line
            System.out.println("Gate\tCost\tDelay");
            if (strLine == null) {
                System.out.println("Invalid gate library file");
            } else {
                gate_size = Integer.parseInt(strLine);
            }
            int i = 0;
            while ((strLine = br.readLine()) != null) {
                String[] tokens = strLine.split(" ");
                System.out.println("g" + (i++) + "\t" + tokens[0] + "\t" + tokens[1]);
                gates.add(Float.parseFloat(tokens[0]));
                gates.add(Float.parseFloat(tokens[1]));
            }
            in.close();         //Close the input stream
        } catch (Exception e) {
            System.err.println("Error: gates:  " + e.getMessage());
        }
    }

    public void createGateTree() {
        final Stack<TreeNode> nodes = new Stack<TreeNode>();
        for (int i = 0; i < result.size(); i++) {
            String ch = result.get(i);
            if (ch == "g") {
                TreeNode rightNode = nodes.pop();
                TreeNode leftNode = nodes.pop();
                if (rightNode.sol_size == 1 && leftNode.sol_size == 1) {   /* Gate node with two leaf nodes*/
                    nodes.push(new TreeNode(leftNode, 0, rightNode));
                } else if (rightNode.sol_size != 1 || leftNode.sol_size != 1) {  /*Gate node with one or more non leaf node*/
                    nodes.push(new TreeNode(leftNode, -1, rightNode));
                }

            } else {
                float c = pins.get(Integer.parseInt(ch));
                nodes.add(new TreeNode(null, Integer.parseInt(ch), null));  /* Leaf nodes */
            }
        }
        root = nodes.pop();
    }

    private void print_sol(int i, int i0) {
        /*Construct optimal solution  and result will be used to construct tree for formulation 3*/
        if (i == i0) {
            System.out.print(" p" + i0);
            result.add(String.valueOf(i0));
        } else if (i == i0 - 1) {
            System.out.print(" p" + i + " p" + i0 + " g0");
            result.add(String.valueOf(i));
            result.add(String.valueOf(i0));
            result.add("g");
        } else {
            print_sol(i, c_opt[i][i0] - 1);
            print_sol(c_opt[i][i0], i0);
            System.out.print(" g0");
            result.add("g");
        }
    }

    private float formulation1(int i, int j, float d) {
        if (opt[i][j] != 0f) {
            return opt[i][j];
        }
        if (i == j) {
            opt[i][j] = pins.get(i);
            return 0 + pins.get(i);  
        } else if (i == (j - 1)) {
            opt[i][j] = d + max(pins.get(i), pins.get(j)); //delay
            c_opt[i][j] = i;
            return opt[i][j];
        }
        float min_time = 99999;
        for (int L = 1; L < (j - i); L++) {
            for (int k = 0; i + k < j; k = k + L) {
                float prev_min = min_time;
                min_time = min(min_time, max(formulation1(i, i + k, d), formulation1(i + k + 1, j, d)) + d);
                if (prev_min != min_time) {
                    c_opt[i][j] = i + k + 1;
                }
       // System.out.println("\t Main: "+i+","+(i+k)+"sol->"+formulation1(i,i+k,d)+" : "+(i+k+1)+","+j+
                //               " ->sol:"+formulation1(i+k+1,j,d)+" min"+min_time);             //Print statement for debugging
            }
        }
        opt[i][j] = min_time;
        return min_time;
    }

    public static void main(String[] args) {
        float gate_delay = 1;
        String hint="For formulation 1: use command GTree PinInputfile -d delay_value\n" +
                    "for default value: use command GTree PinInputfile\n" +
                    "\n" +
                    "For formulation 3: use command GTree PinInputfile gateLibraryfile";
        if (args.length == 0) {
            System.out.println("No Command Line arguments supplied!");
            System.out.print(hint);
            System.exit(0);
        } 
            GTree gateTree;
            gateTree = new GTree(args[0]);
            gateTree.getPins(args[0]);
            gateTree.result = new ArrayList<String>(gateTree.pin_size * 2 - 1);
            switch (args.length) {

                case 1:  /*Formulation 1 with default delay=1 :  GTree test1 */
                    System.out.println("Formulation 1: Gate delay=" + gate_delay);
                    System.out.println("Arrival time of solution:" + String.valueOf(gateTree.formulation1(0, gateTree.pin_size - 1, gate_delay)));
                    System.out.println("Cost: " + (gateTree.pin_size - 1));
                    System.out.print("Postfix Sequence:");
                    gateTree.print_sol(0, gateTree.pin_size - 1);
                    System.out.println();
                    break;
                case 3:    /* Formulation 1 with use given delay: GTree test1 -d 3.0  */
                    try {
                        gate_delay = Float.parseFloat(args[2]);
                    } catch (Exception e) {
                        System.out.println("Third parameter is not valid delay. Program will exit!");
                        System.out.print(hint);
                        System.exit(1);
                    }
                    System.out.println("Formulation 1: Gate delay=" + gate_delay);
                    System.out.println("Arrival time of solution:" + String.valueOf(gateTree.formulation1(0, gateTree.pin_size - 1, gate_delay)));
                    System.out.println("Cost: " + (gateTree.pin_size - 1));
                    System.out.print("Postfix Sequence:");
                    gateTree.print_sol(0, gateTree.pin_size - 1);
                    System.out.println();
                    break;
                case 2:  /*Command of form : GTree test1 lib1 */
                    gateTree.getGates(args[1]);    
                    System.out.println("Formulation 3: ");
                    int min_gate_index = gateTree.gates.lastIndexOf(gateTree.gates.get(1)); // For case when two gates have some delay but different cost

                    Float T = gateTree.formulation1(0, gateTree.pin_size - 1, gateTree.gates.get(min_gate_index));
                    gateTree.result = new ArrayList<String>(gateTree.pin_size * 2 - 1);
                    gateTree.print_sol(0, gateTree.pin_size - 1);
                    System.out.print('\r');
                    gateTree.createGateTree();
                    for (int i = gateTree.root.sol_size - 1; i >= 0; i--) {
                        System.out.println("Arrival time of solution:" + gateTree.root.delay[i] + "\nCost:" + gateTree.root.cost[i]
                                + "\nPostfix Sequence:" + gateTree.root.rs[i] + "\n");
                    }
                    break;
                default:
                    System.out.println("Invalid command line arguments!");
            }                                       /*End of switch statement*/

        }
    }

