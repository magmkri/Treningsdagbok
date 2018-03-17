package tdt4140.gr1837.app.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HTTPServer {
	
	static HttpServer server;
	
	 public static void initialize() throws Exception {
	        server = HttpServer.create(new InetSocketAddress(8000), 0);
	        server.createContext("/session", new SessionHandler());
	        server.createContext("/clients", new ClientHandler());
	        server.createContext("/exercise", new ExerciseHandler());
	        server.setExecutor(null); // creates a default executor
	        server.start();
	 }
	 
	 public static void tearDown() {
		 server.stop(0);
	 }
	 
	 public static Map<String, String> getParams(HttpExchange ex) {
		 Map<String, String> params = new HashMap<>();
		 try {
			 String bodyString = IOUtils.toString(ex.getRequestBody());
			 for(String s:bodyString.split("&")) {
				 String[] tokens = s.split("=");
				 params.put(tokens[0], tokens[1]);
			 }
			 return params;
		 }catch(IOException e) {
			 return new HashMap<String, String>();
		 }
	 }
	 
	 public static void sendResponse(HttpExchange ex, String response, int statusCode) {
         try {
        	 ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
			 ex.sendResponseHeaders(statusCode, response.length());
	         OutputStream os = ex.getResponseBody();
	         os.write(response.getBytes());
	         os.close();
         } catch(IOException e) {
        	 return;
         }
	 }
	 public static void sendResponse(HttpExchange ex, String response) {
		 sendResponse(ex, response, 200);
	 }
	 
	 private static String changeNorwegianLetters(String response) {
	 	return response.replaceAll("\u00e6", "ae")
	 				   .replaceAll("\u00f8", "o")
	 				   .replaceAll("\u00e5", "aa")
	 				   .replaceAll("\u00c6", "AE")
	 				   .replaceAll("\u00d8", "O")
	 				   .replaceAll("\u00c5", "AA");	
	 }
	 

    static class ClientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) {
        	if(ex.getRequestMethod().equals("GET")) {
        	    get(ex);
        	} else if(ex.getRequestMethod().equals("POST")) {
        		post(ex);
        	} else if(ex.getRequestMethod().equals("UPDATE")) {
        		update(ex);
        	}
        }
        
        private void post(HttpExchange ex) {
        	// Faar en eksisterene klient. Kalles ved POST /client
        	Map<String, String> params = getParams(ex);
        	try {
				int clientID = SQLConnector.createUser(params.get("name"), Integer.parseInt(params.get("age")), params.get("motivation"), Integer.parseInt(params.get("trainer_id")));
				sendResponse(ex, Integer.toString(clientID), 201);
			} catch (NumberFormatException e) {
				sendResponse(ex, "Ugyldig klient-id", 400);
			} catch (SQLException e) {
				sendResponse(ex, "Kunne ikke koble til databasen", 503);
			}
        }
        
        private void get(HttpExchange ex) {
        	// Lager en ny client. Kalles om man faar en http request GET /client/id
        }
        
        private void update(HttpExchange ex) {
        	// Oppdaterer en klient. Kalles ved UPDATE /client/id
        }
    }
    static class SessionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) {
        	if(ex.getRequestMethod().equals("GET")) {
        	    get(ex);
        	} else if(ex.getRequestMethod().equals("POST")) {
        		post(ex);
        	}
        }
        
        private void post(HttpExchange ex) {
        	// Lager en ny session. Kalles om man faar en http request POST /session
        	Map<String, String> params = getParams(ex);
        	try {
				int sessionID = SQLConnector.createSession(Integer.parseInt(params.get("clientID")), params.get("date"), params.get("note"));
				sendResponse(ex, Integer.toString(sessionID), 201);
			} catch (NumberFormatException e) {
				sendResponse(ex, "Ugyldig klient-id", 400);
			} catch (SQLException e) {
				sendResponse(ex, "Kunne ikke koble til databasen", 503);
			}
        }
        
        private void get(HttpExchange ex) {
        	// Faar en eksisterene klient. Kalles ved GET /exercise/session_id
        	String sessionId = ex.getRequestURI().toString().split("/")[2];
        	try {
				Session session = SQLConnector.getSession(Integer.parseInt(sessionId));
				Gson gson = new Gson();
				String response = gson.toJson(session); // gson.tojson() converts your pojo to json
				response = changeNorwegianLetters(response);
				sendResponse(ex, response);
			} catch (NumberFormatException e) {
				sendResponse(ex, "Ugyldig format paa okt-id", 400);
			} catch (IllegalArgumentException e) {
				sendResponse(ex, e.getMessage(), 404);
				e.printStackTrace();
			} catch (SQLException e) {
				sendResponse(ex, "Kunne ikke koble til databasen", 503);
			}
        }
        
        
        private void update(HttpExchange ex) {
        	// Oppdaterer en klient. Kalles ved UPDATE /client/id
        }
    }
    
    static class ExerciseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) {
        	if(ex.getRequestMethod().equals("GET")) {
        	    get(ex);
        	} else if(ex.getRequestMethod().equals("POST")) {
        		post(ex);
        	} else if(ex.getRequestMethod().equals("UPDATE")) {
        		update(ex);
        	}
        }
        
        private void get(HttpExchange ex) {
        	// Faar en eksisterene ovelse. Kalles ved GET /exercise/session_id
        	String sessionId = ex.getRequestURI().toString().split("/")[2];
        	try {
				List<Exercise> exercises = SQLConnector.getAllExercises(Integer.parseInt(sessionId));
				Gson gson = new Gson();
				String response = gson.toJson(exercises);
				response = changeNorwegianLetters(response);
				sendResponse(ex, response);
			} catch (NumberFormatException e) {
				sendResponse(ex, "Ugyldig format paa okt-id", 400);
			} catch (IllegalArgumentException e) {
				sendResponse(ex, e.getMessage(), 404);
				e.printStackTrace();
			} catch (SQLException e) {
				sendResponse(ex, "Kunne ikke koble til databasen", 503);
			}
        	
        }
        
        private void post(HttpExchange ex) {
        	// Lager en ny ovelse. Kalles om man faar en http request POST /exercise/session_id
        	Map<String, String> params = getParams(ex);
        	try {
				SQLConnector.createExercise(Integer.parseInt(params.get("sessionId")),params.get("reps"), params.get("sett"), params.get("weight"), params.get("note"));
				sendResponse(ex, Integer.toString(201));
			} catch (NumberFormatException e) {
				sendResponse(ex, "Ugyldig klient-id", 400);
			} catch (SQLException e) {
				sendResponse(ex, "Kunne ikke koble til databasen", 503);
			}
        }
        
        private void update(HttpExchange ex) {
        	// Oppdaterer en ovelse. Kalles ved UPDATE /exercise/id
        	Map<String, String> params = getParams(ex);
        	String exerciseId = ex.getRequestURI().toString().split("/")[2];
        	try {
				SQLConnector.updateExercise(Integer.parseInt(exerciseId), params.get("reps"), params.get("sett"), params.get("weight"), params.get("note"));
				sendResponse(ex, exerciseId, 200);
			} catch (NumberFormatException e) {
				sendResponse(ex, "Ugyldig exercise-id", 400);
			} catch (SQLException e) {
				sendResponse(ex, "Kunne ikke koble til databasen", 503);
			}
        }
    }
}
