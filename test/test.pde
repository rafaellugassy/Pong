// 

import processing.net.*;

void setup() {
  size(800, 400);
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

void draw() {
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

void keyPressed() {
  if (state == "host") {
    if (key == ENTER) {
      game = new Game(game.name1, game.name2, game.p1, game.p2);
      start = false;
      s.write("reset\n");
    }
  }
}

void mouseClicked() {
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
