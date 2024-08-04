package scenes.gamescene.rendering;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.components.Position;
import core.ecs.helper.CameraHelper;
import core.input.MouseListener;
import core.util.Bounds;
import core.util.Vector2f;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.components.Selection;
import scenes.lib.rendering.IsometricHelper;
import scenes.lib.rendering.Renderer;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.MouseEvent;

public class SelectionRenderer implements Renderer {

    private Vector2f cameraOffset = Vector2f.ZERO;
    private boolean enabled = true;
    private Vector2f scale;
    private boolean selecting = false;
    private Vector2f selectionAnchor = null;
    private Vector2f currentPosition = null;

    private final Ecs ecs;
    private final MouseListener mouseListener;

    private final PublishSubject<Bounds> boundsSelection = PublishSubject.create();
    private final PublishSubject<Vector2f> pointSelection = PublishSubject.create();

    public SelectionRenderer(EngineContext context, Vector2f scale) {
        this.scale = scale;
        ecs = context.getService(Ecs.class);
        mouseListener = context.getService(MouseListener.class);

        mouseListener.bindMouseClicked(this::onMouseClicked);
        mouseListener.bindMousePressed(this::onMousePressed);
        mouseListener.bindMouseReleased(this::onMouseReleased);
        mouseListener.bindMouseDragged(this::onMouseDragged);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void render(Graphics2D g2d) {
        if(!enabled) return;

        cameraOffset = CameraHelper.getCameraOffset(ecs);

        var selectableUnits = ecs.view(Position.class, Selection.class);

        for(var unit : selectableUnits) {
            if(!unit.component2().selected()) continue;

            var pos = unit.component1().position();
            var bounds = unit.component2().bounds().move(IsometricHelper.toScreenSpace(pos).add(cameraOffset));

            g2d.setColor(new Color(255, 255, 255));
            g2d.drawRect(
                    (int) bounds.position().x(), (int) bounds.position().y(),
                    (int) bounds.size().x(), (int) bounds.size().y());
        }

        if(!selecting || currentPosition == null || selectionAnchor == null) {
            return;
        }

        var selectionBounds = getSelectionBounds();

        g2d.setColor(new Color(255,255,255));
        g2d.drawRect(
                (int) selectionBounds.position().x(), (int) selectionBounds.position().y(),
                (int) selectionBounds.size().x(), (int) selectionBounds.size().y());
        g2d.setColor(new Color(255,255,255, 50));
        g2d.fillRect(
                (int) selectionBounds.position().x(), (int) selectionBounds.position().y(),
                (int) selectionBounds.size().x(), (int) selectionBounds.size().y());
    }

    public Subscription bindBoundsSelection(Action1<Bounds> action) {
        return boundsSelection.subscribe(action);
    }
    public Subscription bindPointSelection(Action1<Vector2f> action) {
        return pointSelection.subscribe(action);
    }

    private void onMouseClicked(MouseEvent e) {
        if(!enabled) return;

        if(e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if(currentPosition != null) return;

        var selectionPoint = Vector2f.of(e.getX(), e.getY()).div(scale).sub(cameraOffset);
        pointSelection.onNext(selectionPoint);
    }

    private void onMouseDragged(MouseEvent e) {
        if(!enabled) return;

        if(!selecting) {
            return;
        }

        currentPosition = Vector2f.of(e.getX(), e.getY()).div(scale);
    }

    private void onMousePressed(MouseEvent e) {
        if(!enabled) return;

        if(e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        selecting = true;
        selectionAnchor = Vector2f.of(e.getX(), e.getY()).div(scale);
    }

    private void onMouseReleased(MouseEvent e) {
        if(!enabled) return;

        if(e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        var selectionBounds = getSelectionBounds().move(cameraOffset.mul(-1));
        boundsSelection.onNext(selectionBounds);

        selecting = false;
        currentPosition = null;
        selectionAnchor = null;
    }

    private Bounds getSelectionBounds() {
        if(currentPosition == null || selectionAnchor == null) {
            return new Bounds(Vector2f.ZERO, Vector2f.ZERO);
        }

        var minPos = Vector2f.of(Math.min(currentPosition.x(), selectionAnchor.x()), Math.min(currentPosition.y(), selectionAnchor.y()));
        var size = Vector2f.of(Math.abs(currentPosition.x() - selectionAnchor.x()), Math.abs(currentPosition.y() - selectionAnchor.y()));

        return new Bounds(minPos, size);
    }

}
