package sh.miles.metatrace;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sh.miles.metatrace.meta.MinecraftVersion;
import sh.miles.metatrace.meta.rule.Environment;
import sh.miles.metatrace.meta.version.VersionData;
import sh.miles.metatrace.meta.version.VersionLibrary;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetaTraceVersionDataTest {

    private static final String VERSION = "26.2";

    private static final Environment LINUX = new Environment("linux", "x86_64", "6.1.0", Map.of());
    private static final Environment WINDOWS = new Environment("windows", "x86_64", "10.0", Map.of());
    private static final Environment OSX = new Environment("osx", "arm64", "14.0", Map.of());

    private static VersionData data;

    @BeforeAll
    static void fetch() {
        final MinecraftVersion version = assertDoesNotThrow(() -> MetaTrace.getVersion(VERSION));
        data = version.data();
    }

    @Test
    void should_Read_Launch_Metadata() {
        assertEquals("net.minecraft.client.main.Main", data.mainClass());
        assertNotNull(data.javaRuntime());
        assertEquals(25, data.javaRuntime().majorVersion());
    }

    @Test
    void should_Read_Asset_Index() {
        assertNotNull(data.assetIndex());
        assertFalse(data.assetIndex().id().isBlank());
        assertTrue(data.assetIndex().totalSize() > 0);
    }

    @Test
    void should_Filter_Libraries_By_Environment() {
        final List<VersionLibrary> all = data.libraries();
        final List<VersionLibrary> linux = data.librariesFor(LINUX);

        assertFalse(linux.isEmpty());
        assertTrue(linux.size() < all.size(), "rule gated libraries should be excluded");
    }

    @Test
    void should_Select_Different_Libraries_Per_Platform() {
        // The whole point of rule evaluation: natives must not be shared across platforms.
        assertNotEquals(data.librariesFor(LINUX), data.librariesFor(WINDOWS));
        assertNotEquals(data.librariesFor(LINUX), data.librariesFor(OSX));
    }

    @Test
    void should_Apply_Rules_To_Jvm_Arguments() {
        assertTrue(data.jvmArgumentsFor(OSX).contains("-XstartOnFirstThread"));
        assertFalse(data.jvmArgumentsFor(LINUX).contains("-XstartOnFirstThread"));
    }

    @Test
    void should_Round_Trip_Through_Json() {
        // The new rule, argument and asset index adapters must serialize symmetrically
        // or saved versions silently lose their platform gating.
        final var gson = MetaTrace.GSON;
        final MinecraftVersion original = assertDoesNotThrow(() -> MetaTrace.getVersion(VERSION));
        final String json = assertDoesNotThrow(() -> gson.toJson(original, MinecraftVersion.class));
        final MinecraftVersion restored = assertDoesNotThrow(() -> gson.fromJson(json, MinecraftVersion.class));
        assertEquals(original, restored);
        assertEquals(original.data().librariesFor(LINUX), restored.data().librariesFor(LINUX));
    }

    @Test
    void should_Expose_Game_Argument_Placeholders() {
        final List<String> game = data.gameArgumentsFor(LINUX);
        assertTrue(game.contains("--assetIndex"));
        assertTrue(game.contains("${assets_index_name}"));
    }
}
