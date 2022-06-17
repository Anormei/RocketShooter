 attribute vec2 position;
 attribute vec4 inColor;
 attribute vec2 texCoordIn;
 uniform mat4 mvp;

 varying vec2 texCoordOut;
 varying vec4 color;

 void main(){

     color = inColor;
     texCoordOut = texCoordIn;
     gl_Position = mvp * vec4(position, 0, 1.0);


 }