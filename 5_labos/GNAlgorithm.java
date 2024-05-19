import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class GNAlgorithm {

	public static int maxSlicnost;

	public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public static Map<Integer, Map<Integer, Integer>> brojNajkracihPuteva = new HashMap<>();
	public static Map<Integer, Map<Integer, Double>> najkraciPut = new HashMap<>();
	public static Map<Integer, Map<Integer, List<List<Integer>>>> najkraciPutevi = new HashMap<>();
	public static int iteracija = 1;
	public static int brojBridova;
	public static int brojUklonjenihBridova = 0;
	public static double maxModularnost = Double.NEGATIVE_INFINITY;
	public static Map<Integer, Map<Integer, Double>> maxModularnostGraf;
	public static double m;
	public static Map<Integer, Double> k;
	public static Map<Integer, Map<Integer, Double>> originalniGraf;
	public static DecimalFormat decimalFormat;

	public static void main(String[] args) throws IOException {

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
		symbols.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat("#0.000", symbols);
		Map<Integer, List<Integer>> vezeIzmeduKorisnika = ucitajVezeIzmeduKorisnika();
		Map<Integer, List<Integer>> svojstvaKorisnika = ucitajSvojstvaKorisnika();
		// System.out.println(vezeIzmeduKorisnika);
		// System.out.println(svojstvaKorisnika);

		Map<Integer, Map<Integer, Double>> graf = konstruiranjeGrafa(vezeIzmeduKorisnika, svojstvaKorisnika);
		originalniGraf = new HashMap<>();

		// Create a deep copy of the graf map
		for (Map.Entry<Integer, Map<Integer, Double>> entry : graf.entrySet()) {
			int cvor = entry.getKey();
			Map<Integer, Double> bridovi = entry.getValue();

			// Create a new map and copy the entries from the bridovi map
			Map<Integer, Double> copyBridovi = new HashMap<>(bridovi);

			// Put the copied map into the originalniGraf map
			originalniGraf.put(cvor, copyBridovi);
		}

		m = ukupnaTezina(graf);
		k = tezineCvorova(graf);
		maxModularnostGraf = new HashMap<Integer, Map<Integer, Double>>(graf);
		// System.out.println("graf " + graf);
		brojNajkracihPuteva(graf);
		// System.out.println("broj najkracih puteva " + brojNajkracihPuteva);

		// System.out.println("najkraciPutevi " + najkraciPutevi);

		ukloniBridove(graf);
		// System.out.println(maxModularnostGraf);
		ispisiGrafMaxModularnosti();
	}

	private static Map<Integer, List<Integer>> ucitajVezeIzmeduKorisnika() throws NumberFormatException, IOException {
		Map<Integer, List<Integer>> vezeIzmeduKorisnika = new HashMap<>();

		String red;
		while ((red = br.readLine()) != null && !red.isEmpty()) {
			brojBridova++;
			int id1 = Integer.parseInt(red.split(" ")[0]);
			int id2 = Integer.parseInt(red.split(" ")[1]);

			if (vezeIzmeduKorisnika.containsKey(id1)) {
				vezeIzmeduKorisnika.get(id1).add(id2);
			} else {
				List<Integer> lista = new ArrayList<>();
				lista.add(id2);
				vezeIzmeduKorisnika.put(id1, lista);
			}
			if (vezeIzmeduKorisnika.containsKey(id2)) {
				vezeIzmeduKorisnika.get(id2).add(id1);
			} else {
				List<Integer> lista = new ArrayList<>();
				lista.add(id1);
				vezeIzmeduKorisnika.put(id2, lista);
			}
		}
		return vezeIzmeduKorisnika;
	}

	private static Map<Integer, List<Integer>> ucitajSvojstvaKorisnika() throws NumberFormatException, IOException {
		Map<Integer, List<Integer>> svojstvaKorisnika = new HashMap<>();

		String red;
		while ((red = br.readLine()) != null && !red.isEmpty()) {
			int id = Integer.parseInt(red.split(" ")[0]);
			List<Integer> lista = new ArrayList<>();

			String[] dijelovi = red.split(" ");
			maxSlicnost = dijelovi.length - 1;

			for (int i = 1; i < dijelovi.length; i++) {
				int value = Integer.parseInt(dijelovi[i]);
				lista.add(value);
			}
			svojstvaKorisnika.put(id, lista);
		}
		return svojstvaKorisnika;
	}

	private static Map<Integer, Map<Integer, Double>> konstruiranjeGrafa(
			Map<Integer, List<Integer>> vezeIzmeduKorisnika, Map<Integer, List<Integer>> svojstvaKorisnika) {
		Map<Integer, Map<Integer, Double>> graf = new HashMap<>();

		for (Integer key : svojstvaKorisnika.keySet()) {
			Map<Integer, Double> mapa = new HashMap<>();
			graf.put(key, mapa);
		}
		for (Integer key : vezeIzmeduKorisnika.keySet()) {
			Map<Integer, Double> mapa = new HashMap<>();
			graf.put(key, mapa);
		}

		for (Integer key : vezeIzmeduKorisnika.keySet()) {
			Map<Integer, Double> slicnosti = new HashMap<>();
			for (Integer val : vezeIzmeduKorisnika.get(key)) {
				double slicnost = GNAlgorithm.maxSlicnost
						- izracunajSlicnost(svojstvaKorisnika.get(key), svojstvaKorisnika.get(val)) + 1;
				slicnosti.put(val, slicnost);
			}
			slicnosti.put(key, 0.0); // dodati i samog sebe
			graf.put(key, slicnosti);
		}

		return graf;
	}

	private static double izracunajSlicnost(List<Integer> vektor1, List<Integer> vektor2) {
		double slicnost = 0;
		int size = Math.min(vektor1.size(), vektor2.size());

		for (int i = 0; i < size; i++) {
			if (vektor1.get(i).equals(vektor2.get(i))) {
				slicnost++;
			}
		}

		// System.out.println(vektor1 + " i " + vektor2 + " = " + slicnost);
		return slicnost;
	}

	public static List<List<Integer>> findAllPaths(Map<Integer, Map<Integer, Double>> graph, int source,
			int destination) {
		List<List<Integer>> allPaths = new ArrayList<>();
		List<Integer> currentPath = new ArrayList<>();
		Set<Integer> visited = new HashSet<>();
		findPaths(graph, source, destination, currentPath, visited, allPaths);

		return allPaths;
	}

	private static void findPaths(Map<Integer, Map<Integer, Double>> graph, int currentVertex, int destination,
			List<Integer> currentPath, Set<Integer> visited, List<List<Integer>> allPaths) {
		currentPath.add(currentVertex);
		visited.add(currentVertex);

		if (currentVertex == destination) {
			allPaths.add(new ArrayList<>(currentPath));
		} else {
			Set<Integer> neighbors = graph.get(currentVertex).keySet();
			if (neighbors != null) {
				for (int neighbor : neighbors) {
					if (!visited.contains(neighbor)) {
						findPaths(graph, neighbor, destination, currentPath, visited, allPaths);
					}
				}
			}
		}

		currentPath.remove(currentPath.size() - 1);
		visited.remove(currentVertex);
	}

	private static void brojNajkracihPuteva(Map<Integer, Map<Integer, Double>> graf) {
		for (int cvor1 : graf.keySet()) {
			Map<Integer, Double> mapa = new HashMap<>();
			Map<Integer, Integer> mapa2 = new HashMap<>();
			Map<Integer, List<List<Integer>>> mapa3 = new HashMap<>();
			for (int cvor2 : graf.keySet()) {
				if (cvor1 == cvor2) {
					mapa.put(cvor2, 0.0);
				} else {
					mapa.put(cvor2, Double.POSITIVE_INFINITY);
				}
				mapa2.put(cvor2, 0);

				List<List<Integer>> lista = new ArrayList<>();
				mapa3.put(cvor2, lista);
			}
			najkraciPut.put(cvor1, mapa);
			brojNajkracihPuteva.put(cvor1, mapa2);
			najkraciPutevi.put(cvor1, mapa3);
		}

		for (int cvor1 : graf.keySet()) {
			for (int cvor2 : graf.keySet()) {
				List<List<Integer>> paths = findAllPaths(graf, cvor1, cvor2);
				for (List<Integer> path : paths) {
					double duljinaPuta = 0.0;
					int source = path.get(0);
					int destination = path.get(path.size() - 1);
					// System.out.println(source + " -> " + destination);
					for (int i = 0; i < path.size() - 1; i++) {
						int from = path.get(i);
						int to = path.get(i + 1);
						duljinaPuta += graf.get(from).get(to);
					}
					// System.out.println(duljinaPuta);
					// System.out.println(najkraciPut.get(source).get(destination));
					if (duljinaPuta < najkraciPut.get(source).get(destination)) {
						najkraciPut.get(source).put(destination, duljinaPuta);
						brojNajkracihPuteva.get(source).put(destination, 1);
						Map<Integer, List<List<Integer>>> temp = najkraciPutevi.get(cvor1);
						List<List<Integer>> lista = new ArrayList<>();
						lista.add(path);
						temp.put(destination, lista);
						najkraciPutevi.put(source, temp);
					} else if (duljinaPuta == najkraciPut.get(source).get(destination)) {
						brojNajkracihPuteva.get(source).put(destination,
								brojNajkracihPuteva.get(source).get(destination) + 1);
						Map<Integer, List<List<Integer>>> temp = najkraciPutevi.get(cvor1);
						List<List<Integer>> lista = temp.get(cvor2);
						lista.add(path);
						temp.put(destination, lista);
						najkraciPutevi.put(source, temp);
					}
				}
			}
		}
		// System.out.println(najkraciPut);
	}

	private static Map<Integer, Map<Integer, Double>> bridneCentralnostiGraf(Map<Integer, Map<Integer, Double>> graf) {

		// System.out.println("graf " + graf);
		brojNajkracihPuteva(graf);
		// System.out.println("najkraci put " + najkraciPut);
		Map<Integer, Map<Integer, Double>> bridneCentralnosti = new HashMap<>();
		for (int cvor1 : graf.keySet()) {
			Map<Integer, Double> mapa = new HashMap<>();
			for (int cvor2 : graf.get(cvor1).keySet()) {
				mapa.put(cvor2, 0.0); // inicijalizacija
			}
			bridneCentralnosti.put(cvor1, mapa);
		}

		// System.out.println(bridneCentralnosti);
		// System.out.println(brojNajkracihPuteva);
		for (int source : brojNajkracihPuteva.keySet()) {
			for (int destination : brojNajkracihPuteva.get(source).keySet()) {
				if (source != destination && brojNajkracihPuteva.get(source).get(destination) > 0
						&& graf.containsKey(source)) {
					for (List<Integer> path : najkraciPutevi.get(source).get(destination)) {
						// System.out.println(path);
						for (int i = 0; i < path.size() - 1; i++) {
							int from = path.get(i);
							int to = path.get(i + 1);

							double trenutnaBridnaCentralnost = bridneCentralnosti.get(from).get(to);
							trenutnaBridnaCentralnost = trenutnaBridnaCentralnost
									+ 1.0 / brojNajkracihPuteva.get(source).get(destination);
							bridneCentralnosti.get(from).replace(to, trenutnaBridnaCentralnost); // trebalo bi dijeliti
																									// s dva, ali bitno
																									// je samo
																									// sortiranje, a ne
																									// sam iznos bridne
																									// centralnosti
						}
					}
				}
			}
		}
		return bridneCentralnosti;
	}

	private static void ukloniBridove(Map<Integer, Map<Integer, Double>> graf) {
		if (brojUklonjenihBridova < brojBridova) {
			// System.out.println("Iteracija " + iteracija++);
			Map<Integer, Map<Integer, Double>> bridneCentralnosti = bridneCentralnostiGraf(graf);
			// System.out.println("bridne centralnosti " + bridneCentralnosti);
			double modularnost = izracunajModularnost();
			// System.out.println("MODULARITY:" + decimalFormat.format(modularnost));
			if (modularnost > maxModularnost) {
				maxModularnost = modularnost;
				maxModularnostGraf = new HashMap<>();

				// Create a deep copy of the graf map
				for (Map.Entry<Integer, Map<Integer, Double>> entry : graf.entrySet()) {
					int cvor = entry.getKey();
					Map<Integer, Double> bridovi = entry.getValue();

					// Create a new map and copy the entries from the bridovi map
					Map<Integer, Double> copyBridovi = new HashMap<>(bridovi);

					// Put the copied map into the originalniGraf map
					maxModularnostGraf.put(cvor, copyBridovi);
				}

				// System.out.println("novi max");
			}

			double maxValue = Double.NEGATIVE_INFINITY;
			Map<Integer, Set<Integer>> bridoviZaUkloniti = new HashMap<>();
			for (int cvor : bridneCentralnosti.keySet()) {
				Set<Integer> set = new HashSet<>();
				bridoviZaUkloniti.put(cvor, set);
			}
			maxValue = Double.NEGATIVE_INFINITY;
			for (int cvor1 : bridneCentralnosti.keySet()) {
				for (int cvor2 : bridneCentralnosti.get(cvor1).keySet()) {
					if (cvor1 < cvor2) {
						double bridnaCentralnost = bridneCentralnosti.get(cvor1).get(cvor2);
						if (bridnaCentralnost > maxValue) {
							maxValue = bridnaCentralnost;
							bridoviZaUkloniti = new HashMap<>();
							Set<Integer> set = new HashSet<>();
							set.add(cvor2);
							bridoviZaUkloniti.put(cvor1, set);
						} else if (bridnaCentralnost == maxValue) {
							Set<Integer> set = bridoviZaUkloniti.get(cvor1);
							if (set == null) {
								set = new HashSet<>();
							}
							set.add(cvor2);
							bridoviZaUkloniti.put(cvor1, set);
						}
					}
				}
			}
			// System.out.println("\t" + bridoviZaUkloniti);
			List<String> ispis = new ArrayList<>();
			for (int cvor1 : bridoviZaUkloniti.keySet()) {
				for (int cvor2 : bridoviZaUkloniti.get(cvor1)) {
					brojUklonjenihBridova += 1;
					ispis.add(cvor1 + " " + cvor2);
					// System.out.println(cvor1 + " " + cvor2);

					Map<Integer, Double> mapa = bridneCentralnosti.get(cvor1);
					if (mapa != null) {
						mapa.remove(cvor2);
						bridneCentralnosti.put(cvor1, mapa);
					}

					mapa = graf.get(cvor1);
					if (mapa != null) {
						mapa.remove(cvor2);
						graf.put(cvor1, mapa);
					}

					mapa = graf.get(cvor2);
					if (mapa != null) {
						mapa.remove(cvor1);
						graf.put(cvor2, mapa);
					}
					// System.out.println(graf);
				}
			}
			String[] ispisArray = ispis.toArray(new String[0]);
			Arrays.sort(ispisArray,
					Comparator.comparingInt(GNAlgorithm::getFirstValue).thenComparingInt(GNAlgorithm::getSecondValue));
			for (String s : ispisArray) {
				System.out.println(s);
			}

			ukloniBridove(graf);
		} else {
			return;
		}
	}

	private static double izracunajModularnost() {
		// double m = ukupnaTezina(graf);
		// System.out.println("Ukupna tezina = " + m);
		// Map<Integer, Double> k = tezineCvorova(graf);
		// System.out.println("Tezine cvorova = " + k);
		double modularnost = 0.0;
		double suma;
		for (int cvor1 : originalniGraf.keySet()) {
			for (int cvor2 : originalniGraf.keySet()) {
				if (uZajednici(cvor1, cvor2)) {
					// System.out.println("izracun modularnosti za " + cvor1 + " i " + cvor2);
					if (cvor1 != cvor2 && originalniGraf.get(cvor1).containsKey(cvor2)) {
						suma = originalniGraf.get(cvor1).get(cvor2) - (k.get(cvor1) * k.get(cvor2)) / (2 * m);
						// System.out.println("vrijednost koja se zbraja je : " + suma);
						modularnost += suma;
						// System.out.println("trenutna suma je " + modularnost);
					} else {
						suma = 0.0 - k.get(cvor1) * k.get(cvor2) / (2 * m);
						// System.out.println("Suma koja se zbraja je : " + suma);
						modularnost += suma;
						// System.out.println("Trenutna suma je " + modularnost);
					}
				}
			}
		}

		modularnost = modularnost / 2;
		modularnost = modularnost / m;
		return modularnost;
	}

	private static boolean uZajednici(int cvor1, int cvor2) {
		if (najkraciPut.get(cvor1).get(cvor2) < Double.POSITIVE_INFINITY) {
			// System.out.println(" pripadaju zajednici");
			return true;
		} else {
			// System.out.println(" ne pripadaju zajednici");
			return false;
		}
	}

	private static Map<Integer, Double> tezineCvorova(Map<Integer, Map<Integer, Double>> graf) {
		Map<Integer, Double> k = new HashMap<>();
		for (int cvor : graf.keySet()) {
			double tezina = 0.0;
			for (int cvor2 : graf.get(cvor).keySet()) {
				tezina += graf.get(cvor).get(cvor2);
			}
			k.put(cvor, tezina);
		}
		return k;
	}

	private static double ukupnaTezina(Map<Integer, Map<Integer, Double>> graf) {
		double m = 0.0;
		for (int cvor1 : graf.keySet()) {
			for (int cvor2 : graf.keySet()) {
				if (graf.get(cvor1).containsKey(cvor2)) {
					if (cvor1 < cvor2) {
						if (cvor1 != cvor2) {
							m += graf.get(cvor1).get(cvor2);
						}
					}
				}
			}
		}
		return m;
	}

	public static int getFirstValue(String str) {
		String[] split = str.trim().split("\\s+");
		return Integer.parseInt(split[0]);
	}

	public static int getSecondValue(String str) {
		String[] split = str.trim().split("\\s+");
		return Integer.parseInt(split[1]);
	}

	public static void ispisiGrafMaxModularnosti() {
		brojNajkracihPuteva(maxModularnostGraf);
		// System.out.println(maxModularnostGraf);
		// System.out.println(najkraciPutevi);
		Map<Integer, Set<Integer>> zajedniceMapa = new HashMap<>();
		for (int cvor1 : maxModularnostGraf.keySet()) {
			Set<Integer> set = new HashSet<>();
			set.add(cvor1);
			for (int cvor2 : najkraciPutevi.get(cvor1).keySet()) {
				for (List<Integer> put : najkraciPutevi.get(cvor1).get(cvor2)) {
					set.addAll(put);
				}
			}
			zajedniceMapa.put(cvor1, set);
		}
		// System.out.println(zajednice);
		Set<Set<Integer>> zajednice = new HashSet<>();
		for (Set<Integer> set : zajedniceMapa.values()) {
			zajednice.add(set);
		}
		// System.out.println(setOfSets);
		List<String> ispis = new ArrayList<>();
		for (Set<Integer> set : zajednice) {
			List<Integer> sortedList = new ArrayList<>(set);
			Collections.sort(sortedList);
			StringBuilder sb = new StringBuilder();
			for (int cvor : sortedList) {
				sb.append(cvor).append("-");
			}
			ispis.add(sb.substring(0, sb.length() - 1));
		}

		Collections.sort(ispis, new LengthStringComparator());
		StringBuilder sb = new StringBuilder();
		for (String s : ispis) {
			sb.append(s).append(" ");
		}
		System.out.println(sb.substring(0, sb.length() - 1));

	}

	static class LengthStringComparator implements Comparator<String> {
		@Override
		public int compare(String str1, String str2) {
			int lengthComparison = Integer.compare(str1.length(), str2.length());

			if (lengthComparison != 0) {
				// If lengths are different, return the result of length comparison
				return lengthComparison;
			} else {
				// If lengths are the same, compare the strings lexicographically
				return str1.compareTo(str2);
			}
		}
	}

}
