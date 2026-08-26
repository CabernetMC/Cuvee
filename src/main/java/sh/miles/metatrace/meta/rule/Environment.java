package sh.miles.metatrace.meta.rule;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The platform a version is being resolved for. Rules are evaluated against an
 * Environment rather than against system properties directly, so a classpath may
 * be resolved for a target other than the running host.
 *
 * @param osName    mojang's operating system name, one of {@link #WINDOWS}, {@link #OSX} or {@link #LINUX}
 * @param osArch    mojang's architecture name, e.g. x86, x86_64 or arm64
 * @param osVersion the operating system version, matched against rules as a regular expression
 * @param features  the launcher features advertised, e.g. is_demo_user
 * @since 2.0.0-SNAPSHOT
 */
public record Environment(@NotNull String osName, @NotNull String osArch, @NotNull String osVersion,
                          @NotNull Map<String, Boolean> features) {

    public static final String WINDOWS = "windows";
    public static final String OSX = "osx";
    public static final String LINUX = "linux";

    public Environment {
        features = Map.copyOf(features);
    }

    /**
     * The environment of the currently running host, advertising no features
     *
     * @return the current environment
     */
    @NotNull
    public static Environment current() {
        return new Environment(currentOsName(), currentOsArch(), System.getProperty("os.version", ""), Map.of());
    }

    /**
     * Creates a copy of this environment with the given feature flag set
     *
     * @param feature the feature name
     * @param enabled whether the feature is advertised
     * @return the new environment
     */
    @NotNull
    public Environment withFeature(@NotNull final String feature, final boolean enabled) {
        final Map<String, Boolean> copy = new HashMap<>(this.features);
        copy.put(feature, enabled);
        return new Environment(this.osName, this.osArch, this.osVersion, copy);
    }

    /**
     * Whether the given feature is advertised. Features which were never set are never advertised.
     *
     * @param feature the feature name
     * @return true if advertised, otherwise false
     */
    public boolean hasFeature(@NotNull final String feature) {
        return this.features.getOrDefault(feature, false);
    }

    @NotNull
    private static String currentOsName() {
        final String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return WINDOWS;
        }
        if (os.contains("mac") || os.contains("darwin")) {
            return OSX;
        }
        return LINUX;
    }

    @NotNull
    private static String currentOsArch() {
        final String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        return switch (arch) {
            case "amd64", "x86_64" -> "x86_64";
            case "aarch64", "arm64" -> "arm64";
            case "i386", "i486", "i586", "i686", "x86" -> "x86";
            default -> arch;
        };
    }
}
