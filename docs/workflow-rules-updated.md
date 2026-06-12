# Workflow Rules — Updated Gitflow

## Updated: GitHub Flow with Smart Branch Creation

The `gitflow` alias now includes automatic branch creation for better workflow management.

### Before gitflow (the old way):

```bash
# 1. Assume you're on the right branch (created manually)
git checkout main
git pull origin main
git checkout -b feature/add-user-auth
# ... code changes ...
git add .
git commit -m "feat(user): add JWT refresh token support"
git push -u origin feature/add-user-auth
gh pr create --title "feat(user): add JWT refresh token support"
gh pr merge <PR-number> --merge
git checkout main
git pull origin main
git checkout feature/add-user-auth
```

### After gitflow (the new way):

```bash
# 1. Just run gitflow — it guesses, confirms, and creates the branch
gitflow

# Gitflow does:
# - Analyzes changed files and task context
# - Suggests a branch name (e.g., "feat/add-user-auth")
# - Confirms with you (or lets you edit)
# - Creates the branch from main
# - Continues with the rest of gitflow
# - Returns you to your new branch
```

### Smart Branch Creation Logic

Gitflow's branch guessing:

```
# File → Guess Type → Context → Final Suggestion
# ------------------------
# viewmodel/ files, UI logic → feature/
# fix/ someOption in task description → fix/
# refactor/ config cleanup → chore/
# setup new tests → test/
# manual DI injection → refactor/

# Example task: "Fix login timeout error"
# viewmodel/ → login failure → fix/
# task context → "Fix login timeout error"
# Result: "fix/login-timeout"
```

### Quick Reference

| Rule | Trigger | Action |
|------|---------|--------|
| Branch Naming | Start any plan | Create `<type>/<desc>` branch first |
| Task Tracking | Execute any plan | Break into subtasks, update board after EACH |
| `gitflow` | User says "gitflow" | **smart branch from main** → add → commit → push → PR → merge → update main → keep branch |

### What Gitflow Handles

Gitflow runs this entire sequence automatically:

1. **Smart Branch Creation** (NEW)
   - If on main: guess → confirm → create from main
   - If already on feature/fix: skip to step 2

2. `git add .` — Stage all changes
3. `git commit -m "<message>"` — Commit with message
4. `git push -u origin <branch>` — Push to remote
5. `gh pr create --title "<title>" --body "<description>"` — Create PR
6. `gh pr merge <PR-number> --merge` — Accept/merge PR
7. `git checkout main && git pull origin main` — Update main locally
8. `git checkout <branch>` — Return to working branch

### 🔴 NEVER DELETE THE BRANCH — EVER

**Forbidden flags:** `gh pr merge --delete-branch`, `gh pr merge -d`, `git branch -d`, `git push origin --delete`.

**Why never delete:**
- Keep history and context
- Reference later if needed
- Enable hotfix cherry-picks

### Branch Naming Convention

Must follow: `<type>/<short-description>`

| Type | Description |
|------|-------------|
| `feature/` | New feature or enhancement |
| `fix/` | Bug fix |
| `refactor/` | Code restructuring (no behavior change) |
| `docs/` | Documentation only |
| `test/` | Adding or updating tests |
| `chore/` | Maintenance, config, tooling |
| `hotfix/` | Urgent production fix |

**Examples:**
```
feature/add-user-auth
fix/login-timeout-error
refactor/simplify-api-layer
docs/update-readme
test/auth-integration-tests
chore/update-dependencies
hotfix/emergency-login-bug
```

**Rules:**
- Short, kebab-case description
- No issue numbers
- Keep under one line
- Type first, description after slash

### Example Session

```bash
# Task: "Fix login timeout error"
# Current state: on main branch

# User says gitflow
$ gitflow

# Gitflow analysis:
# - viewmodel/ file changed → user feature
# - task: "Fix login timeout error" → fix/
# - context: "login timeout" → login-timeout

# Gitflow prompt:
Suggested branch: fix/login-timeout
  - Confirm → [Enter]
  - Edit → [fix/login-timeout -  my-password-change]

# Gitflow creates branch and continues...
#  1. git checkout main
#  2. git pull origin main
#  3. git checkout -b fix/login-timeout
#  4. git add .
#  5. git commit -m "fix(auth): handle login timeout after 30s"
#  6. git push -u origin fix/login-timeout
#  7. gh pr create ...
#  8. gh pr merge ...
#  9. git checkout main && git pull origin main
#  10. git checkout fix/login-timeout
#
# Result: You're back on your new branch, ready for next work.
```

### How to Execute

```bash
# Run gitflow to handle branch creation + PR flow
./gradlew gitflow

# Or if gitflow is a script:
./gitflow
```

> **Note:** The exact command may vary based on how your project is configured.