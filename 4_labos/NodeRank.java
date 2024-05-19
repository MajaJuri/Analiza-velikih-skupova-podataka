import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeRank {

	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String red = br.readLine();
		
		int n = Integer.parseInt(red.split(" ")[0]); // broj cvorova
		double beta = Double.parseDouble(red.split(" ")[1]); // vjerojatnost da ide u susjedni cvor, da se NECE teleportirat
		
		Map<Integer, List<Integer>> ulazni = new HashMap<>(); // kljuc je cvor, value su svi cvorovi iz kojih idu poveznice prema cvoru kljuca
		int[] brojIzlaznih = new int[n];
		
		for(int i = 0; i < n; i++) { // i-ti cvor
			red = br.readLine();
			String[] cvorovi = red.split(" ");
			brojIzlaznih[i] = cvorovi.length;
			for(int j = 0; j < cvorovi.length; j++) {
				int cvor = Integer.parseInt(cvorovi[j]);
				if(ulazni.containsKey(cvor)) {
					ulazni.get(cvor).add(i);
				}else {
					List<Integer> ulazniCvorovi = new ArrayList<>();
					ulazniCvorovi.add(i);
					ulazni.put(cvor, ulazniCvorovi);
				}
			}
		}
		//System.out.println(ulazni);
		
		red = br.readLine();
		int Q = Integer.parseInt(red);
		
		double[][] rangovi = izracunajRangove(ulazni, brojIzlaznih, 101, n, beta);
		
		DecimalFormat df = new DecimalFormat("0.0000000000");
		
		StringBuilder sb = new StringBuilder();
		for(int q = 0; q < Q; q++) {
			red = br.readLine();
			int indeksCvora = Integer.parseInt(red.split(" ")[0]);
			int rbrIteracije = Integer.parseInt(red.split(" ")[1]);
			double rezultat = rangovi[indeksCvora][rbrIteracije];
			sb.append(df.format(rezultat).replace(',', '.'));
			sb.append("\n");
		}
		System.out.print(sb);

	}
	
	private static double[][] izracunajRangove(Map<Integer, List<Integer>> ulazni, int[] izlazni, int brojIteracija, int brojCvorova, double beta){
		// inicijalizacija
		double[][] rangovi = new double[brojCvorova][101];
		for(int i = 0; i < brojCvorova; i++) {
			rangovi[i][0] = (double) 1/brojCvorova;
		}
		
		for(int t = 1; t < brojIteracija; t++) { // broj iteracija PageRank algoritma
			double S = 0.0; // accumulator for non leaked importance
			for(Integer cvor: ulazni.keySet()) {
				List<Integer> ulazniCvorovi = ulazni.get(cvor);
				double r = rangovi[cvor][t];
				for(Integer ulaz: ulazniCvorovi) {
					 r += beta * rangovi[ulaz][t-1]/izlazni[ulaz];
				}
				rangovi[cvor][t] = r;
				S = S + rangovi[cvor][t];
			}
			for(int i = 0; i < brojCvorova; i++) { //re-insert the leaked importance
				rangovi[i][t] += ((1-S)/brojCvorova);
			}	
		}
		return rangovi;
	}

}
