package view.gdx.lawn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import model.data.content.chapter.ChapterType;
import view.gdx.AssetContext;

public final class LawnBackgroundRenderer {
    private static final float BASE_HEIGHT = 720f;

    private ChapterType boundChapter;
    private LawnBackgroundDefs.BoardArt art;
    private TextureRegion left;
    private TextureRegion center;
    private TextureRegion right;
    private TextureRegion centerDraw;
    private float worldHeight = BASE_HEIGHT;
    private float drawX;
    private float drawY;
    private float leftW;
    private float centerW;
    private float rightW;
    private float stripH;
    private float scale = 1f;

    public void bind(AssetContext assets, ChapterType chapter, float worldWidth, float worldHeight) {
        if (assets == null) {
            clear();
            return;
        }
        boolean chapterChanged = chapter != boundChapter || art == null || left == null || center == null
                || right == null;
        if (chapterChanged) {
            boundChapter = chapter;
            art = LawnBackgroundDefs.forChapter(chapter);
            left = assets.region(art.leftId);
            center = assets.region(art.centerId);
            right = assets.region(art.rightId);
        }
        this.worldHeight = Math.max(1f, worldHeight);
        layout();
    }

    public boolean ready() {
        return left != null && center != null && right != null;
    }

    public float drawX() {
        return drawX;
    }

    public float drawY() {
        return drawY;
    }

    public float leftW() {
        return leftW;
    }

    public float centerW() {
        return centerW;
    }

    public float rightW() {
        return rightW;
    }

    public float stripH() {
        return stripH;
    }

    public float scale() {
        return scale;
    }

    public void render(SpriteBatch batch) {
        if (!ready() || batch == null) {
            return;
        }
        float x = drawX;
        batch.draw(left, x, drawY, leftW, stripH);
        x += leftW;
        batch.draw(centerDraw, x, drawY, centerW, stripH);
        x += centerW;
        batch.draw(right, x, drawY, rightW, stripH);
    }

    private void layout() {
        if (!ready()) {
            return;
        }
        int leftH = Math.max(1, left.getRegionHeight());
        int rightH = Math.max(1, right.getRegionHeight());
        int sideH = Math.min(leftH, rightH);
        int centerH = Math.max(1, center.getRegionHeight());

        if (centerH > sideH) {
            centerDraw = new TextureRegion(center, 0, 0, center.getRegionWidth(), sideH);
        } else {
            centerDraw = center;
        }

        stripH = worldHeight;
        scale = stripH / sideH;
        leftW = left.getRegionWidth() * scale;
        centerW = centerDraw.getRegionWidth() * scale;
        rightW = right.getRegionWidth() * scale;
        drawX = 0f;
        drawY = 0f;
    }

    private void clear() {
        boundChapter = null;
        art = null;
        left = null;
        center = null;
        right = null;
        centerDraw = null;
    }
}
