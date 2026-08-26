package sh.miles.metatrace.meta.version;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * The asset index of a version, which lists every asset object the client downloads
 *
 * @param id        the index id, also used as the assetIndex launch argument
 * @param sha1      the sha1 of the index file
 * @param size      the size of the index file
 * @param totalSize the combined size of every asset object the index references
 * @param url       the url the index file is at
 * @since 2.0.0-SNAPSHOT
 */
public record VersionAssetIndex(@NotNull String id, @NotNull String sha1, int size, long totalSize,
                                @NotNull URI url) {
}
