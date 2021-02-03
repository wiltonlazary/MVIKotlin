package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.mvikotlin.core.view.BaseMviView

open class MviViewProxy<Model : Any, Event : Any> : BaseMviView<Model, Event>() {

    private val _models = mutableStateOf<Model?>(null)
    val models: State<Model?> = _models

    override fun render(model: Model) {
        super.render(model)

        _models.value = model
    }
}
