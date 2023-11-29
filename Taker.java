package mealplanner;

import java.util.Scanner;

public class Taker {
	private static Taker instance;

	Scanner scanner = new Scanner(System.in);

	private Taker(){}

	public static Taker getInstance() {
		if (instance == null) {
			instance = new Taker();
		}
		return instance;
	}

	public Meal getMeal() {
		System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
		String type = getInput();
		while(!Validator.correctType(type)) {
			System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
			type = getInput();
		}
		System.out.println("Input the meal's name:");
		String name = getInput();
		while(!Validator.correctName(name) || name.isBlank()) {
			System.out.println("Wrong format. Use letters only!");
			name = getInput();
		}
		Meal meal = new Meal(name,type);
		System.out.println("Input the ingredients:");
		String ingredients = getInput();
		while(!Validator.correctIngredient(ingredients) || ingredients.isBlank()) {
			System.out.println("Wrong format. Use letters only!");
			ingredients = getInput();
		}
		meal.addIngredients(ingredients);
		System.out.println("The meal has been added!");
		return meal;
	}

	public String getInput() {
		return scanner.nextLine();
	}
}
