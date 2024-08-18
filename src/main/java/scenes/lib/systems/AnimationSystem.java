package scenes.lib.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.RunnableSystem;
import core.loading.AssetManager;
import scenes.lib.AssetConstants;
import scenes.lib.components.Sprite;
import scenes.lib.components.SpriteAnimation;
import scenes.lib.config.RenderingConfig;
import scenes.lib.rendering.Animation;
import scenes.lib.rendering.AnimationAtlas;
import scenes.lib.rendering.AnimationAtlasEntry;

public class AnimationSystem implements RunnableSystem {

    private final Ecs ecs;
    private final AnimationAtlas animationAtlas;

    public AnimationSystem(EngineContext context) {
        ecs = context.getService(Ecs.class);

        var assetManager = context.<AssetManager>getService(AssetManager.class);

        animationAtlas = assetManager.getAsset(AssetConstants.ANIMATION_ATLAS);
    }

    @Override
    public void update(double delta) {
        var animatedSprites = ecs.view(Sprite.class, SpriteAnimation.class);

        for(var sprite : animatedSprites) {
            updateAnimation(sprite, delta);
        }

    }

    public void updateAnimation(EcsView2<Sprite, SpriteAnimation> animatedSprite, double delta) {

        var sprite = animatedSprite.component1();
        var spriteAnimation = animatedSprite.component2();

        var animationReference = animationAtlas.getEntry(spriteAnimation.animationReference());

        if(animationReference == null) {
            return;
        }

        if(spriteAnimation.currentAnimation() == null) {
            // Setup Initial animation.
            spriteAnimation = spriteAnimation.play(animationReference.startingAnimation());
        }

        if(spriteAnimation.currentFrame() == null) {
            // Init manually started animations.
            spriteAnimation = initFrame(spriteAnimation, animationReference, 0);
        }

        spriteAnimation = spriteAnimation.elapse(delta);

        if(spriteAnimation.hasElapsed()) {
            spriteAnimation = initNextFrame(spriteAnimation, animationReference);
        }

        sprite = updateSprite(sprite, spriteAnimation, animationReference);

        ecs.setComponent(animatedSprite.entityId(), sprite);
        ecs.setComponent(animatedSprite.entityId(), spriteAnimation);
    }

    private Sprite updateSprite(Sprite sprite, SpriteAnimation spriteAnimation, AnimationAtlasEntry animationReference) {
        var currentAnimation = animationReference.animations().get(spriteAnimation.currentAnimation());
        var currentFrame = currentAnimation.frames().get(spriteAnimation.currentFrame());

        return sprite.usingResource(currentFrame.textureKey());
    }

    private SpriteAnimation initNextFrame(SpriteAnimation spriteAnimation, AnimationAtlasEntry animationReference) {
        var currentAnimation = animationReference.animations().get(spriteAnimation.currentAnimation());

        var newFrameIndex = spriteAnimation.currentFrame() + 1;

        if(newFrameIndex >= currentAnimation.frames().size()) {
            return initNextAnimation(spriteAnimation, animationReference);
        }

        return initFrame(spriteAnimation, animationReference, newFrameIndex);
    }

    private SpriteAnimation initNextAnimation(SpriteAnimation spriteAnimation, AnimationAtlasEntry animationReference) {
        var currentAnimation = animationReference.animations().get(spriteAnimation.currentAnimation());

        if(currentAnimation.looping()) {
            return initFrame(spriteAnimation.play(spriteAnimation.currentAnimation()), animationReference, 0);
        }

        if(currentAnimation.leadingAnimation() != null) {
            return initFrame(spriteAnimation.play(currentAnimation.leadingAnimation()), animationReference, 0);
        }

        return initFrame(spriteAnimation.play(spriteAnimation.previousAnimation()), animationReference, 0);
    }

    private SpriteAnimation initFrame(SpriteAnimation spriteAnimation, AnimationAtlasEntry animationReference, int frameId) {
        var currentAnimation = animationReference.animations().get(spriteAnimation.currentAnimation());

        var frame = currentAnimation.frames().get(frameId);

        return spriteAnimation.updateState(
                frameId,
                (frame.time() * RenderingConfig.FRAME_RATE) / currentAnimation.speed()
        );
    }

}
