package mealplanner;

public class Giver {
	private static Giver instance;

	private Giver(){}

	public static Giver getInstance() {
		if (instance == null) {
			instance = new Giver();
		}
		return instance;
	}

	public void printInfo(Meal meal) {
		System.out.println("Category: " + meal.type);
		System.out.println("Name: " + meal.name);
		System.out.println("Ingredients:");
		meal.getIngredients();
	}
}
