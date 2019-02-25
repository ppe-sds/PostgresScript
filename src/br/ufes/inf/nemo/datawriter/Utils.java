package br.ufes.inf.nemo.datawriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
	private static String firstNameDataset = "CSV_Database_of_First_Names.csv";
	private static int numberFirstNames = 5494;
	private static String lastNameDataset = "CSV_Database_of_Last_Names.csv";
	private static int numberLastNames = 88799;
	
	private static List<String> firstNames = new ArrayList<>();
	private static List<String> lastNames = new ArrayList<>();
	
	private static class DateInfo {
		private int year;
		private int month;
		private int day;
		private int hour;
		private int minute;
		private int second;
		
		// Sorteia um ano entre 1990 e 2018
		private static int raffleYear() {
			return 1989 + raffle(29);
		}
		
		private static int raffleMonth() {
			return raffle(12);
		}
		
		private static int raffleDayOfMonth(int month) {
			int day;
			switch(month) {
				case 2:
					day = raffle(28);
					break;
				case 4:
				case 6:
				case 9:
				case 11:
					day = raffle(30);
					break;
				default:
					day = raffle(31);
			}
			return day;
		}
		
		private static DateInfo raffleDateInfo() {
			DateInfo dateinfo = new DateInfo();
			dateinfo.year = raffleYear();
			dateinfo.month = raffleMonth();
			dateinfo.day = raffleDayOfMonth(dateinfo.month);
			
			dateinfo.hour = raffle(24) - 1;
			dateinfo.minute = raffle(60) - 1;
			dateinfo.second = raffle(60) - 1;
			return dateinfo;
		}
		
		private DateInfo previousDate(int monthsAgo) {
			DateInfo previous = new DateInfo();
			
			int quociente = monthsAgo / 12;
			int resto = monthsAgo % 12;
			
			previous.year = this.year - quociente;
			previous.month = this.month - resto;
			
			if(previous.month < 1) {
				previous.month += 12;
				previous.year -= 1;
			}
			
			previous.day = raffleDayOfMonth(previous.month);
			previous.hour = raffle(24) - 1;
			previous.minute = raffle(60) - 1;
			previous.second = raffle(60) - 1;
			
			return previous;
		}
		
		private DateInfo laterDate(int monthsAhead) {
			DateInfo later = new DateInfo();
			
			int quociente = monthsAhead / 12;
			int resto = monthsAhead % 12;
			
			later.year = this.year + quociente;
			later.month = this.month + resto;
			
			if(later.month > 12) {
				later.month -= 12;
				later.year += 1;
			}
			
			later.day = raffleDayOfMonth(later.month);
			later.hour = raffle(24) - 1;
			later.minute = raffle(60) - 1;
			later.second = raffle(60) - 1;
			
			return later;
		}
		
		private static DateInfo fromString(String datetime) {
			DateInfo dateinfo = new DateInfo();
			
			String dateStr = datetime.replaceAll("'", "");
			String[] infoParts = dateStr.split(" ");
			String datePart = infoParts[0];
			String timePart = infoParts[1];
			
			String[] dateParts = datePart.split("-");
			dateinfo.year = Integer.parseInt(dateParts[0]);
			dateinfo.month = Integer.parseInt(dateParts[1]);
			dateinfo.day = Integer.parseInt(dateParts[2]);
			
			String[] timeParts = timePart.split(":");
			dateinfo.hour = Integer.parseInt(timeParts[0]);
			dateinfo.minute = Integer.parseInt(timeParts[1]);
			dateinfo.second = Integer.parseInt(timeParts[2]);
			
			return dateinfo;
		}
		
		private String show() {
			return String.format("'%04d-%02d-%02d %02d:%02d:%02d'", 
					year, month, day, hour, minute, second);
		}
		
		private String mostrar() {
			return String.format("'%02d/%02d/%04d %02d:%02d:%02d'",
					day, month, year, hour, minute, second);
		}
	}
	
	static {
		try {
			BufferedReader firstNamesReader = new BufferedReader(new FileReader(firstNameDataset));
			for(int i = 0; i < numberFirstNames; i++) {
				firstNames.add(firstNamesReader.readLine());
			}
			firstNamesReader.close();
			
			BufferedReader lastNamesReader = new BufferedReader(new FileReader(lastNameDataset));
			for(int i = 0; i < numberLastNames; i++) {
				lastNames.add(lastNamesReader.readLine());
			}
			lastNamesReader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int raffle(int num) {
		Random random = new Random();
		return random.nextInt(num) + 1;
	}
	
	public static String raffleDigits(int num) {
		String digits = "";
		for(int i = 0; i < num; i++) {
			digits += raffle(10) - 1;
		}
		return "'" + digits + "'";
	}
	
	public static String raffleFirstName() {
		int index = raffle(numberFirstNames) - 1;
		return firstNames.get(index);
	}
	
	public static String raffleLastName() {
		int index = raffle(numberLastNames) - 1;
		return lastNames.get(index);
	}
	
	public static String raffleName() {
		String firstName = raffleFirstName();
		String lastName = raffleLastName();
		return "'" + firstName + " " + lastName + "'";
	}
	
	public static String raffleTipoFeito() {
		String[] letters = {"A", "B", "C", "D", "E"};
		String[] numbers = {"1", "2", "3", "4", "5"};
		
		int letterIndex = raffle(letters.length) - 1;
		int numberIndex = raffle(numbers.length) - 1;
		
		return "'" + letters[letterIndex] + numbers[numberIndex] + "'";
	}
	
	public static String raffleSituacFeito() {
		return "'AT'";
	}
	
	public static String raffleDatetime() {
		DateInfo info = DateInfo.raffleDateInfo();
		return info.show();
	}
	
	public static String previousDatetime(String datetime) {
		DateInfo dateinfo = DateInfo.fromString(datetime);
		System.out.println(">>> Data original: " + dateinfo.mostrar());
		
		int monthsAgo = 2 + raffle(28); // De três a trinta meses atrás
		System.out.println(">>> Sorteando data anterior (cerca de " + monthsAgo + " meses atrás)");
		DateInfo previousDate = dateinfo.previousDate(monthsAgo);
		System.out.println(">>> Data anterior: " + previousDate.mostrar());
		System.lineSeparator();
		
		return previousDate.show();
	}
	
	public static String laterDatetime(String datetime) {
		DateInfo dateinfo = DateInfo.fromString(datetime);
		System.out.println(">>> Data original: " + dateinfo.mostrar());
		
		int monthsAhead = 2 + raffle(28); // De três a trinta meses à frente
		System.out.println(">>> Sorteando data posterior (cerca de " + monthsAhead + " meses à frente)");
		DateInfo laterDate = dateinfo.laterDate(monthsAhead);
		System.out.println(">>> Data posterior: " + laterDate.mostrar());
		System.lineSeparator();
		
		return laterDate.show();
	}
}
