package sh.miles.metatrace.json.rule;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sh.miles.metatrace.meta.rule.VersionRuleOs;

import java.lang.reflect.Type;

public class VersionRuleOsAdapter implements JsonSerializer<VersionRuleOs>, JsonDeserializer<VersionRuleOs> {

    @Override
    public JsonElement serialize(final VersionRuleOs os, final Type type, final JsonSerializationContext context) {
        final var parent = new JsonObject();
        if (os.name() != null) {
            parent.addProperty("name", os.name());
        }
        if (os.arch() != null) {
            parent.addProperty("arch", os.arch());
        }
        if (os.version() != null) {
            parent.addProperty("version", os.version());
        }
        return parent;
    }

    @Override
    public VersionRuleOs deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final var name = parent.has("name") ? parent.get("name").getAsString() : null;
        final var arch = parent.has("arch") ? parent.get("arch").getAsString() : null;
        final var version = parent.has("version") ? parent.get("version").getAsString() : null;
        return new VersionRuleOs(name, arch, version);
    }
}
