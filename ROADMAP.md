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
| Backend (proposed, confirm) | Firebase (Auth + Firestore) — cheap to start, minimal server code, good free tier. |
| Content sourcing | AI-generated (by me) now, flagged for human review later. |
| Monetization | Freemium: free tier w/ ads (AdMob), one-time or subscription premium removes ads (Play Billing). |
| First deck size (proposed, confirm) | ~1000 words for German MVP (top-1000 frequency list), expandable to 5000 later. |
| Card categories & colors (proposed, confirm) | Noun=blue, Verb=orange, Adjective=green, Preposition=purple, Other=gray |

Open items I still need your call on are marked **(confirm)** inline below.

---

## Phase 0 — Foundations
- [ ] Repo init (git) + folder structure (`/android`, `/ios-shell`, `/content-pipeline`, `/docs`)
- [ ] Android project skeleton (Kotlin, Compose, min/target SDK decision) **(confirm min SDK, e.g. 26+)**
- [ ] Empty iOS Xcode project shell (SwiftUI, bundle id reserved, no real screens yet)
- [ ] Firebase project created (Auth + Firestore), google-services.json wired but unused until Phase 6
- [ ] Define word-entry JSON schema (see Phase 1)

## Phase 1 — Content model & data pipeline
- [ ] Finalize JSON schema per word type:
  - Common: `id, word, article (if noun), translation, category, cefr/frequency_rank, sentences[3]`
  - Verb-specific: `perfect_form (Partizip II + haben/sein), present-tense conjugation table (ich/du/er.../wir/ihr/sie), separable? prefix`
  - Preposition-specific: `case (Akkusativ/Dativ/Genitiv/two-way), example per case if two-way`
  - Adjective-specific: `comparative, superlative`
  - Noun-specific: `plural form, gender`
- [ ] Generate first batch: German top ~200 words (mixed categories) as a pilot dataset
- [ ] Human review pass on pilot batch (you correct/approve)
- [ ] Scale generation to ~1000 words once schema/quality confirmed
- [ ] Simple validation script (schema check, duplicate check, missing-field check)

## Phase 2 — Spaced-repetition algorithm design
- [ ] Design box/state model per word per user:
  - `box_level` (0..N), `consecutive_correct`, `last_seen_session`, `next_eligible_session`, `status (learning/finished)`
- [ ] Define swipe → transition rules:
  - **Swipe left (forgot)**: reset streak to 0, `next_eligible_session = current + 1` (small delay, grows slightly with box level)
  - **Swipe right (remembered)**: `consecutive_correct += 1`, box level up, delay before reappearing grows (e.g. 2, 5, 12 practices — tune curve); on 3rd consecutive → `status = finished`, removed from rotation permanently
  - **Swipe down (already know)**: `status = finished` immediately
- [ ] Define session builder: pick 10–15 cards per practice = mix of due "learning" words + a few new words, respecting `next_eligible_session`
- [ ] Write this up as a short algorithm spec doc (`/docs/algorithm.md`) before coding it — review together

## Phase 3 — Local persistence (Android)
- [ ] Room schema: `WordEntity`, `UserWordProgressEntity`, `DeckMetadataEntity`
- [ ] Seed DB from bundled JSON content on first launch
- [ ] Repository layer exposing: next-session query, mark-swipe-result, stats query

## Phase 4 — Card UI & visuals
- [ ] Deck-on-floor visual: stacked cards with peeking corners, subtle shadow/offset per depth
- [ ] Category color system + iconography (noun/verb/adjective/preposition/other)
- [ ] Tap-to-reveal state machine on a card: word → tap → meaning → tap → article (if any) → tap → sentence 1 → tap → sentence 2 → tap → sentence 3 → tap → rest of grammar info (conjugation table / case, etc.)
- [ ] Swipe gesture handling with visual feedback (left = subtle red tint + icon, right = green tint + icon, down = gold/star "mastered" animation)
- [ ] Card component per category (verb card shows conjugation table area, preposition card shows case badge, etc.)

## Phase 5 — Practice flow
- [ ] Deck/session screen wiring algorithm output → card stack
- [ ] Progress/stats screen (learning vs finished counts, streaks)
- [ ] Language + deck selection screen (German→English MVP; structure ready for more languages later)

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
