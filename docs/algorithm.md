# Practice algorithm spec (draft — please review)

Leitner-box-style spaced repetition, driven entirely by swipe gestures. "Session" below means
one practice round (one showing of a card), not one calendar day — this app is session-based,
not date-based, since study frequency varies a lot per user.

## Per-word state (stored per user, per word)

```
box_level:          int, 0..5   (0 = new/just missed, 5 = well-known)
consecutive_correct: int, 0..3  (resets to 0 on a left swipe)
next_eligible_at:    int        (session counter value; word won't be shown before this)
status:              "learning" | "finished"
times_seen:          int
```

## Swipe → transition rules

| Swipe | Meaning | Effect |
|---|---|---|
| **Left** | "I didn't remember this" | `consecutive_correct = 0`; `box_level = max(0, box_level - 1)`; `next_eligible_at = current_session + 1` (so it can reappear as early as the *next* session, never the same one) |
| **Right** | "I remembered this" | `consecutive_correct += 1`; `box_level = min(5, box_level + 1)`; `next_eligible_at = current_session + delay(box_level)`; if `consecutive_correct == 3` → `status = "finished"` (removed from rotation permanently) |
| **Down** | "I already know this perfectly" | `status = "finished"` immediately, regardless of history |

`delay(box_level)` — sessions to wait before the word is eligible again, tuned so it grows the
more consistently a word is remembered:

| box_level | delay (sessions) |
|---|---|
| 1 | 2 |
| 2 | 3 |
| 3 | 5 |
| 4 | 8 |
| 5 | 12 |

(Fibonacci-like growth — cheap to compute, and matches the standard spaced-repetition intuition
that intervals should expand geometrically, not linearly.)

## Session builder (10–15 cards per practice)

Given the full word pool for the active deck:

1. **Due pool** = all `status == "learning"` words where `next_eligible_at <= current_session`.
2. **New pool** = all words never seen (`times_seen == 0`), ordered by `frequency_rank` (most
   common first).
3. Fill the session (target size, default 12):
   - Take up to 70% of the slots from the **due pool**, oldest-due-first (words waiting longest
     get priority — prevents starvation).
   - Fill remaining slots from the **new pool**.
   - If due pool is empty, fill entirely from new pool (early on, this is the common case).
   - If both pools are short, session is just however many are available (no padding).
4. Shuffle final selection order (don't show them sorted by rank/box — feels mechanical).

`current_session` is a simple integer counter, incremented once per completed practice round,
stored alongside the deck's progress. This is what makes delays "practices" instead of "days,"
per your original spec.

## Open questions for you

1. **Session target size** — default 12 (mid-point of your 10–15 range). Fixed number, or should
   it vary (e.g. smaller near the end of a deck when few words remain)?
2. **New-word pacing** — should there be a cap on *how many brand-new words* appear per session
   (e.g. max 5), so a user isn't overwhelmed even if they blaze through old material? Proposed: yes, cap at 5.
3. **"Finished" words** — permanently excluded, as you specified. Should there be a separate
   "review finished words" mode later (opt-in, not part of normal practice), or truly never again?

I'll treat my proposed defaults above as accepted unless you say otherwise, and move on to
generating the pilot content batch next.
