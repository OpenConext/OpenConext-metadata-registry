package manage.hook;

import manage.conf.MetaDataAutoConfiguration;
import manage.model.EntityType;
import manage.model.MetaData;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmptyRevisionHook extends MetaDataHookAdapter {

    private MetaDataAutoConfiguration metaDataAutoConfiguration;

    private List<String> ignoreInDiff = Arrays.asList("revisionnote");

    public EmptyRevisionHook(MetaDataAutoConfiguration metaDataAutoConfiguration) {
        this.metaDataAutoConfiguration = metaDataAutoConfiguration;
    }

    @Override
    public MetaData prePut(MetaData previous, MetaData newMetaData) {
        Map<String, Object> previousData = previous.getData();
        Map<String, Object> newData = newMetaData.getData();
        boolean eq = mapEquality(previousData, newData) && mapEquality(newData, previousData);
        if (eq) {
            Schema schema = metaDataAutoConfiguration.schema(EntityType.RP.getType());
            throw new ValidationException(schema, "No data is changed. An update would result in an empty revision", "empty-revision");
        }
        return super.prePut(previous, newMetaData);
    }

    private boolean mapEquality(Map<String, Object> first, Map<String, Object> second) {
        return first.entrySet().stream()
                .allMatch(e -> ignoreInDiff.contains(e.getKey()) ||
                        (e.getValue() == null && second.get(e.getKey()) == null) ||
                        e.getValue().equals(second.get(e.getKey())));
    }
}
