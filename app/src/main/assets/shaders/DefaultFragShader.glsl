 precision mediump float;

   varying vec4 color;
   uniform sampler2D tex;
   varying vec2 texCoordOut;

 void main(){

         gl_FragColor = texture2D(tex, texCoordOut) * color;

 }