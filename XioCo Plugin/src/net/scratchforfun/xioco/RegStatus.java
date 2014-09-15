package net.scratchforfun.xioco;

import java.util.Random;

public class RegStatus {

	Random random = new Random();
	
	String UUID;
	
	int question;
	int correct;
	
	public RegStatus(String UUID){
		this.UUID = UUID;
		
		question = 1;
		correct = 0;
	}

	public void setRandomQuestion() {
		String[] questions = Configs.getValues(Configs.greylistQuestionsFile);
		
		question = Integer.parseInt(questions[random.nextInt(questions.length)].split(":")[0]);
	}
	
}
