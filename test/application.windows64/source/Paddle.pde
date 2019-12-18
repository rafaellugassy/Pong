class Paddle{
  float x, y, sizeX, sizeY;
  Paddle(float x, float y, float sizeX, float sizeY){
     this.x = x;
     this.y = y;
     this.sizeX = sizeX;
     this.sizeY = sizeY;
  }
  
  void moveUp(){
    y -= 5; 
  }
  
  void moveDown(){
    y += 5; 
  }
  
  void display(){
    fill(255);
    rect(x, y, sizeX, sizeY);
  }
  
}
