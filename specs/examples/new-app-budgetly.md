# New app spec — Budgetly (example)

This is a completed example of `specs/new-app.md`.
Copy `specs/new-app.md` (not this file) as your starting point.

```bash
forge feature --spec specs/my-app.md --project .
```

---

## App identity

- **App name:** Budgetly
- **Package:** com.budgetly.app
- **iOS bundle ID:** com.budgetly.app
- **Company:** AppLabs, LLC
- **Category:** Finance / Personal budgeting

---

## Product vision

Budgetly lets you track spending by category so you always know where your money
is going — without connecting to your bank.

---

## Product principles

- Local-first — no data leaves the device
- Simple over powerful — one clear action per screen
- No account required — open the app and start immediately
- Accessible from the start, not retrofitted
- Transparent: no hidden fees, no ads in v1

---

## Target users

Adults who want a lightweight spending tracker without linking a bank account.
No financial or technical knowledge assumed. Must be usable by someone who has
never used a budgeting app before.

---

## Platforms

- **Android minimum SDK:** 26 (Android 8.0)
- **iOS minimum version:** 16.0
- **Tablet support:** No

---

## Design system

### Colors

- Primary: #4CAF82
- On-primary: #FFFFFF
- Secondary: #A8D5BA
- Background: #F9FAFB
- Surface: #FFFFFF
- Error: #E53935

### Typography

- Headline: Inter 28sp, weight 700
- Title: Inter 20sp, weight 600
- Body: Inter 16sp, weight 400
- Label: Inter 12sp, weight 500

### Component style notes

- Buttons: fully rounded (50% radius), 52dp height, no elevation
- Cards: 16dp corner radius, subtle shadow (elevation 2dp), no stroke
- Top bar: no shadow, surface color background, title centered
- Bottom nav: surface color, no border, filled icon for selected tab

---

## Screens

### Transaction list (home)

- Top bar: month title (e.g., "October 2026") with left/right chevrons to change months
- Summary row below top bar: total spent this month in large type, budget remaining in smaller type
- Scrollable list of transactions, newest first
- Each row: category color dot, description, amount (negative = expense), date
- Empty state: centered illustration, "No transactions yet", "Add your first one" button
- FAB bottom-right: "+" to add a transaction

### Add / edit transaction

- Sheet (bottom sheet, not full screen)
- Fields: amount (numeric, large input), description (text), category (chip selector), date (defaults to today, tappable to change)
- Save button at bottom, full width
- Delete button shown only when editing an existing transaction (red text, bottom of sheet)

### Category list

- Reachable via bottom nav tab "Categories"
- List of categories, each showing: color swatch, name, amount spent this month, progress bar toward budget
- "Add category" button at top right
- Tap category → category detail screen

### Category detail

- Top bar with category name and back arrow
- Budget amount with edit pencil icon
- Monthly spend vs budget chart (simple bar, current month only)
- List of transactions in this category this month

### Add / edit category

- Sheet
- Fields: name, color picker (12 preset colors), monthly budget amount (optional)
- Save / Delete buttons

### Settings

- Reachable via bottom nav tab "Settings"
- Rows: Currency (shows current, taps to a picker), Theme (system/light/dark toggle)
- App version shown at bottom in dim text

---

## Navigation

- Bottom navigation bar: Transactions (home), Categories, Settings
- Add/edit transaction and add/edit category open as bottom sheets over the current screen
- Category detail is a full screen push from the category list
- Back navigation follows system back gesture

---

## Authentication

N/A — no authentication. Single-user, entirely local.

---

## Data and storage

- All transactions and categories stored with SQLDelight in shared:data
- User preferences (currency, theme) stored with DataStore in shared:data
- No cloud sync

---

## Backend and API

N/A — fully local, no backend.

---

## Offline behavior

Everything. No network calls in this version.

---

## Notifications

N/A — no notifications in this version.

---

## Analytics

N/A — no analytics in this version.

---

## Crash reporting

N/A — no crash reporting in this version.

---

## Monetization

Free with no ads. No monetization in v1.

---

## Accessibility

- All interactive elements have content descriptions
- Minimum touch target 48dp
- Support system font size scaling up to 1.3x without layout breakage

---

## Localization

English only. All strings extracted to string resources for future localization.

---

## Performance

- App cold start under 2 seconds
- Transaction list scrolls at 60fps with up to 500 rows

---

## Security and privacy

No sensitive data written to logs. No analytics.

---

## Acceptance criteria

- [ ] App builds and runs on Android without errors
- [ ] Package and app name updated — no references to ForgeStarter remain
- [ ] All five screens are reachable via navigation
- [ ] Transactions persist across app restarts
- [ ] Categories persist across app restarts
- [ ] Adding a transaction with a category reflects in the category detail
- [ ] Theme preference persists across app restarts
- [ ] All unit tests pass

---

## Explicit non-goals

- No bank account integration or automatic import
- No cloud sync or multi-device support
- No receipt scanning or photo attachments
- No shared budgets
- No recurring transactions
- No iPad-specific layout
- No widgets

---

## Future roadmap

- v2: iCloud/Google Drive export — design repository interfaces now to support an optional remote backend without restructuring the data layer
- v2: Recurring transaction templates
- v3: CSV import/export
- v3: Budget rollover

---

## Technical notes

- Replace Hilt with Koin — this app has no Android-specific DI needs
- Use SQLDelight for all persistence (transactions, categories) — replace the in-memory stubs
- Use DataStore for preferences only (currency, theme)
- All coroutine dispatchers injected via constructor for testability
