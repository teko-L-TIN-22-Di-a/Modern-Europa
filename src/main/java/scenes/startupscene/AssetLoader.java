package scenes.startupscene;

import core.graphics.ImageHelper;
import scenes.lib.AnimationConstants;
import scenes.lib.TextureConstants;
import scenes.lib.rendering.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static scenes.lib.config.RenderingConfig.*;

public class AssetLoader {

    private final TextureAtlas textureAtlas = new TextureAtlas();
    private final AnimationAtlas animationAtlas = new AnimationAtlas();

    private final List<Color> playerColors = List.of(Color.cyan, Color.red, Color.green, Color.blue);

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }
    public AnimationAtlas getAnimationAtlas() {
        return animationAtlas;
    }

    public AssetLoader loadTileSheet(BufferedImage tileSheet) {
        ImageHelper.keyOut(tileSheet, Color.WHITE);

        var frameMap = Map.ofEntries(
                Map.entry(TextureConstants.DEFAULT_GROUND, 0),
                Map.entry(TextureConstants.MINEABLE_GROUND, 1),
                Map.entry(TextureConstants.HIGHLIGHT, 2),
                Map.entry(TextureConstants.HIGHLIGHT_ERROR, 3)
        );

        textureAtlas.addSplit(TextureConstants.TILE_SHEET, tileSheet, TILE_SIZE);
        for(var key : frameMap.keySet()) {
            textureAtlas.addSingleFrame(key, TextureConstants.TILE_SHEET, frameMap.get(key));
        }

        return this;
    }

    public AssetLoader loadAttackSheet(BufferedImage attackSheet) {

        textureAtlas.addSplit(TextureConstants.ATTACK_SHEET, attackSheet, ATTACK_SIZE);

        var attackAnimations = new AnimationBuilder()
                .addAnimation(AnimationConstants.FLYING, TextureConstants.ATTACK_SHEET, List.of(
                        0, 1, 2, 1, 2, 1, 2
                ), 20, true)
                .addAnimation(AnimationConstants.EXPLODE, TextureConstants.ATTACK_SHEET, List.of(
                       1, 2, 3, 4, 5
                ), 10, AnimationConstants.EXPLODED)
                .addLoopingAnimation(AnimationConstants.EXPLODED, TextureConstants.ATTACK_SHEET, List.of(
                        -1 // Not show anything anymore
                ))
                .build(AnimationConstants.FLYING);
        animationAtlas.addEntry(AnimationConstants.ATTACK_ANIMATIONS, attackAnimations);

        return this;
    }

    public AssetLoader loadBuildingSheet(BufferedImage buildingsSheet) {
        ImageHelper.keyOut(buildingsSheet, Color.WHITE);

        loadColoredBuildingSheet(buildingsSheet, Optional.empty(), Optional.empty());

        var playerId = 0;
        for(var color : playerColors) {
            loadColoredBuildingSheet(buildingsSheet, Optional.of(color), Optional.of(playerId++));
        }

        return this;
    }

    private void loadColoredBuildingSheet(BufferedImage buildingsSheet, Optional<Color> color, Optional<Integer> playerId) {
        var playerSuffix = "";
        var coloredBuildingsSheet = buildingsSheet;

        if(playerId.isPresent()) {
            playerSuffix = playerId.get().toString();
        }

        if(color.isPresent()) {
            coloredBuildingsSheet = ImageHelper.clone(buildingsSheet);
            ImageHelper.keyOut(coloredBuildingsSheet, Color.MAGENTA, color.get().getRGB());
        }

        var frameMap = Map.ofEntries(
                Map.entry(TextureConstants.BASE, 0),
                Map.entry(TextureConstants.GENERATOR, 1),
                Map.entry(TextureConstants.MINER, 2),
                Map.entry(TextureConstants.CONSTRUCTION_SITE, 3)
        );

        textureAtlas.addSplit(TextureConstants.BUILDINGS_SHEET + playerSuffix, coloredBuildingsSheet, BUILDING_SIZE);
        for(var key : frameMap.keySet()) {
            textureAtlas.addSingleFrame(key + playerSuffix, TextureConstants.BUILDINGS_SHEET + playerSuffix, frameMap.get(key));
        }
    }

    public AssetLoader loadCombatUnitSheet(BufferedImage combatUnitSheet) {
        ImageHelper.keyOut(combatUnitSheet, Color.WHITE);

        loadColoredBuildingSheet(combatUnitSheet, Optional.empty(), Optional.empty());

        var playerId = 0;
        for(var color : playerColors) {
            loadColoredCombatUnitSheet(combatUnitSheet, Optional.of(color), Optional.of(playerId++));
        }

        return this;
    }

    private void loadColoredCombatUnitSheet(BufferedImage combatUnitSheet, Optional<Color> color, Optional<Integer> playerId) {
        var playerSuffix = "";
        var coloredCombatUnitSheet = combatUnitSheet;

        if(playerId.isPresent()) {
            playerSuffix = playerId.get().toString();
        }

        if(color.isPresent()) {
            coloredCombatUnitSheet = ImageHelper.clone(combatUnitSheet);
            ImageHelper.keyOut(coloredCombatUnitSheet, Color.MAGENTA, color.get().getRGB());
        }

        var frameMap = Map.ofEntries(
                Map.entry(TextureConstants.MECH_UNIT, 0),
                Map.entry(TextureConstants.BALL_UNIT, 4)
        );

        textureAtlas.addSplit(TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, coloredCombatUnitSheet, COMBAT_UNIT_SIZE);
        for(var key : frameMap.keySet()) {
            textureAtlas.addSingleFrame(key + playerSuffix, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, frameMap.get(key));
        }

        var mechAnimations = new AnimationBuilder()
                .addLoopingAnimation(AnimationConstants.LEFT, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(3))
                .addLoopingAnimation(AnimationConstants.UP, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(2))
                .addLoopingAnimation(AnimationConstants.RIGHT, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(1))
                .addLoopingAnimation(AnimationConstants.DOWN, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(0))
                .build(AnimationConstants.RIGHT);
        animationAtlas.addEntry(AnimationConstants.MECH_UNIT_ANIMATIONS + playerSuffix, mechAnimations);

        var ballAnimations = new AnimationBuilder()
                .addLoopingAnimation(AnimationConstants.LEFT, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(7))
                .addLoopingAnimation(AnimationConstants.UP, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(6))
                .addLoopingAnimation(AnimationConstants.RIGHT, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(5))
                .addLoopingAnimation(AnimationConstants.DOWN, TextureConstants.COMBAT_UNIT_SHEET + playerSuffix, List.of(4))
                .build(AnimationConstants.RIGHT);
        animationAtlas.addEntry(AnimationConstants.BALL_UNIT_ANIMATIONS + playerSuffix, ballAnimations);

    }

}
