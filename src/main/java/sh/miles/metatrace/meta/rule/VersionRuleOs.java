package sh.miles.metatrace.meta.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * The operating system constraint of a {@link VersionRule}. Every component is
 * optional and an absent component matches anything.
 *
 * @param name    mojang's operating system name, e.g. windows, osx or linux
 * @param arch    mojang's architecture name, e.g. x86
 * @param version a regular expression matched against the operating system version
 * @since 2.0.0-SNAPSHOT
 */
public record VersionRuleOs(@Nullable String name, @Nullable String arch, @Nullable String version) {

    /**
     * Whether this constraint is satisfied by the given environment
     *
     * @param environment the environment
     * @return true if satisfied, otherwise false
     */
    public boolean matches(@NotNull final Environment environment) {
        if (this.name != null && !this.name.equalsIgnoreCase(environment.osName())) {
            return false;
        }
        if (this.arch != null && !this.arch.equalsIgnoreCase(environment.osArch())) {
            return false;
        }
        return this.version == null
                || Pattern.compile(this.version).matcher(environment.osVersion()).find();
    }
}
