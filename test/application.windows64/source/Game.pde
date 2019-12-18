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

  void update() {
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

  void display() {
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
