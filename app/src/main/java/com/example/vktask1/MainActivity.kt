package com.example.vktask1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.aspectRatio

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VkTaskApp()
        }
    }
}

@Composable
private fun VkTaskApp(viewModel: AppViewModel = viewModel()) {
    val itemCount by viewModel.itemCount.collectAsStateWithLifecycle()

    val spanCount = integerResource(id = R.integer.grid_span_count)
    val itemSpacing = dimensionResource(id = R.dimen.grid_item_spacing)
    val contentPadding = dimensionResource(id = R.dimen.grid_content_padding)
    val bottomPadding = dimensionResource(id = R.dimen.list_bottom_padding)
    val fabSize = dimensionResource(id = R.dimen.fab_size)
    val fabMargin = dimensionResource(id = R.dimen.fab_margin)
    val dynamicBottom = fabSize + fabMargin + contentPadding
    val finalBottomPadding = if (dynamicBottom > bottomPadding) dynamicBottom else bottomPadding

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addItem() }) {
                Text(text = stringResource(id = R.string.fab_add))
            }
        }
    ) { scaffoldPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(spanCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            contentPadding = PaddingValues(
                start = contentPadding,
                top = contentPadding,
                end = contentPadding,
                bottom = finalBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            items(itemCount) { index ->
                GridItem(index = index)
            }
        }
    }
}

@Composable
private fun GridItem(index: Int) {
    val isEven = index % 2 == 0
    val bgColor = if (isEven) colorResource(id = R.color.item_even_bg) else colorResource(id = R.color.item_odd_bg)
    val textColor = if (isEven) colorResource(id = R.color.item_text_on_even) else colorResource(id = R.color.item_text_on_odd)
    val cornerRadius = dimensionResource(id = R.dimen.grid_item_corner_radius)
    val contentDesc = stringResource(id = R.string.item_content_description, index + 1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor)
            .semantics { contentDescription = listOf(contentDesc).toString() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = (index + 1).toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

class AppViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object { private const val KEY_COUNT = "item_count" }

    private val _itemCount: MutableStateFlow<Int> = MutableStateFlow(savedStateHandle[KEY_COUNT] ?: 0)
    val itemCount: StateFlow<Int> = _itemCount

    fun addItem() {
        _itemCount.update { current ->
            val next = current + 1
            savedStateHandle[KEY_COUNT] = next
            next
        }
    }
}

