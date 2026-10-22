package ense375.personalpasswordmanager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Predicate;

public class CLIUtils {
	
	public static String promptUser(String prompt, List<String> valid_options) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String user_response = "";
		do {
			System.out.print(String.format("%s (%s): ", prompt, valid_options.stream().reduce((accum, curr) -> accum + ", " + curr).get()));
			try {
				user_response = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!valid_options.contains(user_response));
		
		return user_response;
	}
	
	public static String promptUser(String prompt, Predicate<String> meetsConditions) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String user_response = "";
		do {
			System.out.print(String.format("%s: ", prompt));
			try {
				user_response = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!meetsConditions.test(user_response));
		
		return user_response;
	}

}
