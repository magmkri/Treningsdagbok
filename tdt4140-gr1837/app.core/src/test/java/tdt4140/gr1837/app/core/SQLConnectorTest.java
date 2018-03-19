package tdt4140.gr1837.app.core;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class SQLConnectorTest {

	
	@Test
	public void testGetMuscles() {
		Map<String, Integer> session1;
		try {
			session1 = SQLConnector.getMusclesTrained(1);
			assertTrue(4 == session1.get("biceps"));
		} catch (SQLException e) {
			fail();
		}
	}
	
	// Hardkoder data, endres naar insert-metode er implementert
	@Test
	public void testGetSessions() {
		try {
			Session testSession = new Session("Bra pump!", "2018-03-01", 2);
			List<Session> sessions = SQLConnector.getSessions(5);
			Session firstSession = sessions.get(0);
			assertEquals(firstSession.getDate(), testSession.getDate());
			assertEquals(firstSession.getNote(), testSession.getNote());
			assertEquals(firstSession.getId(), testSession.getId());
			assertEquals(firstSession.toString(), testSession.toString());
		} catch(SQLException e) {
			fail();
		}
	}
	
	// Hardkoder data, endres naar insert-metode er implementert
	@Test
	public void testGetAllExercises() {
		StrengthExercise testExercise = new StrengthExercise("Benkpress", "Benk hos meg(harvey)", 1, 1, 100);
		List<Exercise> strengthExercises = new ArrayList<>();
		try {
			strengthExercises = SQLConnector.getAllExercises(1);
		} catch (SQLException e1) {
			e1.printStackTrace();
			// fail();
		}
		StrengthExercise lastExercise = null;
		for(Exercise e : strengthExercises) {
			if(e.getNote().equals("Benk hos meg(harvey)")) {
				lastExercise = (StrengthExercise)e;
			}
		}
		assertTrue(lastExercise != null);
		assertEquals(testExercise.getRepetitions(), lastExercise.getRepetitions());
		assertEquals(testExercise.getSet(), lastExercise.getSet());
		assertEquals(testExercise.getWeight(), lastExercise.getWeight());
		assertEquals(testExercise.getName(), lastExercise.getName());
		assertEquals(testExercise.getNote(), lastExercise.getNote());
		assertTrue(strengthExercises.size() > 0);
	}
	
	@Test
	public void testGetUser() throws SQLException {
		try {
			SQLConnector.getUser(-1);
			fail("Hentet bruker med ugyldig ID");
		} catch (IllegalArgumentException e) {
			// Testen passet
		}
		
		try {
			SQLConnector.getUser(1);
		} catch (IllegalArgumentException e) {
			fail("Fikk ikke hentet bruker med gyldig ID");
		}
	}
	
	@Test
	public void testCreateGetDeleteUser() throws SQLException {
		int clientId = SQLConnector.createUser("Test testesen", "666 66 666", 55, "Fa testene til å kjore", 5);
		try {
			SQLConnector.getUser(clientId);
		} catch (IllegalArgumentException e) {
			fail();
		}
		
		SQLConnector.deleteUser(clientId);
		try {
			SQLConnector.getUser(clientId);
			fail();
		} catch (IllegalArgumentException e) {
			// Testen passet
		}
	}
	
	@Test
	public void testGetTrainers() {
		try {
			if(SQLConnector.getTrainers() == null) fail();
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGetStrengthExercises() throws SQLException {
		try {
			if (SQLConnector.getStrengthExercises("Benkpress", 5) == null) fail();
		} catch (Exception e1) {
			fail();
		}
	}
	
	@Test
	public void testCreateUpdateUser() throws SQLException {
		int clientID = SQLConnector.createUser("Kong Harald", "22225555", 81, "Bli den mest veltrente kongen", 1);
		try {
			SQLConnector.getUser(clientID);
		} catch (IllegalArgumentException e) {
			fail();
		}
		
		SQLConnector.updateUser(clientID, "Kong Harald", "22225555", 81, "Sonja synes jeg er blitt tjukk", 1);
		if (!SQLConnector.getUser(clientID).getMotivation().equals("Sonja synes jeg er blitt tjukk")) {
			fail();
		} 
	}
	@Test
	public void testCreateGetUpdateDeleteExercise() {
		try {
			int index = SQLConnector.getStrengthExercises(1).size();
			int exId = SQLConnector.createStrengthExercise(5, 5, 5, "God okt", 1, 1);
			StrengthExercise exercise = SQLConnector.getStrengthExercises(1).get(index);
			assertTrue(exercise.getNote().equals("God okt"));
			SQLConnector.updateStrengthExercise(5, 5, 5, "Darlig okt", 1, exId);
			exercise = SQLConnector.getStrengthExercises(1).get(index);
			assertTrue(exercise.getNote().equals("Darlig okt"));
			SQLConnector.deleteStrengthExercise(exId);
			try {
				assertTrue(!exercise.equals(SQLConnector.getStrengthExercises(1).get(index)));
			} catch(Exception e) {
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
