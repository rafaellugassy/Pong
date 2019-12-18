import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class test extends PApplet {

// 



public void setup() {
  
  host = new Button(105, 170, 90, 60, "HOST");
  join = new Button(205, 170, 90, 60, "JOIN");
  textbox = new TextBox(105, 130, 100, 40, "127.0.0.1");
  name = new TextBox(0, 0, 100, 20, "Name");
  rectMode(CORNER);
}
Game game;
String state = "menu";
Client c;
Server s;
Button host, join;
TextBox textbox, name;
boolean start = false, noName = true;

public void draw() {
  background(0);

  if (state == "host" && !start) {
    c = s.available();
    if (c != null) {
      if (noName) {
        game.name2 = c.readStringUntil('\n');
        game.name1 = name.text;
        c.write(name.text + "\n");
        noName = false;
      }
      if ((c.read()) == 0)
        start = true;
    }
  }
  if (state == "menu") {
    host.display();
    join.display();
    textbox.update();
    textbox.display();
  } else if ((state == "host" || state == "guest") && start) {

    if (state == "host") {
      c = s.available();
      if (c != null) {
        String data = c.readStringUntil('\n');
        print(data);
        if (data.contains("move")) {
          game.paddle2.y = Float.parseFloat(data.split(" ")[1]);
        } else if (data.split(" ")[0].contains("bounce")) {
          String[] args = data.split(" ");
          game.pong.posX = Float.parseFloat(args[1]);
          game.pong.posY = Float.parseFloat(args[2]);
          game.pong.speedX = Float.parseFloat(args[3]);
          game.pong.speedY = Float.parseFloat(args[4]);
          game.pong.dirX = Float.parseFloat(args[5]);
          game.pong.dirY = Float.parseFloat(args[6]);
        }
      }
    } else if (state == "guest") {
      if (c.available() > 0) {
        String data = c.readStringUntil('\n');
        if (data.contains("move")) {
          game.paddle1.y = Float.parseFloat(data.split(" ")[1]);
        } else if (data.contains("reset")) {
          game = new Game(game.name1, game.name2, game.p1, game.p2);
          c.write(0);
        } else if (data.split(" ")[0].contains("bounce")) {
          String[] args = data.split(" ");
          game.pong.posX = Float.parseFloat(args[1]);
          game.pong.posY = Float.parseFloat(args[2]);
          game.pong.speedX = Float.parseFloat(args[3]);
          game.pong.speedY = Float.parseFloat(args[4]);
          game.pong.dirX = Float.parseFloat(args[5]);
          game.pong.dirY = Float.parseFloat(args[6]);
        }
      }
    }

    game.update();
    clear();
    game.display();
  }
}

public void keyPressed() {
  if (state == "host") {
    if (key == ENTER) {
      game = new Game(game.name1, game.name2, game.p1, game.p2);
      start = false;
      s.write("reset\n");
    }
  }
}

public void mouseClicked() {
  if (host.mouseIn()) {
    state = "host";
    s = new Server(this, 5204);
    host.disable();
    join.disable();
    textbox.disable();
    name.disable();
    game = new Game();
  }

  if (join.mouseIn()) {
    state = "guest";
    game = new Game();
    c = new Client(this, textbox.text, 5204);
    c.write(name.text + "\n");
    while (c.available() <= 0);
    game.name1 = c.readStringUntil('\n');
    game.name2 = name.text;
    noName = false;
    c.write(0);
    host.disable();
    join.disable();
    textbox.disable();
    name.disable();
    start = true;
  }
}
class Button {
  boolean disabled = false;
  float x, y, sx, sy;
  String text;
  Button(float x, float y, float sx, float sy, String text) {
    this.x = x;
    this.y = y;
    this.sx = sx;
    this.sy = sy;
    this.text = text;
  }

  public void disable() {
    disabled = true;
  }

  public boolean mouseIn() {
    if (disabled)
      return false;
    return mouseX > x && mouseX < x + sx && mouseY > y && mouseY < y + sy;
  }

  public void display() {
    textAlign(CENTER, CENTER);
    stroke(0);
    if (mouseIn())
      fill(150);
    else
      fill(200);

    rect(x, y, sx, sy);
    fill(0);
    text(text, x, y, sx, sy);
  }
}
class Game {
  Paddle paddle1, paddle2;
  Pong pong;
  int p1 = 0, p2 = 0;
  String name1 = "Player1", name2 = "Player2";
  boolean gameOver = false;

  Game(String name1, String name2, int p1, int p2) {
    this.name1 = name1;
    this.name2 = name2;
    this.p1 = p1;
    this.p2 = p2;
    paddle1 = new Paddle(25, 150, 10, 100);
    paddle2 = new Paddle(765, 150, 10, 100);
    pong = new Pong();
    pong.dirX = 1 - 2 * ((p1 + p2) % 2);
  }

  Game() {
    paddle1 = new Paddle(25, 150, 10, 100);
    paddle2 = new Paddle(765, 150, 10, 100);
    pong = new Pong();
  }

  public void update() {
    if (pong.posX > 800 && !gameOver) {
      p1++;
      gameOver = true;
    } else if (pong.posX < 0 && !gameOver) {
      p2++;
      gameOver = true;
    }

    if (keyPressed) {
      if (state == "host") {
        if (key == 'w') {
          game.paddle1.moveUp();
          s.write("move " + paddle1.y + "\n");
        } else if (key == 's') {
          game.paddle1.moveDown();
          s.write("move " + paddle1.y + "\n");
        }
      } else if (state == "guest") {
        if (key == 'w') {
          game.paddle2.moveUp();
          c.write("move " + paddle2.y + "\n");
        } else if (key == 's') {
          game.paddle2.moveDown();
          c.write("move " + paddle2.y + "\n");
        }
      }
    }


    if (state == "guest") {
      if (!pong.collide(paddle2.x, paddle2.y, paddle2.sizeX, paddle2.sizeY)) {
        pong.moveX();
        pong.moveY();
      } else {
        pong.moveY();
        c.write("bounce " + pong.posX + " " + pong.posY + " " + pong.speedX +
          " " + pong.speedY + " " + pong.dirX + " " + pong.dirY + "\n");
      }
    } else if (state == "host") {
      if (!pong.collide(paddle1.x, paddle1.y, paddle1.sizeX, paddle1.sizeY)) {
        pong.moveX();
        pong.moveY();
      } else {
        pong.moveY();
        s.write("bounce " + pong.posX + " " + pong.posY + " " + pong.speedX +
          " " + pong.speedY + " " + pong.dirX + " " + pong.dirY + "\n");
      }
    }
    /*
    if (!(pong.collide(paddle1.x, paddle1.y, paddle1.sizeX, paddle1.sizeY) 
     || pong.collide(paddle2.x, paddle2.y, paddle2.sizeX, paddle2.sizeY)))
     pong.moveX();
     
     pong.moveY();*/
  }

  public void display() {
    textSize(64);
    text(p1, 350, 50);
    text(p2, 450, 50);
    textSize(24);
    text(name1, 250, 55);
    text(name2, 550, 55);
    paddle1.display();
    paddle2.display();
    pong.display();
  }
}
class Paddle{
  float x, y, sizeX, sizeY;
  Paddle(float x, float y, float sizeX, float sizeY){
     this.x = x;
     this.y = y;
     this.sizeX = sizeX;
     this.sizeY = sizeY;
  }
  
  public void moveUp(){
    y -= 5; 
  }
  
  public void moveDown(){
    y += 5; 
  }
  
  public void display(){
    fill(255);
    rect(x, y, sizeX, sizeY);
  }
  
}
class Pong {
  float posX, posY;
  float speedX, speedY, dirX, dirY;
  float size;
  boolean score = false;
  
  Pong(){
    posX = 395;
    posY = 195;
    //posY = 20;
    speedX = 2.5f;
    speedY = 2.5f;
    dirX = 1;
    dirY = -1;
    size = 15;
  }
  
  public void moveX(){
    posX += dirX * speedX;
    if (posX > 800 || posX < 0)
    score = true;
  }
  
  public void moveY(){
    posY += dirY * speedY;
    float time, yCheck;
    if (dirY < 0)
      yCheck = 0;
    else
      yCheck = 400 - size;
    time = timeToY(yCheck);
    if (time < 1 && time >= 0) {
      dirY *= -1;
      posY = yCheck + dirY * speedY * (1 - time);
      
    }
      
  }
  
  public float timeToY(float y){
    return (posY - y) / (dirY * speedY);
  }
  
  public float timeToX(float x){
    return (posX - x) / (dirX * speedX);
  }
  
  public float yPosAtTime(float time){
    return  posY + dirY * speedY * time;
  }
  
  public boolean collide(float x, float y, float sizeX, float sizeY){
    float time;
    float xCheck;
    if (dirX == -1)
      xCheck = x + sizeX;
    else
      xCheck = x - size;
    time = timeToX(xCheck);
    if (time < 0 || time > 1)
      return false;
    float yCheck = yPosAtTime(time);
    if (yCheck < y - size || yCheck > y + sizeY)
      return false;
    
    float dirSend = (yCheck - y)/sizeY;
    float sendSpeed = abs(speedY/3.0f);
    if (sendSpeed < 0.5f)
      sendSpeed = 0.5f;
    
    if (dirSend < 0.333f)
      dirY -= sendSpeed;
    else if (dirSend > 0.667f)
      dirY += sendSpeed;
      
    posX = xCheck - dirX * speedX * (1 - time);
    dirX = -dirX;
    speedX += 0.5f;
     return true;
  }
  
  public void display(){
    fill(255);
    rect(posX, posY, size, size);
  }
  
}
class TextBox extends Button{
  
  boolean reset = true;
  boolean select = false;
  char lastKey = ' ';
  TextBox(float x, float y, float sx, float sy, String text) {
    super(x,y,sx,sy,text);
  }
  
  public void update(){
    if (!keyPressed)
      reset = true;
    if (key != lastKey)
      reset = true;
    if (keyPressed && select && reset){
      reset = false;
      lastKey = key;
      if (key == BACKSPACE){
        if (text.length() > 0)
          text = text.substring(0, text.length() - 1);
      }
      else if (key == ENTER)
        select = false;
      else
        text += key;
    }
    else if (mousePressed && !mouseIn())
      select = false;
    else if (mousePressed)
      select = true;
  }
  
}
  public void settings() {  size(800, 400); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "test" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
