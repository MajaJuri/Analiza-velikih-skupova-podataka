import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PCY {

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		//BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\MajaJuric\\Documents\\FER\\Analiza velikih skupova podataka\\Labosi\\lab2_primjer[1]\\sprut_primjer\\R.in", StandardCharsets.UTF_8));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		// ucitaj broj kosara
		int brKosara = Integer.parseInt(br.readLine()); // N -> ukupan broj kosara u datoteci
		
		// ucitaj podatke o pragu iz ulaznih podataka
		float s = Float.parseFloat(br.readLine()); // s -> prag
		int prag = (int) Math.floor(s*brKosara);
		
		// ucitaj broj pretinaca
		int brPretinaca = Integer.parseInt(br.readLine());
		
		// brojac predmeta
		Map<Integer, Integer> brPredmeta = new HashMap<>();
		//Map<Integer, Set<Integer>> kosaraPredmeti = new HashMap<>(); // nije bitno koji je indeks kosare
		List<Set<Integer>> kosare = new ArrayList<>();		
		
		// prvi prolaz
		String red;
		for(int i = 0; i < brKosara; i++) {
			red = br.readLine();
			String[] predmeti = red.split(" ");
			Set<Integer> predmeti_u_kosari = new HashSet<>();
			for(String predmet: predmeti) {
				int predmetIndeks = Integer.parseInt(predmet);
				predmeti_u_kosari.add(predmetIndeks);
				if(brPredmeta.containsKey(predmetIndeks)) {
					brPredmeta.put(predmetIndeks, brPredmeta.get(predmetIndeks)+1);
				}else {
					brPredmeta.put(predmetIndeks, 1);
				}
			}
			kosare.add(predmeti_u_kosari);
		}
		
		// broj cestih predmeta
		int m = 0;
		for(int predmet: brPredmeta.keySet()) {
			if(brPredmeta.get(predmet) >= prag) {
				m+=1;
			}
		}
		
		// pretinci za funkciju sazimanja - polje velicine brPretinaca
		int[] pretinci = new int[brPretinaca];
		// drugi prolaz
		//int ispis = 0;
		for(int kosara = 0; kosara < kosare.size(); kosara++) {
			Set<Integer> predmeti = kosare.get(kosara);
			for(int predmetI: predmeti) {
				for(int predmetJ: predmeti) {
					if(predmetI < predmetJ) {
						if(brPredmeta.get(predmetI) >= prag && brPredmeta.get(predmetJ) >= prag) {
							int k = ((predmetI * brPredmeta.size()) + predmetJ ) % brPretinaca;
							pretinci[k] += 1;
						}
					}
				}
			}
			//System.out.println("2: " + ispis++);
		}
		
		// treci prolaz
		//ispis=0;
		StringBuilder sb = new StringBuilder();
		Map<String, Integer> parovi = new HashMap<>();
		for(int kosara = 0; kosara < kosare.size(); kosara++) {
			Set<Integer> predmeti = kosare.get(kosara);
			for(int predmetI: predmeti) {
				for(int predmetJ: predmeti) {
					if(predmetI < predmetJ) {
						if(brPredmeta.get(predmetI) >= prag && brPredmeta.get(predmetJ) >= prag) {
							int k = ((predmetI * brPredmeta.size()) + predmetJ ) % brPretinaca;
							if(pretinci[k] >= prag) {
								sb.append("(");
								sb.append(predmetI);
								sb.append(",");
								sb.append(predmetJ);
								sb.append(")");
								String kljuc = sb.toString();
								sb.setLength(0);
								if(parovi.containsKey(kljuc)) {
									parovi.put(kljuc, parovi.get(kljuc)+1);
								}else {
									parovi.put(kljuc, 1);
								}								
							}
						}
					}
				}
			}
			//System.out.println("3: " + ispis++);
		}

		int A = m*(m-1)/2;
		int P = parovi.size();
		System.out.println(A);
		System.out.println(P);
		
		List<Integer> brPojavljivanja = new ArrayList<Integer>(parovi.values());
	    Collections.sort(brPojavljivanja); //sorting in ascending order
	    Collections.reverse(brPojavljivanja); //reversing the sorted list
		for(int broj: brPojavljivanja) {
			System.out.println(broj);
		}
	}
}
