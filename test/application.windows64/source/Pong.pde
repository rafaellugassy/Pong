class Pong {
  float posX, posY;
  float speedX, speedY, dirX, dirY;
  float size;
  boolean score = false;
  
  Pong(){
    posX = 395;
    posY = 195;
    //posY = 20;
    speedX = 2.5;
    speedY = 2.5;
    dirX = 1;
    dirY = -1;
    size = 15;
  }
  
  void moveX(){
    posX += dirX * speedX;
    if (posX > 800 || posX < 0)
    score = true;
  }
  
  void moveY(){
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
  
  float timeToY(float y){
    return (posY - y) / (dirY * speedY);
  }
  
  float timeToX(float x){
    return (posX - x) / (dirX * speedX);
  }
  
  float yPosAtTime(float time){
    return  posY + dirY * speedY * time;
  }
  
  boolean collide(float x, float y, float sizeX, float sizeY){
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
    float sendSpeed = abs(speedY/3.0);
    if (sendSpeed < 0.5)
      sendSpeed = 0.5;
    
    if (dirSend < 0.333)
      dirY -= sendSpeed;
    else if (dirSend > 0.667)
      dirY += sendSpeed;
      
    posX = xCheck - dirX * speedX * (1 - time);
    dirX = -dirX;
    speedX += 0.5;
     return true;
  }
  
  void display(){
    fill(255);
    rect(posX, posY, size, size);
  }
  
}
