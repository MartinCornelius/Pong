package PongGame;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

import static javafx.geometry.Pos.CENTER;

public class Pong extends Application {

    // GAME
    int gameWidth = 1000;
    int gameHeight = 400;
    AnimationTimer gameTimer;
    Line seperatorLine;
    boolean gameover;
    Text botScoreText;
    Text playerScoreText;
    Text gameoverText;

    // PLAYER
    Rectangle player;
    int playerScore = 0;

    // BOT
    Rectangle bot;
    int botY = 3;
    int botX = 3;
    int botScore = 0;

    // BALL
    Circle ball;
    int ballX = 3;
    int ballY = 3;

    @Override
    public void start(Stage gameStage) throws Exception {
        // Game Setup
        gameStage.setTitle("Pong");
        Pane gameCanvas = new Pane();
        Scene gameScene = new Scene(gameCanvas, gameWidth, gameHeight);
        gameCanvas.setStyle("-fx-background-color: black;");

        // Draw line in the middle
        seperatorLine = new Line(gameWidth / 2, 0, gameWidth / 2, gameHeight);
        seperatorLine.setStroke(Color.WHITE);

        // Draw scores
        drawScore(gameCanvas);

        // Player paddle
        player = new Rectangle(10, 80, Color.WHITE);
        player.setLayoutX(0);
        player.setLayoutY(gameHeight / 2 - 40);

        // Player Controls
        gameScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                    player.setLayoutY(player.getLayoutY() - 30);
                    break;
                case DOWN:
                    player.setLayoutY(player.getLayoutY() + 30);
                    break;
            }
        });

        // Bot paddle
        bot = new Rectangle(10, 80, Color.WHITE);
        bot.setLayoutX(gameWidth - 10);
        bot.setLayoutY(gameHeight / 2 - 40);

        // Ball
        ball = new Circle(5);
        ball.setFill(Color.WHITE);
        ball.setLayoutX(gameWidth / 2);
        ball.setLayoutY(gameHeight / 2);

        // Add to gameCanvas
        gameCanvas.getChildren().add(seperatorLine);
        gameCanvas.getChildren().add(player);
        gameCanvas.getChildren().add(bot);
        gameCanvas.getChildren().add(ball);

        // Game Loop
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update(gameCanvas);
            }
        };
        gameTimer.start();

        // Set scene and show window
        gameStage.setScene(gameScene);
        gameStage.show();
    }

    private void update(Pane gameCanvas) {
        // Check if gameover
        if (gameover) {
            ballX = 0;
            ballY = 0;
            ball.setLayoutX(gameWidth / 2);
            ball.setLayoutY(gameHeight / 2);
            gameCanvas.setOnMouseClicked(e -> {
                restartGame();
            });
        };

        // Get ball x and y
        int x = (int) ball.getLayoutX();
        int y = (int) ball.getLayoutY();

        // Check for boundaries
        if (y > gameHeight - 5) ballY *= -1;
        if (y < 5) ballY *= -1;

        // Player paddle boundaries
        if (player.getLayoutY() >= gameHeight - 80) player.setLayoutY(gameHeight - 80);
        if (player.getLayoutY() <= 0) player.setLayoutY(0);

        // Check for paddle hit
        if (ball.getBoundsInParent().intersects(player.getBoundsInParent())) ballX *= -1;
        if (ball.getBoundsInParent().intersects(bot.getBoundsInParent())) ballX *= -1;

        // Bot move
        bot.setLayoutY(y - 40);

        // Check for winner
        if (x > gameWidth + 3) gameover(gameCanvas, "Player");
        if (x < -3) gameover(gameCanvas, "Bot");

        // Move ball
        ball.setLayoutX(ball.getLayoutX() + ballX);
        ball.setLayoutY(ball.getLayoutY() + ballY);
    }

    private void drawScore(Pane gameCanvas) {
        // Draw player score
        playerScoreText = new Text(250, 50, String.valueOf(playerScore));
        playerScoreText.setStroke(Color.WHITE);
        playerScoreText.setFill(Color.WHITE);
        playerScoreText.setStyle("-fx-font-size: 24px;");

        // Draw bot score
        botScoreText = new Text(750, 50, String.valueOf(botScore));
        botScoreText.setStroke(Color.WHITE);
        botScoreText.setFill(Color.WHITE);
        botScoreText.setStyle("-fx-font-size: 24px;");

        // Add scores to gameCanvas
        gameCanvas.getChildren().addAll(playerScoreText, botScoreText);
    }

    private void updateScores() {
        playerScoreText.setText(String.valueOf(playerScore));
        botScoreText.setText(String.valueOf(botScore));
    }

    private void restartGame() {
        gameover = false;
        gameoverText.setText("");
        ballX = 3;
        ballY = 3;
    }

    private void gameover(Pane gameCanvas, String winner) {
        // Set gameover
        gameover = true;
        gameoverText = new Text(gameWidth / 2 - 95, gameHeight / 2 - 20, "Click to start");
        gameoverText.setStroke(Color.WHITE);
        gameoverText.setFill(Color.WHITE);
        gameoverText.setStyle("-fx-font-size: 32;");
        gameoverText.setTextAlignment(TextAlignment.CENTER);
        gameCanvas.getChildren().add(gameoverText);

        // Change and draw score
        if (winner.equals("Player")) {
            playerScore++;
            updateScores();
        }
        if (winner.equals("Bot")) {
            botScore++;
            updateScores();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
