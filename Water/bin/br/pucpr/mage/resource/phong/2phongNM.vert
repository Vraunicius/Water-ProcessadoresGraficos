#version 330

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uWorld;

uniform vec3 uCameraPosition;

in vec3 aPosition;
in vec2 aTexCoord;

in vec3 aNormal;
in vec3 aTangent;

out vec2 vTexCoord;

out vec3 tangentLightPos;
out vec3 tangentViewPos;
out vec3 tangentPosition;

uniform vec3 uLightDir;

void main() {
	vec4 worldPos = uWorld * vec4(aPosition, 1.0);
    gl_Position =  uProjection * uView * worldPos;
    vTexCoord = aTexCoord;
   
    vec3 T = normalize(vec3(uWorld * vec4(aTangent, 0.0)));
	vec3 N = normalize(vec3(uWorld * vec4(aNormal, 0.0)));
	
	T = normalize(T - dot(T, N) * N);
	vec3 B = cross(T, N);
	
    mat3 TBN = transpose(mat3(T, B, N));
	
	tangentLightPos = TBN * uLightDir;
	tangentViewPos = TBN * uCameraPosition;
	tangentPosition = TBN * vec3(uWorld * vec4(aPosition, 0.0));
}