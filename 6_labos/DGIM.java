import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DGIM {

	public static List<Pretinac> pretinci = new ArrayList<>();
	public static int N;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String red = br.readLine();
		N = Integer.parseInt(red); // velicina prozora

		while ((red = br.readLine()) != null && !red.isEmpty()) {
			red = red.trim();

			if (red.startsWith("q")) {
				int k = Integer.parseInt(red.split(" ")[1]);

				odgovoriNaUpit(k);

			} else {
				String[] bitovi = red.split("");
				for (String bit : bitovi) {

					dodajUStream(bit);

				}
			}
		}
		// System.out.println(pretinci);
	}

	private static void dodajUStream(String bit) {
		for (Pretinac p : pretinci) {
			p.timestamp++;
		}

		if (!pretinci.isEmpty() && pretinci.get(0).timestamp > N) {
			pretinci.remove(0);
		}

		if (bit.equals("1")) {
			pretinci.add(new Pretinac());

			if (pretinci.size() > 2) {
				spojiPretince();
			}

		}
	}

	private static void spojiPretince() {
		int i = pretinci.size() - 2;
		boolean imaZaMergati = true;
		Pretinac pretinac;
		Pretinac prethodniPretinac;
		Pretinac sljedeciPretinac;
		while (imaZaMergati) {
			pretinac = pretinci.get(i);
			prethodniPretinac = pretinci.get(i - 1);
			if (prethodniPretinac.size == pretinac.size) {
				pretinac.size *= 2;
				pretinci.remove(i - 1);
			}
			i--;
			
			if (i <= 1) {
				imaZaMergati = false;
			}
			
			if (imaZaMergati) {
				i--;
				sljedeciPretinac = pretinci.get(i + 1);
				pretinac = pretinci.get(i);
				prethodniPretinac = pretinci.get(i - 1);
				if (prethodniPretinac.size != sljedeciPretinac.size || pretinac.size != sljedeciPretinac.size) {
					imaZaMergati = false;
				}
			}
		}
	}

	private static void odgovoriNaUpit(int k) {
		List<Pretinac> pretinciZaObraditi = new ArrayList<>();
		for (Pretinac b : pretinci) {
			if (b.timestamp <= k) {
				pretinciZaObraditi.add(b);
			}
		}

		int rezultat = 0;
		if (pretinciZaObraditi.size() > 0) {
			rezultat = rezultat + pretinciZaObraditi.get(0).size / 2;
			for (int i = 1; i < pretinciZaObraditi.size(); i++) {
				rezultat += pretinciZaObraditi.get(i).size;
			}
		}

		System.out.println(rezultat);
	}

	static class Pretinac {
		public int timestamp;
		public int size;

		public Pretinac() {
			timestamp = 1;
			size = 1;
		}

		public String toString() {
			return "Pretinac size = " + this.size + " timestamp = " + this.timestamp;
		}
	}
}
