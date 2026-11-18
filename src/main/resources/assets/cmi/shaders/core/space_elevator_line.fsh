#version 150

uniform vec4 ColorModulator;
uniform vec2 LineStart;
uniform vec2 LineEnd;
uniform vec4 LineMetrics;

in vec4 vertexColor;
in vec2 guiPos;

out vec4 fragColor;

float segmentDistance(vec2 point, vec2 start, vec2 end) {
    vec2 delta = end - start;
    float lengthSquared = max(dot(delta, delta), 0.0001);
    float projection = clamp(dot(point - start, delta) / lengthSquared, 0.0, 1.0);
    vec2 closest = start + delta * projection;
    return length(point - closest);
}

void main() {
    float thickness = max(LineMetrics.x, 0.5);
    float feather = max(LineMetrics.y, 0.001);
    float distanceToLine = segmentDistance(guiPos, LineStart, LineEnd);
    float alpha = 1.0 - smoothstep(thickness, thickness + feather, distanceToLine);
    if (alpha <= 0.0) {
        discard;
    }

    vec4 color = vertexColor * ColorModulator;
    fragColor = vec4(color.rgb, color.a * alpha);
}