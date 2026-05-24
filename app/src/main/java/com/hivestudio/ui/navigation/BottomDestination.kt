package com.hivestudio.ui.navigation

enum class BottomDestination(
    val route: String,
    val title: String,
) {
    Catalog("catalog", "Каталог"),
    Dashboard("dashboard", "Статистика"),
    Beats("beats", "Мои биты"),
    Profile("profile", "Профиль"),
}
