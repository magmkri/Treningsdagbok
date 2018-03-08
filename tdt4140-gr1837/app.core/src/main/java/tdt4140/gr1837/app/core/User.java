package tdt4140.gr1837.app.core;

import java.util.ArrayList;
import java.util.List;

// Midlertidig user-klasse til a fylle userdatabasen, testes derfor ikke
public class User {
	
	private String name;
	private String phoneNumber;
	private int age;
	private String motivation;
	private int id;
	private Trainer trainer;
	
	public User(String name, String phoneNumber, int age, String motivation, int id) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.age = age;
		this.motivation = motivation;
		this.id = id;
	}
	
	public void setTrainer(Trainer trainer) {
		this.trainer = trainer;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public int getAge() {
		return age;
	}

	public String getMotivation() {
		return motivation;
	}

	public Trainer getTrainer() {
		return trainer;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	
	//Vi trenger:
		//- Navn p� �velse
		//- Vekt
		//- Representasjon 1rm/vektvolum
	
	//Returnerer alle exercises en bruker har hatt ila alle sessions
	public List<Exercise> getExercises() {
		List<Exercise> exercises = new ArrayList<>();
		List<Session> sessions = SQLConnector.getSessions(this.getId());
		for (Session session : sessions) {
			exercises.addAll(SQLConnector.getAllExercises(session.getId()));
		}
		return exercises;
	}

}
