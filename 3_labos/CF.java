import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class CF {

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String red = br.readLine();
		
		int broj_stavki = Integer.parseInt(red.split(" ")[0]);
		int broj_korisnika = Integer.parseInt(red.split(" ")[1]);
		
		// ucitavanje userItem matrice
		float[][] userItemMatrix = new float[broj_stavki][broj_korisnika];
		for(int stavka = 0; stavka < broj_stavki; stavka++) {
			red = br.readLine().replace('X', '0');
			for(int korisnik = 0; korisnik < broj_korisnika; korisnik++) {
				userItemMatrix[stavka][korisnik] = Float.parseFloat(red.split(" ")[korisnik]);
			}
		}
		
		// prosjeci nam kasnije trebaju za normalizaciju
		float[] prosjecneOcjeneKorisnika = izracunajProsjek(userItemMatrix, false);
		float[] prosjecneOcjeneStavki = izracunajProsjek(userItemMatrix, true);
		/*System.out.println("Prosjecne ocjene stavki: ");
		for(float f: prosjecneOcjeneStavki) {
			System.out.print(f + " ");
		}
		System.out.println("\nProsjecne ocjene korisnika: ");
		for(float f: prosjecneOcjeneKorisnika) {
			System.out.print(f + " ");
		}
		System.out.println();*/
				
		int broj_upita = Integer.parseInt(br.readLine());
		for(int q = 0; q < broj_upita; q++) {
			String[] upit = br.readLine().split(" ");
			int stavka = Integer.parseInt(upit[0]);
			int korisnik = Integer.parseInt(upit[1]);
			int algoritam = Integer.parseInt(upit[2]);
			int kardinalni_broj = Integer.parseInt(upit[3]);
			
			if(algoritam == 0) {
				userItemAlgoritam(stavka-1, korisnik-1, kardinalni_broj, userItemMatrix.clone(), prosjecneOcjeneStavki);
			}else {
				userItemAlgoritam(korisnik-1, stavka-1, kardinalni_broj, transponiraj(userItemMatrix), prosjecneOcjeneKorisnika);
			}
			
		}
		
	}
	
	public static float[][] transponiraj(float[][] matrica){
		float[][] transponirana = new float[matrica[0].length][matrica.length];
		for(int i = 0; i < matrica.length; i++) {
			float[] red = matrica[i].clone();
			for(int j = 0; j < matrica[0].length; j++) {
				transponirana[j][i] = red[j];
			}
		}
		return transponirana;
	}
	
	public static void userItemAlgoritam(int stavka, int korisnik, int brojSlicnih, float[][] matrica, float[] prosjecneVrijednosti) {
		// od svakog retka oduzmi prosjecnu vrijednost ocjena za taj item
		float[][] nova_matrica = new float[matrica.length][matrica[0].length];
		for(int i = 0; i < matrica.length; i++) {
			nova_matrica[i] = matrica[i].clone();
		}
		
		for(int i = 0; i < matrica.length; i++) {
			for(int j = 0; j < matrica[i].length; j++) {
				if(matrica[i][j] != 0) {
					nova_matrica[i][j] = matrica[i][j] - prosjecneVrijednosti[i];
				}else {
					nova_matrica[i][j] = 0;
				}
			}
		}
		
		
		/*System.out.println("Normalizirana matrica:");
		for(int i = 0; i < nova_matrica.length; i++) {
			for(int j = 0; j < nova_matrica[i].length; j++) {
				System.out.print(nova_matrica[i][j] + " ");
			}
			System.out.println();
		}*/
		
		Map<Integer, Float> slicnostiIzmeduStavki = izracunajSlicnosti(nova_matrica, stavka);
		
		// sortiranje po padajucoj slicnosti
		List<Entry<Integer, Float>> sortiraneSlicnostiIzmeduStavki = new ArrayList<>(slicnostiIzmeduStavki.entrySet());
		sortiraneSlicnostiIzmeduStavki.sort(Entry.comparingByValue(Comparator.reverseOrder()));
		
		float brojnik = 0;
		float nazivnik = 0;
		int k = 0;
		for(int najslicniji = 0; najslicniji < sortiraneSlicnostiIzmeduStavki.size(); najslicniji++) {
			if(k == brojSlicnih) {
				break;
			}
			
			Entry<Integer, Float> elementSimilarity = sortiraneSlicnostiIzmeduStavki.get(najslicniji);
			Float similarity = elementSimilarity.getValue();
			//System.out.println("similarity " + similarity);
			Integer item = elementSimilarity.getKey();
			if(similarity > 0) { //  && item != stavka -> ne smije tu ic jer onda cim dode to stavke ode na else....dummy
				float ocjena = matrica[item][korisnik];
				//System.out.println("position " + item);
				if(ocjena > 0) {
					//System.out.println("grade " + ocjena);
					brojnik += similarity * ocjena;
					nazivnik += similarity;
					k+=1;
				}
			}else {
				break;
			}
		}
		
		float rezultat = brojnik / nazivnik;
		//System.out.println("brojnik " + brojnik + "\nnazivnik " + nazivnik);
		ispisiRezultat(rezultat);		
	}
	
	public static void ispisiRezultat(float rezultat) {
        Locale.setDefault(new Locale("en", "US"));
        DecimalFormat df = new DecimalFormat("#.000", DecimalFormatSymbols.getInstance());
        BigDecimal bd = new BigDecimal(rezultat);
        BigDecimal res = bd.setScale(3, RoundingMode.HALF_UP);
        System.out.println(df.format(res));
	}
	
	public static Map<Integer, Float> izracunajSlicnosti(float[][] nova_matrica, int stavka){
		Map<Integer, Float> slicnostiIzmeduStavki = new HashMap<>();
		for(int i = 0; i < nova_matrica.length; i++) {
			float brojnik = 0;
			float nazivnik1 = 0;
			float nazivnik2 = 0;
			if(i != stavka) {
				for(int j = 0; j < nova_matrica[0].length; j++) {
					brojnik += nova_matrica[i][j]*nova_matrica[stavka][j];
					nazivnik1 += Math.pow(nova_matrica[i][j], 2);
					nazivnik2 += Math.pow(nova_matrica[stavka][j], 2);
				}
				//System.out.println("\ti = " + i);
				//System.out.println("brojnik " + brojnik);
				//System.out.println("nazivnik1 " + nazivnik1);
				//System.out.println("nazivnik2 " + nazivnik2);
				slicnostiIzmeduStavki.put(i, brojnik/ ((float)(Math.sqrt(nazivnik1*nazivnik2))));
			}else {
				slicnostiIzmeduStavki.put(i, 1f); // red je sam sebi u potpunosti slican
			}
		}
		return slicnostiIzmeduStavki;
	}
	
	public static float[] izracunajProsjek(float[][] matrica, boolean zaSvakiRed) {
		if(zaSvakiRed) { // za svaki item
			float[] rezultat = new float[matrica.length];
			for(int i = 0; i < matrica.length; i++) {
				float[] red = matrica[i];
				int broj_neX_elemenata = 0;
				int zbroj_neX_elemenata = 0;
				for(float element: red) {
					if(element != 0) {
						broj_neX_elemenata += 1;
						zbroj_neX_elemenata += element;
					}
				}
				rezultat[i] = zbroj_neX_elemenata/((float) broj_neX_elemenata);
			}
			return rezultat;
		}else { // za svakog korisnika
			float[] rezultat = new float[matrica[0].length];
			int [] broj_neX_elemenata = new int[matrica[0].length];
			float [] zbroj_neX_elemenata = new float[matrica[0].length];
			for(int i = 0; i < matrica.length; i++) {
				for(int j = 0; j < matrica[i].length; j++) {
					if(matrica[i][j] != 0) {
						broj_neX_elemenata[j] += 1;
						zbroj_neX_elemenata[j] += matrica[i][j];
					}
				}
			}
			for(int i = 0; i < matrica[0].length; i++) {
				float rezultat_element = (zbroj_neX_elemenata[i]/((float) broj_neX_elemenata[i]));
				rezultat[i] = rezultat_element;				
			}
			return rezultat;			
		}
	}

}
