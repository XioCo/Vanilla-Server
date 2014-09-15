package net.scratchforfun.xioco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.scratchforfun.xioco.clock.Clock;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AutoRestart /*implements Runnable*/ {

	/*Thread thread = new Thread(this);
	XioCo xioco;
	
	int maxBackups;
	String backupLocation;
	
	public AutoRestart(XioCo xioco){
		this.xioco = xioco;
		
		this.maxBackups = 3;
		this.backupLocation = "backups";
		
		if(!thread.isAlive()){
			thread.start();
			xioco.getServer().broadcastMessage(ChatColor.RED + "AutoRestart successfully started!");
		}else{
			xioco.getServer().broadcastMessage(ChatColor.RED + "AutoRestart already running!");			
		}
	}
	
	//Makes sure to close the thread! Not necessary if /stop, but if /reload it creates another broadcaster without stopping the first!
	public void closeThread(){
		thread.stop();
	}

	public void run() {
		int pos = 0;
		while(true){
			Clock clock = Clock.getClock();
			
			int hour = Integer.parseInt(clock.hour);
			int minute = Integer.parseInt(clock.minute);
			int second = Integer.parseInt(clock.second);
			
			if(hour == 2 && minute == 45 && pos == 0){
				this.xioco.getServer().broadcastMessage(ChatColor.RED + "Serveren restartes om 15 minutt.");
				pos++;
			}
			
			if(hour == 2 && minute == 50 && pos == 1){
				this.xioco.getServer().broadcastMessage(ChatColor.RED + "Serveren restartes om 10 minutt.");
				pos++;
			}
			
			if(hour == 2 && minute == 55 && pos == 2){
				this.xioco.getServer().broadcastMessage(ChatColor.RED + "Serveren restartes om 5 minutt.");
				pos++;
			}
			
			if(hour == 2 && minute == 57 && pos == 3){
				this.xioco.getServer().broadcastMessage(ChatColor.RED + "Serveren restartes om 3 minutt.");
				pos++;
			}
			
			if(hour == 2 && minute == 59 && pos == 4){
				this.xioco.getServer().broadcastMessage(ChatColor.RED + "Serveren restartes om 1 minutt.");
				pos++;
			}
			
			if(hour == 2 && minute == 59 && second >= 30 && pos == 5){
				this.xioco.getServer().broadcastMessage(ChatColor.RED + "Serveren restartes om 30 sekunder.");
				pos++;
			}
			
			if(hour == 3 && minute == 0 && pos == 6){
				this.xioco.getServer().broadcastMessage(ChatColor.RED + "Serveren restartes.");
				backup("world");
				//kickPlayers();
				//restart();
				pos++;
			}
			
			try {
				Thread.sleep(999);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void kickPlayers(){
		for(Player player : XioCo.instance.getServer().getOnlinePlayers()){
			player.kickPlayer("Serveren restartes :) Oppe om 2 sekunder!");
		}
	}
	
	private void restart(){
		XioCo.instance.getServer().dispatchCommand(XioCo.instance.getServer().getConsoleSender(), "save-all");
		XioCo.instance.getServer().dispatchCommand(XioCo.instance.getServer().getConsoleSender(), "stop");	
	}
	
	private void backup(String worldName){
		XioCo.instance.getServer().broadcastMessage(ChatColor.GRAY + "Creating Backup!");
		
		File file = new File(backupLocation);
		if(!file.exists()){
			file.mkdir();
		}
		
		try {
			//To be deleted
			File backup3 = new File(backupLocation+"/"+worldName+"-3.zip");
			if(backup3.exists()) backup3.delete();
			
			//To be renamed backup-3
			File backup2 = new File(backupLocation+"/"+worldName+"-2.zip");
			if(backup2.exists()) backup2.renameTo(new File(backupLocation+"/"+worldName+"-3.zip"));
			
			//To be renamed backup-2
			File backup1 = new File(backupLocation+"/"+worldName+"-1.zip");
			if(backup1.exists()) backup1.renameTo(new File(backupLocation+"/"+worldName+"-2.zip"));
			
			//Creates backup-1
			zipDir(worldName);
		} catch (Exception e) {
			XioCo.instance.getServer().broadcastMessage(ChatColor.GRAY + "Error While Backuping!");
			e.printStackTrace();
		}finally{
			XioCo.instance.getServer().broadcastMessage(ChatColor.GRAY + "Backup Successfull!");
		}
	}
	
	public void zipDir(String dirName) throws IOException {
        ZipOutputStream zip = null;
        FileOutputStream fW = null;
        fW = new FileOutputStream(backupLocation+"/"+dirName+"-1.zip");
        zip = new ZipOutputStream(fW);
        addFolderToZip("", dirName+"/", zip, true);
        zip.close();
        fW.close();
    }

    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, boolean mainFolder) throws IOException {
        File folder = new File(srcFolder);
        if (folder.list().length == 0){
            addFileToZip(path, srcFolder, zip, true);
        }else{
            for (String fileName : folder.list()){
        		if (path.equals("")){
                	addFileToZip(mainFolder?path:folder.getName(), srcFolder + "/" + fileName, zip, false);
                }else{
                    addFileToZip(mainFolder?path:(path+"/"+folder.getName()), srcFolder + "/" + fileName, zip, false);
                }
            }
        }
    }

    private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws IOException {
        File folder = new File(srcFile);
        if(flag){
            zip.putNextEntry(new ZipEntry(path + "/" +folder.getName() + "/"));
        }else{
            if (folder.isDirectory()){
                addFolderToZip(path, srcFile, zip, false);
            }else{
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0){
                    zip.write(buf, 0, len);
                }
            }
        }
    }*/
}
