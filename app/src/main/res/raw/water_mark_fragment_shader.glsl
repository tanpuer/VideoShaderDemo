precision mediump float;
uniform sampler2D uWaterMarkTextureSampler;
varying vec2 vTextureCoord;
void main()
{
    gl_FragColor = texture2D(uWaterMarkTextureSampler, vTextureCoord);
//    if (vTextureCoord.x > 0.5) {
//        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
//    } else {
//        gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
//    }
}
