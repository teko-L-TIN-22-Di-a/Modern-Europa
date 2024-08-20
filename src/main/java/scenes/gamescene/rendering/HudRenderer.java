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
import scenes.lib.components.PlayerResources;
import scenes.lib.components.Selection;
import scenes.lib.helper.CameraHelper;
import scenes.lib.rendering.Renderer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HudRenderer implements Renderer {

    private final Ecs ecs;

    private int playerId;

    public HudRenderer(EngineContext context, int playerId) {
        ecs = context.getService(Ecs.class);
        this.playerId = playerId;
    }

    @Override
    public void render(Graphics2D g2d) {
        var resourceInfos = ecs.view(PlayerResources.class);
        var currentInfo = resourceInfos.stream().filter(info -> info.component().playerId() == playerId).findFirst();

        if(!currentInfo.isPresent()) {
            return;
        }

        g2d.setColor(Color.WHITE);
        g2d.drawString("[ Minerals: " + currentInfo.get().component().minerals() + " ]", 24, 24);
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
