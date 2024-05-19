import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimHashBuckets {
	
	public static int B = 8;
	public static int R = Shared.DULJINA_SAZETKA/B;

	public static void main(String[] args) throws NumberFormatException, IOException {

		//BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\MajaJuric\\Documents\\FER\\Analiza velikih skupova podataka\\git\\AVSP\\labosi\\lab-1\\testovi\\sprutB.in", StandardCharsets.UTF_8));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int N = Integer.parseInt(br.readLine());
		String[] sazetci = new String[N]; // bilo bi bolje da je globalna al ok
		
		for(int i = 0; i < N; i++) {
			String red = br.readLine();
			sazetci[i] = Shared.SimHash(red);
		}
		
		Map<Integer, Set<Integer>> kandidati = algoritamLSH(sazetci);
		
		int Q = Integer.parseInt(br.readLine());
		for (int i = 0; i < Q; i++) {
            String[] red = br.readLine().split(" ");
            int I = Integer.parseInt(red[0]);
            int K = Integer.parseInt(red[1]);
            System.out.println(brojKandidataUdaljenihZaManjeOdK(kandidati, I, K, sazetci));
		}
	}
	
	// za dane sazetke vraca mapu kandidata <indeks dokumenta, moguci slicni dokumenti dokumentu koji je u kljucu>
	private static Map<Integer, Set<Integer>> algoritamLSH(String[] sazetci){
		Map<Integer, Set<Integer>> kandidati = new HashMap<>();
		for(int pojas = 0; pojas < B; pojas++) { // indeks pojasa, u pseudokodu pise da ide od 1, al msm da je i ovako ok (???), 
			Map<Integer, Set<Integer>> pretinci = new HashMap<>();
			for (int trenutni_id = 0; trenutni_id < sazetci.length; trenutni_id++) {
				String hash = sazetci[trenutni_id];
				int val = hash2int(pojas, hash);
				Set<Integer> tekstovi_u_pretincu = new HashSet<>();
				if(pretinci.get(val)!= null) {
					tekstovi_u_pretincu = pretinci.get(val);
					for (Integer tekst_id : tekstovi_u_pretincu) {
						if(!kandidati.containsKey(trenutni_id)) {
							kandidati.put(trenutni_id, new HashSet<>());
						}
						if(!kandidati.containsKey(tekst_id)) {
							kandidati.put(tekst_id, new HashSet<>());
						}
						kandidati.get(trenutni_id).add(tekst_id);
						kandidati.get(tekst_id).add(trenutni_id);
					}
				}
				tekstovi_u_pretincu.add(trenutni_id);
				pretinci.put(val, tekstovi_u_pretincu);
			}
		}
		//System.out.println("kandidati" + kandidati);
		return kandidati;
	}
	
	// dohvaca dio hasha koji odgovara tom pojasu
	private static int hash2int(int pojas, String hash) {
		String hash_u_pojasu = hash.substring(R*pojas, R*(pojas+1));
		return Integer.parseInt(hash_u_pojasu, 2);
	}
	
	private static int brojKandidataUdaljenihZaManjeOdK(Map<Integer, Set<Integer>> kandidati, int I, int K, String[] sazetci) {
		int rezultat = 0;
		String sazetak_I = sazetci[I];
		Set<Integer> kandidati_za_I = kandidati.get(I);
		for(Integer index: kandidati_za_I) {
			if(Shared.HammingovaUdaljenostManjaOdK(sazetci[index], sazetak_I, K)){
				rezultat += 1;
			}		
		}
		return rezultat;
	}

}
