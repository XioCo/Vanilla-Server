package net.scratchforfun.xioco;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Configs {

	public static String extension = ".XioCo";
	public static File folder;
	
	public static File blockProtectionBlacklistFile;
	public static File sqlFile;
	public static File autoinfoFile;
	public static File playerInfoFile;
	public static File chestShopFile;
	public static File helpopFile;
	public static File antispamFile;
	public static File greylistQuestionsFile;
	public static Config serverFile;
	
	public static void initializeFiles(File folder){
		Configs.folder = folder;
		
		blockProtectionBlacklistFile = new File(folder, "BlockProtection"+extension);
		autoinfoFile = new File(folder, "AutoInfo"+extension);
		sqlFile = new File(folder, "SQL"+extension);
		playerInfoFile = new File(folder, "PlayerInfo"+extension);
		helpopFile = new File(folder, "Helpop"+extension);
		antispamFile = new File(folder, "AntiSpam"+extension);
		greylistQuestionsFile = new File(folder, "Graylist"+extension);
		chestShopFile = new File(folder, "ChestShop"+extension);
		serverFile = new Config("Server");
		
		try{
			updateFiles();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void updateFiles() throws Exception{
		//If the file does not exist, create it!
		if(!blockProtectionBlacklistFile.exists()){
			createDefault(blockProtectionBlacklistFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før! <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme :) Bare husk å ikke sett inn mellomrom der :)\n\n\n# Trenger du navnet på noen blokker er det bare å ta kontakt!\n\ntableName:xioco_blockprotection\n\nBEDROCK");
		}

		//If the file does not exist, create it!
		if(!autoinfoFile.exists()){
			createDefault(autoinfoFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før!  <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme(da regnes de ikke) :) Bare husk å ikke sett inn mellomrom der :)\n\n# I framtiden må jeg legge til noe som sier hvem den meldingen skal sendes til :) f.eks. Sende til alle som ikke er VIP om at de kan kjøpe VIP, de som allerede er det får ikke meldingen!\n\n# 300 sekunder er 5 minutter\ntidMellomHverMeldingISekunder:300\n# Alle meldinger skrivers vanlig uten '#' så lenge de ikke starter med 'tidMellomHverMeldingISekunder' eller '#' skal det gå bra :)\n\nDette er en melding til alle på serveren! ScratchForFun er kul! (Melding 1)\nScratchForFun er best! (Melding 2)");
		}
		
		//If the file does not exist, create it!
		if(!sqlFile.exists()){
			createDefault(sqlFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før! <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme :) Bare husk å ikke sett inn mellomrom der :)\n\nip:localhost\ndatabase:XioCo\nport:3306\n\nusername:root\npassword:");
		}
		
		//If the file does not exist, create it!
		if(!playerInfoFile.exists()){
			createDefault(playerInfoFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før! <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme :) Bare husk å ikke sett inn mellomrom der :)\n\ntableName:xioco_player");
		}
		
		//If the file does not exist, create it!
		if(!helpopFile.exists()){
			createDefault(helpopFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før! <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme :) Bare husk å ikke sett inn mellomrom der :)\n\nadminSound:ANVIL_LAND\nvolume:1\npitch:0");
		}
		
		//If the file does not exist, create it!
		if(!antispamFile.exists()){
			createDefault(antispamFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før! <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme :) Bare husk å ikke sett inn mellomrom der :)\n\n\n# Ikke bry deg om store og små bokstaver :)\n# Skriv inn stygge ord under\n\nFAEN");
		}
		
		//If the file does not exist, create it!
		if(!greylistQuestionsFile.exists()){
			createDefault(greylistQuestionsFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før! <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme :) Bare husk å ikke sett inn mellomrom der :)\n\n\n# Ikke bry deg om store og små bokstaver :)\n# Skriv spørsmåls nummer '1' ':' spørsmål --> 'YOLO?' ':' 'ja'/'nei'\n\n1:Er ScratchForFun den kuleste personen i verden:ja\n2:Er ScratchForFun den kuleste personen i verden:ja\n3:Er ScratchForFun den kuleste personen i verden:ja\n4:Er ScratchForFun den kuleste personen i verden:ja\n5:Er ScratchForFun den kuleste personen i verden:ja\n6:Er ScratchForFun den kuleste personen i verden:ja\n7:Er ScratchForFun den kuleste personen i verden:ja\n8:Er ScratchForFun den kuleste personen i verden:ja\n9:Er ScratchForFun den kuleste personen i verden:ja\n10:Er ScratchForFun den kuleste personen i verden:ja");
		}
		
		//If the file does not exist, create it!
		if(!chestShopFile.exists()){
			createDefault(chestShopFile, "# Bruk '#' for å lage kommentarer!\n# Dette '#' må være det første i setningen! Ikke ha mellomrom før! <-- (Jeg er for lat til å legge det til) :)\n# Felter kan også stå tomme :) Bare husk å ikke sett inn mellomrom der :)\n\n#Passord kan ikke inneholde ':' og ikke være for langt (må ha plass på sign)\n\nadminShop:a+`/y");
		}
		
		
	}
	
	public static void removeLine(File file, String line){
		try{
			PrintWriter writer = new PrintWriter(file);
			//Writes the text into the file
			String[] lines = readFile(file);
			for(int i = 0; i < lines.length; i++){
				if(!lines[i].equalsIgnoreCase(line))writer.println(lines[i]);
			}
			//Closes the writer!
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(File file, String line){
		try{
			PrintWriter writer = new PrintWriter(file);
			//Writes the text into the file
			String[] lines = readFile(file);
			for(int i = 0; i < lines.length; i++){
				writer.println(lines[i]);
			}
			writer.println(line);
			//Closes the writer!
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String[] readFile(File file){
		List<String> lines = new ArrayList<String>();
		
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				lines.add(scanner.nextLine());
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		String[] text = new String[lines.size()];
		for(int i = 0; i < text.length; i++){
			text[i] = lines.get(i);
		}
		
		return text;
	}
	
	public static String getValue(File file, String value){
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Setup the tidMellomHverMeldingISekunder variable!
				if(!text.equals("") && text.startsWith(value + ":")){
					String[] string = text.split(":");
					if(string.length > 1) return string[1];
					else return "";
				}
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String getOppositeValue(File file, String value){
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Setup the tidMellomHverMeldingISekunder variable!
				if(!text.equals("") && text.endsWith(":" + value)){
					String[] string = text.split(":");
					if(string.length > 1) return string[0];
					else return "";
				}
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String[] getValues(File file, String value){
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Setup the tidMellomHverMeldingISekunder variable!
				if(!text.equals("") && text.startsWith(value+":")){
					String[] string = text.split(":");
					if(string.length > 1) return string;
					else return null;
				}
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String[] getValues(File file){
		try{
			List<String> values = new ArrayList<String>();
			
			//Grabs the text from the file
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Setup the tidMellomHverMeldingISekunder variable!
				if(!text.equals("") && !text.startsWith("#") && text.contains(":")){
					values.add(text);
				}
			}
			//Closes of the scanner
			scanner.close();
			
			String[] array = new String[values.size()];
			for(int i = 0; i < array.length; i++){
				array[i] = values.get(i);
			}
			
			return array;
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String[] getGenerics(File file){
		List<String> generics = new ArrayList<String>();
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Makes sure that this sentence is not a comment or a variable!
				if(!text.startsWith("#") && !text.contains(":") && !text.equals("")){
					generics.add(text);
				}
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		String[] array = new String[generics.size()];
		for(int i = 0; i < array.length; i++){
			array[i] = generics.get(i);
		}
		
		return array;
	}
	
	//If the file does not exist, it creates a template for easy use!
	private static void createDefault(File file, String text){
		try{
			file.createNewFile();
			//Creates the writer!
			PrintWriter writer = new PrintWriter(file);
			//Writes the text into the file
			writer.println(text);
			//Closes the writer!
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
