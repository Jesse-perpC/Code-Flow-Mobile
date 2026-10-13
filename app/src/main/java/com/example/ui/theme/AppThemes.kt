package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeType(val displayName: String, val tagLine: String) {
    CATPPUCCIN_MOCHA("Catppuccin Mocha", "Soothing pastel lavender, sapphire & deep mocha"),
    NORD_FROST("Nord Frost", "Arctic ice blue, clean polar night & aurora tones"),
    ROSE_PINE("Rosé Pine", "Warm retro dusk, pine green, foam teal & soft gold"),
    GRUVBOX_DARK("Gruvbox Dark", "Warm retro terminal earth tones with amber & olive"),
    CYBER_DARK("Cyber Dark", "High-contrast neon cyan, purple & deep slate"),
    OLED_BLACK("AMOLED Pure Black", "Zero light emission true black for OLED battery saving"),
    TOKYO_NIGHT("Tokyo Night", "Deep navy neon metropolis with electric sky blue and lavender"),
    DRACULA("Dracula Pro", "Vampire dark purple, neon pink, spring green and gold"),
    MONOKAI_PRO("Monokai Pro", "Refined dark charcoal with coral, ice cyan and lime"),
    SYNTHWAVE_84("Synthwave '84", "Retro 80s neon magenta glow with laser cyan and sunset gold")
}

data class AppThemeColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accentPink: Color,
    val accentWarning: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceHighlight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val terminalBg: Color
)

object AppThemes {
    // 1. Catppuccin Mocha (Fresh, trending modern pastel dark theme)
    val CatppuccinMocha = AppThemeColors(
        primary = Color(0xFFCBA6F7),        // Mauve
        secondary = Color(0xFF89B4FA),      // Blue / Sapphire
        tertiary = Color(0xFFA6E3A1),       // Green
        accentPink = Color(0xFFF5C2E7),     // Pink
        accentWarning = Color(0xFFFAB387),  // Peach
        background = Color(0xFF181825),     // Mantle
        surface = Color(0xFF1E1E2E),        // Base
        surfaceVariant = Color(0xFF313244), // Surface0
        surfaceHighlight = Color(0xFF45475A), // Surface1
        textPrimary = Color(0xFFCDD6F4),    // Text
        textSecondary = Color(0xFFA6ADC8),  // Subtext0
        textMuted = Color(0xFF6C7086),      // Overlay0
        terminalBg = Color(0xFF11111B)      // Crust
    )

    // 2. Nord Frost (Calm arctic ice theme)
    val NordFrost = AppThemeColors(
        primary = Color(0xFF88C0D0),        // Frost Cyan
        secondary = Color(0xFF81A1C1),      // Frost Ice Blue
        tertiary = Color(0xFFA3BE8C),       // Aurora Green
        accentPink = Color(0xFFB48EAD),     // Aurora Magenta
        accentWarning = Color(0xFFEBCB8B),  // Aurora Yellow
        background = Color(0xFF242933),     // Polar Night Deep
        surface = Color(0xFF2E3440),        // Polar Night 0
        surfaceVariant = Color(0xFF3B4252), // Polar Night 1
        surfaceHighlight = Color(0xFF4C566A), // Polar Night 3
        textPrimary = Color(0xFFECEFF4),    // Snow Storm Light
        textSecondary = Color(0xFFD8DEE9),  // Snow Storm Mid
        textMuted = Color(0xFF616E88),      // Polar Night Muted
        terminalBg = Color(0xFF1D212A)      // Polar Deepest
    )

    // 3. Rosé Pine (Aesthetic warm dusk theme)
    val RosePine = AppThemeColors(
        primary = Color(0xFFEB6F92),        // Love / Rose
        secondary = Color(0xFF9CCFD8),      // Foam Teal
        tertiary = Color(0xFF31748F),       // Pine Green
        accentPink = Color(0xFFC4A7E7),     // Iris
        accentWarning = Color(0xFFF6C177),  // Gold
        background = Color(0xFF14121E),     // Void
        surface = Color(0xFF191724),        // Base
        surfaceVariant = Color(0xFF26233A), // Surface
        surfaceHighlight = Color(0xFF403D52), // Overlay
        textPrimary = Color(0xFFE0DEF4),    // Text
        textSecondary = Color(0xFF908CAA),  // Subtle
        textMuted = Color(0xFF6E6A86),      // Muted
        terminalBg = Color(0xFF0F0E17)      // Darkest Void
    )

    // 4. Gruvbox Dark (Warm retro terminal earth tones)
    val GruvboxDark = AppThemeColors(
        primary = Color(0xFFFE8019),        // Bright Orange
        secondary = Color(0xFFFABD2F),      // Bright Yellow
        tertiary = Color(0xFFB8BB26),       // Bright Green
        accentPink = Color(0xFFD3869B),     // Bright Purple
        accentWarning = Color(0xFFFABD2F),  // Yellow
        background = Color(0xFF1D2021),     // Hard Dark
        surface = Color(0xFF282828),        // Medium Dark
        surfaceVariant = Color(0xFF3C3836), // Light Dark
        surfaceHighlight = Color(0xFF504945), // Hover Dark
        textPrimary = Color(0xFFEBDBB2),    // Light 1
        textSecondary = Color(0xFFD5C4A1),  // Light 2
        textMuted = Color(0xFF928374),      // Gray
        terminalBg = Color(0xFF141617)      // Terminal Void
    )

    // 5. Cyber Dark (High-contrast neon cyberpunk)
    val CyberDark = AppThemeColors(
        primary = Color(0xFF00E5FF),
        secondary = Color(0xFFBD93F9),
        tertiary = Color(0xFF50FA7B),
        accentPink = Color(0xFFFF79C6),
        accentWarning = Color(0xFFFFB86C),
        background = Color(0xFF0D1117),
        surface = Color(0xFF161B22),
        surfaceVariant = Color(0xFF21262D),
        surfaceHighlight = Color(0xFF30363D),
        textPrimary = Color(0xFFF0F6FC),
        textSecondary = Color(0xFF8B949E),
        textMuted = Color(0xFF6E7681),
        terminalBg = Color(0xFF090D13)
    )

    // 6. AMOLED Pure Black
    val OledBlack = AppThemeColors(
        primary = Color(0xFF00E5FF),
        secondary = Color(0xFFBD93F9),
        tertiary = Color(0xFF50FA7B),
        accentPink = Color(0xFFFF79C6),
        accentWarning = Color(0xFFFFB86C),
        background = Color(0xFF000000),
        surface = Color(0xFF0C0D10),
        surfaceVariant = Color(0xFF16181D),
        surfaceHighlight = Color(0xFF252830),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA0A5B0),
        textMuted = Color(0xFF5C606C),
        terminalBg = Color(0xFF000000)
    )

    // 7. Tokyo Night (Electric neon metropolis)
    val TokyoNight = AppThemeColors(
        primary = Color(0xFF7AA2F7),        // Sky Blue
        secondary = Color(0xFFBB9AF7),      // Purple
        tertiary = Color(0xFF9ECE6A),       // Green
        accentPink = Color(0xFFF7768E),     // Coral Red
        accentWarning = Color(0xFFE0AF68),  // Yellow Gold
        background = Color(0xFF1A1B26),     // Tokyo Deep Navy
        surface = Color(0xFF24283B),        // Elevated Navy
        surfaceVariant = Color(0xFF2F354D), // Card Navy
        surfaceHighlight = Color(0xFF414868), // Selection
        textPrimary = Color(0xFFC0CAF5),    // Bright Text
        textSecondary = Color(0xFF9AA5CE),  // Muted Blue
        textMuted = Color(0xFF565F89),      // Deep Muted
        terminalBg = Color(0xFF16161E)      // Deep Terminal
    )

    // 8. Dracula Pro (Vampire violet & neon)
    val DraculaPro = AppThemeColors(
        primary = Color(0xFFBD93F9),        // Dracula Purple
        secondary = Color(0xFF8BE9FD),      // Cyan
        tertiary = Color(0xFF50FA7B),       // Green
        accentPink = Color(0xFFFF79C6),     // Pink
        accentWarning = Color(0xFFFFB86C),  // Orange
        background = Color(0xFF282A36),     // Dracula Base
        surface = Color(0xFF343746),        // Surface
        surfaceVariant = Color(0xFF44475A), // Current Line
        surfaceHighlight = Color(0xFF6272A4), // Comment/Highlight
        textPrimary = Color(0xFFF8F8F2),    // Foreground
        textSecondary = Color(0xFFE2E2DC),  // Secondary
        textMuted = Color(0xFF6272A4),      // Comment
        terminalBg = Color(0xFF1E1F29)      // Crust
    )

    // 9. Monokai Pro (Warm charcoal & vivid pigments)
    val MonokaiPro = AppThemeColors(
        primary = Color(0xFFFF6188),        // Red/Coral
        secondary = Color(0xFF78DCE8),      // Cyan
        tertiary = Color(0xFFA9DC76),       // Green
        accentPink = Color(0xFFAB9DF2),     // Violet
        accentWarning = Color(0xFFFFD866),  // Yellow
        background = Color(0xFF2D2A2E),     // Dark Charcoal
        surface = Color(0xFF363337),        // Surface
        surfaceVariant = Color(0xFF403E41), // Variant
        surfaceHighlight = Color(0xFF5B595C), // Highlight
        textPrimary = Color(0xFFFCFCFA),    // White
        textSecondary = Color(0xFFC1C0C0),  // Mid
        textMuted = Color(0xFF727072),      // Gray
        terminalBg = Color(0xFF221F22)      // Deep Void
    )

    // 10. Synthwave '84 (Neon retro glow)
    val Synthwave84 = AppThemeColors(
        primary = Color(0xFFFF7EDB),        // Hot Pink
        secondary = Color(0xFF36F9F6),      // Laser Cyan
        tertiary = Color(0xFF72F1B8),       // Neon Mint
        accentPink = Color(0xFFFE4450),     // Neon Red
        accentWarning = Color(0xFFFFE700),  // Sunset Yellow
        background = Color(0xFF262335),     // Synth Night
        surface = Color(0xFF2E2B44),        // Glowing Card
        surfaceVariant = Color(0xFF3E3958), // Card Variant
        surfaceHighlight = Color(0xFF4F486E), // Glow Selection
        textPrimary = Color(0xFFF9EEFF),    // Neon White
        textSecondary = Color(0xFFC4B8DF),  // Lavender
        textMuted = Color(0xFF8478A5),      // Dusk Purple
        terminalBg = Color(0xFF1E1C2B)      // 80s Terminal
    )

    fun getColors(type: AppThemeType): AppThemeColors {
        return when (type) {
            AppThemeType.CATPPUCCIN_MOCHA -> CatppuccinMocha
            AppThemeType.NORD_FROST -> NordFrost
            AppThemeType.ROSE_PINE -> RosePine
            AppThemeType.GRUVBOX_DARK -> GruvboxDark
            AppThemeType.CYBER_DARK -> CyberDark
            AppThemeType.OLED_BLACK -> OledBlack
            AppThemeType.TOKYO_NIGHT -> TokyoNight
            AppThemeType.DRACULA -> DraculaPro
            AppThemeType.MONOKAI_PRO -> MonokaiPro
            AppThemeType.SYNTHWAVE_84 -> Synthwave84
        }
    }
}

val LocalAppThemeColors = staticCompositionLocalOf { AppThemes.CatppuccinMocha }
