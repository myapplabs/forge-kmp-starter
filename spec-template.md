# Spec template

Copy this file, fill it in, and pass it to Forge:

```bash
forge feature --spec path/to/your-spec.md --project .
```

---

## [Feature name]

<!-- One sentence. What does this feature do from the user's perspective? -->

**Summary:** [e.g., "Add a settings screen where users can toggle dark mode, change their display name, and sign out."]

---

### Acceptance criteria

<!-- What does "done" look like? Be specific and testable.
     Forge uses these to evaluate whether the implementation is complete. -->

- [ ] [e.g., "Settings screen is accessible from the home screen via a gear icon in the top bar"]
- [ ] [e.g., "Dark mode toggle updates the app theme immediately without restarting"]
- [ ] [e.g., "Display name change is persisted across app restarts"]
- [ ] [e.g., "Sign out clears all local state and navigates to the login screen"]

---

### Screens and UI

<!-- Describe what the user sees. Sketch it in text if helpful.
     The more specific you are here, the less guessing Forge has to do. -->

**[Screen name]**
- [e.g., "Top bar with back arrow and title 'Settings'"]
- [e.g., "Row with label 'Dark mode' and a toggle switch"]
- [e.g., "Row with label 'Display name' and the current name as subtitle — taps open an edit dialog"]
- [e.g., "Sign out button at the bottom, red text, no icon"]

---

### Out of scope

<!-- Explicitly list what this feature does NOT include.
     This prevents Forge from implementing things you didn't ask for. -->

- [e.g., "No push notification settings in this release"]
- [e.g., "No account deletion — that is a separate feature"]

---

### Technical notes

<!-- Optional. Use this for preferences or constraints Forge should know about.
     Leave blank if you have no specific requirements — Forge will follow CONVENTIONS.md. -->

- [e.g., "Persist settings with DataStore, not SharedPreferences"]
- [e.g., "Reuse the existing AppTopBar component from shared:ui/components"]
- [e.g., "The sign-out logic should call AuthRepository.signOut() — that interface already exists in shared:domain"]

---

### App / package rename (first run only)

<!-- Fill this in only if you want to rename the starter.
     Delete this section for all subsequent feature specs. -->

- App name: [e.g., "MyApp"]
- Package: [e.g., "com.mycompany.myapp"]
- iOS bundle ID: [e.g., "com.mycompany.myapp"]
