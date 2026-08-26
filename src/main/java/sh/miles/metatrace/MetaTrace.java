package sh.miles.metatrace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sh.miles.metatrace.json.manifest.VersionManifestAdapter;
import sh.miles.metatrace.json.manifest.VersionManifestEntryAdapter;
import sh.miles.metatrace.json.manifest.VersionManifestLatestAdapter;
import sh.miles.metatrace.json.rule.VersionRuleAdapter;
import sh.miles.metatrace.json.rule.VersionRuleOsAdapter;
import sh.miles.metatrace.json.version.VersionArgumentAdapter;
import sh.miles.metatrace.json.version.VersionAssetIndexAdapter;
import sh.miles.metatrace.json.version.VersionDataAdapter;
import sh.miles.metatrace.json.version.VersionDownloadEntryAdapter;
import sh.miles.metatrace.json.version.VersionJavaRuntimeAdapter;
import sh.miles.metatrace.json.version.VersionLibraryAdapter;
import sh.miles.metatrace.json.version.VersionLibraryArtifactAdapter;
import sh.miles.metatrace.meta.MinecraftVersion;
import sh.miles.metatrace.meta.manifest.VersionManifest;
import sh.miles.metatrace.meta.manifest.VersionManifestEntry;
import sh.miles.metatrace.meta.manifest.VersionManifestLatest;
import sh.miles.metatrace.meta.rule.VersionRule;
import sh.miles.metatrace.meta.rule.VersionRuleOs;
import sh.miles.metatrace.meta.version.VersionArgument;
import sh.miles.metatrace.meta.version.VersionAssetIndex;
import sh.miles.metatrace.meta.version.VersionData;
import sh.miles.metatrace.meta.version.VersionDownloadEntry;
import sh.miles.metatrace.meta.version.VersionJavaRuntime;
import sh.miles.metatrace.meta.version.VersionLibrary;
import sh.miles.metatrace.meta.version.VersionLibraryArtifact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class MetaTrace {
    public static final URI PISTON_META_LINK = URI.create("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(VersionManifestEntry.class, new VersionManifestEntryAdapter())
            .registerTypeAdapter(VersionManifestLatest.class, new VersionManifestLatestAdapter())
            .registerTypeAdapter(VersionManifest.class, new VersionManifestAdapter())
            .registerTypeAdapter(VersionLibraryArtifact.class, new VersionLibraryArtifactAdapter())
            .registerTypeAdapter(VersionLibrary.class, new VersionLibraryAdapter())
            .registerTypeAdapter(VersionDownloadEntry.class, new VersionDownloadEntryAdapter())
            .registerTypeAdapter(VersionRuleOs.class, new VersionRuleOsAdapter())
            .registerTypeAdapter(VersionRule.class, new VersionRuleAdapter())
            .registerTypeAdapter(VersionArgument.class, new VersionArgumentAdapter())
            .registerTypeAdapter(VersionAssetIndex.class, new VersionAssetIndexAdapter())
            .registerTypeAdapter(VersionJavaRuntime.class, new VersionJavaRuntimeAdapter())
            .registerTypeAdapter(VersionData.class, new VersionDataAdapter())
            .create();
    private static final Gson PRETTY_GSON = GSON.newBuilder()
            .setPrettyPrinting()
            .create();

    private MetaTrace() {
        throw new UnsupportedOperationException("Can not initialize utility class");
    }

    /**
     * Attempts to read the specified file to a MinecraftVersion
     *
     * @param file the file to read from
     * @return the minecraft version or null if the file does not exist
     * @throws IllegalStateException thrown if an IO exception occurs
     */
    @Nullable
    public static MinecraftVersion readVersionFromFile(@NotNull final Path file) throws IllegalStateException {
        if (Files.notExists(file)) return null;
        try (final var reader = Files.newBufferedReader(file)) {
            return GSON.fromJson(reader, MinecraftVersion.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Attempts to save the specified version to a file.
     *
     * @param version the version.
     * @param file    the path to save to.
     * @throws IllegalArgumentException thrown if the version is invalid.
     * @throws IllegalStateException    throw if any exception regarding the IO occurs.
     */
    public static void saveVersionToFile(@NotNull final String version, @NotNull final Path file) throws IllegalArgumentException, IllegalStateException {
        saveVersionToFile(getVersion(version), file);
    }

    /**
     * Attempts to save the specified version to a file.
     *
     * @param version the version.
     * @param file    the path to save to.
     * @throws IllegalStateException thrown if any exception regarding the IO occurs.
     */
    public static void saveVersionToFile(@NotNull final MinecraftVersion version, @NotNull final Path file) throws IllegalStateException {
        final String prettyJsonString = PRETTY_GSON.toJson(version, MinecraftVersion.class);
        try {
            createFileIfNotExists(file);
            try (final var writer = Files.newBufferedWriter(file, StandardOpenOption.WRITE)) {
                writer.write(prettyJsonString);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets the minecraft version
     *
     * @param version the version
     * @return the minecraft version
     * @throws IllegalArgumentException thrown if the version does not exist
     */
    @NotNull
    public static MinecraftVersion getVersion(@NotNull final String version) throws IllegalArgumentException {
        try (final HttpClient client = HttpClient.newHttpClient()) {
            return getVersion(version, getVersionManifest(client), client);
        }
    }

    /**
     * Gets the minecraft version
     *
     * @param version  the version
     * @param manifest the version manifest
     * @return the minecraft version
     * @throws IllegalArgumentException thrown if the version does not exist
     */
    @NotNull
    public static MinecraftVersion getVersion(@NotNull final String version, @NotNull final VersionManifest manifest) throws IllegalArgumentException {
        try (final HttpClient client = HttpClient.newHttpClient()) {
            return getVersion(version, manifest, client);
        }
    }


    /**
     * Gets the minecraft version
     *
     * @param version the version
     * @param client  the HttpClient to use to make the request
     * @return the minecraft version
     * @throws IllegalArgumentException thrown if the version does not exist
     */
    public static MinecraftVersion getVersion(@NotNull final String version, @NotNull final HttpClient client) throws IllegalArgumentException {
        return getVersion(version, getVersionManifest(client), client);
    }

    /**
     * Gets the minecraft version
     *
     * @param version  the version
     * @param manifest the version manifest
     * @param client   the HttpClient to use to make the request
     * @return the minecraft version
     * @throws IllegalArgumentException thrown if the version does not exist
     * @throws IllegalStateException    thrown if any errors occur during the request
     */
    @NotNull
    public static MinecraftVersion getVersion(@NotNull final String version, @NotNull final VersionManifest manifest, @NotNull final HttpClient client) throws IllegalArgumentException {
        final String targetVersion;
        if (version.equals("latest")) {
            targetVersion = manifest.latest().release();
        } else if (version.equals("snapshot")) {
            targetVersion = manifest.latest().snapshot();
        } else {
            targetVersion = version;
        }

        VersionManifestEntry versionEntry = null;
        for (final VersionManifestEntry entry : manifest.entries()) {
            if (entry.id().equalsIgnoreCase(targetVersion)) {
                if (!entry.isValid()) {
                    throw new IllegalArgumentException("While the entry for version %s exists the sha values do not match!".formatted(targetVersion));
                }
                versionEntry = entry;
                break;
            }
        }

        if (versionEntry == null) {
            throw new IllegalArgumentException("no valid manifest entry for version %s was found".formatted(targetVersion));
        }

        final String body;
        final HttpRequest request = HttpRequest.newBuilder().uri(versionEntry.url()).GET().build();
        try {
            body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException(e);
        }

        final VersionData data = MetaTrace.GSON.fromJson(body, VersionData.class);
        return new MinecraftVersion(versionEntry, data);
    }

    /**
     * Gets the VersionManifest, which is a manifest of all minecraft versions.
     *
     * @return the version manifest.
     * @throws IllegalStateException thrown if any errors occur during the request.
     */
    @NotNull
    public static VersionManifest getVersionManifest() throws IllegalStateException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return getVersionManifest(client);
        }
    }

    /**
     * Gets the VersionManifest, which is a manifest of all minecraft versions.
     *
     * @param client the HTTPClient used to make the requests.
     * @return the version manifest.
     * @throws IllegalStateException thrown if any errors occur during the request.
     */
    @NotNull
    public static VersionManifest getVersionManifest(@NotNull final HttpClient client) throws IllegalStateException {
        HttpRequest request = HttpRequest.newBuilder().uri(PISTON_META_LINK).GET().build();
        String body;
        try {
            body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return MetaTrace.GSON.fromJson(body, VersionManifest.class);
    }

    private static void createFileIfNotExists(@NotNull final Path path) {
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
