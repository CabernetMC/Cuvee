package sh.miles.metatrace.meta.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A conditional gate applied to a library or a launch argument. Rules are how
 * platform specific artifacts are selected; most of the libraries in a modern
 * version manifest are gated this way.
 *
 * @param action   the verdict applied when this rule matches
 * @param os       the operating system constraint, or null when unconstrained
 * @param features the launcher features which must match for this rule to apply
 * @since 2.0.0-SNAPSHOT
 */
public record VersionRule(@NotNull RuleAction action, @Nullable VersionRuleOs os,
                          @NotNull Map<String, Boolean> features) {

    public VersionRule {
        features = Map.copyOf(features);
    }

    /**
     * Evaluates a rule list using mojang's semantics. An empty list allows,
     * otherwise the verdict starts denied and each matching rule overwrites it in order.
     *
     * @param rules       the rules to evaluate
     * @param environment the environment to evaluate against
     * @return true if allowed, otherwise false
     */
    public static boolean allows(@NotNull final List<VersionRule> rules, @NotNull final Environment environment) {
        if (rules.isEmpty()) {
            return true;
        }
        boolean allowed = false;
        for (final VersionRule rule : rules) {
            if (rule.matches(environment)) {
                allowed = rule.action() == RuleAction.ALLOW;
            }
        }
        return allowed;
    }

    /**
     * Whether every constraint of this rule is satisfied by the given environment
     *
     * @param environment the environment
     * @return true if satisfied, otherwise false
     */
    public boolean matches(@NotNull final Environment environment) {
        for (final Map.Entry<String, Boolean> feature : this.features.entrySet()) {
            if (environment.hasFeature(feature.getKey()) != feature.getValue()) {
                return false;
            }
        }
        return this.os == null || this.os.matches(environment);
    }
}
