class TextBox extends Button{
  
  boolean reset = true;
  boolean select = false;
  char lastKey = ' ';
  TextBox(float x, float y, float sx, float sy, String text) {
    super(x,y,sx,sy,text);
  }
  
  void update(){
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
