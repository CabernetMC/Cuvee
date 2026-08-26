package sh.miles.metatrace.json.rule;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sh.miles.metatrace.meta.rule.RuleAction;
import sh.miles.metatrace.meta.rule.VersionRule;
import sh.miles.metatrace.meta.rule.VersionRuleOs;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class VersionRuleAdapter implements JsonSerializer<VersionRule>, JsonDeserializer<VersionRule> {

    @Override
    public JsonElement serialize(final VersionRule rule, final Type type, final JsonSerializationContext context) {
        final var parent = new JsonObject();
        parent.addProperty("action", rule.action().id());
        if (rule.os() != null) {
            parent.add("os", context.serialize(rule.os(), VersionRuleOs.class));
        }
        if (!rule.features().isEmpty()) {
            final var features = new JsonObject();
            for (final Map.Entry<String, Boolean> feature : rule.features().entrySet()) {
                features.addProperty(feature.getKey(), feature.getValue());
            }
            parent.add("features", features);
        }
        return parent;
    }

    @Override
    public VersionRule deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final RuleAction action = RuleAction.fromId(parent.get("action").getAsString());

        final VersionRuleOs os = parent.has("os")
                ? context.deserialize(parent.getAsJsonObject("os"), VersionRuleOs.class)
                : null;

        final Map<String, Boolean> features = new HashMap<>();
        if (parent.has("features")) {
            final var featureObject = parent.getAsJsonObject("features");
            for (final String feature : featureObject.keySet()) {
                features.put(feature, featureObject.get(feature).getAsBoolean());
            }
        }

        return new VersionRule(action, os, features);
    }
}
