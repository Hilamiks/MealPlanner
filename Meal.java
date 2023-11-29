package mealplanner;

import java.util.ArrayList;
import java.util.Arrays;

public class Meal {

	String name;

	String type;

	ArrayList<String> ingredients = new ArrayList<>();

	public Meal(String name, String type) {
		this.name = name;
		this.type = type;
	}

	void addIngredients(String ingredients) {
		String[] ings = ingredients.split(",[\\s]*");
		this.ingredients.addAll(Arrays.asList(ings));
	}

	void getIngredients() {
		for (String ingredient : ingredients) {
			System.out.println(ingredient);
		}
	}
}
