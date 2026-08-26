package sh.miles.metatrace.json.version;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sh.miles.metatrace.meta.version.VersionArgument;
import sh.miles.metatrace.meta.version.VersionAssetIndex;
import sh.miles.metatrace.meta.version.VersionData;
import sh.miles.metatrace.meta.version.VersionDownloadEntry;
import sh.miles.metatrace.meta.version.VersionJavaRuntime;
import sh.miles.metatrace.meta.version.VersionLibrary;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersionDataAdapter implements JsonSerializer<VersionData>, JsonDeserializer<VersionData> {

    @Override
    public JsonElement serialize(final VersionData data, final Type type, final JsonSerializationContext context) {
        final var parent = new JsonObject();

        parent.addProperty("mainClass", data.mainClass());

        if (data.javaRuntime() != null) {
            parent.add("javaVersion", context.serialize(data.javaRuntime(), VersionJavaRuntime.class));
        }
        if (data.assetIndex() != null) {
            parent.add("assetIndex", context.serialize(data.assetIndex(), VersionAssetIndex.class));
        }

        final var downloads = new JsonObject();
        final var downloadEntries = data.downloadEntries();
        for (final String downloadKey : downloadEntries.keySet()) {
            downloads.add(downloadKey, context.serialize(downloadEntries.get(downloadKey), VersionDownloadEntry.class));
        }

        parent.add("downloads", downloads);

        final var librariesList = data.libraries();
        final var libraries = new JsonArray();
        for (final VersionLibrary versionLibrary : librariesList) {
            libraries.add(context.serialize(versionLibrary, VersionLibrary.class));
        }

        parent.add("libraries", libraries);

        if (!data.gameArguments().isEmpty() || !data.jvmArguments().isEmpty()) {
            final var arguments = new JsonObject();
            arguments.add("game", serializeArguments(data.gameArguments(), context));
            arguments.add("jvm", serializeArguments(data.jvmArguments(), context));
            parent.add("arguments", arguments);
        }

        return parent;
    }

    @Override
    public VersionData deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final var parent = element.getAsJsonObject();

        final String mainClass = parent.get("mainClass").getAsString();

        final VersionJavaRuntime javaRuntime = parent.has("javaVersion")
                ? context.deserialize(parent.getAsJsonObject("javaVersion"), VersionJavaRuntime.class)
                : null;
        final VersionAssetIndex assetIndex = parent.has("assetIndex")
                ? context.deserialize(parent.getAsJsonObject("assetIndex"), VersionAssetIndex.class)
                : null;

        final Map<String, VersionDownloadEntry> downloadEntries = new HashMap<>();
        final var downloads = parent.getAsJsonObject("downloads");
        for (final String downloadKey : downloads.keySet()) {
            downloadEntries.put(downloadKey, context.deserialize(downloads.get(downloadKey), VersionDownloadEntry.class));
        }

        final List<VersionLibrary> libraries = new ArrayList<>();
        for (final JsonElement library : parent.getAsJsonArray("libraries")) {
            libraries.add(context.deserialize(library, VersionLibrary.class));
        }

        List<VersionArgument> gameArguments = List.of();
        List<VersionArgument> jvmArguments = List.of();
        if (parent.has("arguments")) {
            final var arguments = parent.getAsJsonObject("arguments");
            gameArguments = deserializeArguments(arguments.getAsJsonArray("game"), context);
            jvmArguments = deserializeArguments(arguments.getAsJsonArray("jvm"), context);
        }

        return new VersionData(mainClass, javaRuntime, assetIndex, downloadEntries, libraries, gameArguments, jvmArguments);
    }

    private static JsonArray serializeArguments(final List<VersionArgument> arguments, final JsonSerializationContext context) {
        final var array = new JsonArray();
        for (final VersionArgument argument : arguments) {
            array.add(context.serialize(argument, VersionArgument.class));
        }
        return array;
    }

    private static List<VersionArgument> deserializeArguments(final JsonArray array, final JsonDeserializationContext context) {
        final List<VersionArgument> arguments = new ArrayList<>();
        if (array == null) {
            return arguments;
        }
        for (final JsonElement argument : array) {
            arguments.add(context.deserialize(argument, VersionArgument.class));
        }
        return arguments;
    }
}
