package co.ryred.mail_helper.enigma;

import java.util.Arrays;

/**
 * Created by Cory Redmond on 13/04/2017.
 * ace@ac3-servers.eu
 */
public class Enigma {

    /**
     * Process a sequence of encryptions and decryptions, as specified in the
     * input from the standard input. Print the results on the standard output.
     * Exits normally if there are no errors in the input; otherwise with code
     * 1.
     */
    private static Reflector reflectorB;
    private static Reflector reflectorC;
    private static Rotor rotor1;
    private static Rotor rotor2;
    private static Rotor rotor3;
    private static Rotor rotor4;
    private static Rotor rotor5;
    private static Rotor rotor6;
    private static Rotor rotor7;
    private static Rotor rotor8;

    public static String crypt(String config, String line) {
        Machine M = new Machine();

        buildRotors();
        configure(M, config);
        return M.convert(standardize(line));

    }

    /**
     * Return true iff LINE is an Enigma configuration line.
     */
    static boolean isConfigurationLine(String line) {
        String[] s = line.split(" ");
        for (String a : s) {
            a = a.trim();
        }
        if (!s[0].equals("*")) {
            return false;
        }
        if (!s[1].equals("B") && !s[1].equals("C")) {
            return false;
        }
        if (!(Arrays.asList(rotorList).contains(s[2])
                && Arrays.asList(rotorList).contains(s[3]) && Arrays.asList(
                rotorList).contains(s[4]))) {
            return false;
        }
        if (s[2].equals(s[3]) || s[2].equals(s[4]) || s[3].equals(s[4])) {
            return false;
        }
        char[] init = s[5].toCharArray();
        if (!isUpperLetter(init[0]) || !isUpperLetter(init[1])
                || !isUpperLetter(init[2]) || !isUpperLetter(init[3])) {
            return false;
        }
        return true;
    }

    private static boolean isUpperLetter(char c) {
        if (((int) c > 90) || ((int) c < 65)) {
            return false;
        }
        return true;
    }

    /**
     * Configure M according to the specification given on CONFIG, which must
     * have the format specified in the assignment.
     */
    private static void configure(Machine M, String config) {
        String[] cArray = config.split(" ");
        M.setRotors(getReflectorFromString(cArray[1]),
                getRotorFromString(cArray[2]), getRotorFromString(cArray[3]),
                getRotorFromString(cArray[4]));
        M.setPositions(cArray[5]);

    }

    private static Reflector getReflectorFromString(String s) {
        Reflector r;
        if (s.equals("B")) {
            r = reflectorB;
        } else if (s.equals("C")) {
            r = reflectorC;
        } else {
            return null;
        }
        return r;
    }

    /**
     * returns the numbered rotor from the roman numerals.
     *
     * @param s roman numeral string
     * @returns the Rotor
     */
    private static Rotor getRotorFromString(String s) {
        Rotor r;
        if (s.equals("I")) {
            r = rotor1;
        } else if (s.equals("II")) {
            r = rotor2;
        } else if (s.equals("III")) {
            r = rotor3;
        } else if (s.equals("IV")) {
            r = rotor4;
        } else if (s.equals("V")) {
            r = rotor5;
        } else if (s.equals("VI")) {
            r = rotor6;
        } else if (s.equals("VII")) {
            r = rotor7;
        } else if (s.equals("VIII")) {
            r = rotor8;
        } else {
            return null;
        }
        return r;
    }

    /**
     * Return the result of converting LINE to all upper case, removing all
     * blanks. It is an error if LINE contains characters other than letters and
     * blanks.
     */
    private static String standardize(String line) {
        line = line.toUpperCase();
        line = line.replace(' ', '_');
        char[] cArray = line.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : cArray) {
            if (Character.getType(c) != Character.UPPERCASE_LETTER && c != '_') {
                System.out.println("Invalid character: " + (int) c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Print MSG in groups of five (except that the last group may have fewer
     * letters).
     */
    static void printMessageLine(String msg) {
        int i = 0;
        for (char c : msg.toCharArray()) {
            if (i == 5) {
                System.out.print(" ");
                i = 0;

            }
            System.out.print(c);
            i++;
        }
        System.out.println();
    }

    static String[] rotorList = new String[] { "I", "II", "III", "IV", "V", "VI", "VII", "VIII" };

    /** Create all the necessary rotors. */
    static void buildRotors() {
        rotor1 = Rotor.rotor(
                "E K M F L G D Q V Z N T O W Y H X U S P A I B R C J", "Q");
        rotor2 = Rotor.rotor(
                "A J D K S I R U X B L H W T M C Q G Z N P Y F V O E", "E");
        rotor3 = Rotor.rotor(
                "B D F H J L C P R T X V Z N Y E I W G A K M U S Q O", "V");
        rotor4 = Rotor.rotor(
                "E S O V P Z J A Y Q U I R H X L N F T G K D C M W B", "J");
        rotor5 = Rotor.rotor(
                "V Z B R G I T Y U P S D N H L X A W M J Q O F E C K", "Z");
        rotor6 = Rotor.rotor(
                "J P G V O U M F Y Q B E N H Z R D K A S X L I C T W",
                "Z and M");
        rotor7 = Rotor.rotor(
                "N Z J H G R C X M Y S W B O U F A I V L P E K Q D T",
                "Z and M");
        rotor8 = Rotor.rotor(
                "F K Q H T L X O C B J S P D Z R A M E W N I U Y G V",
                "Z and M");
        reflectorB = Reflector
                .make("Y R U H Q S L D P X N G O K M I E B F Z C W V J A T");
        reflectorC = Reflector
                .make("F V P J I A O Y E D R Z X W G C T K U Q S B N M H L");
    }

}
