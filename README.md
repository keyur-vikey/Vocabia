# Vocabia

A German → English vocabulary flashcard app: swipe-based spaced-repetition practice, per-category
progress tracking, and grammar detail cards (verb conjugations, noun gender/plural, etc.).

The app is now a single **.NET MAUI Blazor Hybrid** solution in [`src/`](src/), targeting:

- **Android**
- **iOS**
- **Mac Catalyst** (macOS)
- **Windows** (WinUI 3)
- **Web** (Blazor WebAssembly, static-hostable)

One shared UI (Razor components) and one shared learning-algorithm/data layer run on every
platform; only the storage layer differs (SQLite on native, `localStorage` on Web).

> The original native-Kotlin/Jetpack Compose Android app lives on under [`android/`](android/) for
> reference, and [`ios-shell/`](ios-shell/) was a placeholder for a native SwiftUI port that this
> MAUI conversion supersedes. Both can be deleted once the MAUI app is validated on-device.

## Solution layout

```
src/
  Vocabia.Core/     Models, learning-repetition algorithm (PracticeRepository), embedded word-seed JSON
  Vocabia.Shared/    Shared Razor components + CSS (Home, Practice/CardStack, Progress) — used by every head
  Vocabia.App/       .NET MAUI Blazor Hybrid head → Android, iOS, Mac Catalyst, Windows
  Vocabia.Web/       Blazor WebAssembly head → any static web host
  Vocabia.sln
```

`Vocabia.Core` has zero platform dependencies: the word list ships as an embedded JSON resource
(ported from `android/app/src/main/assets/vocab/de_en_pilot.json`), and persistence is behind
`IProgressStore` so each head supplies its own:

- `Vocabia.App` → `SqliteProgressStore` (sqlite-net-pcl, file in app data directory)
- `Vocabia.Web` → `LocalStorageProgressStore` (browser `localStorage` via JS interop)

## Prerequisites

- [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- MAUI workload: `dotnet workload install maui`
- Platform tooling as usual for MAUI: Xcode (iOS/Mac Catalyst, macOS only), Android SDK, Windows
  App SDK (Windows target, Windows only)

## Running

```bash
cd src

# Web (works anywhere, no extra workloads needed beyond the wasm-tools workload)
dotnet workload install wasm-tools
dotnet run --project Vocabia.Web

# Android
dotnet build -t:Run -f net8.0-android Vocabia.App

# Windows (from Windows)
dotnet build -t:Run -f net8.0-windows10.0.19041.0 Vocabia.App

# iOS / Mac Catalyst (from macOS, with Xcode installed)
dotnet build -t:Run -f net8.0-ios Vocabia.App
dotnet build -t:Run -f net8.0-maccatalyst Vocabia.App
```

Or open `src/Vocabia.sln` in Visual Studio / Visual Studio Code (with the MAUI/C# Dev Kit
extensions) and pick a target from the run-configuration dropdown.

## Publishing the Web build

```bash
cd src
dotnet publish Vocabia.Web -c Release -o ../publish/web
```

The output in `../publish/web/wwwroot` is static and can be hosted anywhere (GitHub Pages,
Azure Static Web Apps, Netlify, an S3 bucket, etc.).
