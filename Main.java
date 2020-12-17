package com.stackroute.datamunger.query.parser;

public class Main {

	public static void main(String[] args) {
		String[] queries = { "Select name, age, where_city, from_city, order_by_city, "
				+ "city_where from data.csv where age > 40 order by name group by city",
				"Select name, age from data.csv where age > 40 order by name",
				"Select name, age from data.csv where age > 40",
				"Select name, age from data.csv",
				"Select name, age from data.csv order by name group by city",
				"Select name, age from data.csv order by name",
				"Select name, age from data.csv group by city",
				"Select * from data.csv where city=NewYork and age < 50",
				"select city,winner,team1,team2 from data/ipl.csv order by city",
				"select city,winner,team1,team2 from data/ipl.csv group by city",
				"select city,win_by_runs from data/ipl.csv",
				"select city,winner,team1,team2,player_of_match from data/ipl.csv " +
						"where season >= 2008 or toss_decision != bat",
				"select city,winner,team1,team2,player_of_match from data/ipl.csv where " +
				 		"season >= 2008 or toss_decision != bat and city = bangalore",
				"select avg(win_by_wickets), min(win_by_runs) from ipl.csv"
		};
		
		QueryParser queryParser = new QueryParser();
		
		for (int i = 0; i<queries.length; i++) {
			QueryParameter qp = queryParser.parseQuery(queries[i]);
			
			System.out.println("********************************");
			System.out.println("File name: " + qp.getFileName());
			System.out.println("Base query: " + qp.getBaseQuery());
			System.out.println("Order by fields: " + qp.getOrderByFields());
			System.out.println("Group by fields: " + qp.getGroupByFields());
			System.out.println("Selected fields: " + qp.getFields());
			System.out.println("Conditions: " + qp.getRestrictions());
			System.out.println("Logical Operators: " + qp.getLogicalOperators());
			System.out.println("Aggregate Functions: " + qp.getAggregateFunctions());
		}
	}

}
