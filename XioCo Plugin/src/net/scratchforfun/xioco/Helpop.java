package net.scratchforfun.xioco;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Helpop {

	//No extension because we are bad ass people! 
	private static String filename = "helpop";
	private static int amountOfLines = 10;
	
	public static String helpopSound = "ANVIL_SOUND";
	public static int volume = 1;
	public static int pitch = 0;
	
	public static void configHelpop(){
		Helpop.helpopSound = Configs.getValue(Configs.helpopFile, "adminSound");
		Helpop.volume = Integer.parseInt(Configs.getValue(Configs.helpopFile, "volume"));
		Helpop.pitch = Integer.parseInt(Configs.getValue(Configs.helpopFile, "pitch"));
	}
	
	public static String[] getHelplist(){
		try{
			//Nopp, no extension
			File helpopFile = new File(filename);
			
			//If the helpopfile does not exist create it, and make it ready to use
			if(!helpopFile.exists())helpopFile.createNewFile();
			
			//Grabs the text from the file, lazy done --> It's only 10 sentences! 
			int i = amountOfLines-1;
			String[] text = new String[amountOfLines];
			Scanner scanner = new Scanner(helpopFile);
			while(scanner.hasNext()){
				//Grab the hole line, and not only the next word!
				text[i] = scanner.nextLine();
				i--;				
				
				//Since i-- is after the scanner, you are allowed to end up with a negative
				if(i<0)break;
			}
			//Closes of the scanner
			scanner.close();
			
			return text;
		}catch (IOException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void writeHelpopToFile(String helpop){
		try {
			//Nopp, no extension
			File helpopFile = new File(filename);
			
			//If the helpopfile does not exist create it, and make it ready to use
			if(!helpopFile.exists())helpopFile.createNewFile();
			
			//Grabs the text from the file, lazy done --> It's only 10 sentences! 
			int i = 0;
			String[] text = new String[amountOfLines];
			Scanner scanner = new Scanner(helpopFile);
			while(scanner.hasNext()){
				i++;
				
				//By doing the i++ before this we make the [0] position available to the newest helpop(argument)
				//Grab the hole line, and not only the next word!
				text[i] = scanner.nextLine();
				
				//If not you'll get a arrayoutofboundsexception <-- BAD lol xD
				//Only run up to 9, because we are adding our own line making it 10!
				if(i==amountOfLines-1)break;
			}
			//Closes of the scanner
			scanner.close();
			
			//Set the first line to the new message
			text[0] = helpop;
			
			//Creates the writer!
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for(String line : text){
				if(line != null) writer.println(line);
			}
			//Closes the writer!
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
