package sample;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    Random rand=new Random();
    Main obj= new Main();
    double speed;                               //for increasing speed
    Integer score;		                        // player's score
    private List<Block> blocks;
    private List<Text>  blockText;
    Text Score;                             	// Score Board
    Snake snake=new Snake();
    int scoreTracker;                        // to keep track of score to increase length of snake
    boolean paused;
    Token T;
    Wall W;
    AnimationTimer A;
    boolean runningA;
    public Controller(){
        this.speed=0;
        this.score=0;
        this.scoreTracker=0;
        this.blocks=new ArrayList<Block>();
        this.blockText=new ArrayList<Text>();
        this.Score=new Text("Score : "+score.toString());
        this.paused=false;
        T=null;
    }

    protected Color colorPicker(){                  //Random Color Generator
        int toss= rand.nextInt(6);
        if(toss==0)
            return Color.rgb(128,255,255);       //Sky Blue
        else if(toss==1)
            return Color.rgb(255,153,221);       //Pink
        else if(toss==2)
            return Color.rgb(153,255,153);       //Light Green
        else if(toss==3)
            return Color.rgb(255,209,179);       //Orange
        else if(toss==4)
            return Color.rgb(255,102,102);      //Light Red
        else if(toss==5)
            return Color.CORAL;
        else
            return Color.YELLOW;
    }
    
    protected Color colorPicker_Wall() {
    	int toss=rand.nextInt(4);
    	if(toss==0) {
    		return Color.MEDIUMPURPLE;
    	}
    	else if(toss==1) {
    		return Color.LIGHTGRAY;
    	}
    	else if(toss==2) {
    		return Color.WHEAT;
    	}
    	else {
    		return Color.GRAY;
    	}  	
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    void moveText(Text text, double speed) {
        text.setTranslateY(text.getTranslateY() + 1 + speed);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public class blockAndText {                     //helper class for createBlocks to return shape of blocks
        private List<Block> blockShape;             //as well as block value
        private List<Text>  blockText;

        public blockAndText(List<Block> blockShape, List<Text> blockText) {
            this.blockShape = blockShape;
            this.blockText = blockText;
        }

        public List<Block> getShape() {
            return blockShape;
        }

        public List<Text> getText() {
            return blockText;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected blockAndText createBlocks(){                      //Random Blocks Creator
        List<Block> Blockslist=new ArrayList<Block>();
        List<Text>  BlockText =new ArrayList<Text>();
        for(int i=0;i<5;i++){
            int toss= rand.nextInt(3);
            if(toss==0 || toss ==1){
            	int valueofNewBlock;
            	
            	if(i==1) {													// for having at least one block with less value
            		valueofNewBlock= rand.nextInt(snake.getNumBalls())+1;
            	}
            	else {
            		valueofNewBlock= rand.nextInt(20)+1;
            	}
                
                Blockslist.add(new Block( (i+1)*5+i*100,-100,96,90,valueofNewBlock,colorPicker()));
                BlockText.add(new Text(String.valueOf(valueofNewBlock)));
                BlockText.get(BlockText.size()-1).setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 24));
                BlockText.get(BlockText.size()-1).setX((i+1)*5+i*100+40);
                BlockText.get(BlockText.size()-1).setY(-50);}
        }

        return new blockAndText(Blockslist,BlockText);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected void createToken(Pane play) {
    	int p=rand.nextInt(20);
    	if(p<2 && T==null) {
    		int code=rand.nextInt(8);
    		if(code<=4) {
    			T=new Ball("ball","file:ball.png");   			
    		}
    		else if(code==5) {
    			T=new Destroy_Blocks("destroy","file:destroy.png");
    		}
    		else if(code==6) {
    			T=new Shield("shield","file:shield.png");
    		}
    		else {
    			T=new Magnet("magnet","file:magnet.png");
    		}
    		play.getChildren().add(T.getPhoto());
    		if(T instanceof Ball) {
    			play.getChildren().add(((Ball) T).getballT());
    		}
    	}
    }
    
    protected void createWall(Pane play) {
    	int p=rand.nextInt(10);
    	if(p<2 && W==null) {
    		W=new Wall(colorPicker_Wall());
    		play.getChildren().add(W.getLine());
    	}
    }

    protected void gameplay(Pane play) {
        Score.setText("Score : "+score.toString());
        int shieldFlag=0;
//        System.out.println("ShieldFlag : "+shieldFlag);
//        System.out.println(blocks.size()+" "+blocksText.size());
        for(int i=0;i<blocks.size();i++) {
            blocks.get(i).moveDown(speed);
            moveText(blockText.get(i),speed);

        }
        if(T!=null && T instanceof Shield) {
        	if(((Shield)T).isAlive==true){
        		if(System.currentTimeMillis()-((Shield) T).start<5000) {
        			((Shield) T).Timer.setText("Shield:"+String.valueOf(5-(System.currentTimeMillis()-((Shield) T).start)/1000));
        			shieldFlag=1;
        		}
        		else {
        			
        			shieldFlag=0;
        			play.getChildren().remove(((Shield)T).Timer);
        			System.out.println("Over");
        			((Shield)T).Timer.setVisible(false);
        			T=null;
        		}
        		
        	}
        	else {
        		T.moveDown(speed);
        	}
        }
//        System.out.println("ShieldFlag : "+shieldFlag);
        if(T!=null && (T instanceof Shield) == false) {											// if token is on screen
        	T.moveDown(speed);
        	if( T instanceof Ball) {
        		((Ball) T).moveBallText(speed);
        	}
        }
        if(W!=null) {											// if wall is on screen
        	W.moveDown(speed);
        }
        
        List<Block> newBlocks=null;            //so that it is automatically destroyed after each execution
        List<Text>  newBlocksValue=null;

        if(blocks.isEmpty() || blocks.get(blocks.size()-1).getTranslateY()>400) { //distance between two rows of blocks is 250
            blockAndText obj= createBlocks();
            newBlocks= obj.blockShape;
            newBlocksValue =obj.blockText;
            for(int i=0;i<newBlocks.size();i++){
                blocks.add(newBlocks.get(i));
                blockText.add(newBlocksValue.get(i));
                play.getChildren().add(newBlocks.get(i));
                play.getChildren().add(newBlocksValue.get(i));

            }

            speed+=0.15;
//            System.out.println("Speed : "+speed);
        }
        checkCollision(play,shieldFlag);
        checkBoundary(play);
        createToken(play);
        createWall(play);
        checkSnakeBalls(play);
    }
    
    protected void checkSnakeBalls(Pane play) {
    	if(snake.getNumBalls()<1) {
    		A.stop();
    		runningA=false;
    		gameOver(play);
    	}
    	
    }
    
    protected void gameOver(Pane play) {
    	Text t= new Text("Game Over \n \nYour Score \n 	"+score.toString());
    	t.setX(200);
    	t.setY(240);
    	t.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 24));
    	Rectangle r=new Rectangle(200,200,Color.WHITE);
    	r.setX(180);
    	r.setY(200);
    	r.setOpacity(0.4);
    	play.getChildren().addAll(r,t);
    }


    protected void checkCollision(Pane play, int shieldFlag) {
        for(int i=0;i<blocks.size();i++) {
            if(blocks.get(i).getBoundsInParent().intersects(snake.body.get(0).getBoundsInParent()) & blocks.get(i).getAlive()==true){    //Collision Check
            	
            	if(shieldFlag==1) {
            		score+=blocks.get(i).getblockValue();
            	}
            	else if(snake.getNumBalls()>=blocks.get(i).getblockValue()) {
            		score+=blocks.get(i).getblockValue();
            		snake.setNumBalls(-blocks.get(i).getblockValue());
            		snake.setScoreText();
            	}
            	else {
            		snake.setNumBalls(-blocks.get(i).getblockValue());
            		checkSnakeBalls(play);
            	}
                
//                if(snake.body.size()<18) {
//                    if (score - scoreTracker > 20)             // This is of course not the condition to increase snake's length
//                    {                            // These things will be used when coins are added
//
//                        for (int j = 0; j < snake.body.size(); j++) {
//                            snake.body.get(j).setCenterY(snake.body.get(j).getCenterY() - 20);
//                        }
//
//                        snake.body.add(new Circle(snake.body.get(snake.body.size() - 1).getCenterX(), snake.body.get(snake.body.size() - 1).getCenterY() + 20, 10));
//                        snake.body.get(snake.body.size() - 1).setFill(Color.YELLOW);
//                        play.getChildren().add(snake.body.get(snake.body.size() - 1));
//                        scoreTracker += 20;
//                    }
//                }

                blocks.get(i).setAlive(false);
                blocks.get(i).setVisible(false);
                play.getChildren().remove(blocks.get(i));
                blocks.remove(i);


                blockText.get(i).setVisible(false);
                play.getChildren().remove(blockText.get(i));
                blockText.remove(i);

//    	    		System.gc();
            }
            if(T!=null && ((T instanceof Shield)==true) && ((Shield)T).isAlive==true){			//left blank intentionally
            	
            }
            
            else if(T!=null && T.getPhoto().getBoundsInParent().intersects(snake.body.get(0).getBoundsInParent())){
            	if(T instanceof Ball) {
            		((Ball) T).increaseBalls(snake);
            		((Ball) T).getballT().setVisible(false);
            	}
            	else if(T instanceof Destroy_Blocks) {
            		score=score+((Destroy_Blocks) T).destroyBlocks(play,blocks,blockText);
            	}
            	else if(T instanceof Shield) {
            		((Shield) T).protectSnake();
            		play.getChildren().add(((Shield) T).Timer);
            	}
            	else {
            		((Magnet) T).attractCoins();
            	}
            	T.getPhoto().setVisible(false);
            	play.getChildren().remove(T);
            	if(T instanceof Shield == false) {
            		T=null;
            	}
            	
            }
            
            if(W!=null && W.getLine().getBoundsInParent().intersects(snake.body.get(0).getBoundsInParent())){	//walls working
//            	W.getLine().setVisible(false);	
//            	play.getChildren().remove(W);
//            	W=null;
//            	System.out.println(W.getLine().getEndX()+" , "+snake.body.get(0).getCenterX() );
            	if(W.getLine().getEndX()>snake.body.get(0).getCenterX()) {
            		snake.moveLeft();
            	}
            	else {
            		snake.moveRight();
            	}
            	
            }
        }

    }

    protected void checkBoundary(Pane play) {
        for(int i=0;i<blocks.size();i++) {
//    		System.out.println(blocks.get(i).getManualY());
            if(blocks.get(i).getManualY()>820) {						// checking if block passed the snake

                blocks.get(i).setVisible(false);
                blocks.get(i).setAlive(false);
                play.getChildren().remove(blocks.get(i));
                blocks.remove(i);
                blockText.get(i).setVisible(false);
                play.getChildren().remove(blockText.get(i));
                blockText.remove(i);

            }
        }
        
        if(T!=null && T.getManualY()>820) {
//        	System.out.println("Removed Token");
        	T.getPhoto().setVisible(false);
        	play.getChildren().remove(T);
        	T=null;
        }
        
        if(W!=null && W.getManualY()>820) {
//        	System.out.println("Removed Wall");
        	W.getLine().setVisible(false);
        	play.getChildren().remove(W);
        	W=null;
        }
    }


    protected void Play(Stage primaryStage, ImageView imageview) {

        Pane play=new Pane();
        speed=0; scoreTracker=0;
        snake.body.clear();
        blocks.clear();
        blockText.clear();
        Image image = new Image("file:blackBackground.png");
        ImageView blackBackground=new ImageView(image);
        blackBackground.setFitHeight(820);
        blackBackground.setFitWidth(620);

        snake.body.add(new Circle(270,600,12));
        snake.body.get(0).setFill(Color.WHITE);
        snake.setScoreText();
        for(int i=1;i<4;i++){
            snake.body.add(new Circle(270,600+i*20,10));
            snake.body.get(i).setFill(Color.YELLOW);
        }

        A = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameplay(play);
//                if(T!=null) {
//                	play.getChildren().add(T.photo);
//                }
            }
        };
        A.start();
        runningA=true;


        Button btn6=new Button("X");                    //Quit Button
        btn6.setLayoutX(230);
        btn6.setLayoutY(20);
        btn6.setMinSize(30, 30);
        btn6.setStyle("-fx-font: 24 arial; -fx-base: #FE2E2E;");
        btn6.setOnAction(e-> {
            obj.Menu(primaryStage,imageview);
        });

        Button btn7=new Button("| |");                  //Pause Button
        btn7.setLayoutX(170);
        btn7.setLayoutY(20);
        btn7.setMinSize(30, 30);
        btn7.setStyle("-fx-font: 24 arial; -fx-base: #FE2E2E;");
        btn7.setOnAction(e-> {
            if(this.paused==false)
            {   this.paused=true;
                btn7.setText("|>");
                A.stop();
            }
            else if(this.paused==true){
                this.paused=false;
                btn7.setText("| |");
                A.start();
            }
        });

//        HBox topPanel = new HBox(5);                	//Horizontal strip in the upper side for pause,stop and score
//        topPanel.setPadding(new Insets(10));
//        topPanel.setAlignment(Pos.BASELINE_RIGHT);
//		  topPanel.getChildren().addAll(btn6,btn7);
        play.getChildren().setAll(blackBackground,btn6,btn7);
        play.getChildren().addAll(snake.body);
        play.getChildren().add(snake.getScore());

        Score.setX(36);
        Score.setY(44);
        Score.setFill(Color.WHITE);
        Score.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));

        play.getChildren().addAll(blocks);
        play.getChildren().addAll(Score);
        

        Scene scene=new Scene(play,530,800);

        scene.setOnKeyPressed(e-> {

            switch(e.getCode()){

                case A :
                    if(snake.body.get(0).getCenterX()>10 && runningA==true){
                        snake.moveLeft();
                    }
                    break;
                case D :
                    if(snake.body.get(0).getCenterX()<520 && runningA==true){
                        snake.moveRight();
                    }
                    break;
                case LEFT :
                    if(snake.body.get(0).getCenterX()>10 && runningA==true){
                        snake.moveLeft();
                    }
                    break;
                case RIGHT :
                    if(snake.body.get(0).getCenterX()<520 && runningA==true){
                        snake.moveRight();
                    }
                    break;
                default:
                    break;

            }});

        primaryStage.setScene(scene);

    }
}