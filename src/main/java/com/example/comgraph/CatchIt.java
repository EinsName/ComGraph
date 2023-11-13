package com.example.comgraph;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class CatchIt extends Application {
    private Canvas canvas;

    private int playerPosX;
    private int playerPosY;
    private int score = 0;

    private int dropPosY = 73;
    private boolean dropOnField = false;
    private int randColor;
    int randPosDrop;
    private boolean collision = false;

    private boolean gameOver = false;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        playerPosX = 60;
        playerPosY = 450;

        canvas = new Canvas(500, 500);
        draw(canvas.getGraphicsContext2D());

        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                //player shouldn't be able to move out of bounds
                case A:
                    if(playerPosX >= 50){
                        playerPosX -= 5;
                        break;
                    }
                case D:
                    if((playerPosX+20) <= 449){
                        playerPosX += 5;
                        break;
                    }
                case ESCAPE:
                    System.exit(0);

            }
            draw(canvas.getGraphicsContext2D());
        });


        //draw update for moving the drop
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        event -> draw(canvas.getGraphicsContext2D()))
        );
        timeline.play();

        //Timeline for drop spawn
        Timeline timelineDrop = new Timeline();
        timelineDrop.setCycleCount(Animation.INDEFINITE);
        timelineDrop.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        event -> spawnDrop(canvas.getGraphicsContext2D()))
        );
        timelineDrop.play();

        //collision detection loop
        Timeline timelineColl = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        event -> checkCollision())
        );
        timeline.play();

        //setup stage
        primaryStage.setTitle("Catch it or don't");
        primaryStage.setScene(new Scene(new Group(canvas)));
        primaryStage.show();
        canvas.requestFocus();

    }



    private void draw(GraphicsContext context) {

        //clear Display -> important for update the position of rectangles and Scoreboard
        context.setFill(Color.WHITE);
        context.fillRect(0,0,500,500);

        context.setStroke(Color.BLACK);
        context.setFill(Color.BLUE);

        context.fillRect(playerPosX,playerPosY,20,20);


        //draw path for game border
        int xPoints[] = {50,450,450,50};
        int yPoints[] = {70,70,470,470};
        context.beginPath();
        context.moveTo(xPoints[0], yPoints[0]);
        for(int i = 0; i < xPoints.length; i++){
            context.lineTo(xPoints[i], yPoints[i]);
        }
        context.closePath();
        context.stroke();


        //draw Score String
        context.setFill(Color.RED);
        context.setFont(new Font("font", 30));
        context.fillText("Score: " + score, 50,50);

        context.setFill(Color.BLACK);

        //update y-Position of the drop
        if(dropOnField) {
            if(randColor <= 5){
                context.setFill(Color.RED);     //for right update color
            }else if(randColor > 5){
                context.setFill(Color.GREEN);
            }
            //context.clearRect(randPosDrop, dropPosY - 1, 20, 20);
            dropPosY +=4;
            context.fillRect(randPosDrop, dropPosY, 20, 20);
        }

        //if there is a collision the drop "despawns" and the y Position is reseted for the new drop
        if(collision){
            dropOnField = false;
            context.clearRect(randPosDrop,dropPosY,20,20);
            dropPosY = 73;
        }

        if(gameOver){
            context.setFill(Color.WHITE);
            context.fillRect(0,0,500,500);
            context.setFill(Color.RED);
            context.setFont(new Font("font", 40));
            context.fillText("GAME OVER" , 130,230);
        }
    }


    //method for drop spawn
    public void spawnDrop(GraphicsContext context){
        //limited to one drop at a time, drop is spawned when there isn't one alrdy
        if(!dropOnField){
            dropOnField = true;
            Random rand = new Random();
            randColor = rand.nextInt(10)+1;             //rand Number for Drop Color Red(1,2,3,4,5); Green(6,7,8,9,10)
            randPosDrop = rand.nextInt(429-51+1)+51;    //random Drop Position on x-Coordinate
            if(randColor <= 5){
                context.setFill(Color.RED);
                context.fillRect(randPosDrop,dropPosY,20,20);
            }else if(randColor > 5) {
                context.setFill(Color.GREEN);
                context.fillRect(randPosDrop,dropPosY,20,20);
            }
        }

    }

    
    private void drawGameOverText(GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.fillRect(0,0,500,500);
        context.setFill(Color.RED);
        context.setFont(new Font("font", 30));
        context.fillText("GAME OVER", 100,250);
    }

    //collision detection between player and drop
    public void checkCollision() {

        int yColl = playerPosY - (dropPosY + 20);                   //Measure for distance between rectangles on Y coordinate
        int xColl = Math.abs((playerPosX+20) - (randPosDrop+20));   //Measure for distance between the rectangles on X coordinate

        if (dropPosY + 20 >= 470) {                                 //if drop hits ground
            collision = true;
        } else if ((yColl <= 1) && (xColl <= 20)) {                 //collision if rectangles are to close to each other on Y AND X coordinate
            collision = true;
            if(randColor > 5){
                score++;
            }else if(randColor <= 5){
                gameOver = true;
            }
        } else {
            collision = false;
        }
    }




}