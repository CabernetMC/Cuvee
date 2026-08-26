package sh.miles.metatrace.meta.version;

import org.jetbrains.annotations.NotNull;
import sh.miles.metatrace.meta.rule.Environment;
import sh.miles.metatrace.meta.rule.VersionRule;

import java.util.List;

/**
 * A launch argument. Unconditional arguments carry a single value and no rules,
 * while conditional arguments contribute their values only when their rules allow.
 *
 * @param values the values this argument contributes
 * @param rules  the rules gating this argument, empty when unconditional
 * @since 2.0.0-SNAPSHOT
 */
public record VersionArgument(@NotNull List<String> values, @NotNull List<VersionRule> rules) {

    public VersionArgument {
        values = List.copyOf(values);
        rules = List.copyOf(rules);
    }

    /**
     * Creates an unconditional argument
     *
     * @param value the value
     * @return the argument
     */
    @NotNull
    public static VersionArgument of(@NotNull final String value) {
        return new VersionArgument(List.of(value), List.of());
    }

    /**
     * Whether this argument applies within the given environment
     *
     * @param environment the environment
     * @return true if it applies, otherwise false
     */
    public boolean isAllowedOn(@NotNull final Environment environment) {
        return VersionRule.allows(this.rules, environment);
    }
}
