package net.scratchforfun.xioco;

public class Question {
	
	public String question = "";
	
	public void addLine(String line){
		if(!this.question.equals("")) this.question += ",";
		this.question += "\"text\":\"" + line + "\"";
	}
	
	public void addClickLine(String line, String command){
		if(!this.question.equals("")) this.question += ",";
		this.question += 
		
		"{\"text\":\"" + line + 
        "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + 
        command + "\"}}";
	}
	
}
