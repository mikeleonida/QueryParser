package com.stackroute.datamunger.query.parser;

import java.util.ArrayList;
import java.util.List;

/* 
 * This class will contain the elements of the parsed Query String such as conditions,
 * logical operators,aggregate functions, file name, fields group by fields, order by
 * fields, Query Type
 * */

public class QueryParameter {
	
	private String query = "";

	public QueryParameter() {
		
	}
	
	public QueryParameter(String query) {
		super();
		this.query = query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFileName() {
		String[] words = getBaseQuery().split(" from ");
		if (words.length > 1) {
			return words[1];
		}
		
		return null;
	}

	public String getBaseQuery() {
		return query.split(" where ")[0].split(" order by ")[0].split(" group by ")[0];
	}

	public List<Restriction> getRestrictions() {
		List<Restriction> lr = new ArrayList<Restriction>();
		String[] conditionWords = query.trim().replaceAll("\\s+", " ").split(" where ");
		if (conditionWords.length<2) {
			return lr;
		}
		String conditionClause = conditionWords[1].split(" order by ")[0].split(" group by ")[0];
		return getRestrictionClauses(conditionClause);
	}

	public List<String> getLogicalOperators() {
		return parseLogicalOps(query);
	}

	public List<String> getFields() {
		return getListFromArr(getBaseQuery().replaceAll("^\\s*(S|s)elect\\s+", "").split(" from ")[0]
				.replaceAll("\\s*,\\s*"," ").split(" "));
	}

	public List<AggregateFunction> getAggregateFunctions() {
		String[] fields = getBaseQuery().replace("\\s*(Select|select)\\s+", "").split(" from ")[0]
				.replaceAll("\\s*,\\s*"," ").split(" ");
		List<AggregateFunction> laf = new ArrayList<AggregateFunction>();
		for (String f : fields) {
			f = f.replaceAll("\\s+", "");
			if (f.contains( "(") && f.contains( ")" ) && ( f.indexOf('(') < f.indexOf(')') ) ) {
				String fn = f.substring(0, f.indexOf('('));
				if (fn.equalsIgnoreCase("min") || fn.equalsIgnoreCase("max") || fn.equalsIgnoreCase("avg")
						|| fn.equalsIgnoreCase("count") || fn.equalsIgnoreCase("sum")) {
					AggregateFunction af = new AggregateFunction(f.substring(f.indexOf('(')+1, f.indexOf(')')), fn);
					laf.add(af);
				}
			}
		}
		return laf;
	}

	public List<String> getGroupByFields() {
		String[] groupByWords = query.split(" group by ");
		if (groupByWords.length > 1) {
			return  getListFromArr(groupByWords[1].split(" order by ")[0].replaceAll("\\s*,\\s*"," ").split(" "));
		} 
		return null;
	}

	public List<String> getOrderByFields() {
		String[] orderByWords = query.split(" order by ");
		if (orderByWords.length > 1) {
			return  getListFromArr(orderByWords[1].split(" group by ")[0].replaceAll("\\s*,\\s*"," ").split(" "));
		} 
		return null;
	}
	
	private List<String> getListFromArr(String[] sa) {
		ArrayList<String> gf = new ArrayList<String>();
		for (String field : sa) {
			gf.add(field);
		}
		return gf;
	}
	
	private List<Restriction> getRestrictionClauses(String conditionClause) {
		List<Restriction> lr = new ArrayList<Restriction>();
		List<String> logicalOps = parseLogicalOps(conditionClause); 

		String newConditionClause = new String(conditionClause);
		for (int i=0; i<=logicalOps.size(); i++) {
			String op = " ";
			if (i!=logicalOps.size()) {
				op = logicalOps.get(i);
			}
			String[] words = newConditionClause.split(" " + op + " ");
			
			String currentCondition = words[0].trim().replaceAll("\\s+", "");
			lr.add(parseRestriction(currentCondition));
			
			if (words.length<2) {
				break;
			}
			newConditionClause = newConditionClause.substring(newConditionClause.indexOf(" " + op + " ") +
					op.length()+2);
		}
		
		return lr;
	}
	
	private Restriction parseRestriction(String s) {
		if (s.contains("<=")) {
			return new Restriction(s.substring(0, s.indexOf("<=")), s.substring(s.indexOf("<=")+2), "<=");
		} else if (s.contains(">=")) {
			return new Restriction(s.substring(0, s.indexOf(">=")), s.substring(s.indexOf(">=")+2), ">=");
		} else if (s.contains("!=")) {
			return new Restriction(s.substring(0, s.indexOf("!=")), s.substring(s.indexOf("!=")+2), "!=");
		} else if (s.contains("<")) {
			return new Restriction(s.substring(0, s.indexOf("<")), s.substring(s.indexOf("<")+1), "<");
		} else if (s.contains(">")) {
			return new Restriction(s.substring(0, s.indexOf(">")), s.substring(s.indexOf(">")+1), ">");
		} else if (s.contains("=")) {
			return new Restriction(s.substring(0, s.indexOf("=")), s.substring(s.indexOf("=")+1), "=");
		}
		
		return new Restriction();
	}
	
	private List<String> parseLogicalOps(String q) {
		List<String> ls = new ArrayList<String>();
	
		String[] queryWords = q.trim().replaceAll("\\s+", " ").split(" ");
		for (String s : queryWords) {
			if (s.compareToIgnoreCase("and") == 0 || (s.compareToIgnoreCase("or") == 0) 
				|| (s.compareToIgnoreCase("not") == 0)) {
				ls.add(s);
			}
		}
		return ls;	
	}
	
	
	
}