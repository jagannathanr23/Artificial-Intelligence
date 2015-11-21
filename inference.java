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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class inference {
	static BufferedWriter wr = null;
	static Map<String, ArrayList<String>> kbSentences = new HashMap();
	static ArrayList<String> facts = new ArrayList<>();
	static ArrayList<String> queries = new ArrayList<String>();
	static ArrayList<String> parsed = new ArrayList<String>();
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
		try {
		wr = new BufferedWriter(new FileWriter("output.txt"));
		 if (0 < args.length) {
		 inFile = new File(args[0]);
		  }
		        br = new BufferedReader(new FileReader(inFile));
		        noOfQueries = Integer.parseInt(br.readLine());
		        initialize(br);
		        for(int i=0;i<noOfQueries;i++){
		        	allSubstitutions = new ArrayList<>();
					currQuery = queries.get(i);
					currQuerySol = queries.get(i);
					if(isFact(currQuery)) {
						wr.write("TRUE");
						wr.newLine();
						continue;
					}
					allSubstitutions = performBackwardChaining(currQuery,allSubstitutions);
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

	
	 @SuppressWarnings("unchecked")
	private static ArrayList<Map<String, String>> performBackwardChaining(String thisQuery, ArrayList<Map<String, String>> allSubstitutions) {
		// TODO Auto-generated method stub
		String currQuery = thisQuery.trim();
		String regexPattern = "";
		ArrayList<Map<String, String>> thetaDash = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDashCopy = new ArrayList<>();
		ArrayList<Map<String, String>> allSubstitutions1 = new ArrayList<>(); 
		boolean isThisQuery = false;
		Map<String, ArrayList<String>> toProve = new HashMap<>();
		ArrayList<String> lhsRule = new ArrayList<>();
		boolean keyExists = false; 
		Map.Entry<String, ArrayList<String>> entry = null;
		Map<String, String> substList = new HashMap<>();
		int countArgs = countNumberOfArguments(currQuery);
		int countVars = countNumberOfVariables(currQuery);
		boolean isFactInKb = isFact(thisQuery);
		if(isFactInKb){
			return allSubstitutions;
		}
		if(parsed.contains(thisQuery.toString())) {
			allSubstitutions.clear();
			return allSubstitutions;
		}
		parsed.add(thisQuery);
		/*if(currQuery.equals(currQuerySol) && allSubstitutions.size()>0){
			allSubstitutions.clear();
			return allSubstitutions;
		}*/
		
		if(!currQuery.contains("~")){
			regexPattern = "("+"("+"^(?!\\~)"+")"+"["+currQuery.substring(0,currQuery.indexOf('('))+"]"+")"+"\\("+"(\\w+)";
		} else {
			regexPattern = "(\\"+currQuery.substring(0,1)+")"+"["+currQuery.substring(1,currQuery.indexOf('('))+"]"+"\\("+"(\\w+)";
		}
		for(int j=1;j<countArgs;j++){
			regexPattern+="(,)"+"(\\w+)";
		}
		regexPattern+="(\\))";
		toProve = fetchRulesForGoal(regexPattern, kbSentences, currQuery);
		Iterator it = toProve.entrySet().iterator();
		/*if(toProve.size()==1){
			if(countNumberOfVariables(toProve.entrySet().iterator().next().getKey())==0 && countVars==0){
				allSubstitutions.remove(0);
				return allSubstitutions;
			}
			//print false;continue with next query
		}*/
		while (it.hasNext()) {
			entry = (Map.Entry)it.next();
			lhsRule = entry.getValue();
			for(int k=0;k<lhsRule.size();k++){
				String val = "";
				keyExists = false;
				if(currQuerySol.equals(currQuery) && k>0 && thetaDash.size()>0){
					return thetaDash;
				}
				substList = unify(currQuery, entry.getKey(),allSubstitutions);
				if(substList.size()!=0) {
					thetaDash.add(substList);
					thetaDashCopy = (ArrayList<Map<String, String>>) thetaDash.clone();
					if(!lhsRule.get(k).equals("True")) {
						thetaDash = folBcAnd(lhsRule.get(k),thetaDash);
					} else {
						continue;
					}
				}
				/*if(allSubstitutions.size()==0){
					allSubstitutions.add(substList);
				}*/
			}
		}
		return thetaDash;
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
			ArrayList<Map<String, String>> allSubstitutions) {
		// TODO Auto-generated method stub
		String first = ""; 
		String rest = "";;
		String unifiedString = "";
		ArrayList<Map<String, String>> thetaDash = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDashIndividual = new ArrayList<>();
		Map<String, String> tempMap = new HashMap<>();
		ArrayList<Map<String, String>> thetaDashCopy = new ArrayList<>();
		ArrayList<Map<String, String>> thetaDoubleDash = new ArrayList<>();
		if(allSubstitutions.size()==0)
			return null;
		else if(goals.equals("True"))
			return allSubstitutions;
		else {
			if(goals.contains("^")){
				first = goals.substring(0,goals.indexOf('^')).trim();
				rest = goals.substring(goals.indexOf('^')+1).trim();
				unifiedString = subst(first,allSubstitutions);
				thetaDash = performBackwardChaining(unifiedString, allSubstitutions);
				thetaDashCopy = (ArrayList<Map<String, String>>) thetaDash.clone();
				for(int j=0;j<thetaDash.size();j++){
					tempMap = thetaDash.get(j);
					thetaDashIndividual = new ArrayList<>();
					thetaDashIndividual.add(tempMap);
					thetaDoubleDash = folBcAnd(rest, thetaDashIndividual);
				}
		} else {
			unifiedString = subst(goals,allSubstitutions);
			thetaDoubleDash = performBackwardChaining(unifiedString, allSubstitutions);
		}
		}
		return thetaDoubleDash;
	}

	private static String subst(String first, ArrayList<Map<String, String>> allSubstitutions) {
		StringBuffer sb = new StringBuffer();
		Map<String, String> currSubst = new HashMap<>();
		String test = new String(first.substring(first.indexOf('(')+1,first.indexOf(')')));
		String init = new String(first.substring(0,first.indexOf('(')));
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
				test = test.replace(key1, currSubst.get(key1));
		}
		}
		init = init.concat("("+test+")");
		return init;
		//break;
	
	}

	private static ArrayList<Map<String, String>> buildSubstitutionList(
			Map<String, String> substList, ArrayList<Map<String, String>> allSubstitutions, boolean keyExists) {
		// TODO Auto-generated method stub
		Map<String, String> checkIfKeyExists = new HashMap<>();
		String key = "";
		String checkIfKeyExistsVal = "";
		String substListKey = "";
		String substListVal  = "";
		ArrayList<Map<String, String>> newSubstList = new ArrayList<>();
		if(allSubstitutions.size()==0) {
			newSubstList.add(substList);
		}
		for(int z=0;z<allSubstitutions.size();z++){
			String val = "";
			checkIfKeyExists = allSubstitutions.get(z);
			key = checkIfKeyExists.entrySet().iterator().next().getKey();
			if(substList.containsKey(key)){
				val = substList.get(key);
				checkIfKeyExistsVal = checkIfKeyExists.entrySet().iterator().next().getValue();
				if(val.equals(checkIfKeyExistsVal)){
					keyExists = true;
					//break;
				}
			}
			newSubstList.add(checkIfKeyExists);
		} 
		if(!keyExists){
			substListKey = substList.entrySet().iterator().next().getKey();
			substListVal = substList.entrySet().iterator().next().getValue();
			checkIfKeyExists.put(substListKey, substListVal);
			//allSubstitutions.add(substList);
		} 
		
		return newSubstList;
	}


	private static Map<String, String> unify(String currQuery, String toProve, ArrayList<Map<String, String>> thetaDash) {
		// TODO Auto-generated method stub
		StringTokenizer currQueryTokenized = new StringTokenizer(currQuery.substring(currQuery.indexOf(openingPara)+1,currQuery.indexOf(closingPara)),",");
		StringTokenizer toProveTokenized = new StringTokenizer(toProve.substring(toProve.indexOf(openingPara)+1,toProve.indexOf(closingPara)),",");
		Map<String,String> substList = new HashMap<>();
		if(thetaDash.size()!=0){
			substList.putAll(thetaDash.get(0));
		}
		String key = "";
		String tempKey = "";
		String token = "";
		String tempToken = "";
		String value = "";
		while (currQueryTokenized.hasMoreElements()) {
			//System.out.println(currQueryTokenized.nextElement());
			value = "";
			tempToken = currQueryTokenized.nextToken();
			tempKey = toProveTokenized.nextToken();
			if((!isVariable(tempKey) && isVariable(tempToken)) || (!isVariable(tempToken) && isVariable(tempKey))){
			if(isVariable(tempKey)){
				key = tempKey;
				token = tempToken;
				value = token;
				substList.put(key, value);
				} else if(isVariable(tempToken)) {
				key = tempToken;
				token = tempKey;
				value = token;
				substList.put(key, value);
				}
			} else if(!tempKey.equals(tempToken)) {
				substList.clear();
				break;
			}
		}
		return substList;
	}


	private static boolean isVariable(String check) {
		// TODO Auto-generated method stub
		boolean isVarKey = false;
		if(!Character.isUpperCase(check.charAt(0))){
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
		String regexExp = currQuery.substring(currQuery.indexOf(openingPara)+1,currQuery.indexOf(closingPara));
		for ( String key : kbSentencesCopy.keySet() ) {
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
			if(currQuery.substring(0,currQuery.indexOf('(')).equals(key.trim().substring(0,currQuery.indexOf('(')))) {
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
	        	if(tempStr.contains("=>")){
	        	kbImplicationSplit = tempStr.split("=>");
	        	consequent = kbSentences.get(kbImplicationSplit[1]);
	        	if(consequent==null) {
	        		consequent = new ArrayList<>();
	        	}
	        	consequent.add(kbImplicationSplit[0]);
	        	kbSentences.put(kbImplicationSplit[1], consequent); 
	        } else {
        		consequent = new ArrayList<>();
        		consequent.add("True");
        		kbSentences.put(tempStr, consequent);
        		facts.add(tempStr);
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
