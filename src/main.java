import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.security.Policy.Parameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.spec.PSource;
import javax.swing.JFrame;

public class main {
	
/*
 * Tu si definujem vsetky potrebne polia. V LaterUse si odkladam pozicie v pravidlach, ktore sa vykonavaju ako posledne.
 * SuppMem je pomocna pamat, do ktorej si odkladam novovytvorene stavy pred filtraciou. Rules je pamat pravidiel.
 * V spravy sa odkladaju vsetky vypisy na konzolu -- "spravy"
 */
	static boolean Corr;
	static ArrayList<Integer> LaterUse = new ArrayList<Integer>();
	static ArrayList<String> SuppMem = new ArrayList<String>();
	static ArrayList<Rules> Rules = new ArrayList<Rules>();
	static ArrayList<String> PossibleSol = new ArrayList<String>();
	static ArrayList<String> Spravy = new ArrayList<String>();
	
/*
 * Funkcia rekurzivne kontroluje pravidlo postupne doprava. Vzdy dosadi vsetky mozne fakty do pravidla, a ak nejake pravidlo plati,
 * tak sa predpoklada je moze byt pravdive a posunie sa dalej v rekurzii. Vyuzivam tu predpoklad, ze ked predchadzajuca cast pravidla platila a
 * a aj toto pravidlo je splnene, tak to plati.
 * Taktiez tu kontrolujem ci sa dane pravidlo nevyhodnocuje ako posledne "<>". Ak ano, tak sa jeho pozicia odlozi do pola LaterUse.
 */
	
	public static void checkRec(int position, String NumOfString[][], ArrayList<String> Facts, String HelpStr[],HashMap<String, String> Parameters) {
		HashMap<String, String> HParameters = new HashMap<String, String>();
		if(position==NumOfString.length) {							//kontrolujem koniec pravidla
			String Helper ="";
			for (String key : Parameters.keySet()) {
			    Helper+=key+"-"+Parameters.get(key)+" ";
			}
			if(Helper!="") {										
				PossibleSol.add(Helper);
			}else {
				Corr = true;
			}
			return;
		}
		if(NumOfString[position][0].equals("<>")) {					//ak je to pravidlo, ktore sa vyhodnocuje ako posledne
			checkRec(position+1, NumOfString, Facts, HelpStr, Parameters);
		}
			for(int k=0;k<Facts.size();k++) {						//skusam kazdy fakt
				String FactsCurrHelp[] = Facts.get(k).split(" ");
				if(FactsCurrHelp.length==NumOfString[position].length) {
					HParameters = (HashMap) Parameters.clone();
					boolean possibleFit = true;
					for(int g=0;g<NumOfString[position].length;g++) {
						if(NumOfString[position][g].charAt(0)=='?') {
							if(HParameters.containsKey(NumOfString[position][g])) {
								if(HParameters.get(NumOfString[position][g]).equals(FactsCurrHelp[g])) {
									
								}else {
									possibleFit = false;
									break;
								}
							}else {
								HParameters.put(NumOfString[position][g], FactsCurrHelp[g]);
							}
						}else {
							if(NumOfString[position][g].equals(FactsCurrHelp[g])) {
								
							}else {
								possibleFit = false;
								break;
							}
						}
					}if(possibleFit==true) {					  //ak fakty sedia

						if((position+1)==NumOfString.length) {
							String Helper ="";
							for (String key : HParameters.keySet()) {
							    Helper+=key+"-"+HParameters.get(key)+" ";
							}
							if(Helper!="") {
								PossibleSol.add(Helper);
							}else {
								Corr = true;
							}
						}else {
							checkRec(position+1,NumOfString,Facts,HelpStr, HParameters);
						}
					}
			}
		}
	}

/*
 * 	Funkcia filtruje pomocnu pamat. Ak je akcia neplatna tak je vymaze. Neplatna je ak :
 *  Chcem pridat fakt, ktory sa tam uz nachadza a neobsahuje ziadny iny validny krok,
 *  Chcem odstranit fakt, ktory sa tam nenachadza a neobsahuje ziadny iny validny krok,
 *  Chcem len vypisat spravu a neobsahuje ziadny iny validny krok.
 */
	
	public static void FilterMemory(ArrayList<String> facts) {
		ArrayList<String> SuppMemH = new ArrayList<String>();
		SuppMemH = (ArrayList<String>) SuppMem.clone();
		for(int i=0;i<SuppMem.size();i++) {
			String SplitMem[]=SuppMem.get(i).split(",");					
			boolean removeEl = true;
			for(int j=0;j<SplitMem.length;j++) {
				String RuleSplit[] = SplitMem[j].split(" ",2);
				if(RuleSplit[0].equals("pridaj")) {
					if(!facts.contains(RuleSplit[1])) {
						removeEl = false;
						break;
					}
				}else if(RuleSplit[0].equals("vymaz")) {
					if(facts.contains(RuleSplit[1])) {
						removeEl = false;
						break;
					}
				}
			}
			if(removeEl==true) {												//ak je potrebne vymazat
				SuppMemH.remove(SuppMem.get(i));
			}
		}
		SuppMem = (ArrayList<String>) SuppMemH.clone();
	}

/*
 * V tejto funkcii vykonam akciu. Moj algoritmus vykonava vzdy prvu akciu a zvysne zahodi. Akciu najskor rozdeli na dve casti -- {akcia, parametre}
 * Najskor sa pozrie, ci dokaze danu akciu vykonat (nebudem pridavat duplikat, odstranovat neexistujuci prvok) a akciu vykona. Pozna 3 typy akcii
 * pridaj -- prida fakt do pamate faktov
 * vymaz -- vymaze fakt z pamate faktov
 * sprava -- vypise spravu na konzolu 
 */
	public static ArrayList<String> executeFirst(ArrayList<String> Facts){
		ArrayList<String> newFacts =  new ArrayList<String>();
		newFacts = (ArrayList<String>) Facts.clone();
		String FirstCom[] = SuppMem.get(0).split(",");
		for(int i=0;i<FirstCom.length;i++) {
			String Comm[] = FirstCom[i].split(" ",2);
			if(Comm[0].equals("pridaj")) {
				if(!newFacts.contains(Comm[1])) {
					newFacts.add(Comm[1]);
				}
			}else if(Comm[0].equals("vymaz")) {
				if(newFacts.contains(Comm[1])) {
					newFacts.remove(Comm[1]);
				}
			}else if(Comm[0].equals("sprava")) {
				Spravy.add(Comm[1]);
			}
		}
		return newFacts;
	}
	
/*
 * Funkcia vytvara a vykonava vsetky mozne akcie nad pravidlami. Vzdy skusi prvu cast pravidla so vsetkymi faktami, a ak nejake "plati", tak
 * ho kontroluje dalej doprava rekurzivne, pokial sa nedostane az na poslednu cast. Ak kazda cast plati, tak sa akcia odlozi na vykonanie.
 * Ak su vsetky mozne akcie vykonane, tak sa prefiltruju (odstrania sa vsetky nepouzitelne akcie). Potom ak existuje aspon jedna pouzitelna akcia
 * tak vykona prvu a zvysne zahodi. Ak neexistuje ziadna pouzitelna akcia, tak sa cyklus aj program ukoncia, vypise sa vypis a pamat faktov	
 */
	
	public static void checkFacts(ArrayList<String> Facts, int Debug) {
		while(true) {
		HashMap<String, String> Parameters = new HashMap<String, String>();
		
			for(int i=0;i<Rules.size();i++) {
				String HelpSplit[]= Rules.get(i).getCondition().split(",");
				String NumOfString[][]=new String[HelpSplit.length][];
				for(int j=0;j<HelpSplit.length;j++) {
					NumOfString[j]=HelpSplit[j].split(" ");
				}
				Corr=false;
				LaterUse.clear();								//tu premazem pomocne polia
				PossibleSol.clear();
				Parameters.clear();
				
				for(int j=0;j<NumOfString.length;j++) {			//najdem pozicie vsetkych pravidiel, ktore vykonam ako posledne
					if(NumOfString[j][0].equals("<>")) {
						LaterUse.add(j);
					}
				}
					if(NumOfString[0][0].equals("<>")) {		//ak sa ma toto pravidlo ma vykonat ako posledne, prechadzam do rekurzie
						checkRec(0+1,NumOfString,Facts,HelpSplit,Parameters);
					}else {										//inak skontrolujem prvu cast pravidla
						for(int k=0;k<Facts.size();k++) {
							String FactsCurrHelp[] = Facts.get(k).split(" ");

							if(FactsCurrHelp.length==NumOfString[0].length) {			//ak sa zhoduje dlzka tak pokracujem na kotrolu po castiach
								
								Parameters.clear();

								boolean possibleFit = true;
								for(int g=0;g<NumOfString[0].length;g++) {

									if(NumOfString[0][g].charAt(0)=='?') {

										Parameters.put(NumOfString[0][g], FactsCurrHelp[g]);
									}else {
										if(NumOfString[0][g].equals(FactsCurrHelp[g])) {
											
										}else {
											possibleFit = false;
											break;
										}
									}
								}if(possibleFit==true) {								//ak cast pravidla plati, tak pokracuje do rekurzie
									checkRec(0+1,NumOfString,Facts,HelpSplit,Parameters);
								}
						}
					}
				}
					
				for(int j=0;j<PossibleSol.size();j++) {							//kontroluje pole najdenych akcii
					boolean canIns = true;
					String ParsedSol[]=PossibleSol.get(j).split(" ");
					HashMap<String, String> HelpMap = new HashMap<String, String>();		//doplna parametre
					for(int k=0;k<ParsedSol.length;k++) {
						if(ParsedSol[k].length()!=0) {
							String Key[]=ParsedSol[k].split("-");
							HelpMap.put(Key[0], Key[1]);
						}
					}
					for(int k=0;k<LaterUse.size();k++) {						//kontroluje pravidla, ktore sa vykonavaju ako posledne
						if(HelpMap.get(NumOfString[LaterUse.get(k)][1]).equals(HelpMap.get(NumOfString[LaterUse.get(k)][2]))) {
							canIns = false;
							break;
						}
					}
					if(canIns==true) {											//ked splna vsetky podmienty
						String BuildNew = "";
						String ResultNum[] = Rules.get(i).getResult().split(",");
						for(int g=0;g<ResultNum.length;g++) {
							String ResultParsed[] = ResultNum[g].split(" ");
							for(int k=0;k< ResultParsed.length;k++) {
								if(ResultParsed[k].charAt(0)=='?') {
									BuildNew+=HelpMap.get(ResultParsed[k]);
								}else {
									BuildNew+=ResultParsed[k];
								}
								if(k<ResultParsed.length-1) {
									BuildNew+=" ";
								}
							}
							if(g<ResultNum.length-1) {
								BuildNew+=",";
							}
						}
							SuppMem.add(BuildNew);
					}
				}if((PossibleSol.size()==0)&&(Corr==true)) {
					
						SuppMem.add(Rules.get(i).getResult());
				}
			}

			FilterMemory(Facts);
			if(SuppMem.size()>0) {											//ak je aspon jedna akcia
				if(Debug==1) {											
					System.out.println("\n---------------------\nPomocne vypisy\n---------------------\n");
					for(int j=0;j<SuppMem.size();j++) {
						System.out.println(SuppMem.get(j));
					}
					System.out.println("\n---------------------\nSPRAVY\n---------------------\n");
				}
				Facts = executeFirst(Facts);
				if(Debug==1) {
					for(int j=0;j<Spravy.size();j++) {
						System.out.println(Spravy.get(j));
					}
					System.out.println("\n---------------------\nPamat faktov\n---------------------\n");
					for(int j=0;j<Facts.size();j++) {
						System.out.println(Facts.get(j));
					}
					Scanner sc = new Scanner(System.in);
					while(true) {
						System.out.println("\nStlac enter pre dalsi krok\n");
						String inp = sc.nextLine();
						if(inp.equals(""))break;
					}
					System.out.println("\n----------------------------------------------------------------------\n");
				}
				SuppMem.clear();
				
			}else {															//inak koniec
				if(Debug==1) {
					System.out.println("\n---------------------\nPomocne vypisy\n---------------------\n");
					System.out.println("\n---------------------\nSPRAVY\n---------------------\n");
					for(int j=0;j<Spravy.size();j++) {
						System.out.println(Spravy.get(j));
					}
					System.out.println("\n---------------------\nPamat faktov\n---------------------\n");
					for(int j=0;j<Facts.size();j++) {
						System.out.println(Facts.get(j));
					}
				}else {
					System.out.println("\n---------------------\nSPRAVY\n---------------------\n");
					for(int j=0;j<Spravy.size();j++) {
						System.out.println(Spravy.get(j));
					}
					System.out.println("\n---------------------\nPamat faktov\n---------------------\n");
					for(int j=0;j<Facts.size();j++) {
						System.out.println(Facts.get(j));
					}
				}
				break;
			}
			
		}
	}
	
/*
 * Funkcia sluzi na spracovanie vstupneho stringu, rozdeli ich podla zatvoriek	
 */
	
	static String returnSplit(String Spl) {
		String NewSpl = "";
		String Helper[] = Spl.split("\\(");
		for(int i=0;i<Helper.length;i++) {
			if(Helper[i].length()>0) {
				NewSpl+=Helper[i].replaceAll("[()]", "");
				if(i!=Helper.length-1)
					NewSpl+=",";
			}
		}
		return NewSpl;
	}
	
/*
 * V maine nacitam subor v ktorom su zadane pravidla a fakty. Tieto pravidla a fakty potom vyparsujem a rozdelim do spravnych poli.
 * Subor rozdelujem podla slov MENO,AK,POTOM.
 */
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.println("Zadajte nazov suboru (.txt)");
        String fileName;
		while(true) 
		{
			fileName = s.nextLine();
			File myObj = new File(fileName);
			if(myObj.exists()) 
			{
				break;
			}
			System.out.println("Subor neexistuje");
		}
		int Debug;
		System.out.println("Chcete vypis po krokoch?(0/1)");
		Debug = s.nextInt();
		ArrayList<String> Facts = new ArrayList<String>();
		try {
			File myObj = new File(fileName);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        data = data.replaceAll("[()]", "");
		        if(data.equals("/***/")){
		        	break;
		        }
		        Facts.add(data);
		      }
			String helpName = null;
			String helpContition = null;
			String helpResult = null;
			while(myReader.hasNextLine()) {
				String data = myReader.nextLine();
				if(data.equals("")) {
					helpName = helpName.split("MENO ")[1];
					helpContition = helpContition.split("AK ")[1];
					helpResult = helpResult.split("POTOM ")[1];
					helpContition = returnSplit(helpContition);
					helpResult = returnSplit(helpResult);
					Rules.add(new Rules(helpName, helpContition, helpResult));
				}else if(data.substring(0, 4).equals("MENO")) {
					helpName = data;
				}else if(data.substring(0,2).equals("AK")) {
					helpContition = data;
				}else if(data.substring(0,5).equals("POTOM")) {
					helpResult = data;
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		checkFacts(Facts,Debug);
		s.close();
	}

}
