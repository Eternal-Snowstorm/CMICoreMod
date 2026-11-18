#version 150

uniform vec4 ColorModulator;
uniform vec2 RectSize;
uniform vec4 PanelMetrics;
uniform vec4 StrokeColor;

in vec4 vertexColor;
in vec2 localPos;

out vec4 fragColor;

float roundedBoxSdf(vec2 point, vec2 halfSize, float radius) {
    vec2 delta = abs(point) - halfSize + vec2(radius);
    return length(max(delta, 0.0)) + min(max(delta.x, delta.y), 0.0) - radius;
}

void main() {
    vec2 halfSize = RectSize * 0.5;
    float radius = min(PanelMetrics.x, min(halfSize.x, halfSize.y));
    float feather = max(PanelMetrics.y, 0.001);
    float strokeWidth = max(PanelMetrics.z, 0.0);
    float sdf = roundedBoxSdf(localPos - halfSize, halfSize, radius);
    float fillMask = 1.0 - smoothstep(-feather, feather, sdf);
    if (fillMask <= 0.0) {
        discard;
    }

    vec4 fillColor = vertexColor * ColorModulator;
    float strokeMask = strokeWidth > 0.0 ? (1.0 - smoothstep(max(strokeWidth - feather, 0.0), strokeWidth + feather, abs(sdf))) : 0.0;
    float strokeBlend = strokeMask * StrokeColor.a;
    vec4 mixedColor = mix(fillColor, vec4(StrokeColor.rgb, fillColor.a), strokeBlend);
    mixedColor.a = max(fillColor.a, StrokeColor.a * strokeMask) * fillMask;
    fragColor = mixedColor;
}