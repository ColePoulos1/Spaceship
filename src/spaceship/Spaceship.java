
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = new sound("starwars.wav");
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImageAnim;
    Image rocketImage;
    int rocketXPos;
    int rocketYPos;
    int rocketYVel;
    boolean rocketRight;
//variables for stars.
    Image starImage;
    int numstars = 4;
    int starXPos[];
    int starYPos[];
    int starXVel;
    
    Missile missiles[];
    
    int score;
    int highScore;
    boolean gameOver;
    boolean hitdat[];
    
    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
                 if (gameOver)
                        return;
// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                  if(rocketYVel<0)
                    rocketYVel+=2;
                  else if (rocketYVel<15)
                    rocketYVel+=1;   
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    if(rocketYVel>0)
                    rocketYVel-=2;
                    else if(rocketYVel>-15)
                    rocketYVel-=1; 
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    if(starXVel<20)
                    starXVel+=2;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    if(starXVel>-20)
                    starXVel-=2;
                }
                else if (e.VK_SPACE == e.getKeyCode()) {
                    //fire missiles
                    missiles[Missile.current].active = true;
                    missiles[Missile.current].xpos = rocketXPos;
                    missiles[Missile.current].ypos = rocketYPos;
                    missiles[Missile.current].right = rocketRight;
                    Missile.current++;
                    if (Missile.current >= Missile.num)
                        Missile.current = 0;
                }

                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
        
        if(bgSound.donePlaying == true)
            bgSound = new sound("starwars.wav");
       
        
        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        
        if(starXVel>0) 
            rocketRight=false;
        if(starXVel<0) 
            rocketRight=true;
        
        g.setColor(Color.white);
        for (int index=0;index<Missile.num;index++)
        {  
        if(missiles[index].active==true)
            drawCircle(getX(missiles[index].xpos),getYNormal(missiles[index].ypos),0,0.5,0.3);
        }
        if(starXVel==0)
        {
            if(rocketRight == false)
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
            else
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        else
        {
            if(rocketRight == false)
            drawRocket(rocketImageAnim,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
            else
            drawRocket(rocketImageAnim,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        
        for (int index=0;index<numstars;index++)
        {        
        drawStar(starImage,getX(starXPos[index]),getYNormal(starYPos[index]),0.0,1.0,1.0 );
        }
        
        //Display the score.  
        g.setColor(Color.MAGENTA);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("Score: " + score, 10, 43);
  
//Display the high score.
        g.setColor(Color.MAGENTA);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("High Score: " + highScore, 310, 43);
          
//Display gameover when the game is over.
        if (gameOver)
        {
            g.setColor(Color.MAGENTA);
            g.setFont(new Font("Impact",Font.BOLD,60));
            g.drawString("GAME OVER", 70, 350);
        }
        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.red);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width;
        int height;
        
        if(starXVel==0)
        {    
        width = rocketImage.getWidth(this);
        height = rocketImage.getHeight(this);
        }
        else
        {    
        width = rocketImageAnim.getWidth(this);
        height = rocketImageAnim.getHeight(this);
        }
        
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawStar(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = starImage.getWidth(this);
        int height = starImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2() /3;
        rocketYPos = getHeight2() /2;
        rocketYVel = 0;
        rocketRight = true;
        
        hitdat = new boolean[numstars];
        starXPos = new int[numstars];
        starYPos = new int[numstars];
        
        Missile.current=0;
        missiles = new Missile[Missile.num];
        for (int index=0;index<Missile.num;index++)
        missiles[index] = new Missile();
        
        for (int index=0;index<numstars;index++)
        {
        starXPos[index] = (int)(Math.random()*getWidth2());
        starYPos[index] = (int)(Math.random()*getHeight2());
        hitdat[index]=false;
        }
        starXVel = 0;
        
        score = 0;    
        gameOver = false;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            rocketImageAnim = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
            starImage = Toolkit.getDefaultToolkit().getImage("./starAnim.GIF");
            reset();
            highScore = 0;
        }
        if (gameOver)
            return;
        if (score >= highScore)
                highScore = score;
        
        if(rocketYPos+rocketYVel>=getHeight2()-6)
        {
            rocketYVel=0;
            rocketYPos=getHeight2()-7;
        }
        else if(rocketYPos+rocketYVel<=5)
        {
            rocketYVel=0;
            rocketYPos=6;
        }
        else
            rocketYPos+=rocketYVel;
        
        for (int index=0;index<numstars;index++)
        {
            if(starXPos[index]<=0)
            {
                starYPos[index]= (int)(Math.random()*getHeight2());
                starXPos[index]=getWidth2()-1;
            }
            if(starXPos[index]>=getWidth2())
            {
                starYPos[index]= (int)(Math.random()*getHeight2());
                starXPos[index]=1;
            }

            if (starXPos[index]-14 < rocketXPos && 
                starXPos[index]+14 > rocketXPos &&
                starYPos[index]-14 < rocketYPos &&
                starYPos[index]+14 > rocketYPos && hitdat[index]==false)
            {
                gameOver = true;
                bgSound = new sound("ouch.wav");
                hitdat[index] = true;
            }
            else if(starXPos[index]+14 < rocketXPos || 
                    starXPos[index]-14 > rocketXPos ||
                    starYPos[index]+14 < rocketYPos ||
                    starYPos[index]-14 > rocketYPos && hitdat[index]==true)
                        hitdat[index]=false;

            starXPos[index]+=starXVel;
        }
        for (int index=0;index<Missile.num;index++)
        {
            if(missiles[index].active==true)
                if(missiles[index].right)
                    missiles[index].xpos+=5;
                else
                missiles[index].xpos-=5;
        }
        
        for (int i=0;i<missiles.length;i++)
        {
            for (int j=0;j<numstars;j++)
            {
                if (missiles[i].active && missiles[i].xpos+10 > starXPos[j] &&
                missiles[i].xpos-10 < starXPos[j] &&
                missiles[i].ypos+10 > starYPos[j] &&
                missiles[i].ypos-10 < starYPos[j])
                {
                    score++;
                     missiles[i].active = false;
                     if (rocketRight)
                     {
                         starYPos[j] = (int)(Math.random()*getHeight2());
                         starXPos[j] = getWidth2();
                     }
                     else
                     {
                         starYPos[j] = (int)(Math.random()*getHeight2());
                         starXPos[j] = 0;

                     }
                }   
            }
        }
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numstars = Integer.parseInt(numStarsString.trim());
                }
                if (newLine.startsWith("nummissiles"))
                {
                    String numStarsString = newLine.substring(12);
                    Missile.num = Integer.parseInt(numStarsString.trim());
                }
                
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }

}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}


class Missile{

   public static int current = 0;
   public static int num = 1;  
   
   public int xpos;
   public int ypos;
   public boolean active;
   public boolean right;
   Missile()
   {
       active = false;
   }
           
            



}