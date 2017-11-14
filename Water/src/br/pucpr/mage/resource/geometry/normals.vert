#version 330

in vec3 aPosition;
in vec3 aNormal;

out vec3 vNormal;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uWorld;


void main() {
	gl_Position = uProjection * uView * uWorld * vec4(aPosition, 1.0);

    mat3 normalMatrix = mat3(transpose(inverse(uView * uWorld)));
    vNormal = vec3(uProjection * vec4(normalMatrix * aNormal, 1.0));
}
