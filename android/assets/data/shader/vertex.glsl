attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec4 vColor;
varying vec2 vTexCoord;
varying vec2 v_lightCoord;

void main() {
   vColor = a_color;
   vTexCoord = a_texCoord0;
   gl_Position = u_projTrans * a_position;
   v_lightCoord = gl_Position.xy;   
   v_lightCoord =  0.5 + v_lightCoord.xy * 0.5;
}