# MetaTrace

MetaTrace is a Minecraft metadata reader. It takes data from PistonMeta and
converts it into easily digestible and understandable data objects.
That can be used easily with other projects. It also provides some QOL
functions regarding reading and writing this data to files.

## Setup

By default MetaTrace does **not** provide GSON, which it uses internally to process Json.
MetaTrace uses the gson version `2.11.0`

### Maven
```xml
<repository>
  <id>miles-repos-snapshots</id>
  <name>Miles Repositories</name>
  <url>https://maven.miles.sh/snapshots</url>
</repository>

<dependency>
  <groupId>sh.miles</groupId>
  <artifactId>metatrace</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle
```kotlin
maven {
    name = "milesReposSnapshots"
    url = uri("https://maven.miles.sh/snapshots")
}

implementation("sh.miles:metatrace:2.0.0-SNAPSHOT")
```

## Usage

Getting a version's metadata.

```java
final MinecraftVersion version = MetaTrace.getVersion("1.21");
```

Writing a version to a file.

```java
final MinecraftVersion version = MetaTrace.getVersion("1.21");
MetaTrace.saveVersionToFile(version);
```

Reading a version from file.

```java
MetaTrace.readVersionFromFile(Path.of("1.20.6-meta.json"));
```
