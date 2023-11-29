package mealplanner;

public class Main {
	public static void main(String[] args) {
		Taker taker = Taker.getInstance();
		//Giver giver = Giver.getInstance();
		DataBaseController menu = DataBaseController.getInstance();
		//menu.clear();
		menu.start();
		String todo = "";
		while (!todo.equals("exit")) {
			System.out.println("What would you like to do (add, show, plan, save, exit)?");
			todo = taker.getInput();
			switch(todo) {
				case "add":
					menu.add(taker.getMeal());
					break;
				case "show":
					menu.show();
					break;
				case "plan":
					menu.plan();
					break;
				case "save":
					menu.save();
					break;
				case "exit":
					System.out.println("Bye!");
					break;
				default:
					break;
			}
		}
	}
}