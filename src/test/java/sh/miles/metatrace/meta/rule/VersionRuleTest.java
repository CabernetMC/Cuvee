package sh.miles.metatrace.meta.rule;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionRuleTest {

    private static final Environment LINUX = new Environment("linux", "x86_64", "6.1.0", Map.of());
    private static final Environment OSX = new Environment("osx", "arm64", "14.0", Map.of());

    private static VersionRule allowOn(final String os) {
        return new VersionRule(RuleAction.ALLOW, new VersionRuleOs(os, null, null), Map.of());
    }

    @Test
    void should_Allow_When_No_Rules_Are_Present() {
        assertTrue(VersionRule.allows(List.of(), LINUX));
    }

    @Test
    void should_Deny_When_No_Rule_Matches() {
        assertFalse(VersionRule.allows(List.of(allowOn("osx")), LINUX));
    }

    @Test
    void should_Allow_When_Os_Matches() {
        assertTrue(VersionRule.allows(List.of(allowOn("osx")), OSX));
    }

    @Test
    void should_Let_Later_Rules_Overwrite_Earlier_Ones() {
        final List<VersionRule> rules = List.of(
                new VersionRule(RuleAction.ALLOW, null, Map.of()),
                new VersionRule(RuleAction.DISALLOW, new VersionRuleOs("linux", null, null), Map.of()));
        assertFalse(VersionRule.allows(rules, LINUX));
        assertTrue(VersionRule.allows(rules, OSX));
    }

    @Test
    void should_Match_Architecture() {
        final var rule = new VersionRule(RuleAction.ALLOW, new VersionRuleOs(null, "arm64", null), Map.of());
        assertTrue(rule.matches(OSX));
        assertFalse(rule.matches(LINUX));
    }

    @Test
    void should_Match_Os_Version_As_A_Regular_Expression() {
        final var rule = new VersionRule(RuleAction.ALLOW, new VersionRuleOs(null, null, "^6\\."), Map.of());
        assertTrue(rule.matches(LINUX));
        assertFalse(rule.matches(OSX));
    }

    @Test
    void should_Never_Advertise_Unset_Features() {
        final var rule = new VersionRule(RuleAction.ALLOW, null, Map.of("is_demo_user", true));
        assertFalse(rule.matches(LINUX));
        assertTrue(rule.matches(LINUX.withFeature("is_demo_user", true)));
    }
}
