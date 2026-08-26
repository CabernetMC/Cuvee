package sh.miles.metatrace.json.version;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sh.miles.metatrace.meta.version.VersionJavaRuntime;

import java.lang.reflect.Type;

public class VersionJavaRuntimeAdapter implements JsonSerializer<VersionJavaRuntime>, JsonDeserializer<VersionJavaRuntime> {

    @Override
    public JsonElement serialize(final VersionJavaRuntime runtime, final Type type, final JsonSerializationContext context) {
        final var parent = new JsonObject();
        parent.addProperty("component", runtime.component());
        parent.addProperty("majorVersion", runtime.majorVersion());
        return parent;
    }

    @Override
    public VersionJavaRuntime deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final var component = parent.get("component").getAsString();
        final var majorVersion = parent.get("majorVersion").getAsInt();
        return new VersionJavaRuntime(component, majorVersion);
    }
}
