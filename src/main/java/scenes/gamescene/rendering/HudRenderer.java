package scenes.gamescene.rendering;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.components.Position;
import core.input.MouseListener;
import core.util.Bounds;
import core.util.Vector2f;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.components.*;
import scenes.lib.helper.CameraHelper;
import scenes.lib.rendering.Renderer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HudRenderer implements Renderer {

    private final Ecs ecs;
    private final int playerId;

    public HudRenderer(EngineContext context, int playerId) {
        ecs = context.getService(Ecs.class);
        this.playerId = playerId;
    }

    @Override
    public void render(Graphics2D g2d) {
        var currentInfo = ecs.view(PlayerResources.class)
                .stream().filter(info -> info.component().playerId() == playerId)
                .findFirst();
        var entities = ecs
                .view(Powered.class, UnitInfo.class)
                .stream().filter(entity -> entity.component2().playerId() == playerId)
                .toList();
        var generators = ecs
                .view(Generator.class, UnitInfo.class)
                .stream().filter(entry -> entry.component2().playerId() == playerId)
                .toList();

        var availablePower = 0f;
        for (var generator : generators) {
            availablePower += generator.component1().energy();
        }

        g2d.setColor(new Color(50,50,50, 200));
        g2d.fillRect(0, 0, 164, 64);

        var drawY = 24;

        g2d.setColor(Color.WHITE);

        if(currentInfo.isPresent()) {

            g2d.drawString("[ Minerals: " + currentInfo.get().component().minerals() + " ]", 24, drawY);
            drawY += 24;

        }

        if(!entities.isEmpty()) {

            g2d.drawString("[ Power: " + availablePower + " | " + entities.size() + " ]", 24, drawY);

        }

    }

    @Override
    public void setSize(Vector2f size) {
        // Do nothing
    }

    @Override
    public void setScale(Vector2f scale) {
        // Do nothing
    }
}
