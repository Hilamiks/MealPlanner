package mealplanner;

public class DayOfWeek {

	Meal breakfast;

	Meal lunch;

	Meal dinner;

	Days day;

	DayOfWeek(Days day){
		this.day = day;
	}

	void addBreakfast(Meal breakfast) {
		this.breakfast = breakfast;
	}
	void addLunch(Meal lunch) {
		this.lunch = lunch;
	}
	void addDinner(Meal dinner) {
		this.dinner = dinner;
	}
}
