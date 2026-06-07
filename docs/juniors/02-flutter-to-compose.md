# 🦋 Flutter to Jetpack Compose Cheat Sheet

Welcome! Since this Android app is a native port of the Flutter Athkarix app, you might be bringing a lot of Flutter knowledge with you. 

Good news: Jetpack Compose and Flutter are both **declarative UI frameworks**. They work almost exactly the same way! This guide will translate your Flutter vocabulary into Jetpack Compose vocabulary.

---

## 1. The Core UI Building Blocks

In Flutter, everything is a `Widget`. In Compose, everything is a `@Composable` function.

| Flutter Concept | Jetpack Compose Equivalent |
|-----------------|----------------------------|
| `StatelessWidget` | `@Composable fun MyWidget() { }` |
| `build(BuildContext)` | *No build method! The function itself is the builder.* |
| `MaterialApp` | `MaterialTheme { ... }` |
| `Scaffold` | `Scaffold { ... }` |
| `Text('Hello')` | `Text("Hello")` |

### Example Comparison:
**Flutter:**
```dart
class Greeting extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Text('Hello World');
  }
}
```

**Jetpack Compose:**
```kotlin
@Composable
fun Greeting() {
    Text("Hello World")
}
```

---

## 2. Layouts (Rows, Columns, and Boxes)

| Flutter | Jetpack Compose |
|---------|-----------------|
| `Column` | `Column` |
| `Row` | `Row` |
| `Stack` | `Box` |
| `ListView.builder` | `LazyColumn` (or `LazyRow`) |
| `Expanded` | `Modifier.weight(1f)` |

---

## 3. The Big Difference: Modifiers

In Flutter, to add padding or center something, you wrap it in another Widget (`Padding`, `Center`, `Align`).
In Compose, we avoid deep nesting by using a **`Modifier`**. You chain methods on a Modifier to apply styling, padding, and layout rules.

**Flutter:**
```dart
Padding(
  padding: EdgeInsets.all(16.0),
  child: GestureDetector(
    onTap: () { print("Tapped"); },
    child: Container(
      color: Colors.blue,
      child: Text('Click me'),
    ),
  ),
)
```

**Jetpack Compose:**
```kotlin
Text(
    text = "Click me",
    modifier = Modifier
        .padding(16.dp)
        .background(Color.Blue)
        .clickable { println("Tapped") }
)
```
> [!IMPORTANT]
> **Order matters in Modifiers!** `Modifier.padding(16.dp).background(Color.Blue)` is different from `Modifier.background(Color.Blue).padding(16.dp)`. The operations are applied sequentially from top to bottom.

---

## 4. State Management

Flutter uses `StatefulWidget` and `setState` for local state, and things like `Provider` or `Bloc` for global state.

### Local State
**Flutter:** `StatefulWidget` + `setState(() { count++; })`
**Jetpack Compose:** `var count by remember { mutableStateOf(0) }`

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    
    Button(onClick = { count++ }) {
        Text("Count is $count")
    }
}
```

### Global/Architecture State
**Flutter:** `Bloc` / `Cubit` / `Provider`
**Jetpack Compose:** `ViewModel` + `StateFlow`

```kotlin
// In Compose, you inject the ViewModel and observe its StateFlow
@Composable
fun AthkarScreen(viewModel: AthkarViewModel) {
    // This is equivalent to context.watch() or BlocBuilder
    val state by viewModel.uiState.collectAsState() 
    
    Text(state.duaText)
}
```

---

## 5. Navigation

`NavController.navigate(Routes.X)` ↔ `Navigator.pushNamed(context, '/x')`; `popBackStack()` ↔ `pop()`. Routes are constants in `navigation/Routes.kt`. Full walkthrough (path arguments, `navArgument`, single-Activity pattern, `PlaceholderScreenWithVM`) lives in **[`07-navigation-and-di.md`](./07-navigation-and-di.md)**.

---

## 💡 Summary Mindset Shift
When writing Compose, stop thinking: *"Which widget do I wrap this in?"*
Start thinking: *"Which Modifier do I apply to this?"*
