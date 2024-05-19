import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ClosestBlackNode {
	
	public static int udaljenost = 0;
	public static int maxUdaljenost = 10;
	
	public static class Cvor{
		public int boja;
		public int index;
		public List<Cvor> susjedi;
		public int udaljenostDoNajblizegCrnog;
		public int indexNajbliziCrni;
		public boolean posjecen;
		
		public Cvor(int boja, int i) {
			this.boja = boja;
			this.index = i;
			this.susjedi = new ArrayList<>();
			this.udaljenostDoNajblizegCrnog = -1;
			this.indexNajbliziCrni = -1;
		}		
		
	}
		
	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String red = br.readLine();

		int n = Integer.parseInt(red.split(" ")[0]); // broj cvorova
		int e = Integer.parseInt(red.split(" ")[1]); // broj bridova
		
		List<Cvor> graf = new ArrayList<>();
		List<Cvor> crniCvorovi = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			red = br.readLine();
			int boja = Integer.parseInt(red);
			Cvor c = new Cvor(boja, i);
			if(boja == 1) {
				c.udaljenostDoNajblizegCrnog = 0;
				c.indexNajbliziCrni = i;
				crniCvorovi.add(c);
			}
			graf.add(c);
		}
		
		// bridovi
		for (int i = 0; i < e; i++) {
			red = br.readLine();
			int indexCvora1 = Integer.parseInt(red.split(" ")[0]);
			int indexCvora2 = Integer.parseInt(red.split(" ")[1]);
			
			Cvor cvor1 = graf.get(indexCvora1);
			Cvor cvor2 = graf.get(indexCvora2);
			
			cvor1.susjedi.add(cvor2);
			cvor2.susjedi.add(cvor1);
		}

		List<Cvor> cvorovi = udaljenostDoNajblizegCrnogCvora(graf, crniCvorovi, n);
		// ispis udaljenosti od najblizeg crnog cvora za SVAKI cvor u grafu
		for (Cvor c: cvorovi) {
			System.out.println(c.indexNajbliziCrni + " " + c.udaljenostDoNajblizegCrnog);
		}
	}
	
	public static List<Cvor> udaljenostDoNajblizegCrnogCvora(List<Cvor> cvorovi, List<Cvor> crniCvorovi, int n) {
	    List<Cvor> queue = new LinkedList<>();
		for(Cvor crni: crniCvorovi) {
			queue.add(crni);
			crni.posjecen = true;
		}
		
		while(!queue.isEmpty()) {
			udaljenost += 1;
			queue = rekurzija(queue);
		}
		
		return cvorovi;		
	}
	
	public static List<Cvor> rekurzija(List<Cvor> queue){
		List<Cvor> newQueue = new LinkedList<>();
		for(Cvor c: queue) {
			for(Cvor susjed: c.susjedi) {
				if(!susjed.posjecen) {
					newQueue.add(susjed);
					susjed.posjecen = true;
					if(susjed.udaljenostDoNajblizegCrnog <= maxUdaljenost) { 
						susjed.udaljenostDoNajblizegCrnog = udaljenost;
						susjed.indexNajbliziCrni = c.indexNajbliziCrni;
					}
				}		
			}
		}
		return newQueue;
		
	}
	
	
}
