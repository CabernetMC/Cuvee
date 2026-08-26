package sh.miles.metatrace.json.version;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sh.miles.metatrace.meta.rule.VersionRule;
import sh.miles.metatrace.meta.version.VersionArgument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VersionArgumentAdapter implements JsonSerializer<VersionArgument>, JsonDeserializer<VersionArgument> {

    @Override
    public JsonElement serialize(final VersionArgument argument, final Type type, final JsonSerializationContext context) {
        if (argument.rules().isEmpty() && argument.values().size() == 1) {
            return new JsonPrimitive(argument.values().getFirst());
        }

        final var parent = new JsonObject();
        final var rules = new JsonArray();
        for (final VersionRule rule : argument.rules()) {
            rules.add(context.serialize(rule, VersionRule.class));
        }
        parent.add("rules", rules);

        if (argument.values().size() == 1) {
            parent.addProperty("value", argument.values().getFirst());
        } else {
            final var values = new JsonArray();
            argument.values().forEach(values::add);
            parent.add("value", values);
        }
        return parent;
    }

    @Override
    public VersionArgument deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        if (element.isJsonPrimitive()) {
            return VersionArgument.of(element.getAsString());
        }

        final var parent = element.getAsJsonObject();

        final List<VersionRule> rules = new ArrayList<>();
        if (parent.has("rules")) {
            for (final JsonElement rule : parent.getAsJsonArray("rules")) {
                rules.add(context.deserialize(rule, VersionRule.class));
            }
        }

        final List<String> values = new ArrayList<>();
        final JsonElement value = parent.get("value");
        if (value.isJsonArray()) {
            for (final JsonElement entry : value.getAsJsonArray()) {
                values.add(entry.getAsString());
            }
        } else {
            values.add(value.getAsString());
        }

        return new VersionArgument(values, rules);
    }
}
