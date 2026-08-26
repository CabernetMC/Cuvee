package sh.miles.metatrace.meta.version;

import org.jetbrains.annotations.NotNull;
import sh.miles.metatrace.meta.rule.Environment;
import sh.miles.metatrace.meta.rule.VersionRule;

import java.util.List;

/**
 * Represents a library within a version
 *
 * @param name     the dependency notation of the library
 * @param artifact the artifact information
 * @param rules    the rules gating this library, empty when it applies everywhere
 * @since 1.0.0-SNAPSHOT
 */
public record VersionLibrary(@NotNull String name, @NotNull VersionLibraryArtifact artifact,
                             @NotNull List<VersionRule> rules) {

    public VersionLibrary {
        rules = List.copyOf(rules);
    }

    /**
     * Whether this library belongs on the classpath within the given environment.
     * Platform specific natives are selected through this.
     *
     * @param environment the environment
     * @return true if it belongs, otherwise false
     */
    public boolean isAllowedOn(@NotNull final Environment environment) {
        return VersionRule.allows(this.rules, environment);
    }
}
