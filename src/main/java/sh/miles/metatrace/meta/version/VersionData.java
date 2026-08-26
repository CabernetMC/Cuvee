package sh.miles.metatrace.meta.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.miles.metatrace.meta.rule.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents all data needed for a version
 *
 * @param mainClass       the entrypoint the version launches through
 * @param javaRuntime     the java runtime the version expects, absent on versions predating java runtime selection
 * @param assetIndex      the asset index of the version, absent on versions predating asset indexes
 * @param downloadEntries the download entries
 * @param libraries       all libraries for the version, including those gated to other platforms
 * @param gameArguments   the game arguments, empty on versions predating the arguments block
 * @param jvmArguments    the jvm arguments, empty on versions predating the arguments block
 */
public record VersionData(@NotNull String mainClass,
                          @Nullable VersionJavaRuntime javaRuntime,
                          @Nullable VersionAssetIndex assetIndex,
                          @NotNull Map<String, VersionDownloadEntry> downloadEntries,
                          @NotNull List<VersionLibrary> libraries,
                          @NotNull List<VersionArgument> gameArguments,
                          @NotNull List<VersionArgument> jvmArguments) {

    public static final String SERVER = "server";
    public static final String CLIENT = "client";
    /**
     * Mojang stopped publishing mappings once the shipped jars became deobfuscated,
     * so this entry is absent on newer versions.
     */
    public static final String SERVER_MAPPINGS = "server_mappings";
    /**
     * Mojang stopped publishing mappings once the shipped jars became deobfuscated,
     * so this entry is absent on newer versions.
     */
    public static final String CLIENT_MAPPINGS = "client_mappings";

    /**
     * The libraries which belong on the classpath within the given environment
     *
     * @param environment the environment
     * @return the applicable libraries, in manifest order
     */
    @NotNull
    public List<VersionLibrary> librariesFor(@NotNull final Environment environment) {
        final List<VersionLibrary> applicable = new ArrayList<>();
        for (final VersionLibrary library : this.libraries) {
            if (library.isAllowedOn(environment)) {
                applicable.add(library);
            }
        }
        return applicable;
    }

    /**
     * The game arguments which apply within the given environment, flattened in manifest order.
     * The returned values still contain mojang's placeholders, e.g. ${assets_root}.
     *
     * @param environment the environment
     * @return the applicable game arguments
     */
    @NotNull
    public List<String> gameArgumentsFor(@NotNull final Environment environment) {
        return flatten(this.gameArguments, environment);
    }

    /**
     * The jvm arguments which apply within the given environment, flattened in manifest order.
     * The returned values still contain mojang's placeholders, e.g. ${classpath}.
     *
     * @param environment the environment
     * @return the applicable jvm arguments
     */
    @NotNull
    public List<String> jvmArgumentsFor(@NotNull final Environment environment) {
        return flatten(this.jvmArguments, environment);
    }

    @NotNull
    private static List<String> flatten(@NotNull final List<VersionArgument> arguments,
                                        @NotNull final Environment environment) {
        final List<String> flattened = new ArrayList<>();
        for (final VersionArgument argument : arguments) {
            if (argument.isAllowedOn(environment)) {
                flattened.addAll(argument.values());
            }
        }
        return flattened;
    }
}
