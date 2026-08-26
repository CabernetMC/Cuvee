package sh.miles.metatrace.json.version;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sh.miles.metatrace.meta.rule.VersionRule;
import sh.miles.metatrace.meta.version.VersionLibrary;
import sh.miles.metatrace.meta.version.VersionLibraryArtifact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VersionLibraryAdapter implements JsonSerializer<VersionLibrary>, JsonDeserializer<VersionLibrary> {

    @Override
    public JsonElement serialize(final VersionLibrary library, final Type type, final JsonSerializationContext context) {
        final var object = new JsonObject();
        final var downloads = new JsonObject();
        downloads.add("artifact", context.serialize(library.artifact(), VersionLibraryArtifact.class));
        object.add("downloads", downloads);
        object.addProperty("name", library.name());

        if (!library.rules().isEmpty()) {
            final var rules = new JsonArray();
            for (final VersionRule rule : library.rules()) {
                rules.add(context.serialize(rule, VersionRule.class));
            }
            object.add("rules", rules);
        }
        return object;
    }

    @Override
    public VersionLibrary deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var object = element.getAsJsonObject();
        final VersionLibraryArtifact artifact = context.deserialize(object.getAsJsonObject("downloads").getAsJsonObject("artifact"), VersionLibraryArtifact.class);
        final String name = object.get("name").getAsString();

        final List<VersionRule> rules = new ArrayList<>();
        if (object.has("rules")) {
            for (final JsonElement rule : object.getAsJsonArray("rules")) {
                rules.add(context.deserialize(rule, VersionRule.class));
            }
        }

        return new VersionLibrary(name, artifact, rules);
    }
}
