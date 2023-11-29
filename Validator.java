package mealplanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
	static Pattern typePattern = Pattern.compile("breakfast|lunch|dinner");
	static Pattern namePattern = Pattern.compile("[a-zA-Z\\s]+");

	static Pattern ingredientPattern = Pattern.compile("(\\s*[a-zA-Z]+)+");

	public static boolean correctType(String type){
		return typePattern.matcher(type).matches();
	}

	public static boolean correctName(String name){
		return namePattern.matcher(name).matches();
	}

	public static boolean correctIngredient(String ingredients){
		String[] tempArray = ingredients.split(",");
		for (String ing : tempArray) {
			if (!ingredientPattern.matcher(ing).matches()) {
				return false;
			}
		}
		return true;
	}
}
