package com.example.quizmaster.main;

import com.example.quizmaster.manager.QuizManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        QuizManager quizManager = new QuizManager();

        while (true) {
            System.out.println("=== QuizMaster ===");
            System.out.println("1. Play default quiz");
            System.out.println("2. Create your own quiz");
            System.out.println("3. List shared quizzes");
            System.out.println("4. Play shared quiz");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                quizManager.playDefaultQuiz();

            } else if (choice == 2) {
                quizManager.createCustomQuiz();

            } else if (choice == 3) {

                quizManager.listSharedQuizzes();

            } else if (choice == 4) {
                System.out.print("Enter quiz file name (without .txt): ");
                String quizName = scanner.nextLine().trim();
                quizManager.playCustomQuiz(quizName);

            } else if (choice == 5) {
                System.out.println("Goodbye!");
                break;

            } else {
                System.out.println("Invalid choice. Try again.");
            }


        }
    }
}
