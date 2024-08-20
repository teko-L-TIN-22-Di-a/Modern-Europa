package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.RunnableSystem;
import core.ecs.components.Position;
import core.util.InterpolateHelper;
import scenes.lib.AnimationConstants;
import scenes.lib.components.AttackParticle;
import scenes.lib.components.SpriteAnimation;

public class AttackParticleSystem implements RunnableSystem {

    private final Ecs ecs;

    public AttackParticleSystem(EngineContext context) {
        ecs = context.getService(Ecs.class);
    }

    @Override
    public void update(double delta) {
        var particles = ecs.view(Position.class, AttackParticle.class, SpriteAnimation.class);

        for(var particle : particles) {

            var position = particle.component1().position();

            var movement = position.sub(particle.component2().target());
            if(Math.abs(movement.x()) + Math.abs(movement.z()) >= 0.01) {
                var newPos = InterpolateHelper.interpolateLinear(
                        position.toVector2fxz(), particle.component2().target().toVector2fxz(), 0.1f);

                ecs.setComponent(particle.entityId(), new Position(newPos.toVector3fy(particle.component1().position().y())));
                continue;
            }

            var currentAnimation = particle.component3().currentAnimation();

            if(currentAnimation.equals(AnimationConstants.FLYING)) {
                ecs.setComponent(particle.entityId(), particle.component3().play(AnimationConstants.EXPLODE));
            }

            if(currentAnimation.equals(AnimationConstants.EXPLODED)) {
                ecs.delete(particle.entityId());
            }

        }

    }
}
