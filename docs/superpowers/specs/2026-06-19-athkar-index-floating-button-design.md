# Athkar Index Floating Button

## Summary

Replace the index drawer button (FormatListBulleted icon) in the top bar with a floating action button at the top-right corner for Morning (Sabah) and Evening (Massa) athkar screens.

## Motivation

The index button in the top bar is small and hard to reach. A floating button at the top-right is more visible and easier to tap.

## Changes

### AthkarScreen.kt

- Remove the `showIndex`-gated FormatListBulleted `IconButton` from the top bar `actions` block
- Add a new floating `FloatingActionButton` at `Alignment.TopEnd` when `showIndex = true`
  - Black background, gold FormatListBulleted icon, 40dp size
  - `onClick` opens the `indexDrawerState` drawer (same drawer as before)

### Files Touched

- `app/src/main/java/com/athkarix/app/ui/screens/athkar/AthkarScreen.kt`

## Current vs. New

| Area | Before | After |
|------|--------|-------|
| Top bar actions | Index icon, Share, Font controls | Share, Font controls |
| Floating buttons | Counter (bottom-left), Reset (bottom-right) | Counter (bottom-left), Reset (bottom-right), Index drawer (top-right) |
