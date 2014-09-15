package net.scratchforfun.xioco;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Config {

	public String name;
	public List<String> text = new ArrayList<String>();
	
	public Config(String name){
		this.name = name;
		
		readFile();
	}
	
	public void readFile(){
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(new File(Configs.folder, name+Configs.extension));
			while(scanner.hasNext()){
				text.add(scanner.nextLine());
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public String getString(String value){
		for(String linje : text){
			if(linje.startsWith(value+":")) return linje.split(":")[1];
		}
		
		return null;
	}
	
}
