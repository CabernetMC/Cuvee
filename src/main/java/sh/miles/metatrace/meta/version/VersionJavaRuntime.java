package sh.miles.metatrace.meta.version;

import org.jetbrains.annotations.NotNull;

/**
 * The java runtime a version expects to run on
 *
 * @param component    the mojang runtime component name, e.g. java-runtime-epsilon
 * @param majorVersion the major java version required, e.g. 25
 * @since 2.0.0-SNAPSHOT
 */
public record VersionJavaRuntime(@NotNull String component, int majorVersion) {
}
