import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SimHash {
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		//BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\MajaJuric\\Documents\\FER\\Analiza velikih skupova podataka\\git\\AVSP\\labosi\\lab-1\\testovi\\sprutA.in", StandardCharsets.UTF_8));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int N = Integer.parseInt(br.readLine());
		String[] sazetci = new String[N]; // bilo bi bolje da je globalna al ok
		
		for(int i = 0; i < N; i++) {
			String red = br.readLine();
			sazetci[i] = Shared.SimHash(red);
		}
		
		int Q = Integer.parseInt(br.readLine());
		for (int i = 0; i < Q; i++) {
            String[] red = br.readLine().split(" ");
            int I = Integer.parseInt(red[0]);
            int K = Integer.parseInt(red[1]);
            System.out.println(Shared.brojUdaljenihZaManjeOdK(sazetci, sazetci[I], K));
		}
		
	}
}
