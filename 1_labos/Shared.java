import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

// oba zadatka trebaju simhash pa je to u shared
// zadatak 1.1
public class Shared {

	public static final int DULJINA_SAZETKA = 128;

	public static void main(String[] args) {
		//String fer = "fakultet elektrotehnike i racunarstva";
		//System.out.println(SimHash(fer));
	}

	public static String SimHash(String tekst) {
		Set<String> n_grams = generiraj_jedinke(tekst, 1);
		int[] sh = new int[DULJINA_SAZETKA];

		for (String n_gram : n_grams) {
			byte[] sazetak = DigestUtils.md5(n_gram);
			for (int i = 0; i < sazetak.length; i++) {
				byte bajt = sazetak[i];

				for (int j = 0; j < 8; j++) { // za svaki bit u bajtu
					int index = i * 8 + j;
					if (bitNaPoziciji(bajt, j) == 1) {
						sh[index] += 1;
					} else {
						sh[index] -= 1;
					}
				}
			}
		}

		for (int i = 0; i < sh.length; i++) {
			if (sh[i] >= 0) {
				sh[i] = 1;
			} else {
				sh[i] = 0;
			}
		}

		StringBuilder shString = new StringBuilder();

		for (int element: sh) {
			shString.append(element);
		}
		// System.out.println(shString.toString());
		//return pretvoriNizBitovauHex(shString.toString());
		return shString.toString();
	}

	private static Set<String> generiraj_jedinke(String text, int n) {
		Set<String> n_grams = new HashSet<>();
		String[] text_split = text.split(" ");
		for (int i = 0; i < text_split.length; i++) {
			String s = "";
			for (int j = 0; j < n; j++) {
				if (i + n <= text_split.length) {
					s += text_split[i + j];
					if (j < n - 1) {
						s += " ";
					}
				}
			}
			if (s != "") {
				n_grams.add(s);
			}
		}
		return n_grams;
	}

	private static int bitNaPoziciji(byte bajt, int index) {
		// https://stackoverflow.com/questions/9354860/how-to-get-the-value-of-a-bit-at-a-certain-position-from-a-byte
		return bajt >> (8 - index - 1) & 0x0001;
	}

	private static String pretvoriNizBitovauHex(String bitovi) {
		String hex = "";

		for (int i = 0; i < bitovi.length(); i += 4) {
			String bin_string = bitovi.substring(i, i + 4);
			// System.out.println(bin_string);
			int number = 0;
			for (int j = 0; j <= 3; j++) {
				int bit = Integer.parseInt(bin_string.substring(j, j + 1));
				if (j == 3) {
					if (bit == 1) {
						number += 1;
					}
				} else {
					number += Math.pow(2 * bit, 3 - j);
				}
			}
			// System.out.println("\t" + number);
			switch (number) {
			case 15:
				hex += "f";
				break;
			case 14:
				hex += "e";
				break;
			case 13:
				hex += "d";
				break;
			case 12:
				hex += "c";
				break;
			case 11:
				hex += "b";
				break;
			case 10:
				hex += "a";
				break;
			default:
				hex += "" + number;
				break;
			}
		}
		return hex;
	}

	public static boolean HammingovaUdaljenostManjaOdK(String s1, String s2, int K) {
		if (s1.length() != s2.length()) {
			return false;
		} else {
			int rezultat = 0;

			for (int i = 0; i < s1.length(); i++) {
				if (s1.charAt(i) != s2.charAt(i))
					rezultat += 1;
					if(rezultat > K) {
						return false;
					}
			}
			if(rezultat <= K) {
				return true;
			}
			return false;
		}
	}

	public static int brojUdaljenihZaManjeOdK(String[] sazetci, String sazetak_I, int K) {
		int rezultat = 0;
		for (int i = 0; i < sazetci.length; i++) {
			if (HammingovaUdaljenostManjaOdK(sazetak_I, sazetci[i], K)) {
				// System.out.println("Sazetak I = " + sazetak_I);
				// System.out.println("Satetak drugi = " + sazetci[i]);
				// System.out.println(razlika);
				rezultat += 1;
			}
		}
		return rezultat - 1; // da ne usporedujemo sam sa sobom, bolje nego da svaki put usporedujemo i != I
	}
}