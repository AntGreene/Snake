package com.example.snake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Random;

public class Snake extends Application {
    private Random r = new Random();
    private int counter = 3;
    private Rectangle[] rect = new Rectangle[100];
    private Rectangle food;
    private int[] xc = new int[100];
    private int[] yc = new int[100];
    private int dir = 3;
    private Pane root = new Pane();
    private boolean isDone = false;
    private int grow = 0;
    private int[] foodloc, headloc;
    private int score = 0;

    private Rectangle initRect() {
        Rectangle res = new Rectangle(45, 45);
        res.setFill(Color.GREEN);
        res.setStroke(Color.BLACK);
        res.setVisible(false);
        return res;
    }

    public void start(Stage primaryStage) {
        isDone = false;
        startGame(primaryStage);
    }

    private void startGame(Stage primaryStage) {
        headloc = new int[]{xc[0], yc[0]};
        Thread game;
        food = initRect();
        food.setStroke(Color.RED);
        food.setVisible(true);
        food.setTranslateX(r.nextInt(10) * 50);
        food.setTranslateY(r.nextInt(10) * 50);
        root.getChildren().add(food);
        Scene scene = new Scene(root, 514, 543);
        for (int i = 0; i < 3; i++) {
            rect[i] = initRect();
            rect[i].setTranslateX(50 + 50 * i);
            rect[i].setTranslateY(50 + 50);
            rect[i].setVisible(true);
            root.getChildren().add(rect[i]);
        }
        for (int i = 3; i < 100; i++) {
            rect[i] = initRect();
            rect[i].setTranslateX(50 + 50 * i);
            rect[i].setTranslateY(50 + 50);
            root.getChildren().add(rect[i]);
        }
        rect[0].setFill(Color.RED);
        game = new Thread(() -> {
            while (!isDone) {
                move(primaryStage);
                try {
                    Thread.sleep(200);
                } catch (Exception ex) {
                }
            }
        });
        game.start();
        scene.setOnKeyPressed(event -> {
            javafx.scene.input.KeyCode k = event.getCode();
            switch (k) {
                case D:
                    if (dir != 1 && ((dir == 2 || dir == 0) && headloc[1] != yc[0])) dir = 3;
                    break;
                case A:
                    if (dir != 3 && ((dir == 2 || dir == 0) && headloc[1] != yc[0])) dir = 1;
                    break;
                case S:
                    if (dir != 2 && ((dir == 3 || dir == 1) && headloc[0] != xc[0])) dir = 0;
                    break;
                case W:
                    if (dir != 0 && ((dir == 3 || dir == 1) && headloc[0] != xc[0])) dir = 2;
                    break;
            }
            headloc = new int[]{xc[0], yc[0]};
        });
        primaryStage.setTitle("Snake");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(543);
        primaryStage.setMinWidth(514);
        primaryStage.setMaxHeight(543);
        primaryStage.setMaxWidth(514);
        primaryStage.setOnCloseRequest(event -> isDone = true);
        primaryStage.show();
    }

    private void move(Stage primaryStage) {
        int[][] tmp = {{xc[1], yc[1]}, {}};
        xc[1] = xc[0];
        yc[1] = yc[0];
        for (int i = 2; i < counter; i++) {
            tmp[1] = new int[]{xc[i], yc[i]};
            xc[i] = tmp[0][0];
            yc[i] = tmp[0][1];
            tmp[0] = tmp[1];
            if (grow > 0 && xc[counter - 1] == foodloc[0] && yc[counter - 1] == foodloc[1]) {
                rect[counter - 1].setVisible(true);
                --grow;
            }
        }
        switch (dir) {
            case 0:
                if (yc[0] + 50 <= 450) yc[0] += 50;
                else yc[0] = 0;
                break;
            case 1:
                if (xc[0] - 50 >= 0) xc[0] -= 50;
                else xc[0] = 450;
                break;
            case 2:
                if (yc[0] - 50 >= 0) yc[0] -= 50;
                else yc[0] = 450;
                break;
            case 3:
                if (xc[0] + 50 <= 450) xc[0] += 50;
                else xc[0] = 0;
                break;
        }
        if (intersects(xc[0], yc[0])) {
            System.out.print("GAME OVER");
            isDone = true;
            Platform.runLater(() -> gameOver(primaryStage, counter)); // Run on FX Application Thread
        } else {
            for (int i = 0; i < counter; i++) {
                rect[i].setTranslateX(xc[i]);
                rect[i].setTranslateY(yc[i]);
            }
            if (rect[0].getBoundsInParent().intersects(food.getBoundsInParent())) {
                boolean isChanged = false;
                foodloc = new int[]{(int) food.getTranslateX(), (int) food.getTranslateY()};
                while (!isChanged) {
                    food.setTranslateX(r.nextInt(10) * 50);
                    food.setTranslateY(r.nextInt(10) * 50);
                    if (intersects(foodloc[0], foodloc[1]) && (int) food.getTranslateX() != foodloc[0] || (int) food.getTranslateY() != foodloc[1])
                        isChanged = true;
                }
                xc[counter] = (int) food.getTranslateX();
                yc[counter] = (int) food.getTranslateY();
                rect[counter].setTranslateX(rect[counter - 1].getX());
                rect[counter].setTranslateY(rect[counter - 1].getY());
                ++counter;
                ++grow;
                System.out.println(counter + "");
            }
        }
    }

    public boolean intersects(int x, int y) {
        int i = 0;
        for (Rectangle part : rect) {
            if (part != rect[0] && i > 0 && part.isVisible() && x == xc[i] && y == yc[i]) {
                System.out.println(i);
                return true;
            }
            i++;
        }
        return false;
    }

    private void gameOver(Stage primaryStage, int counter) {
        GameOver gameOverScreen = new GameOver(primaryStage, counter);
        gameOverScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class GameOver extends Stage {
    public GameOver(Stage primaryStage, int counter) {
        VBox vbox = new VBox();
        Label gameOverLabel = new Label("Game Over!");
        Label scoreLabel = new Label("Your Score: " + counter);
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> {
            primaryStage.close();
            new Snake().start(new Stage());
        });
        vbox.getChildren().addAll(gameOverLabel, scoreLabel, restartButton);
        Scene scene = new Scene(vbox, 200, 150);
        setScene(scene);
        setTitle("Game Over");
    }
}