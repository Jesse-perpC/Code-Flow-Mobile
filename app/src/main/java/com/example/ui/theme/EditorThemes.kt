package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class EditorThemeType(val displayName: String) {
    CATPPUCCIN("Catppuccin Mocha"),
    NORD("Nord Frost"),
    ROSE_PINE("Rosé Pine"),
    GRUVBOX("Gruvbox Dark"),
    GITHUB_DARK("GitHub Dark"),
    DRACULA("Dracula"),
    MONOKAI("Monokai Pro"),
    ONE_DARK("One Dark Pro"),
    TOKYO_NIGHT("Tokyo Night"),
    SOLARIZED_DARK("Solarized Dark")
}

data class SyntaxColorPalette(
    val background: Color,
    val lineNumbers: Color,
    val text: Color,
    val keyword: Color,
    val function: Color,
    val string: Color,
    val comment: Color,
    val number: Color,
    val type: Color,
    val accent: Color
)

object EditorThemes {
    val Catppuccin = SyntaxColorPalette(
        background = Color(0xFF1E1E2E),
        lineNumbers = Color(0xFF6C7086),
        text = Color(0xFFCDD6F4),
        keyword = Color(0xFFCBA6F7), // Mauve
        function = Color(0xFF89B4FA), // Blue
        string = Color(0xFFA6E3A1), // Green
        comment = Color(0xFF6C7086), // Overlay0
        number = Color(0xFFFAB387), // Peach
        type = Color(0xFFF9E2AF), // Yellow
        accent = Color(0xFFCBA6F7)
    )

    val Nord = SyntaxColorPalette(
        background = Color(0xFF2E3440),
        lineNumbers = Color(0xFF4C566A),
        text = Color(0xFFECEFF4),
        keyword = Color(0xFF81A1C1),
        function = Color(0xFF88C0D0),
        string = Color(0xFFA3BE8C),
        comment = Color(0xFF616E88),
        number = Color(0xFFB48EAD),
        type = Color(0xFFEBCB8B),
        accent = Color(0xFF88C0D0)
    )

    val RosePine = SyntaxColorPalette(
        background = Color(0xFF191724),
        lineNumbers = Color(0xFF524F67),
        text = Color(0xFFE0DEF4),
        keyword = Color(0xFF31748F),
        function = Color(0xFFEB6F92),
        string = Color(0xFFF6C177),
        comment = Color(0xFF6E6A86),
        number = Color(0xFF9CCFD8),
        type = Color(0xFFC4A7E7),
        accent = Color(0xFFEB6F92)
    )

    val Gruvbox = SyntaxColorPalette(
        background = Color(0xFF282828),
        lineNumbers = Color(0xFF928374),
        text = Color(0xFFEBDBB2),
        keyword = Color(0xFFFB4934),
        function = Color(0xFFB8BB26),
        string = Color(0xFFB8BB26),
        comment = Color(0xFF928374),
        number = Color(0xFFD3869B),
        type = Color(0xFFFABD2F),
        accent = Color(0xFFFE8019)
    )

    val GitHubDark = SyntaxColorPalette(
        background = Color(0xFF0D1117),
        lineNumbers = Color(0xFF484F58),
        text = Color(0xFFC9D1D9),
        keyword = Color(0xFFFF7B72),
        function = Color(0xFFD2A8FF),
        string = Color(0xFFA5D6FF),
        comment = Color(0xFF8B949E),
        number = Color(0xFF79C0FF),
        type = Color(0xFFFFA657),
        accent = Color(0xFF58A6FF)
    )

    val Dracula = SyntaxColorPalette(
        background = Color(0xFF282A36),
        lineNumbers = Color(0xFF6272A4),
        text = Color(0xFFF8F8F2),
        keyword = Color(0xFFFF79C6),
        function = Color(0xFF50FA7B),
        string = Color(0xFFF1FA8C),
        comment = Color(0xFF6272A4),
        number = Color(0xFFBD93F9),
        type = Color(0xFF8BE9FD),
        accent = Color(0xFFFF79C6)
    )

    val Monokai = SyntaxColorPalette(
        background = Color(0xFF272822),
        lineNumbers = Color(0xFF75715E),
        text = Color(0xFFF8F8F2),
        keyword = Color(0xFFF92672),
        function = Color(0xFFA6E22E),
        string = Color(0xFFE6DB74),
        comment = Color(0xFF75715E),
        number = Color(0xFFAE81FF),
        type = Color(0xFF66D9EF),
        accent = Color(0xFFFD971F)
    )

    val OneDark = SyntaxColorPalette(
        background = Color(0xFF21252B),
        lineNumbers = Color(0xFF4B5263),
        text = Color(0xFFABB2BF),
        keyword = Color(0xFFC678DD),
        function = Color(0xFF61AFEF),
        string = Color(0xFF98C379),
        comment = Color(0xFF5C6370),
        number = Color(0xFFD19A66),
        type = Color(0xFFE5C07B),
        accent = Color(0xFF61AFEF)
    )

    val TokyoNight = SyntaxColorPalette(
        background = Color(0xFF1A1B26),
        lineNumbers = Color(0xFF3B4261),
        text = Color(0xFFA9B1D6),
        keyword = Color(0xFFBB9AF7),
        function = Color(0xFF7AA2F7),
        string = Color(0xFF9ECE6A),
        comment = Color(0xFF565F89),
        number = Color(0xFFFF9E64),
        type = Color(0xFF2AC3DE),
        accent = Color(0xFF7DCFFF)
    )

    val SolarizedDark = SyntaxColorPalette(
        background = Color(0xFF002B36),
        lineNumbers = Color(0xFF586E75),
        text = Color(0xFF839496),
        keyword = Color(0xFF859900),
        function = Color(0xFF268BD2),
        string = Color(0xFF2AA198),
        comment = Color(0xFF586E75),
        number = Color(0xFFD33682),
        type = Color(0xFFB58900),
        accent = Color(0xFFCB4B16)
    )

    fun getPalette(type: EditorThemeType): SyntaxColorPalette {
        return when (type) {
            EditorThemeType.CATPPUCCIN -> Catppuccin
            EditorThemeType.NORD -> Nord
            EditorThemeType.ROSE_PINE -> RosePine
            EditorThemeType.GRUVBOX -> Gruvbox
            EditorThemeType.GITHUB_DARK -> GitHubDark
            EditorThemeType.DRACULA -> Dracula
            EditorThemeType.MONOKAI -> Monokai
            EditorThemeType.ONE_DARK -> OneDark
            EditorThemeType.TOKYO_NIGHT -> TokyoNight
            EditorThemeType.SOLARIZED_DARK -> SolarizedDark
        }
    }
}
