#version 330 core
out vec4 color;
in vec2 vTexCoord;

uniform sampler2D uTexture;

uniform float tonalidade;

void main()
{             
    const float gamma = 2.2;
    vec3 hdrColor = texture(uTexture, vTexCoord).rgb;
  
    // Exposure tone mapping
    vec3 mapped = vec3(1.0) - exp(-hdrColor * tonalidade);
    // Gamma correction 
    mapped = pow(mapped, vec3(1.0 / gamma));
  
    color = vec4(mapped, 1.0);
}