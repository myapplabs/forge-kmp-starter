package com.forge.starter.ui.render

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.Density
import llc.applabs.design.AppLabsTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import javax.imageio.ImageIO
import kotlin.coroutines.EmptyCoroutineContext

/**
 * FORGE render tier harness (forge docs/render-tier.md, P0 spike).
 *
 * Renders each catalog entry headlessly on the JVM — light and dark, font scale 1.0 and 2.0 — on a
 * Pixel-class phone canvas, and writes for each render a PNG and the semantics tree as JSON to
 * `build/forge-render/`, plus `index.json`. FORGE's ANVIL reads them (RENDER-00x). No device, no emulator.
 */
class RenderCatalog {
    val entries = mutableListOf<Entry>()

    class Entry(val screen: String, val state: String, val kind: String, val content: @Composable () -> Unit)

    inner class Screen(private val name: String, private val kind: String) {
        fun state(state: String, content: @Composable () -> Unit) {
            entries += Entry(name, state, kind, content)
        }
    }

    /** A screen: its content composable with fixed sample states. */
    fun screen(name: String, block: Screen.() -> Unit) = Screen(name, "screen").block()

    /** A design component on its own (create-cmp's "component stories"): catches what no screen shows yet. */
    fun component(name: String, block: Screen.() -> Unit) = Screen(name, "component").block()
}

fun renderCatalog(block: RenderCatalog.() -> Unit): RenderCatalog = RenderCatalog().apply(block)

/**
 * A stand-in for an app interface nothing in the app's tests implements (ForgeRenderLocals.kt): every call returns
 * its type's empty value — false, 0, "", empty collections, an empty Flow — and an app interface another inert
 * stand-in. What has no empty value is null, so a screen that needs it still fails as RENDER-001.
 */
inline fun <reified T : Any> inert(): T = Inert.of(T::class.java)

object Inert {
    @Suppress("UNCHECKED_CAST")
    fun <T> of(type: Class<T>): T = Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { proxy, m, args ->
        when {
            m.name == "equals" && m.parameterCount == 1 -> proxy === args?.get(0)
            m.name == "hashCode" && m.parameterCount == 0 -> System.identityHashCode(proxy)
            m.name == "toString" && m.parameterCount == 0 -> "inert ${type.simpleName}"
            m.isDefault -> InvocationHandler.invokeDefault(proxy, m, *(args ?: emptyArray()))
            else -> empty(m.returnType)
        }
    } as T

    private fun empty(t: Class<*>): Any? = when (t) {
        Void.TYPE, Unit::class.java -> Unit
        java.lang.Boolean.TYPE, java.lang.Boolean::class.java -> false
        Integer.TYPE, Integer::class.java -> 0
        java.lang.Long.TYPE, java.lang.Long::class.java -> 0L
        java.lang.Float.TYPE, java.lang.Float::class.java -> 0f
        java.lang.Double.TYPE, java.lang.Double::class.java -> 0.0
        java.lang.Short.TYPE, java.lang.Short::class.java -> 0.toShort()
        java.lang.Byte.TYPE, java.lang.Byte::class.java -> 0.toByte()
        Character.TYPE, Character::class.java -> '\u0000'
        String::class.java, CharSequence::class.java -> ""
        List::class.java, Collection::class.java, Iterable::class.java -> emptyList<Any>()
        Set::class.java -> emptySet<Any>()
        Map::class.java -> emptyMap<Any, Any>()
        Flow::class.java -> emptyFlow<Any>()
        else -> if (t.isInterface && !t.name.matches(Regex("^(java|javax|kotlin|kotlinx|androidx)\\..*"))) of(t) else null
    }
}

object ForgeRender {
    const val WIDTH_DP = 412
    const val HEIGHT_DP = 915
    const val DENSITY = 2.625f
    val outDir = File("build/forge-render")

    data class Variant(val theme: String, val fontScale: Float) {
        val dark get() = theme == "dark"
        val suffix get() = "$theme-${if (fontScale == 1f) "1x" else "${fontScale}x"}"
    }

    val variants = listOf(Variant("light", 1f), Variant("dark", 1f), Variant("light", 2f), Variant("dark", 2f))

    /** Renders every entry × variant; returns the index rows (one per render, with "ok" or the error). */
    @OptIn(ExperimentalTestApi::class)
    fun renderAll(catalog: RenderCatalog): List<Map<String, Any?>> {
        val mainSet = ensureMain()
        try {
            return renderEach(catalog)
        } finally {
            if (mainSet) testDispatchers("resetMain", Dispatchers::class.java)?.invoke(null, Dispatchers)
        }
    }

    /**
     * ViewModels launch on `Dispatchers.Main` (`viewModelScope`), which a desktop test classpath often lacks.
     * When it does, and kotlinx-coroutines-test is there, Main becomes `Unconfined` for the renders (as an app's
     * own UI tests do with `Dispatchers.setMain`) — true when it was set here, to be reset afterwards.
     */
    private fun ensureMain(): Boolean {
        if (runCatching { Dispatchers.Main.isDispatchNeeded(EmptyCoroutineContext) }.isSuccess) return false
        val setMain = testDispatchers("setMain", Dispatchers::class.java, CoroutineDispatcher::class.java) ?: return false
        return runCatching { setMain.invoke(null, Dispatchers, Dispatchers.Unconfined) }.isSuccess
    }

    private fun testDispatchers(name: String, vararg types: Class<*>) = runCatching {
        Class.forName("kotlinx.coroutines.test.TestDispatchers").getMethod(name, *types)
    }.getOrNull()

    @OptIn(ExperimentalTestApi::class)
    private fun renderEach(catalog: RenderCatalog): List<Map<String, Any?>> {
        outDir.mkdirs()
        val rows = mutableListOf<Map<String, Any?>>()
        for (entry in catalog.entries) for (v in variants) {
            val name = "${entry.screen}-${entry.state}-${v.suffix}"
            val started = System.nanoTime()
            val row = mutableMapOf<String, Any?>(
                "name" to name, "screen" to entry.screen, "state" to entry.state, "kind" to entry.kind, "theme" to v.theme,
                "fontScale" to v.fontScale, "widthDp" to WIDTH_DP, "heightDp" to HEIGHT_DP, "density" to DENSITY,
            )
            try {
                runDesktopComposeUiTest(
                    width = (WIDTH_DP * DENSITY).toInt(), height = (HEIGHT_DP * DENSITY).toInt(),
                ) {
                    setContent {
                        CompositionLocalProvider(LocalDensity provides Density(DENSITY, v.fontScale)) {
                            // What the app provides at its root (ForgeRenderLocals.kt), so screens reading it render.
                            RenderLocals {
                                AppLabsTheme(useDarkTheme = v.dark) {
                                    // The screen's own Scaffold paints the background; a content composable
                                    // rendered alone gets the theme's, as it would inside the app.
                                    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                                        entry.content()
                                    }
                                }
                            }
                        }
                    }
                    val settled = settle { tree(onRoot(useUnmergedTree = true).fetchSemanticsNode()) }
                    ImageIO.write(onRoot().captureToImage().toAwtImage(), "png", File(outDir, "$name.png"))
                    File(outDir, "$name.json").writeText(settled)
                }
                row["status"] = "ok"
                row["png"] = "$name.png"
                row["tree"] = "$name.json"
            } catch (t: Throwable) {
                row["status"] = "error"
                row["error"] = "${t::class.simpleName}: ${t.message}".take(800)
            }
            row["ms"] = (System.nanoTime() - started) / 1_000_000
            rows += row
        }
        File(outDir, "index.json").writeText(
            "{\"schema\":1,\"renders\":[\n" + rows.joinToString(",\n") { json(it) } + "\n]}\n",
        )
        return rows
    }

    const val SETTLE_STEP_MS = 40L
    const val SETTLE_MAX_STEPS = 50

    /**
     * Waits until the screen stops changing and returns its last tree. A screen backed by a ViewModel gets
     * its state from another thread (often `Dispatchers.Default`), which `waitForIdle` does not wait for, so
     * the tree must read the same twice in a row, [SETTLE_STEP_MS] apart (at most [SETTLE_MAX_STEPS] steps).
     */
    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.settle(read: () -> String): String {
        waitForIdle()
        var last = read()
        repeat(SETTLE_MAX_STEPS) {
            Thread.sleep(SETTLE_STEP_MS)
            waitForIdle()
            val now = read()
            if (now == last) return now
            last = now
        }
        return last
    }

    private fun tree(root: SemanticsNode): String = "{\"schema\":1,\"root\":${node(root)}}\n"

    private fun node(n: SemanticsNode): String {
        val c = n.config
        val fields = linkedMapOf<String, Any?>(
            "id" to n.id,
            "text" to c.getOrNull(SemanticsProperties.Text)?.joinToString(" ") { it.text }?.ifEmpty { null },
            "contentDescription" to c.getOrNull(SemanticsProperties.ContentDescription)?.joinToString(" ")?.ifEmpty { null },
            "role" to c.getOrNull(SemanticsProperties.Role)?.toString(),
            "testTag" to c.getOrNull(SemanticsProperties.TestTag),
            "clickable" to c.contains(SemanticsActions.OnClick),
            "disabled" to c.contains(SemanticsProperties.Disabled),
            "heading" to c.contains(SemanticsProperties.Heading),
            "boundsPx" to rect(n.boundsInRoot),
            "touchPx" to rect(n.touchBoundsInRoot),
        )
        val kids = n.children.joinToString(",") { node(it) }
        return json(fields).dropLast(1) + ",\"children\":[$kids]}"
    }

    private fun rect(r: Rect) = listOf(r.left, r.top, r.right, r.bottom).map { Math.round(it * 10) / 10.0 }

    private fun json(m: Map<String, Any?>): String = m.entries.joinToString(",", "{", "}") { (k, v) -> "\"$k\":${value(v)}" }

    private fun value(v: Any?): String = when (v) {
        null -> "null"
        is Boolean, is Int, is Long -> v.toString()
        is Float -> if (v.isFinite()) v.toString() else "null"
        is Double -> if (v.isFinite()) v.toString() else "null"
        is List<*> -> v.joinToString(",", "[", "]") { value(it) }
        else -> "\"" + v.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""
    }
}
