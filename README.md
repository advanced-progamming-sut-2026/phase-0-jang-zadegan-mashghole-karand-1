## How to run the app?

Console (terminal) version:

```bash
./gradlew :core:run
```

Or:

```bash
./gradlew :core:runConsole
```

## Project layout

```text
core/          # game code (model, controller, view, Application)
assets/        # graphics assets for LibGDX / libPVZ (empty for now)
data/          # local SQLite DB (gitignored)
docs/          # project docs / UML
```

Configure external PVZ2 asset path later via `pvz.assets` in `gradle.properties`.

## Contributors

Sourena Kazemi - 404106247<br/>
Mani Aldaghi - 404105489<br/>
Mani Safari - 404106044
