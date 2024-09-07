//package project_B13;
package finalproject;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

//	Pipe with image
class Pipe extends ImageView {
	private String site; // 分upper、lower
	private Timeline move_round;
	private double upper_site, lower_site;
	private int step = 0;
	private boolean move_permit = true;
	static int move_attempt = 0;
	
	
	Pipe( double x,double y,String set_site ){
		double pipe_width = 80;
		double pipe_height = 600;
//		Image pipe_pic = new ImageView( new Image(getClass().getResource( "/resources/images/down_pipe.png").toExternalForm() ));
//		pipe_pic.setFitWidth( pipe_width );
//		pipe_pic.setFitHeight( pipe_height );
        setFitWidth( pipe_width ); // Set the size of the bird
        setFitHeight( pipe_height );
		Image pipe_pic = new Image(getClass().getResource( "/resources/images/the_pipe.png").toExternalForm() );
        
		setImage( pipe_pic );
		site = set_site;
		setX(x);
		setY(y);
//		setFill(Color.GREEN);
//		setWidth(50);
//		setHeight(600);
		if(site.equals("upper")) {
			upper_site = y;
			lower_site = y+250;
		}
		else {
			lower_site = y;
			upper_site = y-250;
		}
	}	//	end Pipe
	
	public void move_up( double paneHeight ){
		double dy = -paneHeight/100;
		double nowY = getY();
		setY(nowY+dy);
	}
	
	public void move_down( double paneHeight){
		double dy = paneHeight/100;
		double nowY = getY();
		setY( nowY + dy );
	}
	
	//	
	public void movePipe(double paneHeight ) {
		 move_attempt += 1;
		if( site.equals("lower") & move_permit == true ) {
			step = 1;
			move_up(paneHeight);
			move_round = new Timeline( new KeyFrame( Duration.millis( 5 ), e -> {
				if( getY() <= upper_site ) {
					step = 2;
				}
				if( getY() >= lower_site ) {
					step = 0;
				}
				if( step==1 ) { 
					move_up(paneHeight);
				}
				else if(step==2) {
					move_down(paneHeight);
				}
				else {
					move_round.stop();
					}
				}));
			move_round.setCycleCount( Timeline.INDEFINITE );
			move_round.play();
			
		}
		else if( site.equals( "upper" )& move_permit == true ) {
			step = 1;
			move_down(paneHeight);
			//	controll pipe to move
			move_round = new Timeline(new KeyFrame(Duration.millis( 5 ), e -> {
				if(getY()<=upper_site) {
					step = 0;
				}
				if(getY()>=lower_site) {
					step = 2;
				}
				if(step==1) {move_down(paneHeight);}
				else if(step==2) {move_up(paneHeight);}
				else {move_round.stop();}
				}));
			move_round.setCycleCount(Timeline.INDEFINITE);
			move_round.play();
		}
	}
	
	//	判斷水管碰到了沒
	public boolean touch( double site_x, double site_y , double radius) {
		boolean judge = false;
		if(( site_x+radius/2 > getX() & site_x < getX() + getFitWidth() ) & ( site_y>getY() & site_y < getY() + getFitHeight() )) {
			judge = true;
		}
		return judge;
	}
	
	public void moveForbidden() {
		move_permit = false;
	}
	
	public void movePermit() {
		move_permit = true;
	}
}
//	Bird , Dragon的父類別： Creature
abstract class Creature extends ImageView {
    Timeline animation;
    private double speed; // Horizontal speed
    private double speedY = 0; // Vertical speed
    private double ga = 4.8; // Gravity acceleration
    private boolean end = false;
    private int hit_score;
    private int radius;
    private double jumpspeed;
    private int curindex = 0;
    ArrayList<Image> images = new ArrayList<>();

    Creature(double speed, int hit_score, int radius, double jumpspeed, String[] imagePaths) {
        this.speed = speed;
        this.hit_score = hit_score;
        this.radius = radius;
        this.jumpspeed = jumpspeed;

        setFitWidth(radius); // Set the size of the creature
        setFitHeight(radius);

        for (String path : imagePaths) {
            images.add(new Image(getClass().getResource(path).toExternalForm()));
        }

        setPreserveRatio(true);

        setX(0);
        setY(100 + (double) (Math.random() * 400));

        animation = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            this.speed += (double) (main.score / 10000) + (double) (0.2 * Pipe.move_attempt / 100);
            move();
            if (getX() >= 1400) {
                this.speed = 0;
                end = true;
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    protected void move() {
        double siteX = getX();
        setX(siteX + speed);
        fall();
        setY(getY() + speedY);
    }

    public void fall() {
        speedY += ga;
    }

    public void jump() {
        speedY = -jumpspeed;
        animate();
    }

    public void animate() {
        curindex++;
        if (curindex == images.size() - 1) {
            curindex = 0;
        }
        setImage(images.get(curindex));
    }

    public int score() {
        return hit_score;
    }

    public boolean get_end_judge() {
        return end;
    }

    public void stop() {
        speed = 0;
        speedY = 0;
        ga = 0;
    }

    public void restart() {
        speed = 10;
    }

    public double getCenterY() {
        return getY() + getFitHeight() / 2.0;
    }

    public double getCenterX() {
        return getX() + getFitWidth() / 2.0;
    }
}

//	Bird with images
class Bird extends Creature {

    public Bird() {
        super(10, 10, 65, 20, new String[]{
                "/resources/images/Ybird_0.png",
                "/resources/images/Ybird_1.png",
                "/resources/images/Ybird_2.png",
                "/resources/images/Ybird_3.png"
        });

        Timeline birdjump = new Timeline(new KeyFrame(Duration.millis(60 + new Random().nextDouble() * 100), e -> {
            if ((getY() > 300)) { // Underground
                jump();
            }
            double rate = new Random().nextDouble();
            if ((rate < 0.3) && (getY() >= 90)) { // Above sky
                jump();
            }
        }));
        birdjump.setCycleCount(Timeline.INDEFINITE);
        birdjump.play();
    }
}

class Dragon extends Creature {

    public Dragon() {
        super(10, 5, 80, 15, new String[]{
                "/resources/images/Rbird_0.png",
                "/resources/images/Rbird_1.png",
                "/resources/images/Rbird_2.png",
                "/resources/images/Rbird_3.png"
        });

        Timeline userinput = new Timeline(new KeyFrame(Duration.millis(40 + new Random().nextDouble() * 60), e -> {
            if ((getY() > 200)) { // Underground
                jump();
            }
            double rate = new Random().nextDouble();
            if ((rate < 0.5) && (getY() >= 40)) { // Above sky
                jump();
            }
        }));
        userinput.setCycleCount(Timeline.INDEFINITE);
        userinput.play();
    }
}


public class main extends Application {
	
	static boolean pipe_click=true;
	static Pane gamepane = new Pane();
	Timeline birdProcedure, hitJudge;
	static int time_count = 0;	//	計算時間間格，以了解是否要生成動物
	static double time_gap = 120 ;	//生成時間間格
	static int birth_count = 0;	//	計算雙龍間生成鳥量
	static double Dragon_birth_gap = 10;	//雙龍間生成鳥量給定
	static ArrayList<Pipe> AllPipe = new ArrayList<Pipe>();
    static ArrayList<Creature> AllBird = new ArrayList<>();
    static ArrayList<Creature> AllDragon = new ArrayList<>();
	static int score = 0;	//	分數
	static int record = 0;	//	分數
	static Boolean gamestart = false;
	static Boolean gameover = false;
	private static int APP_WIDTH = 1400; 
    private static int APP_HEIGHT = 600;
    static double titleY = -100;
    static Text scoreLabel;	
    private static boolean paused = false; // State variable to track pause state
    private boolean isPauseToggleInProgress = false;	//	預防按暫停按太快


    Group pauseblock = new Group();
    ImageView background ;
    MediaPlayer GameoverPlayer;
	
	public void start( Stage primaryStage ) {
		boolean gamestart = false;
		//	鳥行動的動畫
        birdProcedure = new Timeline(new KeyFrame( Duration.millis(10), e -> {
        	time_gap = time_gap + (double)( Math.random()*800 ) - ( double )( score / 100 );
            gameEndJudge();
            if( AllBird.isEmpty() ) {
                birdGenerate( gamepane );
            }
            if (time_count >= time_gap) {
                if( birth_count < Dragon_birth_gap ) {
                    birdGenerate( gamepane );
                } else {
                    dragonGenerate(gamepane);
                }
            } else {
                time_count += 1;
            }
        	time_gap = 120;
        }));
        birdProcedure.setCycleCount(Timeline.INDEFINITE);
        
//		Scene opening = startscene();      
//		primaryStage.setScene( opening ); // Place the scene in the stage
		Pane startpane = new Pane();
		Scene opening = new Scene( startpane , 1400, 600 );
        setLabels();
        double random = ( double )( Math.random() );

		gamepane.getChildren().addAll( setBackground( random ) , scoreLabel );
		Scene scene = new Scene( gamepane, 1400, 600 );
		primaryStage.setScene( opening );
	    ImageView title = new ImageView(new Image(getClass().getResource( "/resources/images/Hitting Bird_1.png" ).toExternalForm()));
	    title.setPreserveRatio( true ); // Preserve the aspect ratio
	    title.setFitWidth( 500 );
//	    title.setFitHeight( 124 );
	    title.setLayoutX( 420 );

	    title.setLayoutY( titleY );
	    double titlespeed = 5;
	    Animation titlemove = new Timeline( new KeyFrame( Duration.millis( 30 ), e -> {
		    title.setLayoutY( titleY + titlespeed );
		    titleY += titlespeed;
	    } ) );
		titlemove.setCycleCount( 36 );
	    titlemove.play();
	    
//	    Text Rules = GameText( "Game Rule" , 40 );
//	    Rules.setLayoutX( 600 );
//	    Rules.setLayoutY( 300 );
//	    startpane.getChildren().addAll( Rules );
	    
        
	    Text inst = GameText( "Press to Start" , 50 );
	    inst.setLayoutX( 550 );
	    inst.setLayoutY( 400 );
        startpane.getChildren().addAll( setBackground( random )  );
//        startpane.setEffect(new GaussianBlur());
        //	展示小鳥飛行動畫
		Animation showbirds = new Timeline( new KeyFrame( Duration.millis( 5 ), e -> {
		if( time_count >= time_gap ) {
			if( birth_count < Dragon_birth_gap ) {
				birdGenerate( startpane );
				birth_count+=1;
			}
			else{
				dragonGenerate( startpane );
			}			
		}		
		else{
			time_count+=1;
		}
		}));
		showbirds.setCycleCount( Timeline.INDEFINITE );
		showbirds.play();
		
	    Text escexit = GameText( "Press to EXIT" , 40 );
	    escexit.setLayoutX( 580 );
	    escexit.setLayoutY( 500 );
	    escexit.setOnMousePressed( e -> {
	    	if( !gamestart ) {
	    		Platform.exit();
	    	}
        });

	    //	按任何鍵開始
        startpane.setOnKeyPressed( e -> {
        	showbirds.stop();
    		primaryStage.setScene( scene );
    		mainGameInitial();	//	建立遊戲
        });
	    //	滑鼠點擊開始
        startpane.setOnMousePressed(e -> {
        	showbirds.stop();
    		primaryStage.setScene( scene );
    		mainGameInitial();	//	建立遊戲
        });

        startpane.getChildren().addAll( title , inst , escexit );
       
        Rectangle block;block = new Rectangle( 400, 200, Color.BEIGE);
        block.setArcWidth(30);
        block.setArcHeight(30);
        block.setStroke(Color.BLACK);
        block.setStrokeWidth(5);

	    block.setLayoutX( 250 );
	    block.setLayoutY( 100 );
	    //	button to restart
	    //	重新開始按鍵
	    ImageView resumebtn = new ImageView( new Image( getClass().getResource( "/resources/images/resume.png" ).toExternalForm() ));
	    resumebtn.setPreserveRatio( true ); // Preserve the aspect ratio
	    resumebtn.setFitWidth( 200 );
	    resumebtn.setLayoutX( 350 );
	    resumebtn.setLayoutY( 100 );
	    resumebtn.setEffect( new Glow( 0.3 ) );
        FadeTransition ft = new FadeTransition( Duration.millis( 700 ) , resumebtn );
        ft.setFromValue( 1.0 );
        ft.setToValue( 0 );
		ft.setCycleCount( Timeline.INDEFINITE );
		ft.setAutoReverse( true );
        ft.play();
	    //	重新開始按鍵的點擊事件
        resumebtn.setOnMousePressed( e -> {
	    	endGame();
        });
        Text exit = GameText( "EXIT" , 40 );
	    exit.setLayoutX( 400 );
	    exit.setLayoutY( 270 );
        pauseblock.setLayoutY( 100 );
        pauseblock.getChildren().addAll( block , exit , resumebtn );
        pauseblock.setLayoutX( 800 );
        pauseblock.setLayoutY( 100 );
	    //	EXIT的點擊事件
	    exit.setOnMousePressed( e -> {
	    	Platform.exit(); // close the application
        });
	    //	點擊ESC事件
        gamepane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE && !isPauseToggleInProgress ) {
                if (paused) {
                    resumeGame();
                } 
                else {
                    pauseGame();
                }
            }
        });
//		Animation glowing = new Timeline( new KeyFrame( Duration.millis( 20 ), e -> {
//			title.setEffect( new Glow( 0.2 ) );
//	        inst.setEffect( new Glow( 2 ) );
//	        long glowsecs = System.currentTimeMillis();
////	        if( glowsecs  % 400 > 0 ) {
////				title.setEffect( new Glow( 0 ) );
////		        inst.setEffect( new Glow( 0 ) );
////	        	glowsecs -= 400;
////	        }
//		}));
//		glowing.setCycleCount( Timeline.INDEFINITE );
//		glowing.play();
//		mainGameInitial();	//	建立遊戲
		primaryStage.setTitle( "Hitting Bird" ); // Set the stage title

//		primaryStage.show(); // Display the stage
		gamepane.requestFocus(); // pane is focused to receive key input
		primaryStage.show();
	}
	
	/*********************設定分數版**************************/
	private void setLabels() {
        scoreLabel = new Text(  "" + score );
        scoreLabel.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 50));
        scoreLabel.setStroke(Color.BLACK);
        scoreLabel.setFill(Color.WHITE);
        scoreLabel.setLayoutX( 20 );
        scoreLabel.setLayoutY(40);
	}
	private static void updateScoreLabel(  ) {
		System.out.println( score );
        scoreLabel.setText( "" + score );
    }
	
    public static Text GameText( String tmpword , double size ) {
    	Text text = new Text( tmpword );

        text.setFont( Font.font( "Courier", FontWeight.EXTRA_BOLD , size ));
        text.setFill( Color.WHITE );
        text.setStroke( Color.BLACK );
        text.setEffect( new Glow( 2 ) );
        FadeTransition ft = new FadeTransition( Duration.millis( 700 ) , text );
        ft.setFromValue( 1.0 );
        ft.setToValue( 0 );
		ft.setCycleCount( Timeline.INDEFINITE );
		ft.setAutoReverse( true );
        ft.play();
    	return text;
    }
	//	初始化主遊戲
    public void mainGameInitial() {
        score = 0;
        updateScoreLabel();
        scoreLabel.setVisible( true );
        time_gap = 120;

        for ( Creature b : AllBird ) {
            gamepane.getChildren().remove(b);
        }
        AllBird.clear();

        for ( Creature g : AllDragon ) {
            gamepane.getChildren().remove(g);
        }
        AllDragon.clear();

        if  (AllPipe.isEmpty() ) {
            pipeGenerate(300, 550, "lower");
            pipeGenerate(600, -550, "upper");
            pipeGenerate(1100, 550, "lower");
            pipeGenerate(1100, -550, "upper");
        }
        for (int k = 0; k < AllPipe.size(); k++) {
            AllPipe.get(k).movePermit();
        }
        paused = false;
        gamestart = true;
        gameover = false;
        birdProcedure.play();

    }

	//	暫停遊戲
    private void pauseGame() {
        if (isPauseToggleInProgress) return;
        isPauseToggleInProgress = true;
        paused = true;
        birdProcedure.pause();
        
        for ( Creature bird : AllBird ) {
            bird.animation.pause();
        }
        for ( Creature dragon : AllDragon ) {
            dragon.animation.pause();
        }

        // Check if pauseblock is already added to gamepane before adding
        if ( !gamepane.getChildren().contains( pauseblock) ) {
            pauseblock.setLayoutX( 250 );
            pauseblock.setLayoutY( 100 );
            gamepane.getChildren().add( pauseblock );
        }
        isPauseToggleInProgress = false;
    }
	//	繼續遊戲
    private void resumeGame() {
        if (isPauseToggleInProgress) return;
        isPauseToggleInProgress = true;
        paused = false;
        gamepane.getChildren().removeAll( pauseblock );
        birdProcedure.play();
        for ( Creature bird : AllBird) {
            bird.animation.play();
        }
        for ( Creature dragon : AllDragon ) {
            dragon.animation.play();
        }
        isPauseToggleInProgress = false;
    }
    
	public void gameEndJudge() {
		//	只判斷因鳥到達終點觸發之遊戲結束判斷
		for(int i=0;i<AllBird.size();i++) {
			if( AllBird.get( i ).get_end_judge() && gamestart ) {
				endGame();
			}
		}
	}
	
	//	生成水管
	public void pipeGenerate( double setX, double setY, String site ) {
		Pipe pipe = new Pipe(setX, setY, site);
		pipe.setOnMouseClicked(e -> {
			if(pipe_click) {
				pipe_click=false;
				pipeMove(pipe);
				//pipe_click=true;
			}
		});
		gamepane.getChildren().add(pipe);
		AllPipe.add(pipe);
	}
	//	生成小鳥
	public static void birdGenerate( Pane pane ) {
		AllBird.add( new Bird() );
		pane.getChildren().add( AllBird.get(AllBird.size()-1 ));
		time_gap = 100+(double)(Math.random()*500);
		time_count = 0;
        birth_count += 1;
	}
	
	public static void dragonGenerate( Pane pane) {
		AllDragon.add(new Dragon());
		pane.getChildren().add( AllDragon.get(AllDragon.size()-1) );
		Dragon_birth_gap = 2+(double)(Math.random()*10);
		time_gap = 200+(double)(Math.random());
		time_count = 0;
		birth_count = 0;
	}
	
	public static void clearCrossDragon() {
		// 清除通過的龍
		for( int i=0; i < AllDragon.size();i++ ) {
			if( AllDragon.get( i ).get_end_judge()) {
				gamepane.getChildren().remove( AllDragon.get( i ));
				AllDragon.remove( i );
				break;
			}
		}
	}
	
	public void pipeMove( Pipe pipe ) {
		Media punchBirdSound = new Media(getClass().getResource("/resources/sounds/punch_bird.mp3").toExternalForm());
	    Media punchDragonSound = new Media(getClass().getResource("/resources/sounds/punch_dragon.mp3").toExternalForm());
	    
		pipe.movePipe( gamepane.getHeight() );
		//	判斷有沒有碰到
		hitJudge = new Timeline( new KeyFrame( Duration.millis(10), f ->{
			boolean break_judge=false;
			for(int i=0;i<AllBird.size();i++) {
				double site_x = AllBird.get(i).getCenterX();
				double site_y = AllBird.get(i).getCenterY();
				double bird_radius=AllBird.get(i).getFitHeight();
				if( pipe.touch( site_x, site_y, bird_radius) ) {
					// 成功打鳥_放音效		punch_bird.mp3
					MediaPlayer punchBirdPlayer = new MediaPlayer( punchBirdSound );
					punchBirdPlayer.play();
					score += AllBird.get( i ).score() ;
					updateScoreLabel();
					gamepane.getChildren().remove( AllBird.get( i ) );
			        FadeTransition ft = new FadeTransition( Duration.millis( 80 ) , AllBird.get(i) );
			        ft.setFromValue( 1.0 );
			        ft.setToValue( 0 );
					ft.setCycleCount( Timeline.INDEFINITE );
					ft.setAutoReverse( true );
			        ft.play();
					AllBird.remove( i );
			        
					break;
				}
			}
			
			for( int j=0;j < AllDragon.size(); j++ ) {
				double Dragon_site_x = AllDragon.get( j ).getX();
				double Dragon_site_y = AllDragon.get( j ).getY();
				double Dragon_radius=AllDragon.get(j).getFitHeight();
				if( pipe.touch( Dragon_site_x, Dragon_site_y, Dragon_radius )) {
					// 打龍成功_放音效，遊戲結束 	punch_dragon.mp3
					MediaPlayer punchDragonPlayer = new MediaPlayer( punchDragonSound );
					punchDragonPlayer.play();
					score += AllDragon.get( j ).score();
					updateScoreLabel();
					System.out.println( "+=" + AllDragon.get( j ).score()  );
					gamepane.getChildren().remove( AllDragon.get( j ) );
					break_judge=true;

					endGame();
					break;
				}
			}
			
			if( break_judge ) {
				hitJudge.stop();
			}
		}
		));
		hitJudge.setCycleCount( 50 );
		hitJudge.play();
		Timeline true_duration = new Timeline(new KeyFrame(Duration.millis(500), stop->{pipe_click=true;}));
		true_duration.play();
		
	}
	//	停止主遊戲
	public void endGame() {
		gameover = true;
	    Pipe.move_attempt = 0;

	    for (int j = 0; j < AllBird.size(); j++) {
	        AllBird.get(j).stop();
	    }
	    for (int i = 0; i < AllDragon.size(); i++) {
	        AllDragon.get(i).stop();
	    }
	    for (int k = 0; k < AllPipe.size(); k++) {
	        AllPipe.get(k).moveForbidden();
	    }
	    birdProcedure.stop();
	    gamestart = false;
	    scoreLabel.setVisible(false);
	    if ( paused ) {
	        gamepane.getChildren().removeAll( pauseblock );
	        replay();
	        return;
	    }

	    Media GameoverSound = new Media(getClass().getResource("/resources/sounds/gameover.mp3").toExternalForm());
	    GameoverPlayer = new MediaPlayer(GameoverSound);
	    GameoverPlayer.play();
	    ending();
	}
	//	結束畫面
	public void ending() {
	    ImageView endtitle = new ImageView(new Image(getClass().getResource("/resources/images/GameOver.png").toExternalForm()));

	    // Set properties for gameover ImageView
	    endtitle.setPreserveRatio(true); // Preserve the aspect ratio
	    endtitle.setFitWidth(550);
	    endtitle.setLayoutX(400);
	    endtitle.setLayoutY(-120);

	    // Background for the score display
        Rectangle block;block = new Rectangle( 400, 200, Color.BEIGE);
        block.setArcWidth(30);
        block.setArcHeight(30);
        block.setStroke(Color.BLACK);
        block.setStrokeWidth(5);
	    block.setLayoutX(500);
	    block.setLayoutY(200);

	    // Score text
	    Text scoreText = new Text("SCORE: " + score);
	    scoreText.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 50));
	    scoreText.setFill(Color.BLACK);
	    scoreText.setLayoutX(550);
	    scoreText.setLayoutY(280);

	    // Best score text
	    record = (score > record) ? score : record;
	    Text Best = new Text("BEST: " + record);
	    Best.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 50));
	    Best.setFill(Color.BLACK);
	    Best.setLayoutX(550);
	    Best.setLayoutY(360);

	    // Group for ending screen elements
	    Group endblock = new Group();
	    endblock.setLayoutY(50);
	    endblock.getChildren().addAll(block, scoreText, Best);

	    gamepane.getChildren().add( endblock );

	    endtitle.setPreserveRatio(true); // Preserve the aspect ratio
	    endtitle.setFitWidth(550);
	    endtitle.setLayoutX(400);
	    endtitle.setLayoutY(-120);

	    // Text for exit button
        Text exit = GameText( "EXIT" , 40 );

	    exit.setLayoutX(650);
	    exit.setLayoutY(540);

	    // Add gameover image and exit button if not already present
	    if( !gamepane.getChildren().contains( endtitle) ){
	        gamepane.getChildren().add( endtitle );
	    }
	    if (!gamepane.getChildren().contains(exit)) {
	        gamepane.getChildren().add(exit);
	    }
	    if ( !gamepane.getChildren().contains( endblock )) {
	        gamepane.getChildren().add( endblock );
	    }
//	    gamepane.setEffect(new ColorAdjust(0, 0, 0, 0));
	    gamepane.setOnKeyPressed(e -> {
	        gamepane.getChildren().removeAll( endtitle, exit, endblock );
	        replay();
	    });
	    
	    gamepane.setOnMousePressed(e -> {
	        gamepane.getChildren().removeAll( endtitle, exit, endblock);
	        replay();
	    });
	}
	//	重新開始玩遊戲
	public void replay() {

	   	//	GameOver
		if( gameover ) {
			if( GameoverPlayer != null ) {
				GameoverPlayer.stop();
			}
			mainGameInitial();	//	建立遊戲
//			hitJudge.play();
			return;
		}
	   	//	Pausing
		if( paused ) {
			mainGameInitial();	//	建立遊戲
			paused = false;
		}
		gamepane.setOnKeyPressed(null);
	    gamepane.setOnMousePressed(null);
	}
	//	設定背景
    private ImageView setBackground( double rate ) {
        Random random = new Random();
        Image bg;
        if( rate <= 0.5  ) {
        	bg = new Image(getClass().getResource(  "/resources/images/background_2.png" ).toExternalForm() );
        }
        else {
        	bg = new Image( getClass().getResource(  "/resources/images/background_1.png" ).toExternalForm() );
        }
        ImageView imageView =  new ImageView( bg );
        imageView.setFitWidth( APP_WIDTH );
        imageView.setFitHeight( APP_HEIGHT );
        return imageView;
    }
	
public static void main( String[]args ) {
	
		launch( args );
	}
}
