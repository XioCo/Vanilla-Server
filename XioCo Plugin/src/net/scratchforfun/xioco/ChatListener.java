package net.scratchforfun.xioco;

import net.scratchforfun.debugg.Debug;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener{

	public String[] spamChars = new String[]{"!", "?"};
	public String[] badWords;
	public String badWordStar = "*";
	
	public void getBadWords(){
		badWords = Configs.getGenerics(Configs.antispamFile);
	}
	
	@EventHandler
	public void onAsyncChat(AsyncPlayerChatEvent e){
		Debug debug = Debug.start();
		e.setMessage(removeBadWords(e.getMessage()));
		debug.print(ChatColor.BLUE + "AsyncPlayerChatEvent: ");
	}
	
	private String removeBadWords(String text){
		String completeSentence = "";
		
		String[] checkTheseWords = text.split(" ");	
		for(int i = 0; i < checkTheseWords.length; i++){
			for(String badWord : badWords){
				if(checkTheseWords[i].equalsIgnoreCase(badWord)){
					String stars = "";
					for(int j = 0; j < badWord.length(); j++){
						stars+=badWordStar;
					}
					checkTheseWords[i] = stars;
				}				
			}
			
			completeSentence+=checkTheseWords[i];
			if(i < checkTheseWords.length-1) completeSentence+=" ";
		}
		
		/*
		for(String badWord : badWords){
			//(?i) means not case sensitive
			String[] fixedWord = text.split("(?i)"+badWord.toLowerCase());
			
			if(fixedWord.length == 0){
				for(int j = 0; j < badWord.length(); j++){
					completeSentence += badWordStar;
				}	
			}
			
			for(int i = 0; i < fixedWord.length; i++){
				if(i>0){
					for(int j = 0; j < badWord.length(); j++){
						completeSentence += badWordStar;
					}					
				}
				
				completeSentence += fixedWord[i];
			}
			
			if(completeSentence.length() != text.length()){
				for(int j = 0; j < badWord.length(); j++){
					completeSentence += badWordStar;
				}	
			}
		}*/
		
		text = completeSentence;
		completeSentence = "";
		
		String lastChar = "";
		for(int i = 0; i < text.length(); i++){
			String currChar = String.valueOf(text.charAt(i));
			boolean spam = false;
			for(int j = 0; j < spamChars.length; j++){
				if(lastChar.equalsIgnoreCase(spamChars[j]) && currChar.equalsIgnoreCase(spamChars[j])) spam = true;
			}			
			lastChar = currChar;
			
			if(!spam){
				completeSentence+=currChar;	
			}
		}
		
		return completeSentence;
	}
	
}
