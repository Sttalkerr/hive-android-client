package com.hivestudio.ui.model

enum class BeatPriceFilter(
    val title: String,
) {
    Any("Любая цена"),
    Under3000("До 3000 ₽"),
    Between3000And5000("3000-5000 ₽"),
    Over5000("От 5000 ₽"),
}
