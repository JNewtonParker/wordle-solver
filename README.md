# WordleSolver
Object Oriented Solution to Wordle
-
I made this soon after Wordle came out over a few versions. Only the final version has been uploaded. It allows two separate text files to be assigned in the central class (WordleBot). One of these is the possible guesses and the other is the possible answers, the files included in this repository were those in the official Wordle site as of 29/01/2022.

There are three separate options for how the code can be run. 

1. Live. The program will calculate and display a recommended word based on the information gained by those previous. Information is written in the console as a five digit string, with 2 representing green, 1 representing yellow and 0 representing grey. This will repeat until there are no possible guesses left, or there is only one possible answer. (Use this one to play the game)

2. Simulation. Enter a target word, and the program will display the path it would've taken to find the solution. It will display each of these in the order of guessing.

3. Analysis. This tests how long it would take to find any of the words in the target guess file. It displays related information after. A screenshot of the results is included in this repository.

(p.s. the first guess is SOARE)
