# New app spec

Use this template when starting a brand new app from ForgeStarter.
Fill in every section, then run:

```bash
forge feature --spec specs/new-app.md --project .
```

Forge will rename the starter, set up your app identity, and build out your
initial screens in a single run.

---

## App identity

- **App name:** [e.g., "Budgetly"]
- **Package:** [e.g., "com.budgetly.app"]
- **iOS bundle ID:** [e.g., "com.budgetly.app"]
- **Description:** [One sentence — what does this app do?]

---

## What this app does

<!-- 2–5 sentences describing the app from the user's perspective.
     Who uses it? What problem does it solve? What do they do in it? -->

[e.g., "Budgetly helps individuals track their monthly spending across categories.
Users add transactions manually or import from a CSV. The app shows a monthly
summary and alerts them when they are close to a category limit."]

---

## Screens

<!-- List every screen in the initial version. For each screen, describe
     what the user sees and what they can do. Be specific — Forge will
     implement exactly what you describe here. -->

### [Screen 1 name]

<!-- What is this screen for? -->

- [e.g., "Shows a list of transactions for the current month, newest first"]
- [e.g., "Each row shows: category icon, description, amount, date"]
- [e.g., "Floating action button in the bottom right to add a transaction"]
- [e.g., "Top bar shows the current month with left/right arrows to navigate months"]

### [Screen 2 name]

- [...]

### [Screen 3 name — repeat as needed]

- [...]

---

## Navigation

<!-- How does the user move between screens?
     A simple list of transitions is enough. -->

- [e.g., "Bottom navigation bar with three tabs: Transactions, Summary, Settings"]
- [e.g., "Tapping a transaction row opens a detail/edit sheet"]
- [e.g., "FAB on Transactions tab opens the Add Transaction screen"]

---

## Acceptance criteria

<!-- What does "done" look like for this initial version?
     Forge uses these to evaluate whether the implementation is complete. -->

- [ ] [e.g., "App builds and runs on Android without errors"]
- [ ] [e.g., "All screens are reachable via navigation"]
- [ ] [e.g., "Transactions persist across app restarts"]
- [ ] [e.g., "Package and app name are updated throughout — no references to ForgeStarter remain"]

---

## Out of scope

<!-- Be explicit. Forge will not implement things listed here,
     which keeps the first run focused and the plan reviewable. -->

- [e.g., "No authentication in this version — that is a separate spec"]
- [e.g., "No cloud sync — local storage only for now"]
- [e.g., "No CSV import in this version"]

---

## Technical notes

<!-- Optional. Specific preferences or constraints Forge should know about.
     Leave blank to use the defaults in CONVENTIONS.md. -->

- [e.g., "Use SQLDelight for persistence instead of the in-memory stubs"]
- [e.g., "Use a Room-style repository pattern for the data layer"]
- [e.g., "Replace Hilt with Koin for dependency injection"]
