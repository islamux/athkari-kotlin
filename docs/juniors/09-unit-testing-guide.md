# Unit Testing Guide for Juniors (No Emulator Required)

Welcome! As a junior developer working on our Kotlin Android app, testing might seem daunting, especially if you think you always need to boot up an Android emulator or use a real device.

**Good news!** You can test most of your logic *locally* on your computer without any emulator. These are called **Local Unit Tests** (or JVM tests). They are fast, reliable, and run directly on your computer's Java Virtual Machine (JVM).

## Why Local Unit Tests?

- **Lightning Fast:** They run in seconds instead of minutes.
- **No Emulator Hassle:** You don't need a heavy emulator taking up your computer's memory.
- **Immediate Feedback:** Perfect for checking if a specific function, calculation, or `ViewModel` logic works correctly.

---

## Where to Put Your Tests

In Android projects, code is separated into folders.
1. `src/main/java/` -> Your actual app code.
2. `src/test/java/` -> **This is where your Local Unit Tests go!** (These run on your JVM).
3. `src/androidTest/java/` -> These are for UI/Integration tests that *require* an emulator (Ignore these for now!).

---

## Writing Your First Test

Let's say we have a very simple utility function in our app that adds two numbers, or formats a string.

**The Code (in `src/main/java/.../MathUtil.kt`):**
```kotlin
object MathUtil {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}
```

**The Test (in `src/test/java/.../MathUtilTest.kt`):**
```kotlin
import org.junit.Assert.assertEquals
import org.junit.Test

class MathUtilTest {

    // The @Test annotation tells the system this is a test function!
    @Test
    fun `when adding 2 and 3, result should be 5`() {
        // 1. Arrange (Set up any data you need)
        val firstNumber = 2
        val secondNumber = 3

        // 2. Act (Call the function you want to test)
        val result = MathUtil.add(firstNumber, secondNumber)

        // 3. Assert (Check if the result matches what you expect)
        assertEquals(5, result)
    }
}
```

### The "Arrange, Act, Assert" Pattern
Notice how the test is broken into three parts:
- **Arrange:** Setup the data.
- **Act:** Execute the function.
- **Assert:** Verify the outcome using functions like `assertEquals`, `assertTrue`, `assertFalse`, or `assertNotNull`.

---

## Testing ViewModels (Advanced but Common)

Often, you'll need to test a `ViewModel`. Because ViewModels interact with data layers and Kotlin Coroutines, testing them can be slightly trickier, but still entirely possible without an emulator!

To test a ViewModel or a Repository, we often use **Mocking**. Mocking means creating a "fake" version of a dependency (like a database or network class) so we can control what it returns without actually making network calls or reading a real database.

Popular mocking libraries include `Mockito` or `MockK`.

**Example using MockK:**
```kotlin
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class MyViewModelTest {

    @Test
    fun `when loading data, state is updated correctly`() {
        // Arrange: Create a "fake" repository
        val mockRepository = mockk<MyRepository>()
        
        // Tell the fake repository what to return when called
        every { mockRepository.getData() } returns "Hello World"
        
        // Pass the fake repository to our ViewModel
        val viewModel = MyViewModel(mockRepository)

        // Act: Trigger the load
        viewModel.loadData()

        // Assert: Check if the ViewModel updated its state
        assertEquals("Hello World", viewModel.uiState.value)
    }
}
```

---

## How to Run Your Tests

You can run your tests directly from Android Studio or via the command line.

### Using Android Studio
1. Open your test file (e.g., `MathUtilTest.kt`).
2. Look for the green "Play" icon 🟢 next to the class name or the specific `@Test` function.
3. Click it and select **Run**.
4. The results will appear in the "Run" tab at the bottom of the screen.

### Using Command Line (Terminal)
To run all your local unit tests across the entire app without opening Android Studio, you can use the terminal (Command Center):
```bash
./gradlew test
```
Or for the debug variant specifically:
```bash
./gradlew testDebugUnitTest
```

---

## Summary
- Put your tests in `src/test/java/`.
- Use the `@Test` annotation.
- Follow the Arrange, Act, Assert pattern.
- If it needs Android components (like Context, Views, Emulator), it belongs in `androidTest`. Otherwise, keep it in `test`!
- Run tests via the green play button in your IDE or `./gradlew test` in terminal.

Happy testing! You've got this.
