package sh.miles.metatrace.meta.rule;

import org.jetbrains.annotations.NotNull;

/**
 * The verdict a {@link VersionRule} applies when it matches
 *
 * @since 2.0.0-SNAPSHOT
 */
public enum RuleAction {
    ALLOW("allow"),
    DISALLOW("disallow");

    private final String id;

    RuleAction(@NotNull final String id) {
        this.id = id;
    }

    /**
     * The identifier mojang uses for this action within the manifest
     *
     * @return the identifier
     */
    @NotNull
    public String id() {
        return this.id;
    }

    /**
     * Finds the action with the given manifest identifier
     *
     * @param id the identifier
     * @return the action
     * @throws IllegalArgumentException thrown if no action has the given identifier
     */
    @NotNull
    public static RuleAction fromId(@NotNull final String id) throws IllegalArgumentException {
        for (final RuleAction action : values()) {
            if (action.id.equalsIgnoreCase(id)) {
                return action;
            }
        }
        throw new IllegalArgumentException("unknown rule action %s".formatted(id));
    }
}
