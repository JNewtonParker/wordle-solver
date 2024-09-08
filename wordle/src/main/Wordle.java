package main;

import logic.Librarian;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class Wordle {
    private static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) {

        String guessFile = "data/WordleValidWords.txt";
        String answerFile = "data/WordleOfficialAnswers.txt";
        System.out.println("Live (1), Simulation (2) or Analysis (3)");

        switch(userInput.nextLine()){
            case "1":
                runLive(guessFile,answerFile);
                break;
            case "2":
                runSimulation(guessFile,answerFile);
                break;
            case "3":
                runAnalysis(guessFile,answerFile);
                break;
            default:
                System.out.println("Incorrect Input Format");
        }
    }

    public static void runLive(String guessFile, String answerFile) {
        Librarian librarian = new Librarian(guessFile,answerFile);

        if (librarian.getPossibleAnswers().size() > 0) {
            for (;;) {
                String guess = librarian.getRecommendation();
                System.out.println("Recommended guess: " + guess);
                String result = userInput.nextLine();
                librarian.writeNewEdition(guess, result);

                switch (librarian.getPossibleAnswers().size()) {
                    case 0:
                        System.out.println(("Check your answers! You made a mistake somewhere"));
                        break;
                    case 1:
                        System.out.println("The word is: " + librarian.getPossibleAnswers().get(0));
                        break;
                    default:
                        continue;
                }
                break;
            }
        }
    }
    public static void runSimulation(String guessFile, String answerFile) {
        Librarian librarian = new Librarian (guessFile, answerFile);

        if (librarian.getPossibleGuesses().size() > 0) {
            System.out.println("Enter target word:");
            String target = userInput.nextLine();

            if (librarian.getPossibleAnswers().contains(target)){
                int guessCount = 0;
                for (;;) {
                    guessCount++;
                    String guess = librarian.getRecommendation();System.out.println("Librarian guess: " + guess);
                    String result = librarian.getGuessResult(guess, target);System.out.println("Guess result: " + result + "\n");

                    librarian.writeNewEdition(guess, result);
                    if (librarian.getPossibleAnswers().size() == 1) {
                        System.out.println("The word is: " + librarian.getPossibleAnswers().get(0));

                        if (!guess.equals(target)) {
                            guessCount++;
                        }
                        System.out.println("Librarian needed " + guessCount + " guesses");
                    } else {
                        continue;
                    }
                    break;
                }

            }else{System.out.println("Word not in file");}

        }
    }

    public static void runAnalysis(String guessFile, String answerFile) {
        double startTime  = System.currentTimeMillis();
        Map<String, Integer> data = new HashMap<String,Integer>();
        Map<String, String> historyBook = new HashMap<String,String>();

        List<String> answersToCheck = new ArrayList<>();
        try {
            answersToCheck = Files.readAllLines(Path.of(answerFile));
            System.out.println("Analysing...");
        } catch (IOException e) {
            System.out.println("One of the files couldn't be found");
        }


        for (String target: answersToCheck){

            Librarian librarian = new Librarian(guessFile, answerFile);
            int guessCount = 0;
            String guess;String result;
            String history = "";
            for (; ; ) {

                if (historyBook.containsKey(history)) {//if the current path has already happened...
                    guess = historyBook.get(history);//...next guess is what happened last time and
                    librarian.addTakenLetters(guess); //add the used letters to TakenLetters in techDict
                }else{
                    guess = librarian.getRecommendation(); //otherwise, make the next guess
                    historyBook.put(history,guess);// and add the new guess as the result of the current path
                }

                guessCount++;//increment guess count
                result = librarian.getGuessResult(guess, target);//find result for new guess
                history += (guess+result); //update path for current history
                librarian.writeNewEdition(guess, result); //modify possible answer list

                if (librarian.getPossibleAnswers().size() == 1) {
                    if (!guess.equals(target)) {
                        guessCount++;
                    }
                } else {

                    continue;
                }
                break; //if they guessed the right answer break target loop


            }
            historyBook.put(history,target);
            data.put(target, guessCount);

        }
        double endTime  = System.currentTimeMillis();
        System.out.println(displayResults(answersToCheck, data, endTime-startTime));
    }
    private static String displayResults(List<String> answersChecked, Map<String, Integer> data, double timeElapsed) {
        String output = "\n------------------------------\n";
        String maxWord = answersChecked.get(0);
        int maxValue = data.get(maxWord);

        DecimalFormat df = new DecimalFormat("#.###");

        Map<Integer,Integer> guessBuckets = new HashMap<Integer,Integer>();

        double failed = 0;
        double avgNumGuesses = 0;
        for (String word:answersChecked){
            if (data.get(word) > maxValue){
                maxValue = data.get(word);
                maxWord = word;}

            if (guessBuckets.containsKey(data.get(word))){
                guessBuckets.put(data.get(word),guessBuckets.get(data.get(word))+1);
            }else{
                guessBuckets.put(data.get(word),1);
            }
            avgNumGuesses += data.get(word);
        }

        avgNumGuesses = avgNumGuesses/answersChecked.size();
        double failedPercentage = (failed/answersChecked.size())*100;

        output += "Longest Path: " + maxWord + ", " + maxValue + "\n";
        output += "Pass Rate: " + df.format(100-failedPercentage) + "%\n";
        output += "\n";
        output += "Avg. Guesses: " + df.format(avgNumGuesses) + "\n";
        output += "Time Elapsed: " + (timeElapsed)/1000 + " seconds.\n";
        output += "------------------------------\n";

        for (Integer bucket:guessBuckets.keySet()){
            output+= bucket + " Guesses: " + guessBuckets.get(bucket) + ". ";
        }

        output += "\n------------------------------\n";
        return output;
    }

}