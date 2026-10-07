# Feature spec

Use this template when adding a feature to an existing app.
Fill in every section, then run:

```bash
forge feature --spec specs/feature.md --project .
```

---

## [Feature name]

<!-- One sentence. What does this feature do from the user's perspective? -->

**Summary:** [e.g., "Add a settings screen where users can toggle dark mode, change their display name, and sign out."]

---

## Screens and UI

<!-- Describe what the user sees. One section per screen.
     The more specific you are, the less guessing Forge has to do. -->

### [Screen name]

- [e.g., "Top bar with back arrow and title 'Settings'"]
- [e.g., "Row with label 'Dark mode' and a toggle switch"]
- [e.g., "Row with label 'Display name' and current name as subtitle — taps open an edit dialog"]
- [e.g., "Sign out button at the bottom, red text, no icon"]

---

## Acceptance criteria

<!-- What does "done" look like? Be specific and testable.
     Forge uses these to evaluate whether the implementation is complete. -->

- [ ] [e.g., "Settings screen is accessible from the home screen via a gear icon in the top bar"]
- [ ] [e.g., "Dark mode toggle updates the app theme immediately without restarting"]
- [ ] [e.g., "Display name change is persisted across app restarts"]
- [ ] [e.g., "Sign out clears all local state and navigates to the login screen"]

---

## Out of scope

<!-- Explicitly list what this feature does NOT include.
     This prevents Forge from over-implementing. -->

- [e.g., "No push notification settings in this release"]
- [e.g., "No account deletion — that is a separate feature"]

---

## Technical notes

<!-- Optional. Specific preferences or constraints Forge should know about.
     Leave blank to use the defaults in CONVENTIONS.md. -->

- [e.g., "Persist settings with DataStore, not SharedPreferences"]
- [e.g., "Reuse the existing AppTopBar component from shared:ui/components"]
- [e.g., "Sign out should call AuthRepository.signOut() — that interface already exists in shared:domain"]
