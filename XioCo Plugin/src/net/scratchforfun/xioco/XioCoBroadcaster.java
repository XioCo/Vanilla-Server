package net.scratchforfun.xioco;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class XioCoBroadcaster implements Runnable{

	private Random random = new Random();
	private static Thread thread;
	private List<String> messages = new ArrayList<String>();
	
	int tidMellomHverMeldingISekunder;
	
	public XioCoBroadcaster(){
		readAutoInfoConfig(Configs.autoinfoFile);
		
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	private void readAutoInfoConfig(File config){
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(config);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Setup the tidMellomHverMeldingISekunder variable!
				if(text.startsWith("tidMellomHverMeldingISekunder")){
					tidMellomHverMeldingISekunder = Integer.parseInt(text.split(":")[1]);
				}else
				//Use # to makes comments in the config!
				if(!text.startsWith("#") && !text.isEmpty()){
					messages.add(text);					
				}
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	//Makes sure to close the thread! Not necessary if /stop, but if /reload it creates another broadcaster without stopping the first!
	public static void closeThread(){
		thread.stop();
		
		// Make sure the thread does not exist
		// Not sure if this works or not
		thread = null;
	}
	
	public void run(){
		long time = System.currentTimeMillis();
		
		while(true){
			if(System.currentTimeMillis() >= time+tidMellomHverMeldingISekunder*1000){
				Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "INFO: " + ChatColor.GOLD + messages.get(random.nextInt(messages.size())));
				time+=tidMellomHverMeldingISekunder*1000;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
