import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String input = "";
        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get(args[0]),
                             StandardCharsets.US_ASCII)) {
            // change Path to args[0] to get the name from the command line args, exact path only for dev build
            String line = null;
            while ((line = reader.readLine()) != null) {
                input += line;
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        List<String> sentences = Arrays.asList(input.split("[!.?]"));
        int sentencesAmount = sentences.size();
        int charactersAmount = input.replaceAll(" ", "").length();

        String[] wordsSentence = input.split(" ");
        int wordsAmount = wordsSentence.length;
        int[] syllables = syllablesAmount(wordsSentence);


        printOutcome(sentencesAmount, charactersAmount, wordsAmount, syllables[0], syllables[1],
                calcAutomatedReadabilityIndex(sentencesAmount, charactersAmount, wordsAmount),
                calcFleschKincaid(sentencesAmount, syllables[0], wordsAmount),
                calcSMOG(sentencesAmount, syllables[1]),
                calcColemanLiau(charactersAmount, wordsAmount, sentencesAmount));
    }


    public static int[] syllablesAmount(String[] words) {
        int counter = 0;
        int counterPollysylables = 0;
        for (String el :
                words ) {
            int count = calculateSyllables(el);
            counter += count;
            if (count >= 3) counterPollysylables++;
        }
        return new int[]{
                counter,
                counterPollysylables
        };
    }

    public static int calculateSyllables(String word) {
        word = word.toLowerCase().replaceAll("[!.?\\s]", "");
        String[] wordArr = word.split("");

        int count = 0;
        boolean isPreviousVowel = false;
        for (String s : wordArr) {
            boolean isVowel = s.matches("[aeiouy]");
            if (isVowel && !isPreviousVowel) {
                count++;
                isPreviousVowel = true;
            } else if(!isVowel){
                isPreviousVowel = false;
            }
        }
        if (word.charAt(word.length() - 1) == 'e') {
            count--;
        }
        if (count == 0) count = 1;

        return count;
    }



    public static double calcAutomatedReadabilityIndex(int sentences, int characters, int words) {
        return 4.71 * characters / words + 0.5 * words / sentences - 21.43;
    }
    public static double calcFleschKincaid(int sentences, int syllables, int words) {
        return 0.39 * words / sentences + 11.8 * syllables / words - 15.59;
    }
    public static double calcSMOG(int sentences, int polysyllables) {
        return 1.043 * Math.sqrt(polysyllables * 30.0 / sentences) + 3.1291;
    }
    public static double calcColemanLiau(int characters, int words, int sentences) {
        double L = (double) characters / words * 100;
        double S = (double) sentences / words * 100;

        return 0.0588 * L - 0.296 * S - 15.8;
    }

    public static void printOutcome(int sentences, int characters, int words, int syllables, int polysyllables,
                                    double score,
                                    double scoreFlesch, double scoreSMOG, double scoreColeman) {
        int roundScore = (int) Math.floor(score);
        int roundscoreFlesch = (int) Math.floor(scoreFlesch);
        int roundscoreSMOG = (int) Math.floor(scoreSMOG);
        int roundscoreColeman = (int) Math.floor(scoreColeman);

        int[] scores = new int[] {
                roundScore,
                roundscoreFlesch,
                roundscoreSMOG,
                roundscoreColeman
        };

        int[] index = new int[4];

        for (int i = 0; i < 4; i++) {
            switch (scores[i]) {
                case 1:
                    index[i] = 6;
                    break;
                case 2:
                    index[i] = 7;
                    break;
                case 3:
                    index[i] = 9;
                    break;
                case 4:
                    index[i] = 10;
                    break;
                case 5:
                    index[i] = 11;
                    break;
                case 6:
                    index[i] = 12;
                    break;
                case 7:
                    index[i] = 13;
                    break;
                case 8:
                    index[i] = 14;
                    break;
                case 9:
                    index[i] = 15;
                    break;
                case 10:
                    index[i] = 16;
                    break;
                case 11:
                    index[i] = 17;
                    break;
                case 12:
                    index[i] = 18;
                    break;
                case 13:
                    index[i] = 24;
                    break;
            }
        }
        double average = (index[0] + index[1] + index[2] + index[3]) / 4;


        System.out.println("Words: " + words);
        System.out.println("Sentences: " + sentences);
        System.out.println("Characters: " + characters);
        System.out.println("Syllables: " + syllables);
        System.out.println("Polysyllables: " + polysyllables);
        System.out.println();
        System.out.printf("Automated Readability Index: %.2f (about %d-year-olds.) \n", score, index[0]);
        System.out.printf("Flesch–Kincaid readability tests: %.2f  (about %d-year-olds.) \n", scoreFlesch, index[1]);
        System.out.printf("Simple Measure of Gobbledygook: %.2f  (about %d-year-olds.) \n", scoreSMOG, index[2]);
        System.out.printf("Coleman–Liau index: %.2f  (about %d-year-olds.) \n", scoreColeman, index[3]);
        System.out.println();
        System.out.println("This text should be understood in average by " + average + "-year-olds.");
    }
}