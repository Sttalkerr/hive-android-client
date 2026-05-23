package com.hivestudio.ui.navigation

enum class BottomDestination(
    val route: String,
    val title: String,
) {
    Dashboard("dashboard", "Статистика"),
    Beats("beats", "Мои биты"),
    AddBeat("add_beat", "Добавить"),
    Settings("settings", "Настройки"),
}
