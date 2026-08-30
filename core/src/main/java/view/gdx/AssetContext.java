package view.gdx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import view.gdx.catalog.*;

public final class AssetContext implements Disposable {
    public static final String RESOLUTION = "768";

    private final TextureBank textures;
    private final PamPlayer pamPlayer;
    private final Map<String, ClipRef> clipCache = new HashMap<>();
    private final String status;

    public AssetContext(VisualCatalog catalog) {
        FileHandle assetsRoot = Gdx.files.internal("assets");
        TextureBank bank = null;
        PamPlayer player = null;
        String loadStatus = "ok";
        try {
            bank = new TextureBank(RESOLUTION, assetsRoot);
            player = new PamPlayer(bank, assetsRoot);
            preload(catalog, player);
        } catch (RuntimeException e) {
            loadStatus = "libPVZ failed: " + e.getMessage();
            Gdx.app.error("AssetContext", loadStatus, e);
        }
        this.textures = bank;
        this.pamPlayer = player;
        this.status = loadStatus;
    }

    private void preload(VisualCatalog catalog, PamPlayer player) {
        Set<String> pamPaths = new HashSet<>();
        for (PlantVisualDef def : catalog.allPlants()) {
            pamPaths.add(def.pamPath);
        }
        for (ZombieVisualDef def : catalog.allZombies()) {
            pamPaths.add(def.pamPath);
            if(def.companions !=null){
                for(CompanionVisual c :  def.companions){
                    if(c.pamPath != null){
                        pamPaths.add(c.pamPath);
                    }
                }
            }
        }
        for (ProjectileVisualDef def : catalog.allProjectile()){
            pamPaths.add(def.pamPath);
        }
        if(catalog.barrel() != null) {
            catalog.barrel();
            pamPaths.add(catalog.barrel().pamPath);
        }
        for (String path : pamPaths) {
            player.loadSync(path);
        }
    }

    public void update() {
        if (textures != null) {
            textures.update();
        }
    }

    public ClipRef clip(String pamPath, String clipName) {
        if (pamPlayer == null || pamPath == null || clipName == null) {
            return null;
        }
        String key = pamPath + "#" + clipName;
        ClipRef cached = clipCache.get(key);
        if (cached != null) {
            return cached;
        }
        if (clipCache.containsKey(key)) {
            return null;
        }
        try {
            ClipRef clip = pamPlayer.getClip(pamPath, clipName);
            clipCache.put(key, clip);
            return clip;
        } catch (IllegalArgumentException e) {
            Gdx.app.debug("AssetContext", "Missing PAM clip " + clipName + " in " + pamPath);
            clipCache.put(key, null);
            return null;
        }
    }

    public TextureRegion region(String imageId) {
        if (textures == null || imageId == null) {
            return null;
        }
        return textures.region(imageId);
    }

    public PamPlayer pamPlayer() {
        return pamPlayer;
    }

    public String status() {
        return status;
    }

    @Override
    public void dispose() {
        clipCache.clear();
        if (textures != null) {
            textures.dispose();
        }
    }
}
