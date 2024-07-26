package core.ecs;

import java.util.List;
import java.util.Map;

public record EcsSnapshot(
        int currentId,
        List<SnapshotEntity> entities,
        Map<String, Map<Integer, Object>> componentMaps) {
}
