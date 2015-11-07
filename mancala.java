import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Node {
	State p1;
	State p2;
	int utility;
	Node parent;
	String label;
	String currMoveName;
	boolean playNewMove;
	boolean trackback;
	int depth;
	int value;
	
	Node(){
	
	}
	Node(State p1,State p2,int utility){
	this.p1=p1;
	this.p2=p2;
	this.utility=utility;
	}
	public State getP1() {
	return p1;
	}
	public void setP1(State p1) {
	this.p1 = p1;
	}
	public State getP2() {
	return p2;
	}
	public void setP2(State p2) {
	this.p2 = p2;
	}
	public int getUtility() {
	return utility;
	}
	public void setUtility(int utility) {
	this.utility = utility;
	}
	public boolean isPlayNewMove() {
	return playNewMove;
	}
	public void setPlayNewMove(boolean playNewMove) {
	this.playNewMove = playNewMove;
	}
	public boolean isTrackback() {
	return trackback;
	}
	public void setTrackback(boolean trackback) {
	this.trackback = trackback;
	}
	public int getDepth() {
	return depth;
	}
	public void setDepth(int depth) {
	this.depth = depth;
	}
	public Node getParent() {
	return parent;
	}
	public void setParent(Node parent) {
	this.parent = parent;
	}
	public String getLabel() {
	return label;
	}
	public void setLabel(String label) {
	this.label = label;
	}
	public int getValue() {
	return value;
	}
	public void setValue(int value) {
	this.value = value;
	}
	public String getCurrMoveName() {
	return currMoveName;
	}
	public void setCurrMoveName(String currMoveName) {
	this.currMoveName = currMoveName;
	}
	
}

class State {
	int[] pits1;
	int[] pits2;
	State(){
	
	}
	State(int pits1[],int pits2[]){
	this.pits1 = pits1;
	this.pits2 = pits2;
	}
	public int[] getPits1() {
	return pits1;
	}

	public void setPits1(int[] pits) {
	this.pits1 = pits;
	}

	public int[] getPits2() {
	return pits2;
	}

	public void setPits2(int[] pits2) {
	this.pits2 = pits2;
	}	
}

public class mancala {
	static int currentPlayer = 1; 
	static int cutoffDepth = 1; 
	static String[] pitsP1;
	static String[] pitsP2;
	static int pits1[];
	static int pits2[];
	static Node root = new Node();
	static Node minExpandNode = new Node();
	static State rootState = new State();
	static String traversalPath = "";
	static Node maxNode = new Node(null,null,Integer.MIN_VALUE);
	static Node minNode = new Node();
	static int maxP1Stones[] = null;
	static int maxP2Stones[] = null;
	static int minP1Stones[] = null;
	static int minP2Stones[] = null;
	static int maxUtility = Integer.MIN_VALUE;
	static int minUtility = Integer.MAX_VALUE;
	static final String MAX = "MAX";
	static final String MIN = "MIN";
	static final String ROOT = "ROOT";
	static StringBuffer traverseLog = new StringBuffer();
	static ArrayList<Node> globalMin = new ArrayList<Node>();
	static String rootStateClone[] = null;
	static int[] nextStatePrint = null; 
	static List<int[]> nextState = new ArrayList<int[]>();
	static List<Node> globalMax = new ArrayList<>();
	static List<Node> finalMaxNodes = new ArrayList<>();
	static int globalIndex = -1;
	static StringBuffer tempTraverseLog = new StringBuffer();
	static StringBuffer traverseLogAB = new StringBuffer();
	static List<Node> minNodesDepthOne = new ArrayList<>();
	static int task = 0;
	static StringBuffer finalTraverseLog = new StringBuffer();
	static boolean globalGameEnd = false;
	static BufferedWriter wr1 = null;
	static BufferedWriter wr = null;
	static BufferedWriter wr2 = null;
	public static void main(String[] args) {

	// TODO Auto-generated method stub
	
	BufferedReader br = null;
	File inFile = null;
	int printp1[];
	int printp2[];
	String p2 = "";
	String p1 = "";
	int mancala1 = 0;
   	int mancala2 = 0;
	try {
	wr = new BufferedWriter(new FileWriter("next_state.txt"));	
	wr1 = new BufferedWriter(new FileWriter("traverse_log.txt"));
	wr2 = new BufferedWriter(new FileWriter("traverse_log.txt"));
	 if (0 < args.length) {
	 inFile = new File(args[0]);
	  }
	        br = new BufferedReader(new FileReader(inFile));
	        task = Integer.parseInt(br.readLine());
	        	switch(task){
	        	case 1 :
	        	System.out.println("Greedy");
	        	initialize(br);
	       	if(currentPlayer==1){
	       	chooseNextMove(root); 
	       	}
	       	else if(currentPlayer==2){
	       		chooseMoveP2(root);
	       	}
	     	printp2 = maxNode.getP2().getPits2();
	       	printp1 = maxNode.getP1().getPits1();
	       	mancala1 = printp1[printp1.length-1];
	       	mancala2 = printp2[printp2.length-1];
	       	for(int i=printp2.length-2;i>=0;i--){
	       		wr.write(String.valueOf(printp2[i])+" ");
	       	}
	       	wr.newLine();
	       	for(int i=0;i<printp1.length-1;i++){
	       		wr.write(String.valueOf(printp1[i])+" ");
	       	}
	       	wr.newLine();
	       	wr.write(String.valueOf(mancala2));
	       	wr.newLine();
	       	wr.write(String.valueOf(mancala1));
	       	break;
	   
	        	case 2 :
	        	System.out.println("Minimax");
	        	Node globalMax = new Node(null,null,Integer.MIN_VALUE);
	        	initialize(br);
	        	if(currentPlayer==1){
	        	tempTraverseLog.append("Node,Depth,Value");
	        	printTraverseLog(ROOT.toLowerCase(), "", 0, Integer.MIN_VALUE, root.getP1().getPits1(), root.getP2().getPits2(), false, Integer.MAX_VALUE, Integer.MIN_VALUE, false);
		        wr1.write("Node,Depth,Value");
		        wr1.write(System.getProperty("line.separator")+"root,0,-Infinity");
	        	globalMax = chooseNextMoveMinimax(root, "", 0, Integer.MIN_VALUE, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
	        	} else if(currentPlayer==2){
	        		tempTraverseLog.append("Node,Depth,Value");
	        		wr1.write("Node,Depth,Value");
	        		wr1.write(System.getProperty("line.separator")+"root,0,-Infinity");
	        		printTraverseLog(ROOT.toLowerCase(), "", 0, Integer.MIN_VALUE, root.getP1().getPits1(), root.getP2().getPits2(), false, Integer.MAX_VALUE, Integer.MIN_VALUE, false);
		        	globalMax = chooseNextMoveMin(root, "", 0, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
	        	}
	        	printp2 = globalMax.getP2().getPits2();
		       	printp1 = globalMax.getP1().getPits1();
		       	mancala1 = printp1[printp1.length-1];
		       	mancala2 = printp2[printp2.length-1];
		       	for(int i=printp2.length-2;i>=0;i--){
		       		wr.write(String.valueOf(printp2[i])+" ");
		       	}
		       	wr.newLine();
		       	for(int i=0;i<printp1.length-1;i++){
		       		wr.write(String.valueOf(printp1[i])+" ");
		       	}
		       	wr.newLine();
		       	wr.write(String.valueOf(mancala2));
		       	wr.newLine();
		       	wr.write(String.valueOf(mancala1));
	        	//wr1.write(tempTraverseLog.toString());
	        	break;
	        	
	        	case 3 :
	        	System.out.println("Alpha-Beta");
	        	Node globalMaxAlphaBeta = new Node(null,null,Integer.MIN_VALUE);
	        	initialize(br);
	        	wr2.write("Node,Depth,Value,Alpha,Beta");
	        	wr2.write(System.getProperty("line.separator")+"root,0,-Infinity,-Infinity,Infinity");
	        	traverseLogAB.append("Node,Depth,Value,Alpha,Beta");
	        	traverseLogAB.append(System.getProperty("line.separator")+"root,0,-Infinity,-Infinity,Infinity");
	        	
	        	if(currentPlayer==1){
	        	//printTraverseLog(ROOT.toLowerCase(), "", 0, Integer.MIN_VALUE, root.getP1().getPits1(), root.getP2().getPits2(), false, Integer.MAX_VALUE, Integer.MIN_VALUE, false);
	        	globalMaxAlphaBeta = chooseNextMoveMinimax(root, "", 0, Integer.MIN_VALUE, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
	        	} else if(currentPlayer==2){
	        	globalMaxAlphaBeta = chooseNextMoveMin(root, "", 0, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
	        	}
	        	int printp2AB[] = globalMaxAlphaBeta.getP2().getPits2();
		       	int printp1AB[] = globalMaxAlphaBeta.getP1().getPits1();
		       	int mancala1AB = printp1AB[printp1AB.length-1];
		       	int mancala2AB = printp2AB[printp2AB.length-1];
		       	for(int i=printp2AB.length-2;i>=0;i--){
		       		wr.write(String.valueOf(printp2AB[i])+" ");
		       	}
		       	wr.newLine();
		       	for(int i=0;i<printp1AB.length-1;i++){
		       		wr.write(String.valueOf(printp1AB[i])+" ");
		       	}
		       	wr.newLine();
		       	wr.write(String.valueOf(mancala2AB));
		       	wr.newLine();
		       	wr.write(String.valueOf(mancala1AB));
		       	/*String x = traverseLogAB.toString().replaceAll(String.valueOf(Integer.MAX_VALUE), "Infinity");
		       	String y = x.replaceAll(String.valueOf(Integer.MIN_VALUE), "-Infinity");
	        	wr1.write(y);*/
	    	        break;
	    	        
	        	case 4:
	        	System.out.println("Competition");
	    	        break; 
	        }
	}
	    catch (Exception e) {
	    	e.printStackTrace();
	    } 

	    finally {
	        try {
	            if (br != null)br.close();
	            if (wr != null)wr.close();
	            if (wr1 != null)wr1.close();
	            if (wr2 != null)wr2.close();
	            } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }	
}
	
private static int printTraverseLog(String currMoveName, String prevMove, int depth, int utility, int[] noOfStones, int[] noOfStonesClone, 
	boolean isNewMove, int currMin, int currMax, boolean gameEnd) {
	// TODO Auto-generated method stub
	
	/*if(gameEnd){
	 * if at cutoff depth, print only eval value of node; else print inf and eval value 
	 * if(cutoff){
	 * if(freeturn){
	 * print inf
	 * }
	 * print only eval value of node
	 * }
	 * else {
	 * print inf and eval value of node
	 * }
	 * }
	 * */
	
	int finalUtil = utility;
	if(gameEnd){
		if(depth>=cutoffDepth){
			if(isNewMove){
				tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+(currMoveName.startsWith("A")?"Infinity":"-Infinity"));
			} 
			tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+(noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1]));
		} else {
			tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+(currMoveName.startsWith("A")?"Infinity":"-Infinity"));
			tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+(noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1]));
		}
	} else {
	if(!isNewMove){
	if(currMoveName.contains("A")){
	finalUtil = currMin;
	} else if(currMoveName.contains("B")) {
	finalUtil = currMax;
	}
	}
	
	if(depth<cutoffDepth){
	if(utility==Integer.MIN_VALUE){
	if(!isNewMove){
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+"-Infinity");
	}else{
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+(currMoveName.contains("A")?"Infinity":"-Infinity"));
	}}
	else if(utility==Integer.MAX_VALUE){
	if(!isNewMove){
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+"Infinity");
	} else{
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+(currMoveName.contains("A")?"Infinity":"-Infinity"));
	}
	} else{
	if(prevMove.contains("B") || prevMove.trim().equals("")){
	if(utility>currMax){
	currMax=utility;
	}
	tempTraverseLog.append(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(prevMove.trim().equals("")?0:depth)+","+currMax);
	finalUtil = currMax;
	}
	else if(prevMove.contains("A")){
	if(utility<currMin){
	currMin=utility;
	}
	tempTraverseLog.append(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(prevMove.trim().equals("")?0:depth)+","+currMin);
	finalUtil = currMin;
	}  
	} 
	
	} else if(depth==cutoffDepth && !isNewMove){
	utility = noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1];
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+utility);
	if(currMoveName.contains("A"))/*parent=min, child=max */{
	//compare with currMin
	if(utility<currMin){
	currMin=utility;
	}
	if(!currMoveName.trim().equals("") && !prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
	tempTraverseLog.append(System.getProperty("line.separator")+prevMove+","+(depth)+","+currMin);
	}
	else{
	tempTraverseLog.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+currMin);
	}
	finalUtil = currMin;
	} else if(currMoveName.contains("B")) /*parent=max, child=min */{
	utility = noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1];
	if(utility>currMax){
	currMax=utility;
	}
	if(!currMoveName.trim().equals("") && !prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
	tempTraverseLog.append(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(depth)+","+currMax);
	} else{
	tempTraverseLog.append(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(depth-1)+","+currMax);
	}
	finalUtil = currMax;
	}
	} else if(depth==cutoffDepth && isNewMove){
	if(currMoveName.contains("A")){
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+"Infinity");
	} else if(currMoveName.contains("B")) {
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+depth+","+"-Infinity");
	}
	}
	}
	/*System.out.println("-------------------------------------------------------LOGS----------------------------------------------");
	System.out.println(tempTraverseLog.toString());
	System.out.println("-------------------------------------------------------LOGS----------------------------------------------");*/
	return finalUtil;
}

private static Node chooseNextMoveMinimax(Node expandNode, String prevMove, int depth, int prevUtil, boolean isNewMove, int alpha, int beta) throws IOException {
	// TODO Auto-generated method stub
	Node maxNode = new Node();
	Node minNode = new Node();
	globalGameEnd = false;
	Node v = new Node();
	if(currentPlayer==1){
	v = new Node(null,null,Integer.MIN_VALUE);
	} else {
	v = new Node(null,null,Integer.MAX_VALUE);
	}
	boolean isNewMoveCopy = isNewMove;
	List<Node> localMax = new ArrayList<>();
	int parMax = Integer.MIN_VALUE;
	int parMin = Integer.MAX_VALUE;
	isNewMove = false;
	int noOfStones[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesClone[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesMaxClone[] = new int[noOfStones.length];
	int p1Stones[] = expandNode.getP1().getPits1();
	int p2Stones[] = expandNode.getP2().getPits2();
	maxP1Stones = expandNode.getP1().getPits1();
	maxP2Stones = expandNode.getP2().getPits2();
	int p1Temp[] = new int[expandNode.getP1().getPits1().length];
	int p2Temp[] = new int[expandNode.getP2().getPits2().length];
	int maxI = 0;
	int tempFwdTrack = 0;
	boolean gameEnd = false;
	boolean gameEndP1 = true;
	boolean gameEndP2 = true;
	boolean expand = false;
	int oppPitSum = 0;
	int ownPitSum = 0;
	int sumTrack = 0;
	int childUtil = 0;
	int parentUtil = 0;
	String nodeUtil = "";
	String currMoveName = "";
	Node currMin = new Node();
	Node currMax = new Node();
	if(currentPlayer==1){
		currMin = new Node(null,null,Integer.MAX_VALUE);
		currMax = new Node(null,null,Integer.MIN_VALUE);
	} else {
		currMin = new Node(null,null,Integer.MIN_VALUE);
		currMax = new Node(null,null,Integer.MAX_VALUE);
	}
	int currMinUtil = Integer.MAX_VALUE;
	Node parent = expandNode.getParent()!=null?expandNode.getParent():null;
	String label = "";
	Node tempNode = null;
	if(expandNode.getLabel().equals(ROOT) || expandNode.getLabel().equals(MAX)){
	expandNode.setUtility(Integer.MIN_VALUE);
	}
	else if(expandNode.getLabel().equals(MIN)) {
	expandNode.setUtility(Integer.MAX_VALUE);
	}
	if(expandNode.getLabel().equals(ROOT) ){
	depth=1;
	}
	
	int currUtility = 0;
	if(currentPlayer==1) {
	currUtility = p1Stones[p1Stones.length-1]-p2Stones[p2Stones.length-1];
	} else {
		currUtility = p2Stones[p2Stones.length-1] - p1Stones[p1Stones.length-1];
	}
	
	System.arraycopy(p1Stones, 0, noOfStones, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStones, p1Stones.length, p2Stones.length);
	System.arraycopy(p1Stones, 0, noOfStonesClone, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStonesClone, p1Stones.length, p2Stones.length);
	int currStones = 0;
	int forwardTrack = 0;
	String currUtil = "";
	String printDepth = "0";
	int previousDepth = 0;
	int tempUtil = Integer.MIN_VALUE;
	boolean isRoot = true;
	//changed code
	System.arraycopy(noOfStones, 0, p1Temp, 0, p1Temp.length);
	System.arraycopy(noOfStones, noOfStones.length/2, p2Temp, 0, p2Temp.length);
	State ns3 = new State(p1Temp,p2Temp);
	State ns4 = new State(p1Temp,p2Temp);
	parent = new Node(ns3, ns4, expandNode.getUtility());
	parent.setLabel(expandNode.getLabel());
	parent.setDepth(expandNode.getDepth());
	//parent.setCurrMoveName(prevParent.getCurrMoveName()!=null?prevParent.getCurrMoveName():expandNode.getCurrMoveName());
	parent.setCurrMoveName(expandNode.getCurrMoveName());
	expandNode.setParent(parent);
	//end of change
	
	for(int i=0;noOfStones!=null && i<noOfStones.length/2-1;i++){
	expand = false;
	gameEndP1 = true;
	gameEndP2 = true;
	globalGameEnd = false;
	currStones = noOfStones[i];
	forwardTrack = i+1;
	tempFwdTrack = forwardTrack;
	System.arraycopy(noOfStones, 0, noOfStonesClone, 0, noOfStones.length);
	gameEnd = false;
	if(noOfStones[i]>0){
		
		/*System.out.println();
		for(int k=0;k<noOfStones.length;k++){
		System.out.print(noOfStones[k]+" ");
		}
		System.out.println();*/
		
	while(currStones!=0){
	noOfStonesClone[forwardTrack]++;
	noOfStonesClone[i]--;
	currStones--;
	forwardTrack++;
	sumTrack = 0;
	expandNode.setPlayNewMove(true);
	if(forwardTrack==noOfStonesClone.length-1 && currStones!=0){
	forwardTrack = 0;
	}
	}
	currMoveName = "B"+(i+2);
/*	System.out.println();
	for(sumTrack=0;noOfStonesClone!=null && sumTrack<noOfStonesClone.length;sumTrack++){
	System.out.print(noOfStonesClone[sumTrack]+" ");
	}
	System.out.println();*/
	//capture test 
	if(forwardTrack<noOfStonesClone.length/2 && noOfStonesClone[forwardTrack-1]-1==0 && currStones==0){
	noOfStonesClone[noOfStonesClone.length/2-1]+=noOfStonesClone[((noOfStonesClone.length-2)/2)*2-forwardTrack+1]+1;
	noOfStonesClone[forwardTrack-1]--;
	noOfStonesClone[((noOfStonesClone.length-2)/2)*2-forwardTrack+1]=0;
	}	
	//capture test ends
	
	//game end test - p1 board
	for(sumTrack=0;noOfStonesClone!=null && sumTrack<noOfStonesClone.length/2-1;sumTrack++){
	if(noOfStonesClone[sumTrack]!=0){
	gameEndP1 = false;
	break;
	}
	}
	for(sumTrack=noOfStones.length-2;noOfStonesClone!=null && sumTrack>noOfStones.length/2-1;sumTrack--){
	if(noOfStonesClone[sumTrack]!=0){
	gameEndP2 = false;
	break;
	}
	}
	if(gameEndP1 || gameEndP2){
	for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
	oppPitSum+=noOfStonesClone[j];
	}
	for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
	noOfStonesClone[j]=0;
	}
	noOfStonesClone[noOfStonesClone.length-1]+=oppPitSum;
	
	for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
	ownPitSum+=noOfStonesClone[j];
	}
	for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
	noOfStonesClone[j]=0;
	}
	noOfStonesClone[noOfStonesClone.length/2-1]+=ownPitSum;
	if(currentPlayer==1) {
	currUtility = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
	if(currUtility>v.getUtility()){
		v = copyNode(noOfStonesClone,currUtility, prevMove, depth);
		parMax = v.getUtility();
	}
	parMax = v.getUtility();
	if(task==3){
	if(v.getUtility()>=beta){
		//print
		wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+currUtility+","+getValue(alpha)+","+getValue(beta));
		traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
		wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
		return v;
	}
	if(v.getUtility()>alpha){
		alpha = v.getUtility();
	}
	}
	} else {
		currUtility =noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
		if(currUtility<v.getUtility()){
			v = copyNode(noOfStonesClone,currUtility, prevMove, depth);
			parMax = v.getUtility();
		}
		parMax = v.getUtility();
		
		//task3 begins
		if(task==3){
			if(v.getUtility()<=alpha){
				//print
				/*traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));*/
				wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+currUtility+","+getValue(alpha)+","+getValue(beta));
				if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
					traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
				} else {
					traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
				}
				return v;
			}
			if(v.getUtility()<beta){
				beta = v.getUtility();
			}
			}
		//task3 ends
	}
	
	globalGameEnd = true;
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth)+","+currUtility);
	tempTraverseLog.append(System.getProperty("line.separator")+prevMove+","+(depth)+","+parMax);
	
	if(task==2) {
	wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+currUtility);
	if(!prevMove.trim().equals("") && !currMoveName.equals("") &&
			currMoveName.substring(0,1).equals(prevMove.substring(0,1))) {
		wr1.write(System.getProperty("line.separator")+prevMove+","+(depth)+","+parMax);
	}
	else {
		wr1.write(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(depth-1)+","+parMax);
	}
	} else {
		wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+currUtility+","+getValue(alpha)+","+getValue(beta));
		if(!prevMove.trim().equals("") && !currMoveName.equals("") &&
				currMoveName.substring(0,1).equals(prevMove.substring(0,1))) {
			wr2.write(System.getProperty("line.separator")+prevMove+","+(depth)+","+parMax+","+getValue(alpha)+","+getValue(beta));
		}
		else {
			wr2.write(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(depth-1)+","+parMax+","+getValue(alpha)+","+getValue(beta));
		}
	}
	Node tempMax = null;
	//maxNode.setUtility(parMax);
	//return v;
	continue;
	}
	//game end test over
	
	//make a move and print
	
	/*System.out.println("----------------------------CLONE------------------------------------");*/
	/*System.out.println();
	for(int k=0;k<noOfStonesClone.length;k++){
	System.out.print(noOfStonesClone[k]+" ");
	}
	System.out.println();*/
	if(expandNode.getLabel()!=null && expandNode.getLabel().equals(ROOT)){
	parent.setLabel(MAX);
	parent.setUtility(Integer.MAX_VALUE);
	expandNode.setCurrMoveName(ROOT);
	}
	
	if(forwardTrack==noOfStonesClone.length/2 && currStones==0){
	isNewMove = true;
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(false);
	//parent.setDepth(parent.getDepth()+1);/*?commented*/
	expandNode.setDepth(parent.getDepth()==0?parent.getDepth()+1:parent.getDepth());
	previousDepth = parent.getDepth(); 
	System.arraycopy(noOfStonesClone, 0, p1Temp, 0, p1Temp.length);
	System.arraycopy(noOfStonesClone, noOfStonesClone.length/2, p2Temp, 0, p2Temp.length);
	expandNode.getP1().setPits1(p1Temp);
	expandNode.getP2().setPits2(p2Temp);
	if(currentPlayer==1) {
		currUtility = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
		} else {
			currUtility =noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
		}
	expandNode.setUtility(Integer.MIN_VALUE);
	label = (parent.getLabel()!=null && (parent.getLabel().equals(MAX) || parent.getLabel().equals(MIN)))?parent.getLabel():MAX;
	expandNode.setLabel(label);
	expandNode.setCurrMoveName("B"+(i+2));
	
	expand = true;
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth)+","+(currentPlayer==1?"-Infinity":"Infinity"));
	traverseLogAB.append(System.getProperty("line.separator")+currMoveName+","+(depth)+","+(currentPlayer==1?"-Infinity":"Infinity")+","+alpha+","+beta);
	if(task==3)
		wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+(currentPlayer==1?"-Infinity":"Infinity")+","+getValue(alpha)+","+getValue(beta));
	if(task==2)
		wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+(currentPlayer==1?"-Infinity":"Infinity"));
	currMax = chooseNextMoveMinimax(expandNode, currMoveName, depth, currUtility, true, alpha, beta);
	if(currentPlayer==1) {
	if(currMax.getUtility()>v.getUtility()){
	v = currMax;
	}
	parMax = v.getUtility();
	//task3 begins
	if(task==3){
	if(v.getUtility()>=beta){
		//print
		if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
		} else {
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
		}
		return v;
	}
	if(v.getUtility()>alpha){
		alpha = v.getUtility();
	}
	}
	//task3 ends
	} else {
		if(currMax.getUtility()<v.getUtility()){
		v = currMax;
		}
		parMax = v.getUtility();
		
		//task3 begins
		if(task==3){
		if(v.getUtility()<=alpha){
			//print
			if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
				traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			} else {
				traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			}
			return v;
		}
		if(v.getUtility()<beta){
			beta = v.getUtility();
		}
		}
		//task3 ends
		
	}
	isNewMove = false;
	localMax = new ArrayList<Node>();
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(true);
	globalGameEnd = false;
	}
	else{
	//no extra move : call min
	label = (parent.getLabel()!=null && parent.getLabel().equals(MAX))?MIN:MAX; //no new move : if coming from a max node, new node is min; else max.
	expandNode.setLabel(label);
	
	if(parent.getLabel().equals(MAX) && parent.getDepth()==1){
	expandNode.setDepth(parent.getDepth());
	} else {
	expandNode.setDepth(parent.getDepth()+1);
	}
	
	depth++;
	
	if(expandNode.isPlayNewMove() || gameEnd){
		if(currentPlayer==1) {
			currUtility = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
			} else {
				currUtility =noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
			}
	expandNode.setUtility(expandNode.getLabel().equals(MAX)?Integer.MIN_VALUE:Integer.MAX_VALUE); //going down the tree, passing down utility values from parent; -inf for max, inf for min
	//appending current move played 
	if(noOfStones[i]!=0){	
	expandNode.setCurrMoveName("B"+(i+2));
	parent.setCurrMoveName("B"+(i+2));
	}
	
	State s1 = new State();
	State s2 = new State();
	int notMaxP1[]=new int[p1Stones.length];
	int notMaxP2[]=new int[p1Stones.length];
	System.arraycopy(noOfStonesClone, 0, notMaxP1, 0, notMaxP1.length);
	System.arraycopy(noOfStonesClone, notMaxP1.length, notMaxP2, 0, notMaxP2.length);
	s1.setPits1(notMaxP1);
	s2.setPits2(notMaxP2);
	s1=new State(notMaxP1, notMaxP2);
	s2=new State(notMaxP1, notMaxP2);
	expandNode.setP1(s1);
	expandNode.setP2(s2);
	
	
	if(depth<=cutoffDepth){
	expandNode.setUtility(Integer.MAX_VALUE);
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+(currentPlayer==1?"Infinity":"-Infinity"));
	traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+(currentPlayer==1?"Infinity":"-Infinity")+","+alpha+","+beta);
	if(task==3)
		wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+(currentPlayer==1?"Infinity":"-Infinity")+","+getValue(alpha)+","+getValue(beta));
	else
		wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+(currentPlayer==1?"Infinity":"-Infinity"));
	
	currMin = chooseNextMoveMin(expandNode, currMoveName, prevUtil, depth, false, alpha, beta);
	minNode.setValue(i);
	depth--;
	localMax.add(minNode);
	if(currentPlayer==1) {
	if(currMin.getUtility()>v.getUtility()){
		v = copyNode(noOfStonesClone,currMin.getUtility(), currMoveName, depth); 
	}
	parMin = v.getUtility();
	//task3 begins
	if(task==3){
	if(v.getUtility()>=beta){
		//print		
		if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
		} else {
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
		}
		return v;
	}
	if(v.getUtility()>alpha){
		alpha = v.getUtility();
	}
	//task3 ends
	}
	} else {
		if(currMin.getUtility()<v.getUtility()){
			v = copyNode(noOfStonesClone,currMin.getUtility(), currMoveName, depth); 
		}
		parMin = v.getUtility();
		
		//task3 begins
		if(task==3){
		if(v.getUtility()<=alpha){
			//print
			if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
				traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			} else {
				traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			}
			
			return v;
		}
		if(v.getUtility()<beta){
			beta = v.getUtility();
		}
		//task3 ends
		}	
	}
	nextState.add(noOfStonesClone);
	}
	else {			
		//no extra move + cutoff depth -- print child with utility and parent with updated utility
	expandNode.setDepth(parent.getDepth());
	expandNode.setLabel(parent.getLabel());
	if(currentPlayer==1) {
		childUtil = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
		if(childUtil>v.getUtility()) {
			v = copyNode(noOfStonesClone,childUtil, currMoveName, depth);
		}
		if(task==3){
		if(v.getUtility()>=beta){
			//print current
			if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
				traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
				} else {
					traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
				}
			//print ends
			
			//print prev move
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+(v.getUtility())+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			//print ends
			return v;
		}
		if(v.getUtility()>alpha){
			alpha = v.getUtility();
		}
		}
		} else {
			childUtil =noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
			if(childUtil<v.getUtility()){
				v = copyNode(noOfStonesClone,childUtil, currMoveName, depth);
			}
			
			//task3
			if(task==3){
				if(v.getUtility()<=alpha){
					//print
/*					traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+(v.getUtility())+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));*/
					
					//print current
					if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
						traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
						} else {
							traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+alpha+","+beta);
							wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
						}
					//print ends
					
					
					if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
						traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
					} else {
						traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-1:depth)+","+(v.getUtility())+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-1:depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
					}
					return v;
				}
				if(v.getUtility()<beta){
					beta = v.getUtility();
				}
				}
			//task3 ends
			if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
				tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+childUtil);
				traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+alpha+","+beta);
				if(task==2)
					wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+childUtil);
				else
					wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
				} else {
					tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+childUtil);
					traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+alpha+","+beta);
					if(task==2)
						wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+childUtil);
					else
						wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth-1)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
				}
		}
	parentUtil = expandNode.getUtility();
	
	depth--;
	//continue;
	}
	}
	}
	if(prevMove.trim().equals("")){
		tempTraverseLog.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility());
		traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+alpha+","+beta);
		if(task==2)
			wr1.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility());
		else
			wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
		
		}
		else{
			if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
			tempTraverseLog.append(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+v.getUtility());
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+v.getUtility()+","+alpha+","+beta);
			if(task==2)
				wr1.write(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+v.getUtility());
			else 
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
			
			} else {
			tempTraverseLog.append(System.getProperty("line.separator")+(prevMove)+","+(depth>=cutoffDepth?cutoffDepth:depth)+","+v.getUtility());
				traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>=cutoffDepth?cutoffDepth:depth)+","+v.getUtility()+","+alpha+","+beta);
				if(task==2)
					wr1.write(System.getProperty("line.separator")+(prevMove)+","+(depth>=cutoffDepth?cutoffDepth:depth)+","+v.getUtility());
				else 
					wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>=cutoffDepth?cutoffDepth:depth)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
				
			}
		}
	}
}
	 return v;
	}

private static Node copyNode(int[] noOfStones, int parMax, String prevMove,
		int depth) {
	// TODO Auto-generated method stub
	Node tempNode = new Node();
	State s1 = new State();
	State s2 = new State();
	int maxP1Stones[] = new int[noOfStones.length/2];
	int maxP2Stones[] = new int[noOfStones.length/2];
	System.arraycopy(noOfStones, 0, maxP1Stones, 0, maxP1Stones.length);
	System.arraycopy(noOfStones, maxP1Stones.length, maxP2Stones, 0, maxP2Stones.length);
	s1.setPits1(maxP1Stones);
	s2.setPits2(maxP2Stones);
	tempNode = new Node(s1,s2,parMax);
	tempNode.setDepth(depth);
	tempNode.getP1().setPits1(maxP1Stones);
	tempNode.getP2().setPits2(maxP2Stones);
	tempNode.setUtility(parMax);
	tempNode.setCurrMoveName(prevMove);
	return tempNode;
	} 

private static Node chooseNextMoveMin(Node expandNode, String prevMove, int prevUtil, int depth, boolean isNewMove, int alpha, int beta) throws IOException {
	// TODO Auto-generated method stub
	Node minNode = new Node();
	minNode.setUtility(Integer.MAX_VALUE);
	Node maxNode = new Node();
	Node v = new Node();
	if(currentPlayer==1){
	v = new Node(null,null,Integer.MAX_VALUE);
	} else {
	v = new Node(null,null,Integer.MIN_VALUE);
	}
	boolean isNewMoveCopy = isNewMove;
	int parMax = Integer.MIN_VALUE;
	int parMin = Integer.MAX_VALUE;
	isNewMove = false;
	globalGameEnd = false;
	int noOfStones[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesClone[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesMaxClone[] = new int[noOfStones.length];
	int p1Stones[] = expandNode.getP1().getPits1();
	int p2Stones[] = expandNode.getP2().getPits2();
	minP1Stones = expandNode.getP1().getPits1();
	minP2Stones = expandNode.getP2().getPits2();
	int p1Temp[] = new int[expandNode.getP1().getPits1().length];
	int p2Temp[] = new int[expandNode.getP2().getPits2().length];
	int maxI = 0;
	Node tempMin = new Node();
	int tempFwdTrack = 0;
	boolean gameEnd = false;
	boolean expand = false;
	int oppPitSum = 0;
	int sumTrack = 0;
	String printUtil = "";
	String printDepth = "";
	int childUtil = 0;
	int parentUtil = 0;
	int currMinUtil = Integer.MAX_VALUE;
	boolean gameEndP1 = true;
	boolean gameEndP2 = true;
	int ownPitSum = 0;
	Node parent = expandNode.getParent()!=null?expandNode.getParent():null; //was null
	Node prevParent = expandNode.getParent();
	String nodeUtil = "";
	int currUtility = 0;
	if(currentPlayer==1) {
	currUtility = p1Stones[p1Stones.length-1]-p2Stones[p2Stones.length-1];
	} else {
		currUtility = p2Stones[p2Stones.length-1] - p1Stones[p1Stones.length-1];
	}
	if(expandNode.getLabel().equals(ROOT) ){
		depth=1;
		}
	System.arraycopy(p1Stones, 0, noOfStones, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStones, p1Stones.length, p2Stones.length);
	System.arraycopy(p1Stones, 0, noOfStonesClone, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStonesClone, p1Stones.length, p2Stones.length);
	
	//changed code
	System.arraycopy(noOfStones, 0, p1Temp, 0, p1Temp.length);
	System.arraycopy(noOfStones, noOfStones.length/2, p2Temp, 0, p2Temp.length);
	State ns1 = new State(p1Temp,p2Temp);
	State ns2 = new State(p1Temp,p2Temp);
	parent = new Node(ns1, ns2, expandNode.getUtility());
	parent.setLabel(expandNode.getLabel());
	parent.setDepth(expandNode.getDepth());
	//parent.setCurrMoveName(prevParent.getCurrMoveName()!=null?prevParent.getCurrMoveName():expandNode.getCurrMoveName());
	parent.setCurrMoveName(expandNode.getCurrMoveName());
	expandNode.setParent(parent);
	//end of change
	Node currMin = new Node();
	Node currMax = new Node();
	if(currentPlayer==1){
		currMin = new Node(null,null,Integer.MAX_VALUE);
		currMax = new Node(null,null,Integer.MIN_VALUE);
	} else {
		currMin = new Node(null,null,Integer.MIN_VALUE);
		currMax = new Node(null,null,Integer.MAX_VALUE);
	}
	String currMoveName = "";
	int currStones = 0;
	int forwardTrack = 0;
	int tempCurrUtility = 0;
	String label = "";
	if(expandNode.getLabel().equals(ROOT) || expandNode.getLabel().equals(MAX)){
	expandNode.setUtility(Integer.MIN_VALUE);
	}
	else if(expandNode.getLabel().equals(MIN)) {
	expandNode.setUtility(Integer.MAX_VALUE);
	}
	
	for(int i=noOfStones.length-2;noOfStones!=null && i>noOfStones.length/2-1;i--){
	expand = false; 
	gameEndP1 = true;
	gameEndP2 = true;
	globalGameEnd = false;
	currStones = noOfStones[i];
	forwardTrack = i+1;
	tempFwdTrack = forwardTrack;
	System.arraycopy(noOfStones, 0, noOfStonesClone, 0, noOfStones.length);
	gameEnd = false;
	if(noOfStones[i]>0){
		/*System.out.println();
		for(int k=0;k<noOfStones.length;k++){
		System.out.print(noOfStones[k]+" ");
		}
		System.out.println();*/
		
	while(currStones!=0){
	noOfStonesClone[forwardTrack]++;
	noOfStonesClone[i]--;
	currStones--;
	forwardTrack++;
	sumTrack = 0;
	expandNode.setPlayNewMove(true);
	if(forwardTrack==noOfStonesClone.length/2-1 && currStones!=0){
	forwardTrack = noOfStonesClone.length/2;
	}
	if(forwardTrack>noOfStonesClone.length-1){
	forwardTrack = 0;
	}
	}
	currMoveName = "A"+(noOfStonesClone.length-i);
	//capture test
	if(forwardTrack>noOfStonesClone.length/2-1 && noOfStonesClone[forwardTrack-1]-1==0 && currStones==0){
	noOfStonesClone[noOfStonesClone.length-1]+=noOfStonesClone[((noOfStonesClone.length-2))-forwardTrack+1]+1;
	noOfStonesClone[forwardTrack-1]--;
	noOfStonesClone[((noOfStonesClone.length-2))-forwardTrack+1]=0;
	}
	//capture test ends
	
	//game end test - p1 board
	for(sumTrack=0;noOfStonesClone!=null && sumTrack<noOfStonesClone.length/2-1;sumTrack++){
	if(noOfStonesClone[sumTrack]!=0){
	gameEndP1 = false;
	break;
	}
	}
	for(sumTrack=noOfStones.length-2;noOfStonesClone!=null && sumTrack>noOfStones.length/2-1;sumTrack--){
	if(noOfStonesClone[sumTrack]!=0){
	gameEndP2 = false;
	break;
	}
	}
	
	if(gameEndP1 || gameEndP2){
	for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
	oppPitSum+=noOfStonesClone[j];
	}
	for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
	noOfStonesClone[j]=0;
	}
	noOfStonesClone[noOfStonesClone.length-1]+=oppPitSum;
	
	for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
	ownPitSum+=noOfStonesClone[j];
	}
	for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
	noOfStonesClone[j]=0;
	}
	globalGameEnd = true;
	noOfStonesClone[noOfStonesClone.length/2-1]+=ownPitSum;
	if(currentPlayer==1) {
	currUtility = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
	if(currUtility<v.getUtility()){
		v = copyNode(noOfStonesClone,currUtility, prevMove, depth);
		parMin = currUtility;
	}
	//task3 
	if(task==3){
	if(v.getUtility()<=alpha){
		//print
		wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
		if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth>cutoffDepth?depth-2:depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
		} else {
			traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
		}
		return v;
	}
	if(v.getUtility()<beta){
		beta = v.getUtility();
	}
	}
	//task3 ends
	} else {
		currUtility =  noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
		if(currUtility>v.getUtility()){
			v = copyNode(noOfStonesClone,currUtility, prevMove, depth);
			parMin = currUtility;
		}
		if(task==3){
		//task4 
		if(v.getUtility()>=beta){
			//print
			wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
			if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
				traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+(v.getUtility())+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth-1)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			} else {
				traverseLogAB.append(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(prevMove)+","+(depth)+","+(v.getUtility())+","+getValue(alpha)+","+getValue(beta));
			}
			//print done
			return v;
		}
		if(v.getUtility()>alpha){
			alpha = v.getUtility();
		}
		//task4 ends
	}
	}
	
	/*if at cutoff depth, print only eval value node; else print inf and eval value */
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth)+","+v.getUtility());
	traverseLogAB.append(System.getProperty("line.separator")+currMoveName+","+(depth)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
	if(task==2)
		wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+v.getUtility());
	else 
		wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
	
	if(!prevMove.trim().equals("") && !prevMove.substring(0,1).equals(currMoveName.substring(0,1))){
		tempTraverseLog.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+v.getUtility());
		traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
		if(task==2)
			wr1.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?depth-2:depth-1)+","+v.getUtility());
		else 
			wr2.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?depth-2:depth-1)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
	} else {
		tempTraverseLog.append(System.getProperty("line.separator")+prevMove+","+(depth)+","+v.getUtility());
		traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
		if(task==2)
			wr1.write(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(depth)+","+v.getUtility());
		else 
			wr2.write(System.getProperty("line.separator")+(prevMove.trim().equals("")?ROOT.toLowerCase():prevMove)+","+(depth)+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
	}
	//printTraverseLog(currMoveName, prevMove, depth, Integer.MAX_VALUE, noOfStones, noOfStonesClone, isNewMove, parMin, parMax, true);
	//minNode.setUtility(parMin);
	continue;
	//return v;
	}
	//game end test over
	
	/*System.out.println("----------------------------CLONE------------------------------------");*/
/*	System.out.println();
	for(int k=0;k<noOfStonesClone.length;k++){
	System.out.print(noOfStonesClone[k]+" ");
	}
	System.out.println();*/
	/*System.out.println("----------------------------CLONE------------------------------------");*/
	
	if(currentPlayer==1) {
		tempCurrUtility = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
		} else {
			tempCurrUtility =  noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
		}
	nodeUtil = String.valueOf(tempCurrUtility);
	
	if(forwardTrack==0 && currStones==0){
	//do extra move : call min
	//currentNode = expandNode;
	isNewMove = true;
	//parent.setDepth(parent.getDepth()+1);
	expandNode.setDepth(parent.getDepth()+1);
	expandNode.setLabel(MIN);
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(false);
	System.arraycopy(noOfStonesClone, 0, p1Temp, 0, p1Temp.length);
	System.arraycopy(noOfStonesClone, noOfStonesClone.length/2, p2Temp, 0, p2Temp.length);
	expandNode.getP1().setPits1(p1Temp);
	expandNode.getP2().setPits2(p2Temp);
	currUtility = noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1];
	expandNode.setUtility(Integer.MAX_VALUE);
	expand = true;
	expandNode.setLabel(MIN);
	expandNode.setCurrMoveName("A"+(noOfStonesClone.length-i));
	expandNode.setParent(parent);
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth)+","+(currentPlayer==1?"Infinity":"-Infinity"));
	traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+depth+","+(currentPlayer==1?"Infinity":"-Infinity")+","+alpha+","+beta);
	if(task==2)
		wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth)+","+(currentPlayer==1?"Infinity":"-Infinity"));
	else 
		wr2.write(System.getProperty("line.separator")+(currMoveName)+","+depth+","+(currentPlayer==1?"Infinity":"-Infinity")+","+getValue(alpha)+","+getValue(beta));
	
	
	currMin = chooseNextMoveMin(expandNode, currMoveName, minNode.getUtility(), depth, true, alpha, beta);
	isNewMove = false;
	if(currentPlayer==1){
	if(currMin.getUtility()<v.getUtility()){
	v = currMin;
	} 	
	parMin = v.getUtility();
	if(task==3){
	//task4
	if(v.getUtility()<=alpha){
		//print
		if(prevMove.trim().equals("")){
			traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
			} 
			else{
			if(!prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
			traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+getValue(alpha)+","+getValue(beta));
			} else {
			traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+alpha+","+beta);
			wr2.write(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
			}
		}
		//print ends
		
		return v;
	}
	if(v.getUtility()<beta){
		beta = v.getUtility();
	}
	//task4 ends
	}
	} else {
		if(currMin.getUtility()>v.getUtility()){
			v = currMin;
			} 	
			parMin = v.getUtility();
			if(task==3){
			//task4
			if(v.getUtility()>=beta){
				//print
				if(prevMove.trim().equals("")){
					traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
					} 
					else{
					if(!prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
					traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+getValue(alpha)+","+getValue(beta));
					} else {
					traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
					}
				}
				//print ends
				return v;
			}
			if(v.getUtility()>alpha){
				alpha = v.getUtility();
			}
			//task4 ends
			}
	}
	
	parent.setUtility(minNode.getUtility());
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(true);
	}
	else{
	expandNode.setDepth(parent.getDepth()+1);
	depth++;
	expandNode.setLabel(MAX);
	
	if(expandNode.isPlayNewMove() || gameEnd){
		if(currentPlayer==1) {
			currUtility = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
			} else {
			currUtility =  noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
			}
	if(noOfStones[i]!=0){
	if(parent.getCurrMoveName()!=null){
	if(currUtility<parent.getUtility()){
	parent.setUtility(currUtility);
	}
	else if(currUtility>=parent.getUtility()){
	if(prevParent.getUtility()!=Integer.MIN_VALUE){
	//expandNode.setUtility(currUtility);
	int currUtil = 0;
	if(parent.getUtility()<expandNode.getUtility()){
	currUtil = parent.getUtility(); 
	} else {
	currUtil = expandNode.getUtility();
	}
	}
	else{
	//traverseLog.append(System.getProperty("line.separator")+expandNode.getCurrMoveName()+","+String.valueOf(parent.getDepth())+"-Infinity");
	if(expandNode.getDepth()<cutoffDepth)
	expandNode.setUtility(Integer.MIN_VALUE);
	}
	} 
	}
	}
	expandNode.setCurrMoveName("A"+(noOfStonesClone.length-i));
	State s1 = new State();
	State s2 = new State();
	int notMaxP1[]=new int[p1Stones.length];
	int notMaxP2[]=new int[p1Stones.length];
	System.arraycopy(noOfStonesClone, 0, notMaxP1, 0, notMaxP1.length);
	System.arraycopy(noOfStonesClone, notMaxP1.length, notMaxP2, 0, notMaxP2.length);
	s1.setPits1(notMaxP1);
	s2.setPits2(notMaxP2);
	s1=new State(notMaxP1, notMaxP2);
	s2=new State(notMaxP1, notMaxP2);
	expandNode.setP1(s1);
	expandNode.setP2(s2);

	if(depth<=cutoffDepth && noOfStones[i]!=0){
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+(currentPlayer==1?"-Infinity":"Infinity"));
	if(task==2)
		wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+(currentPlayer==1?"-Infinity":"Infinity"));
	else 
		wr2.write(System.getProperty("line.separator")+currMoveName+","+(depth-1)+","+(currentPlayer==1?"-Infinity":"Infinity")+","+getValue(alpha)+","+getValue(beta));
	currMax = chooseNextMoveMinimax(expandNode, currMoveName, depth, expandNode.getUtility(), false, alpha, beta);
	//System.out.println("Hello from Recursion End");
	depth--;
	if(currentPlayer==1){
		if(currMax.getUtility()<v.getUtility()){
		v=copyNode(noOfStones,currMax.getUtility(), prevMove, depth);
		}
		parMin = v.getUtility();
		if(task==3){
		//task4
		if(v.getUtility()<=alpha){	
			//print
			if(prevMove.trim().equals("")){
				traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
				} 
				else{
				if(!prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
				traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+getValue(alpha)+","+getValue(beta));
				} else {
				traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+alpha+","+beta);
				wr2.write(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
				}
			}
			//print ends
			
			return v;
		}
		if(v.getUtility()<beta){
			beta = v.getUtility();
		}
		//task4 ends
		}
		
		} else {
			if(currMax.getUtility()>v.getUtility()){
				v=copyNode(noOfStonesClone,currMax.getUtility(), prevMove, depth);
				}
				parMin = v.getUtility();	
				if(task==3){
				//task4
				if(v.getUtility()>=beta){	
					//print
					if(prevMove.trim().equals("")){
						traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
						} 
						else{
						if(!prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
						traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+getValue(alpha)+","+getValue(beta));
						} else {
						traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
						}
					}
					//print ends
					return v;
				}
				if(v.getUtility()>alpha){
					alpha = v.getUtility();
				}
				//task4 ends
				}
		}
	}
	else {
		if(currentPlayer==1) {
			childUtil = noOfStonesClone[noOfStonesClone.length/2-1] -  noOfStonesClone[noOfStonesClone.length-1];
			} else {
			childUtil =  noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
			}
	parentUtil = expandNode.getUtility();
	
	if(currentPlayer==1){
	if(childUtil<v.getUtility()){
	v = copyNode(noOfStonesClone,childUtil, currMoveName, depth);
	}
	parMin = v.getUtility();
	
	if(task==3){
	//task4
		if(v.getUtility()<=alpha){
			//print prev move
			tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil);
			traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil+","+alpha+","+beta);
			if(task==2)
				wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil);
			else 
				wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
			//print ends
			
			//print
			if(prevMove.trim().equals("")){
				traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
				wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
				} 
				else{
				if(!prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
				traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?cutoffDepth:depth)+","+parMin+","+getValue(alpha)+","+getValue(beta));
				wr2.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?cutoffDepth:depth)+","+parMin+","+getValue(alpha)+","+getValue(beta));
				} else {
				traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?depth-2:depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
				wr2.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?depth-2:depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
				}
			}
			//print ends
			return v;
		}
		if(v.getUtility()<beta){
			beta = v.getUtility();
		}
		//task4 ends
	}
	} else {
		if(childUtil>v.getUtility()){
			v = copyNode(noOfStonesClone,childUtil, currMoveName, depth);
			}
			parMin = v.getUtility();
			if(task==3){
			//task4
			if(v.getUtility()>=beta){
				
				//print prev move
				tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil);
				traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil+","+alpha+","+beta);
				if(task==2)
					wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil);
				else 
					wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
				//print ends
				
				//print
				if(prevMove.trim().equals("")){
					traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+alpha+","+beta);
					wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
					} 
					else{
					if(!prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
					traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?cutoffDepth:depth)+","+parMin+","+getValue(alpha)+","+getValue(beta));
					} else {
					traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?depth-2:depth-1)+","+parMin+","+alpha+","+beta);
						wr2.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?depth-2:depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
					}
				}
				//print ends
				return v;
			}
			if(v.getUtility()>alpha){
				alpha = v.getUtility();
			}
			//task4 ends
			}
	}
	tempTraverseLog.append(System.getProperty("line.separator")+currMoveName+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil);
	traverseLogAB.append(System.getProperty("line.separator")+(currMoveName)+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil+","+alpha+","+beta);
	if(task==2)
		wr1.write(System.getProperty("line.separator")+currMoveName+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil);
	else 
		wr2.write(System.getProperty("line.separator")+(currMoveName)+","+(depth>cutoffDepth?cutoffDepth:depth)+","+childUtil+","+getValue(alpha)+","+getValue(beta));
	expandNode.setDepth(parent.getDepth());
	expandNode.setLabel(parent.getLabel());
	if(noOfStones[i]!=0){
	if(childUtil<parentUtil){
	expandNode.setUtility(childUtil);
	minNode.setUtility(childUtil);
	}
	}
	depth--;
	//continue;
	}
	}
	}
	if(prevMove.trim().equals("")){
		tempTraverseLog.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility());
		traverseLogAB.append(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+alpha+","+beta);
		if(task==2)
			wr1.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility());
		else 
			wr2.write(System.getProperty("line.separator")+(ROOT.toLowerCase())+","+0+","+v.getUtility()+","+getValue(alpha)+","+getValue(beta));
		} 
		else{
		if(!prevMove.trim().equals("") && currMoveName.substring(0,1).equals(prevMove.substring(0,1))){
		tempTraverseLog.append(System.getProperty("line.separator")+prevMove+","+(depth)+","+parMin);
		traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+depth+","+parMin+","+alpha+","+beta);
		if(task==2)
			wr1.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?cutoffDepth:depth)+","+parMin);
		else
			wr2.write(System.getProperty("line.separator")+prevMove+","+(depth>cutoffDepth?cutoffDepth:depth)+","+parMin+","+getValue(alpha)+","+getValue(beta));
		} else {
		tempTraverseLog.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin);
		traverseLogAB.append(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+alpha+","+beta);
		if(task==2)
			wr1.write(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin);
		else
			wr2.write(System.getProperty("line.separator")+prevMove+","+(depth-1)+","+parMin+","+getValue(alpha)+","+getValue(beta));
		}
	}
	}}
	minNode.setUtility(parMin);
	
	if(!isNewMoveCopy){
		tempMin = copyNode(noOfStones,parMin, prevMove, depth);
	}
	else {
		tempMin = copyNode(noOfStonesClone,parMin, prevMove, depth);
	}
	return v;
}

private static void chooseNextMove(Node expandNode) {
	// TODO Auto-generated method stub
	
	int noOfStones[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesClone[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesMaxClone[] = new int[noOfStones.length];
	int p1Stones[] = expandNode.getP1().getPits1();
	int p2Stones[] = expandNode.getP2().getPits2();
	maxP1Stones = expandNode.getP1().getPits1();
	maxP2Stones = expandNode.getP2().getPits2();
	int p1Temp[] = new int[expandNode.getP1().getPits1().length];
	int p2Temp[] = new int[expandNode.getP2().getPits2().length];
	int maxI = 0;
	int tempFwdTrack = 0;
	boolean gameEnd = false;
	boolean expand = false;
	int oppPitSum = 0;
	int sumTrack = 0;
	Node currentNode = new Node();
	boolean gameEndP1 = true;
	boolean gameEndP2 = true;
	int currUtility = p1Stones[p1Stones.length-1]-p2Stones[p2Stones.length-1];
	System.arraycopy(p1Stones, 0, noOfStones, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStones, p1Stones.length, p2Stones.length);
	System.arraycopy(p1Stones, 0, noOfStonesClone, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStonesClone, p1Stones.length, p2Stones.length);
	currentNode = expandNode;
	int currStones = 0;
	int forwardTrack = 0;
	int ownPitSum = 0;
	for(int i=0;noOfStones!=null && i<noOfStones.length/2-1;i++){
	expand = false;
	currStones = noOfStones[i];
	forwardTrack = i+1;
	tempFwdTrack = forwardTrack;
	System.arraycopy(noOfStones, 0, noOfStonesClone, 0, noOfStones.length);
	gameEnd = false;
	if(noOfStones[i]!=0) {
	while(currStones!=0){
	noOfStonesClone[forwardTrack]++;
	noOfStonesClone[i]--;
	currStones--;
	forwardTrack++;
	sumTrack = 0;
	expandNode.setPlayNewMove(true);
	if(forwardTrack==noOfStonesClone.length-1 && currStones!=0){
	forwardTrack = 0;
	}
	}
	
	//capture test 
		if(forwardTrack<noOfStonesClone.length/2 && noOfStonesClone[forwardTrack-1]-1==0 && currStones==0){
		noOfStonesClone[noOfStonesClone.length/2-1]+=noOfStonesClone[((noOfStonesClone.length-2)/2)*2-forwardTrack+1]+1;
		noOfStonesClone[forwardTrack-1]--;
		noOfStonesClone[((noOfStonesClone.length-2)/2)*2-forwardTrack+1]=0;
	}	
	//capture test ends
	
	//game end test	
		for(sumTrack=0;noOfStonesClone!=null && sumTrack<noOfStonesClone.length/2-1;sumTrack++){
			if(noOfStonesClone[sumTrack]!=0){
			gameEndP1 = false;
			break;
			}
			}
		
			if(gameEndP1){
				for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
					oppPitSum+=noOfStonesClone[j];
					}
					for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
					noOfStonesClone[j]=0;
					}
					noOfStonesClone[noOfStonesClone.length-1]+=oppPitSum;
					
					for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
					ownPitSum+=noOfStonesClone[j];
					}
					for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
					noOfStonesClone[j]=0;
					}
					noOfStonesClone[noOfStonesClone.length/2-1]+=ownPitSum;
					currUtility = noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1];
					if(currUtility>maxNode.getUtility()) {
						maxNode = copyNode(noOfStonesClone,currUtility, "", 1);
					}
			}
	//	
	
	if(forwardTrack==noOfStonesClone.length/2 && currStones==0){
	//currentNode = expandNode;
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(false);
	System.arraycopy(noOfStonesClone, 0, p1Temp, 0, p1Temp.length);
	System.arraycopy(noOfStonesClone, noOfStonesClone.length/2, p2Temp, 0, p2Temp.length);
	expandNode.getP1().setPits1(p1Temp);
	expandNode.getP2().setPits2(p2Temp);
	System.out.println();
	currUtility = noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1];
	expandNode.setUtility(currUtility);
	expand = true;
	chooseNextMove(expandNode);
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(true);
	}
	else{	
	currUtility = noOfStonesClone[noOfStonesClone.length/2-1]-noOfStonesClone[noOfStonesClone.length-1];
	expandNode.setUtility(currUtility);
	if((currentPlayer==1 && expandNode.getUtility()>maxUtility) || (currentPlayer==2 && expandNode.getUtility()>=maxUtility)){
	State s1 = new State();
	State s2 = new State();
	
	System.arraycopy(noOfStonesClone, 0, noOfStonesMaxClone, 0, noOfStonesClone.length);
	maxI = i;
	maxUtility = currUtility; 
	System.arraycopy(maxP1Stones, 0, noOfStonesClone, 0, p1Stones.length);
	System.arraycopy(maxP2Stones, 0, noOfStonesClone, p1Stones.length, p2Stones.length);
	System.arraycopy(noOfStonesMaxClone, 0, maxP1Stones, 0, maxP1Stones.length);
	System.arraycopy(noOfStonesMaxClone, maxP1Stones.length, maxP2Stones, 0, maxP2Stones.length);
	s1.setPits1(expandNode.getP1().getPits1());
	s2.setPits2(expandNode.getP2().getPits2());
	maxNode = new Node(s1,s2,maxUtility);
	maxNode.getP1().setPits1(expandNode.getP1().getPits1());
	maxNode.getP2().setPits2(expandNode.getP2().getPits2());
	maxNode.setUtility(maxUtility);
	}
	if(gameEnd){
	return;
	}
	}}
	}
}

private static void chooseMoveP2(Node expandNode) {

	// TODO Auto-generated method stub
	
	int noOfStones[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesClone[] = new int[expandNode.getP1().getPits1().length+expandNode.getP2().getPits2().length];
	int noOfStonesMaxClone[] = new int[noOfStones.length];
	int p1Stones[] = expandNode.getP1().getPits1();
	int p2Stones[] = expandNode.getP2().getPits2();
	maxP1Stones = expandNode.getP1().getPits1();
	maxP2Stones = expandNode.getP2().getPits2();
	int p1Temp[] = new int[expandNode.getP1().getPits1().length];
	int p2Temp[] = new int[expandNode.getP2().getPits2().length];
	int maxI = 0;
	int tempFwdTrack = 0;
	boolean gameEnd = false;
	boolean expand = false;
	int oppPitSum = 0;
	int sumTrack = 0;
	Node currentNode = new Node();
	boolean gameEndP1 = true;
	boolean gameEndP2 = true;
	int currUtility = p1Stones[p1Stones.length-1]-p2Stones[p2Stones.length-1];
	System.arraycopy(p1Stones, 0, noOfStones, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStones, p1Stones.length, p2Stones.length);
	System.arraycopy(p1Stones, 0, noOfStonesClone, 0, p1Stones.length);
	System.arraycopy(p2Stones, 0, noOfStonesClone, p1Stones.length, p2Stones.length);
	currentNode = expandNode;
	int currStones = 0;
	int forwardTrack = 0;
	int ownPitSum = 0;
	for(int i=noOfStones.length-2;noOfStones!=null && i>noOfStones.length/2-1;i--){
		expand = false; 
		globalGameEnd = false;
		currStones = noOfStones[i];
		forwardTrack = i+1;
		tempFwdTrack = forwardTrack;
		System.arraycopy(noOfStones, 0, noOfStonesClone, 0, noOfStones.length);
			
		while(currStones!=0){
		noOfStonesClone[forwardTrack]++;
		noOfStonesClone[i]--;
		currStones--;
		forwardTrack++;
		sumTrack = 0;
		expandNode.setPlayNewMove(true);
		if(forwardTrack==noOfStonesClone.length/2-1 && currStones!=0){
		forwardTrack = noOfStonesClone.length/2;
		}
		if(forwardTrack>noOfStonesClone.length-1){
		forwardTrack = 0;
		}
		}
		//capture test
		if(forwardTrack>noOfStonesClone.length/2-1 && noOfStonesClone[forwardTrack-1]-1==0 && currStones==0){
		noOfStonesClone[noOfStonesClone.length-1]+=noOfStonesClone[((noOfStonesClone.length-2))-forwardTrack+1]+1;
		noOfStonesClone[forwardTrack-1]--;
		noOfStonesClone[((noOfStonesClone.length-2))-forwardTrack+1]=0;
		}
		//capture test ends
		
		//p2 board game end
		for(sumTrack=noOfStones.length-2;noOfStonesClone!=null && sumTrack>noOfStones.length/2-1;sumTrack--){
		if(noOfStonesClone[sumTrack]!=0){
		gameEndP2 = false;
		break;
		}
		}
		
		if(gameEndP2){
		for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
		oppPitSum+=noOfStonesClone[j];
		}
		for(int j=noOfStonesClone.length/2;noOfStonesClone!=null && j<noOfStonesClone.length-1;j++){
		noOfStonesClone[j]=0;
		}
		noOfStonesClone[noOfStonesClone.length-1]+=oppPitSum;
		
		for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
		ownPitSum+=noOfStonesClone[j];
		}
		for(int j=0;noOfStonesClone!=null && j<noOfStonesClone.length/2-1;j++){
		noOfStonesClone[j]=0;
		}
		currUtility = noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
		if(currUtility>maxNode.getUtility()) {
			maxNode = copyNode(noOfStonesClone,currUtility, "", 1);
		}
		}
	if(forwardTrack==0 && currStones==0){
	//currentNode = expandNode;
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(false);
	System.arraycopy(noOfStonesClone, 0, p1Temp, 0, p1Temp.length);
	System.arraycopy(noOfStonesClone, noOfStonesClone.length/2, p2Temp, 0, p2Temp.length);
	expandNode.getP1().setPits1(p1Temp);
	expandNode.getP2().setPits2(p2Temp);
	System.out.println();
	currUtility = noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
	expandNode.setUtility(currUtility);
	expand = true;
	chooseMoveP2(expandNode);
	expandNode.setPlayNewMove(false);
	expandNode.setTrackback(true);
	}
	else{	
	currUtility = noOfStonesClone[noOfStonesClone.length-1] - noOfStonesClone[noOfStonesClone.length/2-1];
	expandNode.setUtility(currUtility);
	if(currUtility>maxNode.getUtility()){
	State s1 = new State();
	State s2 = new State();
	
	System.arraycopy(noOfStonesClone, 0, noOfStonesMaxClone, 0, noOfStonesClone.length);
	maxI = i;
	maxUtility = currUtility; 
	System.arraycopy(maxP1Stones, 0, noOfStonesClone, 0, p1Stones.length);
	System.arraycopy(maxP2Stones, 0, noOfStonesClone, p1Stones.length, p2Stones.length);
	System.arraycopy(noOfStonesMaxClone, 0, maxP1Stones, 0, maxP1Stones.length);
	System.arraycopy(noOfStonesMaxClone, maxP1Stones.length, maxP2Stones, 0, maxP2Stones.length);
	s1.setPits1(expandNode.getP1().getPits1());
	s2.setPits2(expandNode.getP2().getPits2());
	maxNode = new Node(s1,s2,maxUtility);
	maxNode.getP1().setPits1(expandNode.getP1().getPits1());
	maxNode.getP2().setPits2(expandNode.getP2().getPits2());
	maxNode.setUtility(maxUtility);
	}
	}}
	}


private static void initialize(BufferedReader br) throws NumberFormatException, IOException {
	// TODO Auto-generated method stub
	int mancala1 = 0;
	int mancala2 = 0;
	root.setP1(rootState);
	root.setP2(rootState);
	maxNode.setP1(rootState);
	maxNode.setP2(rootState);
	root.setDepth(0);
	root.setParent(null);
	root.setLabel(ROOT);
	currentPlayer = Integer.parseInt(br.readLine());
	cutoffDepth = Integer.parseInt(br.readLine());
	/*if(currentPlayer==1 || (currentPlayer==2 && (task==2 || task==3))){*/
	pitsP2 = br.readLine().split(" ");
	pitsP1 = br.readLine().split(" ");
	mancala2 = Integer.parseInt(br.readLine());
	mancala1 = Integer.parseInt(br.readLine());
	/*}
	else if(currentPlayer==2 && task==1){
	pitsP1 = br.readLine().split(" ");
	pitsP2 = br.readLine().split(" ");
	
	List<String> reverseP1 = Arrays.asList(pitsP1);
	Collections.reverse(reverseP1);
	pitsP1 = (String[]) reverseP1.toArray();
	
	List<String> reverseP2 = Arrays.asList(pitsP2);
	Collections.reverse(reverseP2);
	pitsP2 = (String[]) reverseP2.toArray();
	
	mancala1 = Integer.parseInt(br.readLine());
	mancala2 = Integer.parseInt(br.readLine());
	/*}*/
	pits2 = new int[pitsP2.length+1];
	pits1 = new int[pitsP1.length+1];
	root.getP1().setPits1(new int[pitsP1.length+1]);
	root.getP2().setPits2(new int[pitsP2.length+1]);
	for(int i=0;i<pitsP1.length;i++){
	root.getP1().pits1[i] = Integer.parseInt(pitsP1[i]);
	root.getP2().pits2[i] = Integer.parseInt(pitsP2[pitsP2.length-i-1]);
	}
	rootStateClone = new String[pitsP1.length+pitsP2.length+2];
	System.arraycopy(pitsP1, 0, rootStateClone, 0, pitsP1.length);
	rootStateClone[pitsP1.length] =  String.valueOf(mancala1);
	System.arraycopy(pitsP2, 0, rootStateClone,  pitsP1.length+1, pitsP2.length);
	rootStateClone[rootStateClone.length-1] =  String.valueOf(mancala2);
	
	root.getP1().pits1[pitsP1.length] = mancala1;
	root.getP2().pits2[pitsP2.length] = mancala2;
	if(currentPlayer==1){
		root.setUtility(mancala1-mancala2);
	} else {
		root.setUtility(mancala2-mancala1);
	}
	root.setPlayNewMove(false);
	root.setTrackback(false);
	nextStatePrint = new int[rootStateClone.length];
}

public static String getValue(int val){
	if(val==Integer.MAX_VALUE) { 
		return "Infinity";
		}
	if(val==Integer.MIN_VALUE) {
		return "-Infinity";
	}
	else {
		return String.valueOf(val);
	}
}

}