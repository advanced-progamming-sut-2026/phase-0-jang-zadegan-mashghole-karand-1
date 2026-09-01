package pvz.libpvz.pam;

public final class ProjectilePamAnchor {
    private ProjectilePamAnchor() {
    }


    public static boolean drawOriginDelta(ClipRef clip, float time, boolean loop, float[] out) {
        if (clip == null || out == null || out.length < 2) {
            return false;
        }
        BakedAnimation.BakedFrame frame = resolveFrame(clip, time, loop);
        if (frame == null) {
            return false;
        }
        float[] center = frameCenter(frame);
        if (center == null) {
            return false;
        }
        BakedAnimation ba = clip.ba;
        float cw = ba.canvasWidth > 0f ? ba.canvasWidth : 1f;
        float ch = ba.canvasHeight > 0f ? ba.canvasHeight : 1f;
        out[0] = cw * 0.5f - center[0];
        out[1] = center[1] - ch * 0.5f;
        return true;
    }

    private static BakedAnimation.BakedFrame resolveFrame(ClipRef clip, float time, boolean loop) {
        BakedAnimation ba = clip.ba;
        int[] range = clip.range;
        if (ba == null || ba.frames == null || ba.frames.length == 0 || range == null || range.length < 2) {
            return null;
        }

        int span = Math.max(1, range[1] - range[0] + 1);
        int fi = (int) Math.floor(time * ba.frameRate);
        if (fi < 0) {
            fi = 0;
        }
        int local = loop ? ((fi % span) + span) % span : Math.min(fi, span - 1);
        int frameIndex = range[0] + local;
        if (frameIndex < 0 || frameIndex >= ba.frames.length) {
            return null;
        }

        BakedAnimation.BakedFrame frame = ba.frames[frameIndex];
        if (frame == null || frame.count <= 0 || frame.corners == null) {
            return null;
        }
        return frame;
    }

    private static float[] frameCenter(BakedAnimation.BakedFrame frame) {
        float sumX = 0f;
        float sumY = 0f;
        int quads = 0;
        for (int i = 0; i < frame.count; i++) {
            int c8 = i * 8;
            if (c8 + 7 >= frame.corners.length) {
                break;
            }
            sumX += (frame.corners[c8]
                    + frame.corners[c8 + 2]
                    + frame.corners[c8 + 4]
                    + frame.corners[c8 + 6]) * 0.25f;
            sumY += (frame.corners[c8 + 1]
                    + frame.corners[c8 + 3]
                    + frame.corners[c8 + 5]
                    + frame.corners[c8 + 7]) * 0.25f;
            quads++;
        }
        if (quads == 0) {
            return null;
        }
        return new float[] { sumX / quads, sumY / quads };
    }
}
