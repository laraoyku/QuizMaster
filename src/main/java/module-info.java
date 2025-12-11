module com.example.quizmaster {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.quizmaster to javafx.fxml;
    exports com.example.quizmaster;
}