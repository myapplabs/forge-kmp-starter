# Feature spec

Use this template when adding a feature to an existing app.
Fill in every section. For sections that do not apply, write **N/A**.

```bash
forge feature --spec specs/my-feature.md --project .
```

---

## [Feature name]

<!-- One sentence. What does this feature do from the user's perspective? -->

**Summary:** [e.g., "Add a settings screen where users can toggle dark mode, change their display name, and sign out."]

---

## Background

<!-- What does Forge need to know before it starts?
     - Decisions already made in earlier runs that this builds on
     - Existing interfaces, components, or patterns to follow
     - Anything in CONVENTIONS.md that is especially relevant here
     Leave blank if this is a standalone feature with no prior context. -->

- [e.g., "AuthRepository.signOut() already exists in shared:domain — use it, do not reimplement"]
- [e.g., "AppTopBar is the shared component for all top bars — reuse it, do not create a new one"]
- [e.g., "Theme preference is already persisted in SettingsRepository — read from there"]

---

## Screens and UI

<!-- Describe what the user sees. One section per new or modified screen.
     The more specific you are, the less guessing Forge has to do. -->

### [Screen name]

- [e.g., "Top bar with back arrow and title 'Settings'"]
- [e.g., "Row: label 'Dark mode', toggle switch on the right"]
- [e.g., "Row: label 'Display name', current name as subtitle — taps open an edit dialog"]
- [e.g., "Sign out button at the bottom, red text, no icon"]

---

## Acceptance criteria

<!-- What does "done" look like? Be specific and testable.
     Forge uses these to evaluate whether the implementation is complete. -->

- [ ] [e.g., "Settings screen is accessible from the home screen via a gear icon in the top bar"]
- [ ] [e.g., "Dark mode toggle updates the app theme immediately without restarting"]
- [ ] [e.g., "Display name change is persisted across app restarts"]
- [ ] [e.g., "Sign out clears all local state and navigates to the login screen"]
- [ ] [e.g., "All new code has unit tests"]

---

## What NOT to do

<!-- Be explicit about what Forge should not touch in this run.
     This is different from "out of scope" — it tells Forge which layers,
     files, or features to leave completely alone, even if they seem related.
     This keeps each run focused and the plan reviewable. -->

- [e.g., "Do not modify the authentication logic — only call the existing signOut()"]
- [e.g., "Do not add push notification settings — that is a separate run"]
- [e.g., "Do not change the navigation graph structure — only add the new destination"]
- [e.g., "No changes to shared:data or shared:domain in this run — UI layer only"]

---

## New dependencies

<!-- List any new libraries this feature requires.
     Include the exact Maven coordinates and where to add them.
     N/A if no new dependencies are needed. -->

- [e.g., "`androidx.datastore:datastore-preferences-core:1.1.1` — add to shared:data commonMain"]

```toml
# gradle/libs.versions.toml additions
[versions]
# my-lib = "1.0.0"

[libraries]
# my-lib = { module = "com.example:my-lib", version.ref = "my-lib" }
```

---

## Technical notes

<!-- Optional. Any other constraints or preferences Forge should know about
     that are not already in CONVENTIONS.md or covered above. -->

- [e.g., "Use StateFlow for the display name edit state, not a separate MutableState"]
