package pvz.libpvz.pam;

public final class PamClipTiming {
    private PamClipTiming() {
    }

    public static float durationSeconds(ClipRef clip) {
        if (clip == null || clip.ba == null || clip.range == null || clip.range.length < 2) {
            return 0.5f;
        }
        int span = Math.max(1, clip.range[1] - clip.range[0] + 1);
        float rate = clip.ba.frameRate > 0f ? clip.ba.frameRate : 12f;
        return span / rate;
    }
}
