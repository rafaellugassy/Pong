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

  void disable() {
    disabled = true;
  }

  boolean mouseIn() {
    if (disabled)
      return false;
    return mouseX > x && mouseX < x + sx && mouseY > y && mouseY < y + sy;
  }

  void display() {
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
