import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class inference {
	static BufferedWriter wr = null;
	static Map<String, ArrayList<String>> kbSentences = new HashMap();
	static Map<String, ArrayList<String>> standardizedKb = new HashMap();
	static ArrayList<String> facts = new ArrayList<>();
	static ArrayList<String> queries = new ArrayList<String>();
	static ArrayList<String> staticParsed = new ArrayList<String>();
	static ArrayList<String> kbArr = new ArrayList<String>();
	static int noOfQueries = 0;
	static int noOfSentencesInKb = 0;
	static String[] kbImplicationSplit = null;
	static char openingPara = '(';
	static char closingPara = ')';
	static String currQuerySol = "";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		File inFile = null;
		String currQuery = "";
		ArrayList<Map<String, String>> allSubstitutions = new ArrayList<>();
		ArrayList<String> stdKb = new ArrayList<>();
		try {
		wr = new BufferedWriter(new FileWriter("output.txt"));
		 if (0 < args.length) {
		 inFile = new File(args[0]);
		  }
		        br = new BufferedReader(new FileReader(inFile));
		        noOfQueries = Integer.parseInt(br.readLine());
		        initialize(br);
		        stdKb = standardizeVars();
		        standardizedKb = initializeStd(stdKb);
		        for(int i=0;i<noOfQueries;i++){
		        	allSubstitutions = new ArrayList<>();
					currQuery = queries.get(i);
					currQuerySol = queries.get(i);
					if(isFact(currQuery)) {
						wr.write("TRUE");
						wr.newLine();
						continue;
					}
					allSubstitutions = performBackwardChaining(currQuery,allSubstitutions, new ArrayList<String>());
					if(allSubstitutions.size()==1 && allSubstitutions.get(0).containsKey("!")){
						wr.write("FALSE");
						wr.newLine();
						continue;
					}
					if(allSubstitutions.size()>0){
						wr.write("TRUE");
						wr.newLine();
					} else {
						wr.write("FALSE");
						wr.newLine();
					}
		        }
		}
		    catch (Exception e) {
		    	e.printStackTrace();
		    } 

		    finally {
		        try {
		            if (br != null)br.close();
		            if (wr != null)wr.close();
		            } catch (IOException ex) {
		            ex.printStackTrace();
		        }
		    }	

	}

	
	 private static Map<String, ArrayList<String>> initializeStd(ArrayList<String> stdKb) {
		// TODO Auto-generated method stub
		 ArrayList<String> consequent = new ArrayList<>();
		 Map<String, ArrayList<String>> finalArr = new HashMap<>();
		 String[] stdKbImplicationSplit = null;
		 for(int i=0;i<stdKb.size();i++){
	        	String tempStr = stdKb.get(i);
	        	if(tempStr.contains("=>")){
	        	stdKbImplicationSplit = tempStr.split("=>");
	        	consequent = finalArr.get(stdKbImplicationSplit[1]);
	        	if(consequent==null) {
	        		consequent = new ArrayList<>();
	        	}
	        	consequent.add(stdKbImplicationSplit[0].trim());
	        	finalArr.put(stdKbImplicationSplit[1].trim(), consequent); 
	        } else {
     		consequent = new ArrayList<>();
     		consequent.add("True");
     		finalArr.put(tempStr.trim(), consequent);
     	}
	 	}
		return finalArr;
	}


	private static ArrayList<String> standardizeVars() {
		// TODO Auto-generated method stub
		 ArrayList<String> KBliststd = new ArrayList<String>();
			HashMap<String,String> usedvariable = new HashMap<String,String>();
			ArrayList<String> newLHS = new ArrayList<String>();
			ArrayList<String> newRHS = new ArrayList<String>();
			
			boolean fact = false;
			int count=1;
			String stdvar = "x";
			for(int i=0;i<kbArr.size();i++) {
				
				StringBuffer newtemp = new StringBuffer();
				
				String tempstring = kbArr.get(i);
				if(!tempstring.contains("=>")) {
					System.out.println(tempstring);
					KBliststd.add(tempstring);
					continue;
				}
				String temp1[] = tempstring.split("=>");
				String RHS = temp1[1].trim();
				String LHS = temp1[0].trim();
				
				String LHSsplit[] = LHS.split("\\^");
				
				for(int j=0;j<LHSsplit.length;j++) {				
					newtemp.append(getName(LHSsplit[j])).append("(");
					ArrayList<String> variableLHS = getVariables(LHSsplit[j]);				
					for(int k=0;k<variableLHS.size();k++){
						if(!isVariable(variableLHS.get(k))){
							if(k!=variableLHS.size()-1)
								newtemp.append(variableLHS.get(k)).append(",");
							else
								newtemp.append(variableLHS.get(k));
							if(k == variableLHS.size()-1)
								newtemp.append(")");
							continue;
						}
						if(!usedvariable.containsKey(variableLHS.get(k))){
							usedvariable.put(variableLHS.get(k),stdvar+count);
							count++;
						}
						if(k!=variableLHS.size()-1)
							newtemp.append(usedvariable.get(variableLHS.get(k))).append(",");
						else
							newtemp.append(usedvariable.get(variableLHS.get(k))).append(")");	
							
					}			
					if(j != (LHSsplit.length-1))
						newtemp.append(" ^ ");
				}
				newtemp.append(" => ");
				newtemp.append(getName(RHS)).append("(");
				ArrayList<String> variableRHS = getVariables(RHS);
				for(int k=0;k<variableRHS.size();k++){
					if(!isVariable(variableRHS.get(k))){
						if(k!=variableRHS.size()-1)
							newtemp.append(variableRHS.get(k)).append(",");
						else
							newtemp.append(variableRHS.get(k));
						if(k == variableRHS.size()-1)
							newtemp.append(")");
						continue;
					}
					if(!usedvariable.containsKey(variableRHS.get(k))){
						usedvariable.put(variableRHS.get(k),stdvar+count);
						count++;
					}
					if(k != variableRHS.size()-1)
						newtemp.append(usedvariable.get(variableRHS.get(k))).append(",");
					else
						newtemp.append(usedvariable.get(variableRHS.get(k))).append(")");
				}
				usedvariable.clear();
				KBliststd.add(newtemp.toString());
				System.out.println(newtemp);
			}
			return KBliststd;
		}


	private static ArrayList<String> getVariables(String rHS) {
		// TODO Auto-generated method stub
		ArrayList<String> returnSplitVars = new ArrayList<>();
		String[] split = rHS.substring(rHS.indexOf('(')+1,rHS.indexOf(')')).split(",");
		for(int i=0;i<split.length;i++){
			returnSplitVars.add(split[i]);
		}
		return returnSplitVars;
	}


	private static Object getName(String first) {
		// TODO Auto-generated method stub
		return new String(first.substring(0,first.indexOf('(')));
	}


	@SuppressWarnings("unchecked")
	private static ArrayList<Map<String, String>> performBackwardChaining(String thisQuery, ArrayList<Map<String, String>> allSubstitutions, ArrayList<String> parsedList) {
		// TODO Auto-generated method stub
		String currQuery = thisQuery.trim();
		String regexPattern = "";
		ArrayList<Map<String, String>> thetaDash = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDashAnd = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDashCopy = new ArrayList<>();
		ArrayList<Map<String, String>> returnTheta = new ArrayList<>();
		ArrayList<Map<String, String>> allSubstitutions1 = new ArrayList<>(); 
		boolean isThisQuery = false;
		Map<String, ArrayList<String>> toProve = new HashMap<>();
		ArrayList<String> lhsRule = new ArrayList<>();
		boolean keyExists = false; 
		Map.Entry<String, ArrayList<String>> entry = null;
		Map<String, String> substList = new HashMap<>();
		ArrayList<String> parsed = new ArrayList<>();
		parsed.addAll(parsedList);
		int countArgs = countNumberOfArguments(currQuery);
		int countVars = countNumberOfVariables(currQuery);
		boolean isFactInKb = isFact(thisQuery);
		staticParsed.add(thisQuery);
		if(isFactInKb){
			return allSubstitutions;
		}
		if(parsed.contains(thisQuery.toString())) {
			allSubstitutions.clear();
			return allSubstitutions;
		}
		parsed.add(thisQuery);
		if(!currQuery.contains("~")){
			regexPattern = "("+"("+"^(?!\\~)"+")"+"["+currQuery.substring(0,currQuery.indexOf('('))+"]"+")"+"\\("+"(\\w+)";
		} else {
			regexPattern = "(\\"+currQuery.substring(0,1)+")"+"["+currQuery.substring(1,currQuery.indexOf('('))+"]"+"\\("+"(\\w+)";
		}
		for(int j=1;j<countArgs;j++){
			regexPattern+="(,)"+"(\\w+)";
		}
		regexPattern+="(\\))";
		toProve = fetchRulesForGoal(regexPattern, standardizedKb, currQuery);
		Iterator it = toProve.entrySet().iterator();
		while (it.hasNext()) {
			thetaDash = new ArrayList<>();
			entry = (Map.Entry)it.next();
			lhsRule = entry.getValue();
			if(thetaDash.size()>0 && countNumberOfVariables(currQuery)==0){
				return thetaDash;
			}
			for(int k=0;k<lhsRule.size();k++){
				String val = "";
				keyExists = false;
				/*if(currQuerySol.equals(currQuery) && k>0 && thetaDash.size()>0){
					return thetaDash;
				}*/
				//check for fact=>variable combination
				ArrayList<String> factCheck = entry.getValue();
				boolean factC = false;
				for(int i=0;i<factCheck.size();i++){
					if(isFact(factCheck.get(i))){
							Map<String, String> newMap = new HashMap<String, String>();
							newMap.put("!", "@");
							thetaDash.add(newMap);
							returnTheta.addAll(thetaDash);
							factC = true;
				}
				}
				if(factC){
					continue;
				}
				substList = unify(currQuery, entry.getKey(),allSubstitutions);
				/*if(substList.size()==0 && countNumberOfVariables(currQuery)==0){
					substList.put("1", "2");
				}*/
				if(substList.size()!=0) {
					thetaDash.add(substList);
					
					thetaDashCopy = (ArrayList<Map<String, String>>) thetaDash.clone();
					if(!lhsRule.get(k).equals("True")) {
						thetaDashAnd = folBcAnd(lhsRule.get(k),thetaDash, parsed);
						thetaDashAnd = substVars(thetaDashCopy, thetaDashAnd);
						returnTheta.addAll(thetaDashAnd);
					} else {
						returnTheta.addAll(thetaDash);
						continue;
					}
				}
			}
		}
		return returnTheta;
	}



	private static ArrayList<Map<String, String>> substVars(ArrayList<Map<String, String>> theta_copy,
			ArrayList<Map<String, String>> theta_and) {
		// TODO Auto-generated method stub
		Map<String, String> temp = new HashMap<String,String>();
		ArrayList<Map<String, String>> theta_and_clone =(ArrayList<Map<String, String>>) theta_and.clone();
		ArrayList<Map<String, String>> theta_copy_clone =(ArrayList<Map<String, String>>) theta_copy.clone();
		if(theta_and.size()==0){
			return theta_and;
		}
		ArrayList<Map<String, String>> return_theta_copy =new ArrayList<Map<String, String>>();
		temp = theta_copy_clone.size()>0?theta_copy_clone.get(0):null;
		//temp = theta_copy.size()>0?theta_copy.get(0):null;
		if(temp!=null) {
		Set<String> keyset = temp.keySet();
	    ArrayList<String> varkeys = new ArrayList<String>();
	    Iterator<String> keys = keyset.iterator();
	    while(keys.hasNext()){
	    	String t = keys.next();
	    	if(isVariable(temp.get(t))){
	    		varkeys.add(t);
	    	}
	    }
	    HashMap<String, String> modifyMap = new HashMap<>();
	    for(int i=0;i<theta_copy.size();i++){
	    	modifyMap = new HashMap<>();
	    	for(int x=0;x<theta_and_clone.size();x++) {
	    			HashMap<String, String> currMap = new HashMap<>();
	    			modifyMap = new HashMap<>();
	    			//copy map without reference
	    			Iterator itr = theta_copy_clone.get(i).keySet().iterator();
	    			while(itr.hasNext()){
	    				String k = (String) itr.next();
	    				String v = theta_copy_clone.get(i).get(k);
	    				modifyMap.put(k, v);
	    			}
	    			
	    			
	    			
	    			
	    			
	    			currMap.putAll(theta_copy_clone.get(i));
	    			//modifyMap.putAll(theta_copy_clone.get(i));
	    			Set keysOfCurrMap = currMap.keySet();
	    	        Iterator iter = keysOfCurrMap.iterator();
	    			while(iter.hasNext()){
	    				String key = (String) iter.next();
	    				String val = currMap.get(key);
	    				if(theta_and_clone.size()>0 && theta_and_clone.get(x).containsKey(val)){
	    					modifyMap.put(key, theta_and_clone.get(x).get(val));
	    					//theta_and_clone.add(modifyMap);
	    				} 
	    			}
	    			return_theta_copy.add(modifyMap);
	    			/*if(!theta_and_clone.get(i).get(varkeys.get(x)).equals(value)) {*/
	    			//theta_and_clone.get(i).remove(varkeys.get(x));	
	    			/*if(temp.get(varkeys.get(x))!=null){*/
	    			//theta_and_clone.get(i).put(temp.get(varkeys.get(x)), value);
	    			/*} else {
	    				theta_and_clone.get(i).clear();
	    				break;
	    			}*/
	    		/*}*/
	    		/*}*/
	    	}
	    }
	}
	    return return_theta_copy;
	}

	


	private static int countNumberOfVariables(String currQuery) {
		// TODO Auto-generated method stub
		int noOfVars = 0;
		String tempString = currQuery.substring(currQuery.indexOf('(')+1,currQuery.length()-1);
		String currQuerySplit[] = tempString.split(",");
		for(int i=0;i<currQuerySplit.length;i++){
			if(!Character.isUpperCase(currQuerySplit[i].charAt(0))){
				noOfVars++;
			}
		}
		return noOfVars;
	}


	private static boolean isFact(String thisQuery) {
		// TODO Auto-generated method stub
		boolean isFactInKb = false;
		if(facts.contains(thisQuery.trim())){
			isFactInKb = true;
		}
		return isFactInKb;
	}


	private static ArrayList<Map<String, String>> folBcAnd(String goals,
			ArrayList<Map<String, String>> allSubstitutions, ArrayList<String> parsed) {
		// TODO Auto-generated method stub
		String first = ""; 
		String rest = "";;
		String unifiedString = "";
		ArrayList<Map<String, String>> thetaDash = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDashIndividual = new ArrayList<>();
		Map<String, String> tempMap = new HashMap<>();
		ArrayList<Map<String, String>> thetaDashCopy = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDoubleDash = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDoubleDashReturn = new ArrayList<>();
		if(allSubstitutions.size()==0)
			return allSubstitutions;
		else if(goals.equals("True"))
			return allSubstitutions;
		else {
			if(goals.contains("^")){
				first = goals.substring(0,goals.indexOf('^')).trim();
				rest = goals.substring(goals.indexOf('^')+1).trim();
				unifiedString = subst(first,allSubstitutions);
				//rest = subst(rest,allSubstitutions);
				thetaDash = performBackwardChaining(unifiedString, allSubstitutions, parsed);
				thetaDashCopy = (ArrayList<Map<String, String>>) thetaDash.clone();
				thetaDash = addAllVars(thetaDash, allSubstitutions);
				
				for(int j=0;j<thetaDash.size();j++){
					tempMap = thetaDash.get(j);
					thetaDashIndividual = new ArrayList<>();
					thetaDashIndividual.add(tempMap);
					thetaDoubleDash = folBcAnd(rest, thetaDashIndividual, parsed);
					thetaDoubleDashReturn.addAll(thetaDoubleDash);
				}
		} else {
			unifiedString = subst(goals,allSubstitutions);
			thetaDoubleDash = performBackwardChaining(unifiedString, allSubstitutions, parsed);
			//changes
			//substVars(allSubstitutions, thetaDoubleDash);
			//change ends
			thetaDoubleDashReturn.addAll(thetaDoubleDash);
		}
		}
		return thetaDoubleDashReturn;
	}

	private static ArrayList<Map<String, String>> addAllVars(
			ArrayList<Map<String, String>> thetaDash,
			ArrayList<Map<String, String>> allSubstitutions) {
		// TODO Auto-generated method stub
		Map<String, String> tempMap = new HashMap<>();
		Map<String, String> tempVarVarMap = new HashMap<>();
		ArrayList<Map<String, String>> thetaDashCopy = (ArrayList<Map<String, String>>) thetaDash.clone(); 
		ArrayList<Map<String, String>> thetaDashTry = new ArrayList<>();
		boolean isVarVar = false;
		for(int i=0;i<allSubstitutions.size();i++){
			tempMap = allSubstitutions.get(i);
			isVarVar = isVarSubst(tempMap);
			if(isVarVar){
				thetaDashTry = substVars(allSubstitutions, thetaDash);
			}
			for(int j=0;j<thetaDash.size();j++){
			tempVarVarMap = new HashMap<>();
			thetaDashCopy.get(j).putAll(tempMap);
			tempVarVarMap = substVarVar((HashMap<String, String>) thetaDashCopy.get(j));
			thetaDashCopy.remove(j);
			thetaDashCopy.add(j, tempVarVarMap);
		}
		}
		return thetaDashCopy;
	}


	private static Map<String, String> substVarVar(HashMap<String, String> map) {
		// TODO Auto-generated method stub
		Map<String, String> mapClone = new HashMap<>();
		mapClone = (Map<String, String>) map.clone();
		String key = "";
		String val = "";
		String substVal = "";
		for (Map.Entry<String, String> entry : map.entrySet()) {
			key = entry.getKey();
			val = entry.getValue();
			/*System.out.println("Key : "+key);
			System.out.println("Val : "+val);*/
			/*if(key==null){
				return mapClone;
			}*/
			if(isVariable(key) && isVariable(val)){
				substVal = map.get(val);
				if(substVal!=null && !substVal.trim().equals("")){
					mapClone.remove(key);
					mapClone.put(key, substVal);
				}
			}
		}
		return mapClone;
	}


	private static boolean isVarSubst(Map<String, String> tempMap) {
		// TODO Auto-generated method stub
		boolean isVarVar = false;
		String key = "";
		String val = "";
		for (Map.Entry<String, String> entry : tempMap.entrySet()) {
			key = entry.getKey();
			val = entry.getValue();
			if(isVariable(key) && isVariable(val)){
				isVarVar = true;
				break;
			}
		}
		return isVarVar;
	}


	private static String subst(String first, ArrayList<Map<String, String>> allSubstitutions) {
		StringBuffer sb = new StringBuffer();
		Map<String, String> currSubst = new HashMap<>();
		String[] conjunctString = first.trim().split("\\^");
		String[] finalConjunctString = new String[conjunctString.length];
		String finalString = "";
		String test = new String(first.substring(first.indexOf('(')+1,first.indexOf(')')));
		String init = new String(first.substring(0,first.indexOf('(')));
		for(int a=0;a<conjunctString.length;a++){
			test = new String(conjunctString[a].substring(conjunctString[a].indexOf('(')+1,conjunctString[a].indexOf(')')));
			init = new String(conjunctString[a].substring(0,conjunctString[a].indexOf('(')));
		for(int i=0;i<allSubstitutions.size();i++){
			currSubst = allSubstitutions.get(i);
			for ( String key1 : currSubst.keySet() ) {
				/*Pattern p = Pattern.compile(key1);
				 Matcher m = p.matcher(first);
				 while (m.find()) {
				     m.appendReplacement(sb, currSubst.get(key1));
				     System.out.println(m.group());
				 }
				 m.appendTail(sb);
				 System.out.println(sb.toString());*/
				if(!isVariable(currSubst.get(key1))){
				test = test.replace(key1, currSubst.get(key1));
				}
		}
		}
		init = init.concat("("+test+")");
		finalConjunctString[a] = init;
		}
		finalString += finalConjunctString[0];
		for(int x=1;x<finalConjunctString.length;x++){
			finalString += "^" + finalConjunctString[x].trim(); 
		}
		return finalString;
		//break;
	
	}

	private static Map<String, String> unify(String currQuery, String toProve, ArrayList<Map<String, String>> thetaDash) {
		// TODO Auto-generated method stub
		StringTokenizer currQueryTokenized = new StringTokenizer(currQuery.substring(currQuery.indexOf(openingPara)+1,currQuery.indexOf(closingPara)),",");
		StringTokenizer toProveTokenized = new StringTokenizer(toProve.substring(toProve.indexOf(openingPara)+1,toProve.indexOf(closingPara)),",");
		Map<String,String> substList = new HashMap<>();
		ArrayList<String> keyMatch = new ArrayList<>();
		/*if(thetaDash.size()!=0){
			substList.putAll(thetaDash.get(0));
		}*/
		String key = "";
		String tempKey = "";
		String token = "";
		String tempToken = "";
		String value = "";
		String duplicateKey = "";
		String duplicateValue = "";
		/*if(countNumberOfVariables(currQuery)==countNumberOfArguments(currQuery) && countNumberOfVariables(toProve)==countNumberOfArguments(toProve)){
			substList = unifyNoVars(currQuery, toProve, thetaDash);
			substList.put("!1", "@1");
			return substList;
		}*/
		if(countNumberOfVariables(currQuery)==0 && countNumberOfVariables(toProve)==0 && currQuery.equals(toProve)){
		if(substList.size()==0){
			substList.put("!", "@");
			return substList;
		} else {
			return substList;
		}
		}
		while (currQueryTokenized.hasMoreElements()) {
			//System.out.println(currQueryTokenized.nextElement());
			value = "";
			tempToken = currQueryTokenized.nextToken().trim();
			tempKey = toProveTokenized.nextToken().trim();
			if(!isVariable(tempKey) && !isVariable(tempToken) && !tempKey.equals(tempToken)) {
				substList.clear();
				break;
			}
			
			if(!substList.containsKey(tempKey.trim()) && !substList.containsKey(tempToken.trim())){
			if((!isVariable(tempKey) && isVariable(tempToken)) || (!isVariable(tempToken) && isVariable(tempKey))){
			if(isVariable(tempKey)){
				key = tempKey;
				token = tempToken;
				value = token;
				if(!keyMatch.contains(key)){
				substList.put(key.trim(), value.trim());
				} else {
					substList.clear();
					break;
				}
				} else if(isVariable(tempToken)) {
				key = tempToken;
				token = tempKey;
				value = token;
				if(!keyMatch.contains(key)){
				substList.put(key.trim(), value.trim());
				} else {
					substList.clear();
					break;
				}
				}
			} else if(!tempKey.equals(tempToken) && (isVariable(tempKey) && isVariable(tempToken))) {
				if(!keyMatch.contains(tempToken)){
					substList.put(tempToken,tempKey);
					keyMatch.add(tempToken);
				} else {
					//substList.clear();
					break;
				}
				
				/*if(!keyMatch.contains(tempToken)){
					keyMatch.add(tempToken);
				} else {
				substList.clear();
				break;
				}*/
			} 
		} else {
			if(isVariable(tempKey) || isVariable(tempToken)){
				if(isVariable(tempKey)) {
					duplicateKey = tempKey;
				} else {
					duplicateKey = tempToken;
				}
			}
			if(!isVariable(tempKey) || !isVariable(tempToken)){
				if(!isVariable(tempKey)) {
					duplicateValue = tempKey;
				} else {
					duplicateValue = tempToken;
				}
			} else {
				duplicateValue = tempToken; //both are vars then key = tempkey-->first assignment above and value = temptoken
			}
			if(substList.get(duplicateKey)==null){
				substList.clear();
				break;
			}
			if(substList!=null && !substList.get(duplicateKey).equals(duplicateValue)){
				substList.clear();
				break;
			}
			/*if(!substList.get(duplicateKey).equals(duplicateValue)){
				substList.clear();
				return substList;
			}*/
		}
		}
		return substList;
	}


	private static Map<String, String> unifyNoVars(String currQuery,
			String toProve, ArrayList<Map<String, String>> thetaDash) {
		// TODO Auto-generated method stub
		Map<String,String> substList = new HashMap<>();
		String value = "", tempToken = "", tempKey = "";
		StringTokenizer currQueryTokenized = new StringTokenizer(currQuery.substring(currQuery.indexOf(openingPara)+1,currQuery.indexOf(closingPara)),",");
		StringTokenizer toProveTokenized = new StringTokenizer(toProve.substring(toProve.indexOf(openingPara)+1,toProve.indexOf(closingPara)),",");
		while (currQueryTokenized.hasMoreElements()) {
			//System.out.println(currQueryTokenized.nextElement());
			value = "";
			tempKey= currQueryTokenized.nextToken().trim();
			tempToken  = toProveTokenized.nextToken().trim();
			if(substList.containsKey(tempToken)){
				substList.clear();
				break;
			} else {
				substList.put(tempKey, tempToken);
			}
		}
		return substList;
	}


	private static boolean isVariable(String check) {
		// TODO Auto-generated method stub
		boolean isVarKey = false;
		if(!Character.isUpperCase(check.trim().charAt(0))){
			isVarKey = true;
		} 
		return isVarKey;
	}


	public static Map<String, ArrayList<String>> fetchRulesForGoal(String theRegex, Map<String, ArrayList<String>> kbSentencesCopy, String currQuery){
		Pattern checkRegex = Pattern.compile(theRegex); 
		Map<String, String> substList = new HashMap<>();
		ArrayList<String> toProve = new ArrayList<>();
		Map<String, ArrayList<String>> returnRules = new HashMap<>();
		boolean matchFound = false;
		Matcher regexMatcher = null;
		currQuery = currQuery.trim();
		String regexExp = currQuery.substring(currQuery.indexOf(openingPara)+1,currQuery.indexOf(closingPara));
		for ( String key : kbSentencesCopy.keySet() ) {
			key = key.trim();
			regexMatcher = checkRegex.matcher(key.trim());
			matchFound = false;
			while(regexMatcher.find()){/*
				System.out.println(regexMatcher.group());
				toProve = kbSentencesCopy.get(key);
				returnRules.put(regexMatcher.group(),toProve);
				if(substList.size()>0){
				matchFound = true;
				}
				break;
			*/} 
			/*System.out.println("Current Query : "+currQuery);
			System.out.println("Key : "+key);*/
			if(currQuery.equals("Married(Bob,x31)")){
				System.out.println("Hello");
			}
			if(currQuery.substring(0,currQuery.indexOf('(')).equals(key.trim().substring(0,key.indexOf('(')))) {
				toProve = kbSentencesCopy.get(key);
				returnRules.put(key,toProve);
			}
		}
		return returnRules;
	}
	 
	 
	private static int countNumberOfArguments(String currQuery) {
		// TODO Auto-generated method stub
		int noOfCommas = 0;
		int noOfVars = 0;
		String tempString = currQuery.substring(currQuery.indexOf('(')+1,currQuery.length()-1);
		String currQuerySplit[] = tempString.split(",");
		for(int i=0;i<currQuery.length();i++){
			if(currQuery.charAt(i)==','){
				noOfCommas++;
			}
		}
		for(int i=0;i<currQuerySplit.length;i++){
			if(!Character.isUpperCase(currQuerySplit[i].charAt(0))){
				noOfVars++;
			}
		}
		return (noOfCommas+1);
	}


	private static void initialize(BufferedReader br) throws IOException {
		// TODO Auto-generated method stub
		 ArrayList<String> consequent = new ArrayList<>();
		 String[] kbAntecedentSplit = new String[100]; 
		 for(int i=0;i<noOfQueries; i++) {
	        	queries.add(br.readLine());
	        }
	        System.out.println("Queries:");
	        for(int i=0;i<queries.size();i++){
	        	System.out.println(queries.get(i)+" ");
	        }
	        System.out.println();
	        noOfSentencesInKb = Integer.parseInt(br.readLine());
	        for(int i=0;i<noOfSentencesInKb;i++){
	        	String tempStr = br.readLine();
	        	kbArr.add(tempStr.trim());
	        	if(tempStr.contains("=>")){
	        	kbImplicationSplit = tempStr.split("=>");
	        	consequent = kbSentences.get(kbImplicationSplit[1].trim());
	        	if(consequent==null) {
	        		consequent = new ArrayList<>();
	        	}
	        	consequent.add(kbImplicationSplit[0].trim());
	        	kbSentences.put(kbImplicationSplit[1].trim(), consequent); 
	        } else {
        		consequent = new ArrayList<>();
        		consequent.add("True");
        		kbSentences.put(tempStr.trim(), consequent);
        		facts.add(tempStr.trim());
        	}
	 	}
	        System.out.println("Knowledge Base:");
	        retrieveValuesFromListMethod(kbSentences);
	        System.out.println();
	 }

	public static void retrieveValuesFromListMethod(Map map)
	    {
	        Set keys = map.keySet();
	        Iterator itr = keys.iterator();
	 
	        String key;
	        ArrayList<String> value = new ArrayList<>();
	        while(itr.hasNext())
	        {
	            key = (String)itr.next();
	            value = (ArrayList<String>)map.get(key);
	            for(int i=0;i<value.size();i++){
	            System.out.println(key + " - "+ value.get(i));
	        }}
	    }
}
