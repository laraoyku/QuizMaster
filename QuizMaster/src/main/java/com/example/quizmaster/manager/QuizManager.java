package com.example.quizmaster.manager;

import com.example.quizmaster.model.Question;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;

public class QuizManager {

    private final List<Question> defaultQuestions = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();

    public QuizManager() {
        loadSampleQuestions();
    }

    private void loadSampleQuestions() {
        try {
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("com/example/quizmaster/questions.txt");

            if (inputStream == null) {
                System.out.println("questions.txt NOT found via classpath");
                return;
            }

            Scanner scanner = new Scanner(inputStream);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");

                if (parts.length < 3) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String questionText = parts[0];
                List<String> options = List.of(parts[1].split(","));
                int correctIndex = Integer.parseInt(parts[2]);

                defaultQuestions.add(new Question(questionText, options, correctIndex));

            }


            scanner.close();
            System.out.println("Questions loaded successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startQuiz() {

            if (questions.isEmpty()) {
                System.out.println("No questions available.");
                return;
            }

            Scanner scanner = new Scanner(System.in);

            boolean playAgain = true;

        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine().trim().toLowerCase();

        int score = 0;


        while (playAgain) {

                score = 0;

                Collections.shuffle(questions);

                for (Question q : questions) {
                    System.out.println(q.getQuestionText());

                    for (int i = 0; i < q.getOptions().size(); i++) {
                        System.out.println((i + 1) + ". " + q.getOptions().get(i));
                    }

                    int answer ;

                    while (true) {
                        System.out.print("Your answer: ");
                        if (scanner.hasNextInt()) {
                            answer = scanner.nextInt();
                            if (answer >= 1 && answer <= q.getOptions().size()) {
                                break;
                            }
                        } else {
                            scanner.next();
                        }
                        System.out.println("Please enter a number between 1 and " + q.getOptions().size());
                    }

                    if (answer - 1 == q.getCorrectIndex()) {
                        score++;
                        System.out.println("Correct!\n");
                    } else {
                        System.out.println("Wrong!");
                        System.out.println("Correct answer: "
                                + q.getOptions().get(q.getCorrectIndex()) + "\n");
                    }
                }

                System.out.println("Final score: " + score + "/" + questions.size());

            System.out.print("Do you want to play again? (y/n): ");
                String choice = scanner.next().toLowerCase();
                playAgain = choice.equals("y");
            }
        saveScore(playerName, score);
        showLeaderboard();

            System.out.println("Thanks for playing!");
        }
    public void createCustomQuiz() {
        new java.io.File(System.getProperty("user.dir") + "/quizzes").mkdirs();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Author name: ");
        String authorName = scanner.nextLine().trim();

        System.out.print("Quiz name: ");
        String quizName = scanner.nextLine().trim().toLowerCase();


        String filePath = System.getProperty("user.dir") + "/quizzes/" + quizName + ".txt";

        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write("#AUTHOR:" + authorName + "\n");


            System.out.print("How many questions? ");
            int count = scanner.nextInt();
            scanner.nextLine();

            for (int i = 1; i <= count; i++) {
                System.out.println("\nQuestion " + i + ":");
                System.out.print("Question text: ");
                String question = scanner.nextLine();

                System.out.print("Options (comma separated): ");
                String options = scanner.nextLine();

                System.out.print("Correct option number: ");
                int correct = scanner.nextInt();
                scanner.nextLine();

                writer.write(question + "|" + options + "|" + (correct - 1) + "\n");
            }

            System.out.println("Quiz saved as " + quizName + ".txt");

        } catch (Exception e) {
            System.out.println("Failed to create quiz.");
        }
    }


    private void saveScore(String name, int score) {
        try {
            String path = System.getProperty("user.dir") + "/scores.txt";
            java.io.File file = new java.io.File(path);

            List<String> updatedScores = new ArrayList<>();
            int bestScore = score;

            if (file.exists()) {
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");

                    if (parts.length < 2) continue;

                    String existingName = parts[0].trim().toLowerCase();
                    int existingScore = Integer.parseInt(parts[1]);

                    if (existingName.equals(name)) {
                        bestScore = Math.max(bestScore, existingScore);
                    } else {
                        updatedScores.add(existingName + "," + existingScore);
                    }
                }
                scanner.close();
            }

            updatedScores.add(name + "," + bestScore);

            java.io.FileWriter writer = new java.io.FileWriter(path, false);
            for (String entry : updatedScores) {
                writer.write(entry + "\n");
            }
            writer.close();

        } catch (Exception e) {
            System.out.println("Failed to save score.");
        }
    }


    private void showLeaderboard() {
            System.out.println("\n🏆 Leaderboard (Highest Score First)");

            List<String> entries = new ArrayList<>();

            try {
                String path = System.getProperty("user.dir") + "/scores.txt";
                Scanner fileScanner = new Scanner(new java.io.File(path));

                while (fileScanner.hasNextLine()) {
                    entries.add(fileScanner.nextLine());
                }
                fileScanner.close();

                entries.sort((a, b) -> {
                    int scoreA = Integer.parseInt(a.split(",")[1]);
                    int scoreB = Integer.parseInt(b.split(",")[1]);
                    return Integer.compare(scoreB, scoreA);
                });

                int rank = 1;
                for (String entry : entries) {
                    String[] parts = entry.split(",");
                    System.out.println(rank + ". " + parts[0] + " — " + parts[1]);
                    rank++;
                }

            } catch (Exception e) {
                System.out.println("No scores yet.");
            }
        }

    public void playCustomQuiz(String quizName) {

        questions.clear();

        String filePath = System.getProperty("user.dir") + "/quizzes/" + quizName + ".txt";

        try {
            java.io.File file = new java.io.File(filePath);

            if (!file.exists()) {
                System.out.println("Quiz not found: " + quizName);
                return;
            }

            Scanner scanner = new Scanner(file);
            String author = "Unknown";

            if (scanner.hasNextLine()) {
                String firstLine = scanner.nextLine().trim();
                if (firstLine.startsWith("#AUTHOR:")) {
                    author = firstLine.substring(8).trim();
                }

            }

            System.out.println("\n📘 Quiz by: " + author);
            System.out.println("---------------------------");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;

                String questionText = parts[0];
                List<String> options = List.of(parts[1].split(","));
                int correctIndex = Integer.parseInt(parts[2]);

                questions.add(new Question(questionText, options, correctIndex));
            }

            scanner.close();

            startQuiz();

        } catch (Exception e) {
            System.out.println("Failed to load quiz.");
        }
    }
    public void listSharedQuizzes() {
        java.io.File quizDir = new java.io.File("quizzes");

        if (!quizDir.exists() || !quizDir.isDirectory()) {
            System.out.println("No shared quizzes found.");
            return;
        }

        java.io.File[] files = quizDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No shared quizzes available.");
            return;
        }

        System.out.println("\nAvailable shared quizzes:");
        for (int i = 0; i < files.length; i++) {
            java.io.File file = files[i];
            String quizName = file.getName().replace(".txt", "");
            String author = "Unknown";

            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    String firstLine = scanner.nextLine().trim();
                    if (firstLine.startsWith("#AUTHOR:")) {
                        author = firstLine.replace("#AUTHOR:", "").trim();
                    }
                }
            } catch (Exception e) {
            }

            System.out.println((i + 1) + ". " + quizName + " — by " + author);
        }

    }
    public void playDefaultQuiz() {
        questions.clear();
        questions.addAll(defaultQuestions); // copy default quiz
        startQuiz();
    }





}

