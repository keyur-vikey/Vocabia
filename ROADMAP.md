# Vocabia — Roadmap

Vocabulary learning app (Android first, iOS shell later). Deck-of-cards practice UI with
tap-to-reveal and swipe-based spaced repetition. First language: German (for English speakers).

Legend: `[ ]` todo · `[~]` in progress · `[x]` done

---

## Decisions log

| Decision | Choice |
|---|---|
| Platform order | Android native first (full app). iOS: empty Xcode/SwiftUI shell project only, ported later. |
| Android stack | Kotlin + Jetpack Compose + Room (local DB) |
| Data strategy | Offline-first. Local Room DB is source of truth; optional cloud sync when signed in. |
| Backend | Firebase (Auth + Firestore) |
| Content sourcing | AI-generated (by me) now, flagged for human review later. |
| Monetization | Freemium: free tier w/ ads (AdMob), one-time unlock (Play Billing) removes ads. |
| Min Android SDK | API 29 (Android 10) |
| First deck size | ~1000 words for German MVP, expandable to 5000 later. |
| Card categories & colors (proposed, confirm) | Noun=blue, Verb=orange, Adjective=green, Preposition=purple, Other=gray |

All major decisions confirmed. Remaining **(confirm)** items are minor/cosmetic.

---

## Phase 0 — Foundations
- [x] Repo init (git) + folder structure (`/android`, `/ios-shell`, `/content-pipeline`, `/docs`)
- [x] Android project skeleton (Kotlin, Compose, min SDK 29 / Android 10)
- [x] Empty iOS Xcode project shell placeholder (real `.xcodeproj` needs Xcode on a Mac — see note in `/ios-shell`)
- [x] Firebase project created (Auth + Firestore), `google-services.json` placed in `android/app/` (gitignored) and SDK wired into Gradle
- [x] Define word-entry JSON schema (see Phase 1)

## Phase 1 — Content model & data pipeline
- [x] Finalize JSON schema per word type — see [`content-pipeline/schema/word-entry.schema.json`](content-pipeline/schema/word-entry.schema.json)
- [~] Pilot dataset: 60/200 words generated (`content-pipeline/de/pilot-batch-1.json`, `pilot-batch-2.json`, bundled into `android/app/src/main/assets/vocab/de_en_pilot.json`) — **paused, resume manually on request**
- [ ] **[MANUAL TRIGGER]** Continue word generation to ~200 pilot words, then scale to ~1000 — only when you explicitly prompt for it, in batches of ~30 words at a time
- [ ] Human review pass on generated batches (you correct/approve)
- [ ] Simple validation script (schema check, duplicate check, missing-field check)

## Phase 2 — Spaced-repetition algorithm design
- [x] Design box/state model, swipe transition rules, and session builder
- [x] Write up algorithm spec doc — see [`docs/algorithm.md`](docs/algorithm.md) — **awaiting your review of 3 open questions in it**

## Phase 3 — Local persistence (Android)
- [x] Room schema: `WordEntity`, `UserWordProgressEntity`, `SessionCounterEntity`
- [x] Seed DB from bundled JSON content on first launch (`SeedLoader`)
- [x] Repository layer (`PracticeRepository`): session builder + swipe handling per algorithm spec

## Phase 4 — Card UI & visuals
- [x] Deck-on-floor visual: stacked peeking cards behind the active one (`CardStack.kt`)
- [x] Category color system (noun/verb/adjective/preposition/other) — `CategoryColors.kt`
- [x] Tap-to-reveal state machine: word → meaning → article (if any) → sentence 1 → 2 → 3 → grammar info (`WordCardContent.kt`)
- [x] Swipe gesture handling with drag-tint visual feedback (green=right, red=left, gold=down)
- [x] Polish pass: category icons (chip + stats), swipe direction icons (check/close/star) fading in with drag progress, improved typography/spacing
- [x] Card component renders category-specific grammar (verb conjugation table, noun plural/gender, adjective comp/superlative, preposition case)

## Phase 5 — Practice flow
- [x] Deck/session screen (`DeckScreen.kt`) wiring repository → card stack, session-complete → auto new session
- [x] Progress/stats screen (`StatsScreen.kt`): overall progress bar + per-category breakdown (finished/learning/new), reachable via a chart icon on the deck screen
- [ ] Language + deck selection screen (not needed yet — single German pilot deck hardcoded)

**Status: MVP runs on real devices.** Built and verified crash-free on both a T10Plus tablet and a Pixel 4a via adb (build tooling: downloaded Gradle 8.7 + used Android Studio's bundled JDK 21, since neither was on PATH). Phases 0–5 are functionally complete for the pilot deck; remaining work is mostly Phase 1 (more words) and Phase 6+ (sync, monetization, release).

## Phase 6 — Cloud sync (optional sign-in)
- [ ] Firebase Auth (anonymous by default, optional Google sign-in to back up)
- [ ] Firestore sync of `UserWordProgressEntity` (local-first, push/pull merge on sign-in/reconnect)
- [ ] Conflict resolution rule (e.g. most-advanced-state wins per word) **(confirm this is acceptable)**

## Phase 7 — Monetization
- [ ] AdMob integration (banner between sessions or interstitial after session) — placement TBD, not mid-card-reveal
- [ ] Play Billing: single "Premium" SKU removing ads **(confirm: one-time purchase or subscription?)**
- [ ] Entitlement check gating ad display

## Phase 8 — Polish & release
- [ ] App icon, splash, store screenshots
- [ ] Play Console listing + closed testing track
- [ ] Crash reporting (Firebase Crashlytics)
- [ ] Release build + internal test → closed track → production

## Phase 9 — iOS (deferred)
- [ ] Port Phase 3–5 logic to SwiftUI once Android is stable and released
- [ ] Reuse content JSON + algorithm spec as-is (platform-agnostic)
- [ ] Wire Firebase iOS SDK for sync/auth parity

---

## Immediate next steps
1. Confirm the **(confirm)** items above (min SDK, backend choice, deck size, premium pricing model).
2. I'll draft `/docs/algorithm.md` with the exact spaced-repetition curve for your review.
3. I'll generate the Phase 1 pilot dataset (~200 German words) for you to spot-check quality.
