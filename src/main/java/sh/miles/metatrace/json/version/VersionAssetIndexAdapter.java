package sh.miles.metatrace.json.version;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sh.miles.metatrace.meta.version.VersionAssetIndex;

import java.lang.reflect.Type;
import java.net.URI;

public class VersionAssetIndexAdapter implements JsonSerializer<VersionAssetIndex>, JsonDeserializer<VersionAssetIndex> {

    @Override
    public JsonElement serialize(final VersionAssetIndex index, final Type type, final JsonSerializationContext context) {
        final var parent = new JsonObject();
        parent.addProperty("id", index.id());
        parent.addProperty("sha1", index.sha1());
        parent.addProperty("size", index.size());
        parent.addProperty("totalSize", index.totalSize());
        parent.addProperty("url", index.url().toASCIIString());
        return parent;
    }

    @Override
    public VersionAssetIndex deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();
        final var id = parent.get("id").getAsString();
        final var sha1 = parent.get("sha1").getAsString();
        final var size = parent.get("size").getAsInt();
        final var totalSize = parent.get("totalSize").getAsLong();
        final var url = URI.create(parent.get("url").getAsString());
        return new VersionAssetIndex(id, sha1, size, totalSize, url);
    }
}
