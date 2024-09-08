package logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Librarian {
    private double greenBonus = 0.3; //0.3 3.719
    private List<String> possibleAnswers = new ArrayList<String>();
    private List<String> possibleGuesses = new ArrayList<String>();
    private List<Character> takenLetters = new ArrayList<>();
    private Map<String, Double> relations = new HashMap<String, Double>();

    public Librarian(String guessFile, String answerFile) {
        try {
            this.possibleGuesses = Files.readAllLines(Path.of(guessFile));
            this.possibleAnswers = Files.readAllLines(Path.of(answerFile));
        } catch (IOException e) {
            System.out.println("One of the files couldn't be found");
        }
    }

    public String getRecommendation() {
        createRelations();

        String recommendation = mostRelations(possibleGuesses);
        double averageGuesses = (possibleAnswers.size()+1)/2;

        if (averageGuesses > relations.get(recommendation)){
            recommendation = mostRelations(possibleAnswers);
        }

        addTakenLetters(recommendation);

        if (relations.get(recommendation)== 0 || possibleAnswers.size() == 2){//redundant?
            return possibleAnswers.get(0);
        }
        return recommendation;
    }

    private void createRelations(){
        for (String word:possibleGuesses){
            relations.remove(word);
            relations.put(word, 0.0);
        }

        for (String subject:possibleGuesses){
            for (String target:possibleAnswers){
                boolean related = false;
                for (Character c:target.toCharArray()){
                    if (!takenLetters.contains(c) && subject.contains(String.valueOf(c))){
                        related = true;
                        if (target.charAt(subject.indexOf(c)) == c){//if the target contains the desired letter in the same space
                            relations.put(subject,relations.get(subject)+greenBonus); //add a lil bonus :)
                        }

                    }
                }
                if (related){
                    relations.put(subject,relations.get(subject)+1);}
            }

        }
    }

    private String mostRelations(List<String> guesses){
        String recommendation = guesses.get(0);
        double maxRelations = relations.get(recommendation);

        for (String word:guesses){
            if (relations.get(word) > maxRelations){
                maxRelations = relations.get(word);
                recommendation = word;
            }
        }
        return recommendation;
    }

    public void addTakenLetters(String recommendation){
        for (int i = 0;i<recommendation.length();i++){
            if (!takenLetters.contains(recommendation.charAt(i))){
                takenLetters.add(recommendation.charAt(i));
            }
        }
    }

    public void writeNewEdition(String guess, String result) {

        if (result.length() == guess.length()) {

            for (int i = 0; i < guess.length(); i++) {char letter = guess.charAt(i);

                int desiredLetterCount = 0;
                for (int j = 0;j < guess.length();j++){
                    if (guess.charAt(j) == letter && result.charAt(j) != '0'){desiredLetterCount++;}
                }

                List<String> pACopy = new ArrayList<>(possibleAnswers);//avoids concurrent modification of possibleAnswers
                for (String word: pACopy){
                    switch (result.charAt(i)) {
                        case '1':
                            if (word.charAt(i) == letter || !word.contains(String.valueOf(letter))) {
                                possibleAnswers.remove(word);}
                            break;
                        case '2':
                            if (word.charAt(i) != letter){possibleAnswers.remove(word);}
                            break;

                        case '0':
                            if (desiredLetterCount > 0){
                                int letterCount = 0;
                                for (int j = 0;j < word.length();j++){
                                    if (word.charAt(j) == letter){letterCount++;}
                                }
                                if (letterCount != desiredLetterCount){possibleAnswers.remove(word);}

                            }else if (word.contains(String.valueOf(letter))){
                                possibleAnswers.remove(word);}
                            break;
                        default:
                            System.out.println("Invalid code");
                    }
                }
            }
        }
    }

    public String getGuessResult(String guess, String target){
        String[] output = new String[guess.length()];
        String[] taken = new String[guess.length()];

        for (int i = 0;i<output.length;i++){output[i] = "0";taken[i] = "0";}

        for (int i = 0;i<guess.length();i++){
            if (guess.charAt(i) == target.charAt(i)){
                output[i] = "2";taken[i] = "2";
            }
        }

        for (int i = 0;i<guess.length();i++){char letter = guess.charAt(i);
            if (letter != target.charAt(i) && target.contains(String.valueOf(letter))){
                boolean valid = false;
                for (int j = 0; j < guess.length();j++){
                    if (taken[j] == "0" && target.charAt(j) == letter){valid = true;}
                    if (valid){output[i] = "1";}
                }
            }
        }
        return String.join("",output);
    }

    public List<String> getPossibleGuesses() {
        return possibleGuesses;
    }
    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

}
