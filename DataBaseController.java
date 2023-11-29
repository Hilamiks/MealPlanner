package mealplanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class DataBaseController {
	static final String DB_URL = "jdbc:postgresql://localhost/meals_db";
	//static final String DB_URL = "jdbc:postgresql:meals_db";
	static final String USER = "postgres";
	static final String PASS = "1111";

	List<String> mealTimes = List.of("breakfast","lunch","dinner");

	private static DataBaseController instance;

	private DataBaseController() {
	}

	static DataBaseController getInstance() {
		if (instance == null) {
			instance = new DataBaseController();
		}
		return instance;
	}

	void start() {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			statement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS meals (" +
							"category VARCHAR(1024)," +
							"meal VARCHAR(1024) UNIQUE NOT NULL," +
							"meal_id INTEGER UNIQUE" +
							");"
			);
			statement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS ingredients (" +
							"ingredient VARCHAR(1024) NOT NULL," +
							"ingredient_id INTEGER," +
							"meal_id INTEGER REFERENCES meals(meal_id)" +
							");"
			);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	void show() {
		System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
		String cat = Taker.getInstance().getInput();
		while (!cat.equals("breakfast") && !cat.equals("lunch") && !cat.equals("dinner")) {
			System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
			cat = Taker.getInstance().getInput();
		}
		showCategory(cat);
	}

	void showCategory(String cat) {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			ArrayList<Integer> allID = new ArrayList<>();
			try (ResultSet rsMeal = statement.executeQuery(
					"SELECT * FROM meals WHERE category = '" + cat + "';"
			)) {
				if (rsMeal.next()) {
					System.out.println("Category: " + cat);
					do {
						allID.add(rsMeal.getInt("meal_id"));
					} while (rsMeal.next());
				} else {
					System.out.println("No meals found.");

				}
			}
			for (int i : allID) {
				try (ResultSet rsMeal = statement.executeQuery(
						"SELECT * FROM meals " +
								"WHERE meal_id = " + i + ";"
				)) {
					rsMeal.next();
					System.out.println("Name: " + rsMeal.getString("meal"));
					System.out.println("Ingredients: ");
				}
				try (ResultSet rsIngredients = statement.executeQuery(
						"SELECT * FROM ingredients " +
								"WHERE meal_id = " + i + ";"
				)) {
					while (rsIngredients.next()) {
						System.out.println(rsIngredients.getString("ingredient"));
					}
				}
				System.out.println();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	void plan() {
		createPlanTable();
		planForDays();
	}

	void add(Meal meal) {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			String name = meal.name;
			String category = meal.type;
			ArrayList<String> ings = meal.ingredients;
			int mealID;
			ResultSet rsLatestID = statement.executeQuery(
					"SELECT * FROM meals "
			);
			if (rsLatestID.next()) {
				ResultSet rsMax = statement.executeQuery(
						"SELECT MAX(meal_id) FROM meals;"
				);
				rsMax.next();
				mealID = rsMax.getInt("max") + 1;
			} else {
				mealID = 1;
			}
			statement.executeUpdate(
					"INSERT INTO meals (meal, category, meal_id) " +
							"VALUES ('" +
							name + "', '" + category + "', " + mealID +
							");"
			);
			ResultSet mealFinder = statement.executeQuery(
					"SELECT meal_id FROM meals " +
							"WHERE meal = '" + name + "';"
			);
			mealFinder.next();
			int mealIdentifier = mealFinder.getInt("meal_id");
			for (String ing : ings) {
				statement.executeUpdate(
						"INSERT INTO ingredients (ingredient, meal_id) " +
								"VALUES ('" + ing + "', " + mealIdentifier + ");"
				);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	void clear() {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			statement.executeUpdate("DROP TABLE IF EXISTS ingredients;" +
					"DROP TABLE IF EXISTS meals;");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	void createPlanTable() {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			statement.executeUpdate(
					"DROP TABLE IF EXISTS plan;"
			);
			statement.executeUpdate(
					"CREATE TABLE plan (" +
							"day_of_week VARCHAR(1024)," +
							"meal VARCHAR(1024)," +
							"category VARCHAR(1024)," +
							"meal_id INTEGER REFERENCES meals(meal_id)" +
							");"
			);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	void planForDays() {
		for (Days day : Days.values()) {
			System.out.println();
			System.out.println(day);
			for (String time : mealTimes) {
				ArrayList<String> tempMeals = new ArrayList<>();
				String choice = "";
				//print all "time" options
				getOptions(tempMeals, time);
				//end
				//choose meal for "time"
				System.out.println("Choose the " + time + " for " + day + " from the list above:");
				choice = Taker.getInstance().getInput();
				while (!tempMeals.contains(choice)) {
					System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
					choice = Taker.getInstance().getInput();
				}
				//end
				//add choice to plan
				int mealID = getMealID(choice);
				addChoiceToPlan(day, time, choice, mealID);
				//end
			}
			System.out.println("Yeah! We planned the meals for " + day + ".");
		}
		printMenu();
	}

	void getOptions(ArrayList<String> meals, String time) {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			ResultSet options = statement.executeQuery(
					"SELECT * FROM meals " +
							"WHERE category = '" + time + "' " +
							"ORDER BY meal"
			);
			while (options.next()) {
				System.out.println(options.getString("meal"));
				meals.add(options.getString("meal"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	void addChoiceToPlan(Days day, String time, String choice, int mealID) {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			statement.executeUpdate(
					"INSERT INTO plan(day_of_week,category,meal,meal_id) " +
							"VALUES(" +
							"'"+day+"'," +
							"'"+time+"'," +
							"'"+choice+"'," +
							mealID+
							");"
			);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	void printMenu() {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			for (Days day : Days.values()) {
				System.out.println(day);
				for (String time : mealTimes) {
					ResultSet option = statement.executeQuery(
							"SELECT " +
									"meal " +
								"FROM " +
									"plan " +
								"WHERE " +
									"day_of_week = '" + day + "' " +
									"AND " +
									"category = '" + time + "';"
					);
					option.next();
					System.out.println(
							time.substring(0,1).toUpperCase()+time.substring(1) +
							": " +
							option.getString("meal")
					);

				}

				System.out.println();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	int getMealID(String choice) {
		int mealID;
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			ResultSet id = statement.executeQuery(
					"SELECT meal_id FROM meals WHERE meal = '" + choice + "';"
			);
			id.next();
			mealID = id.getInt("meal_id");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return mealID;
	}

	boolean save() {
		if(!planExists()) {
			System.out.println("Unable to save. Plan your meals first.");
			return false;
		}
		createShoppingList();
		return true;
	}

	boolean planExists() {
		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			ResultSet plan = statement.executeQuery(
					"SELECT * FROM plan"
			);
			return plan.next();
		} catch (SQLException e) {
			return false;
		}
	}

	void createShoppingList() {
		System.out.println("Input a filename: ");
		String fileName = Taker.getInstance().getInput();
		File shoppingList = new File(fileName);

		try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			ResultSet mealList = statement.executeQuery(
					"SELECT meal FROM plan"
			);
			List<String> meals = new ArrayList<>();
			while (mealList.next()) {
				meals.add(mealList.getString("meal"));
			}
			HashMap<String, Integer> toShop = new HashMap<>();
			for (String meal : meals) {
				int id = getMealID(meal);
				ResultSet ingredients = statement.executeQuery(
						"SELECT ingredient FROM ingredients " +
								"WHERE meal_id = " + id + ";"
				);
				while (ingredients.next()) {
					String ingredient = ingredients.getString("ingredient");
					if (!toShop.containsKey(ingredient)) {
						toShop.put(ingredient,1);
					} else {
						toShop.replace(ingredient,(toShop.get(ingredient) + 1));
					}
				}
			}
			try (FileWriter writer = new FileWriter(fileName)) {
				for (String item : toShop.keySet()) {
					if (toShop.get(item) > 1) {
						writer.write(item + " x" + toShop.get(item)+"\n");
					} else {
						writer.write(item+"\n");
					}
				}
			}
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("Saved!");
	}
}
