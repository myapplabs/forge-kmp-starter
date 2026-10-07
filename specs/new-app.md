# New app spec

Use this template when starting a brand new app from ForgeStarter.
Fill in every section. For sections that do not apply, write **N/A** — this
tells Forge the requirement was intentionally excluded, not accidentally omitted.

```bash
forge feature --spec specs/new-app.md --project .
```

---

## App identity

- **App name:** [e.g., "Budgetly"]
- **Package:** [e.g., "com.budgetly.app"]
- **iOS bundle ID:** [e.g., "com.budgetly.app"]
- **Company:** [e.g., "AppLabs, LLC"]
- **Category:** [e.g., "Finance / Personal budgeting"]

---

## Product vision

<!-- One sentence. The core promise of the app. -->

[e.g., "Budgetly lets you track spending by category so you always know where
your money is going, without connecting to your bank."]

---

## Product principles

<!-- The values that guide every decision Forge makes when the spec is ambiguous.
     Keep this short — 5 to 10 principles. -->

- [e.g., "Local-first — no data leaves the device unless the user explicitly exports it"]
- [e.g., "Simple over powerful — remove friction before adding features"]
- [e.g., "No dark patterns — monetization is transparent"]
- [e.g., "Accessibility from the start, not retrofitted"]

---

## Target users

<!-- Who uses this app? What is their technical level?
     Forge uses this to calibrate UI complexity and onboarding requirements. -->

[e.g., "Adults who want to track personal spending without connecting to a bank.
No technical knowledge assumed. The app must be usable by someone who has never
used a budgeting app before."]

---

## Platforms

- **Android minimum SDK:** [e.g., 26 (Android 8.0)]
- **iOS minimum version:** [e.g., 16.0]
- **Tablet support:** [Yes / No / iPad only]

---

## Design system

<!-- Describe your visual design. Forge will use Material3 defaults if this
     section is left blank — fine for a prototype, not for a branded app.
     Copy your values from Figma or your design tool. -->

### Colors

<!-- List your semantic color tokens. Use hex values. -->

- Primary: [e.g., #6750A4]
- On-primary: [e.g., #FFFFFF]
- Secondary: [e.g., #625B71]
- Background: [e.g., #FFFBFE]
- Surface: [e.g., #FFFBFE]
- Error: [e.g., #B3261E]

OR: **N/A — use Material3 defaults.**

### Typography

- Display: [e.g., "Inter 57sp, weight 400"]
- Headline: [e.g., "Inter 32sp, weight 600"]
- Body: [e.g., "Inter 16sp, weight 400"]
- Label: [e.g., "Inter 11sp, weight 500"]

OR: **N/A — use Material3 defaults.**

### Component style notes

<!-- Describe how key components should look if they differ from Material3 defaults. -->

- Buttons: [e.g., "Fully rounded (50% radius), 48dp height, no elevation"]
- Cards: [e.g., "12dp corner radius, 1dp outline stroke, no shadow"]
- Top bar: [e.g., "No shadow, surface color background"]

OR: **N/A — use Material3 defaults.**

---

## Screens

<!-- List every screen in the initial version. For each screen, describe
     what the user sees and what actions are available.
     Be specific — Forge implements exactly what is described here. -->

### [Screen 1 name]

- [e.g., "Shows a list of transactions for the current month, newest first"]
- [e.g., "Each row: category icon, description, amount, date"]
- [e.g., "Floating action button to add a transaction"]
- [e.g., "Top bar: current month with left/right arrows to change months"]

### [Screen 2 name]

- [...]

### [Screen 3 name — repeat as needed]

- [...]

---

## Navigation

<!-- How does the user move between screens? -->

- [e.g., "Bottom navigation bar with three tabs: Transactions, Summary, Settings"]
- [e.g., "Tapping a transaction opens an edit sheet"]
- [e.g., "Back navigation follows the system back gesture/button"]

---

## Authentication

<!-- Is there a login or account system? -->

[e.g., "No authentication. The app is single-user and entirely local."]

OR

[e.g., "Email + password sign-in via Firebase Auth. Google Sign-In as an
alternative. No guest mode — account is required to use the app."]

---

## Data and storage

<!-- What data does the app store? Where does it live?
     Be explicit about persistence requirements. -->

- [e.g., "All transactions stored locally using SQLDelight"]
- [e.g., "User preferences (theme, currency) stored in DataStore"]
- [e.g., "No cloud sync in this version"]

---

## Backend and API

<!-- Is there a backend? What does it do? -->

[e.g., "N/A — fully local, no backend."]

OR

[e.g., "REST API at api.budgetly.app. Endpoints needed:
- GET /categories — fetch default categories
- POST /export — upload an encrypted backup
Authentication: JWT bearer token from Firebase Auth."]

---

## Offline behavior

<!-- What works without a network connection? -->

[e.g., "Everything. The app is fully offline — no network calls in this version."]

OR

[e.g., "All local data is accessible offline. Sync happens in the background
when connectivity is restored. The UI shows a sync status indicator."]

---

## Notifications

<!-- Push notifications? Local (scheduled) notifications? Neither? -->

[e.g., "N/A — no notifications in this version."]

OR

[e.g., "Local notification when the user approaches 90% of a category budget.
No push notifications."]

---

## Analytics

<!-- Which analytics provider? What events matter? -->

[e.g., "N/A — no analytics in this version."]

OR

[e.g., "Firebase Analytics. Track: app_open, transaction_added,
category_created, export_completed. No PII in event parameters."]

---

## Crash reporting

[e.g., "Firebase Crashlytics."]

OR

[e.g., "N/A — no crash reporting in this version."]

---

## Monetization

<!-- Free? Paid? Subscriptions? Ads? -->

[e.g., "Free with no ads. No monetization in v1."]

OR

[e.g., "Free tier: up to 3 budget categories. Premium ($2.99/month via
Google Play Billing / StoreKit): unlimited categories + CSV export."]

---

## Accessibility

<!-- Any specific requirements beyond platform defaults? -->

[e.g., "All interactive elements must have content descriptions. Minimum touch
target 48dp. Support system font size scaling."]

OR

[e.g., "N/A — use platform defaults for this version."]

---

## Localization

<!-- Which languages? Is RTL support required? -->

[e.g., "English only for this version. String resources must be extracted to
enable future localization."]

---

## Performance

<!-- Any specific targets? -->

[e.g., "App cold start under 2 seconds. Transaction list must scroll at 60fps
with up to 1000 rows."]

OR

[e.g., "N/A — no specific performance targets for this version."]

---

## Security and privacy

<!-- Any requirements beyond the defaults? -->

[e.g., "No sensitive data written to logs. No analytics events containing
transaction amounts or descriptions."]

OR

[e.g., "N/A — standard platform security is sufficient."]

---

## Acceptance criteria

<!-- What does "done" look like for this initial version?
     Forge uses these to evaluate whether the implementation is complete. -->

- [ ] [e.g., "App builds and runs on Android without errors"]
- [ ] [e.g., "All screens are reachable via navigation"]
- [ ] [e.g., "Transactions persist across app restarts"]
- [ ] [e.g., "Package and app name updated — no references to ForgeStarter remain"]
- [ ] [e.g., "All tests pass"]

---

## Explicit non-goals

<!-- What does this app intentionally NOT do in this version?
     Forge will not implement anything listed here.
     This is one of the most important sections — be thorough. -->

- [e.g., "No bank account integration or automatic transaction import"]
- [e.g., "No cloud sync or multi-device support"]
- [e.g., "No shared budgets or collaboration features"]
- [e.g., "No receipt scanning or photo attachments"]
- [e.g., "No iPad-specific layout"]

---

## Future roadmap

<!-- Features planned for later versions that Forge should NOT build now,
     but should keep in mind architecturally so they are not blocked. -->

- [e.g., "v2: iCloud / Google Drive sync — repository interfaces should be
  designed to support a remote backend without restructuring the data layer"]
- [e.g., "v2: iPad split-view layout"]
- [e.g., "v3: receipt scanning with ML Kit"]

---

## Technical notes

<!-- Optional. Specific preferences or constraints Forge should know about.
     Leave blank to accept the defaults in CONVENTIONS.md and project.yaml. -->

- [e.g., "Replace Hilt with Koin for dependency injection"]
- [e.g., "Use SQLDelight instead of the in-memory repository stubs"]
- [e.g., "All coroutine dispatchers must be injectable for testing"]
