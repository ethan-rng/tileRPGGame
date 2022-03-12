import arc.*;
import java.awt.*;
import java.awt.image.*;

//Special Features
//	HUD with Hearts and Armor
//	Multiple Maps
//	New Carriable Hidden Armor Item
//	Smooth Buffering
//	No Need to Pause

public class grade11ReviewProject3{
	static int intRow;
	static int intCol;
	static int intNewRow;
	static int intNewCol;

	public static void main(String[] args){
		Console con = new Console("Grade 11 Review RPG", 600, 630);
		char chrLetter;
		String strMapFile;
		String strMap [][];
		int intHealth;
		int intEnemy;
		int intArmor;
		
		strMap = new String[20][20];
		intHealth = 50;
		intRow = 0;
		intCol = 0;
		intNewRow = 0;
		intNewCol = 0;
		intEnemy = 6;
		intArmor = 0;

		//Main Menu To Select
		//Loads Menu Image
		BufferedImage imgMenu = con.loadImage("mainMenu.png");
		con.drawImage(imgMenu, 0, 0);
		con.drawString("Press H For Help",200, 570);

		con.repaint();
		
		while(true){
			chrLetter = con.getChar();
			if(chrLetter == '1'){
				con.clear();
				strMapFile = "plains";
				break;
			}else if(chrLetter == '2'){
				con.clear();
				strMapFile = "mountain";
				break;
			}else if(chrLetter == '3'){
				con.clear();
				strMapFile = "beach";
				break;
			}else if(chrLetter == 'h'){	//Help Screen
				con.setDrawColor(Color.BLACK);
				con.fillRect(0, 0, 800, 800);
				con.repaint();
				con.println("Game Instructions");
				con.println("--> Use WASD to move around the map");
				con.println("--> Don't go in the water");
				con.println("--> Kill all the enemies by walking over them. \n Do beware that you lose 10 health each time");
				con.println("--> Walk over buildings to gain 10 health (you \ncan only have 50 health)");
				con.println("--> Walk on armor tile to get armor. Each armor \n will protect you from the enemy once. You can only have three armors at a time");
				con.println("--> Press [b] again to go back");
			}else if(chrLetter == 'b'){
				con.clear();
				con.drawImage(imgMenu, 0, 0);
				con.drawString("Press H For Help",200, 570);
				con.repaint();
			}
		}
		
		con.setDrawColor(Color.BLACK);
		con.fillRect(0, 0, 800, 800);
		
		strMap = loadMapFile(con, strMapFile);
		loadMap(con, strMap);
		
		//HUD
		drawHeart(con, 50);
		
		//Game Loop
		while (true){
			//User Input
			chrLetter = con.currentChar();
			System.out.println(chrLetter);
			String strCurrTile;
			String strNextTile;
		
			strCurrTile = strMap[intRow][intCol];
		
			//New Location Detector
			if(chrLetter == 'w' || chrLetter == 'W'){
				intNewRow = intRow - 1;
			}else if(chrLetter == 'a' || chrLetter == 'A'){
				intNewCol = intCol - 1;
			}else if(chrLetter == 's' || chrLetter == 'S'){
				intNewRow = intRow + 1;
			}else if(chrLetter == 'd' || chrLetter == 'D'){
				intNewCol = intCol + 1;
			}
			
			//Bound Detection, in case map changes
			if(intNewCol <= 20 && intNewCol >= 0 && intNewRow <= 20 && intNewRow >= 0){
				//Diagnostics
				//	printMap(strMap);
				//	System.out.println("Hero | x: " + intCol + " y: " + intRow);
				//	System.out.println("Next | x: " + intNewCol + " y: " + intNewRow);
				
				drawArmor(con, intArmor);
				strNextTile = strMap[intNewRow][intNewCol];
				System.out.println("Next Tile is: " + strNextTile);
					
				if(intHealth == 0 || strNextTile.equals("W")){
					loseScreen(con);
					break;
				}else if(intEnemy == 0){
					winScreen(con);
					break;
				}else if(strNextTile.equals("B1") || strNextTile.equals("B2") || strNextTile.equals("B3")){
					//Run into building                                        
					if(intHealth < 50){
						intHealth += 10; 
						drawHeart(con, intHealth);
						strMap = walk(strMapFile, strMap);
						loadMap(con, strMap);
					}
				}else if(strNextTile.equals("E1") || strNextTile.equals("E2") || strNextTile.equals("E3")){
					//Run into enemy
					if(intArmor > 0){
						intArmor -= 1;
						drawArmor(con, intArmor);
					}else{
						intHealth -= 10;
						drawHeart(con, intHealth);
					}
					
					if(intHealth <= 0){ //Second Lose Condition
						loseScreen(con);
						break;
					}else{
						strMap = walk(strMapFile, strMap);
						intEnemy -= 1;
						loadMap(con, strMap);
					}
				}else if(strNextTile.equals("G") || strNextTile.equals("R") || strNextTile.equals("Sa")){
					//Run Into Grass, Rock, Sand
					strMap = walk(strMapFile, strMap);
					loadMap(con, strMap);
				}else if(strNextTile.equals("A")){
					if(intArmor < 3){
						strMap = walk(strMapFile, strMap);
						intArmor += 1;
						drawArmor(con, intArmor);
						loadMap(con, strMap);
					}
				}
				con.sleep(15);
			}
		}
	}
	public static String[][] loadMapFile(Console con, String strMapFile){
		TextInputFile txtMapFile = new TextInputFile(strMapFile + ".csv");
		String strMap [][] = new String[20][20];
		
		for(int intCount0 = 0; intCount0 < 20; intCount0++){
			String strMapLine[] = new String[20];
			strMapLine = txtMapFile.readLine().split(",");
			
			for(int intCount1 = 0; intCount1 < 20; intCount1++){
				strMap[intCount0][intCount1] = strMapLine[intCount1];
			}
		}		
		txtMapFile.close();
		return strMap;
	}
	public static void loadMap(Console con, String[][] strMap){
		//W = Water
		//T = Tree
		//G = Grass
		//H1 = Hero (world 1)
		//E1 = Enemy (world 1)
		//B1 = Building (world 1)
		
		//W = Water
		//P = Palm Tree
		//Sa = Sand
		//H2 = Hero (world 2)
		//E2 = Enemy (world 2)
		//B2 = Building (world 2)
		
		//W = Water
		//S = Spuce Tree
		//R = Rock
		//H3 = Hero (world 3)
		//E3 = Enemy (world 3)
		//B3 = Building (world 3)
		
		for(int intCount0 = 0; intCount0 < 20; intCount0++){
			for(int intCount1 = 0; intCount1 < 20; intCount1++){
				String strTile = strMap[intCount0][intCount1];
				BufferedImage imgTile;
				imgTile = con.loadImage("water.png");
				
				if(strTile.equals("T")){
					imgTile = con.loadImage("tree.png");
				}else if(strTile.equals("G")){
					imgTile = con.loadImage("grass.png");
				}else if(strTile.equals("H1")){
					intRow = intCount0;
					intCol = intCount1;
					intNewRow = intRow;
					intNewCol = intCol;
					imgTile = con.loadImage("hero.png");
				}else if(strTile.equals("E1")){
					imgTile = con.loadImage("enemy.png");
				}else if(strTile.equals("B1")){
					imgTile = con.loadImage("building.png");
				}else if(strTile.equals("P")){
					imgTile = con.loadImage("palmTree.png");
				}else if(strTile.equals("Sa")){
					imgTile = con.loadImage("sand.png");
				}else if(strTile.equals("H2")){
					intRow = intCount0;
					intCol = intCount1;
					intNewRow = intRow;
					intNewCol = intCol;
					imgTile = con.loadImage("hero2.png");
				}else if(strTile.equals("E2")){	
					imgTile = con.loadImage("enemy2.png");
				}else if(strTile.equals("B2")){
					imgTile = con.loadImage("building2.png");
				}else if(strTile.equals("S")){
					imgTile = con.loadImage("spruceTree.png");
				}else if(strTile.equals("R")){
					imgTile = con.loadImage("rock.png");
				}else if(strTile.equals("H3")){
					intRow = intCount0;
					intCol = intCount1;
					intNewRow = intRow;
					intNewCol = intCol;
					imgTile = con.loadImage("hero3.png");
				}else if(strTile.equals("E3")){
					imgTile = con.loadImage("enemy3.png");
				}else if(strTile.equals("B3")){
					imgTile = con.loadImage("building3.png");
				}else if(strTile.equals("A")){
					imgTile = con.loadImage("armorTile.png");
				}

				con.drawImage(imgTile, intCount1*30, intCount0*30+30);
				con.repaint();
			}
		}
	}
	public static String[][] walk(String strMapFile, String[][] strMap){
		//update array
		String strTile;
		String strHero;
		
		if(strMapFile.equals("plains")){
			strTile = "G";
			strHero = "H1";
		}else if(strMapFile.equals("beach")){
			strTile = "Sa";
			strHero = "H2";
		}else{
			strTile = "R";
			strHero = "H3";
		}
		strMap[intNewRow][intNewCol] = strHero;
		strMap[intRow][intCol] = strTile;
		return strMap;
	}
	public static void printMap(String[][] strMap){
		for(int i = 0; i < 20; i++){
			for(int j = 0; j < 20; j++){
				System.out.print(strMap[i][j] + " ");
			}
			System.out.println();
		}
	}
	public static void winScreen(Console con){
		BufferedImage imgWinner;
		imgWinner = con.loadImage("winner.png");
		
		con.drawImage(imgWinner, 0, 0);
		con.repaint();
	}
	public static void loseScreen(Console con){
		BufferedImage imgLoser;
		imgLoser = con.loadImage("loser.png");
		
		con.drawImage(imgLoser, 0, 0);
		con.repaint();
	}
	public static void drawHeart(Console con, int intHealth){
		con.setDrawColor(Color.BLACK);
		con.fillRect(230, 0, 380, 30);
		
		BufferedImage imgHeart;
		BufferedImage imgHeroHealth;
		BufferedImage imgX10;
	
		imgHeart = con.loadImage("heart.png");
		imgHeroHealth = con.loadImage("heroHealth.png");
		imgX10 = con.loadImage("x10.png");
		
		con.drawImage(imgHeroHealth, 0, 0);
		for(int intCount = 0; intCount < intHealth/10; intCount++){
			con.drawImage(imgHeart, intCount*30+230, 0);
		}
		con.drawImage(imgX10, 380, 0);
		con.repaint();
	}
	public static void drawArmor(Console con, int intArmor){
		con.setDrawColor(Color.BLACK);
		con.fillRect(510, 0, 90, 30);
		
		BufferedImage imgArmor;
		
		imgArmor = con.loadImage("armor.png");
		for(int intCount = 0; intCount < intArmor; intCount++){
			con.drawImage(imgArmor, intCount*30+510, 0);
		}
		con.repaint();
	}
}
